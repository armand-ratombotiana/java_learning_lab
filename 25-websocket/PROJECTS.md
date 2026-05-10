# WebSocket Module - PROJECTS.md

---

# Mini-Project: Real-Time Chat System

## Project Overview

**Duration**: 4-5 hours  
**Difficulty**: Intermediate  
**Concepts Used**: WebSocket Endpoints, STOMP Messages, WebSocket Configuration, Real-Time Communication

This mini-project demonstrates WebSocket with Spring for real-time chat functionality.

---

## Project Structure

```
25-websocket/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── config/
│   │   └── WebSocketConfig.java
│   ├── controller/
│   │   └── ChatController.java
│   └── service/
│       └── ChatService.java
```

---

## POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>
    </dependencies>
</project>
```

---

## Implementation

```java
// config/WebSocketConfig.java
package com.learning.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }
    
    @Override
    public void registerStompEndpoints(Registry registry) {
        registry.addEndpoint("/ws")
            .setAllowedOrigins("*")
            .withSockJS();
    }
}
```

```java
// controller/ChatController.java
package com.learning.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message sendMessage(Message message) {
        return new Message(
            message.getFrom(),
            message.getText(),
            java.time.LocalDateTime.now().toString()
        );
    }
    
    public record Message(String from, String text, String timestamp) {}
}
```

```java
// Main.java
package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

---

## Build Instructions

```bash
cd 25-websocket
mvn spring-boot:run
```

---

# Real-World Project

```java
// Advanced WebSocket with Authentication
@Configuration
public class AdvancedWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureClientInboundChannel(ChannelInterceptor interceptor) {
        // Add authentication logic
    }
}
```

---

## Build Instructions

```bash
cd 25-websocket
mvn clean compile
mvn spring-boot:run
```