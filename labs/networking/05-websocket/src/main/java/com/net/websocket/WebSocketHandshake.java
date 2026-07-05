package com.net.websocket;

import java.util.*;
import java.security.*;

public class WebSocketHandshake {

    private static final String MAGIC = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    public static class HandshakeRequest {
        public final Map<String, String> headers;

        public HandshakeRequest(Map<String, String> headers) {
            this.headers = headers;
        }

        public String getKey() {
            return headers.get("Sec-WebSocket-Key");
        }

        public String getVersion() {
            return headers.get("Sec-WebSocket-Version");
        }

        public String getProtocol() {
            return headers.get("Sec-WebSocket-Protocol");
        }
    }

    public static class HandshakeResponse {
        public final int statusCode;
        public final String acceptKey;

        public HandshakeResponse(int statusCode, String acceptKey) {
            this.statusCode = statusCode;
            this.acceptKey = acceptKey;
        }

        public Map<String, String> toHeaders() {
            Map<String, String> h = new LinkedHashMap<>();
            h.put("Upgrade", "websocket");
            h.put("Connection", "Upgrade");
            h.put("Sec-WebSocket-Accept", acceptKey);
            return h;
        }
    }

    public static HandshakeResponse performHandshake(HandshakeRequest req) {
        String key = req.getKey();
        if (key == null) {
            return new HandshakeResponse(400, null);
        }

        try {
            String combined = key + MAGIC;
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(combined.getBytes());
            String acceptKey = Base64.getEncoder().encodeToString(digest);
            System.out.println("WebSocket handshake successful");
            System.out.println("  Key: " + key);
            System.out.println("  Accept: " + acceptKey);
            return new HandshakeResponse(101, acceptKey);
        } catch (NoSuchAlgorithmException e) {
            return new HandshakeResponse(500, null);
        }
    }

    public static void main(String[] args) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Host", "example.com");
        headers.put("Upgrade", "websocket");
        headers.put("Connection", "Upgrade");
        headers.put("Sec-WebSocket-Key", "dGhlIHNhbXBsZSBub25jZQ==");
        headers.put("Sec-WebSocket-Version", "13");
        headers.put("Sec-WebSocket-Protocol", "chat");

        HandshakeRequest req = new HandshakeRequest(headers);
        HandshakeResponse resp = performHandshake(req);

        System.out.println("\nResponse headers:");
        resp.toHeaders().forEach((k, v) -> System.out.println("  " + k + ": " + v));
    }
}
