package gq.cader.realfakestoreserver.model.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import gq.cader.realfakestoreserver.exception.CustomerNotFoundException;
import gq.cader.realfakestoreserver.model.entity.Customer;
import gq.cader.realfakestoreserver.model.repository.CustomerRepository;
import lombok.NonNull;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private static final Logger LOG = LoggerFactory.getLogger(CustomerService.class);
    private KafkaTemplate<Integer, String> queryMessageProducer;
    private KafkaTemplate<Integer, Integer> productViewedMessageProducer;

    @Autowired
    public CustomerService(CustomerRepository customerRepository,
            @Qualifier("kafkaTemplateIntStr") KafkaTemplate<Integer, String> queryMessageProducer,
            @Qualifier("kafkaTemplateIntInt") KafkaTemplate<Integer, Integer> productViewedMessageProducer) {

        this.customerRepository = customerRepository;
        this.productViewedMessageProducer = productViewedMessageProducer;
        this.queryMessageProducer = queryMessageProducer;
    }

    public Customer postNewCustomer(@NonNull Customer customer) {
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

    public Customer findById(@NonNull Integer customerId) throws CustomerNotFoundException {

        LOG.info("Querying CustomerRepository for ID:" + customerId.toString());
        return customerRepository.findById(customerId).orElseThrow(CustomerNotFoundException::new);
    }

    public List<Customer> findByFirstName(String name) {
        return customerRepository.findByFirstNameContainsIgnoreCase(name);
    }

    public List<Customer> findByLastName(String name) {
        return customerRepository.findByLastNameContainsIgnoreCase(name);
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public void sendSearchQueryMessage(Integer customerId, String query) {

        var future = queryMessageProducer.send("searches", customerId, query);
        future.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                LOG.info("We need to go back to the furute!");

            }

            @Override
            public void onFailure(Throwable ex) {
                // TODO Auto-generated method stub

            }

        });

    }

    public void sendProductViewedMessage(Integer customerId, Integer productId) {
        productViewedMessageProducer.send("viewed_product", customerId, productId);
    }
}
