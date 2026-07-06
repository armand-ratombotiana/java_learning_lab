package com.javaacademy.lab32.networking;

import java.net.URI;
import java.net.http.*;
import java.net.http.WebSocket.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class WebSocketExample {

    private final HttpClient client = HttpClient.newHttpClient();

    public WebSocket connect(String uri, Listener listener) {
        CompletableFuture<WebSocket> wsFuture = client.newWebSocketBuilder()
            .buildAsync(URI.create(uri), listener);
        try {
            return wsFuture.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("WebSocket connection failed", e);
        }
    }

    public static class EchoListener implements WebSocket.Listener {
        private final AtomicReference<String> lastMessage = new AtomicReference<>();
        private final CompletableFuture<Void> closed = new CompletableFuture<>();

        @Override
        public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
            lastMessage.set(data.toString());
            return WebSocket.Listener.super.onText(ws, data, last);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket ws, int statusCode, String reason) {
            closed.complete(null);
            return WebSocket.Listener.super.onClose(ws, statusCode, reason);
        }

        public String getLastMessage() { return lastMessage.get(); }
        public CompletableFuture<Void> getClosed() { return closed; }
    }
}
