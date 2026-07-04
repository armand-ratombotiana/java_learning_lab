# Refactoring: Query Optimization

## N+1 → JOIN FETCH

### Before
```java
// Service method
@Transactional(readOnly = true)
public List<Category> getCategoriesWithProducts() {
    List<Category> categories = categoryRepository.findAll();
    for (Category c : categories) {
        c.getProducts().size(); // triggers lazy load
    }
    return categories;
}
```

### After
```java
@Query("SELECT c FROM Category c JOIN FETCH c.products")
List<Category> findAllWithProducts();
```

## Multiple Queries → Single Query

### Before
```java
// 3 separate queries
User user = userRepository.findById(id);
List<Order> orders = orderRepository.findByUserId(id);
List<Review> reviews = reviewRepository.findByUserId(id);
```

### After
```java
// Single JPQL with JOIN FETCH
@Query("SELECT u FROM User u "
     + "JOIN FETCH u.orders "
     + "JOIN FETCH u.reviews "
     + "WHERE u.id = :id")
User findByIdWithOrdersAndReviews(@Param("id") Long id);
```

## Inefficient WHERE → Indexed Column

### Before
```sql
SELECT * FROM users WHERE LOWER(email) = 'test@example.com';
-- Can't use index on email (function wraps column)
```

### After
```sql
-- Create expression index
CREATE INDEX idx_users_lower_email ON users(LOWER(email));
SELECT * FROM users WHERE LOWER(email) = 'test@example.com';
```
