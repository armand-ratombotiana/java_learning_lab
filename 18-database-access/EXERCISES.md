# Database Access - Exercises

---

## Exercise Set 1: JDBC Fundamentals

### Exercise 1.1: Basic Connection
**Task**: Create a method that establishes a database connection and prints the database product name.

```java
// Implement this method
public void demonstrateBasicConnection() {
    // 1. Load the JDBC driver
    // 2. Create a connection using DriverManager
    // 3. Get database metadata
    // 4. Print product name, version, and supported features
    // 5. Close the connection properly
}
```

**Success Criteria**:
- Uses try-with-resources for connection
- Handles ClassNotFoundException
- Prints meaningful database info

---

### Exercise 1.2: CRUD Operations
**Task**: Implement all CRUD operations for a `Product` table.

```sql
-- Expected table structure
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2),
    stock INT DEFAULT 0
);
```

**Task**: Implement these methods:

```java
public class ProductDAO {
    
    // CREATE: Insert a new product
    public void insert(Product product) { /* ... */ }
    
    // READ: Find product by ID
    public Product findById(Long id) { /* ... */ }
    
    // READ: Find all products
    public List<Product> findAll() { /* ... */ }
    
    // UPDATE: Update product stock
    public void updateStock(Long id, Integer newStock) { /* ... */ }
    
    // DELETE: Delete product by ID
    public void delete(Long id) { /* ... */ }
}
```

**Success Criteria**:
- Uses PreparedStatement with parameters
- Handles NULL values properly
- Returns generated keys for insert

---

### Exercise 1.3: Search Functionality
**Task**: Implement a search feature that finds products by name pattern.

```java
public List<Product> searchByName(String pattern) {
    // Use LIKE with PreparedStatement
    // Support partial matching (case-insensitive)
    // Return results sorted by name
}
```

---

## Exercise Set 2: Connection Pooling

### Exercise 2.1: HikariCP Configuration
**Task**: Configure HikariCP for a high-traffic web application.

Requirements:
- Maximum 50 connections
- Minimum 10 idle connections
- Connection timeout: 30 seconds
- Idle timeout: 10 minutes
- Max lifetime: 30 minutes
- Pool name: "ProductionPool"

```java
public class PoolConfig {
    public DataSource createOptimizedPool() {
        // Implement HikariCP configuration
        // Add leak detection (2 second threshold)
        // Enable fast-fail on connection errors
    }
}
```

---

### Exercise 2.2: Pool Monitoring
**Task**: Create a monitoring utility that reports pool statistics.

```java
public class PoolMonitor {
    public void printPoolStats(HikariDataSource dataSource) {
        // Print: active connections, idle connections, total connections
        // Print: threads awaiting connection
        // Print: connection timeout count
        // Print: average connection wait time
    }
}
```

---

### Exercise 2.3: Connection Leak Detection
**Task**: Configure and test connection leak detection.

```java
public void demonstrateLeakDetection() {
    // 1. Set leak detection threshold to 1 second
    // 2. Get a connection
    // 3. Don't close it
    // 4. Observe leak detection log message
}
```

---

## Exercise Set 3: Transaction Management

### Exercise 3.1: Basic Transactions
**Task**: Implement a fund transfer operation with proper transaction handling.

```java
public void transferFunds(Long fromAccount, Long toAccount, BigDecimal amount) {
    // Use transaction with proper isolation
    // Deduct from source account
    // Add to target account
    // Handle insufficient funds
    // Rollback on any error
}
```

---

### Exercise 3.2: Savepoints
**Task**: Use savepoints to implement partial rollback.

Scenario: Order processing with multiple steps
- Step 1: Reserve inventory
- Step 2: Process payment
- Step 3: Create shipping label

If step 3 fails, rollback to after step 2 (keep payment processed, release inventory).

```java
public void processOrderWithSavepoints(Order order) {
    // Implement three-step order processing
    // Use savepoint after step 2
    // Implement rollback to savepoint on step 3 failure
}
```

---

### Exercise 3.3: Nested Transactions
**Task**: Demonstrate transaction propagation behavior.

