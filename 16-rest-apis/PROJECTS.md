# REST APIs Module - PROJECTS.md

---

# Mini-Projects Overview

| Concept | Duration | Description |
|---------|----------|-------------|
| REST Controller | 2 hours | @RestController, @RequestMapping |
| Request/Response | 2 hours | @RequestBody, @ResponseBody, DTOs |
| Validation | 2 hours | @Valid, BindingResult, custom validators |
| HATEOAS | 2 hours | RepresentationModel, Link builders |
| Real-world: API Gateway | 8+ hours | Versioning, rate limiting, caching |

---

# Mini-Project: REST Client Implementation

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: REST Client, HTTP Methods, Request/Response Handling, Error Handling, JSON Parsing

This project implements a comprehensive REST client for consuming external APIs.

---

## Project Structure

```
16-rest-apis/src/main/java/com/learning/
├── Main.java
├── client/
│   ├── RestClient.java
│   ├── HttpClient.java
│   └── ApiClient.java
├── model/
│   ├── User.java
│   └── Post.java
├── request/
│   ├── HttpRequest.java
│   └── HttpResponse.java
└── handler/
    ├── ErrorHandler.java
    └── ResponseHandler.java
```

---

## Step 1: Request/Response Model

```java
// request/HttpRequest.java
package com.learning.request;

import java.net.http.*;
import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.HashMap;

public class HttpRequest {
    private String url;
    private HttpMethod method;
    private Map<String, String> headers;
    private String body;
    private Duration timeout;
    
    public enum HttpMethod {
        GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS
    }
    
    private HttpRequest(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers;
        this.body = builder.body;
        this.timeout = builder.timeout;
    }
    
    public String getUrl() { return url; }
    public HttpMethod getMethod() { return method; }
    public Map<String, String> getHeaders() { return headers; }
    public String getBody() { return body; }
    public Duration getTimeout() { return timeout; }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String url;
        private HttpMethod method = HttpMethod.GET;
        private Map<String, String> headers = new HashMap<>();
        private String body;
        private Duration timeout = Duration.ofSeconds(30);
        
        public Builder url(String url) {
            this.url = url;
            return this;
        }
        
        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }
        
        public Builder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }
        
        public Builder headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }
        
        public Builder body(String body) {
            this.body = body;
            return this;
        }
        
        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }
        
        public HttpRequest build() {
            return new HttpRequest(this);
        }
    }
}
```

```java
// request/HttpResponse.java
package com.learning.request;

import java.net.http.*;
import java.util.Map;

public class HttpResponse<T> {
    private final int statusCode;
    private final String statusMessage;
    private final Map<String, String> headers;
    private final T body;
    private final boolean successful;
    
    public HttpResponse(int statusCode, String statusMessage, 
                     Map<String, String> headers, T body) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = headers;
        this.body = body;
        this.successful = statusCode >= 200 && statusCode < 300;
    }
    
    public int getStatusCode() { return statusCode; }
    public String getStatusMessage() { return statusMessage; }
    public Map<String, String> getHeaders() { return headers; }
    public T getBody() { return body; }
    public boolean isSuccessful() { return successful; }
    
    public static <T> HttpResponse<T> error(int statusCode, String message) {
        return new HttpResponse<>(statusCode, message, Map.of(), null);
    }
}
```

---

## Step 2: REST Client Implementation

