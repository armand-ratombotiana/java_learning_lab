# Code Deep Dive: Polyglot Persistence in Java

## Multi-Database Application Configuration

```java
@Configuration
public class DatabaseConfig {

    // Relational (Spring Data JPA)
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.orders")
    public DataSource ordersDataSource() {
        return DataSourceBuilder.create().build();
    }

    // Document (MongoDB)
    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }

    // Graph (Neo4j)
    @Bean
    public Driver neo4jDriver() {
        return GraphDatabase.driver("bolt://localhost:7687",
            AuthTokens.basic("neo4j", "password"));
    }

    // Key-Value (Redis)
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
```

## Service Using Multiple Databases

```java
@Service
public class ProductService {
    private final JpaRepository<Inventory, Long> inventoryRepo;   // RDBMS
    private final MongoRepository<ProductDoc, String> catalogRepo; // MongoDB
    private final RedisTemplate<String, Object> cache;             // Redis

    @Transactional
    public ProductResponse getProduct(String productId) {
        // Check cache first
        ProductResponse cached = (ProductResponse) cache.opsForValue()
            .get("product:" + productId);
        if (cached != null) return cached;

        // Query document DB for catalog data
        ProductDoc doc = catalogRepo.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));

        // Query relational DB for inventory
        Inventory inv = inventoryRepo.findBySku(doc.getSku());

        // Build and cache response
        ProductResponse response = new ProductResponse(doc, inv);
        cache.opsForValue().set("product:" + productId, response, 5, TimeUnit.MINUTES);
        return response;
    }
}
```
