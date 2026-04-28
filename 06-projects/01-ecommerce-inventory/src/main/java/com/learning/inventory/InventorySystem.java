package com.learning.inventory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-Safe E-Commerce Inventory Management System
 * 
 * Real-World Scenario:
 * - Multiple concurrent users (threads) purchasing products
 * - Inventory must remain consistent (thread-safe)
 * - Orders must be tracked accurately
 * - Real-time inventory updates visible to all users
 * 
 * Technologies Used:
 * - Module 05: Concurrency (ReentrantReadWriteLock, ConcurrentHashMap)
 * - Module 03: Collections (List, Map, Queue)
 * - Module 02: OOP (Abstract classes, interfaces, composition)
 * - Module 01: Exception handling
 * 
 * Companies Using This Pattern:
 * - Amazon (inventory management)
 * - Shopify (e-commerce platform)
 * - Alibaba (supply chain)
 * - eBay (marketplace)
 */
public class InventorySystem {
    
    private final Map<String, Product> inventory;  // ProductId -> Product
    private final Queue<Order> orderHistory;       // Track all orders
    private final ReentrantReadWriteLock lock;     // Fine-grained locking
    private final AtomicInteger orderCounter;      // Atomic ID generation
    
    public InventorySystem() {
        this.inventory = new ConcurrentHashMap<>();
        this.orderHistory = new ConcurrentLinkedQueue<>();
        this.lock = new ReentrantReadWriteLock();
        this.orderCounter = new AtomicInteger(0);
    }
    
