# REST APIs - Exercises

## Exercise 1: REST Client Implementation
**Objective**: Build a reusable REST client.

### Task
Create a generic REST client with:
1. Support for all HTTP methods
2. Request/response logging
3. Error handling
4. Timeout configuration

### Implementation
```java
public class RestClient {
    
    public <T> Response<T> execute(Request<T> request) {
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(request.getUrl()))
            .timeout(Duration.ofSeconds(30))
            .method(request.getMethod(), BodyPublishers.ofString(request.getBody()))
            .build();
        
        return client.send(httpRequest, new ResponseHandler<>(request.getResponseType()));
    }
}
```

---

## Exercise 2: API Versioning
**Objective**: Implement multiple versioning strategies.

### Task
Create a versioning system supporting:
1. URL path versioning (`/api/v1/users`)
2. Header versioning (`X-API-Version: v1`)
3. Query parameter versioning (`/users?version=1`)

### Implementation
```java
public class VersionRouter {
    
    public String resolveVersion(HttpRequest request) {
        // Check header
        String headerVersion = request.headers().firstValue("X-API-Version").orElse(null);
        if (headerVersion != null) return headerVersion;
        
        // Check query param
        String queryVersion = request.url().query().get("version");
        if (queryVersion != null) return queryVersion;
        
        // Default to v1
        return "v1";
    }
    
    public Object route(String version, String endpoint, Object payload) {
        return switch (version) {
            case "v1" -> handleV1(endpoint, payload);
            case "v2" -> handleV2(endpoint, payload);
            default -> throw new UnsupportedOperationException("Version not supported");
        };
    }
}
```

---

## Exercise 3: Rate Limiting
**Objective**: Implement rate limiting for API protection.

### Task
Create a rate limiter with:
1. Token bucket algorithm
2. Per-client rate limiting
3. Rate limit headers in response

### Implementation
```java
public class RateLimiter {
    
    private final Map<String, RateLimitBucket> buckets = new ConcurrentHashMap<>();
    private final int maxRequests;
    private final Duration window;
    
    public boolean allowRequest(String clientId) {
        RateLimitBucket bucket = buckets.computeIfAbsent(clientId, 
            k -> new RateLimitBucket(maxRequests, window));
        return bucket.tryConsume();
    }
    
    public Map<String, String> getHeaders(String clientId) {
        RateLimitBucket bucket = buckets.get(clientId);
        return Map.of(
            "X-RateLimit-Limit", String.valueOf(maxRequests),
            "X-RateLimit-Remaining", String.valueOf(bucket.getRemaining()),
            "X-RateLimit-Reset", String.valueOf(bucket.getResetTime())
        );
    }
}
```

---

## Exercise 4: Response Caching
**Objective**: Implement HTTP caching with ETags.

### Task
Create a caching system:
1. Generate ETag for responses
2. Handle conditional requests (If-None-Match)
3. Cache invalidation strategies

### Implementation
```java
public class ResponseCache {
    
    public <T> CacheEntry<T> get(String key) {
        CacheEntry<T> entry = cache.get(key);
        if (entry != null && entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return entry;
    }
    
    public <T> String generateETag(T response) {
        return '"' + DigestUtils.md5Hex(toJson(response)) + '"';
    }
    
    public boolean isNotModified(HttpRequest request, String etag) {
        return request.headers().firstValue("If-None-Match")
            .map(ifNoneMatch -> ifNoneMatch.equals(etag))
            .orElse(false);
    }
}
```

---

## Exercise 5: Request/Response Logging
**Objective**: Create comprehensive API logging.

### Task
Implement logging with:
1. Request/response body logging
2. Timing information
3. Correlation IDs for tracing

### Implementation
```java
public class RequestLoggingFilter {
    
    public void logRequest(RequestLog log) {
        logger.info("Request: method={}, uri={}, clientId={}, duration={}ms",
            log.method(), log.uri(), log.clientId(), log.durationMs());
        
        // Store for analysis
        logs.add(log);
        
        // Clean old logs
        if (logs.size() > MAX_LOGS) {
            logs.remove(0);
        }
    }
    
    public String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
```

---

## Exercise 6: Pagination Implementation
**Objective**: Implement cursor-based and offset pagination.

### Task
Create pagination support:
1. Offset-based pagination
2. Cursor-based pagination
3. Navigation links

