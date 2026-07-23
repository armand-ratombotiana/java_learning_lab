// MultiTenantExample.java — Spring Data JPA Multi-Tenancy
// Demonstrates schema-per-tenant and discriminator multi-tenancy patterns

package com.backend.academy.leetcode;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// ============================================================
// Multi-Tenant Entity (Schema-per-Tenant via Discriminator)
// ============================================================

@Entity
@Table(name = "products")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tenant_id", discriminatorType = DiscriminatorType.STRING)
abstract class BaseProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false, updatable = false)
    private String tenantId;

    public BaseProduct() {}

    public BaseProduct(String name, double price, String tenantId) {
        this.name = name;
        this.price = price;
        this.tenantId = tenantId;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}

// Tenant-specific entities using discriminator
@Entity
@DiscriminatorValue("tenant_a")
class TenantAProduct extends BaseProduct {
    @Column
    private String tenantASpecificField;

    public TenantAProduct() {}
    public TenantAProduct(String name, double price) {
        super(name, price, "tenant_a");
    }
}

@Entity
@DiscriminatorValue("tenant_b")
class TenantBProduct extends BaseProduct {
    @Column
    private String tenantBSpecificField;

    public TenantBProduct() {}
    public TenantBProduct(String name, double price) {
        super(name, price, "tenant_b");
    }
}

// ============================================================
// Database-per-Tenant Strategy
// ============================================================

/**
 * Tenant context holder for database-per-tenant pattern.
 */
class TenantContext {
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    private static final String DEFAULT_TENANT = "public";

    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static String getTenantId() {
        String tenant = CURRENT_TENANT.get();
        return tenant != null ? tenant : DEFAULT_TENANT;
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}

/**
 * Resolves tenant from HTTP request header.
 */
@Configuration
class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {

    @Override
    public String resolveCurrentTenantIdentifier() {
        // Resolve from request header
        ServletRequestAttributes attributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String tenant = attributes.getRequest().getHeader("X-Tenant-Id");
            if (tenant != null && !tenant.isEmpty()) {
                return tenant;
            }
        }
        // Fall back to thread-local (for async processing)
        return TenantContext.getTenantId();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}

/**
 * Multi-tenant connection provider using DataSource per tenant.
 */
class TenantConnectionProvider implements MultiTenantConnectionProvider<String> {
    private final Map<String, javax.sql.DataSource> dataSourceMap = new ConcurrentHashMap<>();
    private final javax.sql.DataSource defaultDataSource;

    public TenantConnectionProvider(javax.sql.DataSource defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    public void addTenant(String tenantId, javax.sql.DataSource dataSource) {
        dataSourceMap.put(tenantId, dataSource);
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        javax.sql.DataSource ds = dataSourceMap.get(tenantIdentifier);
        if (ds == null) ds = defaultDataSource;
        return ds.getConnection();
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return defaultDataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return true;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }
}

// ============================================================
// Repository and Service
// ============================================================

interface ProductRepository extends JpaRepository<BaseProduct, Long> {
    List<BaseProduct> findByTenantId(String tenantId);
    List<BaseProduct> findByNameContaining(String name);
}

@Service
class MultiTenantProductService {
    private final ProductRepository productRepository;

    public MultiTenantProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<BaseProduct> getAllProducts(String tenantId) {
        TenantContext.setTenantId(tenantId);
        try {
            return productRepository.findByTenantId(tenantId);
        } finally {
            TenantContext.clear();
        }
    }

    public BaseProduct createProduct(String tenantId, String name, double price) {
        TenantContext.setTenantId(tenantId);
        try {
            BaseProduct product = switch (tenantId) {
                case "tenant_a" -> new TenantAProduct(name, price);
                case "tenant_b" -> new TenantBProduct(name, price);
                default -> throw new IllegalArgumentException("Unknown tenant: " + tenantId);
            };
            return productRepository.save(product);
        } finally {
            TenantContext.clear();
        }
    }

    public void deleteProduct(String tenantId, Long productId) {
        TenantContext.setTenantId(tenantId);
        try {
            productRepository.deleteById(productId);
        } finally {
            TenantContext.clear();
        }
    }
}

// ============================================================
// REST Controller
// ============================================================

@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/products")
class MultiTenantController {
    private final MultiTenantProductService productService;

    public MultiTenantController(MultiTenantProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<BaseProduct> getProducts(@PathVariable String tenantId) {
        return productService.getAllProducts(tenantId);
    }

    @PostMapping
    public BaseProduct createProduct(
            @PathVariable String tenantId,
            @RequestParam String name,
            @RequestParam double price) {
        return productService.createProduct(tenantId, name, price);
    }

    @DeleteMapping("/{productId}")
    public void deleteProduct(@PathVariable String tenantId, @PathVariable Long productId) {
        productService.deleteProduct(tenantId, productId);
    }
}

// ============================================================
// Main Application
// ============================================================

@SpringBootApplication
public class MultiTenantExample {
    public static void main(String[] args) {
        SpringApplication.run(MultiTenantExample.class, args);
    }
}

// === LeetCode-Style Problems ===

/*
 * LeetCode 173: Binary Search Tree Iterator
 */
class BSTIterator {
    private java.util.Stack<TreeNode> stack;

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    public BSTIterator(TreeNode root) {
        stack = new java.util.Stack<>();
        pushLeft(root);
    }

    public int next() {
        TreeNode node = stack.pop();
        pushLeft(node.right);
        return node.val;
    }

    public boolean hasNext() {
        return !stack.isEmpty();
    }

    private void pushLeft(TreeNode node) {
        while (node != null) {
            stack.push(node);
            node = node.left;
        }
    }
}

/*
 * LeetCode 348: Design Tic-Tac-Toe
 */
class TicTacToe {
    private int[] rows;
    private int[] cols;
    private int diagonal;
    private int antiDiagonal;
    private int n;

    public TicTacToe(int n) {
        this.n = n;
        rows = new int[n];
        cols = new int[n];
    }

    public int move(int row, int col, int player) {
        int add = player == 1 ? 1 : -1;
        rows[row] += add;
        cols[col] += add;
        if (row == col) diagonal += add;
        if (row + col == n - 1) antiDiagonal += add;
        if (Math.abs(rows[row]) == n || Math.abs(cols[col]) == n
            || Math.abs(diagonal) == n || Math.abs(antiDiagonal) == n) {
            return player;
        }
        return 0;
    }
}
