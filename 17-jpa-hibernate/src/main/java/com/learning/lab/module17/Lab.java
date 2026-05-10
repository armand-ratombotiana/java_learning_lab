package com.learning.lab.module17;

import jakarta.persistence.*;
import java.util.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 17: JPA/Hibernate ===");
        entityDemo();
        relationshipsDemo();
        repositoryDemo();
        queriesDemo();
        lifecycleDemo();
        cachingDemo();
    }

    static void entityDemo() {
        System.out.println("\n--- JPA Entity Basics ---");
        System.out.println("@Entity - Marks class as JPA entity");
        System.out.println("@Table(name=\"users\") - Specifies table name");
        System.out.println("@Id - Primary key");
        System.out.println("@GeneratedValue - Auto-generate ID");
        System.out.println("@Column - Column configuration");
        System.out.println("@Transient - Skip persistence");
        System.out.println("@Temporal - Date/Time handling");
    }

    static void relationshipsDemo() {
        System.out.println("\n--- Entity Relationships ---");
        System.out.println("@OneToOne - One-to-one relationship");
        System.out.println("@OneToMany/@ManyToOne - One-to-many relationship");
        System.out.println("@ManyToMany - Many-to-many relationship");
        System.out.println("\nCascade Types:");
        System.out.println("  ALL, PERSIST, MERGE, REMOVE, REFRESH, DETACH");
        System.out.println("\nFetch Types:");
        System.out.println("  EAGER - Load immediately");
        System.out.println("  LAZY - Load on demand");
    }

    static void repositoryDemo() {
        System.out.println("\n--- JPA Repository ---");
        System.out.println("CrudRepository:");
        System.out.println("  save(), findById(), findAll(), deleteById()");
        System.out.println("\nJpaRepository:");
        System.out.println("  flush(), getReference(), saveAll()");
        System.out.println("  findAll(Sort), Page<T> findAll(Pageable)");
        System.out.println("\nPagingAndSortingRepository:");
        System.out.println("  Page<T> findAll(Pageable)");
        System.out.println("  Iterable<T> findAll(Sort)");
    }

    static void queriesDemo() {
        System.out.println("\n--- Query Methods ---");
        System.out.println("Method naming conventions:");
        System.out.println("  findByName(String name)");
        System.out.println("  findByAgeGreaterThan(int age)");
        System.out.println("  findByEmailContaining(String pattern)");
        System.out.println("  findByNameAndEmail(String name, String email)");
        System.out.println("  findByNameOrAgeLessThan(String name, int age)");
        System.out.println("\n@Query annotation:");
        System.out.println("  @Query(\"SELECT u FROM User u WHERE u.age > :age\")");
        System.out.println("  List<User> findByAge(@Param(\"age\") int age);");
    }

    static void lifecycleDemo() {
        System.out.println("\n--- Entity Lifecycle ---");
        System.out.println("States:");
        System.out.println("  NEW - New entity, not managed");
        System.out.println("  MANAGED - Managed by EntityManager");
        System.out.println("  REMOVED - Marked for deletion");
        System.out.println("  DETACHED - No longer managed");
        System.out.println("\nOperations:");
        System.out.println("  persist() - NEW -> MANAGED");
        System.out.println("  merge() - DETACHED -> MANAGED");
        System.out.println("  remove() - MANAGED -> REMOVED");
        System.out.println("  flush() - Sync to DB");
        System.out.println("  refresh() - Reload from DB");
        System.out.println("  detach() - MANAGED -> DETACHED");
    }

    static void cachingDemo() {
        System.out.println("\n--- Caching ---");
        System.out.println("L1 Cache (First Level):");
        System.out.println("  - Session-scoped by default");
        System.out.println("  - Automatically enabled");
        System.out.println("  - Per EntityManager instance");
        System.out.println("\nL2 Cache (Second Level):");
        System.out.println("  - SessionFactory-scoped");
        System.out.println("  - Requires explicit configuration");
        System.out.println("  @Cacheable(true)");
        System.out.println("  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)");
        System.out.println("\nQuery Cache:");
        System.out.println("  - Caches query results");
        System.out.println("  - Depends on L2 cache");
    }
}

@Entity
@Table(name = "users")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }
}

@Entity
@Table(name = "departments")
class Department {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @OneToMany(mappedBy = "department")
    private List<User> users;
}

@Entity
@Table(name = "orders")
class Order {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private double total;
}

import jakarta.persistence.*;
import java.time.LocalDateTime;
