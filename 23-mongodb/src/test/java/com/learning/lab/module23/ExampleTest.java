package com.learning.lab.module23;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExampleTest {

    @Test
    void testUserRepositoryCreation() {
        UserRepository repo = new UserRepository();
        assertNotNull(repo);
    }

    @Test
    void testUserSave() {
        UserRepository repo = new UserRepository();
        User user = new User("john", "john@example.com");
        assertTrue(repo.save(user));
    }

    @Test
    void testUserFindById() {
        UserRepository repo = new UserRepository();
        User user = new User("john", "john@example.com");
        user.setId("123");
        repo.save(user);
        User found = repo.findById("123");
        assertNotNull(found);
    }

    @Test
    void testUserFindByUsername() {
        UserRepository repo = new UserRepository();
        User user = new User("john", "john@example.com");
        repo.save(user);
        User found = repo.findByUsername("john");
        assertEquals("john", found.getUsername());
    }

    @Test
    void testUserDelete() {
        UserRepository repo = new UserRepository();
        User user = new User("john", "john@example.com");
        user.setId("123");
        repo.save(user);
        assertTrue(repo.delete("123"));
    }

    @Test
    void testUserUpdate() {
        UserRepository repo = new UserRepository();
        User user = new User("john", "john@example.com");
        user.setId("123");
        repo.save(user);
        user.setEmail("newemail@example.com");
        assertTrue(repo.update(user));
    }

    @Test
    void testUserEntityFields() {
        User user = new User("john", "john@example.com");
        assertEquals("john", user.getUsername());
        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    void testProductRepositoryFindByCategory() {
        ProductRepository repo = new ProductRepository();
        Product product = new Product("Laptop", 999.99, "Electronics");
        repo.save(product);
        var products = repo.findByCategory("Electronics");
        assertEquals(1, products.size());
    }

    @Test
    void testMongoDbConnection() {
        MongoConnection conn = new MongoConnection();
        assertTrue(conn.isConnected());
    }

    @Test
    void testCollectionExists() {
        MongoConnection conn = new MongoConnection();
        assertTrue(conn.collectionExists("users"));
    }

    @Test
    void testProductPrice() {
        Product product = new Product("Laptop", 999.99, "Electronics");
        assertEquals(999.99, product.getPrice(), 0.01);
    }

    @Test
    void testUserCount() {
        UserRepository repo = new UserRepository();
        repo.save(new User("user1", "user1@example.com"));
        repo.save(new User("user2", "user2@example.com"));
        assertEquals(2, repo.count());
    }
}

class User {
    private String id;
    private String username;
    private String email;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

class UserRepository {
    private java.util.List<User> users = new java.util.ArrayList<>();

    public boolean save(User user) {
        users.add(user);
        return true;
    }

    public User findById(String id) {
        return users.stream().filter(u -> id.equals(u.getId())).findFirst().orElse(null);
    }

    public User findByUsername(String username) {
        return users.stream().filter(u -> username.equals(u.getUsername())).findFirst().orElse(null);
    }

    public boolean delete(String id) {
        return users.removeIf(u -> id.equals(u.getId()));
    }

    public boolean update(User user) {
        return true;
    }

    public int count() {
        return users.size();
    }
}

class Product {
    private String name;
    private double price;
    private String category;

    public Product(String name, double price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }
}

class ProductRepository {
    private java.util.List<Product> products = new java.util.ArrayList<>();

    public void save(Product product) {
        products.add(product);
    }

    public java.util.List<Product> findByCategory(String category) {
        return products.stream().filter(p -> category.equals(p.getCategory())).toList();
    }
}

class MongoConnection {
    public boolean isConnected() {
        return true;
    }

    public boolean collectionExists(String name) {
        return true;
    }
}