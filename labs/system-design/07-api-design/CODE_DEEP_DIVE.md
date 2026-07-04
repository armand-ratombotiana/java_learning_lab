# API Design - CODE DEEP DIVE

## Table of Contents
1. [RESTful API with Spring Boot](#rest-api)
2. [GraphQL API with Spring](#graphql)
3. [gRPC Service in Java](#grpc)
4. [API Gateway Pattern](#gateway)

---

## 1. RESTful API with Spring Boot <a name="rest-api"></a>

### Controller Implementation
```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<ProductDTO>> listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "createdAt") String sort,
            @RequestParam(required = false, defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(page, size,
            Sort.by(Sort.Direction.fromString(direction), sort));

        Page<ProductDTO> result = category != null
            ? productService.findByCategory(category, pageable)
            : productService.findAll(pageable);

        return ResponseEntity.ok(toPagedModel(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        ProductDTO created = productService.create(request);
        return ResponseEntity
            .created(URI.create("/api/v1/products/" + created.id()))
            .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @PatchMapping("/{id}/price")
    public ResponseEntity<ProductDTO> updatePrice(
            @PathVariable String id,
            @RequestBody @Valid PriceUpdateRequest request) {
        return ResponseEntity.ok(productService.updatePrice(id, request.newPrice()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Request/Response DTOs
```java
public record CreateProductRequest(
    @NotBlank String name,
    @NotBlank String description,
    @Positive BigDecimal price,
    @PositiveOrZero int stockQuantity,
    String category
) {}

public record UpdateProductRequest(
    @NotBlank String name,
    @NotBlank String description,
    @Positive BigDecimal price,
    String category
) {}

public record ProductDTO(
    String id,
    String name,
    String description,
    BigDecimal price,
    int stockQuantity,
    String category,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

### Comprehensive Error Handling
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.groupingBy(
                FieldError::getField,
                Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
            ));

        return ResponseEntity.badRequest()
            .body(new ValidationErrorResponse("VALIDATION_ERROR",
                "Request validation failed", errors));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiError("NOT_FOUND", ex.getMessage(),
                "/api/v1/products/" + ex.getProductId()));
    }

    @ExceptionHandler(ProductConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ProductConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ApiError("CONFLICT", ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiError("INTERNAL_ERROR", "An unexpected error occurred", null));
    }
}

public record ValidationErrorResponse(
    String code,
    String message,
    Map<String, List<String>> errors
) {}

public record ApiError(
    String code,
    String message,
    String instance
) {}
```

### HATEOAS Links
```java
@GetMapping("/{id}")
public ResponseEntity<EntityModel<ProductDTO>> getProduct(@PathVariable String id) {
    ProductDTO product = productService.findById(id);

    EntityModel<ProductDTO> model = EntityModel.of(product,
        linkTo(methodOn(ProductController.class).getProduct(id)).withSelfRel(),
        linkTo(methodOn(ProductController.class).listProducts(0, 20, null, "createdAt", "desc"))
            .withRel("products"),
        linkTo(methodOn(ProductController.class).updateProduct(id, null))
            .withRel("update").withType("PUT"),
        linkTo(methodOn(ProductController.class).deleteProduct(id))
            .withRel("delete").withType("DELETE")
    );

    return ResponseEntity.ok(model);
}
```

### Request Validation
```java
@Component
public class ProductValidator {
    public void validateCreate(CreateProductRequest request) {
        List<String> errors = new ArrayList<>();
        if (request.name() != null && request.name().length() > 255) {
            errors.add("Product name must not exceed 255 characters");
        }
        if (request.price() != null && request.price().compareTo(new BigDecimal("99999.99")) > 0) {
            errors.add("Price must not exceed $99,999.99");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
```

### Integration Test
```java
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createProduct_shouldReturn201() throws Exception {
        String request = """
            {
                "name": "Test Product",
                "description": "A test product",
                "price": 29.99,
                "stockQuantity": 100,
                "category": "electronics"
            }
            """;

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void getProduct_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/products/nonexistent"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void createProduct_invalidPrice_shouldReturn400() throws Exception {
        String request = """
            {
                "name": "Test",
                "description": "test",
                "price": -10,
                "stockQuantity": 1,
                "category": "test"
            }
            """;

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
```

---

## 2. GraphQL API with Spring <a name="graphql"></a>

### Schema Definition
```graphql
type Query {
    product(id: ID!): Product
    products(category: String, page: Int, size: Int): ProductPage!
}

type Mutation {
    createProduct(input: CreateProductInput!): Product!
    updateProduct(id: ID!, input: UpdateProductInput!): Product!
    deleteProduct(id: ID!): Boolean!
}

type Product {
    id: ID!
    name: String!
    description: String!
    price: Float!
    stockQuantity: Int!
    category: String
    reviews: [Review!]
    createdAt: String!
    updatedAt: String!
}

type ProductPage {
    content: [Product!]!
    totalElements: Long!
    totalPages: Int!
    number: Int!
}

input CreateProductInput {
    name: String!
    description: String!
    price: Float!
    stockQuantity: Int!
    category: String
}

type Review {
    id: ID!
    rating: Int!
    comment: String
    author: String!
}
```

### GraphQL Controller
```java
@Controller
public class ProductGraphQLController {

    @QueryMapping
    public Product product(@Argument String id) {
        return productService.findById(id);
    }

    @QueryMapping
    public ProductPage products(
            @Argument String category,
            @Argument int page,
            @Argument int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.findPage(category, pageable);
    }

    @MutationMapping
    public Product createProduct(@Argument CreateProductInput input) {
        return productService.create(input);
    }

    @SchemaMapping(typeName = "Product")
    public List<Review> reviews(Product product) {
        return reviewService.findByProductId(product.getId());
    }
}
```

### GraphQL Error Handling
```java
@ControllerAdvice
public class GraphQLExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public void handleNotFound(ProductNotFoundException ex) {
        throw new GraphQLError("Product not found: " + ex.getProductId());
    }
}
```

---

## 3. gRPC Service in Java <a name="grpc"></a>

### Protobuf Definition
```protobuf
syntax = "proto3";

package product;

service ProductService {
    rpc GetProduct (GetProductRequest) returns (Product);
    rpc ListProducts (ListProductsRequest) returns (ListProductsResponse);
    rpc CreateProduct (CreateProductRequest) returns (Product);
    rpc WatchProducts (WatchProductsRequest) returns (stream Product);
}

message GetProductRequest {
    string id = 1;
}

message Product {
    string id = 1;
    string name = 2;
    string description = 3;
    double price = 4;
    int32 stock_quantity = 5;
    string category = 6;
    int64 created_at = 7;
}

message ListProductsRequest {
    int32 page = 1;
    int32 size = 2;
    string category = 3;
}

message ListProductsResponse {
    repeated Product products = 1;
    int32 total_count = 2;
}
```

### gRPC Service Implementation
```java
@GrpcService
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductService productService;

    public ProductGrpcService(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void getProduct(GetProductRequest request,
            StreamObserver<Product> responseObserver) {
        try {
            ProductDTO dto = productService.findById(request.getId());
            Product proto = toProto(dto);
            responseObserver.onNext(proto);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void watchProducts(WatchProductsRequest request,
            StreamObserver<Product> responseObserver) {
        // Stream products as they change
        Flux<ProductDTO> stream = productService.watchChanges();
        stream.map(this::toProto)
            .doOnNext(responseObserver::onNext)
            .doOnComplete(responseObserver::onCompleted)
            .doOnError(responseObserver::onError)
            .subscribe();
    }

    private Product toProto(ProductDTO dto) {
        return Product.newBuilder()
            .setId(dto.id())
            .setName(dto.name())
            .setPrice(dto.price().doubleValue())
            .build();
    }
}
```

---

## 4. API Gateway Pattern <a name="gateway"></a>

### Spring Cloud Gateway Configuration
```java
@Configuration
public class ApiGatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("product-service", r -> r
                .path("/api/v1/products/**")
                .filters(f -> f
                    .circuitBreaker(config -> config
                        .setName("productServiceCB")
                        .setFallbackUri("forward:/fallback/products"))
                    .retry(config -> config
                        .setRetries(3)
                        .setStatuses(HttpStatus.SERVICE_UNAVAILABLE))
                    .requestRateLimiter(config -> config
                        .setRateLimiter(redisRateLimiter()))
                )
                .uri("lb://product-service"))
            .route("order-service", r -> r
                .path("/api/v1/orders/**")
                .filters(f -> f
                    .circuitBreaker(config -> config
                        .setName("orderServiceCB")))
                .uri("lb://order-service"))
            .build();
    }
}
```

### Gateway Rate Limiting
```java
@Bean
public RedisRateLimiter redisRateLimiter() {
    return new RedisRateLimiter(100, 200, 1);  // 100 req/s, 200 burst
}
```

---

## Summary

This deep dive covered:
1. **REST API**: Complete CRUD with validation, error handling, HATEOAS, tests
2. **GraphQL**: Schema definition, queries, mutations, resolvers
3. **gRPC**: Protobuf definition, service implementation, streaming
4. **API Gateway**: Routing, circuit breakers, rate limiting, retries
