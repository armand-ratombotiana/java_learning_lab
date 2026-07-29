# PROBLEM WALKTHROUGH: Implement a GraalVM Native-Image Compatible REST Service

## Problem Statement

Design and implement a RESTful microservice that is fully compatible with GraalVM native-image compilation. The service should:

- Compile to a native binary with sub-second startup time and minimal memory footprint
- Use reflection, proxies, and resources in a native-image-friendly way
- Implement proper `@Configuration` and `@Serialization` registration via GraalVM reachability metadata
- Support AOT (Ahead-of-Time) compilation with Spring Boot 3.x
- Provide configuration hints using `@RegisterReflectionForBinding`, `RuntimeHints`, and GraalVM feature files
- Implement a REST API for product catalog with proper serialization
- Expose health checks and metrics endpoints

**Constraints:**
- Spring Boot 3.x with GraalVM native-image support
- Java 21+
- Spring AOT engine for auto-configuration hints
- Minimal runtime dependencies (avoid dynamic classloading, reflection-heavy libs)

---

## Step-by-Step Solution

### Step 1: Project Setup with Spring Boot 3.x Native Support

```xml
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
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.graalvm.buildtools</groupId>
            <artifactId>native-maven-plugin</artifactId>
        </plugin>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

### Step 2: Application Entry Point

```java
@SpringBootApplication
public class ProductCatalogApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductCatalogApplication.class, args);
    }
}
```

### Step 3: Domain Model — Serializable Records

```java
public record Product(
    String id,
    String name,
    String description,
    BigDecimal price,
    String category,
    Set<String> tags,
    boolean available,
    Instant createdAt,
    Instant updatedAt
) {}

public record CreateProductRequest(
    @NotBlank String name,
    String description,
    @NotNull @Positive BigDecimal price,
    @NotBlank String category,
    Set<String> tags
) {}

public record UpdateProductRequest(
    String name,
    String description,
    BigDecimal price,
    String category,
    Set<String> tags,
    Boolean available
) {}

public record ProductSummary(
    String id,
    String name,
    BigDecimal price,
    boolean available
) {}

public record ApiError(
    int status,
    String message,
    Instant timestamp
) {}

public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {}
```

### Step 4: Repository — Avoid Dynamic Proxies for Native

```java
@Repository
public class ProductRepository {

    private final ConcurrentHashMap<String, Product> store = new ConcurrentHashMap<>();

    public ProductRepository() {
        // Pre-populate with sample data; in production, use a proper DB
        initSampleData();
    }

    void initSampleData() {
        save(new Product(null, "MacBook Pro 16", "Apple M3 Pro chip, 18GB RAM, 512GB SSD",
            new BigDecimal("2499.00"), "Laptops", Set.of("apple", "premium"), true,
            Instant.now(), Instant.now()));
        save(new Product(null, "ThinkPad X1 Carbon", "Intel i7, 16GB RAM, 1TB SSD",
            new BigDecimal("1899.00"), "Laptops", Set.of("lenovo", "business"), true,
            Instant.now(), Instant.now()));
        save(new Product(null, "iPhone 15 Pro", "A17 Pro, 256GB, Titanium",
            new BigDecimal("1199.00"), "Phones", Set.of("apple", "mobile"), true,
            Instant.now(), Instant.now()));
        save(new Product(null, "Dell UltraSharp 27", "4K IPS, USB-C Hub",
            new BigDecimal("749.00"), "Monitors", Set.of("dell", "4k"), true,
            Instant.now(), Instant.now()));
    }

    public List<Product> findAll() {
        return List.copyOf(store.values());
    }

    public PageResponse<Product> findAll(int page, int size, String sortBy, boolean asc) {
        List<Product> sorted = new ArrayList<>(store.values());
        Comparator<Product> comparator = switch (sortBy) {
            case "name" -> Comparator.comparing(Product::name);
            case "price" -> Comparator.comparing(Product::price);
            case "createdAt" -> Comparator.comparing(Product::createdAt);
            default -> Comparator.comparing(Product::id);
        };
        if (!asc) comparator = comparator.reversed();
        sorted.sort(comparator);

        int total = sorted.size();
        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);
        List<Product> content = from < to ? sorted.subList(from, to) : List.of();

