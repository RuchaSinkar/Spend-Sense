package com.spendsense.repository;

import com.spendsense.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository
        extends JpaRepository<Budget, String> {

    Optional<Budget> findByUserIdAndCategoryAndMonth(String userId, String category, String month);

    List<Budget> findByUserIdAndMonth(String userId, String month);
}