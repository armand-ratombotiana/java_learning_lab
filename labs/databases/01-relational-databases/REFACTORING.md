# Refactoring: Improving Database Schema and Code

## Problem 1: Unnormalized Schema → 3NF

### Before (1NF violation)
```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    customer_name VARCHAR(200),
    customer_email VARCHAR(320),
    product_names TEXT,  -- "Widget,Gadget,Doohickey"
    product_quantities VARCHAR(100)  -- "3,1,2"
);
```

### After (3NF)
```sql
CREATE TABLE customers (id BIGSERIAL PRIMARY KEY, name VARCHAR(200), email VARCHAR(320));
CREATE TABLE products (id BIGSERIAL PRIMARY KEY, name VARCHAR(200));
CREATE TABLE orders (id BIGSERIAL PRIMARY KEY, customer_id BIGINT REFERENCES customers(id));
CREATE TABLE order_items (id BIGSERIAL PRIMARY KEY, order_id BIGINT REFERENCES orders(id), product_id BIGINT REFERENCES products(id), quantity INT);
```

## Problem 2: N+1 Queries in JPA → JOIN FETCH

### Before
```java
@Query("SELECT o FROM Order o")
List<Order> findAllOrders(); // N+1 on o.customer
```

### After
```java
@Query("SELECT o FROM Order o JOIN FETCH o.customer")
List<Order> findAllOrdersWithCustomer();
```

## Problem 3: Raw JDBC → Spring Data JPA

### Before (JDBC with manual mapping)
```java
List<Customer> customers = new ArrayList<>();
try (Statement stmt = conn.createStatement();
     ResultSet rs = stmt.executeQuery("SELECT * FROM customers")) {
    while (rs.next()) {
        Customer c = new Customer();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        customers.add(c);
    }
}
```

### After (Spring Data JPA)
```java
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByName(String name);
}
```

## Problem 4: Sequential UUID PK → Snowflake ID

### Before
```java
@Id
private UUID id; // bad for B-tree index performance
```

### After
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

## Problem 5: Multiple Small Queries → Batch

### Before
```java
for (Product p : products) {
    jdbcTemplate.update("INSERT INTO products VALUES (?,?)", p.getId(), p.getName());
}
```

### After
```java
jdbcTemplate.batchUpdate("INSERT INTO products VALUES (?,?)",
    products.stream().map(p -> new Object[]{p.getId(), p.getName()}).toList());
```
