package com.example.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final RestTemplate restTemplate;

    @Value("${chat.service.url:http://localhost:5001}")
    private String chatServiceUrl;

    public String chat(String question) {
        try {
            String url = chatServiceUrl + "/chat";
            Map<String, String> requestBody = Map.of("question", question);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(
                    url, requestBody, Map.class
            );

            if (response != null && response.containsKey("answer")) {
                return (String) response.get("answer");
            }
        } catch (Exception e) {
            log.warn("Chat service unavailable: {}", e.getMessage());
        }
        return "Chat service is currently unavailable. Please try again later.";
    }
}