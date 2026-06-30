package com.spendsense.controller;

import com.spendsense.dto.MonthlySummary;
import com.spendsense.service.SummaryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/summary")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    @GetMapping("/{month}")
    public ResponseEntity<MonthlySummary> getSummary(
            @PathVariable String month,
            HttpServletRequest request) {

        // Get userId from JWT token
        String userId = (String) request
                .getAttribute("userId");

        return ResponseEntity.ok(
                summaryService.getSummary(userId, month));
    }
}