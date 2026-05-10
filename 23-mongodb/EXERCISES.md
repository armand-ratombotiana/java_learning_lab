# MongoDB - Exercises

---

## Exercise Set 1: MongoDB Fundamentals

### Exercise 1.1: Document Creation
**Task**: Create a product document with embedded reviews.

```java
@Test
public void createProductWithReviews() {
    Product product = new Product();
    product.setName("Wireless Headphones");
    product.setDescription("Premium noise-canceling headphones");
    product.setPrice(new BigDecimal("299.99"));
    product.setCategory("Electronics");
    product.setTags(List.of("audio", "wireless", "noise-canceling"));
    product.setStockQuantity(100);
    product.setActive(true);
    
    product.setReviews(List.of(
        new Review("user1", 5, "Amazing sound quality!"),
        new Review("user2", 4, "Great but pricey")
    ));
    
    Product saved = repository.save(product);
    assertNotNull(saved.getId());
}
```

---

### Exercise 1.2: Query Methods
**Task**: Implement various query methods.

```java
public interface ProductRepository extends MongoRepository<Product, String> {
    
    // Find by single field
    List<Product> findByCategory(String category);
    
    // Find with multiple conditions
    List<Product> findByCategoryAndActive(String category, Boolean active);
    
    // Find with comparison operators
    @Query("{ 'price': { $gte: ?0, $lte: ?1 } }")
    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Case-insensitive search
    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<Product> searchByName(String searchTerm);
    
    // Exists check
    List<Product> findByTagsContaining(String tag);
    
    // Sorted query
    List<Product> findByCategoryOrderByPriceDesc(String category);
}
```

---

### Exercise 1.3: Update Operations
**Task**: Implement various update patterns.

```java
@Service
public class ProductService {
    
    public void addReview(String productId, Review review) {
        Query query = Query.query(Criteria.where("_id").is(productId));
        Update update = new Update().push("reviews", review);
        
        mongoTemplate.updateFirst(query, update, Product.class);
    }
    
    public void incrementStock(String productId, int quantity) {
        Query query = Query.query(Criteria.where("_id").is(productId));
        Update update = new Update().inc("stockQuantity", quantity);
        
        mongoTemplate.updateFirst(query, update, Product.class);
    }
    
    public void setPrice(String productId, BigDecimal newPrice) {
        Query query = Query.query(Criteria.where("_id").is(productId));
        Update update = new Update().set("price", newPrice);
        
        mongoTemplate.updateFirst(query, update, Product.class);
    }
    
    public void deactivateOutOfStock() {
        Query query = Query.query(Criteria.where("stockQuantity").lte(0));
        Update update = new Update().set("active", false);
        
        mongoTemplate.updateMulti(query, update, Product.class);
    }
}
```

---

## Exercise Set 2: Document Modeling

### Exercise 2.1: Order Document Model
**Task**: Design an order document with embedded items.

```java
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    
    private String orderNumber;
    private String customerId;
    private String customerEmail;
    
    @Field("customer_info")
    private CustomerInfo customerInfo;
    
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private String status;
    
    private ShippingAddress shippingAddress;
    private BillingAddress billingAddress;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Field("fulfillment_info")
    private FulfillmentInfo fulfillmentInfo;
    
    // Embedded document classes
    public static class OrderItem {
        private String productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
        private String imageUrl;
    }
    
    public static class CustomerInfo {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
    }
}
```

---

### Exercise 2.2: Reference Pattern
**Task**: Implement order with referenced entities.

```java
// In order service
public Order createOrderWithReferences(OrderRequest request) {
    Order order = new Order();
    order.setOrderNumber("ORD-" + System.currentTimeMillis());
    order.setCustomerId(request.customerId());
    order.setShippingAddress(request.shippingAddress());
    
    List<OrderItem> items = new ArrayList<>();
    BigDecimal total = BigDecimal.ZERO;
    
    for (OrderItemRequest itemReq : request.items()) {
        Product product = productRepository.findById(itemReq.productId())
            .orElseThrow(() -> new ProductNotFoundException(itemReq.productId()));
        
        OrderItem item = new OrderItem();
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setQuantity(itemReq.quantity());
        item.setUnitPrice(product.getPrice());
        item.setSubtotal(product.getPrice()
            .multiply(new BigDecimal(itemReq.quantity())));
        
        items.add(item);
        total = total.add(item.getSubtotal());
    }
    
    order.setItems(items);
    order.setTotalAmount(total);
    
    return repository.save(order);
}
```

