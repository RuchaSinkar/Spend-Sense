package com.spendsense.service;

import com.spendsense.dto.TransactionRequest;
import com.spendsense.exception.DuplicateTransactionException;
import com.spendsense.model.Transaction;
import com.spendsense.repository.TransactionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategorizerService categorizerService;
    private final BudgetEngine budgetEngine;

    public Transaction saveTransaction(
            TransactionRequest request) {

        // Check for duplicate UPI ref
        if (transactionRepository.existsByUpiRef(request.getUpiRef())) {
            throw new DuplicateTransactionException(
                    "Duplicate transaction: "
                            + request.getUpiRef()
                            + " already exists");
        }

        // Categorize using AI
        String category = categorizerService.categorize(request.getDescription());

        // Build and save transaction
        Transaction transaction = new Transaction();
        transaction.setUserId(request.getUserId());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setUpiRef(request.getUpiRef());
        transaction.setCategory(category);

        Transaction saved = transactionRepository.save(transaction);

        // Update Redis budget counter
        String month = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        budgetEngine.recordSpending(
                request.getUserId(),
                category,
                request.getAmount(),
                month
        );

        return saved;
    }

    public List<Transaction> getByUser(String userId) {
        return transactionRepository.findByUserId(userId);
    }
}