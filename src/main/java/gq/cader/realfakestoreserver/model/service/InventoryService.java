package gq.cader.realfakestoreserver.model.service;

import gq.cader.realfakestoreserver.exception.CheckoutFailedException;
import gq.cader.realfakestoreserver.exception.ProductInventoryException;
import gq.cader.realfakestoreserver.model.entity.Product;

import lombok.NonNull;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InventoryService {

    private static final Logger LOG = LoggerFactory
            .getLogger(InventoryService.class);

    private ProductService productService;

    @Autowired
    public InventoryService(ProductService productService) {
        this.productService = productService;
    }
    protected void reduceProductInventoryByDelta(
        @NonNull Map<Product,Integer> productQuantityMap)
        throws CheckoutFailedException{

        Map<Product,Integer> stagedChanges = new HashMap<>();

        try {
            productQuantityMap
                .forEach((key, value) -> {
                    reduceProductInventoryByDelta(key, value);
                    stagedChanges.put(key, value);
                });
        }catch (ProductInventoryException e){
            stagedChanges
                .forEach(this::increaseProductInventoryByDelta);
            throw new CheckoutFailedException(e.getMessage());
        }
    }
    private void reduceProductInventoryByDelta(
            Product product, Integer delta) throws ProductInventoryException {

        verifyProductInventory(product, delta);
        product.setNumInInventory(product.getNumInInventory() - delta);
        productService.putUpdatedProduct(product);
        LOG.info(product.getName() +
                " inventory successfully reduced by:" + delta);
    }
    private void increaseProductInventoryByDelta(
        @NonNull Product product, Integer delta) {

        product.setNumInInventory(product.getNumInInventory() + delta);
        productService.putUpdatedProduct(product);
        LOG.info(product.getName() +
            " inventory successfully increased by:" + delta);
    }
    protected Boolean verifyProductInventory(
        @NonNull Product product, Integer quantity)
            throws ProductInventoryException {

        product.setNumInInventory(productService.refresh(product)
            .getNumInInventory());

        if (product.getNumInInventory() >= quantity) {
            return true;
        } else {
            throw new ProductInventoryException("Product ID:"
                    + product.getProductId() + ": " + product.getName()
                    + " is not in sufficient stock to reduce inventory by "
                    + quantity.toString());
        }
    }
}
