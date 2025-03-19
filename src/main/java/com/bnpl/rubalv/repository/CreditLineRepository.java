package com.bnpl.rubalv.repository;

import com.bnpl.rubalv.enums.CreditLineStatus;
import com.bnpl.rubalv.model.CreditLine;
import com.bnpl.rubalv.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditLineRepository extends JpaRepository<CreditLine, UUID> {
    Optional<CreditLine> findByCustomerAndStatusEquals(Customer customer, CreditLineStatus status);
}