```java
// client/RestClient.java
package com.learning.client;

import com.learning.request.*;
import java.net.http.*;
import java.net.URI;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class RestClient {
    private final HttpClient httpClient;
    private final String baseUrl;
    private Map<String, String> defaultHeaders;
    
    public RestClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        this.defaultHeaders = new HashMap<>();
        defaultHeaders.put("Content-Type", "application/json");
        defaultHeaders.put("Accept", "application/json");
    }
    
    public RestClient defaultHeader(String key, String value) {
        defaultHeaders.put(key, value);
        return this;
    }
    
    public <T> HttpResponse<T> get(String endpoint, 
                                 Class<T> responseType) {
        return execute(buildRequest(endpoint, HttpRequest.HttpMethod.GET), 
            responseType);
    }
    
    public <T, R> HttpResponse<R> post(String endpoint, T body,
                                     Class<R> responseType) {
        HttpRequest request = buildRequest(endpoint, HttpRequest.HttpMethod.POST)
            .body(toJson(body));
        return execute(request, responseType);
    }
    
    public <T, R> HttpResponse<R> put(String endpoint, T body,
                                    Class<R> responseType) {
        HttpRequest request = buildRequest(endpoint, HttpRequest.HttpMethod.PUT)
            .body(toJson(body));
        return execute(request, responseType);
    }
    
    public <R> HttpResponse<R> delete(String endpoint, 
                                   Class<R> responseType) {
        return execute(buildRequest(endpoint, HttpRequest.HttpMethod.DELETE), 
            responseType);
    }
    
    private HttpRequest buildRequest(String endpoint, 
                                   HttpRequest.HttpMethod method) {
        return HttpRequest.builder()
            .url(baseUrl + endpoint)
            .method(method)
            .headers(defaultHeaders)
            .timeout(Duration.ofSeconds(30))
            .build();
    }
    
    private <T> HttpResponse<T> execute(HttpRequest request,
                                         Class<T> responseType) {
        try {
            HttpClient.Builder clientBuilder = HttpClient.newBuilder()
                .connectTimeout(request.getTimeout());
            
            java.net.http.HttpRequest.Builder builder = 
                java.net.http.HttpRequest.newBuilder()
                .uri(URI.create(request.getUrl()))
                .timeout(request.getTimeout());
            
            request.getHeaders().forEach(builder::header);
            
            switch (request.getMethod()) {
                case GET -> builder.GET();
                case POST -> builder.POST(
                    java.net.http.HttpRequest.BodyPublishers.ofString(
                        request.getBody() != null ? request.getBody() : ""));
                case PUT -> builder.PUT(
                    java.net.http.HttpRequest.BodyPublishers.ofString(
                        request.getBody() != null ? request.getBody() : ""));
                case DELETE -> builder.DELETE();
                case PATCH -> builder.method("PATCH",
                    java.net.http.HttpRequest.BodyPublishers.ofString(
                        request.getBody() != null ? request.getBody() : ""));
            }
            
            java.net.http.HttpRequest httpRequest = builder.build();
            HttpResponse<T> response = httpClient.send(httpRequest, 
                new BodyHandler<>(responseType));
            
            return response;
            
        } catch (Exception e) {
            return HttpResponse.error(500, e.getMessage());
        }
    }
    
    private <T> String toJson(T object) {
        try {
            return new com.google.gson.Gson().toJson(object);
        } catch (Exception e) {
            return object.toString();
        }
    }
    
    private static class BodyHandler<T> implements 
            HttpResponse.BodyHandler<HttpResponse<T>> {
        private final Class<T> responseType;
        
        public BodyHandler(Class<T> responseType) {
            this.responseType = responseType;
        }
        
        @Override
        public HttpResponse<T> apply(
                java.net.http.HttpResponse.ResponseInfo responseInfo) {
            int statusCode = responseInfo.statusCode();
            String statusMessage = getStatusMessage(statusCode);
            
            Map<String, String> headers = responseInfo.headers()
                .map().entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> e.getValue().get(0)));
            
            T body = null;
            if (responseType != void.class) {
                try {
                    if (responseInfo.body() != null && 
                        responseType != String.class) {
                        body = parseResponseJson(
                            responseInfo.body(), responseType);
                    }
                } catch (Exception e) {
                    // Handle parsing error
                }
            }
            
            return new HttpResponse<>(statusCode, statusMessage, 
                headers, body);
        }
        
        private String getStatusMessage(int code) {
            return switch (code) {
                case 200 -> "OK";
                case 201 -> "Created";
                case 204 -> "No Content";
                case 400 -> "Bad Request";
                case 401 -> "Unauthorized";
                case 403 -> "Forbidden";
                case 404 -> "Not Found";
                case 500 -> "Internal Server Error";
                default -> "Unknown";
            };
        }
        
        private <T> T parseResponseJson(String json, Class<T> type) {
            try {
                return new com.google.gson.Gson().fromJson(json, type);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
```

---

## Step 3: API Client