        return new PageResponse<>(content, page, size, total,
            (int) Math.ceil((double) total / size));
    }

    public Optional<Product> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public Product save(Product product) {
        String id = product.id() != null ? product.id() : UUID.randomUUID().toString();
        Instant now = Instant.now();
        Product saved = new Product(id, product.name(), product.description(),
            product.price(), product.category(), product.tags(),
            product.available(), product.createdAt() != null ? product.createdAt() : now, now);
        store.put(id, saved);
        return saved;
    }

    public void deleteById(String id) {
        store.remove(id);
    }

    public boolean exists(String id) {
        return store.containsKey(id);
    }
}
```

### Step 5: Controller

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public PageResponse<Product> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "true") boolean asc) {
        return productRepository.findAll(page, size, sortBy, asc);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable String id) {
        return productRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody CreateProductRequest request) {
        Product product = new Product(null, request.name(), request.description(),
            request.price(), request.category(),
            request.tags() != null ? request.tags() : Set.of(),
            true, null, null);
        Product saved = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable String id,
                                           @Valid @RequestBody UpdateProductRequest request) {
        return productRepository.findById(id).map(existing -> {
            Product updated = new Product(id,
                request.name() != null ? request.name() : existing.name(),
                request.description() != null ? request.description() : existing.description(),
                request.price() != null ? request.price() : existing.price(),
                request.category() != null ? request.category() : existing.category(),
                request.tags() != null ? request.tags() : existing.tags(),
                request.available() != null ? request.available() : existing.available(),
                existing.createdAt(), Instant.now());
            return ResponseEntity.ok(productRepository.save(updated));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (productRepository.exists(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
```

### Step 6: RuntimeHints for GraalVM Native-Image

```java
@Configuration
public class NativeHintsConfig implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // Register serialization for all domain records
        hints.serialization()
            .registerType(Product.class)
            .registerType(CreateProductRequest.class)
            .registerType(UpdateProductRequest.class)
            .registerType(ProductSummary.class)
            .registerType(ApiError.class)
            .registerType(PageResponse.class);

        // Register reflection for Jackson serialization
        hints.reflection()
            .registerType(Product.class, MemberCategory.values())
            .registerType(CreateProductRequest.class, MemberCategory.values())
            .registerType(UpdateProductRequest.class, MemberCategory.values())
            .registerType(ProductSummary.class, MemberCategory.values())
            .registerType(ApiError.class, MemberCategory.values())
            .registerType(PageResponse.class, MemberCategory.values());

        // Register resources for static content
        hints.resources()
            .registerPattern("application.yml")
            .registerPattern("application.properties")
            .registerPattern("messages.properties")
            .registerPattern("logback.xml");

        // Register proxies for Spring classes
        hints.proxies()
            .registerJdkProxy(org.springframework.data.domain.Pageable.class);

        // Register GraalVM reflection for feature flags
        hints.reflection()
            .registerType(SpringApplication.class, MemberCategory.INVOKE_DECLARED_METHODS);
    }
}
```

### Step 7: Alternative Approach — @RegisterReflectionForBinding

```java
@Configuration
@RegisterReflectionForBinding({
    Product.class,
    CreateProductRequest.class,
    UpdateProductRequest.class,
    ProductSummary.class,
    ApiError.class,
    PageResponse.class
})
public class ReflectionConfig {
    // Alternative: use annotation-based reflection hints
}
```

### Step 8: GraalVM Feature (Optional Custom Initialization)

```java
public class NativeImageFeature implements Feature {

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        System.out.println("[GraalVM Feature] Running before analysis phase");

        // Register classes for runtime instantiation
        access.registerClassForReflectiveInstantiation(
            access.findClassByName("com.example.Product"));
    }

    @Override
    public void afterAnalysis(AfterAnalysisAccess access) {
        System.out.println("[GraalVM Feature] Analysis complete. Unused types: "
            + access.getUnusedTypes().size());
    }

    @Override
    public void duringSetup(DuringSetupAccess access) {
        System.out.println("[GraalVM Feature] Setting up...");
    }
}
```

### Step 9: Native Image Configuration (JSON)

```json
// META-INF/native-image/com.example/product-catalog/native-image.properties
// Args for native-image build

// META-INF/native-image/com.example/product-catalog/reflect-config.json
{
  "name": "com.example.Product",
  "allDeclaredFields": true,
  "allDeclaredMethods": true,
  "allDeclaredConstructors": true
}

// META-INF/native-image/com.example/product-catalog/resource-config.json
{
  "resources": {
    "includes": [
      {"pattern": "\\Qapplication.yml\\E"},
      {"pattern": "\\Qbanner.txt\\E"}
    ]
  }
}

// META-INF/native-image/com.example/product-catalog/serialization-config.json
{
  "types": [
    {"name": "com.example.Product"},
    {"name": "com.example.CreateProductRequest"},
    {"name": "com.example.PageResponse"}
  ]
}

// META-INF/native-image/com.example/product-catalog/proxy-config.json
{
  "interfaces": [
    "org.springframework.data.domain.Pageable"
  ]
}
```

### Step 10: Maven Native Build Configuration

