package com.demo.repository;

import com.demo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * This is a "magic" query we will use.
     * Spring Data JPA will automatically create a SQL query like:
     * "SELECT * FROM customers WHERE contact = ?"
     *
     * We use Optional<> because the customer might not exist.
     */
    Optional<Customer> findByContact(String contact);
}
