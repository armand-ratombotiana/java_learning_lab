# Security for REST APIs

## CORS Configuration
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("https://myapp.com", "http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
```

## JWT Authentication
```java
@PostMapping("/auth/login")
public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
    String token = jwtService.generateToken(request.getUsername());
    return ResponseEntity.ok(new JwtResponse(token));
}
```

## Input Validation
```java
public class CreateUserRequest {
    @NotBlank @Email private String email;
    @Size(min = 8, max = 100) private String password;
}
```

## API Key Authentication
```java
@Component
public class ApiKeyFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(request, response, chain) {
        String apiKey = request.getHeader("X-API-Key");
        if (!apiKeyService.isValid(apiKey)) {
            response.setStatus(401);
            return;
        }
        chain.doFilter(request, response);
    }
}
```
