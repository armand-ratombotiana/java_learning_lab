# PROBLEM WALKTHROUGH: Implement Custom Spring Data Repository with Query DSL

## Problem Statement

Implement a custom Spring Data JPA repository that supports a type-safe Query DSL (Domain Specific Language) for building dynamic queries at runtime without writing JPQL or native SQL. The DSL should support:

- Fluent API: `query.select().from(entity).where(...)`
- Type-safe predicates: `eq`, `ne`, `gt`, `lt`, `like`, `in`, `between`
- Logical operators: `and`, `or`, `not`
- Pagination and sorting
- Projection support
- Joins: `innerJoin`, `leftJoin`
- Aggregations: `count`, `sum`, `avg`, `min`, `max`

**Constraints:**
- Pure Java 21+ with Spring Data JPA
- Use JPA Criteria API under the hood
- No external query libraries (no Querydsl, no jOOQ)
- Generics for type safety

---

## Step-by-Step Solution

### Step 1: Domain Entities

```java
@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    private String customerName;
    private String customerEmail;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private LocalDateTime shippedDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public LocalDateTime getShippedDate() { return shippedDate; }
    public void setShippedDate(LocalDateTime shippedDate) { this.shippedDate = shippedDate; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sku;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
}

public enum OrderStatus {
    PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
}
```

### Step 2: Query DSL Core — Path & Predicate Interfaces

```java
public interface Path<T> {
    String getAttribute();
    Class<T> getJavaType();
}

public interface Expression<T> {
    // Marker interface for expressions in select/projection
}

public interface Predicate {
    Predicate and(Predicate other);
    Predicate or(Predicate other);
    Predicate negate();
    javax.persistence.criteria.Predicate toJpaPredicate(Root<?> root,
        CriteriaQuery<?> query, CriteriaBuilder cb);
}
```

### Step 3: Typed Path Implementations

```java
public record StringPath(String attribute) implements Path<String>, Expression<String> {
    public Predicate eq(String value) { return new SimplePredicate(this, Operator.EQ, value); }
    public Predicate ne(String value) { return new SimplePredicate(this, Operator.NE, value); }
    public Predicate like(String pattern) { return new SimplePredicate(this, Operator.LIKE, pattern); }
    public Predicate notLike(String pattern) { return new SimplePredicate(this, Operator.NOT_LIKE, pattern); }
    public Predicate in(Collection<String> values) { return new SimplePredicate(this, Operator.IN, values); }
    public Predicate contains(String value) { return new SimplePredicate(this, Operator.CONTAINS, value); }
    public Predicate startsWith(String value) { return new SimplePredicate(this, Operator.STARTS_WITH, value); }
    public Predicate isEmpty() { return new SimplePredicate(this, Operator.IS_EMPTY, null); }
    public Predicate isNotEmpty() { return new SimplePredicate(this, Operator.IS_NOT_EMPTY, null); }
}

public record NumberPath<N extends Number & Comparable<N>>(String attribute, Class<N> type)
        implements Path<N>, Expression<N> {
    public Predicate eq(N value) { return new SimplePredicate(this, Operator.EQ, value); }
    public Predicate ne(N value) { return new SimplePredicate(this, Operator.NE, value); }
    public Predicate gt(N value) { return new SimplePredicate(this, Operator.GT, value); }
    public Predicate gte(N value) { return new SimplePredicate(this, Operator.GTE, value); }
    public Predicate lt(N value) { return new SimplePredicate(this, Operator.LT, value); }
    public Predicate lte(N value) { return new SimplePredicate(this, Operator.LTE, value); }
    public Predicate between(N min, N max) { return new SimplePredicate(this, Operator.BETWEEN, new N[]{min, max}); }
    public Predicate in(Collection<N> values) { return new SimplePredicate(this, Operator.IN, values); }
}

public record DateTimePath<T extends Comparable<T>>(String attribute, Class<T> type)
        implements Path<T>, Expression<T> {
    public Predicate eq(T value) { return new SimplePredicate(this, Operator.EQ, value); }
    public Predicate after(T value) { return new SimplePredicate(this, Operator.GT, value); }
    public Predicate before(T value) { return new SimplePredicate(this, Operator.LT, value); }
    public Predicate between(T min, T max) { return new SimplePredicate(this, Operator.BETWEEN, new T[]{min, max}); }
}

public record BooleanPath(String attribute) implements Path<Boolean>, Expression<Boolean> {
    public Predicate isTrue() { return new SimplePredicate(this, Operator.IS_TRUE, null); }
    public Predicate isFalse() { return new SimplePredicate(this, Operator.IS_FALSE, null); }
}

public record EnumPath<E extends Enum<E>>(String attribute, Class<E> type)
        implements Path<E>, Expression<E> {
    public Predicate eq(E value) { return new SimplePredicate(this, Operator.EQ, value); }
    public Predicate ne(E value) { return new SimplePredicate(this, Operator.NE, value); }
    public Predicate in(Collection<E> values) { return new SimplePredicate(this, Operator.IN, values); }
}

public record CollectionPath<E>(String attribute, Class<E> elementType) implements Path<List<E>> {
    public Predicate isMember(E value) { return new SimplePredicate(this, Operator.IS_MEMBER, value); }
    public Predicate isNotEmpty() { return new SimplePredicate(this, Operator.IS_NOT_EMPTY, null); }
    public Predicate isEmpty() { return new SimplePredicate(this, Operator.IS_EMPTY, null); }
    public Predicate hasSize(int size) { return new SimplePredicate(this, Operator.COLLECTION_SIZE, size); }
}
```

