# Database Projects

This directory contains projects focused on database concepts, JDBC, JPA, Hibernate, and database design patterns. These projects help you master data persistence, ORM, transactions, and database optimization in Java applications.

## Mini-Project: JDBC Transaction Manager (2-4 hours)

### Overview

Build a robust JDBC transaction manager demonstrating proper transaction handling, connection pooling, and resource management. This project teaches database transaction fundamentals including ACID properties and isolation levels.

### Project Structure

```
jdbc-transaction-manager/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── databases/
                    ├── TransactionManager.java
                    ├── ConnectionPool.java
                    ├── DataAccessObject.java
                    ├── entity/
                    │   ├── Account.java
                    │   └── Transaction.java
                    └── service/
                        └── BankService.java
```

### Implementation

```java
package com.databases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TransactionManager {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);
    
    private final ConnectionPool connectionPool;
    private final ThreadLocal<Connection> currentConnection = new ThreadLocal<>();

    public TransactionManager(String url, String username, String password) {
        this.connectionPool = new ConnectionPool(url, username, password, 10);
    }

    public TransactionManager(String url, Properties props) {
        this.connectionPool = new ConnectionPool(url, props, 10);
    }

    public void beginTransaction() throws SQLException {
        Connection connection = connectionPool.getConnection();
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        currentConnection.set(connection);
        logger.debug("Transaction started on thread {}", Thread.currentThread().getId());
    }

    public void commit() throws SQLException {
        Connection connection = currentConnection.get();
        if (connection != null) {
            connection.commit();
            logger.debug("Transaction committed on thread {}", Thread.currentThread().getId());
            releaseConnection();
        }
    }

    public void rollback() {
        Connection connection = currentConnection.get();
        if (connection != null) {
            try {
                connection.rollback();
                logger.debug("Transaction rolled back on thread {}", Thread.currentThread().getId());
            } catch (SQLException e) {
                logger.error("Error during rollback", e);
            } finally {
                releaseConnection();
            }
        }
    }

    public Connection getConnection() throws SQLException {
        Connection connection = currentConnection.get();
        if (connection == null) {
            throw new IllegalStateException("No active transaction. Call beginTransaction() first.");
        }
        return connection;
    }

    private void releaseConnection() {
        Connection connection = currentConnection.get();
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("Error resetting connection", e);
            }
            connectionPool.releaseConnection(connection);
            currentConnection.remove();
        }
    }

    public void executeInTransaction(TransactionCallback callback) {
        try {
            beginTransaction();
            callback.execute();
            commit();
        } catch (Exception e) {
            rollback();
            throw new RuntimeException("Transaction failed", e);
        }
    }

    public void shutdown() {
        connectionPool.shutdown();
    }
}

interface TransactionCallback {
    void execute() throws SQLException;
}
```

```java
package com.databases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Properties;
import java.util.Queue;

public class ConnectionPool {
    
    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
    
    private final String url;
    private final Properties props;
    private final int maxSize;
    
    private final Queue<Connection> availableConnections = new ArrayDeque<>();
    private final Queue<Connection> usedConnections = new ArrayDeque<>();
    
    private volatile boolean isShutdown = false;
    private int currentSize = 0;

    public ConnectionPool(String url, String username, String password, int maxSize) {
        this.url = url;
        this.props = new Properties();
        this.props.setProperty("user", username);
        this.props.setProperty("password", password);
        this.maxSize = maxSize;
        
        initializePool();
    }

    public ConnectionPool(String url, Properties props, int maxSize) {
        this.url = url;
        this.props = props;
        this.maxSize = maxSize;
        
        initializePool();
    }

    private void initializePool() {
        try {
            for (int i = 0; i < Math.min(maxSize, 3); i++) {
                Connection conn = createConnection();
                availableConnections.add(conn);
                currentSize++;
            }
            logger.info("Connection pool initialized with {} connections", currentSize);
        } catch (SQLException e) {
            logger.error("Failed to initialize connection pool", e);
        }
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, props);
    }

    public synchronized Connection getConnection() throws SQLException {
        if (isShutdown) {
            throw new SQLException("Connection pool is shutdown");
        }
        
        Connection connection = availableConnections.poll();
        
        if (connection == null) {
            if (currentSize < maxSize) {
                connection = createConnection();
                currentSize++;
                logger.debug("Created new connection. Pool size: {}", currentSize);
            } else {
                throw new SQLException("No available connections in pool");
            }
        }
        
        if (!isValid(connection)) {
            connection = createConnection();
            logger.debug("Replaced invalid connection");
        }
        
        usedConnections.add(connection);
        return connection;
    }

    private boolean isValid(Connection connection) {
        try {
            return connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }

    public synchronized void releaseConnection(Connection connection) {
        if (connection == null) return;
        
        usedConnections.remove(connection);
        
        if (!isShutdown && isValid(connection)) {
            availableConnections.add(connection);
        } else {
            try {
                connection.close();
                currentSize--;
                logger.debug("Closed connection. Pool size: {}", currentSize);
            } catch (SQLException e) {
                logger.error("Error closing connection", e);
            }
        }
    }

    public synchronized void shutdown() {
        isShutdown = true;
        
        closeConnections(availableConnections);
        closeConnections(usedConnections);
        
        logger.info("Connection pool shutdown. Connections closed: {}", currentSize);
    }

    private void closeConnections(Queue<Connection> connections) {
        Connection conn;
        while ((conn = connections.poll()) != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("Error closing connection", e);
            }
        }
    }

    public int getAvailableCount() { return availableConnections.size(); }
    public int getUsedCount() { return usedConnections.size(); }
    public int getTotalCount() { return currentSize; }
}
```

