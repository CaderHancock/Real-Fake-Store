package gq.cader.realfakestoreserver.controller;

import gq.cader.realfakestoreserver.exception.ProductNotFoundException;
import gq.cader.realfakestoreserver.model.entity.Address;
import gq.cader.realfakestoreserver.model.entity.Customer;
import gq.cader.realfakestoreserver.model.entity.Product;
import gq.cader.realfakestoreserver.model.entity.ShoppingCart;
import gq.cader.realfakestoreserver.model.service.AddressService;
import gq.cader.realfakestoreserver.model.service.CheckoutService;
import gq.cader.realfakestoreserver.model.service.CustomerService;
import gq.cader.realfakestoreserver.model.service.ProductService;
import gq.cader.realfakestoreserver.model.service.ShoppingCartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/customers")
public class CustomerController {

    private static final Logger LOG =
        LoggerFactory.getLogger(CustomerController.class);
    private CustomerService customerService;
    private CheckoutService checkoutService;
    private ProductService productService;
    private ShoppingCartService shoppingCartService;
    private AddressService addressService;

    @Autowired
    CustomerController(CustomerService customerService,
                       CheckoutService checkoutService,
                       ProductService productService,
                       ShoppingCartService shoppingCartService,
                       AddressService addressService) {
        this.customerService = customerService;
        this.checkoutService = checkoutService;
        this.productService =productService;
        this.shoppingCartService =shoppingCartService;
        this.addressService = addressService;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public Customer findByCustomerId(@PathVariable Integer id) {
        return customerService.findById(id);
    }

    @GetMapping("/")
    public List<Customer> getAllCustomers(){
        return customerService.findAll();
    }

    @GetMapping(value = "/create/{firstName}/{lastName}/{email}")
    public Customer createCustomer(@PathVariable String firstName,
                                   @PathVariable String lastName,
                                   @PathVariable String email){
        return customerService.postNewCustomer(new Customer(firstName, lastName, email));
    }

    @GetMapping(value = "/{id}/addresses/add/{streetAddress}/{city" +
        "}/{postalCode}/{state}/{country}")
    public Customer addAddress(@PathVariable Integer id,
                               @PathVariable String streetAddress,
                               @PathVariable String city,
                               @PathVariable String postalCode,
                               @PathVariable String state,
                               @PathVariable String country){

        Customer customer = customerService.findById(id);
        Address address = new Address(streetAddress, city, postalCode, state,
            country);
        customer.getAddresses().add(address);
        return customerService.save(customer);
    }

    @GetMapping(value = "/{customerId}/addToCart/{productId}/{quantity}")
    public Boolean addProductToCart(@PathVariable Integer customerId,
                                    @PathVariable Integer productId,
                                    @PathVariable Integer quantity){
        try{
            Customer customer = customerService.findById(customerId);
            ShoppingCart shoppingCart = customer.getShoppingCart();
            Product product = productService.findById(productId);

            shoppingCartService.setProductQuantity(
                shoppingCart,product,quantity);

            customerService.save(customer);
            return true;
        }catch (Exception e){
            LOG.error(e.getMessage());
            return false;
        }
    }

    @GetMapping(value = "/{id}/checkout")
    public String checkoutCustomer(@PathVariable Integer id){
        Customer customer;
        try{
            customer = customerService.findById(id);
            checkoutService.checkout(customer);
            return "Success";
        }catch(Exception e){
            LOG.error(e.getMessage());
            return e.getMessage();
        }
    }

    @GetMapping(value = "/{id}/checkout/address/{addressId}")
    public String checkoutCustomer(@PathVariable Integer id,
                                   @PathVariable Integer addressId){

        try{
            Customer customer = customerService.findById(id);
            Address address = addressService.findById(addressId);
            checkoutService.checkout(customer, address);
            return "Success";
        }catch(Exception e){
            LOG.error(e.getMessage());
            return e.getMessage();
        }
    }
    @GetMapping(value = "/{customerId}/viewProduct/{productId}")
    public Product viewProduct(@PathVariable Integer customerId,
                                @PathVariable Integer productId){
        try {
            Product product = productService.findById(productId);
            customerService.newProductViewedMessage(customerId, productId);
            return product;
        }catch (ProductNotFoundException e){
            return null;
        }
    }
    @GetMapping(value = "/{customerId}/search/{query}")
    public List<Product> search(@PathVariable Integer customerId,
                                @PathVariable String query){
        try {
            customerService.newSearchQueryMessage(customerId, query);
            return productService.search(query);
        }catch (ProductNotFoundException e){
            return Collections.emptyList();
        }
    }
    @GetMapping(value = "/{customerId}/search/{query}/{resultNum}")
    public Product searchResult(@PathVariable Integer customerId,
                                      @PathVariable String query,
                                      @PathVariable Integer resultNum){
        try {
            Product product = productService.search(query)
                .get(resultNum);
            customerService.newProductViewedMessage(customerId,
                product.getProductId());
            return product;
        }catch (Exception e){
            return null;
        }
    }
}