### Step 4: Operator Enum & SimplePredicate

```java
enum Operator {
    EQ, NE, GT, GTE, LT, LTE, LIKE, NOT_LIKE, IN, BETWEEN,
    CONTAINS, STARTS_WITH, IS_NULL, IS_NOT_NULL, IS_TRUE, IS_FALSE,
    IS_EMPTY, IS_NOT_EMPTY, IS_MEMBER, COLLECTION_SIZE, AND, OR, NOT
}

record SimplePredicate(Path<?> path, Operator operator, Object value) implements Predicate {

    @Override
    public Predicate and(Predicate other) {
        return new CompoundPredicate(this, Operator.AND, other);
    }

    @Override
    public Predicate or(Predicate other) {
        return new CompoundPredicate(this, Operator.OR, other);
    }

    @Override
    public Predicate negate() {
        return new CompoundPredicate(this, Operator.NOT, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public javax.persistence.criteria.Predicate toJpaPredicate(
            Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        String attr = path.getAttribute();
        Path<?> jpaPath = resolvePath(root, attr);

        return switch (operator) {
            case EQ -> cb.equal(jpaPath, value);
            case NE -> cb.notEqual(jpaPath, value);
            case GT -> cb.greaterThan((Path<Comparable<Object>>) jpaPath, (Comparable<Object>) value);
            case GTE -> cb.greaterThanOrEqualTo((Path<Comparable<Object>>) jpaPath, (Comparable<Object>) value);
            case LT -> cb.lessThan((Path<Comparable<Object>>) jpaPath, (Comparable<Object>) value);
            case LTE -> cb.lessThanOrEqualTo((Path<Comparable<Object>>) jpaPath, (Comparable<Object>) value);
            case LIKE -> cb.like((Path<String>) jpaPath, (String) value);
            case NOT_LIKE -> cb.notLike((Path<String>) jpaPath, (String) value);
            case IN -> {
                if (value instanceof Collection<?> c) {
                    yield jpaPath.in(c);
                }
                throw new IllegalArgumentException("IN requires a Collection");
            }
            case BETWEEN -> {
                if (value instanceof Object[] arr && arr.length == 2) {
                    yield cb.between((Path<Comparable<Object>>) jpaPath,
                        (Comparable<Object>) arr[0], (Comparable<Object>) arr[1]);
                }
                throw new IllegalArgumentException("BETWEEN requires two-element array");
            }
            case CONTAINS -> cb.like((Path<String>) jpaPath, "%" + value + "%");
            case STARTS_WITH -> cb.like((Path<String>) jpaPath, value + "%");
            case IS_NULL -> cb.isNull(jpaPath);
            case IS_NOT_NULL -> cb.isNotNull(jpaPath);
            case IS_TRUE -> cb.isTrue((Path<Boolean>) jpaPath);
            case IS_FALSE -> cb.isFalse((Path<Boolean>) jpaPath);
            case IS_EMPTY -> cb.isEmpty((Path<Collection<?>>) jpaPath);
            case IS_NOT_EMPTY -> cb.isNotEmpty((Path<Collection<?>>) jpaPath);
            case IS_MEMBER -> cb.isMember(value, (Path<Collection<?>>) jpaPath);
            case COLLECTION_SIZE -> cb.equal(cb.size((Path<Collection<?>>) jpaPath), value);
            default -> throw new UnsupportedOperationException("Operator: " + operator);
        };
    }

    private Path<?> resolvePath(Root<?> root, String attribute) {
        if (attribute.contains(".")) {
            String[] parts = attribute.split("\\.");
            Path<?> path = root.get(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                path = path.get(parts[i]);
            }
            return path;
        }
        return root.get(attribute);
    }
}
```

