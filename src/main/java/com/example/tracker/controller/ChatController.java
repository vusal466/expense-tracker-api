package com.example.tracker.controller;

import com.example.tracker.service.impl.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "question is required"));
        }
        String answer = chatService.chat(question);
        return ResponseEntity.ok(Map.of(
                "question", question,
                "answer", answer
        ));
    }
}