package com.bnpl.rubalv.repository;

import com.bnpl.rubalv.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository  extends JpaRepository<Customer, UUID> {
    Optional<Customer> findBySequentialId(Long sequentialId);
}
