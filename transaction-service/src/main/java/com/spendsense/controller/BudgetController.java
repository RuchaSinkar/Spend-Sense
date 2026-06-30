package com.spendsense.controller;

import com.spendsense.dto.BudgetRequest;
import com.spendsense.model.Budget;
import com.spendsense.service.BudgetEngine;
import com.spendsense.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final BudgetEngine budgetEngine;

    // Set a budget
    @PostMapping
    public ResponseEntity<Budget> setBudget(@RequestBody BudgetRequest request) {
        return ResponseEntity.ok(budgetService.setBudget(request));
    }

    // Check current spending
    @GetMapping("/{userId}/{category}/{month}")
    public ResponseEntity<?> getSpending(
            @PathVariable String userId,
            @PathVariable String category,
            @PathVariable String month) {

        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "category", category,
                "month", month,
                "spent", budgetEngine
                        .getSpending(userId, category, month)
        ));
    }
}