# JPA & Hibernate - Exercises

## Exercise 1: Entity Mapping
**Objective**: Map Java classes to database tables.

### Task
Create entity mappings for a payroll system:
1. Employee entity with all basic annotations
2. Department entity with one-to-many relationship
3. Project entity with many-to-many relationship

### Implementation
```java
@Entity
@Table(name = "employees")
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String firstName;
    
    @Column(nullable = false, length = 50)
    private String lastName;
    
    @Column(unique = true)
    private String email;
    
    @Enumerated(EnumType.STRING)
    private EmployeeType type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    
    // Getters, setters, constructors
}
```

---

## Exercise 2: JPQL Queries
**Objective**: Write JPQL queries for complex data retrieval.

### Task
Implement various JPQL queries:
1. Simple SELECT with filtering
2. JOIN queries
3. Aggregate functions with GROUP BY
4. Subqueries
5. Native SQL queries

### Implementation
```java
// Find employees by department
TypedQuery<Employee> query = em.createQuery(
    "SELECT e FROM Employee e WHERE e.department.name = :deptName", 
    Employee.class);
query.setParameter("deptName", "Engineering");
List<Employee> employees = query.getResultList();

// Aggregate query
Query avgSalaryQuery = em.createQuery(
    "SELECT e.department.name, AVG(e.salary) FROM Employee e GROUP BY e.department");
List<Object[]> results = avgSalaryQuery.getResultList();

// Subquery
TypedQuery<Employee> highEarnerQuery = em.createQuery(
    "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(e2.salary) FROM Employee e2)", 
    Employee.class);
```

---

## Exercise 3: Criteria API
**Objective**: Build type-safe queries using Criteria API.

### Task
Create dynamic queries:
1. Simple query building
2. Multiple predicates
3. Sorting and pagination
4. Complex join queries

### Implementation
```java
public List<Employee> findByCriteria(EmployeeSearchCriteria criteria) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Employee> query = cb.createQuery(Employee.class);
    Root<Employee> root = query.from(Employee.class);
    
    Predicate predicate = cb.conjunction();
    
    if (criteria.getName() != null) {
        predicate = cb.and(predicate, 
            cb.like(root.get("firstName"), "%" + criteria.getName() + "%"));
    }
    
    if (criteria.getMinSalary() != null) {
        predicate = cb.and(predicate, 
            cb.ge(root.get("salary"), criteria.getMinSalary()));
    }
    
    if (criteria.getDepartment() != null) {
        predicate = cb.and(predicate, 
            cb.equal(root.get("department").get("name"), criteria.getDepartment()));
    }
    
    query.where(predicate);
    query.orderBy(cb.asc(root.get("lastName")));
    
    return em.createQuery(query).getResultList();
}
```

---

## Exercise 4: Entity Relationships
**Objective**: Manage complex entity relationships.

### Task
Implement bidirectional relationships:
1. One-to-Many / Many-to-One
2. Many-to-Many with join table
3. Lazy vs eager loading
4. Cascade operations

### Implementation
```java
@OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<Employee> employees = new ArrayList<>();

@ManyToMany
@JoinTable(
    name = "employee_projects",
    joinColumns = @JoinColumn(name = "employee_id"),
    inverseJoinColumns = @JoinColumn(name = "project_id")
)
private List<Project> projects = new ArrayList<>();

public void addEmployee(Employee employee) {
    employees.add(employee);
    employee.setDepartment(this);
}

public void addProject(Project project) {
    projects.add(project);
    project.getEmployees().add(this);
}
```

---

## Exercise 5: Entity Graphs
**Objective**: Optimize query performance with entity graphs.

### Task
Implement entity graphs:
1. Define graph with @EntityGraph
2. Use with find() methods
3. Fetch multiple related entities
4. Compare with JOIN FETCH

### Implementation
```java
@Entity
@NamedEntityGraph(
    name = "Employee.withDepartment",
    attributeNodes = @NamedAttributeNode("department")
)
public class Employee { ... }

// Usage
EntityGraph<?> graph = em.getEntityGraph("Employee.withDepartment");
Employee employee = em.find(Employee.class, id, 
    Collections.singletonMap("javax.persistence.fetchgraph", graph));

// Or inline
Map<String, Object> hints = new HashMap<>();
hints.put("javax.persistence.fetchgraph", 
    em.createEntityGraph("Employee.withDepartment"));
Employee emp = em.find(Employee.class, id, hints);
```