```java
package com.databases;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataAccessObject<T> {
    
    private final TransactionManager transactionManager;
    private final RowMapper<T> rowMapper;

    public DataAccessObject(TransactionManager transactionManager, RowMapper<T> rowMapper) {
        this.transactionManager = transactionManager;
        this.rowMapper = rowMapper;
    }

    public int executeUpdate(String sql, Object... params) throws SQLException {
        Connection conn = transactionManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, params);
            return stmt.executeUpdate();
        }
    }

    public Optional<T> executeQuerySingle(String sql, Object... params) throws SQLException {
        List<T> results = executeQuery(sql, params);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<T> executeQuery(String sql, Object... params) throws SQLException {
        Connection conn = transactionManager.getConnection();
        
        List<T> results = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, params);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(rowMapper.mapRow(rs));
                }
            }
        }
        
        return results;
    }

    public <K> K executeScalar(String sql, Class<K> type, Object... params) throws SQLException {
        Connection conn = transactionManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, params);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return type.cast(rs.getObject(1));
                }
            }
        }
        
        return null;
    }

    public long insertAndGetId(String sql, Object... params) throws SQLException {
        Connection conn = transactionManager.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(stmt, params);
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        
        throw new SQLException("Failed to retrieve generated ID");
    }

    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
}

interface RowMapper<T> {
    T mapRow(ResultSet rs) throws SQLException;
}
```

```java
package com.databases.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account {
    
    private Long id;
    private String accountNumber;
    private String accountHolder;
    private BigDecimal balance;
    private String accountType;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Account() {
    }

    public Account(String accountNumber, String accountHolder, BigDecimal initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
        this.accountType = "SAVINGS";
        this.active = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountHolder() { return accountHolder; }
    public void setAccountHolder(String accountHolder) { this.accountHolder = accountHolder; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
```

```java
package com.databases.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    
    private Long id;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private String transactionType;
    private String status;
    private String description;
    private LocalDateTime createdAt;

    public Transaction() {
    }

    public Transaction(Long fromAccountId, Long toAccountId, BigDecimal amount, String type) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.transactionType = type;
        this.status = "COMPLETED";
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(Long fromAccountId) { this.fromAccountId = fromAccountId; }

    public Long getToAccountId() { return toAccountId; }
    public void setToAccountId(Long toAccountId) { this.toAccountId = toAccountId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
```

