package gq.cader.realfakestoreserver.model.repository;

import gq.cader.realfakestoreserver.model.entity.Product;
import gq.cader.realfakestoreserver.model.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    //Product findByProductId(Integer id);

    Optional<List<Product>> findByCategory(ProductCategory category);

    Optional<Product> findByName(String name);

    Optional<List<Product>> findByNameContainsIgnoreCase(String query);
}



