package gq.cader.realfakestoreserver.model.service;

import gq.cader.realfakestoreserver.exception.CustomerNotFoundException;
import gq.cader.realfakestoreserver.model.entity.Customer;
import gq.cader.realfakestoreserver.model.repository.CustomerRepository;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private static final Logger LOG = LoggerFactory
            .getLogger(CustomerService.class);
    private KafkaTemplate<Integer, String> queryMessageProducer;
    private KafkaTemplate<Integer, Integer> productViewedMessageProducer;

    @Autowired
    public CustomerService(CustomerRepository customerRepository,
                           @Qualifier("kafkaTemplateIntStr")
                           KafkaTemplate queryMessageProducer,
                           @Qualifier("kafkaTemplateIntInt")
                           KafkaTemplate productViewedMessageProducer) {

        this.customerRepository = customerRepository;
        this.productViewedMessageProducer = productViewedMessageProducer;
        this.queryMessageProducer = queryMessageProducer;
    }

    public Customer postNewCustomer(Customer customer) {
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            LOG.info("Customer: " + customer.toString() + " Already exists");
            return customerRepository.findByEmail(customer.getEmail()).get();
        } else {
            LOG.info("Created Customer: " + customer.toString());
            return customerRepository.save(customer);
        }
    }
    public @NonNull List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer findById(Integer customerId)
            throws CustomerNotFoundException {

        LOG.info("Querying CustomerRepository for ID:" + customerId.toString());
        return customerRepository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);
    }

    public List<Customer> findByFirstName(String name) {
        return customerRepository.findByFirstNameContainsIgnoreCase(name);
    }

    public List<Customer> findByLastName(String name) {
        return customerRepository.findByLastNameContainsIgnoreCase(name);
    }
    public Customer save(Customer customer){
        return customerRepository.save(customer);

    }

    public void newSearchQueryMessage(Integer customerId, String query) {
        queryMessageProducer.send("searches",customerId, query);
    }

    public void newProductViewedMessage(Integer customerId, Integer productId) {
        productViewedMessageProducer.send("viewed_product",
                                         customerId, productId);
    }
}