```java
package com.databases.service;

import com.databases.*;
import com.databases.entity.Account;
import com.databases.entity.Transaction;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BankService {
    
    private final TransactionManager transactionManager;
    private final DataAccessObject<Account> accountDao;
    private final DataAccessObject<Transaction> transactionDao;

    public BankService(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.accountDao = new DataAccessObject<>(transactionManager, new AccountMapper());
        this.transactionDao = new DataAccessObject<>(transactionManager, new TransactionMapper());
    }

    public void createAccount(Account account) {
        transactionManager.executeInTransaction(() -> {
            String sql = """
                INSERT INTO accounts (account_number, account_holder, balance, 
                    account_type, active, created_at) 
                VALUES (?, ?, ?, ?, ?, ?)
                """;
            accountDao.executeUpdate(sql,
                account.getAccountNumber(),
                account.getAccountHolder(),
                account.getBalance(),
                account.getAccountType(),
                account.isActive(),
                LocalDateTime.now()
            );
        });
    }

    public Optional<Account> getAccount(Long id) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        return accountDao.executeQuerySingle(sql, id);
    }

    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        transactionManager.executeInTransaction(() -> {
            Optional<Account> fromAccount = accountDao.executeQuerySingle(
                "SELECT * FROM accounts WHERE id = ? FOR UPDATE", fromAccountId);
            
            Optional<Account> toAccount = accountDao.executeQuerySingle(
                "SELECT * FROM accounts WHERE id = ? FOR UPDATE", toAccountId);
            
            if (fromAccount.isEmpty() || toAccount.isEmpty()) {
                throw new IllegalArgumentException("Account not found");
            }
            
            Account from = fromAccount.get();
            Account to = toAccount.get();
            
            if (from.getBalance().compareTo(amount) < 0) {
                throw new IllegalStateException("Insufficient balance");
            }
            
            accountDao.executeUpdate(
                "UPDATE accounts SET balance = balance - ?, updated_at = ? WHERE id = ?",
                amount, LocalDateTime.now(), fromAccountId
            );
            
            accountDao.executeUpdate(
                "UPDATE accounts SET balance = balance + ?, updated_at = ? WHERE id = ?",
                amount, LocalDateTime.now(), toAccountId
            );
            
            Transaction tx = new Transaction(fromAccountId, toAccountId, amount, "TRANSFER");
            transactionDao.executeUpdate("""
                INSERT INTO transactions (from_account_id, to_account_id, amount, 
                    transaction_type, status, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                tx.getFromAccountId(),
                tx.getToAccountId(),
                tx.getAmount(),
                tx.getTransactionType(),
                tx.getStatus(),
                tx.getCreatedAt()
            );
            
            System.out.println("Transfer completed: " + amount + " from account " + 
                fromAccountId + " to " + toAccountId);
        });
    }

    public List<Transaction> getTransactionHistory(Long accountId) throws SQLException {
        String sql = """
            SELECT * FROM transactions 
            WHERE from_account_id = ? OR to_account_id = ?
            ORDER BY created_at DESC
            """;
        return transactionDao.executeQuery(sql, accountId, accountId);
    }

    static class AccountMapper implements RowMapper<Account> {
        @Override
        public Account mapRow(ResultSet rs) throws SQLException {
            Account account = new Account();
            account.setId(rs.getLong("id"));
            account.setAccountNumber(rs.getString("account_number"));
            account.setAccountHolder(rs.getString("account_holder"));
            account.setBalance(rs.getBigDecimal("balance"));
            account.setAccountType(rs.getString("account_type"));
            account.setActive(rs.getBoolean("active"));
            account.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            account.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return account;
        }
    }

    static class TransactionMapper implements RowMapper<Transaction> {
        @Override
        public Transaction mapRow(ResultSet rs) throws SQLException {
            Transaction tx = new Transaction();
            tx.setId(rs.getLong("id"));
            tx.setFromAccountId(rs.getLong("from_account_id"));
            tx.setToAccountId(rs.getLong("to_account_id"));
            tx.setAmount(rs.getBigDecimal("amount"));
            tx.setTransactionType(rs.getString("transaction_type"));
            tx.setStatus(rs.getString("status"));
            tx.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return tx;
        }
    }
}
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.databases</groupId>
    <artifactId>jdbc-transaction-manager</artifactId>
    <version>1.0.0</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.7.1</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.4.11</version>
        </dependency>
    </dependencies>
</project>
```

### Build and Run

```bash
cd jdbc-transaction-manager
mvn clean compile

# Create tables first
# Then run: java -cp target/classes com.databases.BankApplication
```

---

## Real-World Project: Multi-Tenant E-commerce Database System (8+ hours)

### Overview

