package com.example.tracker.service.impl;

import com.example.tracker.enums.ExpenseCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MlCategoryService {

    private final RestTemplate restTemplate;

    @Value("${ml.service.url:http://ml-service:5000}")
    private String mlServiceUrl;


    public ExpenseCategory predict(String title) {
        try {
            String url = mlServiceUrl + "/predict";

            Map<String, String> requestBody = Map.of("title", title);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(
                    url, requestBody, Map.class
            );

            if (response != null && response.containsKey("category")) {
                String category = (String) response.get("category");
                log.info("ML prediction: title='{}' → category='{}' (confidence={})",
                        title, category, response.get("confidence"));
                return ExpenseCategory.valueOf(category);
            }
        } catch (Exception e) {
            log.warn("ML service unavailable, using fallback OTHER. Error: {}", e.getMessage());
        }
        return ExpenseCategory.OTHER;
    }
}