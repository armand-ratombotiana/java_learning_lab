# Code Deep Dive: Relational Database Patterns in Java

## JDBC Metadata – Inspecting Database Schema

```java
public class SchemaInspector {
    public static void inspect(Connection conn) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();

        System.out.println("DB: " + meta.getDatabaseProductName());
        System.out.println("Version: " + meta.getDatabaseProductVersion());
        System.out.println("Driver: " + meta.getDriverName());

        ResultSet tables = meta.getTables(null, null, "%",
                new String[]{"TABLE"});
        while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            System.out.println("\nTable: " + tableName);

            ResultSet columns = meta.getColumns(null, null, tableName, "%");
            while (columns.next()) {
                System.out.printf("  %s %s(%d) %s%n",
                        columns.getString("COLUMN_NAME"),
                        columns.getString("TYPE_NAME"),
                        columns.getInt("COLUMN_SIZE"),
                        "YES".equals(columns.getString("IS_NULLABLE")) ? "NULL" : "NOT NULL"
                );
            }
        }
    }
}
```

## JPA Entity Mapping – Full Relationship Example

```java
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    public void addOrder(Order order) {
        orders.add(order);
        order.setCustomer(this);
    }
}

@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
}

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
    private BigDecimal unitPrice;
}

@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    private BigDecimal price;
}
```

## Transaction Management – Manual JDBC

```java
public void transferFunds(Connection conn, long fromId, long toId,
                          BigDecimal amount) throws SQLException {
    conn.setAutoCommit(false);
    conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    try {
        // Atomic: both updates succeed or both fail
        updateBalance(conn, fromId, amount.negate());
        updateBalance(conn, toId, amount);

        conn.commit();
    } catch (SQLException e) {
        conn.rollback();
        throw e;
    } finally {
        conn.setAutoCommit(true);
    }
}
```

## Spring `@Transactional` – Declarative Approach

```java
@Service
public class OrderService {
    @Transactional(isolation = Isolation.READ_COMMITTED,
                   propagation = Propagation.REQUIRED,
                   rollbackFor = InsufficientFundsException.class)
    public Order placeOrder(Long customerId, List<OrderItemRequest> items) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        Order order = new Order();
        order.setCustomer(customer);
        for (OrderItemRequest req : items) {
            Product product = productRepo.findById(req.productId())
                    .orElseThrow(() -> new ProductNotFoundException(req.productId()));
            order.addItem(new OrderItem(product, req.quantity()));
        }
        return orderRepo.save(order);
    }
}
```
