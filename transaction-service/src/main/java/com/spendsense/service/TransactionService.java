package com.spendsense.service;

import com.spendsense.dto.TransactionRequest;
import com.spendsense.exception.DuplicateTransactionException;
import com.spendsense.model.Transaction;
import com.spendsense.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;

    public Transaction save(TransactionRequest req) {
        // Idempotency: if this UPI ref already exists, reject it
        if (repository.findByUpiRef(req.getUpiRef()).isPresent()) {
            throw new DuplicateTransactionException("Transaction already recorded: " + req.getUpiRef());
        }

        Transaction txn = new Transaction();
        txn.setUserId(req.getUserId());
        txn.setAmount(req.getAmount());
        txn.setDescription(req.getDescription());
        txn.setUpiRef(req.getUpiRef());

        return repository.save(txn);
    }

    public List<Transaction> getByUser(String userId) {
        return repository.findByUserId(userId);
    }
}