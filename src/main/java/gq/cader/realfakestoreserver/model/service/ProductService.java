package gq.cader.realfakestoreserver.model.service;

import gq.cader.realfakestoreserver.exception.ProductNotFoundException;
import gq.cader.realfakestoreserver.model.entity.Product;
import gq.cader.realfakestoreserver.model.repository.ProductRepository;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private static final Logger LOG = LoggerFactory
            .getLogger(ProductService.class);
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Integer id) throws ProductNotFoundException {

        LOG.info("Querying ProductRepository for productId:" + id);
        return productRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);
    }

    /**
     *
     * @param product to refresh
     * @return product with updated fields
     */
    public Product refresh(@NonNull Product product){
        return this.findById(product.getProductId());
    }

    public Product postNewProduct(@NonNull Product product) {
        if (productRepository.findByName(product.getName()).isPresent()){

            Product alreadyExistingProduct = productRepository.findByName(
                product.getName()).get();
            LOG.warn("Detected duplicate product during post attempt. \n" +
                alreadyExistingProduct.toString());
            return alreadyExistingProduct;

        } else {
            return productRepository.save(product);}
    }

    protected void putUpdatedProduct(Product product) {
        productRepository.save(product);
    }

    public List<Product> findByNameContains(String query) {
        LOG.info("Querying ProductRepository for partial name: " + query);

        return productRepository.findByNameContainsIgnoreCase(query)
                .orElseThrow(ProductNotFoundException::new);

    }
    public List<Product> search(String query){

        return findByNameContains(query);
    }
}