---

### Exercise 2.3: Schema Validation
**Task**: Configure JSON schema validation.

```java
@Collection("products")
public class Product {
    @Id
    private String id;
    
    @Indexed
    private String name;
    
    private BigDecimal price;
    
    @Indexed
    private String category;
    
    @Indexed
    private List<String> tags;
    
    @Field("stock_quantity")
    private Integer stockQuantity;
    
    private Boolean active;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}

// Collection configuration for validation
@Configuration
public class MongoConfig {
    
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(
            Arrays.asList(new BigDecimalToDecimal128Converter(),
                        new Decimal128ToBigDecimalConverter()));
    }
}
```

---

## Exercise Set 3: Aggregation Pipelines

### Exercise 3.1: Sales Analytics
**Task**: Build a sales analytics aggregation pipeline.

```java
public AggregationResults<SalesStats> getSalesAnalytics(
        LocalDate startDate, LocalDate endDate) {
    
    Aggregation aggregation = Aggregation.newAggregation(
        // Match orders in date range
        Aggregation.match(Criteria.where("createdAt")
            .gte(startDate.atStartOfDay())
            .lte(endDate.atTime(23, 59, 59))),
        
        // Unwind order items
        Aggregation.unwind("items"),
        
        // Group by category
        Aggregation.group("items.productId")
            .first("items.productName").as("productName")
            .sum("items.quantity").as("unitsSold")
            .sum("items.subtotal").as("revenue")
            .count().as("orderCount"),
        
        // Sort by revenue
        Aggregation.sort(Sort.Direction.DESC, "revenue"),
        
        // Limit to top 10
        Aggregation.limit(10),
        
        // Project final result
        Aggregation.project()
            .and("_id").as("productId")
            .andInclude("productName", "unitsSold", "revenue", "orderCount")
    );
    
    return mongoTemplate.aggregate(aggregation, "orders", SalesStats.class);
}
```

---

### Exercise 3.2: Customer Analytics
**Task**: Build customer lifetime value analysis.

```java
public AggregationResults<CustomerLTV> calculateCustomerLTV() {
    Aggregation aggregation = Aggregation.newAggregation(
        // Match completed orders
        Aggregation.match(Criteria.where("status").is("COMPLETED")),
        
        // Group by customer
        Aggregation.group("customerId")
            .first("customerEmail").as("email")
            .sum("totalAmount").as("totalSpent")
            .count().as("orderCount")
            .min("createdAt").as("firstOrderDate")
            .max("createdAt").as("lastOrderDate"),
        
        // Calculate metrics
        Aggregation.project()
            .and("_id").as("customerId")
            .andInclude("email", "totalSpent", "orderCount", 
                       "firstOrderDate", "lastOrderDate")
            .and(DateOperators.DiffBetween
                .dateOf("$lastOrderDate")
                .dateOf("$firstOrderDate")
                .inUnit(DateOperators.DateUnit.DAYS))
                .as("daysSinceFirstOrder")
            .and(ArithmeticOperators.Divide
                .divideOf("$totalSpent")
                .divideBy(ArithmeticOperators.Divide
                    .divideOf("$orderCount")
                    .by(1)))
                .as("averageOrderValue"),
        
        // Sort by lifetime value
        Aggregation.sort(Sort.Direction.DESC, "totalSpent")
    );
    
    return mongoTemplate.aggregate(aggregation, "orders", CustomerLTV.class);
}
```

---

### Exercise 3.3: Inventory Reports
**Task**: Generate inventory status reports.

```java
public AggregationResults<InventoryStatus> getInventoryReport() {
    Aggregation aggregation = Aggregation.newAggregation(
        // Group by category and stock status
        Aggregation.group("category")
            .count().as("totalProducts")
            .sum(ConditionalOperators.when(
                Criteria.where("stockQuantity").gt(10))
                .then(1).otherwise(0))
                .as("wellStocked")
            .sum(ConditionalOperators.when(
                Criteria.where("stockQuantity").between(1, 10))
                .then(1).otherwise(0))
                .as("lowStock")
            .sum(ConditionalOperators.when(
                Criteria.where("stockQuantity").is(0))
                .then(1).otherwise(0))
                .as("outOfStock")
            .avg("price").as("avgPrice"),
        
        // Project final shape
        Aggregation.project()
            .and("_id").as("category")
            .andInclude("totalProducts", "wellStocked", "lowStock", 
                       "outOfStock", "avgPrice")
    );
    
    return mongoTemplate.aggregate(aggregation, "products", InventoryStatus.class);
}
```

