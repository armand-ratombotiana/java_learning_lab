package com.learning.data;

import java.util.*;

public class SpringDataJpaTraining {

    public static void main(String[] args) {
        System.out.println("=== Spring Data JPA Training ===");

        demonstrateRepositoryPattern();
        demonstrateQueryMethods();
        demonstrateEntityRelationships();
        demonstrateTransactions();
    }

    private static void demonstrateRepositoryPattern() {
        System.out.println("\n--- Repository Pattern ---");

        System.out.println("Repository Hierarchy:");
        String[] hierarchy = {
            "Repository<T, ID> - marker interface",
            "CrudRepository<T, ID> - CRUD operations",
            "PagingAndSortingRepository<T, ID> - pagination & sorting",
            "JpaRepository<T, ID> - JPA-specific operations"
        };
        for (String h : hierarchy) System.out.println("  " + h);

        System.out.println("\nBasic Repository:");
        String repo = """
            @Repository
            public interface UserRepository extends JpaRepository<User, Long> {
            }""";
        System.out.println(repo);

        System.out.println("Key Methods:");
        String[] methods = {
            "save(S) - insert/update entity",
            "findById(ID) - find by primary key",
            "findAll() - find all entities",
            "deleteById(ID) - delete by ID",
            "count() - count entities",
            "existsById(ID) - check existence"
        };
        for (String m : methods) System.out.println("  - " + m);
    }

    private static void demonstrateQueryMethods() {
        System.out.println("\n--- Query Methods ---");

        System.out.println("Deriving Queries from Method Names:");
        String[] derive = {
            "findByName(String name) - WHERE name = ?",
            "findByAgeGreaterThan(int age) - WHERE age > ?",
            "findByNameContaining(String name) - WHERE name LIKE %?%",
            "findByEmailAndActive(String email, boolean active) - AND"
        };
        for (String d : derive) System.out.println("  " + d);

        System.out.println("\n@Query - Custom JPQL:");
        String query = """
            @Query("SELECT u FROM User u WHERE u.email LIKE %:email%")
            List<User> searchByEmail(@Param("email") String email);""";
        System.out.println(query);

        System.out.println("\n@Query - Native SQL:");
        String nativeQuery = """
            @Query(value = "SELECT * FROM users WHERE age > :age", 
                   nativeQuery = true)
            List<User> findUsersOlderThan(@Param("age") int age);""";
        System.out.println(nativeQuery);

        System.out.println("\nQuery Result Modifiers:");
        String[] modifiers = {
            "@Modifying - for UPDATE/DELETE",
            "@Transactional - for write operations",
            "@QueryHints - for query optimization"
        };
        for (String m : modifiers) System.out.println("  " + m);
    }

    private static void demonstrateEntityRelationships() {
        System.out.println("\n--- Entity Relationships ---");

        System.out.println("Relationship Types:");
        String[] relationships = {
            "@OneToOne - one-to-one mapping",
            "@OneToMany / @ManyToOne - one-to-many / many-to-one",
            "@ManyToMany - many-to-many mapping"
        };
        for (String r : relationships) System.out.println("  " + r);

        System.out.println("\nCascade Types:");
        String[] cascades = {
            "CascadeType.PERSIST - persist related entities",
            "CascadeType.MERGE - merge related entities",
            "CascadeType.REMOVE - delete related entities",
            "CascadeType.ALL - all cascade types"
        };
        for (String c : cascades) System.out.println("  " + c);

        System.out.println("\nFetch Types:");
        String[] fetch = {
            "FetchType.EAGER - load immediately",
            "FetchType.LAZY - load on demand (default for collections)"
        };
        for (String f : fetch) System.out.println("  " + f);

        System.out.println("\nExample:");
        String example = """
            @Entity
            public class Author {
                @Id @GeneratedValue
                private Long id;
                
                @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
                private List<Book> books;
            }""";
        System.out.println(example);
    }

    private static void demonstrateTransactions() {
        System.out.println("\n--- Transaction Management ---");

        System.out.println("@Transactional:");
        String[] tx = {
            "propagation - how transactions relate",
            "isolation - transaction isolation level",
            "timeout - transaction timeout",
            "readOnly - optimization hint",
            "rollbackFor - exception types to rollback"
        };
        for (String t : tx) System.out.println("  " + t);

        System.out.println("\nPropagation Types:");
        String[] prop = {
            "REQUIRED (default) - use existing or create new",
            "REQUIRES_NEW - always create new",
            "NESTED - savepoint-based",
            "NEVER - never execute in transaction"
        };
        for (String p : prop) System.out.println("  " + p);

        System.out.println("\nIsolation Levels:");
        String[] isolation = {
            "READ_UNCOMMITTED - dirty reads possible",
            "READ_COMMITTED - prevents dirty reads",
            "REPEATABLE_READ - prevents non-repeatable reads",
            "SERIALIZABLE - full isolation"
        };
        for (String i : isolation) System.out.println("  " + i);

        System.out.println("\nService Layer Example:");
        String service = """
            @Service
            public class OrderService {
                @Transactional
                public Order placeOrder(Order order) {
                    order.setStatus("PENDING");
                    return orderRepository.save(order);
                }
            }""";
        System.out.println(service);
    }
}