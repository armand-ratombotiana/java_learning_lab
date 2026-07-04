# Common Mistakes: Relational Databases

## 1. Missing Foreign Keys
```sql
-- WRONG: no relationship enforcement
CREATE TABLE orders (customer_id BIGINT);

-- RIGHT: referential integrity
CREATE TABLE orders (customer_id BIGINT REFERENCES customers(id));
```

## 2. Storing Comma-Separated Values (Violates 1NF)
```java
// WRONG: "1,2,3" in a VARCHAR column
private String productIds;

// RIGHT: junction table
@OneToMany
private List<OrderItem> items;
```

## 3. Not Using Transactions for Multi-Table Operations
```java
// WRONG: partial failure possible
orderRepo.save(order);
orderItemRepo.saveAll(items); // could fail after order saved

// RIGHT: transactional
@Transactional
public Order placeOrder(...) { ... }
```

## 4. SELECT N+1 in JPA
```java
// WRONG: N+1 queries (1 for orders + N for each customer)
List<Order> orders = orderRepo.findAll();
for (Order o : orders) {
    System.out.println(o.getCustomer().getName());
}

// RIGHT: use JOIN FETCH
@Query("SELECT o FROM Order o JOIN FETCH o.customer")
List<Order> findAllWithCustomer();
```

## 5. Ignoring Transaction Isolation
```java
// WRONG: default isolation may cause dirty reads
@Transactional
public void readUncommittedData() { ... }

// RIGHT: specify isolation level
@Transactional(isolation = Isolation.READ_COMMITTED)
```

## 6. Over-Normalization
Splitting tables unnecessarily – causes excessive joins.
Denormalize when read performance is critical (e.g., reporting).

## 7. Choosing Wrong Primary Key
- Natural keys (SSN, email) change or are sensitive
- UUIDs hurt index performance in B-trees (random vs sequential)
- Prefer sequential surrogate keys: `BIGSERIAL` / `IDENTITY`
