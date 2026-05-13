package com.aiassistant.service;

import com.aiassistant.core.*;
import com.aiassistant.model.Conversation;
import com.aiassistant.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssistantService {

    private final Planner planner;
    private final ToolExecutor toolExecutor;
    private final MemoryManager memoryManager;
    private final RAGService ragService;
    private final ConversationRepository conversationRepository;

    public String processMessage(String conversationId, String userId, String message) {
        log.info("Processing message for user {}: {}", userId, message);

        Conversation conversation = getOrCreateConversation(conversationId, userId);

        memoryManager.addToShortTerm(userId, message);

        List<Conversation.Message> context = conversation.getMessages();
        Planner.Plan plan = planner.createPlan(message, context);

        StringBuilder response = new StringBuilder();

        for (Planner.Plan.Step step = plan.getNextStep(); step != null; step = plan.getNextStep()) {
            String stepResult = executeStep(step, message, userId);
            response.append(stepResult).append("\n");
        }

        conversation.addMessage(new Conversation.Message("user", message));
        conversation.addMessage(new Conversation.Message("assistant", response.toString().trim()));
        conversationRepository.save(conversation);

        log.info("Response generated for user {}", userId);
        return response.toString().trim();
    }

    private String executeStep(Planner.Plan.Step step, String originalMessage, String userId) {
        return switch (step.action()) {
            case "RETRIEVE" -> {
                List<String> knowledge = ragService.retrieve(originalMessage, 3);
                yield knowledge.isEmpty() ? "" : "Based on knowledge: " + String.join(", ", knowledge);
            }
            case "CALCULATE" -> {
                Map<String, Object> args = Map.of("a", 10, "b", 5, "operation", "add");
                String result = toolExecutor.execute("calculator", args);
                yield "Calculation result: " + result;
            }
            case "SEARCH" -> {
                String result = toolExecutor.execute("web_search", Map.of("query", originalMessage));
                yield result;
            }
            case "CODE" -> {
                String result = toolExecutor.execute("code_executor", Map.of("code", originalMessage));
                yield "Code execution: " + result;
            }
            case "RESPOND" -> {
                List<String> recentContext = memoryManager.getRecentContext(userId, 5);
                String contextStr = recentContext.isEmpty() ? "" : "Based on our conversation: " + String.join(" ", recentContext);
                List<String> knowledge = ragService.retrieve(originalMessage, 2);
                String knowledgeStr = knowledge.isEmpty() ? "" : "Additionally: " + String.join(" ", knowledge);
                
                yield generateResponse(originalMessage, contextStr, knowledgeStr);
            }
            default -> "I'm not sure how to help with that.";
        };
    }

    private String generateResponse(String message, String context, String knowledge) {
        StringBuilder response = new StringBuilder();
        
        response.append("Based on your question about '").append(message).append("':\n\n");

        if (!knowledge.isEmpty()) {
            response.append("From my knowledge base: ").append(knowledge).append("\n\n");
        }

        if (!context.isEmpty()) {
            response.append("From our recent conversation: ").append(context).append("\n\n");
        }

        response.append("This demonstrates the AI Assistant's multi-agent capabilities combining RAG, ");
        response.append("tool execution, and memory management.");

        return response.toString();
    }

    public List<Conversation.Message> getConversationHistory(String conversationId) {
        return conversationRepository.findById(conversationId)
            .map(Conversation::getMessages)
            .orElse(List.of());
    }

    public void clearConversation(String conversationId) {
        conversationRepository.findById(conversationId).ifPresent(conv -> {
            conv.setStatus("ARCHIVED");
            conversationRepository.save(conv);
        });
    }

    private Conversation getOrCreateConversation(String conversationId, String userId) {
        if (conversationId != null && !conversationId.isEmpty()) {
            return conversationRepository.findById(conversationId)
                .orElseGet(() -> createNewConversation(userId));
        }
        return createNewConversation(userId);
    }

    private Conversation createNewConversation(String userId) {
        Conversation conversation = new Conversation();
        conversation.setUserId(userId);
        conversation.setTitle("Conversation " + System.currentTimeMillis());
        return conversationRepository.save(conversation);
    }
}