### Step 5: Compound Predicate (AND, OR, NOT)

```java
record CompoundPredicate(Predicate left, Operator operator, Predicate right) implements Predicate {

    CompoundPredicate(Predicate left, Operator operator, Predicate right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Predicate and(Predicate other) {
        return new CompoundPredicate(this, Operator.AND, other);
    }

    @Override
    public Predicate or(Predicate other) {
        return new CompoundPredicate(this, Operator.OR, other);
    }

    @Override
    public Predicate negate() {
        return new CompoundPredicate(this, Operator.NOT, null);
    }

    @Override
    public javax.persistence.criteria.Predicate toJpaPredicate(
            Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return switch (operator) {
            case AND -> cb.and(left.toJpaPredicate(root, query, cb),
                right.toJpaPredicate(root, query, cb));
            case OR -> cb.or(left.toJpaPredicate(root, query, cb),
                right.toJpaPredicate(root, query, cb));
            case NOT -> cb.not(left.toJpaPredicate(root, query, cb));
            default -> throw new UnsupportedOperationException("Compound operator: " + operator);
        };
    }
}
```

### Step 6: Static Metamodel (Q-Classes)

```java
public class QOrder {
    public static final StringPath orderNumber = new StringPath("orderNumber");
    public static final StringPath customerName = new StringPath("customerName");
    public static final StringPath customerEmail = new StringPath("customerEmail");
    public static final NumberPath<BigDecimal> totalAmount = new NumberPath<>("totalAmount", BigDecimal.class);
    public static final EnumPath<OrderStatus> status = new EnumPath<>("status", OrderStatus.class);
    public static final DateTimePath<LocalDateTime> orderDate = new DateTimePath<>("orderDate", LocalDateTime.class);
    public static final DateTimePath<LocalDateTime> shippedDate = new DateTimePath<>("shippedDate", LocalDateTime.class);
    public static final CollectionPath<OrderItem> items = new CollectionPath<>("items", OrderItem.class);

    // Joins
    public static final QOrderItem item = new QOrderItem("items");
}

public class QOrderItem {
    private final String path;
    public QOrderItem(String path) { this.path = path; }
    public StringPath sku = new StringPath(path + ".sku");
    public StringPath productName = new StringPath(path + ".productName");
    public NumberPath<Integer> quantity = new NumberPath<>(path + ".quantity", Integer.class);
    public NumberPath<BigDecimal> unitPrice = new NumberPath<>(path + ".unitPrice", BigDecimal.class);
}
```

### Step 7: Query Builder (Fluent API)

