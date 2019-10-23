package gq.cader.realfakestoreserver.model.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gq.cader.realfakestoreserver.exception.AddressException;
import gq.cader.realfakestoreserver.exception.CheckoutFailedException;
import gq.cader.realfakestoreserver.model.entity.Address;
import gq.cader.realfakestoreserver.model.entity.Customer;
import gq.cader.realfakestoreserver.model.entity.Order;
import gq.cader.realfakestoreserver.model.entity.Product;
import gq.cader.realfakestoreserver.model.entity.ShoppingCart;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

/**
 * The type Checkout service.
 */
@Service
public class CheckoutService {

    private static final Logger LOG = LoggerFactory
        .getLogger(CheckoutService.class);
    private final InventoryService inventoryService;
    private final CustomerService customerService;
    private KafkaTemplate<Integer, String> orderMessageProducer;

    /**
     * Instantiates a new Checkout service.
     *
     * @param inventoryService the inventory service
     * @param customerService  the customer service
     */
    @Autowired
    public CheckoutService(InventoryService inventoryService,
                           CustomerService customerService,
                           @Qualifier("kafkaTemplateIntStr")
                           KafkaTemplate<Integer, String> orderMessageProducer){

        this.inventoryService = inventoryService;
        this.customerService =  customerService;
        this.orderMessageProducer = orderMessageProducer;
    }

    /**
     * @param customer the customer
     * @throws AddressException
     * @throws CheckoutFailedException
     */
    public void checkout(@NonNull Customer customer) throws
        AddressException,
        CheckoutFailedException {

        if (customer.getAddresses().isEmpty()){

            throw new AddressException("Address Missing");

        }else if(customer.getAddresses().size() > 1){
            StringBuilder availableAddresses = new StringBuilder();
            customer.getAddresses().forEach(address -> availableAddresses
                .append("Id:")
                .append(address.getAddressId())
                .append(" ")
                .append(address.getStreetAddress())
                .append("\r\n"));
            throw new AddressException("Address Not Selected. Available " +
                "Options:\r\n" + availableAddresses);

        }
        checkout(customer, customer.getAddresses().iterator().next());
    }

    /**
     * Checkout.
     *
     * @param customer the customer
     * @param address  the address
     * @throws CheckoutFailedException the checkout failed exception
     */
    public void checkout (@NonNull Customer customer, Address address)
        throws CheckoutFailedException {

        if (!customer.getAddresses().contains(address)){
            throw new CheckoutFailedException("Address:" +
                address.getAddressId() + "does not belong to Customer:"
                + customer.getCustomerId());
        }

        Instant timestamp =  Instant.now();
        Map<Product, Integer> orderProductQuantityMap =
            customer.getShoppingCart().getProductQuantityMap();
        if(orderProductQuantityMap.isEmpty()){
            throw new CheckoutFailedException("Cannot Checkout Empty Cart");
        }
        //TODO implement payment system and execute here
        inventoryService.reduceProductInventoryByDelta(orderProductQuantityMap);

        Order order = new Order(customer.getCustomerId(),
            customer.getShoppingCart(), address,
            timestamp);
        customer.getOrders().add(order);
        customer.setShoppingCart(new ShoppingCart());
        customerService.save(customer);
        LOG.info("Customer:" + customer.getCustomerId() + " successfully " +
            "checked out");
        String orderJson = "";
        try {
            orderJson =
               new ObjectMapper().writeValueAsString(order);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        orderMessageProducer.send("orders",
            customer.getCustomerId(), orderJson);


    }
}
