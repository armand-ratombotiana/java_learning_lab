package com.learning.security;

import java.util.*;

public class SecurityTraining {

    public static void main(String[] args) {
        System.out.println("=== Spring Security Training ===");

        demonstrateAuthentication();
        demonstrateAuthorization();
        demonstrateJwt();
        demonstrateOAuth2();
    }

    private static void demonstrateAuthentication() {
        System.out.println("\n--- Authentication Methods ---");

        String[] authMethods = {
            "Form Login - traditional web login",
            "HTTP Basic - username/password in header",
            "JWT Token - stateless token-based",
            "OAuth 2.0 - third-party authentication",
            "LDAP - directory services",
            "Database - custom user details"
        };

        for (String m : authMethods) System.out.println("  " + m);

        System.out.println("\nSecurity Filter Chain:");
        String filterChain = """
            @Configuration
            @EnableWebSecurity
            public class SecurityConfig {
                
                @Bean
                public SecurityFilterChain filterChain(
                        HttpSecurity http) throws Exception {
                    http
                        .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/public/**").permitAll()
                            .requestMatchers("/admin/**").hasRole("ADMIN")
                            .anyRequest().authenticated()
                        )
                        .formLogin(login -> login
                            .loginPage("/login")
                            .defaultSuccessUrl("/dashboard")
                        )
                        .httpBasic(Customizer.withDefaults());
                    return http.build();
                }
            }""";
        System.out.println(filterChain);
    }

    private static void demonstrateAuthorization() {
        System.out.println("\n--- Authorization ---");

        System.out.println("Method-Level Security:");
        String[] methods = {
            "@PreAuthorize(\"hasRole('ADMIN')\") - method access",
            "@Secured(\"ROLE_USER\") - role-based",
            "@PermitAll - allow all",
            "@DenyAll - deny all"
        };
        for (String m : methods) System.out.println("  " + m);

        System.out.println("\nAccess Control Examples:");
        String[] examples = {
            "hasRole('USER') - user role required",
            "hasAnyRole('USER', 'ADMIN') - multiple roles",
            "hasAuthority('READ') - specific permission",
            "isAuthenticated() - logged in required",
            "isAnonymous() - not logged in",
            "#userId == authentication.principal.id - owner check"
        };
        for (String e : examples) System.out.println("  " + e);

        System.out.println("\nSecurity Expressions:");
        Map<String, String> expressions = new LinkedHashMap<>();
        expressions.put("web.authorizeHttpRequests", "URL-based rules");
        expressions.put("@userService.isOwner()", "Custom bean method");
        expressions.put("T(System).currentTimeMillis()", "Static method");

        expressions.forEach((k, v) -> System.out.printf("  %s: %s%n", k, v));
    }

    private static void demonstrateJwt() {
        System.out.println("\n--- JWT (JSON Web Tokens) ---");

        String jwtStructure = """
            JWT Structure: header.payload.signature
            header: {"alg": "HS256", "typ": "JWT"}
            payload: {"sub": "user123", "role": "ADMIN", "exp": 1234567890}
            signature: HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload))""";
        System.out.println(jwtStructure);

        System.out.println("\nJWT Flow:");
        String[] flow = {
            "1. Client sends credentials to /login",
            "2. Server validates and generates JWT",
            "3. Server returns JWT to client",
            "4. Client includes JWT in Authorization header",
            "5. Server validates JWT on each request",
            "6. JWT expires after set time"
        };
        for (String f : flow) System.out.println("  " + f);

        System.out.println("\nJWT Dependencies:");
        String[] deps = {
            "jjwt-api - JWT library",
            "jjwt-impl - implementation",
            "jjwt-jackson - JSON parsing"
        };
        for (String d : deps) System.out.println("  " + d);
    }

    private static void demonstrateOAuth2() {
        System.out.println("\n--- OAuth 2.0 ---");

        String[] grants = {
            "Authorization Code - full web flow",
            "Client Credentials - machine-to-machine",
            "Password Credentials - trusted clients",
            "Refresh Token - get new access tokens"
        };
        System.out.println("Grant Types:");
        for (String g : grants) System.out.println("  " + g);

        System.out.println("\nOAuth2 Providers:");
        String[] providers = {"Google", "GitHub", "Facebook", "Okta", "Keycloak"};
        for (String p : providers) System.out.println("  - " + p);

        System.out.println("\nOAuth2 Resource Server Config:");
        String config = """
            @Configuration
            @EnableWebSecurity
            public class OAuth2Config {
                
                @Bean
                public SecurityFilterChain filterChain(
                        HttpSecurity http) {
                    http
                        .oauth2ResourceServer(oauth2 -> oauth2
                            .jwt(jwt -> jwt.jwtDecoder(jwtDecoder()))
                        );
                    return http.build();
                }
            }""";
        System.out.println(config);

        System.out.println("\nSecurity Best Practices:");
        String[] practices = {
            "Use HTTPS always",
            "Hash passwords with BCrypt",
            "Implement rate limiting",
            "Use secure cookies (HttpOnly, Secure)",
            "Validate and sanitize inputs",
            "Implement CSRF protection",
            "Use modern security headers"
        };
        for (String p : practices) System.out.println("  " + p);
    }
}