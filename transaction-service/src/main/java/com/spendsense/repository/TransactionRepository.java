package com.spendsense.repository;

import com.spendsense.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Optional<Transaction> findByUpiRef(String upiRef);
    List<Transaction> findByUserId(String userId);
}