---

## Exercise 6: Transaction Management
**Objective**: Handle transactions properly.

### Task
Implement transaction patterns:
1. Programmatic transactions
2. Isolation levels
3. Optimistic locking
4. Pessimistic locking

### Implementation
```java
@Stateless
public class OrderService {
    
    @PersistenceContext
    private EntityManager em;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createOrder(Order order) {
        em.persist(order);
        
        for (OrderItem item : order.getItems()) {
            Inventory inventory = item.getProduct().getInventory();
            inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
            em.merge(inventory);
        }
    }
    
    @Lock(LockModeType.OPTIMISTIC)
    public Product findWithLock(Long id) {
        return em.find(Product.class, id);
    }
}
```

---

## Exercise 7: Second-Level Cache
**Objective**: Implement caching for performance.

### Task
Configure and use second-level cache:
1. Enable caching in persistence.xml
2. Configure cache per entity
3. Use cache in queries
4. Invalidate cache

### Configuration
```xml
<property name="jakarta.persistence.cache.retrieveMode" value="USE"/>
<property name="jakarta.persistence.cache.storeMode" value="REFRESH"/>
```

### Entity
```java
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Product { ... }

// Usage
Product product = entityManager.find(Product.class, id); // Cached

// Cache query results
Query query = em.createQuery("SELECT p FROM Product p WHERE p.category = :category");
query.setHint("jakarta.persistence.cacheRetrieveMode", "USE");
query.setHint("jakarta.persistence.cacheStoreMode", "BYPASS");
```

---

## Exercise 8: Batch Operations
**Objective**: Optimize bulk operations.

### Task
Implement efficient batch processing:
1. Batch insert
2. Bulk update
3. Batch update with care for entities
4. Stateless sessions

### Implementation
```java
public void batchInsert(List<Entity> entities) {
    for (int i = 0; i < entities.size(); i++) {
        em.persist(entities.get(i));
        
        if (i % 50 == 0) {
            em.flush();
            em.clear();
        }
    }
}

// Bulk update
public int updatePrices(BigDecimal increase) {
    return em.createQuery(
        "UPDATE Product p SET p.price = p.price + :increase")
        .setParameter("increase", increase)
        .executeUpdate();
}

// Using StatelessSession for massive operations
StatelessSession session = sessionFactory.openStatelessSession();
ScrollableResults results = session.createQuery("from Employee").scroll();
while (results.next()) {
    Employee employee = (Employee) results.get(0);
    employee.setSalary(employee.getSalary().multiply(new BigDecimal("1.1")));
    session.update(employee);
}
session.close();
```

---

## Exercise 9: Custom Types & Converters
**Objective**: Map complex types to database columns.

### Task
Create custom converters:
1. Boolean to Y/N
2. Enum with custom values
3. JSON to String
4. Date/time with timezone

### Implementation
```java
@Converter
public class BooleanToYNConverter implements AttributeConverter<Boolean, String> {
    
    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        return attribute ? "Y" : "N";
    }
    
    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        return "Y".equals(dbData);
    }
}

@Converter(autoApply = true)
public class JsonConverter implements AttributeConverter<JsonNode, String> {
    
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public String convertToDatabaseColumn(JsonNode attribute) {
        return attribute == null ? null : attribute.toString();
    }
    
    @Override
    public JsonNode convertToEntityAttribute(String dbData) {
        return dbData == null ? null : mapper.readTree(dbData);
    }
}
```

---

## Exercise 10: Entity Listeners & Callbacks
**Objective**: Implement lifecycle callbacks and audit logging.

### Task
Create entity listeners:
1. Pre/Post persist callbacks
2. Pre/Post update callbacks
3. Custom audit listeners
4. Soft delete implementation

### Implementation
```java
@EntityListeners(AuditListener.class)
@Entity
public class Product {
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

public class AuditListener {
    
    @PostPersist
    public void logCreate(Object entity) {
        // Log creation
    }
    
    @PreRemove
    public void logDelete(Object entity) {
        // Mark as deleted instead of removing
    }
}
```

---

## Running Tests
```bash
# Run with H2 console
mvn exec:java -Dexec.mainClass="com.learning.Main"

# Enable SQL logging
mvn exec:java -Dexec.mainClass="com.learning.Main" -Djpa.showSql=true
```