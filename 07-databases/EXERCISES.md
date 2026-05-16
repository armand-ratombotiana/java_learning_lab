# Databases Exercises

## Exercise 1: JDBC CRUD Operations

**Problem:** Implement a complete CRUD (Create, Read, Update, Delete) application using JDBC.

**Requirements:**
1. Use PreparedStatement for all operations
2. Handle resources properly with try-with-resources
3. Implement proper exception handling
4. Add batch operations for bulk inserts

**Solution:**

```java
// User.java
public class User {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    
    // Getters and setters
}

// UserDao.java
public class UserDao {
    
    private final DataSource dataSource;
    
    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public Long insert(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, created_at) VALUES (?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, 
                 Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            
            stmt.executeUpdate();
            
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            return null;
        }
    }
    
    public Optional<User> findById(Long id) throws SQLException {
        String sql = "SELECT id, name, email, created_at FROM users WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        }
    }
    
    public List<User> findAll() throws SQLException {
        String sql = "SELECT id, name, email, created_at FROM users ORDER BY id";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        }
        return users;
    }
    
    public List<User> findByNameContaining(String pattern) throws SQLException {
        String sql = "SELECT id, name, email, created_at FROM users WHERE name LIKE ?";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + pattern + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRow(rs));
                }
            }
        }
        return users;
    }
    
    public int update(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setLong(3, user.getId());
            
            return stmt.executeUpdate();
        }
    }
    
    public int delete(Long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate();
        }
    }
    
    public int batchInsert(List<User> users) throws SQLException {
        String sql = "INSERT INTO users (name, email, created_at) VALUES (?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (User user : users) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                stmt.addBatch();
            }
            
            int[] results = stmt.executeBatch();
            return results.length;
        }
    }
    
    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}
```

---

## Exercise 2: JPA Entity and Repository

**Problem:** Create JPA entities with relationships and repository interfaces.

**Requirements:**
1. Create entities with OneToMany and ManyToOne relationships
2. Implement custom queries with JPQL
3. Use pagination and sorting
4. Add transaction boundaries

**Solution:**

```java
// Author.java
@Entity
public class Author {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true)
    private String email;
    
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, 
               orphanRemoval = true)
    private List<Book> books = new ArrayList<>();
    
    // Constructors, getters, setters
}

// Book.java
@Entity
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(name = "isbn", unique = true)
    private String isbn;
    
    private BigDecimal price;
    
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;
    
    @ManyToMany
    @JoinTable(name = "book_categories",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();
    
    // Constructors, getters, setters
}

// Category.java
@Entity
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    // Constructors, getters, setters
}

// AuthorRepository.java
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    Optional<Author> findByEmail(String email);
    
    @Query("SELECT a FROM Author a WHERE a.name LIKE %:name%")
    List<Author> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.books WHERE a.id = :id")
    Optional<Author> findByIdWithBooks(@Param("id") Long id);
    
    @Query("SELECT a FROM Author a WHERE SIZE(a.books) > :count")
    List<Author> findAuthorsWithManyBooks(@Param("count") int count);
    
    @Query("SELECT a FROM Author a ORDER BY a.name")
    List<Author> findAllSorted();
}

// BookRepository.java
public interface BookRepository extends JpaRepository<Book, Long> {
    
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    Optional<Book> findByIsbn(String isbn);
    
    @Query("SELECT b FROM Book b WHERE b.author.id = :authorId")
    List<Book> findByAuthorId(@Param("authorId") Long authorId);
    
    @Query("SELECT b FROM Book b WHERE b.price BETWEEN :min AND :max")
    List<Book> findByPriceRange(@Param("min") BigDecimal min, 
                                @Param("max") BigDecimal max);
    
    @Query("SELECT b FROM Book b JOIN b.categories c WHERE c.name = :category")
    List<Book> findByCategoryName(@Param("category") String category);
    
    @Query("SELECT b FROM Book b ORDER BY b.title")
    Page<Book> findAllWithPageable(Pageable pageable);
    
    @Modifying
    @Query("UPDATE Book b SET b.price = b.price * :multiplier WHERE b.price < :threshold")
    int updatePrices(@Param("multiplier") BigDecimal multiplier, 
                     @Param("threshold") BigDecimal threshold);
}

// BookService.java
@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private AuthorRepository authorRepository;
    
    @Transactional
    public Book createBook(Book book, Long authorId) {
        Author author = authorRepository.findById(authorId)
            .orElseThrow(() -> new EntityNotFoundException("Author not found"));
        
        book.setAuthor(author);
        return bookRepository.save(book);
    }
    
    @Transactional(readOnly = true)
    public Page<Book> getBooks(int page, int size, String sortBy) {
        return bookRepository.findAllWithPageable(
            PageRequest.of(page, size, Sort.by(sortBy)));
    }
    
    @Transactional(readOnly = true)
    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }
}
```

