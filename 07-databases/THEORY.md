# Databases Theory

## Table of Contents
1. [JDBC](#jdbc)
2. [JPA](#jpa)
3. [Transactions](#transactions)
4. [Connection Pooling](#connection-pooling)

---

## 1. JDBC (Java Database Connectivity)

### 1.1 Introduction to JDBC

JDBC is the standard Java API for database connectivity. It provides:
- Connection management
- SQL execution
- Result set handling
- Transaction control

### 1.2 Basic JDBC Operations

```java
public class JdbcExample {
    
    public void basicQuery() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/mydb";
        String user = "user";
        String password = "password";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            
            String sql = "SELECT id, name, email FROM users WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, 1L);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Long id = rs.getLong("id");
                        String name = rs.getString("name");
                        String email = rs.getString("email");
                        System.out.println(id + ", " + name + ", " + email);
                    }
                }
            }
        }
    }
    
    public void insertData() throws SQLException {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, 
                 Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, "John Doe");
            stmt.setString(2, "john@example.com");
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        Long id = keys.getLong(1);
                        System.out.println("Generated ID: " + id);
                    }
                }
            }
        }
    }
    
    public void updateData() throws SQLException {
        String sql = "UPDATE users SET email = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "newemail@example.com");
            stmt.setLong(2, 1L);
            
            int updated = stmt.executeUpdate();
            System.out.println("Rows updated: " + updated);
        }
    }
    
    public void deleteData() throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, 1L);
            int deleted = stmt.executeUpdate();
            System.out.println("Rows deleted: " + deleted);
        }
    }
}
```

### 1.3 Transaction Management

```java
public class TransactionExample {
    
    public void transferFunds(Connection conn, long fromId, long toId, 
                             BigDecimal amount) throws SQLException {
        
        conn.setAutoCommit(false);
        
        try {
            String debitSql = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
            String creditSql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
            
            try (PreparedStatement debit = conn.prepareStatement(debitSql);
                 PreparedStatement credit = conn.prepareStatement(creditSql)) {
                
                debit.setBigDecimal(1, amount);
                debit.setLong(2, fromId);
                debit.executeUpdate();
                
                credit.setBigDecimal(1, amount);
                credit.setLong(2, toId);
                credit.executeUpdate();
            }
            
            conn.commit();
            
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
```

### 1.4 Batch Operations

```java
public class BatchExample {
    
    public void batchInsert(List<User> users) throws SQLException {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (User user : users) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                stmt.addBatch();
            }
            
            int[] results = stmt.executeBatch();
            System.out.println("Inserted: " + results.length + " rows");
        }
    }
    
    public void batchUpdate(List<User> users) throws SQLException {
        String sql = "UPDATE users SET email = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (User user : users) {
                stmt.setString(1, user.getEmail());
                stmt.setLong(2, user.getId());
                stmt.addBatch();
            }
            
            int[] results = stmt.executeBatch();
            
            for (int i = 0; i < results.length; i++) {
                if (results[i] == Statement.SUCCESS_NO_INFO) {
                    System.out.println("Batch " + i + ": No info");
                } else {
                    System.out.println("Batch " + i + ": " + results[i]);
                }
            }
        }
    }
}
```

---

## 2. JPA (Java Persistence API)

### 2.1 Entity Classes

```java
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_created", columnList = "created_at")
})
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, 
               orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    
    // Getters and setters
}
```

### 2.2 Repositories

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    List<User> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailCustom(@Param("email") String email);
    
    @Query(value = "SELECT * FROM users WHERE name LIKE ?1%", nativeQuery = true)
    List<User> findByNameLikeNative(String namePattern);
    
    @Query("SELECT u FROM User u WHERE u.active = true ORDER BY u.createdAt DESC")
    Page<User> findActiveUsers(Pageable pageable);
    
    @Modifying
    @Query("UPDATE User u SET u.active = false WHERE u.lastLogin < :date")
    int deactivateInactiveUsers(@Param("date") LocalDate date);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :role")
    List<User> findByRoleName(@Param("role") String roleName);
}
```

### 2.3 Entity Manager Operations

```java
@Service
public class UserService {
    
    @PersistenceContext
    private EntityManager em;
    
    @Transactional
    public User createUser(User user) {
        em.persist(user);
        return user;
    }
    
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return em.find(User.class, id);
    }
    
    @Transactional
    public User updateUser(Long id, User updated) {
        User user = em.find(User.class, id);
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }
        
        user.setName(updated.getName());
        user.setEmail(updated.getEmail());
        
        return em.merge(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        User user = em.getReference(User.class, id);
        em.remove(user);
    }
    
    @Transactional
    public void addRole(Long userId, Role role) {
        User user = em.find(User.class, userId);
        user.getRoles().add(role);
        role.getUsers().add(user);
    }
}
```

---

## 3. Transactions

### 3.1 Transaction Basics

```java
@Service
public class OrderService {
    
    @PersistenceContext
    private EntityManager em;
    
    @Transactional(propagation = Propagation.REQUIRED)
    public Order createOrder(Order order) {
        em.persist(order);
        
        // Update inventory
        for (OrderItem item : order.getItems()) {
            item.getProduct().decrementStock(item.getQuantity());
        }
        
        return order;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendConfirmationEmail(Long orderId) {
        // Runs in separate transaction
        Order order = em.find(Order.class, orderId);
        emailService.send(order.getCustomerEmail(), "Order confirmed");
    }
    
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Order> getUserOrders(Long userId) {
        return em.createQuery("SELECT o FROM Order o WHERE o.user.id = :id", 
            Order.class)
            .setParameter("id", userId)
            .getResultList();
    }
}
```

### 3.2 Isolation Levels

```java
@Transactional(isolation = Isolation.READ_COMMITTED)
public void processData() {
    // Can read committed data from other transactions
    // Prevents dirty reads
}

@Transactional(isolation = Isolation.SERIALIZABLE)
public void updateAccount(Long accountId, BigDecimal amount) {
    // Highest isolation - prevents all concurrency issues
    // But may cause lock contention
}
```

### 3.3 Optimistic Locking

```java
@Entity
public class Product {
    
    @Id
    private Long id;
    
    private String name;
    
    private int stock;
    
    @Version
    private Long version;
}

@Service
public class InventoryService {
    
    @PersistenceContext
    private EntityManager em;
    
    @Transactional
    public void reserveStock(Long productId, int quantity) {
        Product product = em.find(Product.class, productId);
        
        if (product.getStock() < quantity) {
            throw new InsufficientStockException();
        }
        
        product.setStock(product.getStock() - quantity);
        
        // If another transaction modified this, 
        // OptimisticLockException is thrown
    }
}
```

### 3.4 Pessimistic Locking

```java
@Service
public class AccountService {
    
    @PersistenceContext
    private EntityManager em;
    
    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        Account from = em.find(Account.class, fromId, 
            LockModeType.PESSIMISTIC_WRITE);
        
        Account to = em.find(Account.class, toId, 
            LockModeType.PESSIMISTIC_WRITE);
        
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
    }
}
```

---

## 4. Connection Pooling

### 4.1 HikariCP Configuration

```java
@Configuration
public class DataSourceConfig {
    
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
        config.setUsername("user");
        config.setPassword("password");
        config.setDriverClassName("org.postgresql.Driver");
        
        // Pool configuration
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(300000);  // 5 minutes
        config.setConnectionTimeout(10000);  // 10 seconds
        config.setMaxLifetime(1800000);  // 30 minutes
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("MainPool");
        
        return new HikariDataSource(config);
    }
}
```

### 4.2 Spring Boot Configuration

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: user
    password: password
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
      idle-timeout: 300000
      connection-timeout: 10000
      max-lifetime: 1800000
      pool-name: MainPool
    hikari:
      maximum-pool-size: 10
```

### 4.3 Monitoring Connection Pool

```java
@Service
public class PoolMonitor {
    
    @Autowired
    private DataSource dataSource;
    
    public void printPoolStats() {
        if (dataSource instanceof HikariDataSource hikari) {
            HikariPoolMXBean pool = hikari.getHikariPoolMXBean();
            
            System.out.println("Active: " + pool.getActiveConnections());
            System.out.println("Idle: " + pool.getIdleConnections());
            System.out.println("Waiting: " + pool.getThreadsAwaitingConnection());
            System.out.println("Total: " + pool.getTotalConnections());
        }
    }
}
```

### 4.4 Connection Pool with JNDI

```java
@Configuration
public class JndiConfig {
    
    @Bean
    public DataSource dataSource() {
        JndiTemplate jndi = new JndiTemplate();
        return (DataSource) jndi.lookup("java:comp/env/jdbc/myDB");
    }
}
```

---

## Key Concepts Summary

| Concept | Description |
|---------|-------------|
| JDBC | Low-level database API |
| JPA | ORM abstraction layer |
| Entity | Java object mapped to database table |
| Repository | Data access abstraction |
| Transaction | ACID operations |
| Isolation | Transaction concurrency control |
| Connection Pool | Reuses database connections |
| HikariCP | Fast JDBC connection pool |