Build a sophisticated multi-tenant database system for e-commerce with advanced patterns including schema separation, row-level security, tenant-specific configurations, complex queries, and performance optimization. This project demonstrates enterprise database architecture.

### Implementation

```java
package com.databases.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EntityScan(basePackages = {"com.databases.ecommerce.entity"})
@EnableJpaRepositories(basePackages = {"com.databases.ecommerce.repository"})
@EnableTransactionManagement
public class EcommerceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }
}
```

```java
package com.databases.ecommerce.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tenants")
public class Tenant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tenantId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String domain;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TenantStatus status;

    @Column(name = "max_products")
    private Integer maxProducts = 1000;

    @Column(name = "max_storage_mb")
    private Long maxStorageMb = 1024L;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<TenantConfig> configs = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "subscription_expires_at")
    private LocalDateTime subscriptionExpiresAt;

    public Tenant() {
    }

    public Tenant(String tenantId, String name, String domain) {
        this.tenantId = tenantId;
        this.name = name;
        this.domain = domain;
        this.status = TenantStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public boolean isSubscriptionActive() {
        return status == TenantStatus.ACTIVE && 
               (subscriptionExpiresAt == null || subscriptionExpiresAt.isAfter(LocalDateTime.now()));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public TenantStatus getStatus() { return status; }
    public void setStatus(TenantStatus status) { this.status = status; }

    public Integer getMaxProducts() { return maxProducts; }
    public void setMaxProducts(Integer maxProducts) { this.maxProducts = maxProducts; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

enum TenantStatus {
    ACTIVE, SUSPENDED, EXPIRED, CANCELLED
}
```

```java
package com.databases.ecommerce.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "external_id")
    private Long externalId;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(nullable = false)
    private String category;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public Product() {
    }

    public Product(Tenant tenant, String sku, String name, BigDecimal price) {
        this.tenant = tenant;
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public Long getExternalId() { return externalId; }
    public void setExternalId(Long externalId) { this.externalId = externalId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
```

```java
package com.databases.ecommerce.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "external_id")
    private Long externalId;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(length = 500)
    private String shippingAddress;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public Order() {
    }

    public Order(Tenant tenant, String orderNumber, Customer customer) {
        this.tenant = tenant;
        this.orderNumber = orderNumber;
        this.customer = customer;
        this.status = OrderStatus.PENDING;
        this.totalAmount = BigDecimal.ZERO;
        this.paymentStatus = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        calculateTotal();
    }

    public void calculateTotal() {
        totalAmount = items.stream()
            .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public Long getExternalId() { return externalId; }
    public void setExternalId(Long externalId) { this.externalId = externalId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { 
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
}

enum OrderStatus {
    PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
}

enum PaymentStatus {
    PENDING, AUTHORIZED, CAPTURED, FAILED, REFUNDED
}

@Entity
class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

@Entity
class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
```

```java
package com.databases.ecommerce.repository;

import com.databases.ecommerce.entity.Product;
import com.databases.ecommerce.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    @Query("SELECT p FROM Product p WHERE p.tenant.tenantId = :tenantId AND p.id = :id")
    Optional<Product> findByTenantIdAndId(@Param("tenantId") String tenantId, @Param("id") Long id);

    @Query("SELECT p FROM Product p WHERE p.tenant.tenantId = :tenantId AND p.sku = :sku")
    Optional<Product> findByTenantIdAndSku(@Param("tenantId") String tenantId, @Param("sku") String sku);

    @Query("SELECT p FROM Product p WHERE p.tenant.tenantId = :tenantId AND p.category = :category")
    List<Product> findByTenantIdAndCategory(@Param("tenantId") String tenantId, @Param("category") String category);

    @Query("SELECT p FROM Product p WHERE p.tenant.tenantId = :tenantId AND p.active = true")
    Page<Product> findActiveProducts(@Param("tenantId") String tenantId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.tenant.tenantId = :tenantId AND " +
           "p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByTenantIdAndPriceRange(@Param("tenantId") String tenantId,
                                               @Param("minPrice") BigDecimal minPrice,
                                               @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT p FROM Product p WHERE p.tenant.tenantId = :tenantId AND " +
           "p.name LIKE %:name%")
    List<Product> searchByName(@Param("tenantId") String tenantId, @Param("name") String name);

    @Query("SELECT p.category, COUNT(p) FROM Product p WHERE p.tenant.tenantId = :tenantId GROUP BY p.category")
    List<Object[]> countByCategory(@Param("tenantId") String tenantId);

    @Query("SELECT p FROM Product p WHERE p.tenant.tenantId = :tenantId AND p.stock < :threshold")
    List<Product> findLowStockProducts(@Param("tenantId") String tenantId, @Param("threshold") Integer threshold);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.tenant.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT AVG(p.price) FROM Product p WHERE p.tenant.tenantId = :tenantId")
    BigDecimal averagePriceByTenantId(@Param("tenantId") String tenantId);
}
```

