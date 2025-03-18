package com.bnpl.rubalv.repository;

import com.bnpl.rubalv.model.CreditLine;
import com.bnpl.rubalv.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanRepository extends JpaRepository<Loan, UUID> {
}