### Implementation
```java
public class PaginationUtil {
    
    public static <T> PagedResponse<T> paginate(List<T> items, int page, int size, long total) {
        int totalPages = (int) Math.ceil((double) total / size);
        
        return new PagedResponse<>(
            items,
            page,
            size,
            total,
            totalPages,
            page > 0,
            page < totalPages - 1
        );
    }
    
    public static <T> CursorResponse<T> cursorPaginate(List<T> items, String cursor, int limit) {
        return new CursorResponse<>(items, computeNextCursor(items), items.size() == limit);
    }
}
```

---

## Exercise 7: Error Response Standardization
**Objective**: Create consistent error responses.

### Task
Implement standardized error format:
1. RFC 7807 Problem Details
2. Error codes and messages
3. Validation error details

### Implementation
```java
public class ErrorResponse {
    
    private String type;
    private String title;
    private int status;
    private String detail;
    private Map<String, List<String>> errors;
    private String traceId;
    
    public static ErrorResponse notFound(String resource) {
        return new ErrorResponse(
            "https://api.example.com/errors/not-found",
            "Not Found",
            404,
            resource + " not found"
        );
    }
    
    public static ErrorResponse validation(Map<String, List<String>> errors) {
        return new ErrorResponse(
            "https://api.example.com/errors/validation",
            "Validation Error",
            400,
            "Invalid request",
            errors
        );
    }
}
```

---

## Exercise 8: API Documentation with OpenAPI
**Objective**: Generate API documentation.

### Task
Create OpenAPI specification:
1. Define endpoints and models
2. Add examples
3. Configure security schemes

### Implementation
```yaml
openapi: 3.0.3
info:
  title: User Management API
  version: 1.0.0
  description: API for managing users

paths:
  /users:
    get:
      summary: List users
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            default: 0
      responses:
        '200':
          description: Successful response
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/User'

components:
  schemas:
    User:
      type: object
      properties:
        id:
          type: integer
        name:
          type: string
        email:
          type: string
```

---

## Exercise 9: Async API Calls
**Objective**: Implement non-blocking API clients.

### Task
Create async HTTP client:
1. Use CompletableFuture
2. Implement retry logic
3. Handle backpressure

### Implementation
```java
public class AsyncApiClient {
    
    public CompletableFuture<Response<T>> getAsync(String url, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return executeWithRetry(url, responseType, 3);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }
    
    private <T> Response<T> executeWithRetry(String url, Class<T> type, int maxRetries) {
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts < maxRetries) {
            try {
                return execute(url, type);
            } catch (Exception e) {
                lastException = e;
                attempts++;
                if (attempts < maxRetries) {
                    sleep(calculateBackoff(attempts));
                }
            }
        }
        throw new RuntimeException("Failed after " + maxRetries + " attempts", lastException);
    }
}
```

---

## Exercise 10: API Gateway Pattern
**Objective**: Implement an API gateway.

### Task
Create an API gateway that:
1. Routes requests to backend services
2. Aggregates multiple API responses
3. Handles authentication
4. Implements rate limiting

### Implementation
```java
public class ApiGateway {
    
    private final Map<String, String> routes;
    private final RateLimiter rateLimiter;
    private final ResponseCache cache;
    
    public GatewayResponse handle(GatewayRequest request) {
        // Rate limiting
        if (!rateLimiter.allow(request.clientId())) {
            return new GatewayResponse(429, "Rate limit exceeded");
        }
        
        // Routing
        String backendUrl = routes.get(request.getService());
        if (backendUrl == null) {
            return new GatewayResponse(404, "Service not found");
        }
        
        // Caching check
        if (request.isGet()) {
            Optional<String> cached = cache.get(request.getCacheKey());
            if (cached.isPresent()) {
                return new GatewayResponse(200, cached.get(), true);
            }
        }
        
        // Forward request
        Response response = restClient.forward(backendUrl + request.getPath(), request);
        
        // Cache response
        if (response.isSuccessful()) {
            cache.put(request.getCacheKey(), response.getBody());
        }
        
        return new GatewayResponse(response.getStatus(), response.getBody());
    }
}
```

---

## Running Tests
```bash
# Run REST client demo
mvn exec:java -Dexec.mainClass="com.learning.Main"

# Run API gateway demo
mvn exec:java -Dexec.mainClass="com.learning.gateway.Main"
```