package com.learning.keycloak;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;

public class Lab {
    static class Token {
        String subject, issuer, clientId;
        List<String> roles;
        Instant issuedAt, expiresAt;
        boolean valid = true;

        Token(String sub, String iss, String cid, List<String> roles, long ttlSec) {
            this.subject = sub; this.issuer = iss; this.clientId = cid;
            this.roles = roles; this.issuedAt = Instant.now();
            this.expiresAt = issuedAt.plusSeconds(ttlSec);
        }

        boolean isExpired() { return Instant.now().isAfter(expiresAt); }
        boolean hasRole(String role) { return roles.contains(role); }

        public String toString() {
            return "Token{sub=" + subject + ", iss=" + issuer + ", roles=" + roles + ", exp=" + expiresAt + "}";
        }
    }

    static class AuthorizationServer {
        Map<String, String> users = new ConcurrentHashMap<>();
        Map<String, List<String>> userRoles = new ConcurrentHashMap<>();
        Map<String, String> clients = new ConcurrentHashMap<>();
        Map<String, Token> activeTokens = new ConcurrentHashMap<>();

        AuthorizationServer() {
            users.put("alice", "password123");
            users.put("bob", "pass456");
            userRoles.put("alice", List.of("user", "admin"));
            userRoles.put("bob", List.of("user"));
            clients.put("web-app", "client-secret-123");
            clients.put("mobile-app", "client-secret-456");
        }

        String authenticate(String username, String password) {
            if (users.containsKey(username) && users.get(username).equals(password))
                return username;
            return null;
        }

        Token issueToken(String username, String clientId, long ttlSec) {
            var token = new Token(username, "http://localhost:8080/auth/realms/master",
                clientId, userRoles.getOrDefault(username, List.of("user")), ttlSec);
            activeTokens.put(token.subject + ":" + token.clientId, token);
            return token;
        }

        Token validateToken(String subject, String clientId) {
            var token = activeTokens.get(subject + ":" + clientId);
            if (token == null || token.isExpired()) return null;
            return token;
        }

        boolean checkRole(String subject, String clientId, String role) {
            var token = validateToken(subject, clientId);
            return token != null && token.hasRole(role);
        }

        void logout(String subject, String clientId) {
            activeTokens.remove(subject + ":" + clientId);
        }

        Token refreshToken(String subject, String clientId, long ttlSec) {
            logout(subject, clientId);
            return issueToken(subject, clientId, ttlSec);
        }

        String authorizationCodeGrant(String username, String password, String clientId) {
            String user = authenticate(username, password);
            if (user == null) return null;
            Token token = issueToken(user, clientId, 3600);
            return "Authorization: Bearer " + Base64.getEncoder().encodeToString(token.toString().getBytes());
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Keycloak / Auth Concepts Lab ===\n");

        AuthorizationServer keycloak = new AuthorizationServer();

        System.out.println("1. Authentication (login with password):");
        String user = keycloak.authenticate("alice", "password123");
        System.out.println("   Alice authenticated: " + (user != null));

        System.out.println("\n2. OAuth2 Authorization Code Grant:");
        String bearer = keycloak.authorizationCodeGrant("alice", "password123", "web-app");
        System.out.println("   " + (bearer != null ? bearer.substring(0, 50) + "..." : "Failed"));

        System.out.println("\n3. Token Issuance (JWT-like):");
        Token token = keycloak.issueToken("alice", "web-app", 3600);
        System.out.println("   " + token);

        System.out.println("\n4. Token Validation:");
        var validated = keycloak.validateToken("alice", "web-app");
        System.out.println("   Token valid: " + (validated != null));
        System.out.println("   Subject: " + validated.subject);

        System.out.println("\n5. Role-Based Access Control (RBAC):");
        boolean isAdmin = keycloak.checkRole("alice", "web-app", "admin");
        boolean isUser = keycloak.checkRole("bob", "web-app", "admin");
        System.out.println("   Alice has admin role: " + isAdmin);
        System.out.println("   Bob has admin role: " + isUser);

        System.out.println("\n6. Token Expiry:");
        Token shortToken = keycloak.issueToken("alice", "mobile-app", 1);
        Thread.sleep(1100);
        var expiredCheck = keycloak.validateToken("alice", "mobile-app");
        System.out.println("   Short token still valid after 1.1s: " + (expiredCheck != null));

        System.out.println("\n7. Token Refresh:");
        var refreshed = keycloak.refreshToken("alice", "web-app", 7200);
        System.out.println("   Refreshed token: " + refreshed);

        System.out.println("\n8. Single Sign-On (SSO):");
        System.out.println("   Alice logs in once -> gets session cookie");
        System.out.println("   Accesses app1 (web-app), app2 (admin-console)");
        System.out.println("   No re-authentication needed for same SSO session");

        System.out.println("\n9. OAuth2 Flows:");
        System.out.println("   Authorization Code: web apps (PKCE for SPA)");
        System.out.println("   Client Credentials: machine-to-machine");
        System.out.println("   Password Grant: legacy / trusted apps");

        System.out.println("\n10. OpenID Connect (OIDC):");
        System.out.println("    /protocol/openid-connect/auth - authorize endpoint");
        System.out.println("    /protocol/openid-connect/token - token endpoint");
        System.out.println("    ID Token (JWT) contains identity claims (sub, name, email)");
        System.out.println("    UserInfo endpoint returns user profile attributes");

        System.out.println("\n=== Lab Complete ===");
    }
}