---

## Exercise 3: Transaction Management

**Problem:** Implement transaction management with proper isolation levels and error handling.

**Solution:**

```java
// Account.java
@Entity
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String accountNumber;
    
    private BigDecimal balance;
    
    private String currency;
    
    @Version
    private Long version;
    
    // Getters and setters
}

// AccountRepository.java
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :number")
    Optional<Account> findByAccountNumberForUpdate(@Param("number") String number);
}

// AccountService.java
@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void transfer(String fromAccount, String toAccount, BigDecimal amount) {
        Account from = accountRepository.findByAccountNumberForUpdate(fromAccount)
            .orElseThrow(() -> new AccountNotFoundException(fromAccount));
        
        Account to = accountRepository.findByAccountNumberForUpdate(toAccount)
            .orElseThrow(() -> new AccountNotFoundException(toAccount));
        
        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient balance");
        }
        
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        
        accountRepository.save(from);
        accountRepository.save(to);
    }
    
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public BigDecimal getBalance(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .map(Account::getBalance)
            .orElse(BigDecimal.ZERO);
    }
    
    @Transactional
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }
}

// TransactionDemo.java
@Component
public class TransactionDemo {
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private PlatformTransactionManager txManager;
    
    public void demonstrateTransaction() {
        // Create test accounts
        Account acc1 = new Account();
        acc1.setAccountNumber("ACC001");
        acc1.setBalance(BigDecimal.valueOf(1000));
        acc1.setCurrency("USD");
        
        Account acc2 = new Account();
        acc2.setAccountNumber("ACC002");
        acc2.setBalance(BigDecimal.valueOf(500));
        acc2.setCurrency("USD");
        
        accountService.createAccount(acc1);
        accountService.createAccount(acc2);
        
        // Transfer with manual transaction
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        
        TransactionStatus status = txManager.getTransaction(definition);
        
        try {
            accountService.transfer("ACC001", "ACC002", BigDecimal.valueOf(200));
            txManager.commit(status);
        } catch (Exception e) {
            txManager.rollback(status);
            throw e;
        }
    }
}
```

---

## Exercise 4: Connection Pool Configuration

**Problem:** Configure HikariCP connection pool with monitoring and optimization.

**Solution:**

```java
// DataSourceConfig.java
@Configuration
public class DataSourceConfig {
    
    @Value("${db.url}")
    private String jdbcUrl;
    
    @Value("${db.username}")
    private String username;
    
    @Value("${db.password}")
    private String password;
    
    @Value("${db.driver}")
    private String driverClass;
    
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClass);
        
        // Pool sizing
        int cores = Runtime.getRuntime().availableProcessors();
        config.setMaximumPoolSize(cores * 2);
        config.setMinimumIdle(cores);
        
        // Timeouts (milliseconds)
        config.setConnectionTimeout(20000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(1200000);
        
        // Validation
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);
        
        // Performance
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        // Leak detection
        config.setLeakDetectionThreshold(60000);
        
        config.setPoolName("MainPool");
        
        return new HikariDataSource(config);
    }
}

// DatabaseHealthIndicator.java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    private final DataSource dataSource;
    
    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public Health health() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(5)) {
                HikariDataSource hikari = (HikariDataSource) dataSource;
                HikariPoolMXBean pool = hikari.getHikariPoolMXBean();
                
                return Health.up()
                    .withDetail("activeConnections", pool.getActiveConnections())
                    .withDetail("idleConnections", pool.getIdleConnections())
                    .withDetail("totalConnections", pool.getTotalConnections())
                    .withDetail("threadsAwaiting", pool.getThreadsAwaitingConnection())
                    .build();
            }
        } catch (Exception e) {
            return Health.down(e).build();
        }
        
        return Health.down().build();
    }
}

// application.yml
/*
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: user
    password: password
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 20000
      idle-timeout: 300000
      max-lifetime: 1200000
      leak-detection-threshold: 60000
      pool-name: MainPool
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
*/
```

---

## Exercise 5: Complex Queries and Performance

**Problem:** Write complex queries with JOINs, aggregations, and optimize for performance.

**Solution:**

