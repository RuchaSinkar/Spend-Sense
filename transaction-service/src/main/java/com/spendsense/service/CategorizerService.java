package com.spendsense.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategorizerService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.api.url}")
    private String groqApiUrl;

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    // Categories Gemini must choose from
    private static final String PROMPT_TEMPLATE = """
        You are a financial transaction categorizer for Indian users.
        Categorize this transaction description into exactly ONE of these categories:
        Food Delivery, Groceries, Transport, Entertainment, Education,
        Health, Shopping, Utilities, Cash Withdrawal, Transfer, Other
        
        Transaction: "%s"
        
        Reply with ONLY the category name, nothing else.
        """;

    public String categorize(String description) {
        try {
            return callGroq(description);
        } catch (Exception e) {
            log.error("Groq failed for '{}'. Error: {}", description, e.getMessage());
            return keywordFallback(description);
        }
    }

    private String callGroq(String description) throws Exception {
        String prompt = PROMPT_TEMPLATE.formatted(description);

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", new Object[]{
                        Map.of("role", "user", "content", prompt)
                },
                "temperature", 0,
                "max_tokens", 20
        );

        String response = webClientBuilder.build()
                .post()
                .uri(groqApiUrl)
                .header("Authorization", "Bearer " + groqApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        res -> res.bodyToMono(String.class)
                                .map(body -> {
                                    log.error("Groq Error Response: {}", body);
                                    return new RuntimeException(body);
                                })
                )
                .bodyToMono(String.class)
                .block();

        JsonNode root = objectMapper.readTree(response);
        String category = root
                .path("choices").get(0)
                .path("message")
                .path("content")
                .asText()
                .trim();

        log.info("Groq categorized '{}' as '{}'", description, category);
        return category;
    }

    // Fallback if Gemini fails
    private String keywordFallback(String description) {
        String d = description.toUpperCase();

        if (d.contains("SWIGGY") || d.contains("ZOMATO") || d.contains("FOOD")) return "Food Delivery";
        if (d.contains("UBER") || d.contains("OLA") || d.contains("AUTO") || d.contains("METRO")) return "Transport";
        if (d.contains("BIGBASKET") || d.contains("GROFERS") || d.contains("BLINKIT")) return "Groceries";
        if (d.contains("NETFLIX") || d.contains("HOTSTAR") || d.contains("SPOTIFY")) return "Entertainment";
        if (d.contains("BYJU") || d.contains("UNACADEMY") || d.contains("UDEMY")) return "Education";
        if (d.contains("PHARMACY") || d.contains("HOSPITAL") || d.contains("CLINIC")) return "Health";
        if (d.contains("ATM") || d.contains("CASH") || d.contains("WDL")) return "Cash Withdrawal";
        if (d.contains("AMAZON") || d.contains("FLIPKART") || d.contains("MYNTRA")) return "Shopping";
        if (d.contains("ELECTRICITY") || d.contains("WATER") || d.contains("RECHARGE")) return "Utilities";

        return "Other";
    }
}