```java
// client/ApiClient.java
package com.learning.client;

import com.learning.model.*;
import com.learning.request.*;
import java.util.List;

public class ApiClient {
    private final RestClient restClient;
    
    public ApiClient(String baseUrl) {
        this.restClient = new RestClient(baseUrl);
    }
    
    public List<User> getUsers() {
        HttpResponse<User[]> response = restClient.get(
            "/users", User[].class);
        
        if (response.isSuccessful() && response.getBody() != null) {
            return List.of(response.getBody());
        }
        return List.of();
    }
    
    public User getUser(Long id) {
        HttpResponse<User> response = restClient.get(
            "/users/" + id, User.class);
        
        if (response.isSuccessful()) {
            return response.getBody();
        }
        return null;
    }
    
    public User createUser(User user) {
        HttpResponse<User> response = restClient.post(
            "/users", user, User.class);
        
        if (response.isSuccessful()) {
            return response.getBody();
        }
        return null;
    }
    
    public User updateUser(Long id, User user) {
        HttpResponse<User> response = restClient.put(
            "/users/" + id, user, User.class);
        
        if (response.isSuccessful()) {
            return response.getBody();
        }
        return null;
    }
    
    public boolean deleteUser(Long id) {
        HttpResponse<Void> response = restClient.delete(
            "/users/" + id, Void.class);
        
        return response.isSuccessful();
    }
    
    public List<Post> getUserPosts(Long userId) {
        HttpResponse<Post[]> response = restClient.get(
            "/users/" + userId + "/posts", Post[].class);
        
        if (response.isSuccessful() && response.getBody() != null) {
            return List.of(response.getBody());
        }
        return List.of();
    }
}
```

---

## Step 4: Model Classes

```java
// model/User.java
package com.learning.model;

public class User {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String website;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { 
        this.username = username; 
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    
    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}
```

```java
// model/Post.java
package com.learning.model;

public class Post {
    private Long id;
    private Long userId;
    private String title;
    private String body;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    
    @Override
    public String toString() {
        return "Post{id=" + id + ", title='" + title + "'}";
    }
}
```

---

## Step 5: Main Application

```java
// Main.java
package com.learning;

import com.learning.client.*;
import com.learning.model.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        ApiClient client = new ApiClient("https://jsonplaceholder.typicode.com");
        
        testGetUsers(client);
        testGetUser(client);
        testCreateUser(client);
    }
    
    private static void testGetUsers(ApiClient client) {
        System.out.println("=== GET Users ===");
        
        List<User> users = client.getUsers();
        users.forEach(System.out::println);
    }
    
    private static void testGetUser(ApiClient client) {
        System.out.println("\n=== GET User ===");
        
        User user = client.getUser(1L);
        if (user != null) {
            System.out.println(user);
        } else {
            System.out.println("User not found");
        }
    }
    
    private static void testCreateUser(ApiClient client) {
        System.out.println("\n=== CREATE User ===");
        
        User newUser = new User();
        newUser.setName("John Doe");
        newUser.setUsername("johndoe");
        newUser.setEmail("john@example.com");
        
        User created = client.createUser(newUser);
        if (created != null) {
            System.out.println("Created: " + created);
        } else {
            System.out.println("Failed to create user");
        }
    }
}
```

---

# Real-World Project: API Versioning System

## Project Overview

**Duration**: 8+ hours  
**Difficulty**: Advanced  
**Concepts Used**: API Versioning Strategies, Rate Limiting, Request/Response Logging, Authentication, Caching

This project implements a complete API gateway with versioning, rate limiting, and monitoring.

---

## Project Structure

```
16-rest-apis/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── gateway/
│   │   ├── ApiGateway.java
│   │   ├── RateLimiter.java
│   │   └── VersionRouter.java
│   ├── versioning/
│   │   ├── Version Negotiation/
│   │   ├── UriVersioning.java
│   │   ├── HeaderVersioning.java
│   │   └── QueryParamVersioning.java
│   ├── security/
│   │   ├── ApiKeyAuth.java
│   │   ├── BearerTokenAuth.java
│   │   └── AuthenticationFilter.java
│   ├── logging/
│   │   └── RequestLoggingFilter.java
│   └── cache/
│       └── ResponseCache.java
└── src/main/resources/
    └── config.properties
```

---

## POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>rest-apis</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.10.1</version>
        </dependency>
    </dependencies>
</project>
```

---

## Versioning Strategies

```java
// versioning/UriVersioning.java
package com.learning.versioning;

public interface VersionStrategy {
    String getVersion();
    boolean supports(String version);
}

public class UriVersioning implements VersionStrategy {
    private final String defaultVersion = "v1";
    
    @Override
    public String getVersion() {
        return defaultVersion;
    }
    
    @Override
    public boolean supports(String version) {
        return version != null && version.startsWith("v");
    }
}
```

```java
// versioning/HeaderVersioning.java
package com.learning.versioning;

import java.util.Map;

public class HeaderVersioning implements VersionStrategy {
    private static final String HEADER_NAME = "X-API-Version";
    private final String defaultVersion = "v1";
    
