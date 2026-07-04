# Code Deep Dive: Spring Data JPA

## Entity Relationships
```java
@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Derived query
    List<Order> findByCustomerEmail(String email);

    // JPQL query
    @Query("SELECT o FROM Order o WHERE o.total > :amount")
    List<Order> findExpensiveOrders(@Param("amount") BigDecimal amount);

    // Native query
    @Query(value = "SELECT * FROM orders WHERE DATE(created_at) = :date",
           nativeQuery = true)
    List<Order> findByCreationDate(@Param("date") LocalDate date);

    // Pagination
    Page<Order> findByCustomer(Customer customer, Pageable pageable);
}
```
