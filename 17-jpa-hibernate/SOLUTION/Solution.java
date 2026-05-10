package com.learning.lab.module17.solution;

import java.util.*;
import java.time.LocalDateTime;

public class Solution {

    // JPA Entity
    @javax.persistence.Entity
    @javax.persistence.Table(name = "users")
    public static class User {
        @javax.persistence.Id
        @javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
        private Long id;

        @javax.persistence.Column(nullable = false, length = 100)
        private String name;

        @javax.persistence.Column(unique = true)
        private String email;

        @javax.persistence.Column(name = "created_at")
        private LocalDateTime createdAt;

        @javax.persistence.Version
        private Long version;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public Long getVersion() { return version; }
        public void setVersion(Long version) { this.version = version; }
    }

    // Product Entity
    @javax.persistence.Entity
    public static class Product {
        @javax.persistence.Id
        @javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
        private Long id;

        @javax.persistence.Column(nullable = false)
        private String name;

        private String description;

        @javax.persistence.Column(precision = 10, scale = 2)
        private double price;

        @javax.persistence.ManyToOne(fetch = javax.persistence.FetchType.LAZY)
        @javax.persistence.JoinColumn(name = "category_id")
        private Category category;

        @javax.persistence.OneToMany(mappedBy = "product")
        private List<Review> reviews = new ArrayList<>();

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public Category getCategory() { return category; }
        public void setCategory(Category category) { this.category = category; }
        public List<Review> getReviews() { return reviews; }
    }

    // Category Entity
    @javax.persistence.Entity
    public static class Category {
        @javax.persistence.Id
        @javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
        private Long id;

        @javax.persistence.Column(unique = true)
        private String name;

        @javax.persistence.OneToMany(mappedBy = "category")
        private List<Product> products = new ArrayList<>();

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<Product> getProducts() { return products; }
    }

    // Review Entity
    @javax.persistence.Entity
    public static class Review {
        @javax.persistence.Id
        @javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
        private Long id;

        private int rating;
        private String comment;

        @ManyToOne
        @JoinColumn(name = "product_id")
        private Product product;

        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public int getRating() { return rating; }
        public void setRating(int rating) { this.rating = rating; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        public Product getProduct() { return product; }
        public void setProduct(Product product) { this.product = product; }
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
    }

    // JPA Repository
    public interface JpaRepository<T, ID> {
        void save(T entity);
        void delete(T entity);
        Optional<T> findById(ID id);
        List<T> findAll();
        long count();
    }

    public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByEmail(String email);
        List<User> findByNameContaining(String name);
    }

    public interface ProductRepository extends JpaRepository<Product, Long> {
        List<Product> findByCategoryId(Long categoryId);
        List<Product> findByPriceLessThan(double price);
    }

    // In-memory repository implementation
    public static class InMemoryUserRepository implements UserRepository {
        private final Map<Long, User> users = new HashMap<>();
        private Long idSequence = 1L;

        @Override
        public void save(User user) {
            if (user.getId() == null) {
                user.setId(idSequence++);
            }
            users.put(user.getId(), user);
        }

        @Override
        public void delete(User user) {
            users.remove(user.getId());
        }

        @Override
        public Optional<User> findById(Long id) {
            return Optional.ofNullable(users.get(id));
        }

        @Override
        public List<User> findAll() {
            return new ArrayList<>(users.values());
        }

        @Override
        public long count() {
            return users.size();
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return users.values().stream().filter(u -> email.equals(u.getEmail())).findFirst();
        }

        @Override
        public List<User> findByNameContaining(String name) {
            return users.values().stream().filter(u -> u.getName().contains(name)).toList();
        }
    }

    // JPQL Query Builder
    public static classJpQLQueryBuilder {
        private StringBuilder query = new StringBuilder();
        private List<String> parameters = new ArrayList<>();

        publicJpQLQueryBuilder select(String entity) {
            query.append("SELECT u FROM ").append(entity).append(" u");
            return this;
        }

        publicJpQLQueryBuilder where(String condition) {
            query.append(" WHERE ").append(condition);
            return this;
        }

        publicJpQLQueryBuilder and(String condition) {
            query.append(" AND ").append(condition);
            return this;
        }

        publicJpQLQueryBuilder orderBy(String field) {
            query.append(" ORDER BY ").append(field);
            return this;
        }

        publicJpQLQueryBuilder setParameter(String name, Object value) {
            parameters.add(name + "=" + value);
            return this;
        }

        public String build() {
            return query.toString();
        }

        public List<String> getParameters() {
            return parameters;
        }
    }

    // Criteria API
    public interface CriteriaQuery<T> {
        CriteriaQuery<T> select(String field);
        CriteriaQuery<T> from(Class<T> entity);
        CriteriaQuery<T> where(CriteriaBuilder cb);
        List<T> getResult();
    }

    public static class CriteriaBuilder {
        public static Condition equal(String field, Object value) {
            return new Condition(field + " = " + value);
        }

        public static Condition greaterThan(String field, Object value) {
            return new Condition(field + " > " + value);
        }

        public static Condition like(String field, String pattern) {
            return new Condition(field + " LIKE " + pattern);
        }

        public static Condition between(String field, Object start, Object end) {
            return new Condition(field + " BETWEEN " + start + " AND " + end);
        }
    }

    public static class Condition {
        private final String expression;

        public Condition(String expression) {
            this.expression = expression;
        }

        public String getExpression() { return expression; }
    }

    // Entity Manager
    public static class EntityManager {
        private final Map<Class<?>, Map<Object, Object>> cache = new HashMap<>();

