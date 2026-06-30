package com.spendsense.service;

import com.spendsense.dto.AlertMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendAlert(String userId,
                          String category,
                          BigDecimal spent,
                          BigDecimal limit,
                          BigDecimal percentage) {

        String type;
        String message;

        if (percentage.compareTo(
                BigDecimal.valueOf(100)) >= 0) {
            type = "BUDGET_EXCEEDED";
            message = "🚨 You have exceeded your "
                    + category + " budget! "
                    + "Spent ₹" + spent
                    + " of ₹" + limit;

        } else if (percentage.compareTo(
                BigDecimal.valueOf(90)) >= 0) {
            type = "CRITICAL";
            message = "⚠️ Critical! 90% of your "
                    + category + " budget used. "
                    + "Only ₹" + limit.subtract(spent)
                    + " remaining.";

        } else {
            type = "WARNING";
            message = "📢 You have used 70% of your "
                    + category + " budget.";
        }

        AlertMessage alert = new AlertMessage(
                type, userId, category,
                spent, limit, percentage, message
        );

        // Push to user's personal topic
        String destination = "/topic/alerts/" + userId;
        messagingTemplate.convertAndSend(destination, alert);

        log.info("📤 Alert sent to {} → {} | {}",
                userId, destination, type);
    }
}