    @Override
    public String getVersion() {
        return defaultVersion;
    }
    
    @Override
    public boolean supports(String version) {
        return version != null && version.startsWith("v");
    }
    
    public static String extractFromHeaders(Map<String, String> headers) {
        return headers.get(HEADER_NAME);
    }
}
```

```java
// versioning/QueryParamVersioning.java
package com.learning.versioning;

import java.util.Map;

public class QueryParamVersioning implements VersionStrategy {
    private static final String PARAM_NAME = "version";
    private final String defaultVersion = "v1";
    
    @Override
    public String getVersion() {
        return defaultVersion;
    }
    
    @Override
    public boolean supports(String version) {
        return version != null && version.startsWith("v");
    }
    
    public static String extractFromParams(Map<String, String> params) {
        return params.get(PARAM_NAME);
    }
}
```

---

## Rate Limiter

```java
// gateway/RateLimiter.java
package com.learning.gateway;

import java.time.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;

public class RateLimiter {
    private final int maxRequests;
    private final Duration window;
    private final Map<String, RateLimitBucket> buckets;
    
    public RateLimiter(int maxRequests, Duration window) {
        this.maxRequests = maxRequests;
        this.window = window;
        this.buckets = new ConcurrentHashMap<>();
    }
    
    public boolean allowRequest(String clientId) {
        RateLimitBucket bucket = buckets.computeIfAbsent(clientId, 
            k -> new RateLimitBucket(window));
        
        return bucket.tryConsume();
    }
    
    public RateLimitInfo getRateLimitInfo(String clientId) {
        RateLimitBucket bucket = buckets.get(clientId);
        if (bucket == null) {
            return new RateLimitInfo(maxRequests, maxRequests, 
                window.getSeconds());
        }
        
        return new RateLimitInfo(
            bucket.getConsumedRequests(),
            bucket.getRemainingRequests(),
            bucket.getTimeToReset()
        );
    }
    
    public static class RateLimitBucket {
        private final int maxRequests;
        private final Duration window;
        private final AtomicInteger consumed;
        private Instant windowStart;
        
        public RateLimitBucket(Duration window) {
            this.maxRequests = 100;
            this.window = window;
            this.consumed = new AtomicInteger(0);
            this.windowStart = Instant.now();
        }
        
        public boolean tryConsume() {
            if (isWindowExpired()) {
                reset();
            }
            
            if (consumed.get() < maxRequests) {
                consumed.incrementAndGet();
                return true;
            }
            
            return false;
        }
        
        public int getConsumedRequests() {
            return consumed.get();
        }
        
        public int getRemainingRequests() {
            return maxRequests - consumed.get();
        }
        
        public long getTimeToReset() {
            Duration elapsed = Duration.between(windowStart, Instant.now());
            return Math.max(0, window.getSeconds() - elapsed.getSeconds());
        }
        
        private boolean isWindowExpired() {
            return Duration.between(windowStart, Instant.now()).compareTo(window) > 0;
        }
        
        private void reset() {
            consumed.set(0);
            windowStart = Instant.now();
        }
    }
    
    public record RateLimitInfo(
        int consumed,
        int remaining,
        long resetSeconds
    ) {}
}
```

---

## Request Logging Filter

```java
// logging/RequestLoggingFilter.java
package com.learning.logging;

import java.time.*;
import java.util.*;

public class RequestLoggingFilter {
    private final List<RequestLog> logs;
    private final int maxLogs;
    
    public RequestLoggingFilter(int maxLogs) {
        this.maxLogs = maxLogs;
        this.logs = Collections.synchronizedList(new ArrayList<>());
    }
    
    public void logRequest(RequestLog log) {
        logs.add(log);
        
        if (logs.size() > maxLogs) {
            logs.remove(0);
        }
    }
    
    public List<RequestLog> getLogs() {
        return new ArrayList<>(logs);
    }
    
    public List<RequestLog> getLogsByClient(String clientId) {
        return logs.stream()
            .filter(log -> log.clientId().equals(clientId))
            .toList();
    }
    
    public Map<String, Long> getRequestCountsByClient() {
        return logs.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                RequestLog::clientId,
                java.util.stream.Collectors.counting()));
    }
    
    public record RequestLog(
        String id,
        String clientId,
        String method,
        String uri,
        int statusCode,
        Duration duration,
        Instant timestamp
    ) {}
}
```

---

## Response Cache

```java
// cache/ResponseCache.java
package com.learning.cache;