```java
public class JpaQuery<T> {
    private final Class<T> entityClass;
    private final EntityManager entityManager;
    private final CriteriaBuilder cb;
    private Predicate whereClause;
    private final List<javax.persistence.criteria.Order> orders = new ArrayList<>();
    private int offset;
    private int limit = Integer.MAX_VALUE;
    private List<String> joins = new ArrayList<>();

    JpaQuery(Class<T> entityClass, EntityManager entityManager) {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
        this.cb = entityManager.getCriteriaBuilder();
    }

    public JpaQuery<T> where(Predicate predicate) {
        this.whereClause = predicate;
        return this;
    }

    public JpaQuery<T> orderBy(String attribute, boolean ascending) {
        Path<Object> path = new SimplePath<>(attribute);
        orders.add(ascending ? cb.asc(resolvePath(null, attribute))
            : cb.desc(resolvePath(null, attribute)));
        return this;
    }

    public JpaQuery<T> offset(int offset) {
        this.offset = offset;
        return this;
    }

    public JpaQuery<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public JpaQuery<T> join(String association) {
        this.joins.add(association);
        return this;
    }

    public List<T> list() {
        CriteriaQuery<T> query = cb.createQuery(entityClass);
        Root<T> root = query.from(entityClass);
        applyJoins(root);
        query.select(root);
        applyWhere(root, query);
        applyOrders(root, query);

        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        if (offset > 0) typedQuery.setFirstResult(offset);
        if (limit < Integer.MAX_VALUE) typedQuery.setMaxResults(limit);
        return typedQuery.getResultList();
    }

    public Optional<T> singleResult() {
        List<T> results = limit(1).list();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public long count() {
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> root = query.from(entityClass);
        applyJoins(root);
        query.select(cb.countDistinct(root));
        applyWhere(root, query);
        return entityManager.createQuery(query).getSingleResult();
    }

    // Projections
    public <P> List<P> project(Class<P> projectionClass, Expression<?>... expressions) {
        CriteriaQuery<P> query = cb.createQuery(projectionClass);
        Root<T> root = query.from(entityClass);
        applyJoins(root);
        if (expressions.length == 0) {
            query.select(cb.construct(projectionClass, root));
        } else {
            query.select(cb.construct(projectionClass,
                Arrays.stream(expressions).map(expr -> resolvePath(root, ((Path<?>) expr).getAttribute())).toArray()));
        }
        applyWhere(root, query);
        return entityManager.createQuery(query).getResultList();
    }

    // Aggregations
    public long count(String attribute) {
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> root = query.from(entityClass);
        query.select(cb.count(resolvePath(root, attribute)));
        applyWhere(root, query);
        return entityManager.createQuery(query).getSingleResult();
    }

    public BigDecimal sum(String attribute) {
        CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
        Root<T> root = query.from(entityClass);
        query.select(cb.sum(resolvePath(root, attribute).as(BigDecimal.class)));
        applyWhere(root, query);
        return entityManager.createQuery(query).getSingleResult();
    }

    public Double avg(String attribute) {
        CriteriaQuery<Double> query = cb.createQuery(Double.class);
        Root<T> root = query.from(entityClass);
        query.select(cb.avg(resolvePath(root, attribute).as(Double.class)));
        applyWhere(root, query);
        return entityManager.createQuery(query).getSingleResult();
    }

    // Private helpers
    private void applyJoins(Root<T> root) {
        joins.forEach(j -> root.join(j, JoinType.LEFT));
    }

    private void applyWhere(Root<T> root, CriteriaQuery<?> query) {
        if (whereClause != null) {
            query.where(whereClause.toJpaPredicate(root, query, cb));
        }
    }

    private void applyOrders(Root<T> root, CriteriaQuery<?> query) {
        if (!orders.isEmpty()) {
            query.orderBy(orders);
        }
    }

    private Path<Object> resolvePath(Root<?> root, String attribute) {
        if (root == null) return new SimplePath<>(attribute);
        if (attribute.contains(".")) {
            String[] parts = attribute.split("\\.");
            Path<?> path = root.get(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                path = path.get(parts[i]);
            }
            @SuppressWarnings("unchecked")
            Path<Object> result = (Path<Object>) path;
            return result;
        }
        return root.get(attribute);
    }

    record SimplePath<T>(String attribute) implements Path<T> {
        @Override public String getAttribute() { return attribute; }
        @Override public Class<T> getJavaType() { return null; }
    }
}
```

### Step 8: Repository Interface & Implementation

```java
public interface QueryDslRepository<T, ID> {
    JpaQuery<T> query();
    Optional<T> findOne(Predicate predicate);
    List<T> findAll(Predicate predicate);
    Page<T> findAll(Predicate predicate, Pageable pageable);
    long count(Predicate predicate);
    boolean exists(Predicate predicate);
    <P> List<P> project(Class<P> projectionClass, Expression<?>... expressions);
}

public class QueryDslRepositoryImpl<T, ID> implements QueryDslRepository<T, ID> {

    private final EntityManager entityManager;
    private final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public QueryDslRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.entityClass = (Class<T>) ((ParameterizedType) getClass()
            .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public JpaQuery<T> query() {
        return new JpaQuery<>(entityClass, entityManager);
    }

    @Override
    public Optional<T> findOne(Predicate predicate) {
        return query().where(predicate).singleResult();
    }

    @Override
    public List<T> findAll(Predicate predicate) {
        return query().where(predicate).list();
    }

    @Override
    public Page<T> findAll(Predicate predicate, Pageable pageable) {
        List<T> content = query()
            .where(predicate)
            .orderBy(extractSortProperty(pageable.getSort()),
                pageable.getSort().isAscending())
            .offset((int) pageable.getOffset())
            .limit(pageable.getPageSize())
            .list();
        long total = query().where(predicate).count();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public long count(Predicate predicate) {
        return query().where(predicate).count();
    }

    @Override
    public boolean exists(Predicate predicate) {
        return query().where(predicate).count() > 0;
    }

    @Override
    public <P> List<P> project(Class<P> projectionClass, Expression<?>... expressions) {
        return query().project(projectionClass, expressions);
    }

    private String extractSortProperty(Sort sort) {
        return sort.iterator().next().getProperty();
    }
}
```

