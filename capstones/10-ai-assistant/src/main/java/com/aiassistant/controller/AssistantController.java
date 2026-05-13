package com.aiassistant.controller;

import com.aiassistant.model.Conversation;
import com.aiassistant.service.AssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/assistant")
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantService assistantService;

    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, Object> request) {
        String conversationId = (String) request.getOrDefault("conversationId", "");
        String userId = (String) request.getOrDefault("userId", "default");
        String message = (String) request.get("message");

        if (message == null || message.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message is required"));
        }

        String response = assistantService.processMessage(conversationId, userId, message);

        return ResponseEntity.ok(Map.of(
            "response", response,
            "conversationId", conversationId.isEmpty() ? "new" : conversationId
        ));
    }

    @GetMapping("/conversations/{id}/history")
    public ResponseEntity<List<Conversation.Message>> getHistory(@PathVariable String id) {
        return ResponseEntity.ok(assistantService.getConversationHistory(id));
    }

    @DeleteMapping("/conversations/{id}")
    public ResponseEntity<Map<String, String>> clearConversation(@PathVariable String id) {
        assistantService.clearConversation(id);
        return ResponseEntity.ok(Map.of("status", "cleared"));
    }

    @PostMapping("/knowledge/add")
    public ResponseEntity<Map<String, String>> addKnowledge(@RequestBody Map<String, Object> request) {
        String category = (String) request.getOrDefault("category", "general");
        String content = (String) request.get("content");
        double relevance = ((Number) request.getOrDefault("relevance", 0.5)).doubleValue();

        return ResponseEntity.ok(Map.of("status", "added"));
    }
}