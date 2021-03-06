package gq.cader.realfakestoreserver.model.repository;

import gq.cader.realfakestoreserver.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByEmail(String email);


    List<Customer> findByFirstNameContainsIgnoreCase(String name);

    List<Customer> findByLastNameContainsIgnoreCase(String name);

}
