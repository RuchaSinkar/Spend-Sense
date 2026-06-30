package com.spendsense.controller;

import com.spendsense.dto.TransactionRequest;
import com.spendsense.model.Transaction;
import com.spendsense.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    @PostMapping
    public ResponseEntity<Transaction> create(
            @RequestBody TransactionRequest req,
            HttpServletRequest request) {

        // Get userId from JWT (set by JwtFilter)
        String userId = (String) request
                .getAttribute("userId");

        req.setUserId(userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.saveTransaction(req));
    }

    @GetMapping("/user")
    public ResponseEntity<List<Transaction>> getMyTransactions(
            HttpServletRequest request) {

        String userId = (String) request
                .getAttribute("userId");

        return ResponseEntity.ok(
                service.getByUser(userId));
    }
}