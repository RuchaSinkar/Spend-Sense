package com.spendsense.service;

import com.spendsense.dto.CategorySummary;
import com.spendsense.dto.MonthlySummary;
import com.spendsense.model.Budget;
import com.spendsense.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SummaryService {

    private final BudgetRepository budgetRepository;
    private final BudgetEngine budgetEngine;

    public MonthlySummary getSummary(String userId,
                                     String month) {

        // Get all budgets for this user and month
        List<Budget> budgets = budgetRepository
                .findByUserIdAndMonth(userId, month);

        // Build category summaries
        List<CategorySummary> categories = budgets
                .stream()
                .map(budget -> {

                    // Get spent amount from Redis
                    BigDecimal spent = budgetEngine
                            .getSpending(userId,
                                    budget.getCategory(), month);

                    BigDecimal limit = budget.getLimitAmount();

                    // Calculate percentage
                    BigDecimal percentage = spent
                            .divide(limit, 2, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));

                    // Determine status
                    String status = getStatus(percentage);

                    return new CategorySummary(
                            budget.getCategory(),
                            spent,
                            limit,
                            percentage,
                            status
                    );
                })
                .collect(Collectors.toList());

        // Total spent across all categories
        BigDecimal totalSpent = categories.stream()
                .map(CategorySummary::getSpent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MonthlySummary(
                userId, month, totalSpent, categories);
    }

    private String getStatus(BigDecimal percentage) {
        if (percentage.compareTo(
                BigDecimal.valueOf(100)) >= 0) {
            return "EXCEEDED";
        } else if (percentage.compareTo(
                BigDecimal.valueOf(90)) >= 0) {
            return "CRITICAL";
        } else if (percentage.compareTo(
                BigDecimal.valueOf(70)) >= 0) {
            return "WARNING";
        }
        return "OK";
    }
}