### Step 9: Spring Data Integration

```java
// Custom base repository interface
@NoRepositoryBean
public interface CustomJpaRepository<T, ID> extends JpaRepository<T, ID>, QueryDslRepository<T, ID> {
}

// Custom repository factory bean
public class CustomJpaRepositoryFactoryBean<R extends JpaRepository<T, ID>, T, ID>
        extends JpaRepositoryFactoryBean<R, T, ID> {

    public CustomJpaRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new CustomRepositoryFactory(entityManager);
    }

    private static class CustomRepositoryFactory extends JpaRepositoryFactory {
        public CustomRepositoryFactory(EntityManager entityManager) {
            super(entityManager);
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return QueryDslRepositoryImpl.class;
        }
    }
}

// Enable custom factory
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### Step 10: Service Layer — Using the DSL

```java
@Service
public class OrderQueryService {

    private final CustomJpaRepository<Order, Long> orderRepository;

    public OrderQueryService(CustomJpaRepository<Order, Long> orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> findHighValueOrders(BigDecimal minAmount) {
        return orderRepository.findAll(
            QOrder.totalAmount.gt(minAmount)
                .and(QOrder.status.ne(OrderStatus.CANCELLED))
        );
    }

    public Page<Order> searchOrders(String customerName, OrderStatus status,
                                      BigDecimal minAmount, BigDecimal maxAmount,
                                      LocalDateTime fromDate, LocalDateTime toDate,
                                      Pageable pageable) {
        Predicate predicate = null;

        if (customerName != null && !customerName.isBlank()) {
            predicate = and(predicate, QOrder.customerName.contains(customerName));
        }
        if (status != null) {
            predicate = and(predicate, QOrder.status.eq(status));
        }
        if (minAmount != null) {
            predicate = and(predicate, QOrder.totalAmount.gte(minAmount));
        }
        if (maxAmount != null) {
            predicate = and(predicate, QOrder.totalAmount.lte(maxAmount));
        }
        if (fromDate != null) {
            predicate = and(predicate, QOrder.orderDate.after(fromDate));
        }
        if (toDate != null) {
            predicate = and(predicate, QOrder.orderDate.before(toDate));
        }

        return predicate != null
            ? orderRepository.findAll(predicate, pageable)
            : ((JpaRepository<Order, Long>) orderRepository).findAll(pageable);
    }

    public List<Order> findOrdersWithExpensiveItems(int minQuantity) {
        return orderRepository.query()
            .join("items")
            .where(QOrder.item.quantity.gte(minQuantity))
            .orderBy("orderDate", false)
            .list();
    }

    public BigDecimal getTotalRevenueBetween(LocalDateTime from, LocalDateTime to) {
        return orderRepository.query()
            .where(QOrder.orderDate.between(from, to)
                .and(QOrder.status.ne(OrderStatus.CANCELLED)))
            .sum("totalAmount");
    }

    private Predicate and(Predicate existing, Predicate next) {
        return existing == null ? next : existing.and(next);
    }
}
```

### Step 11: Usage Example

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderQueryService orderQueryService;

    public OrderController(OrderQueryService orderQueryService) {
        this.orderQueryService = orderQueryService;
    }

    @GetMapping("/search")
    public Page<Order> search(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                LocalDateTime toDate,
            Pageable pageable) {
        return orderQueryService.searchOrders(
            customerName, status, minAmount, maxAmount, fromDate, toDate, pageable);
    }

    @GetMapping("/high-value")
    public List<Order> highValue(@RequestParam BigDecimal min) {
        return orderQueryService.findHighValueOrders(min);
    }

    @GetMapping("/revenue")
    public Map<String, BigDecimal> revenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return Map.of("totalRevenue", orderQueryService.getTotalRevenueBetween(from, to));
    }
}
```

---

## Complexity Analysis

| Operation | Complexity |
|-----------|------------|
| Single predicate evaluation | O(1) JPA predicate construction |
| Compound predicate (AND/OR) | O(k) where k = number of sub-predicates |
| Query execution with simple predicate | O(N) database scan without index |
| Query with indexed column | O(log N) B-tree lookup |
| Count query | Same as list but returns count |
| Pagination | O(N) for counting + O(limit) for fetching |
| Join query | O(N*M) depending on join cardinality |

---

## Follow-Up Questions

1. **How would you add support for subqueries?** — Extend `Expression` with `SubqueryExpression`. JPA Criteria API supports `cb.subquery()`. Add `exists(Predicate)`, `notExists(Predicate)` methods.

2. **How do you prevent SQL injection?** — DSL uses JPA Criteria API parameter binding (`cb.parameter()`). Never concatenates user input into SQL strings. All values use `setParameter`.

3. **How would you support dynamic sort from user input?** — Whitelist allowed sort properties to prevent injection. Use `PropertyInspector` to validate the property path against entity metadata.

4. **How does this compare to Querydsl?** — Querydsl generates Q-classes via APT, supports more databases, has richer expression support. Our DSL is simpler, no APT needed, but less feature-complete.

5. **How would you add support for custom functions (DATE_TRUNC, JSON_EXTRACT)?** — Extend `Expression` with `FunctionExpression`. Use `cb.function("DATE_TRUNC", String.class, path, Literal.of("month"))`.

---

## Test Cases

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class QueryDslRepositoryTest {

    @Autowired
    private CustomJpaRepository<Order, Long> orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        for (int i = 1; i <= 10; i++) {
            Order order = new Order();
            order.setOrderNumber("ORD-" + i);
            order.setCustomerName("Customer " + (i % 3));
            order.setStatus(i % 2 == 0 ? OrderStatus.DELIVERED : OrderStatus.PENDING);
            order.setTotalAmount(new BigDecimal(i * 100));
            order.setOrderDate(LocalDateTime.now().minusDays(i));
            orderRepository.save(order);
        }
    }

