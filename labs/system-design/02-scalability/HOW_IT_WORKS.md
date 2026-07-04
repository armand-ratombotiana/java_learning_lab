# Scalability - HOW IT WORKS

## Horizontal Scaling (Scale Out)

### Stateless Services
```java
@RestController
public class ProductController {
    // No session state stored locally
    // All state in database or cache
    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable String id) {
        return productService.findById(id);
    }
}
```
Multiple instances run behind a load balancer. Any instance can handle any request.

### Load Balancer Algorithms
- **Round Robin**: Distributes evenly if request duration is uniform
- **Least Connections**: Sends to least busy server
- **IP Hash**: Sticky sessions (avoid if possible)
- **Weighted**: Distributes based on instance capacity

## Vertical Scaling (Scale Up)

### Increase Resources
- More CPU cores
- More RAM
- Faster disks (NVMe)
- Network bandwidth upgrades

### JVM Tuning for Vertical Scale
```bash
java -Xms8g -Xmx16g -XX:+UseG1GC \
     -XX:ParallelGCThreads=8 \
     -jar application.jar
```

## Database Scaling

### Read Replicas
```java
// Configure read/write data sources
@Bean
@Primary
public DataSource writeDataSource() { /* master */ }

@Bean
public DataSource readDataSource() { /* replica */ }

// Route reads to replica, writes to master
@Transactional(readOnly = true)
public List<Product> findAll() {
    return readRepository.findAll();  // replica
}

@Transactional
public Product save(Product p) {
    return writeRepository.save(p);  // master
}
```

### Database Sharding
```java
// Shard by customer ID
public class ShardRouter {
    public int getShard(String customerId) {
        return Math.abs(customerId.hashCode()) % NUM_SHARDS;
    }
}

// Route to appropriate shard
public Order findOrder(String customerId, String orderId) {
    int shard = router.getShard(customerId);
    return shardDataSources[shard]
        .getOrderRepository()
        .findById(orderId);
}
```

### Caching Layer
```java
@Cacheable(value = "products", key = "#id")
public Product getProduct(String id) {
    return productRepository.findById(id)
        .orElseThrow();  // expensive DB call
}
```

## Message Queues for Async Scaling
```java
// Producer: Queue work for async processing
public void placeOrder(Order order) {
    orderRepository.save(order);
    kafkaTemplate.send("order-processing", order.getId());
}

// Consumer: Process asynchronously
@KafkaListener(topics = "order-processing")
public void processOrder(String orderId) {
    Order order = orderRepository.findById(orderId);
    // expensive processing off the request thread
}
```