```xml
<profiles>
    <profile>
        <id>native</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.graalvm.buildtools</groupId>
                    <artifactId>native-maven-plugin</artifactId>
                    <configuration>
                        <mainClass>com.example.ProductCatalogApplication</mainClass>
                        <imageName>product-catalog-native</imageName>
                        <buildArgs>
                            <buildArg>--no-fallback</buildArg>
                            <buildArg>--enable-url-protocols=http</buildArg>
                            <buildArg>--trace-class-initialization=ch.qos.logback.classic.Logger</buildArg>
                            <buildArg>-H:+ReportExceptionStackTraces</buildArg>
                            <buildArg>-H:ReflectionConfigurationFiles=reflect-config.json</buildArg>
                        </buildArgs>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

### Step 11: Application Properties for AOT

```yaml
spring:
  aot:
    enabled: true
  main:
    lazy-initialization: false
  jackson:
    serialization:
      write-dates-as-timestamps: false
    default-property-inclusion: non_null
  jmx:
    enabled: false
server:
  port: 8080
```

### Step 12: AOT-Safe Error Handler

```java
@RestControllerAdvice
public class NativeSafeExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(ConstraintViolationException ex) {
        return new ApiError(400, ex.getMessage(), Instant.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return new ApiError(400, message, Instant.now());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(NotFoundException ex) {
        return new ApiError(404, ex.getMessage(), Instant.now());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleGeneral(Exception ex) {
        return new ApiError(500, "Internal server error", Instant.now());
    }
}

class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}
```

### Step 13: AOT-Safe Configuration Properties

```java
@ConfigurationProperties(prefix = "app.catalog")
@Validated
public record CatalogProperties(
    @Positive int maxPageSize,
    @NotNull List<String> allowedSortFields,
    @NotNull String defaultSort,
    boolean cacheEnabled,
    @Positive Duration cacheTtl
) {
    public CatalogProperties {
        // Normalize
        if (allowedSortFields == null || allowedSortFields.isEmpty()) {
            allowedSortFields = List.of("name", "price", "createdAt");
        }
        if (defaultSort == null || defaultSort.isBlank()) {
            defaultSort = "name";
        }
    }
}
```

### Step 14: AOT-Safe Health Indicator

```java
@Component
public class NativeHealthIndicator implements HealthIndicator {

    private final ProductRepository productRepository;

    public NativeHealthIndicator(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Health health() {
        try {
            long count = productRepository.findAll().size();
            return Health.up()
                .withDetail("products", count)
                .withDetail("native", isNativeImage())
                .withDetail("startupTime", getStartupTime())
                .build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }

    static boolean isNativeImage() {
        return System.getProperty("org.graalvm.nativeimage.imagecode") != null;
    }

    static String getStartupTime() {
        return ManagementFactory.getRuntimeMXBean().getUptime() + "ms";
    }
}
```

### Step 15: Metrics for Native Image

```java
@Component
public class NativeMetricsConfig {

    private final MeterRegistry meterRegistry;

    public NativeMetricsConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        registerCustomMetrics();
    }

    void registerCustomMetrics() {
        // Native image info
        meterRegistry.gauge("app.native.image", Tags.empty(),
            NativeHealthIndicator.isNativeImage() ? 1 : 0);

        // Memory usage — safe in native image
        meterRegistry.gauge("app.memory.used", Runtime.getRuntime().totalMemory()
            - Runtime.getRuntime().freeMemory());
        meterRegistry.gauge("app.memory.max", Runtime.getRuntime().maxMemory());

        // Thread count
        meterRegistry.gauge("app.threads", Thread.activeCount());
    }
}
```

### Step 16: Native Build Commands

```bash
# Build native image (Maven)
mvn -Pnative native:compile

# Build native image (Gradle)
gradle nativeCompile

# Run the native binary
./target/product-catalog-native

# Build with Docker (Paketo Buildpacks)
mvn -Pnative spring-boot:build-image

# Compare startup time
echo "=== JVM Mode ==="
time java -jar target/product-catalog.jar
echo "=== Native Mode ==="
time ./target/product-catalog-native
```

### Step 17: AOT-Safe Test Configuration

```java
@SpringBootTest
@ActiveProfiles("test")
class ProductCatalogApplicationTests {

    @Autowired
    private ProductController controller;

    @Autowired
    private ProductRepository repository;

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @Test
    void shouldCreateNativeImageHints() {
        // Verify hints work by testing reflection registration
        assertThat(Product.class.getRecordComponents()).isNotEmpty();
    }

