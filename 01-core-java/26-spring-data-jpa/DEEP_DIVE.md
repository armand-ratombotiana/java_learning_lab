# Module 26: Spring Data JPA - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-25, specifically Module 19 (JDBC) & Module 25 (Spring Boot Basics)  
**Estimated Reading Time**: 90 minutes  

---

## 📚 Table of Contents

1. [Introduction to Spring Data JPA](#intro)
2. [Entities and Primary Keys](#entities)
3. [Repositories & Query Methods](#repositories)
4. [Custom Queries with @Query](#custom-queries)
5. [Pagination and Sorting](#pagination)
6. [Entity Relationships](#relationships)
7. [Auditing](#auditing)

---

## 1. Introduction to Spring Data JPA <a name="intro"></a>
Spring Data JPA adds a layer on top of JPA (Java Persistence API) to significantly reduce the amount of boilerplate code required to implement data access layers. It integrates seamlessly with Hibernate (the default JPA provider in Spring Boot).

---

## 2. Entities and Primary Keys <a name="entities"></a>
An Entity represents a table in a relational database.

```java
import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    // Constructors, Getters, and Setters
}
```

---

## 3. Repositories & Query Methods <a name="repositories"></a>
Spring Data JPA provides the `JpaRepository` interface, which includes methods for basic CRUD operations.

```java
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    // Derived query method: Spring parses the method name to generate SQL
    List<Employee> findByNameContainingIgnoreCase(String name);
    
    Employee findByEmail(String email);
}
```

---

## 4. Custom Queries with @Query <a name="custom-queries"></a>
For complex queries, you can use JPQL (Java Persistence Query Language) or native SQL.

```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // JPQL
    @Query("SELECT e FROM Employee e WHERE e.email LIKE %:domain%")
    List<Employee> findByEmailDomain(@Param("domain") String domain);

    // Native SQL
    @Query(value = "SELECT * FROM employees WHERE name = ?1", nativeQuery = true)
    List<Employee> findByNameNative(String name);
}
```

---

## 5. Pagination and Sorting <a name="pagination"></a>
Spring Data JPA makes pagination and sorting trivial using `Pageable` and `Sort`.

```java
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

// In a Service class:
Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
Page<Employee> page = employeeRepository.findAll(pageable);

long totalElements = page.getTotalElements();
int totalPages = page.getTotalPages();
List<Employee> employees = page.getContent();
```

---

## 6. Entity Relationships <a name="relationships"></a>
JPA supports `OneToOne`, `OneToMany`, `ManyToOne`, and `ManyToMany` relationships.

```java
@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Bidirectional OneToMany
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> employees = new ArrayList<>();
}

@Entity
public class Employee {
    // ... other fields ...

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
}
```

---

## 7. Auditing <a name="auditing"></a>
Automatically populate `createdDate`, `lastModifiedDate`, `createdBy`, and `lastModifiedBy`.

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class AuditableEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```
Requires `@EnableJpaAuditing` on a configuration class.