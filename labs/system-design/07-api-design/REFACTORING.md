# API Design - REFACTORING

## From Monolithic Controller to Resource Controllers

### Before: God controller
```java
@RestController
@RequestMapping("/api")
public class AllController {
    @GetMapping("/products") { /* ... */ }
    @PostMapping("/products") { /* ... */ }
    @GetMapping("/orders") { /* ... */ }
    @PostMapping("/orders") { /* ... */ }
    @GetMapping("/users") { /* ... */ }
    // ... 50+ endpoints
}
```

### After: Resource-specific controllers
```java
@RestController
@RequestMapping("/api/products")
public class ProductController { /* product endpoints */ }

@RestController
@RequestMapping("/api/orders")
public class OrderController { /* order endpoints */ }

@RestController
@RequestMapping("/api/users")
public class UserController { /* user endpoints */ }
```

## From No Validation to Proper Validation

### Before: Manual validation everywhere
```java
@PostMapping("/products")
public Product create(@RequestBody Product product) {
    if (product.getName() == null || product.getName().isEmpty()) {
        throw new BadRequestException("Name required");
    }
    if (product.getPrice() == null || product.getPrice() < 0) {
        throw new BadRequestException("Invalid price");
    }
    // ... more manual checks
}
```

### After: Declarative validation
```java
public record CreateProductRequest(
    @NotBlank String name,
    @Positive BigDecimal price,
    @Size(max = 1000) String description
) {}

@PostMapping("/products")
public Product create(@Valid @RequestBody CreateProductRequest req) {
    return productService.create(req);
}
```

## From No Pagination to Paginated

### Before: Unbounded
```java
@GetMapping("/products")
public List<Product> getAll() {
    return productService.findAll();  // returns everything
}
```

### After: Paginated
```java
@GetMapping("/products")
public ResponseEntity<PagedModel<Product>> getAll(
        @PageableDefault(size = 20) Pageable pageable) {
    Page<Product> page = productService.findAll(pageable);
    return ResponseEntity.ok(toPagedModel(page));
}
```

## Performance Improvement

| Refactoring | Before | After |
|------------|--------|-------|
| No pagination | 500ms for 100K rows | 10ms for 20 rows |
| No validation | 500 errors caught in production | Caught at edge |
| Chatty API | 3-5 requests per page | 1 request per page |
| No caching | 50ms per request | 1ms per request (cached) |
| No error handling | Stack traces leaked | Consistent error responses |
