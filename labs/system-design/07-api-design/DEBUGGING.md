# API Design - DEBUGGING

## API Testing Tools

### curl Examples
```bash
# Basic GET
curl http://localhost:8080/api/v1/products

# POST with JSON body
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Widget","price":19.99}'

# Include headers for debugging
curl -v http://localhost:8080/api/v1/products  # verbose

# Check response time
curl -w "Total: %{time_total}s\n" http://localhost:8080/api/v1/products
```

### HTTPie (alternative)
```bash
http GET :8080/api/v1/products
http POST :8080/api/v1/products name=Widget price:=19.99
```

## Debugging APIs

### Spring Boot DevTools
```yaml
# Fast restart for development
spring.devtools.restart.enabled: true
```

### Request Logging
```java
@Component
public class RequestLoggingFilter extends AbstractRequestLoggingFilter {
    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        log.info("Request: {} {} from {}", request.getMethod(),
            request.getRequestURI(), request.getRemoteAddr());
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        log.debug("Response: {}", message);
    }
}
```

## Common Issues

| Symptom | Cause | Check |
|---------|-------|-------|
| 404 on valid URL | Wrong base path, typo | Check @RequestMapping |
| 405 Method Not Allowed | Wrong HTTP method | Check @GetMapping/@PostMapping |
| 400 Bad Request | Validation failed | Check @Valid, request body |
| 415 Unsupported Media Type | Wrong Content-Type | Check consumes/produces |
| 500 Internal Error | Unhandled exception | Check logs, add global handler |
| CORS error | Missing CORS config | Check CorsConfiguration |
| Slow response | No pagination | Add pagination, check DB query |
