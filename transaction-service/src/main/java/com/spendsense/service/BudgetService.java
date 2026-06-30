package com.spendsense.service;

import com.spendsense.dto.BudgetRequest;
import com.spendsense.model.Budget;
import com.spendsense.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public Budget setBudget(BudgetRequest request) {
        // Check if budget already exists
        Optional<Budget> existing = budgetRepository
                .findByUserIdAndCategoryAndMonth(
                        request.getUserId(),
                        request.getCategory(),
                        request.getMonth()
                );

        Budget budget = existing.orElse(new Budget());
        budget.setUserId(request.getUserId());
        budget.setCategory(request.getCategory());
        budget.setLimitAmount(request.getLimitAmount());
        budget.setMonth(request.getMonth());

        return budgetRepository.save(budget);
    }
}