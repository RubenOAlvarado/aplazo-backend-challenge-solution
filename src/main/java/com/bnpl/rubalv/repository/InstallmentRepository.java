package com.bnpl.rubalv.repository;

import com.bnpl.rubalv.model.Installment;
import com.bnpl.rubalv.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InstallmentRepository extends JpaRepository<Installment, UUID> {
    List<Installment> findByLoan(Loan loan);
}