    @Test
    void shouldFindBySimplePredicate() {
        List<Order> results = orderRepository.findAll(
            QOrder.status.eq(OrderStatus.DELIVERED));
        assertThat(results).hasSize(5);
        assertThat(results).allMatch(o -> o.getStatus() == OrderStatus.DELIVERED);
    }

    @Test
    void shouldFindByCompoundPredicate() {
        List<Order> results = orderRepository.findAll(
            QOrder.totalAmount.gt(new BigDecimal("500"))
                .and(QOrder.status.ne(OrderStatus.CANCELLED)));
        assertThat(results).allMatch(o -> o.getTotalAmount().compareTo(new BigDecimal("500")) > 0);
    }

    @Test
    void shouldFindByLikePredicate() {
        List<Order> results = orderRepository.findAll(
            QOrder.customerName.contains("Customer 1"));
        assertThat(results).hasSize(3);
    }

    @Test
    void shouldPaginateResults() {
        Predicate predicate = QOrder.totalAmount.gt(BigDecimal.ZERO);
        Pageable pageable = PageRequest.of(0, 3, Sort.by("totalAmount").descending());
        Page<Order> page = orderRepository.findAll(predicate, pageable);
        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getTotalElements()).isEqualTo(10);
        assertThat(page.getTotalPages()).isEqualTo(4);
    }

    @Test
    void shouldCountWithPredicate() {
        long count = orderRepository.count(
            QOrder.status.eq(OrderStatus.PENDING));
        assertThat(count).isEqualTo(5);
    }

    @Test
    void shouldCheckExistence() {
        boolean exists = orderRepository.exists(
            QOrder.orderNumber.eq("ORD-1"));
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnEmptyForNonExistent() {
        Optional<Order> result = orderRepository.findOne(
            QOrder.orderNumber.eq("NONEXISTENT"));
        assertThat(result).isEmpty();
    }

    @Test
    void shouldComputeSumAggregation() {
        BigDecimal sum = orderRepository.query()
            .where(QOrder.status.eq(OrderStatus.DELIVERED))
            .sum("totalAmount");
        assertThat(sum).isEqualTo(new BigDecimal("3000")); // 200+400+600+800+1000
    }

    @Test
    void shouldUseFluentQueryWithJoins() {
        List<Order> results = orderRepository.query()
            .join("items")
            .where(QOrder.items.isNotEmpty())
            .orderBy("orderDate", true)
            .limit(5)
            .list();
        assertThat(results).isNotNull();
    }
}
```

---

## Summary

This custom Query DSL demonstrates:
- **Type-safe queries**: compile-time checking of property names and types
- **Fluent API**: chainable `.where().orderBy().limit().list()` pattern
- **Logical operators**: `and`, `or`, `negate()` for complex predicates
- **JPA Criteria integration**: all predicates translate to JPA Criteria API under the hood
- **Spring Data integration**: `CustomJpaRepository` extends `JpaRepository` + `QueryDslRepository`
- **Aggregation support**: `count()`, `sum()`, `avg()` on query results
- **Pagination and sorting**: full `Pageable` support