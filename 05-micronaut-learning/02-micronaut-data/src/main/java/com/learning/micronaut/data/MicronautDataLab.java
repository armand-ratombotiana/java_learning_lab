package com.learning.micronaut.data;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MicronautDataLab {

    static class Entity {
        final Long id;
        final String name;
        Entity(Long id, String name) { this.id = id; this.name = name; }
    }

    static class User extends Entity {
        final String email;
        final int age;
        User(Long id, String name, String email, int age) {
            super(id, name); this.email = email; this.age = age;
        }
        public String toString() { return String.format("User[%d: %s, %s, age=%d]", id, name, email, age); }
    }

    static class Product extends Entity {
        final double price;
        Product(Long id, String name, double price) { super(id, name); this.price = price; }
        public String toString() { return String.format("Product[%d: %s, $%.2f]", id, name, price); }
    }

    interface Repository<T extends Entity> {
        T save(T entity);
        Optional<T> findById(Long id);
        List<T> findAll();
        void deleteById(Long id);
        long count();
    }

    static class InMemoryRepository<T extends Entity> implements Repository<T> {
        protected final Map<Long, T> store = new ConcurrentHashMap<>();
        private final AtomicLong idGen = new AtomicLong(1);

        @Override
        public T save(T entity) {
            T toSave;
            if (entity.id == null) {
                Long newId = idGen.getAndIncrement();
                toSave = entity;
            } else {
                toSave = entity;
            }
            store.put(toSave.id, toSave);
            return toSave;
        }

        @Override
        public Optional<T> findById(Long id) { return Optional.ofNullable(store.get(id)); }

        @Override
        public List<T> findAll() { return List.copyOf(store.values()); }

        @Override
        public void deleteById(Long id) { store.remove(id); }

        @Override
        public long count() { return store.size(); }
    }

    static class UserRepository extends InMemoryRepository<User> {
        public List<User> findByAgeGreaterThan(int age) {
            return store.values().stream().filter(u -> u.age > age).collect(Collectors.toList());
        }
        public List<User> findByNameLike(String name) {
            return store.values().stream()
                .filter(u -> u.name.toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
        }
    }

    static class ProductRepository extends InMemoryRepository<Product> {
        public List<Product> findByPriceLessThan(double max) {
            return store.values().stream().filter(p -> p.price < max).collect(Collectors.toList());
        }
        public Optional<Product> findByName(String name) {
            return store.values().stream().filter(p -> p.name.equals(name)).findFirst();
        }
    }

    static class DataService {
        private final UserRepository userRepo = new UserRepository();
        private final ProductRepository productRepo = new ProductRepository();

        public void initialize() {
            userRepo.save(new User(null, "Alice", "alice@example.com", 30));
            userRepo.save(new User(null, "Bob", "bob@example.com", 25));
            userRepo.save(new User(null, "Charlie", "charlie@example.com", 35));
            productRepo.save(new Product(null, "Laptop", 1200.00));
            productRepo.save(new Product(null, "Mouse", 25.50));
            productRepo.save(new Product(null, "Keyboard", 89.99));
        }

        public void runQueries() {
            System.out.println("  All users: " + userRepo.findAll());
            System.out.println("  User by ID 2: " + userRepo.findById(2L).orElse(null));
            System.out.println("  Users > 25: " + userRepo.findByAgeGreaterThan(25));
            System.out.println("  Products < $100: " + productRepo.findByPriceLessThan(100.0));
            System.out.println("  Total count: users=" + userRepo.count() + ", products=" + productRepo.count());

            userRepo.deleteById(1L);
            System.out.println("  After delete, count: " + userRepo.count());
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Micronaut Data Concepts Lab ===");
        System.out.println("Demonstrating repository patterns, queries, and ORM concepts\n");

        repositoryPattern();
        queryMethods();
        dataServiceDemo();
        transactionConcept();
        dtoProjection();
    }

    static void repositoryPattern() {
        System.out.println("--- Repository Pattern ---");
        UserRepository repo = new UserRepository();
        repo.save(new User(null, "Dave", "dave@test.com", 28));
        repo.save(new User(null, "Eve", "eve@test.com", 32));

        repo.findAll().forEach(u -> System.out.println("  " + u));
    }

    static void queryMethods() {
        System.out.println("\n--- Derived Query Methods ---");
        UserRepository repo = new UserRepository();
        repo.save(new User(1L, "Alice", "alice@x.com", 30));
        repo.save(new User(2L, "Bob", "bob@x.com", 22));
        repo.save(new User(3L, "Alex", "alex@x.com", 45));

        List<User> adults = repo.findByAgeGreaterThan(25);
        List<User> named = repo.findByNameLike("al");

        System.out.println("  Adults (>25): " + adults);
        System.out.println("  Name like 'al': " + named);
    }

    static void dataServiceDemo() {
        System.out.println("\n--- Data Service Layer ---");
        DataService ds = new DataService();
        ds.initialize();
        ds.runQueries();
    }

    static void transactionConcept() {
        System.out.println("\n--- Transaction Concept ---");
        List<String> log = new ArrayList<>();

        try {
            withTransaction(() -> {
                log.add("INSERT INTO users VALUES (1, 'Alice')");
                log.add("INSERT INTO users VALUES (2, 'Bob')");
                if (false) throw new RuntimeException("rollback");
                log.add("COMMIT");
                return true;
            });
        } catch (Exception e) {
            log.add("ROLLBACK");
        }

        log.forEach(entry -> System.out.println("  " + entry));
        System.out.println("  (Micronaut Data uses @Transactional for declarative tx management)");
    }

    static void dtoProjection() {
        System.out.println("\n--- DTO Projection ---");
        UserRepository repo = new UserRepository();
        repo.save(new User(1L, "Alice", "alice@x.com", 30));
        repo.save(new User(2L, "Bob", "bob@x.com", 22));

        List<String> names = repo.findAll().stream().map(u -> u.name).collect(Collectors.toList());
        System.out.println("  Projected names: " + names);
        System.out.println("  (Micronaut Data supports interface-based projections)");
    }

    static <T> T withTransaction(Callable<T> operation) throws Exception {
        return operation.call();
    }

    interface Callable<T> { T call() throws Exception; }
}