```java
package com.databases.ecommerce.repository;

import com.databases.ecommerce.entity.Order;
import com.databases.ecommerce.entity.OrderStatus;
import com.databases.ecommerce.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query("SELECT o FROM Order o WHERE o.tenant.tenantId = :tenantId AND o.id = :id")
    Optional<Order> findByTenantIdAndId(@Param("tenantId") String tenantId, @Param("id") Long id);

    @Query("SELECT o FROM Order o WHERE o.tenant.tenantId = :tenantId AND o.orderNumber = :orderNumber")
    Optional<Order> findByTenantIdAndOrderNumber(@Param("tenantId") String tenantId, @Param("orderNumber") String orderNumber);

    @Query("SELECT o FROM Order o WHERE o.tenant.tenantId = :tenantId AND o.customer.id = :customerId")
    List<Order> findByTenantIdAndCustomerId(@Param("tenantId") String tenantId, @Param("customerId") Long customerId);

    @Query("SELECT o FROM Order o WHERE o.tenant.tenantId = :tenantId AND o.status = :status")
    Page<Order> findByTenantIdAndStatus(@Param("tenantId") String tenantId, @Param("status") OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.tenant.tenantId = :tenantId AND o.createdAt >= :startDate AND o.createdAt <= :endDate")
    List<Order> findByTenantIdAndDateRange(@Param("tenantId") String tenantId, 
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o.status, COUNT(o) FROM Order o WHERE o.tenant.tenantId = :tenantId GROUP BY o.status")
    List<Object[]> countByStatus(@Param("tenantId") String tenantId);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.tenant.tenantId = :tenantId")
    java.math.BigDecimal sumTotalAmountByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT o FROM Order o WHERE o.tenant.tenantId = :tenantId AND o.createdAt >= :startDate")
    List<Order> findRecentOrders(@Param("tenantId") String tenantId, @Param("startDate") LocalDateTime startDate);
}
```

