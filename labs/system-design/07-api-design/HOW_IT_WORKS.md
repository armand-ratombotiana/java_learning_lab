# API Design - HOW IT WORKS

## RESTful API Design

### Resource Naming
```java
// Resources are nouns, plural
GET    /api/products                    // List products
GET    /api/products/{id}               // Get single product
POST   /api/products                    // Create product
PUT    /api/products/{id}               // Replace product
PATCH  /api/products/{id}               // Partial update
DELETE /api/products/{id}               // Delete product

// Sub-resources
GET    /api/products/{id}/reviews       // List product reviews
POST   /api/products/{id}/reviews       // Create product review

// Actions (rare) — use verbs only for non-CRUD
POST   /api/orders/{id}/cancel          // Cancel order
POST   /api/orders/{id}/refund          // Refund order
```

### Request/Response Structure
```json
// Request
POST /api/orders
{
    "customerId": "c123",
    "items": [
        { "productId": "p456", "quantity": 2 }
    ]
}

// Response (201 Created)
{
    "id": "ord789",
    "customerId": "c123",
    "status": "PENDING",
    "total": 59.98,
    "createdAt": "2025-01-15T10:30:00Z"
}
```

## Error Handling

### Standard Error Response
```java
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiError("PRODUCT_NOT_FOUND",
                "Product with ID " + ex.getProductId() + " not found",
                "/api/products/" + ex.getProductId()));
    }
}

public record ApiError(
    String code,       // Machine-readable error code
    String message,    // Human-readable message
    String instance    // URI that caused the error
) {}
```

## Pagination

### Cursor-Based Pagination
```java
@GetMapping("/api/products")
public ResponseEntity<PageResponse<Product>> listProducts(
        @RequestParam(defaultValue = "20") int limit,
        @RequestParam(required = false) String cursor) {

    List<Product> products = productService.findPage(limit, cursor);
    String nextCursor = products.size() == limit
        ? products.get(products.size() - 1).getId()
        : null;

    return ResponseEntity.ok()
        .header("X-Total-Count", String.valueOf(productService.count()))
        .body(new PageResponse<>(products, nextCursor));
}

public record PageResponse<T>(
    List<T> data,
    String nextCursor,
    boolean hasMore
) {}
```

## API Versioning

### URL Path Versioning
```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductControllerV1 { /* ... */ }

@RestController
@RequestMapping("/api/v2/products")
public class ProductControllerV2 { /* enhanced version */ }
```

## Rate Limiting

```java
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimiter rateLimiter;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain) {
        String clientId = getClientId(request);
        if (!rateLimiter.allowRequest(clientId)) {
            response.setStatus(429);
            response.setHeader("Retry-After", "60");
            response.getWriter().write("Rate limit exceeded");
            return;
        }
        chain.doFilter(request, response);
    }
}
```
