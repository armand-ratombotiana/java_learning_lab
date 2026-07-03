# 25 - WebSocket Server

WebSocket communication with Java. Covers server endpoint configuration (@ServerEndpoint), client endpoints (@ClientEndpoint), lifecycle annotations (@OnOpen, @OnMessage, @OnClose, @OnError), message handling (text, binary, pong), session management, and broadcast patterns.

## Prerequisites

- Java 11+
- Maven 3.x
- Jakarta WebSocket API

## Key Concepts

- `@ServerEndpoint` annotation for WebSocket server endpoints
- `@ClientEndpoint` for WebSocket clients
- Lifecycle callbacks: `@OnOpen`, `@OnMessage`, `@OnClose`, `@OnError`
- Session management and tracking
- Broadcast messaging to all connected sessions
- Text and binary message handling
- Path parameters with `@PathParam`
- Ping/pong for connection health monitoring

## Module Structure

- `src/main/java/com/learning/lab/module25/Lab.java` - WebSocket concepts
- `src/test/` - Test implementations
- `SOLUTION/` - Solution code

## Learning Objectives

- Create WebSocket server endpoints with annotations
- Implement client connections and message exchange
- Build broadcast and chat-style communication patterns

## Estimated Time

- 1-2 hours

## How to Run

```bash
cd 25-websocket
mvn compile exec:java -Dexec.mainClass="com.learning.lab.module25.Lab"
```