        public <T> void persist(T entity) {
            cache.computeIfAbsent(entity.getClass(), k -> new HashMap<>()).put(entity.hashCode(), entity);
        }

        public <T> T find(Class<T> entityClass, Object id) {
            return entityClass.cast(cache.getOrDefault(entityClass, new HashMap<>()).get(id));
        }

        public <T> void remove(T entity) {
            cache.getOrDefault(entity.getClass(), new HashMap<>()).remove(entity.hashCode());
        }

        public <T> List<T> createQuery(String jpql, Class<T> resultClass) {
            return new ArrayList<>();
        }
    }

    // Entity Transaction
    public static class EntityTransaction {
        private boolean active = false;

        public void begin() {
            active = true;
        }

        public void commit() {
            active = false;
        }

        public void rollback() {
            active = false;
        }

        public boolean isActive() {
            return active;
        }
    }

    // Persistence Context
    public static class PersistenceContext {
        private final Map<Object, Object> entities = new HashMap<>();
        private final EntityManager em = new EntityManager();

        public void persist(Object entity) {
            entities.put(entity, entity);
        }

        public <T> T find(Class<T> entityClass, Object id) {
            return entityClass.cast(entities.get(id));
        }

        public void remove(Object entity) {
            entities.remove(entity);
        }

        public void clear() {
            entities.clear();
        }
    }

    // JPQL Queries
    public static class Queries {
        public static final String FIND_ALL_USERS = "SELECT u FROM User u";
        public static final String FIND_USER_BY_EMAIL = "SELECT u FROM User u WHERE u.email = :email";
        public static final String FIND_PRODUCTS_BY_CATEGORY = "SELECT p FROM Product p WHERE p.category.id = :categoryId";
        public static final String FIND_EXPENSIVE_PRODUCTS = "SELECT p FROM Product p WHERE p.price > :price";
        public static final String COUNT_USERS_BY_NAME = "SELECT COUNT(u) FROM User u WHERE u.name LIKE :name";
    }

    // N+1 Problem Demo
    public static class NPlusOneDemo {
        public static void demonstrateNPlusOne(ProductRepository productRepo) {
            List<Product> products = productRepo.findAll();
            for (Product product : products) {
                product.getReviews().size();
            }
        }

        public static String getOptimizedQuery() {
            return "SELECT p FROM Product p JOIN FETCH p.reviews";
        }
    }

    // Entity Relationships
    public static class Relationships {
        @javax.persistence.OneToOne
        public static class Address {
            @javax.persistence.Id
            private Long id;
            private String street;
            @javax.persistence.OneToOne(mappedBy = "address")
            private User user;
        }

        @javax.persistence.ManyToMany
        public static class Tag {
            @javax.persistence.Id
            private Long id;
            private String name;
            @javax.persistence.ManyToMany(mappedBy = "tags")
            private List<Product> products = new ArrayList<>();
        }
    }

    // Cascading
    public static class CascadingDemo {
        public static javax.persistence.CascadeType[] ALL = {
            javax.persistence.CascadeType.PERSIST,
            javax.persistence.CascadeType.MERGE,
            javax.persistence.CascadeType.REMOVE,
            javax.persistence.CascadeType.REFRESH,
            javax.persistence.CascadeType.DETACH
        };

        public static javax.persistence.CascadeType[] PERSIST_MERGE = {
            javax.persistence.CascadeType.PERSIST,
            javax.persistence.CascadeType.MERGE
        };
    }

    // Lock Types
    public static class LockDemo {
        public static javax.persistence.LockModeType OPTIMISTIC = javax.persistence.LockModeType.OPTIMISTIC;
        public static javax.persistence.LockModeType PESSIMISTIC = javax.persistence.LockModeType.PESSIMISTIC;
    }

    // Pagination
    public static class PagedResult<T> {
        private final List<T> content;
        private final long totalElements;
        private final int totalPages;
        private final int page;
        private final int size;

        public PagedResult(List<T> content, long totalElements, int totalPages, int page, int size) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.page = page;
            this.size = size;
        }

        public List<T> getContent() { return content; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getPage() { return page; }
        public int getSize() { return size; }
        public boolean hasNext() { return page < totalPages - 1; }
    }

    public static void demonstrateJPA() {
        System.out.println("=== JPA Entity ===");
        User user = new User();
        user.setName("John");
        user.setEmail("john@test.com");
        user.setCreatedAt(LocalDateTime.now());
        System.out.println("User: " + user.getName());

        System.out.println("\n=== Repository ===");
        UserRepository userRepo = new InMemoryUserRepository();
        userRepo.save(user);
        System.out.println("Total users: " + userRepo.count());

        System.out.println("\n=== JPQL Query ===");
       JpQLQueryBuilder query = newJpQLQueryBuilder()
            .select("User")
            .where("u.email = :email")
            .orderBy("u.name");
        System.out.println("Query: " + query.build());

        System.out.println("\n=== Criteria API ===");
        Condition cond = CriteriaBuilder.equal("email", "test@test.com");
        System.out.println("Condition: " + cond.getExpression());

        System.out.println("\n=== Entity Manager ===");
        EntityManager em = new EntityManager();
        em.persist(user);

        System.out.println("\n=== Pagination ===");
        PagedResult<User> page = new PagedResult<>(List.of(user), 1, 1, 0, 10);
        System.out.println("Page " + page.getPage() + " of " + page.getTotalPages());
    }

    public static void main(String[] args) {
        demonstrateJPA();
    }
}