import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseCache<K, V> {
    private final Map<K, CacheEntry<V>> cache;
    private final Duration defaultTtl;
    
    public ResponseCache(Duration defaultTtl) {
        this.cache = new ConcurrentHashMap<>();
        this.defaultTtl = defaultTtl;
    }
    
    public void put(K key, V value) {
        put(key, value, defaultTtl);
    }
    
    public void put(K key, V value, Duration ttl) {
        cache.put(key, new CacheEntry<>(value, 
            Instant.now().plus(ttl)));
    }
    
    public Optional<V> get(K key) {
        CacheEntry<V> entry = cache.get(key);
        
        if (entry == null) {
            return Optional.empty();
        }
        
        if (entry.isExpired()) {
            cache.remove(key);
            return Optional.empty();
        }
        
        return Optional.of(entry.value());
    }
    
    public void invalidate(K key) {
        cache.remove(key);
    }
    
    public void clear() {
        cache.clear();
    }
    
    public int size() {
        return cache.size();
    }
    
    private record CacheEntry<V>(V value, Instant expiresAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
```

---

## API Gateway

```java
// gateway/ApiGateway.java
package com.learning.gateway;

import com.learning.versioning.*;
import com.learning.logging.*;
import com.learning.cache.*;
import java.time.*;
import java.util.*;

public class ApiGateway {
    private final RestClient restClient;
    private final RateLimiter rateLimiter;
    private final VersionStrategy versionStrategy;
    private final RequestLoggingFilter loggingFilter;
    private final ResponseCache<String, String> responseCache;
    
    public ApiGateway(String baseUrl) {
        this.restClient = new RestClient(baseUrl);
        this.rateLimiter = new RateLimiter(100, Duration.ofMinutes(1));
        this.versionStrategy = new UriVersioning();
        this.loggingFilter = new RequestLoggingFilter(1000);
        this.responseCache = new ResponseCache<>(Duration.ofMinutes(5));
    }
    
    public GatewayResponse processRequest(GatewayRequest request) {
        if (!rateLimiter.allowRequest(request.clientId())) {
            return new GatewayResponse(429, "Too Many Requests", 
                Map.of("X-RateLimit-Remaining", "0"));
        }
        
        LoggingFilter.RequestLog log = new LoggingFilter.RequestLog(
            UUID.randomUUID().toString(),
            request.clientId(),
            request.method(),
            request.uri(),
            0,
            Duration.ZERO,
            Instant.now()
        );
        
        loggingFilter.logRequest(log);
        
        String cacheKey = request.uri();
        Optional<String> cached = responseCache.get(cacheKey);
        
        if (cached.isPresent()) {
            return new GatewayResponse(200, cached.get(), Map.of());
        }
        
        String version = versionStrategy.getVersion();
        com.learning.request.HttpResponse<String> response = 
            restClient.get(version + "/" + request.uri(), String.class);
        
        if (response.isSuccessful()) {
            responseCache.put(cacheKey, response.getBody());
        }
        
        return new GatewayResponse(
            response.getStatusCode(),
            response.getBody(),
            Map.of("X-API-Version", version)
        );
    }
    
    public GatewayResponse handleRequest(GatewayRequest request) {
        return processRequest(request);
    }
    
    public static class GatewayRequest {
        private final String clientId;
        private final String method;
        private final String uri;
        
        public GatewayRequest(String clientId, String method, String uri) {
            this.clientId = clientId;
            this.method = method;
            this.uri = uri;
        }
        
        public String clientId() { return clientId; }
        public String method() { return method; }
        public String uri() { return uri; }
    }
    
    public static class GatewayResponse {
        private final int statusCode;
        private final String body;
        private final Map<String, String> headers;
        
        public GatewayResponse(int statusCode, String body, 
                           Map<String, String> headers) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = headers;
        }
        
        public int statusCode() { return statusCode; }
        public String body() { return body; }
        public Map<String, String> headers() { return headers; }
    }
}
```

---

## Main Application

```java
// Main.java
package com.learning;

import com.learning.gateway.*;
import com.learning.logging.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        ApiGateway gateway = new ApiGateway("https://api.example.com");
        
        testRateLimiting(gateway);
        testCaching(gateway);
        testLogging();
    }
    
    private static void testRateLimiting(ApiGateway gateway) {
        System.out.println("=== Rate Limiting ===");
        
        for (int i = 0; i < 105; i++) {
            var request = new ApiGateway.GatewayRequest(
                "client-1", "GET", "/users");
            
            var response = gateway.handleRequest(request);
            System.out.println("Request " + (i + 1) + ": " + 
                response.statusCode());
        }
    }
    
    private static void testCaching(ApiGateway gateway) {
        System.out.println("\n=== Caching ===");
        
        var request = new ApiGateway.GatewayRequest(
            "client-2", "GET", "/users/1");
        
        var response1 = gateway.handleRequest(request);
        System.out.println("First request: " + response1.statusCode());
        
        var response2 = gateway.handleRequest(request);
        System.out.println("Cached request: " + response2.statusCode());
    }
    
    private static void testLogging() {
        System.out.println("\n=== Request Logging ===");
        
        LoggingFilter filter = new LoggingFilter(1000);
        
        for (int i = 0; i < 5; i++) {
            filter.logRequest(new LoggingFilter.RequestLog(
                UUID.randomUUID().toString(),
                "client-" + (i % 3),
                "GET",
                "/api/users",
                200,
                java.time.Duration.ofMillis(100),
                java.time.Instant.now()
            ));
        }
        
        Map<String, Long> counts = filter.getRequestCountsByClient();
        System.out.println("Requests by client: " + counts);
    }
}
```

---

# Mini-Project: HATEOAS REST API

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: HATEOAS Links, Resource Representation, HAL Format, Link Builders

This mini-project implements HATEOAS (Hypermedia as the Engine of Application State) for a RESTful API with hypermedia links.

---

## Step 1: POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>hateoas-api</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-hateoas</artifactId>
        </dependency>
    </dependencies>
</project>
```