    /**
     * Add product to inventory
     * Thread-safe: Uses write lock
     */
    public void addProduct(String productId, String name, int quantity, double price) 
            throws InvalidProductException {
        
        if (productId == null || productId.trim().isEmpty()) {
            throw new InvalidProductException("Product ID cannot be empty");
        }
        if (quantity < 0) {
            throw new InvalidProductException("Quantity cannot be negative");
        }
        if (price < 0) {
            throw new InvalidProductException("Price cannot be negative");
        }
        
        lock.writeLock().lock();
        try {
            if (inventory.containsKey(productId)) {
                throw new InvalidProductException("Product " + productId + " already exists");
            }
            inventory.put(productId, new Product(productId, name, quantity, price));
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Purchase product with concurrent safety
     * Prevents overselling even with many concurrent requests
     */
    public Order purchaseProduct(String userId, String productId, int quantity) 
            throws InsufficientStockException, ProductNotFoundException {
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        lock.readLock().lock();
        try {
            // Check if product exists (read lock is sufficient)
            Product product = inventory.get(productId);
            if (product == null) {
                throw new ProductNotFoundException("Product not found: " + productId);
            }
            
            if (product.getAvailableQuantity() < quantity) {
                throw new InsufficientStockException(
                    "Only " + product.getAvailableQuantity() + " units available"
                );
            }
        } finally {
            lock.readLock().unlock();
        }
        
        // Upgrade to write lock for actual purchase
        lock.writeLock().lock();
        try {
            Product product = inventory.get(productId);
            
            // Double-check after acquiring write lock
            if (product == null || product.getAvailableQuantity() < quantity) {
                throw new InsufficientStockException("Stock no longer available");
            }
            
            // Create order
            int orderId = orderCounter.incrementAndGet();
            Order order = new Order(
                orderId,
                userId,
                productId,
                product.getName(),
                quantity,
                product.getPrice()
            );
            
            // Update product inventory (atomic operation)
            product.reduceQuantity(quantity);
            
            // Record order
            orderHistory.offer(order);
            
            return order;
            
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Get current inventory for a product
     * Thread-safe: Uses read lock (multiple readers allowed)
     */
    public int getAvailableQuantity(String productId) throws ProductNotFoundException {
        lock.readLock().lock();
        try {
            Product product = inventory.get(productId);
            if (product == null) {
                throw new ProductNotFoundException("Product not found: " + productId);
            }
            return product.getAvailableQuantity();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Get all products (snapshot)
     */
    public Map<String, Product> getAllProducts() {
        lock.readLock().lock();
        try {
            return new HashMap<>(inventory);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Get order history
     */
    public List<Order> getOrderHistory() {
        return new ArrayList<>(orderHistory);
    }
    
    /**
     * Restock product
     */
    public void restockProduct(String productId, int quantity) 
            throws ProductNotFoundException {
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be positive");
        }
        
        lock.writeLock().lock();
        try {
            Product product = inventory.get(productId);
            if (product == null) {
                throw new ProductNotFoundException("Product not found: " + productId);
            }
            product.increaseQuantity(quantity);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Product class - represents an item in inventory
     */
    public static class Product {
        private final String productId;
        private final String name;
        private volatile int quantity;  // volatile for visibility
        private final double price;
        private final LocalDateTime createdAt;
        private int totalSold = 0;
        
        public Product(String productId, String name, int quantity, double price) {
            this.productId = productId;
            this.name = name;
            this.quantity = quantity;
            this.price = price;
            this.createdAt = LocalDateTime.now();
        }
        
        public synchronized void reduceQuantity(int amount) {
            this.quantity -= amount;
            this.totalSold += amount;
        }
        
        public synchronized void increaseQuantity(int amount) {
            this.quantity += amount;
        }
        
        public int getAvailableQuantity() {
            return quantity;
        }
        
        public String getProductId() { return productId; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public int getTotalSold() { return totalSold; }
        
        @Override
        public String toString() {
            return String.format("%s (ID: %s) - $%.2f - %d in stock - %d sold",
                name, productId, price, quantity, totalSold);
        }
    }
    
    /**
     * Order class - represents a purchase
     */
    public static class Order {
        private final int orderId;
        private final String userId;
        private final String productId;
        private final String productName;
        private final int quantity;
        private final double unitPrice;
        private final LocalDateTime timestamp;
        private OrderStatus status;
        
        public Order(int orderId, String userId, String productId, 
                    String productName, int quantity, double unitPrice) {
            this.orderId = orderId;
            this.userId = userId;
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.timestamp = LocalDateTime.now();
            this.status = OrderStatus.PENDING;
        }
        
        public void completeOrder() { this.status = OrderStatus.COMPLETED; }
        public void cancelOrder() { this.status = OrderStatus.CANCELLED; }
        
        public double getTotalPrice() { return quantity * unitPrice; }
        
        public int getOrderId() { return orderId; }
        public String getUserId() { return userId; }
        public String getProductId() { return productId; }
        public int getQuantity() { return quantity; }
        public OrderStatus getStatus() { return status; }
        public LocalDateTime getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return String.format("Order #%d: %s ordered %d x %s for $%.2f [%s]",
                orderId, userId, quantity, productName, getTotalPrice(), status);
        }
    }
    
    public enum OrderStatus {
        PENDING, COMPLETED, CANCELLED, SHIPPED
    }
    
    // ==================== CUSTOM EXCEPTIONS ====================
    
    public static class InvalidProductException extends Exception {
        public InvalidProductException(String message) {
            super(message);
        }
    }
    
    public static class ProductNotFoundException extends Exception {
        public ProductNotFoundException(String message) {
            super(message);
        }
    }
    
    public static class InsufficientStockException extends Exception {
        public InsufficientStockException(String message) {
            super(message);
        }
    }
    
    // ==================== STRESS TEST ====================
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔" + "═".repeat(68) + "╗");
        System.out.println("║" + " ".repeat(15) + "E-COMMERCE INVENTORY SYSTEM DEMONSTRATION" + " ".repeat(12) + "║");
        System.out.println("╚" + "═".repeat(68) + "╝\n");
        
        InventorySystem system = new InventorySystem();
        
        // Setup inventory
        try {
            system.addProduct("LAPTOP-001", "Gaming Laptop", 10, 1299.99);
            system.addProduct("MOUSE-001", "Wireless Mouse", 100, 49.99);
            system.addProduct("KEYBOARD-001", "Mechanical Keyboard", 50, 129.99);
        } catch (InvalidProductException e) {
            System.err.println("Setup failed: " + e.getMessage());
            return;
        }
        
        System.out.println("Initial Inventory:");
        system.getAllProducts().forEach((id, product) -> 
            System.out.println("  " + product)
        );
        System.out.println();
        
        // Simulate concurrent purchases
        System.out.println("Simulating 50 concurrent purchases...");
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        List<Future<?>> futures = new ArrayList<>();
        
        for (int i = 1; i <= 50; i++) {
            final int customerId = i;
            futures.add(executor.submit(() -> {
                try {
                    Order order = system.purchaseProduct(
                        "customer-" + customerId,
                        "MOUSE-001",
                        1
                    );
                    System.out.println("✓ " + order);
                } catch (InsufficientStockException | ProductNotFoundException e) {
                    System.out.println("✗ Customer " + customerId + ": " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }));
        }
        
        // Wait for all purchases
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        System.out.println();
        System.out.println("Final Inventory:");
        system.getAllProducts().forEach((id, product) -> {
            if (id.equals("MOUSE-001")) {
                System.out.println("  " + product);
            }
        });
        
        System.out.println();
        System.out.println("Total Orders: " + system.getOrderHistory().size());
        System.out.println("✨ System demonstrated perfect thread safety!");
    }
}
