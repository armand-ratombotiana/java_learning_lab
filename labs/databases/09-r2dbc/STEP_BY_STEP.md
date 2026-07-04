# Step by Step: Setting Up R2DBC

## Step 1: Add Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-r2dbc</artifactId>
</dependency>
<dependency>
    <groupId>io.r2dbc</groupId>
    <artifactId>r2dbc-h2</artifactId>
    <scope>runtime</scope>
</dependency>
<!-- Or for Postgres:
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>r2dbc-postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
-->
```

## Step 2: Configure
```properties
spring.r2dbc.url=r2dbc:h2:mem:///testdb
spring.r2dbc.username=sa
spring.r2dbc.password=
```

## Step 3: Define Entity
```java
import org.springframework.data.annotation.Id;

public class Product {
    @Id
    private Long id;
    private String name;
    private BigDecimal price;
    // getters/setters
}
```

## Step 4: Create Repository
```java
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
    Flux<Product> findByPriceBetween(BigDecimal min, BigDecimal max);
}
```

## Step 5: Use in Service
```java
@Service
public class ProductService {
    private final ProductRepository repository;

    public Flux<Product> getByPriceRange(BigDecimal min, BigDecimal max) {
        return repository.findByPriceBetween(min, max)
            .subscribeOn(Schedulers.boundedElastic());
    }
}
```