---

## Step 2: Model with HATEOAS

```java
// model/Product.java
package com.learning.model;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.Link;
import java.math.BigDecimal;

public class Product extends RepresentationModel<Product> {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    
    public Product() {}
    
    public Product(Long id, String name, String description, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
```

---

## Step 3: HATEOAS Controller

```java
// controller/ProductHateoasController.java
package com.learning.controller;

import com.learning.model.Product;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/hateoas/products")
public class ProductHateoasController {
    
    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    
    public ProductHateoasController() {
        Product p1 = new Product(1L, "Laptop", "High-perf laptop", new BigDecimal("1299.99"));
        Product p2 = new Product(2L, "Phone", "Smartphone", new BigDecimal("899.99"));
        
        addLinks(p1);
        addLinks(p2);
        
        products.put(1L, p1);
        products.put(2L, p2);
    }
    
    private void addLinks(Product product) {
        product.add(Link.of("/api/hateoas/products/" + product.getId(), "self"));
        product.add(Link.of("/api/hateoas/products", "products"));
        product.add(WebMvcLinkBuilder.linkTo(
            ProductHateoasController.class
        ).slash(product.getId()).withSelfRel());
    }
    
    @GetMapping
    public ResponseEntity<CollectionModel<Product>> getAllProducts() {
        CollectionModel<Product> resources = CollectionModel.of(products.values());
        resources.add(Link.of("/api/hateoas/products", "self"));
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Product>> getProduct(@PathVariable Long id) {
        Product product = products.get(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        
        EntityModel<Product> resource = EntityModel.of(product);
        resource.add(WebMvcLinkBuilder.linkTo(
            methodOn(ProductHateoasController.class).getAllProducts()
        ).withRel("all-products"));
        
        return ResponseEntity.ok(resource);
    }
    
    @PostMapping
    public ResponseEntity<EntityModel<Product>> createProduct(@RequestBody Product product) {
        Long id = products.size() + 1L;
        product.setId(id);
        addLinks(product);
        products.put(id, product);
        
        EntityModel<Product> resource = EntityModel.of(product);
        resource.add(Link.of("/api/hateoas/products/" + id, "self"));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }
}
```

---

## Step 4: Application

```java
// Application.java
package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

---

## Test HATEOAS Response

```bash
curl http://localhost:8080/api/hateoas/products/1

# Response includes:
# {
#   "id": 1,
#   "name": "Laptop",
#   "price": 1299.99,
#   "_links": {
#     "self": { "href": "http://localhost:8080/api/hateoas/products/1" },
#     "products": { "href": "http://localhost:8080/api/hateoas/products" }
#   }
# }
```

---

## Build Instructions

```bash
cd 16-rest-apis
mvn spring-boot:run
curl http://localhost:8080/api/hateoas/products
```

---

## Build Instructions

```bash
cd 16-rest-apis
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

This project demonstrates comprehensive REST API client implementation with versioning strategies, rate limiting, caching, and request logging.