---

## Exercise Set 4: Advanced Patterns

### Exercise 4.1: Text Search
**Task**: Implement product search with text index.

```java
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    
    @TextIndexed(weight = 3)
    private String name;
    
    @TextIndexed(weight = 1)
    private String description;
    
    private String category;
    private BigDecimal price;
    
    // In repository
    @Query("{ $text: { $search: ?0 } }")
    @TextScore
    List<Product> searchByText(String searchQuery);
}

// Service method
public List<Product> searchProducts(String query) {
    Query searchQuery = TextQuery.queryText(query);
    searchQuery.fields().include("score");
    searchQuery.sortByScore();
    
    return mongoTemplate.find(searchQuery, Product.class);
}
```

---

### Exercise 4.2: Change Streams
**Task**: Implement real-time order notifications.

```java
@Service
public class OrderChangeStreamService {
    
    private final MongoTemplate mongoTemplate;
    
    public OrderChangeStreamService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    @PostConstruct
    public void watchOrderChanges() {
        ChangeStreamOptions options = ChangeStreamOptions.builder()
            .filter(Aggregation.newAggregation(
                Aggregation.match(ChangeStreamOperationInserter.forInsert(
                    Document.class))))
            .build();
        
        Flux<ChangeStreamEvent<Document>> stream = 
            mongoTemplate.changeStream(
                "orders", 
                options, 
                Document.class);
        
        stream.subscribe(change -> {
            Document order = change.getBody();
            String status = order.getString("status");
            String orderId = order.getObjectId("_id").toString();
            
            processOrderStatusChange(orderId, status);
        });
    }
    
    private void processOrderStatusChange(String orderId, String status) {
        System.out.println("Order " + orderId + " status changed to: " + status);
        // Send notifications, update caches, etc.
    }
}
```

---

### Exercise 4.3: Transactions
**Task**: Implement atomic order processing.

```java
@Service
public class OrderProcessingService {
    
    private final MongoTemplate mongoTemplate;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    
    public void processOrder(String orderId) {
        Session session = mongoTemplate.getDb().startSession();
        
        session.startTransaction();
        
        try {
            Order order = orderRepository.findById(orderId).orElseThrow();
            
            for (Order.OrderItem item : order.getItems()) {
                Product product = productRepository.findById(item.getProductId())
                    .orElseThrow();
                
                int newStock = product.getStockQuantity() - item.getQuantity();
                if (newStock < 0) {
                    throw new InsufficientStockException(item.getProductId());
                }
                
                product.setStockQuantity(newStock);
                productRepository.save(product);
            }
            
            order.setStatus("PROCESSED");
            orderRepository.save(order);
            
            session.commitTransaction();
            
        } catch (Exception e) {
            session.abortTransaction();
            throw e;
        } finally {
            session.endSession();
        }
    }
}
```

---

## Challenge Problems

### Challenge 1: Real-Time Dashboard
**Difficulty**: Advanced
**Task**: Build real-time analytics dashboard.

Requirements:
- Orders per minute
- Revenue per hour
- Top products
- Customer activity
- Update in real-time

---

### Challenge 2: Recommendation Engine
**Difficulty**: Advanced
**Task**: Build product recommendation system.

Requirements:
- Based on purchase history
- Collaborative filtering
- Category-based suggestions
- Real-time updates

---

### Challenge 3: Multi-Tenant Architecture
**Difficulty**: Expert
**Task**: Implement multi-tenant document model.

Requirements:
- Tenant isolation
- Shared indexes
- Efficient queries
- Data migration

---

## Solutions Guidance

For each exercise:
1. Start with simple documents
2. Consider access patterns first
3. Test queries with explain()
4. Add complexity only when needed

---

## Time Estimates

| Exercise | Estimated Time |
|----------|---------------|
| Set 1 | 2-3 hours |
| Set 2 | 2-3 hours |
| Set 3 | 3-4 hours |
| Set 4 | 3-4 hours |
| Challenges | 8+ hours each |

