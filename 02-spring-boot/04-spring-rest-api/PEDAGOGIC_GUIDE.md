# Spring REST API - Pedagogic Guide

## Learning Path

### Phase 1: REST Fundamentals (Day 1)
1. **REST Principles** - Resources, verbs, status codes
2. **@RestController** - REST endpoint annotation
3. **HTTP Methods** - GET, POST, PUT, DELETE
4. **Status Codes** - 200, 201, 400, 404, 500

### Phase 2: Request Handling (Day 2)
1. **@RequestBody / @ResponseBody** - JSON mapping
2. **@PathVariable** - URL parameters
3. **@RequestParam** - Query parameters
4. **@RequestHeader** - Headers

### Phase 3: Advanced REST (Day 3)
1. **@ControllerAdvice** - Global error handling
2. **Bean Validation** - @Valid, @NotNull, @Size
3. **HATEOAS** - Hypermedia links (optional)
4. **API Versioning** - URL path versioning

## Key Concepts

### REST Best Practices
- Use nouns for resources (`/users`)
- Use HTTP verbs appropriately
- Return proper status codes
- Version your API (`/api/v1/`)

### Error Response
```json
{
  "timestamp": "...",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found"
}
```

### Content Negotiation
- Accept: application/json (default)
- Accept: application/xml (configurable)
- Spring handles automatically

## Common Patterns

### CRUD Endpoints
```
GET    /resources        - List all
GET    /resources/{id}   - Get one
POST   /resources        - Create
PUT    /resources/{id}  - Update
DELETE /resources/{id}  - Delete
```

### Pagination
```java
@GetMapping
public Page<D> list(Pageable pageable) {
    return repo.findAll(pageable);
}
```

## Best Practices
- Document your API
- Version early
- Use DTOs for responses
- Validate input