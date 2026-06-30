package com.spendsense.service;

import com.spendsense.model.Budget;
import com.spendsense.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetEngine {

    private final StringRedisTemplate redisTemplate;
    private final BudgetRepository budgetRepository;
    private final AlertService alertService;  // ADD THIS

    // Called every time a transaction is saved
    public void recordSpending(String userId, String category, BigDecimal amount, String month) {

        // Redis key: spent:rahul_01:Food Delivery:2026-06
        String key = buildKey(userId, category, month);

        // Atomically add amount to Redis counter
        redisTemplate.opsForValue().increment(key, amount.longValue());

        // Temporary debug — remove later
        /*String debugVal = redisTemplate.opsForValue().get(key);
        log.info("DEBUG — Redis value after increment: {}", debugVal);*/

        log.info("Recorded ₹{} for {} | key: {}",
                amount, category, key);

        // Check if budget threshold is crossed
        checkThreshold(userId, category, month, key);
    }

    /*private void checkThreshold(String userId, String category, String month, String key) {

        // Get current spending from Redis
        String spentStr = redisTemplate.opsForValue().get(key);

        if (spentStr == null) return;

        BigDecimal spent = new BigDecimal(spentStr);

        // Get budget limit from PostgreSQL
        Optional<Budget> budgetOpt = budgetRepository
                .findByUserIdAndCategoryAndMonth(userId, category, month);

        if (budgetOpt.isEmpty()) {
            log.info("No budget set for {} - {} - {}",
                    userId, category, month);
            return;
        }

        BigDecimal limit = budgetOpt.get().getLimitAmount();

        // Calculate percentage used
        BigDecimal percentage = spent
                .divide(limit, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        log.info("Budget check → {} spent ₹{} of ₹{} ({}%)",
                category, spent, limit, percentage);

        // Fire alerts based on threshold
        if (percentage.compareTo(
                BigDecimal.valueOf(100)) >= 0) {
            log.warn("🚨 BUDGET EXCEEDED for {} | " +
                            "Category: {} | Spent: ₹{} | Limit: ₹{}",
                    userId, category, spent, limit);

        } else if (percentage.compareTo(
                BigDecimal.valueOf(90)) >= 0) {
            log.warn("⚠️ CRITICAL: 90% budget used for {} | " +
                            "Category: {} | Only ₹{} left",
                    userId, category,
                    limit.subtract(spent));

        } else if (percentage.compareTo(
                BigDecimal.valueOf(70)) >= 0) {
            log.warn("📢 WARNING: 70% budget used for {} | " +
                            "Category: {}",
                    userId, category);
        }
    }*/

    private void checkThreshold(String userId,
                                String category,
                                String month,
                                String key) {

        String spentStr = redisTemplate
                .opsForValue().get(key);
        if (spentStr == null) return;

        BigDecimal spent = new BigDecimal(spentStr);

        budgetRepository
                .findByUserIdAndCategoryAndMonth(
                        userId, category, month)
                .ifPresent(budget -> {

                    BigDecimal limit = budget.getLimitAmount();
                    BigDecimal percentage = spent
                            .divide(limit, 2,
                                    java.math.RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));

                    log.info("Budget check → {} ₹{}/₹{} ({}%)",
                            category, spent, limit, percentage);

                    // Only send alert if threshold crossed
                    if (percentage.compareTo(
                            BigDecimal.valueOf(70)) >= 0) {
                        alertService.sendAlert(
                                userId, category,
                                spent, limit, percentage);
                    }
                });
    }

    // Get current spending for a user+category+month
    public BigDecimal getSpending(String userId, String category, String month) {
        String key = buildKey(userId, category, month);
        String value = redisTemplate.opsForValue().get(key);
        return value != null
                ? new BigDecimal(value)
                : BigDecimal.ZERO;
    }

    private String buildKey(String userId, String category, String month) {
        return "spent:" + userId + ":" + category + ":" + month;
    }
}