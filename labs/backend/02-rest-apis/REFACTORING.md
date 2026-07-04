# Refactoring REST APIs

## Extract Global Error Handling
`java
// Before: Duplicate error handling in each controller
@GetMapping
public ResponseEntity<?> get() {
    try { ... } catch (Exception e) { return badRequest(); }
}

// After: Global handler
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handle(ValidationException e) { ... }
}
`

## Use DTOs and MapStruct
`java
// Before: Entity exposed directly
public User entity; // JPA annotations visible, lazy loading issues

// After: Clean DTOs
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User user);
    User toEntity(UserDTO dto);
}
`

## Version Your API
`java
// Before: No versioning, breaking changes cause issues
@RequestMapping("/api/users")

// After: Versioned, old clients continue working
@RequestMapping("/api/v1/users")
// and
@RequestMapping("/api/v2/users")
`

## Add Pagination
`java
// Before: Returning all results (memory issues)
@GetMapping
public List<User> getAll() { return repo.findAll(); }

// After: Paginated
@GetMapping
public Page<UserDTO> getAll(@PageableDefault Pageable pageable) { ... }
`
"@

New-LabFile "02-rest-apis" "PERFORMANCE.md" @"
# Performance of REST APIs

## Response Compression
`properties
server.compression.enabled=true
server.compression.mime-types=application/json,application/xml,text/plain
`

## Caching Headers
`java
@GetMapping("/{id}")
public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
    return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
        .eTag(calculateHash(user))
        .body(user);
}
`

## JSON Serialization Optimizations
`properties
# Jackson tuning
spring.jackson.serialization.write-dates-as-timestamps=true
spring.jackson.default-property-inclusion=non_null
spring.jackson.serialization.fail-on-empty-beans=false
`

## Connection Pooling
`properties
server.tomcat.max-threads=200
server.tomcat.max-connections=10000
server.tomcat.accept-count=100
server.tomcat.connection-timeout=5000
`

## Batch Endpoints
`java
// Instead of N individual POST calls
@PostMapping("/batch")
public ResponseEntity<List<UserDTO>> createBatch(
        @Valid @RequestBody List<CreateUserRequest> users) {
    List<UserDTO> created = userService.createAll(users);
    return ResponseEntity.created(...).body(created);
}
`
"@

New-LabFile "02-rest-apis" "SECURITY.md" @"
# Security for REST APIs

## CORS Configuration
`java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("https://myapp.com", "http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
`

## JWT Authentication
`java
@PostMapping("/auth/login")
public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
    String token = jwtService.generateToken(request.getUsername());
    return ResponseEntity.ok(new JwtResponse(token));
}

// Protected endpoints require Bearer token header
// Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
`

## Input Validation
`java
public class CreateUserRequest {
    @NotBlank @Email private String email;
    @Size(min = 8, max = 100) private String password;
    @Pattern(regexp = "^[a-zA-Z ]+$") private String name;
}
`

## API Key Authentication
`java
// Check X-API-Key header in filter
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
`
"@

New-LabFile "02-rest-apis" "ARCHITECTURE.md" @"
# REST API Architecture

## Standard Layered Architecture
`
+--------------------------+
|   Controller Layer       |  @RestController
+--------------------------+
|    Service Layer         |  @Service (business logic)
+--------------------------+
|  Repository Layer        |  @Repository (data access)
+--------------------------+
|      Domain Layer        |  Entities, Value Objects
+--------------------------+
`

## DTO Pattern
`
Controller <-> Service: DTO objects
Service <-> Repository: Domain entities
Controller -> Response: DTO objects serialized to JSON
`

## API Gateway Pattern
`
Client -> API Gateway -> User Service
                      -> Order Service
                      -> Payment Service
`

## Versioning Strategies
1. URL Path: /api/v1/users, /api/v2/users
2. Request Header: Accept: application/vnd.myapp.v1+json
3. Query Parameter: /api/users?version=1

## Pagination Pattern
`json
GET /api/users?page=0&size=20
Response:
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5
}
`
"@

New-LabFile "02-rest-apis" "EXERCISES.md" @"
# REST API Exercises

## Exercise 1: CRUD API for Products
Build a REST API for products with:
- GET /api/products (paginated, default 20 per page)
- GET /api/products/{id}
- POST /api/products (with validation)
- PUT /api/products/{id}
- DELETE /api/products/{id}
- Use @ControllerAdvice for error handling
- Return proper HTTP status codes

## Exercise 2: Search and Filter
Add search and filter capabilities:
- GET /api/products?category=electronics
- GET /api/products/search?q=keyword
- GET /api/products?sort=price,desc
- GET /api/products?page=0&size=10&sort=name,asc

## Exercise 3: HATEOAS
Add hypermedia links to responses:
`json
{
  "id": 1,
  "name": "Product",
  "_links": {
    "self": { "href": "/api/products/1" },
    "category": { "href": "/api/categories/5" }
  }
}
`

## Exercise 4: API Versioning
Implement both URL-based and header-based versioning.

## Exercise 5: Async REST
Use @Async with CompletableFuture for long-running operations.
