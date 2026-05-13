package com.aiassistant.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;

@Document(collection = "conversations")
public class Conversation {

    @Id
    private String id;
    private String userId;
    private String title;
    private List<Message> messages;
    private Instant createdAt;
    private Instant updatedAt;
    private String status;

    public Conversation() {
        this.messages = new java.util.ArrayList<>();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = "ACTIVE";
    }

    public void addMessage(Message message) {
        messages.add(message);
        updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public record Message(
        String role,
        String content,
        List<ToolCall> toolCalls,
        Instant timestamp,
        Map<String, Object> metadata
    ) {
        public Message(String role, String content) {
            this(role, content, null, Instant.now(), null);
        }
    }

    public record ToolCall(String name, Map<String, Object> arguments) {}
}