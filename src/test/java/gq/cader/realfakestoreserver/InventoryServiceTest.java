package gq.cader.realfakestoreserver;

import gq.cader.realfakestoreserver.exception.CheckoutFailedException;
import gq.cader.realfakestoreserver.model.entity.Product;
import gq.cader.realfakestoreserver.model.repository.ProductRepository;
import gq.cader.realfakestoreserver.model.service.InventoryService;
import gq.cader.realfakestoreserver.model.service.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@RunWith(SpringJUnit4ClassRunner.class)
public class InventoryServiceTest {

    @MockBean
    private ProductRepository productRepository;
    private MockProductService productService =
        Mockito.spy(new MockProductService(productRepository));
    private TestableInventoryService inventoryService =
        new TestableInventoryService(productService);

    private Product product1 = new Product();
    private Product product2 = new Product();
    private Map<Product, Integer> mockShoppingCart = new HashMap<>();

    //We need to stub and call protected/private methods for this test, hence
    // the local classes
    private static class MockProductService extends ProductService{
        Set<Product> mockRepo = new HashSet<>();
        MockProductService(ProductRepository productRepository) {
            super(productRepository);
        }
        @Override
        protected void putUpdatedProduct(Product product){
            if(mockRepo.contains(product)){
                mockRepo.remove(product);
                mockRepo.add(product);
            }else{
                mockRepo.add(product);
            }
        }
    }
    private static class TestableInventoryService extends InventoryService{
        TestableInventoryService(ProductService productService) {
            super(productService);
        }
        @Override
        protected void reduceProductInventoryByDelta(
            Map<Product,Integer> productQuantityMap)
            throws CheckoutFailedException {
            super.reduceProductInventoryByDelta(productQuantityMap);}
    }





    @Before
    public void setUp(){

        Mockito.doReturn(product1).when(productService).refresh(product1);
        Mockito.doReturn(product2).when(productService).refresh(product2);
    }

    private void reset() {

        product1.setNumInInventory(99);
        product1.setName("prod1");
        product1.setPrice(1.0);

        product2.setName("prod2");
        product2.setPrice(1.0);
        product2.setNumInInventory(100);

        mockShoppingCart.clear();
    }
    @Test
    public void whenSufficientInventory_thenCorrectlyReduced(){

        reset();
        mockShoppingCart.put(product1,99);
        mockShoppingCart.put(product2,100);
        Product result1,
                result2;
        inventoryService.reduceProductInventoryByDelta(mockShoppingCart);
        result1 =
            productService.mockRepo.stream().filter(x -> x.getName().equals(
            "prod1")).findFirst().get();
        result2 =
            productService.mockRepo.stream().filter(x -> x.getName().equals(
                "prod2")).findFirst().get();
        assertEquals(0, result1.getNumInInventory());
        assertEquals(0,result2.getNumInInventory());
    }
    @Test
    public void whenInsufficientInventory_thenThrowException(){

        reset();
        mockShoppingCart.put(product1, 100);
        assertThrows(Exception.class, () ->
            inventoryService.reduceProductInventoryByDelta(mockShoppingCart));
    }
    @Test
    public void whenPartialInventory_thenNoMutation(){

        reset();
        mockShoppingCart.put(product1, 98);
        mockShoppingCart.put(product2,101);
        assertThrows(Exception.class, () ->
            inventoryService.reduceProductInventoryByDelta(mockShoppingCart));
        Product result =
            productService.mockRepo.stream().filter(x -> x.getName().equals(
                "prod1")).findFirst().get();
        assertEquals(99,result.getNumInInventory());
    }

}