```java
package com.databases.ecommerce.service;

import com.databases.ecommerce.entity.*;
import com.databases.ecommerce.repository.ProductRepository;
import com.databases.ecommerce.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TenantService {
    
    private final TenantRepository tenantRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final TenantContext tenantContext;

    public TenantService(TenantRepository tenantRepository,
                        ProductRepository productRepository,
                        OrderRepository orderRepository,
                        TenantContext tenantContext) {
        this.tenantRepository = tenantRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.tenantContext = tenantContext;
    }

    @Transactional
    public Tenant createTenant(Tenant tenant) {
        if (tenantRepository.findByTenantId(tenant.getTenantId()).isPresent()) {
            throw new IllegalArgumentException("Tenant ID already exists: " + tenant.getTenantId());
        }
        return tenantRepository.save(tenant);
    }

    public Optional<Tenant> getTenant(String tenantId) {
        return tenantRepository.findByTenantId(tenantId);
    }

    public List<Product> getProducts(String tenantId, Pageable pageable) {
        tenantContext.setCurrentTenant(tenantId);
        return productRepository.findActiveProducts(tenantId, pageable).getContent();
    }

    public Optional<Product> getProduct(String tenantId, Long productId) {
        return productRepository.findByTenantIdAndId(tenantId, productId);
    }

    @Transactional
    public Product createProduct(String tenantId, Product product) {
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));
        
        if (!tenant.isSubscriptionActive()) {
            throw new IllegalStateException("Tenant subscription is not active");
        }
        
        long currentCount = productRepository.countByTenantId(tenantId);
        if (currentCount >= tenant.getMaxProducts()) {
            throw new IllegalStateException("Maximum product limit reached for tenant");
        }
        
        product.setTenant(tenant);
        return productRepository.save(product);
    }

    public List<Order> getOrders(String tenantId, OrderStatus status, Pageable pageable) {
        return orderRepository.findByTenantIdAndStatus(tenantId, status, pageable).getContent();
    }

    public TenantAnalytics getTenantAnalytics(String tenantId) {
        long productCount = productRepository.countByTenantId(tenantId);
        BigDecimal avgPrice = productRepository.averagePriceByTenantId(tenantId);
        
        List<Object[]> categoryCounts = productRepository.countByTenantId(tenantId);
        
        List<Product> lowStock = productRepository.findLowStockProducts(tenantId, 10);
        
        BigDecimal totalRevenue = orderRepository.sumTotalAmountByTenantId(tenantId);
        
        List<Object[]> orderStatusCounts = orderRepository.countByStatus(tenantId);
        
        return new TenantAnalytics(
            productCount,
            avgPrice,
            categoryCounts.size(),
            lowStock.size(),
            totalRevenue != null ? totalRevenue : BigDecimal.ZERO,
            orderStatusCounts
        );
    }

    public void validateTenantAccess(String tenantId) {
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        if (!tenant.isSubscriptionActive()) {
            throw new IllegalStateException("Tenant subscription is not active");
        }
    }
}

record TenantAnalytics(
    long productCount,
    BigDecimal averagePrice,
    int categoryCount,
    int lowStockCount,
    BigDecimal totalRevenue,
    List<Object[]> orderStatusCounts
) {}

class TenantContext {
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    
    public void setCurrentTenant(String tenantId) {
        currentTenant.set(tenantId);
    }
    
    public String getCurrentTenant() {
        return currentTenant.get();
    }
    
    public void clear() {
        currentTenant.remove();
    }
}

interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByTenantId(String tenantId);
}
```

```java
package com.databases.ecommerce.service;

import com.databases.ecommerce.entity.*;
import com.databases.ecommerce.repository.ProductRepository;
import com.databases.ecommerce.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final TenantService tenantService;

    public OrderService(OrderRepository orderRepository,
                       ProductRepository productRepository,
                       TenantService tenantService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.tenantService = tenantService;
    }

    @Transactional
    public Order createOrder(String tenantId, Long customerId, OrderRequest request) {
        tenantService.validateTenantAccess(tenantId);
        
        Customer customer = new Customer();
        customer.setName(request.customerName());
        customer.setEmail(request.customerEmail());
        
        Tenant tenant = tenantService.getTenant(tenantId).orElseThrow();
        customer.setTenant(tenant);
        
        Order order = new Order(tenant, generateOrderNumber(), customer);
        order.setShippingAddress(request.shippingAddress());
        
        for (var itemRequest : request.items()) {
            Product product = productRepository.findByTenantIdAndId(tenantId, itemRequest.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            
            if (product.getStock() < itemRequest.quantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }
            
            product.setStock(product.getStock() - itemRequest.quantity());
            productRepository.save(product);
            
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemRequest.quantity());
            item.setPrice(product.getPrice());
            order.addItem(item);
        }
        
        return orderRepository.save(order);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Order updateOrderStatus(String tenantId, Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findByTenantIdAndId(tenantId, orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);
        
        if (newStatus == OrderStatus.CANCELLED) {
            restoreProductStock(order);
        }
        
        return orderRepository.save(order);
    }

    private void restoreProductStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

record OrderRequest(
    String customerName,
    String customerEmail,
    String shippingAddress,
    java.util.List<OrderItemRequest> items
) {}

record OrderItemRequest(
    Long productId,
    Integer quantity
) {}
```

### application.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommerce
    username: postgres
    password: password
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
    open-in-view: false
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>

    <groupId>com.databases</groupId>
    <artifactId>ecommerce-database</artifactId>
    <version>1.0.0</version>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.7.1</version>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

### Build and Run

```bash
cd ecommerce-database
mvn clean package

# Run with PostgreSQL
java -jar target/ecommerce-database-1.0.0.jar

# Or run tests with H2
mvn test
```

---

## Additional Learning Resources

- JDBC Documentation: https://docs.oracle.com/javase/tutorial/jdbc/
- JPA/Hibernate: https://hibernate.org/orm/
- PostgreSQL Documentation: https://www.postgresql.org/docs/