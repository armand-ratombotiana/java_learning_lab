# API Design - STEP BY STEP

## Creating a RESTful API

### Step 1: Define Resources
```
/products  — Product catalog
/orders    — Customer orders
/customers — Customer accounts
/reviews   — Product reviews
```

### Step 2: Create Controller
```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController { /* endpoints */ }
```

### Step 3: Implement GET
```java
@GetMapping
public ResponseEntity<List<ProductDTO>> getAll() {
    return ResponseEntity.ok(productService.findAll());
}

@GetMapping("/{id}")
public ResponseEntity<ProductDTO> getById(@PathVariable String id) {
    return ResponseEntity.ok(productService.findById(id));
}
```

### Step 4: Implement POST
```java
@PostMapping
public ResponseEntity<ProductDTO> create(@Valid @RequestBody CreateProductRequest req) {
    ProductDTO created = productService.create(req);
    return ResponseEntity.created(URI.create("/api/v1/products/" + created.id()))
        .body(created);
}
```

### Step 5: Add Validation
```java
public record CreateProductRequest(
    @NotBlank @Size(max = 255) String name,
    @Positive BigDecimal price,
    @PositiveOrZero int stockQuantity
) {}
```

### Step 6: Add Error Handling
```java
@RestControllerAdvice
public class ExceptionHandler { /* handle validation, not found, etc. */ }
```

### Step 7: Add Pagination
```java
@GetMapping
public ResponseEntity<PagedModel<ProductDTO>> list(
        @PageableDefault(size = 20) Pageable pageable) { /* ... */ }
```

### Step 8: Add Documentation
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
</dependency>
```
Access: `http://localhost:8080/swagger-ui.html`

## API Versioning Strategy

### Step 1: Define URL structure
```
/api/v1/products   — Initial version
/api/v2/products   — Breaking changes (e.g., new response format)
```

### Step 2: Maintain backward compatibility
```java
// V1 controller (deprecated)
@RestController
@RequestMapping("/api/v1/products")
public class ProductControllerV1 { /* ... */ }

// V2 controller
@RestController
@RequestMapping("/api/v2/products")
public class ProductControllerV2 { /* ... */ }
```