```java
// OrderRepository.java
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query("SELECT o FROM Order o JOIN FETCH o.customer JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId")
    Page<Order> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);
    
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();
    
    @Query("SELECT c.name, COUNT(o) FROM Customer c LEFT JOIN c.orders o GROUP BY c.name ORDER BY COUNT(o) DESC")
    List<Object[]> getCustomerOrderCounts();
    
    @Query("SELECT o.customer.email, SUM(o.total) FROM Order o WHERE o.status = 'COMPLETED' GROUP BY o.customer.email ORDER BY SUM(o.total) DESC")
    List<Object[]> getTopCustomersBySpend();
    
    @Query("SELECT FUNCTION('DATE', o.createdAt), COUNT(o) FROM Order o WHERE o.createdAt >= :start GROUP BY FUNCTION('DATE', o.createdAt)")
    List<Object[]> getDailyOrderCounts(@Param("start") LocalDateTime start);
    
    @Query("SELECT p.name, SUM(oi.quantity) FROM OrderItem oi JOIN oi.product p WHERE oi.order.createdAt >= :start GROUP BY p.name ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> getTopSellingProducts(@Param("start") LocalDateTime start);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt < :cutoff")
    List<Order> findOrdersForCancellation(@Param("status") OrderStatus status, 
                                           @Param("cutoff") LocalDateTime cutoff);
    
    @Modifying
    @Query("UPDATE Order o SET o.status = 'CANCELLED' WHERE o.status = :status AND o.createdAt < :cutoff")
    int cancelOldOrders(@Param("status") OrderStatus status, @Param("cutoff") LocalDateTime cutoff);
}

// ReportService.java
@Service
public class ReportService {
    
    @PersistenceContext
    private EntityManager em;
    
    public List<Object[]> generateSalesReport(LocalDateTime startDate) {
        String sql = """
            SELECT 
                c.name as customer,
                c.email,
                COUNT(o.id) as order_count,
                SUM(o.total) as total_spent,
                AVG(o.total) as avg_order_value,
                MAX(o.created_at) as last_order
            FROM customers c
            LEFT JOIN orders o ON c.id = o.customer_id 
                AND o.created_at >= ?1
                AND o.status = 'COMPLETED'
            GROUP BY c.id, c.name, c.email
            ORDER BY total_spent DESC
            """;
        
        return em.createNativeQuery(sql)
            .setParameter(1, startDate)
            .getResultList();
    }
    
    public void generateReportFile(LocalDateTime startDate) {
        List<Object[]> sales = generateSalesReport(startDate);
        
        System.out.println("Customer Sales Report");
        System.out.println("=".repeat(80));
        System.out.printf("%-30s %-25s %-10s %-15s %-15s%n",
            "Customer", "Email", "Orders", "Total", "Avg Order");
        System.out.println("-".repeat(80));
        
        for (Object[] row : sales) {
            System.out.printf("%-30s %-25s %-10s %-15s %-15s%n",
                row[0], row[1], row[2], row[3], row[4]);
        }
    }
}

// Performance Optimization Example
@Service
public class OptimizedQueryService {
    
    @PersistenceContext
    private EntityManager em;
    
    // Use entity graphs to fetch related entities efficiently
    @EntityGraph(attributePaths = {"customer", "items", "items.product"})
    @Query("SELECT o FROM Order o")
    List<Order> findAllWithDetails();
    
    // Use projection instead of entity for read-only operations
    public interface OrderSummary {
        Long getId();
        String getCustomerName();
        BigDecimal getTotal();
        LocalDateTime getCreatedAt();
    }
    
    @Query("SELECT new map(o.id as id, c.name as customerName, o.total as total, o.createdAt as createdAt) FROM Order o JOIN o.customer c")
    List<Map<String, Object>> findOrderSummaries();
    
    // Pagination with keyset for better performance
    public List<Order> findAfterCursor(Long lastId, int limit) {
        return em.createQuery("SELECT o FROM Order o WHERE o.id > :lastId ORDER BY o.id")
            .setParameter("lastId", lastId)
            .setMaxResults(limit)
            .getResultList();
    }
}
```

---

## Summary

| Exercise | Key Concepts |
|----------|-------------|
| 1 | JDBC CRUD, PreparedStatement, try-with-resources, batch operations |
| 2 | JPA entities, relationships, repository queries, JPQL |
| 3 | Transactions, isolation levels, pessimistic locking |
| 4 | HikariCP configuration, pooling, monitoring |
| 5 | Complex queries, JOINs, aggregations, projections, entity graphs |