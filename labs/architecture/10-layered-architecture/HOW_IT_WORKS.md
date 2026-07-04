# How Layered Architecture Works

## Request Flow
```
Client -> Controller (HTTP) -> Service (Business) -> Repository (Data) -> Database
```

## Complete Request Flow Example

### 1. Presentation Layer
```java
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody @Valid CreateProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }
}
```

### 2. Business Layer
```java
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductResponse createProduct(CreateProductRequest request) {
        // Business validation
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException(request.getSku());
        }

        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

        Product product = Product.builder()
            .name(request.getName())
            .sku(request.getSku())
            .price(request.getPrice())
            .category(category)
            .build();

        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductResponse.from(product);
    }
}
```

### 3. Persistence Layer
```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySku(String sku);
    List<Product> findByCategoryId(Long categoryId);
    Page<Product> findByNameContaining(String name, Pageable pageable);
}

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String sku;

    private String name;
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
```

### 4. Cross-Cutting Concerns
```java
@Aspect
@Component
public class PerformanceMonitor {

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object monitorTransaction(ProceedingJoinPoint pjp) throws Throwable {
        Timer.Sample sample = Timer.start();
        try {
            return pjp.proceed();
        } finally {
            sample.stop(Timer.builder("transaction.duration")
                .tag("method", pjp.getSignature().toShortString())
                .register(meterRegistry));
        }
    }
}
```
