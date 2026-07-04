# 05 - WebSocket

## Overview

WebSocket provides full-duplex communication over a single TCP connection. This lab covers the WebSocket protocol, handshake, frames, STOMP, and Spring WebSocket support with Java implementations.

## Learning Objectives
- Understand WebSocket protocol and handshake
- Implement WebSocket clients and servers in Java
- Use STOMP for pub/sub messaging
- Integrate WebSocket with Spring

## Quick Start
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }
}
```
