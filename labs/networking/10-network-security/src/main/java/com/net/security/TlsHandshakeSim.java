package com.net.security;

import java.util.*;
import java.security.*;
import javax.crypto.*;

public class TlsHandshakeSim {

    public static class TlsHandshake {
        private final String serverPrivateKey = "server-private-key-abc123";
        private final String serverPublicKey = "server-public-key-abc123";
        private final String serverCertificate = "CN=example.com,O=Example Inc";

        private String sessionKey;
        private boolean handshakeComplete;

        public static class HandshakeResult {
            public final boolean success;
            public final List<String> messages;
            public final String sessionId;

            public HandshakeResult(boolean success, List<String> messages) {
                this.success = success;
                this.messages = messages;
                this.sessionId = "session-" + UUID.randomUUID().toString().substring(0, 8);
            }
        }

        public HandshakeResult performHandshake() {
            List<String> messages = new ArrayList<>();

            messages.add("ClientHello: TLS 1.3, Cipher Suites [TLS_AES_128_GCM_SHA256]");
            messages.add("ServerHello: TLS 1.3, Selected Cipher: TLS_AES_128_GCM_SHA256");
            messages.add("ServerCertificate: " + serverCertificate);
            messages.add("ServerKeyExchange: " + serverPublicKey.substring(0, 20) + "...");
            messages.add("ServerHelloDone");

            messages.add("ClientKeyExchange: [encrypted premaster secret]");
            messages.add("ChangeCipherSpec");
            messages.add("ClientFinished: [encrypted]");

            messages.add("ChangeCipherSpec");
            messages.add("ServerFinished: [encrypted]");

            this.sessionKey = "derived-session-key-" + UUID.randomUUID().toString().substring(0, 8);
            this.handshakeComplete = true;

            return new HandshakeResult(true, messages);
        }

        public void sendEncrypted(String data) {
            if (!handshakeComplete) {
                throw new IllegalStateException("Handshake not complete");
            }
            System.out.println("Encrypted data sent using session key: " + sessionKey);
        }
    }

    public static class TLSSocket {
        private final TlsHandshake handshake = new TlsHandshake();

        public boolean connect() {
            System.out.println("=== TLS Handshake ===");
            TlsHandshake.HandshakeResult result = handshake.performHandshake();
            for (String msg : result.messages) {
                System.out.println("  " + msg);
            }
            System.out.println("Session ID: " + result.sessionId);
            System.out.println("Handshake: " + (result.success ? "SUCCESS" : "FAILED"));
            return result.success;
        }

        public void send(String data) {
            handshake.sendEncrypted(data);
        }
    }

    public static void main(String[] args) {
        TLSSocket socket = new TLSSocket();
        boolean connected = socket.connect();
        if (connected) {
            socket.send("GET /secure-data HTTP/1.1");
        }
    }
}
