package com.learning.lab.module20;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 20: Spring Security ===");
        securityBasics();
        authenticationDemo();
        authorizationDemo();
        passwordEncoderDemo();
        csrfDemo();
        corsDemo();
        jwtDemo();
        methodSecurityDemo();
    }

    static void securityBasics() {
        System.out.println("\n--- Security Fundamentals ---");
        System.out.println("Core concepts:");
        System.out.println("  - Authentication: Who are you?");
        System.out.println("  - Authorization: What can you do?");
        System.out.println("  - Principal: The authenticated user");
        System.out.println("  - Role: Group of permissions");
        System.out.println("  - Authority: Individual permission");
        
        System.out.println("\nSpring Security architecture:");
        System.out.println("  SecurityFilterChain -> Authentication -> Authorization");
    }

    static void authenticationDemo() {
        System.out.println("\n--- Authentication Configuration ---");
        System.out.println("@Configuration");
        System.out.println("public class SecurityConfig {");
        System.out.println("    @Bean");
        System.out.println("    SecurityFilterChain securityFilterChain(HttpSecurity http) {");
        System.out.println("        http");
        System.out.println("            .authorizeHttpRequests(auth -> auth");
        System.out.println("                .requestMatchers(\"/public/**\").permitAll()");
        System.out.println("                .anyRequest().authenticated()");
        System.out.println("            )");
        System.out.println("            .formLogin(Customizer.withDefaults());");
        System.out.println("        return http.build();");
        System.out.println("    }");
        System.out.println("}");
        
        System.out.println("\nForm login flow:");
        System.out.println("  1. User accesses protected resource");
        System.out.println("  2. Spring redirects to /login");
        System.out.println("  3. User submits credentials");
        System.out.println("  4. AuthenticationManager validates");
        System.out.println("  5. Success -> Original URL / Failure -> /login?error");
    }

    static void authorizationDemo() {
        System.out.println("\n--- Authorization Rules ---");
        System.out.println("URL-based authorization:");
        System.out.println("  .requestMatchers(\"/admin/**\").hasRole(\"ADMIN\")");
        System.out.println("  .requestMatchers(\"/user/**\").hasAnyRole(\"USER\", \"ADMIN\")");
        System.out.println("  .requestMatchers(\"/api/**\").authenticated()");
        System.out.println("  .anyRequest().permitAll()");
        
        System.out.println("\nAccess control expressions:");
        System.out.println("  hasRole('ROLE_USER')");
        System.out.println("  hasAnyRole('USER', 'ADMIN')");
        System.out.println("  hasAuthority('ROLE_USER')");
        System.out.println("  hasAuthorityGranted('USER_READ')");
        System.out.println("  isAuthenticated()");
        System.out.println("  isAnonymous()");
        System.out.println("  isFullyAuthenticated()");
    }

    static void passwordEncoderDemo() {
        System.out.println("\n--- Password Encoding ---");
        System.out.println("PasswordEncoder interface:");
        System.out.println("  String encode(CharSequence rawPassword)");
        System.out.println("  boolean matches(CharSequence, String)");
        
        System.out.println("\nEncoders:");
        System.out.println("  BCryptPasswordEncoder (recommended)");
        System.out.println("  Argon2PasswordEncoder");
        System.out.println("  Pbkdf2PasswordEncoder");
        System.out.println("  SCryptPasswordEncoder");
        System.out.println("  NoOpPasswordEncoder (testing only)");
        
        System.out.println("\nUsage:");
        System.out.println("  @Bean");
        System.out.println("  PasswordEncoder passwordEncoder() {");
        System.out.println("      return new BCryptPasswordEncoder();");
        System.out.println("  }");
    }

    static void csrfDemo() {
        System.out.println("\n--- CSRF Protection ---");
        System.out.println("CSRF (Cross-Site Request Forgery):");
        System.out.println("  - Attacker tricks user into submitting request");
        System.out.println("  - Browser automatically includes cookies");
        System.out.println("  - Server can't distinguish legitimate requests");
        
        System.out.println("\nMitigations:");
        System.out.println("  1. CSRF Token (Synchronizer Token)");
        System.out.println("  2. SameSite cookies");
        System.out.println("  3. Double Submit Cookie");
        
        System.out.println("\nSpring Security CSRF:");
        System.out.println("  http.csrf(csrf -> csrf");
        System.out.println("      .csrfTokenRepository(CookieCsrfTokenRepository)");
        System.out.println("      .ignoringRequestMatchers(\"/api/public/**\")");
        System.out.println("  );");
    }

    static void corsDemo() {
        System.out.println("\n--- CORS Configuration ---");
        System.out.println("Cross-Origin Resource Sharing:");
        System.out.println("  - Browsers block cross-origin requests");
        System.out.println("  - CORS allows servers to permit requests");
        
        System.out.println("\nHeaders:");
        System.out.println("  Access-Control-Allow-Origin");
        System.out.println("  Access-Control-Allow-Methods");
        System.out.println("  Access-Control-Allow-Headers");
        System.out.println("  Access-Control-Max-Age");
        
        System.out.println("\nSpring Security CORS:");
        System.out.println("  http.cors(Customizer.withDefaults());");
        System.out.println("\n  @Bean");
        System.out.println("  CorsConfigurationSource corsConfigurationSource() {");
        System.out.println("      CorsConfiguration config = new CorsConfiguration();");
        System.out.println("      config.setAllowedOrigins(List.of(\"http://localhost:3000\"));");
        System.out.println("      config.setAllowedMethods(List.of(\"*\"));");
        System.out.println("      return new UrlBasedCorsConfigurationSource();");
        System.out.println("  }");
    }

    static void jwtDemo() {
        System.out.println("\n--- JWT Authentication ---");
        System.out.println("JWT Structure:");
        System.out.println("  header.payload.signature");
        System.out.println("  eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9");
        System.out.println("  .eyJzdWIiOiIxMjM0NTY3ODkwIn0");
        System.out.println("  .SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
        
        System.out.println("\nJWT Filter:");
        System.out.println("  OncePerRequestFilter + JWT validation");
        System.out.println("  Extract token from Authorization header");
        System.out.println("  Validate signature and claims");
        System.out.println("  Set SecurityContext");
        
        System.out.println("\nClaims:");
        System.out.println("  iss (issuer)");
        System.out.println("  sub (subject/user ID)");
        System.out.println("  iat (issued at)");
        System.out.println("  exp (expiration)");
        System.out.println("  roles / authorities");
    }

    static void methodSecurityDemo() {
        System.out.println("\n--- Method-Level Security ---");
        System.out.println("@EnableMethodSecurity");
        System.out.println("\nAnnotations:");
        System.out.println("  @PreAuthorize(\"hasRole('ADMIN')\")");
        System.out.println("  @PreAuthorize(\"hasAuthority('USER_WRITE')\")");
        System.out.println("  @PreAuthorize(\"@service.checkUser(#userId)\")");
        System.out.println("  @PostAuthorize(\"returnObject.owner == authentication.name\")");
        System.out.println("  @Secured(\"ROLE_ADMIN\")");
        
        System.out.println("\nSpEL expressions:");
        System.out.println("  #user.name");
        System.out.println("  #user.getId()");
        System.out.println("  hasRole('USER')");
        System.out.println("  hasPermission(#resource, 'READ')");
        
        System.out.println("\nExample:");
        System.out.println("  @PreAuthorize(\"hasRole('ADMIN') OR hasRole('USER')\")");
        System.out.println("  public User updateUser(@RequestBody User user) { ... }");
    }
}
