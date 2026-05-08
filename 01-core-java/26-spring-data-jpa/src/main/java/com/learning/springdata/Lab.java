package com.learning.springdata;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Spring Data JPA Lab (Conceptual) ===\n");

        entityMapping();
        repositoryPattern();
        relationships();
        queryMethods();
        transactions();
    }

    static void entityMapping() {
        System.out.println("--- Entity Mapping ---");

        record Column(String name, String jpaType, boolean pk) {}
        var cols = List.of(
            new Column("id", "Long (BIGINT) PK @GeneratedValue", true),
            new Column("name", "String (VARCHAR 100) @Column(nullable=false)", false),
            new Column("email", "String (VARCHAR 255) @Column(unique=true)", false),
            new Column("createdAt", "LocalDateTime @Temporal(TIMESTAMP)", false)
        );
        System.out.printf("%-20s %-55s %s%n", "Field", "JPA Mapping", "PK");
        System.out.println("-".repeat(80));
        cols.forEach(c -> System.out.printf("%-20s %-55s %s%n", c.name(), c.jpaType(), c.pk() ? "*" : ""));

        System.out.println("\n  JPA annotations:");
        for (var a : List.of("@Entity @Table(name)", "@Id @GeneratedValue", "@Column @Transient",
                "@Enumerated @Temporal @Lob"))
            System.out.println("  " + a);
    }

    static void repositoryPattern() {
        System.out.println("\n--- Repository Pattern ---");

        interface Repo<T, ID> {
            Optional<T> findById(ID id); List<T> findAll(); T save(T entity);
            void delete(T entity); long count();
        }

        class MapRepo implements Repo<Map<String,Object>, Long> {
            final ConcurrentHashMap<Long, Map<String,Object>> store = new ConcurrentHashMap<>();
            final AtomicLong idGen = new AtomicLong(1);

            public Optional<Map<String,Object>> findById(Long id) { return Optional.ofNullable(store.get(id)); }
            public List<Map<String,Object>> findAll() { return List.copyOf(store.values()); }
            public Map<String,Object> save(Map<String,Object> e) {
                Long id = (Long) e.getOrDefault("id", idGen.getAndIncrement());
                e.put("id", id); store.put(id, new ConcurrentHashMap<>(e)); return e;
            }
            public void delete(Map<String,Object> e) { store.remove(e.get("id")); }
            public long count() { return store.size(); }
        }

        var repo = new MapRepo();
        repo.save(new HashMap<>(Map.of("name", "Alice", "email", "alice@x.com")));
        repo.save(new HashMap<>(Map.of("name", "Bob", "email", "bob@x.com")));
        System.out.println("  Saved: " + repo.count() + " users");
        repo.findAll().forEach(e -> System.out.println("    " + e));

        System.out.println("\n  Hierarchy: Repository -> CrudRepository -> PagingAndSortingRepository -> JpaRepository");
    }

    static void relationships() {
        System.out.println("\n--- Entity Relationships ---");

        for (var r : List.of(
            "@OneToOne (User -> Profile) fetch=LAZY",
            "@OneToMany (User -> Orders) fetch=LAZY",
            "@ManyToOne (Order -> User) fetch=EAGER",
            "@ManyToMany (User -> Roles) fetch=LAZY"))
            System.out.println("  " + r);

        System.out.println("\n  Cascade types: PERSIST, MERGE, REMOVE, ALL, DETACH");
        System.out.println("  Fetch: LAZY (load on access), EAGER (load immediately)");

        String example = """
            @Entity
            public class Order {
              @Id private Long id;
              @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id")
              private User user;
              @OneToMany(mappedBy="order", cascade=CascadeType.ALL)
              private List<OrderItem> items;
            }""";
        System.out.println(example);
    }

    static void queryMethods() {
        System.out.println("\n--- Query Methods ---");

        for (var m : List.of(
            "findByName(String) -> SELECT u FROM User u WHERE u.name = ?1",
            "findByEmailAndActive(String, boolean) -> WHERE email = ?1 AND active = ?2",
            "findByAgeBetween(int, int) -> WHERE age BETWEEN ?1 AND ?2",
            "findByNameContainingIgnoreCase(String) -> WHERE UPPER(name) LIKE UPPER(CONCAT('%',?1,'%'))",
            "countByDepartment(String) -> SELECT COUNT(u) ...",
            "deleteByLastLoginBefore(LocalDateTime) -> DELETE ..."))
            System.out.println("  " + m);

        System.out.println("\n  @Query for custom JPQL:");
        System.out.println("""
            @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%',:search,'%'))")
            List<User> searchByName(@Param("search") String search);

            @Modifying
            @Query("UPDATE User u SET u.active=false WHERE u.lastLogin < :date")
            int deactivateInactive(@Param("date") LocalDateTime date);""");

        System.out.println("\n  Keywords: And, Or, Between, LessThan, Like, Containing, In, OrderBy, Asc, Desc");
    }

    static void transactions() {
        System.out.println("\n--- Transaction Management ---");

        class TxManager {
            boolean active = false;
            List<Runnable> ops = new ArrayList<>();
            void begin() { active = true; ops.clear(); System.out.println("  TX started"); }
            void add(Runnable r) { if (active) ops.add(r); }
            void commit() { System.out.println("  Committing " + ops.size() + " ops"); ops.forEach(Runnable::run); ops.clear(); active = false; }
            void rollback() { System.out.println("  Rolling back " + ops.size() + " ops"); ops.clear(); active = false; }
        }

        var tx = new TxManager();
        tx.begin();
        tx.add(() -> System.out.println("    INSERT users (Alice)"));
        tx.add(() -> System.out.println("    UPDATE accounts -$100"));
        tx.rollback();

        System.out.println("\n  @Transactional attributes:");
        for (var a : List.of("readOnly", "rollbackFor", "noRollbackFor", "propagation", "isolation", "timeout"))
            System.out.println("  " + a);

        System.out.println("\n  Propagation: REQUIRED, REQUIRES_NEW, SUPPORTS, NOT_SUPPORTED, NEVER, MANDATORY, NESTED");

        System.out.println("\n  JPA concepts: PersistenceContext, EntityManager, JPQL, N+1 Problem, 1st/2nd level cache, @Version");
    }
}