```java
public void outerTransaction() {
    // Start transaction
    // Call innerTransaction()
    // Check if inner changes are committed or rolled back
    // Based on propagation setting
}

public void innerTransaction() {
    // Try different propagation settings:
    // - REQUIRED (default)
    // - REQUIRES_NEW
    // - NESTED
}
```

---

## Exercise Set 4: Batch Operations

### Exercise 4.1: Batch Insert
**Task**: Insert 10,000 products using batch operations.

```java
public void batchInsert(List<Product> products) {
    // Use addBatch() and executeBatch()
    // Process in chunks of 1000
    // Measure and report execution time
    // Compare with individual inserts
}
```

**Bonus**: Track progress and handle partial failures.

---

### Exercise 4.2: Batch Update
**Task**: Update product prices based on a price adjustment factor.

```java
public void batchPriceUpdate(String category, double adjustmentFactor) {
    // Update all products in category
    // Apply adjustment factor to price
    // Handle zero or negative prices
}
```

---

### Exercise 4.3: Batch Delete
**Task**: Implement scheduled cleanup of old records.

```java
public int cleanupOldRecords(LocalDate cutoffDate) {
    // Delete records older than cutoff date
    // Return count of deleted records
    // Use batch delete for efficiency
}
```

---

## Exercise Set 5: Advanced Patterns

### Exercise 5.1: Custom RowMapper
**Task**: Create a RowMapper for a complex join query.

```java
public class OrderWithDetailsMapper implements RowMapper<Order> {
    // Map result of:
    // SELECT o.*, c.name as customer_name, c.email, 
    //        p.name as product_name, p.price
    // FROM orders o
    // JOIN customers c ON o.customer_id = c.id
    // JOIN order_items oi ON o.id = oi.order_id
    // JOIN products p ON oi.product_id = p.id
}
```

---

### Exercise 5.2: Stored Procedure Call
**Task**: Call a database stored procedure using CallableStatement.

```sql
-- Create procedure (run in database)
CREATE PROCEDURE get_monthly_sales(IN year INT, OUT total DECIMAL)
BEGIN
    SELECT SUM(amount) INTO total FROM orders 
    WHERE YEAR(order_date) = year;
END
```

```java
public BigDecimal callMonthlySalesProcedure(int year) {
    // Use CallableStatement
    // Register output parameter
    // Execute and retrieve result
}
```

---

### Exercise 5.3: Multiple Data Sources
**Task**: Configure and use multiple data sources.

Scenario:
- Primary database: Read/write operations
- Analytics database: Read-only operations

```java
public class MultiDataSourceConfig {
    // Configure two HikariCP data sources
    // Create separate JdbcTemplates
    // Implement routing logic
}
```

---

## Challenge Problems

### Challenge 1: Connection Pool Exhaustion Recovery
**Difficulty**: Advanced
**Task**: Implement automatic pool recovery when connections fail.

```java
public class ResilientDataSource {
    // Monitor pool health
    // Detect connection failures
    // Automatically recover pool
    // Circuit breaker pattern
}
```

---

### Challenge 2: Distributed Transaction
**Difficulty**: Expert
**Task**: Implement a two-phase commit across two databases.

```java
public class TwoPhaseCommit {
    // Phase 1: Prepare all resources
    // Phase 2: Commit or rollback all
    // Handle partial failures
}
```

---

### Challenge 3: Query Optimization
**Difficulty**: Advanced
**Task**: Optimize a slow query using EXPLAIN and indexes.

```java
public void optimizeSlowQuery() {
    // Identify slow query using logs
    // Run EXPLAIN ANALYZE
    // Add appropriate indexes
    // Verify improvement
}
```

---

## Solutions Guidance

For each exercise:
1. Attempt the exercise before checking solutions
2. Focus on understanding, not just copying
3. Compare your approach with production patterns
4. Consider edge cases and error handling

---

## Time Estimates

| Exercise | Estimated Time |
|----------|---------------|
| Set 1 | 2-3 hours |
| Set 2 | 1-2 hours |
| Set 3 | 2-3 hours |
| Set 4 | 1-2 hours |
| Set 5 | 3-4 hours |
| Challenges | 4+ hours each |

