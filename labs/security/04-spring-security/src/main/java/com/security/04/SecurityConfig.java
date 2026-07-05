package com.security04;

import java.util.List;

/**
 * Simulates a Spring Security SecurityConfig / SecurityFilterChain.
 * 
 * SECURITY CONCEPT: Spring Security uses a filter chain architecture
 * where each filter handles a specific security concern:
 * 
 * Filter chain order (simplified):
 * 1. SecurityContextPersistenceFilter — restore SecurityContext
 * 2. LogoutFilter — handle logout
 * 3. UsernamePasswordAuthenticationFilter — process form logins
 * 4. DefaultLoginPageGeneratingFilter — generate login page
 * 5. BasicAuthenticationFilter — process HTTP Basic credentials
 * 6. RequestCacheAwareFilter — cache requests for post-auth redirect
 * 7. SecurityContextHolderAwareRequestFilter — wraps HttpServletRequest
 * 8. AnonymousAuthenticationFilter — assign anonymous identity
 * 9. SessionManagementFilter — session fixation protection
 * 10. ExceptionTranslationFilter — translate AccessDeniedException
 * 11. FilterSecurityInterceptor — authorize the request
 * 
 * This class demonstrates the equivalent configuration as a
 * SecurityFilterChain bean using the modern lambda DSL.
 */
public class SecurityConfig {

    // Simulated HTTP methods
    public enum HttpMethod { GET, POST, PUT, DELETE }

    // Simulated request
    static class Request {
        final String path;
        final HttpMethod method;
        final boolean authenticated;

        Request(String path, HttpMethod method, boolean authenticated) {
            this.path = path;
            this.method = method;
            this.authenticated = authenticated;
        }
    }

    // Simulated filter chain
    interface SecurityFilter {
        boolean filter(Request request, List<SecurityFilter> chain, int index);
    }

    /**
     * Configures security rules — equivalent to a SecurityFilterChain bean.
     * SECURITY: Define rules from most specific to least specific.
     * Deny by default — allow only explicitly permitted paths.
     */
    public static SecurityFilterChain configure() {
        SecurityFilterChain chain = new SecurityFilterChain();

        // Permit static resources without authentication
        chain.addRule((req, ch, idx) -> {
            if (req.path.startsWith("/css/") || req.path.startsWith("/js/")
                    || req.path.startsWith("/images/")
                    || req.path.equals("/favicon.ico")) {
                System.out.println("  [Pass] Static resource: " + req.path);
                return true; // permitted
            }
            return ch.get(idx + 1).filter(req, ch, idx + 1);
        });

        // Public endpoints (no auth required)
        chain.addRule((req, ch, idx) -> {
            if (req.path.equals("/login") || req.path.equals("/register")
                    || req.path.equals("/forgot-password")
                    || req.path.equals("/actuator/health")) {
                System.out.println("  [Pass] Public endpoint: " + req.path);
                return true;
            }
            return ch.get(idx + 1).filter(req, ch, idx + 1);
        });

        // Admin endpoints — require ADMIN role
        chain.addRule((req, ch, idx) -> {
            if (req.path.startsWith("/admin")) {
                if (!req.authenticated) {
                    System.out.println("  [Deny] Admin requires auth: " + req.path);
                    return false;
                }
                System.out.println("  [Pass] Admin access granted: " + req.path);
                return true;
            }
            return ch.get(idx + 1).filter(req, ch, idx + 1);
        });

        // API endpoints — require authentication
        chain.addRule((req, ch, idx) -> {
            if (req.path.startsWith("/api")) {
                if (!req.authenticated) {
                    System.out.println("  [Deny] API requires auth: " + req.path);
                    return false;
                }
                System.out.println("  [Pass] API access: " + req.path);
                return true;
            }
            return ch.get(idx + 1).filter(req, ch, idx + 1);
        });

        // Default: all other requests require authentication
        chain.addRule((req, ch, idx) -> {
            if (!req.authenticated) {
                System.out.println("  [Deny] Authentication required: " + req.path);
                return false;
            }
            System.out.println("  [Pass] Authenticated: " + req.path);
            return true;
        });

        return chain;
    }

    static class SecurityFilterChain {
        private final List<SecurityFilter> filters = new java.util.ArrayList<>();

        void addRule(SecurityFilter filter) {
            filters.add(filter);
        }

        SecurityFilter get(int index) {
            return index < filters.size() ? filters.get(index) : (r, c, i) -> false;
        }

        boolean evaluate(Request request) {
            return filters.get(0).filter(request, filters, 0);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Spring Security Filter Chain Simulation ===\n");

        SecurityFilterChain chain = configure();

        // Test various scenarios
        List<Request> testRequests = List.of(
            new Request("/css/style.css", HttpMethod.GET, false),
            new Request("/login", HttpMethod.GET, false),
            new Request("/api/users", HttpMethod.GET, true),
            new Request("/api/users", HttpMethod.GET, false),
            new Request("/admin/settings", HttpMethod.GET, true),
            new Request("/admin/settings", HttpMethod.GET, false),
            new Request("/secret-data", HttpMethod.GET, false),
            new Request("/secret-data", HttpMethod.GET, true)
        );

        for (Request req : testRequests) {
            System.out.println("Request: " + req.method + " " + req.path
                    + " [auth=" + req.authenticated + "]");
            boolean allowed = chain.evaluate(req);
            System.out.println("  Result: " + (allowed ? "ALLOWED" : "DENIED") + "\n");
        }

        System.out.println("--- Security Principles ---");
        System.out.println("1. PermitAll for static resources and public endpoints");
        System.out.println("2. Authenticated for API endpoints");
        System.out.println("3. Role-based access for admin endpoints");
        System.out.println("4. Deny by default (everything else requires auth)");
    }
}
