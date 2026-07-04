# Refactoring to CQRS

## CRUD to CQRS Migration

### Step 1: Identify Read/Write Imbalance
```java
// BEFORE: Single model for everything
@Entity
public class Product {
    // Fields used for both commands and queries
}
```

### Step 2: Create Separate Interfaces
```java
// PHASE 1: Add read model interface
public interface ProductQueryService {
    ProductSummary getProductSummary(String id);
    List<ProductListItem> searchProducts(String query);
}

public interface ProductCommandService {
    void createProduct(CreateProductCommand cmd);
    void updatePrice(UpdatePriceCommand cmd);
}
```

### Step 3: Implement Separately
```java
// PHASE 2: Implement read side differently
@Service
public class ProductQueryServiceImpl implements ProductQueryService {
    // Uses optimized query database
    private final ProductViewRepository viewRepository;
}

@Service
public class ProductCommandServiceImpl implements ProductCommandService {
    // Uses domain model with business rules
    private final ProductRepository productRepository;
}
```

### Step 4: Add Event Publishing
```java
// PHASE 3: Events to sync read model
public void createProduct(CreateProductCommand cmd) {
    Product product = productFactory.create(cmd);
    productRepository.save(product);
    eventPublisher.publish(new ProductCreatedEvent(product));
}
```

### Step 5: Build Projection
```java
@EventHandler
public void on(ProductCreatedEvent event) {
    viewRepository.save(new ProductView(event));
}
```