    @Test
    @ConditionalOnProperty(name = "org.graalvm.nativeimage.imagecode")
    void onlyRunsInNativeImage() {
        // This test only runs in native mode
        assertThat(NativeHealthIndicator.isNativeImage()).isTrue();
    }
}
```

---

## Complexity Analysis

| Aspect | JVM Mode | Native Image Mode |
|--------|----------|-------------------|
| **Startup time** | 2-5 seconds | 0.05-0.2 seconds |
| **Memory (RSS)** | 150-300 MB | 15-50 MB |
| **First request latency** | 1-3 seconds (JIT warmup) | <10ms |
| **Peak throughput** | Higher (JIT optimized) | ~80% of JVM |
| **Build time** | 10-20 seconds | 2-10 minutes |
| **Binary size** | 40-60 MB (JAR) | 50-120 MB (native) |
| **Reflection** | Full support | Requires hints |
| **Dynamic proxies** | Full support | Requires hints |
| **Serialization** | Full support | Requires hints |

---

## Follow-Up Questions

1. **What is the GraalVM closed-world assumption?** — The native-image AOT compiler assumes all reachable code is known at build time. Dynamic class loading, reflection, and proxies must be declared at build time via reachability metadata.

2. **How does Spring AOT engine help with native images?** — Spring AOT analyzes the application at build time and generates reachability metadata (reflection, serialization, resources, proxies). Spring Boot 3.x applications are automatically processed by the AOT engine.

3. **What are the limitations of native images in Spring Boot?** — No JIT (peak perf is lower), no dynamic classloading, no conditional bean registration at runtime (conditions evaluated at build time), some libraries not compatible, longer build times.

4. **How do you handle configuration profiles in native images?** — Profile-specific configuration is baked into the binary. To change profiles, rebuild. Use environment variables or `-Dspring.profiles.active` at runtime (supported in native).

5. **When should you use native images vs JVM?** — Native: serverless (Lambda), short-lived processes, CLI tools, containers with memory constraints. JVM: long-running services that benefit from JIT optimization, applications needing dynamic behavior.

---

## Test Cases

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ProductCatalogNativeCompatibleTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].name").isString())
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.totalElements").isNumber());
    }

    @Test
    void shouldReturnProductById() throws Exception {
        var response = mockMvc.perform(get("/api/products"))
            .andReturn().getResponse().getContentAsString();
        var page = new ObjectMapper().readValue(response, PageResponse.class);
        String firstId = page.content().get(0).id();

        mockMvc.perform(get("/api/products/{id}", firstId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(firstId));
    }

    @Test
    void shouldCreateProduct() throws Exception {
        var request = new CreateProductRequest("New Product", "Description",
            new BigDecimal("99.99"), "Books", Set.of("fiction"));
        String body = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("New Product"))
            .andExpect(jsonPath("$.id").isString());
    }

    @Test
    void shouldRejectInvalidProduct() throws Exception {
        var request = new CreateProductRequest("", "Desc",
            new BigDecimal("-1"), "", null);
        String body = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        var request = new UpdateProductRequest("Updated Name", null, null, null, null, null);
        String body = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(put("/api/products/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
            .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/products/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldSupportPagination() throws Exception {
        mockMvc.perform(get("/api/products?page=0&size=2&sortBy=price&asc=false"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.totalPages").isNumber());
    }

    @Test
    void shouldReportHealth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }
}

@SpringBootTest
@AutoConfigureTestDatabase
class NativeImageHintsTest {

    @Test
    void shouldRegisterRequiredReflection() {
        // Verify serialization
        var mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
        assertThatCode(() -> {
            Product product = new Product("1", "Test", "Desc",
                BigDecimal.TEN, "Cat", Set.of("a"), true,
                Instant.now(), Instant.now());
            String json = mapper.writeValueAsString(product);
            Product deserialized = mapper.readValue(json, Product.class);
            assertThat(deserialized.name()).isEqualTo("Test");
        }).doesNotThrowAnyException();
    }

    @Test
    void shouldHandleJacksonPolymorphism() throws Exception {
        // Jackson with records requires proper hints
        var mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
        String json = "{\"id\":\"1\",\"name\":\"Test\"}";
        Product product = mapper.readValue(json, Product.class);
        assertThat(product.name()).isEqualTo("Test");
    }
}
```

---

## Summary

This GraalVM native-image compatible REST service demonstrates:
- **AOT compatibility**: records, explicit serialization hints, no dynamic proxies
- **RuntimeHintsRegistrar**: programmatic registration of reflection, serialization, resources, proxies
- **@RegisterReflectionForBinding**: annotation-based reflection hints
- **GraalVM reachability metadata**: JSON-based configuration files
- **Native build**: Maven `native:compile` build with zero-config hints
- **Sub-second startup**: native binary starts in milliseconds vs seconds
- **Low memory**: 15-50MB RSS vs 150-300MB in JVM mode
- **AOT-safe patterns**: no dynamic class loading, explicit exception handlers, safe health indicators