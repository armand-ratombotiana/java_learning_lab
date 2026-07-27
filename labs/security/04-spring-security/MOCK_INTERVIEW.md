# Mock Interview — Spring Security Configuration

## Interviewer: Senior Backend Engineer (45 min)

**Q1: How does the Spring Security filter chain work? Walk through a request lifecycle.**

Candidate: Spring Security uses a chain of servlet filters registered via DelegatingFilterProxy. Each filter handles a specific security concern. When an HTTP request arrives: (1) SecurityContextPersistenceFilter — loads the SecurityContext from the session (for stateful apps) or creates an empty context. If the request has a valid JWT, it may create the SecurityContext via a custom filter. (2) UsernamePasswordAuthenticationFilter — only activated for POST /login with username/password parameters. Extracts credentials, creates an Authentication token, calls the AuthenticationManager. On success, sets the SecurityContext and calls the configured success handler. On failure, calls the failure handler. (3) BasicAuthenticationFilter — activated if the request includes an Authorization: Basic header. Decodes the base64 credentials and authenticates. (4) SecurityContextHolderFilter — ensures the context is available. (5) ExceptionTranslationFilter — catches AuthenticationException (triggers AuthenticationEntryPoint, typically 401) and AccessDeniedException (triggers AccessDeniedHandler, typically 403). Translates security exceptions to HTTP responses. (6) FilterSecurityInterceptor — the last filter. Intercepts the request to apply authorization rules based on the request matcher configuration. If the user is not authenticated, it throws AuthenticationException. If authenticated but lacks authority, it throws AccessDeniedException. The order matters — you can add custom filters at specific positions using addFilterBefore, addFilterAfter, or addFilterAt.

**Q2: Configure Spring Security for a REST API with JWT authentication. Write the config.**

Candidate: 
```java
@Configuration @EnableWebSecurity @EnableMethodSecurity
public class SecurityConfig {
    @Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // REST APIs use token-based auth, not session
            .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/health").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, authException) -> {
                    res.setContentType("application/json");
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.getWriter().write("{\"error\":\"Unauthorized\"}");
                })
                .accessDeniedHandler((req, res, accessDeniedException) -> {
                    res.setContentType("application/json");
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    res.getWriter().write("{\"error\":\"Forbidden\"}");
                })
            );
        return http.build();
    }
}
```

**Q3: How do you implement method-level security with @PreAuthorize?**

Candidate: Enable it with @EnableMethodSecurity, then use SpEL expressions: @PreAuthorize("hasRole('ADMIN')") — requires the ADMIN role. @PreAuthorize("#userId == authentication.principal.id") — ownership check where #userId is a method parameter. @PreAuthorize("hasPermission(#doc, 'DELETE')") — delegates to a custom PermissionEvaluator. @PostAuthorize("returnObject.owner == authentication.name") — validates the return value. Common expressions: hasRole('ROLE'), hasAnyRole('ROLE1','ROLE2'), hasAuthority('PERMISSION'), isAuthenticated(), permitAll(), denyAll(), and #oauth2.hasScope('read'). Method security works via AOP proxies. Important: method-level security supplements URL-level security, doesn't replace it. Always have both layers — defense in depth. For high-security operations (mass assignment, fund transfers), method security is essential because it's closer to the business logic.

**Q4: How do you customize Spring Security's CORS configuration?**

Candidate: CORS configuration bridges the browser's same-origin policy. Define a CorsConfigurationSource bean:
```java
@Bean CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("https://app.example.com", "https://admin.example.com"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
    config.setExposedHeaders(List.of("X-Request-Id", "X-Total-Count"));
    config.setAllowCredentials(true); // Must be true when credentials (cookies) are sent
    config.setMaxAge(3600L); // Cache preflight for 1 hour to reduce latency
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);
    return source;
}
```
For production, maintain an allowlist of origins, never use allowedOriginPatterns("*") with credentials. Validate the Origin header server-side. Set the Vary: Origin header for CDN caching. For development, use a profile-specific config that allows localhost origins.

**Q5: How do you test Spring Security configuration?**

Candidate: Use @WebMvcTest for controller-layer testing with mocked services. @WithMockUser for role-based tests — provides a security context without real authentication. For JWT-based APIs, create a test annotation: @WithMockJwt(roles="ADMIN") that creates a JwtAuthenticationToken. Integration tests with @SpringBootTest and a test SecurityFilterChain bean. Test scenarios: (1) Unauthenticated requests return 401. (2) Authenticated but unauthorized requests return 403. (3) Properly authorized requests return 200. (4) Invalid JWT returns 401. (5) Expired JWT returns 401. (6) JWT with wrong audience gets rejected. Use MockMvc for unit-style tests and WebTestClient for reactive endpoints. For CSRF-enabled endpoints, include the CSRF token. Example: mockMvc.perform(get("/api/admin").with(csrf())). For JWT: mockMvc.perform(get("/api/admin").with(jwt().jwt(jwt -> jwt.claim("scope","admin")))).
