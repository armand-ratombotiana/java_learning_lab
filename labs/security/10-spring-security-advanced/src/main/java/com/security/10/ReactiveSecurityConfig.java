package com.security10;

import java.security.SecureRandom;
import java.util.*;
import java.util.Base64;

/**
 * Demonstrates reactive (WebFlux) security configuration.
 * 
 * SECURITY CONCEPT: Reactive security with Spring Security's
 * WebFlux support uses the same concepts as Servlet-based security
 * but with a fully non-blocking, reactive foundation.
 * 
 * Key differences from Servlet security:
 * - SecurityWebFilterChain instead of SecurityFilterChain
 * - ServerHttpSecurity instead of HttpSecurity
 * - ReactiveUserDetailsService instead of UserDetailsService
 * - Mono/Flux-based authentication and authorization
 * - WebFilter-based architecture (not Servlet Filters)
 * 
 * Benefits of reactive security:
 * - Non-blocking I/O for all security operations
 * - Reactive UserDetailsService can query reactive repositories
 * - Seamless integration with WebFlux controllers
 * - First-class support for functional endpoints (RouterFunction)
 */
public class ReactiveSecurityConfig {

    // Simulated reactive user repository
    static class ReactiveUser {
        final String username;
        final String password; // hashed in production
        final Set<String> roles;
        final boolean enabled;

        ReactiveUser(String username, String password, Set<String> roles,
                     boolean enabled) {
            this.username = username;
            this.password = password;
            this.roles = roles;
            this.enabled = enabled;
        }
    }

    private final Map<String, ReactiveUser> userRepository = new LinkedHashMap<>();

    public ReactiveSecurityConfig() {
        // In production: reactive repository (R2DBC, MongoDB Reactive)
        userRepository.put("admin", new ReactiveUser("admin",
                "$2a$12$hashed_admin_password", Set.of("ADMIN", "USER"), true));
        userRepository.put("user", new ReactiveUser("user",
                "$2a$12$hashed_user_password", Set.of("USER"), true));
    }

    /**
     * Simulates a ReactiveUserDetailsService.
     * In reactive Spring: implements ReactiveUserDetailsService.
     * SECURITY: Use Mono.just() or Mono.from() for reactive types.
     * Never block in reactive pipeline!
     */
    public java.util.Optional<ReactiveUser> findByUsername(String username) {
        // Simulates: return userRepository.findByUsername(username);
        return Optional.ofNullable(userRepository.get(username));
    }

    /**
     * Simulates SecurityWebFilterChain for reactive apps.
     * In Spring: @Bean SecurityWebFilterChain securityWebFilterChain(
     *     ServerHttpSecurity http) { ... }
     * 
     * SECURITY: Reactive rules follow the same principles:
     * - Order matters (most specific first)
     * - Deny by default
     * - Use path matchers appropriate for reactive (not AntPathRequestMatcher)
     */
    public List<SecurityRule> buildReactiveSecurityRules() {
        List<SecurityRule> rules = new ArrayList<>();

        // 1. Public endpoints
        rules.add(new SecurityRule(
                "/webjars/**", "GET", false, false, Set.of()));

        // 2. Static resources
        rules.add(new SecurityRule(
                "/css/**", "GET", false, false, Set.of()));

        // 3. Login/register endpoints
        rules.add(new SecurityRule(
                "/login", "GET", false, false, Set.of()));
        rules.add(new SecurityRule(
                "/register", "POST", false, false, Set.of()));

        // 4. Admin endpoints — ADMIN role only
        rules.add(new SecurityRule(
                "/admin/**", "**", true, true, Set.of("ADMIN")));

        // 5. API endpoints — authenticated users
        rules.add(new SecurityRule(
                "/api/**", "**", true, false, Set.of()));

        // 6. User profile — authenticated
        rules.add(new SecurityRule(
                "/user/**", "**", true, false, Set.of()));

        return rules;
    }

    /**
     * Represents a security rule for the reactive filter chain.
     */
    static class SecurityRule {
        final String pathPattern;
        final String method;
        final boolean requireAuth;
        final boolean requireRole;
        final Set<String> roles;

        SecurityRule(String pathPattern, String method, boolean requireAuth,
                    boolean requireRole, Set<String> roles) {
            this.pathPattern = pathPattern;
            this.method = method;
            this.requireAuth = requireAuth;
            this.requireRole = requireRole;
            this.roles = roles;
        }
    }

    /**
     * Evaluates a request (simulated) against the security rules.
     * SECURITY: In reactive, this would be a WebFilter that returns Mono<Void>.
     */
    public boolean evaluateRequest(String path, String method, String username) {
        List<SecurityRule> rules = buildReactiveSecurityRules();

        for (SecurityRule rule : rules) {
            if (matches(path, rule.pathPattern) && matches(method, rule.method)) {
                if (!rule.requireAuth) {
                    return true; // Public endpoint
                }
                if (username == null || username.isEmpty()) {
                    return false; // Not authenticated
                }
                if (rule.requireRole) {
                    Optional<ReactiveUser> user = findByUsername(username);
                    if (user.isEmpty() || !user.get().enabled) {
                        return false;
                    }
                    if (rule.roles.stream().noneMatch(user.get().roles::contains)) {
                        return false; // Missing required role
                    }
                }
                return true; // Authenticated + authorized
            }
        }

        return false; // No matching rule — deny by default
    }

    private boolean matches(String actual, String pattern) {
        if ("**".equals(pattern)) return true;
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return actual.startsWith(prefix);
        }
        return actual.equals(pattern);
    }

    /**
     * Generates a secure random value for CSRF token or state parameter.
     * Reactive apps should use ServerHttpSecurity's CSRF support.
     */
    public static String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] token = new byte[32];
        random.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }

    public static void main(String[] args) {
        ReactiveSecurityConfig config = new ReactiveSecurityConfig();

        System.out.println("=== Reactive (WebFlux) Security Configuration ===\n");

        // Test request scenarios
        String[][] testCases = {
            {"GET",  "/webjars/swagger-ui/3.0/index.html", null},
            {"POST", "/login", null},
            {"GET",  "/api/users", "admin"},
            {"GET",  "/api/users", "user"},
            {"GET",  "/api/users", null},
            {"POST", "/admin/settings", "admin"},
            {"POST", "/admin/settings", "user"},
            {"GET",  "/unknown/path", "admin"}
        };

        System.out.println("Reactive Security Rules Evaluation:");
        for (String[] test : testCases) {
            boolean allowed = config.evaluateRequest(test[1], test[0], test[2]);
            System.out.printf("  %-6s %-45s user=%-8s → %s%n",
                    test[0], test[1], test[2],
                    allowed ? "ALLOWED" : "DENIED");
        }

        System.out.println("\n--- Reactive Security Features ---");
        System.out.println("1. SecurityWebFilterChain (non-blocking filters)");
        System.out.println("2. ReactiveUserDetailsService (reactive user lookup)");
        System.out.println("3. ServerHttpSecurity (replacement for HttpSecurity)");
        System.out.println("4. @EnableWebFluxSecurity (replacement for @EnableWebSecurity)");
        System.out.println("5. Reactive CSRF tokens via ServerHttpSecurity");
        System.out.println("6. OAuth2 Resource Server with reactive support");
    }
}
