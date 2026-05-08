package com.learning.micronaut.security;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class MicronautSecurityLab {

    static class User {
        final String username;
        final String passwordHash;
        final Set<String> roles;
        final boolean enabled;

        User(String username, String passwordHash, Set<String> roles, boolean enabled) {
            this.username = username;
            this.passwordHash = passwordHash;
            this.roles = roles;
            this.enabled = enabled;
        }
    }

    static class AuthenticationRequest {
        final String username;
        final String password;

        AuthenticationRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    static class AuthenticationResponse {
        final boolean authenticated;
        final String username;
        final Set<String> roles;
        final String token;

        AuthenticationResponse(boolean authenticated, String username, Set<String> roles, String token) {
            this.authenticated = authenticated;
            this.username = username;
            this.roles = roles;
            this.token = token;
        }

        static AuthenticationResponse success(String username, Set<String> roles) {
            return new AuthenticationResponse(true, username, roles,
                "jwt-" + Base64.getEncoder().encodeToString((username + ":" + System.nanoTime()).getBytes()));
        }

        static AuthenticationResponse failure() {
            return new AuthenticationResponse(false, null, Set.of(), null);
        }
    }

    interface AuthenticationProvider {
        AuthenticationResponse authenticate(AuthenticationRequest request);
    }

    interface AccessDecisionManager {
        boolean hasRole(String username, String role, Set<String> userRoles);
        boolean hasPermission(String username, String resource, String action);
    }

    static class SimpleAuthenticationProvider implements AuthenticationProvider {
        private final Map<String, User> users = new ConcurrentHashMap<>();

        public SimpleAuthenticationProvider() {
            String hash = Integer.toHexString("password".hashCode());
            users.put("admin", new User("admin", hash, Set.of("ADMIN", "USER"), true));
            users.put("user", new User("user", hash, Set.of("USER"), true));
            users.put("disabled", new User("disabled", hash, Set.of("USER"), false));
        }

        @Override
        public AuthenticationResponse authenticate(AuthenticationRequest request) {
            User user = users.get(request.username);
            if (user == null || !user.enabled) return AuthenticationResponse.failure();
            String reqHash = Integer.toHexString(request.password.hashCode());
            if (!user.passwordHash.equals(reqHash)) return AuthenticationResponse.failure();
            System.out.println("  Authenticated: " + request.username + " roles=" + user.roles);
            return AuthenticationResponse.success(user.username, user.roles);
        }
    }

    static class SimpleAccessDecisionManager implements AccessDecisionManager {
        @Override
        public boolean hasRole(String username, String role, Set<String> userRoles) {
            boolean granted = userRoles.contains(role);
            System.out.println("  Access check: user=" + username + " role=" + role + " -> " + (granted ? "GRANTED" : "DENIED"));
            return granted;
        }

        @Override
        public boolean hasPermission(String username, String resource, String action) {
            boolean granted = username.equals("admin") || !resource.startsWith("/admin");
            System.out.println("  Permission: user=" + username + " " + action + ":" + resource + " -> " + (granted ? "GRANTED" : "DENIED"));
            return granted;
        }
    }

    static class SecurityFilter {
        private final AuthenticationProvider authProvider;
        private final AccessDecisionManager decisionManager;

        SecurityFilter(AuthenticationProvider authProvider, AccessDecisionManager decisionManager) {
            this.authProvider = authProvider;
            this.decisionManager = decisionManager;
        }

        public AuthenticationResponse login(String username, String password) {
            return authProvider.authenticate(new AuthenticationRequest(username, password));
        }

        public boolean checkAccess(AuthenticationResponse auth, String resource, String action) {
            if (!auth.authenticated) return false;
            return decisionManager.hasPermission(auth.username, resource, action);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Micronaut Security Concepts Lab ===\n");

        authenticationDemo();
        roleBasedAccess();
        permissionBasedAccess();
        securityFilterDemo();
        jwtConcept();
    }

    static void authenticationDemo() {
        System.out.println("--- Authentication ---");
        SimpleAuthenticationProvider auth = new SimpleAuthenticationProvider();

        AuthenticationResponse r1 = auth.authenticate(new AuthenticationRequest("admin", "password"));
        System.out.println("  admin login: " + (r1.authenticated ? "SUCCESS" : "FAILURE"));

        AuthenticationResponse r2 = auth.authenticate(new AuthenticationRequest("admin", "wrong"));
        System.out.println("  admin wrong pw: " + (r2.authenticated ? "SUCCESS" : "FAILURE"));

        AuthenticationResponse r3 = auth.authenticate(new AuthenticationRequest("unknown", "password"));
        System.out.println("  unknown user: " + (r3.authenticated ? "SUCCESS" : "FAILURE"));
    }

    static void roleBasedAccess() {
        System.out.println("\n--- Role-Based Access Control (RBAC) ---");
        SimpleAccessDecisionManager adm = new SimpleAccessDecisionManager();

        adm.hasRole("admin", "ADMIN", Set.of("ADMIN", "USER"));
        adm.hasRole("user", "ADMIN", Set.of("USER"));
        adm.hasRole("user", "USER", Set.of("USER"));
    }

    static void permissionBasedAccess() {
        System.out.println("\n--- Permission-Based Access ---");
        SimpleAccessDecisionManager adm = new SimpleAccessDecisionManager();

        adm.hasPermission("admin", "/admin/settings", "GET");
        adm.hasPermission("user", "/admin/settings", "GET");
        adm.hasPermission("user", "/api/profile", "GET");
    }

    static void securityFilterDemo() {
        System.out.println("\n--- Security Filter Pipeline ---");
        SecurityFilter filter = new SecurityFilter(
            new SimpleAuthenticationProvider(),
            new SimpleAccessDecisionManager());

        AuthenticationResponse auth = filter.login("admin", "password");
        filter.checkAccess(auth, "/admin/settings", "GET");
        filter.checkAccess(auth, "/api/users", "DELETE");

        AuthenticationResponse badAuth = filter.login("user", "password");
        filter.checkAccess(badAuth, "/admin/settings", "GET");
    }

    static void jwtConcept() {
        System.out.println("\n--- JWT (JSON Web Token) Concept ---");
        String header = Base64.getUrlEncoder().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
        String payload = Base64.getUrlEncoder().encodeToString(
            "{\"sub\":\"admin\",\"roles\":[\"ADMIN\"],\"exp\":1893456000}".getBytes());
        String signature = "simulated-hmac-signature";

        System.out.println("  JWT: " + header + "." + payload + "." + signature);
        System.out.println("  Header: " + new String(Base64.getUrlDecoder().decode(header)));
        System.out.println("  Payload: " + new String(Base64.getUrlDecoder().decode(payload)));
        System.out.println("  (Micronaut Security validates JWTs and extracts claims)");
    }
}
