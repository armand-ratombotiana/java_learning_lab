package com.learning.lab.module25;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.websocket.*;

public class Lab {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Module 25: WebSocket Server ===");

        serverEndpointDemo();
        clientDemo();
        messageHandlingDemo();
    }

    static void serverEndpointDemo() throws Exception {
        System.out.println("\n--- WebSocket Server Endpoint ---");
        System.out.println("@ServerEndpoint annotation for server endpoints");
        System.out.println("@ClientEndpoint annotation for client endpoints");
        System.out.println("Methods: @OnOpen, @OnMessage, @OnClose, @OnError");
        System.out.println("URI: ws://localhost:8080/chat");
    }

    static void clientDemo() throws Exception {
        System.out.println("\n--- WebSocket Client ---");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        Session session = container.connectToServer(ClientEndpoint.class, new URI("ws://localhost:8080/echo"));
        session.getBasicRemote().sendText("Hello Server!");
        session.close();
    }

    static void messageHandlingDemo() throws Exception {
        System.out.println("\n--- Message Handling ---");
        System.out.println("Text messages: String, Reader");
        System.out.println("Binary messages: byte[], InputStream");
        System.out.println("Pong: PongHandler for ping/pong");
        System.out.println("Path parameters: @PathParam in URI template");
    }
}

@ClientEndpoint
class EchoClientEndpoint {
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to server");
    }

    @OnMessage
    public String onMessage(String message) {
        System.out.println("Received: " + message);
        return "Echo: " + message;
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Closed: " + reason.getReasonPhrase());
    }

    @OnError
    public void onError(Throwable error) {
        System.out.println("Error: " + error.getMessage());
    }
}

class ChatServerEndpoint {
    private static final java.util.Set<Session> sessions = java.util.Collections.synchronizedSet(new java.util.HashSet<>());

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("Session opened: " + session.getId());
    }

    @OnMessage
    public String onMessage(String message, Session session) {
        broadcast(message);
        return "Message broadcasted";
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        sessions.remove(session);
        System.out.println("Session closed: " + session.getId());
    }

    private void broadcast(String message) {
        for (Session s : sessions) {
            try {
                s.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}