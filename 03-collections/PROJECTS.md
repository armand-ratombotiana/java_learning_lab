# Java Collections Module - PROJECTS.md

---

# 🎯 Mini-Project: Inventory Management System

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Beginner-Intermediate  
**Concepts Used**: List, Map, Set, Collections API, Generic Types, Iteration

This project demonstrates core Java Collections concepts through a practical inventory management system.

---

## Project Structure

```
03-collections/src/main/java/com/learning/project/
├── Main.java
├── model/
│   ├── Product.java
│   └── Category.java
├── service/
│   └── InventoryService.java
├── repository/
│   └── ProductRepository.java
└── ui/
    └── InventoryMenu.java
```

---

## Step 1: Product Model

```java
// model/Product.java
package com.learning.project.model;

public class Product {
    private String id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private Category category;
    
    public Product(String id, String name, String description, 
                   double price, int quantity, Category category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    public boolean isInStock() {
        return quantity > 0;
    }
    
    public boolean isLowStock() {
        return quantity > 0 && quantity <= 10;
    }
    
    public double getTotalValue() {
        return price * quantity;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id.equals(product.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - $%.2f (Qty: %d) [%s]",
            id, name, price, quantity, category.getName());
    }
}
```

---

## Step 2: Category Model

```java
// model/Category.java
package com.learning.project.model;

public class Category {
    private String code;
    private String name;
    private String description;
    
    public Category(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
    
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return code.equals(category.code);
    }
    
    @Override
    public int hashCode() {
        return code.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s", code, name);
    }
}
```

---

## Step 3: Product Repository using Collections

```java
// repository/ProductRepository.java
package com.learning.project.repository;

import com.learning.project.model.Product;
import com.learning.project.model.Category;
import java.util.*;

public class ProductRepository {
    private List<Product> productList;
    private Map<String, Product> productMap;
    private Set<Category> categories;
    private Map<String, Set<Product>> productsByCategory;
    
    public ProductRepository() {
        this.productList = new ArrayList<>();
        this.productMap = new HashMap<>();
        this.categories = new HashSet<>();
        this.productsByCategory = new HashMap<>();
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        Category electronics = new Category("ELEC", "Electronics", "Electronic devices");
        Category clothing = new Category("CLOTH", "Clothing", "Apparel and accessories");
        Category food = new Category("FOOD", "Food", "Food and beverages");
        
        categories.addAll(Arrays.asList(electronics, clothing, food));
        
        addProduct(new Product("P001", "Laptop", "High-performance laptop", 999.99, 15, electronics));
        addProduct(new Product("P002", "Smartphone", "Latest model phone", 699.99, 25, electronics));
        addProduct(new Product("P003", "T-Shirt", "Cotton t-shirt", 29.99, 100, clothing));
        addProduct(new Product("P004", "Jeans", "Designer jeans", 79.99, 50, clothing));
        addProduct(new Product("P005", "Coffee Beans", "Premium coffee", 19.99, 200, food));
        addProduct(new Product("P006", "Chocolate Bar", "Dark chocolate", 3.99, 500, food));
        addProduct(new Product("P007", "Tablet", "10-inch tablet", 449.99, 10, electronics));
    }
    
    public void addProduct(Product product) {
        productList.add(product);
        productMap.put(product.getId(), product);
        
        Category category = product.getCategory();
        productsByCategory.computeIfAbsent(category.getCode(), k -> new HashSet<>())
            .add(product);
    }
    
    public boolean updateProduct(Product product) {
        if (productMap.containsKey(product.getId())) {
            int index = -1;
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getId().equals(product.getId())) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                productList.set(index, product);
                productMap.put(product.getId(), product);
                return true;
            }
        }
        return false;
    }
    
    public boolean removeProduct(String productId) {
        Product product = productMap.remove(productId);
        if (product != null) {
            productList.remove(product);
            Category category = product.getCategory();
            Set<Product> categoryProducts = productsByCategory.get(category.getCode());
            if (categoryProducts != null) {
                categoryProducts.remove(product);
            }
            return true;
        }
        return false;
    }
    
    public Product findById(String id) {
        return productMap.get(id);
    }
    
    public List<Product> findAll() {
        return new ArrayList<>(productList);
    }
    
    public List<Product> findByCategory(String categoryCode) {
        Set<Product> products = productsByCategory.get(categoryCode);
        return products != null ? new ArrayList<>(products) : new ArrayList<>();
    }
    
    public List<Product> findByPriceRange(double min, double max) {
        List<Product> result = new ArrayList<>();
        for (Product p : productList) {
            if (p.getPrice() >= min && p.getPrice() <= max) {
                result.add(p);
            }
        }
        return result;
    }
    
    public List<Product> findInStock() {
        List<Product> result = new ArrayList<>();
        for (Product p : productList) {
            if (p.isInStock()) {
                result.add(p);
            }
        }
        return result;
    }
    
    public List<Product> findLowStock() {
        List<Product> result = new ArrayList<>();
        for (Product p : productList) {
            if (p.isLowStock()) {
                result.add(p);
            }
        }
        return result;
    }
    
    public Set<Category> getCategories() {
        return new HashSet<>(categories);
    }
    
    public int getTotalProductCount() {
        return productList.size();
    }
    
    public double getTotalInventoryValue() {
        double total = 0;
        for (Product p : productList) {
            total += p.getTotalValue();
        }
        return total;
    }
}
```

---

## Step 4: Inventory Service

```java
// service/InventoryService.java
package com.learning.project.service;

import com.learning.project.model.Product;
import com.learning.project.model.Category;
import com.learning.project.repository.ProductRepository;
import java.util.*;

public class InventoryService {
    private ProductRepository repository;
    
    public InventoryService() {
        this.repository = new ProductRepository();
    }
    
    public void addProduct(Product product) {
        repository.addProduct(product);
    }
    
    public boolean updateProduct(Product product) {
        return repository.updateProduct(product);
    }
    
    public boolean removeProduct(String productId) {
        return repository.removeProduct(productId);
    }
    
    public Product getProduct(String id) {
        return repository.findById(id);
    }
    
    public List<Product> getAllProducts() {
        return repository.findAll();
    }
    
    public List<Product> getProductsByCategory(String categoryCode) {
        return repository.findByCategory(categoryCode);
    }
    
    public List<Product> getProductsInPriceRange(double min, double max) {
        return repository.findByPriceRange(min, max);
    }
    
    public List<Product> getLowStockProducts() {
        return repository.findLowStock();
    }
    
    public boolean adjustQuantity(String productId, int adjustment) {
        Product product = repository.findById(productId);
        if (product == null) return false;
        
        int newQuantity = product.getQuantity() + adjustment;
        if (newQuantity < 0) return false;
        
        product.setQuantity(newQuantity);
        return repository.updateProduct(product);
    }
    
    public List<Product> getSortedByPrice(boolean ascending) {
        List<Product> products = repository.findAll();
        if (ascending) {
            products.sort(Comparator.comparingDouble(Product::getPrice));
        } else {
            products.sort(Comparator.comparingDouble(Product::getPrice).reversed());
        }
        return products;
    }
    
    public List<Product> getSortedByName() {
        List<Product> products = repository.findAll();
        products.sort(Comparator.comparing(Product::getName));
        return products;
    }
    
    public List<Product> getSortedByQuantity() {
        List<Product> products = repository.findAll();
        products.sort(Comparator.comparingInt(Product::getQuantity).reversed());
        return products;
    }
    
    public Map<String, Integer> getCategoryProductCount() {
        Map<String, Integer> counts = new HashMap<>();
        for (Product p : repository.findAll()) {
            String category = p.getCategory().getCode();
            counts.put(category, counts.getOrDefault(category, 0) + 1);
        }
        return counts;
    }
    
    public Map<String, Double> getCategoryValue() {
        Map<String, Double> values = new HashMap<>();
        for (Product p : repository.findAll()) {
            String category = p.getCategory().getCode();
            values.put(category, values.getOrDefault(category, 0.0) + p.getTotalValue());
        }
        return values;
    }
    
    public Set<Category> getCategories() {
        return repository.getCategories();
    }
    
    public int getTotalProductCount() {
        return repository.getTotalProductCount();
    }
    
    public double getTotalInventoryValue() {
        return repository.getTotalInventoryValue();
    }
}
```

---

## Step 5: Menu Interface

```java
// ui/InventoryMenu.java
package com.learning.project.ui;

import com.learning.project.model.Product;
import com.learning.project.model.Category;
import com.learning.project.service.InventoryService;
import java.util.*;

public class InventoryMenu {
    private Scanner scanner;
    private InventoryService service;
    private boolean running;
    
    public InventoryMenu() {
        this.scanner = new Scanner(System.in);
        this.service = new InventoryService();
        this.running = true;
    }
    
    public void start() {
        System.out.println("\n📦 INVENTORY MANAGEMENT SYSTEM");
        System.out.println("===============================");
        
        while (running) {
            displayMenu();
            handleChoice(getChoice());
        }
    }
    
    private void displayMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. View All Products");
        System.out.println("2. View by Category");
        System.out.println("3. Search Product");
        System.out.println("4. Add Product");
        System.out.println("5. Update Product");
        System.out.println("6. Remove Product");
        System.out.println("7. Adjust Quantity");
        System.out.println("8. View Low Stock Items");
        System.out.println("9. Price Range Search");
        System.out.println("10. Sort Products");
        System.out.println("11. Category Statistics");
        System.out.println("12. Inventory Summary");
        System.out.println("13. Exit");
        System.out.print("\nChoice: ");
    }
    
    private int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private void handleChoice(int choice) {
        switch (choice) {
            case 1 -> viewAllProducts();
            case 2 -> viewByCategory();
            case 3 -> searchProduct();
            case 4 -> addProduct();
            case 5 -> updateProduct();
            case 6 -> removeProduct();
            case 7 -> adjustQuantity();
            case 8 -> viewLowStock();
            case 9 -> priceRangeSearch();
            case 10 -> sortProducts();
            case 11 -> categoryStats();
            case 12 -> inventorySummary();
            case 13 -> { System.out.println("Goodbye!"); running = false; }
            default -> System.out.println("Invalid choice!");
        }
    }
    
    private void viewAllProducts() {
        var products = service.getAllProducts();
        printProducts(products);
    }
    
    private void viewByCategory() {
        System.out.println("\nCategories:");
        for (var cat : service.getCategories()) {
            System.out.println("  " + cat);
        }
        System.out.print("Enter category code: ");
        String code = scanner.nextLine().trim();
        
        var products = service.getProductsByCategory(code);
        printProducts(products);
    }
    
    private void searchProduct() {
        System.out.print("Enter product ID: ");
        String id = scanner.nextLine().trim();
        
        Product p = service.getProduct(id);
        if (p != null) {
            System.out.println("\n" + p);
        } else {
            System.out.println("Product not found!");
        }
    }
    
    private void addProduct() {
        System.out.print("Product ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Description: ");
        String desc = scanner.nextLine().trim();
        System.out.print("Price: ");
        double price = getDouble();
        System.out.print("Quantity: ");
        int qty = getInt();
        System.out.print("Category Code: ");
        String catCode = scanner.nextLine().trim();
        
        Category category = findCategory(catCode);
        if (category == null) {
            System.out.println("Invalid category!");
            return;
        }
        
        Product product = new Product(id, name, desc, price, qty, category);
        service.addProduct(product);
        System.out.println("Product added!");
    }
    
    private void updateProduct() {
        System.out.print("Product ID: ");
        String id = scanner.nextLine().trim();
        
        Product p = service.getProduct(id);
        if (p == null) {
            System.out.println("Product not found!");
            return;
        }
        
        System.out.println("Current: " + p);
        System.out.print("New Price: ");
        double price = getDouble();
        if (price > 0) p.setPrice(price);
        
        System.out.print("New Quantity: ");
        int qty = getInt();
        if (qty >= 0) p.setQuantity(qty);
        
        service.updateProduct(p);
        System.out.println("Product updated!");
    }
    
    private void removeProduct() {
        System.out.print("Product ID: ");
        String id = scanner.nextLine().trim();
        
        if (service.removeProduct(id)) {
            System.out.println("Product removed!");
        } else {
            System.out.println("Product not found!");
        }
    }
    
    private void adjustQuantity() {
        System.out.print("Product ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Adjustment (+/-): ");
        int adj = getInt();
        
        if (service.adjustQuantity(id, adj)) {
            System.out.println("Quantity adjusted!");
        } else {
            System.out.println("Failed to adjust quantity!");
        }
    }
    
    private void viewLowStock() {
        var products = service.getLowStockProducts();
        System.out.println("\n=== LOW STOCK ITEMS ===");
        printProducts(products);
    }
    
    private void priceRangeSearch() {
        System.out.print("Min Price: ");
        double min = getDouble();
        System.out.print("Max Price: ");
        double max = getDouble();
        
        var products = service.getProductsInPriceRange(min, max);
        printProducts(products);
    }
    
    private void sortProducts() {
        System.out.println("Sort by: 1. Price (Asc) 2. Price (Desc) 3. Name 4. Quantity");
        int choice = getChoice();
        
        List<Product> products = switch (choice) {
            case 1 -> service.getSortedByPrice(true);
            case 2 -> service.getSortedByPrice(false);
            case 3 -> service.getSortedByName();
            case 4 -> service.getSortedByQuantity();
            default -> service.getAllProducts();
        };
        
        printProducts(products);
    }
    
    private void categoryStats() {
        var counts = service.getCategoryProductCount();
        var values = service.getCategoryValue();
        
        System.out.println("\n=== CATEGORY STATISTICS ===");
        for (var entry : counts.entrySet()) {
            System.out.printf("%s: %d products, $%.2f total value%n",
                entry.getKey(), entry.getValue(), values.get(entry.getKey()));
        }
    }
    
    private void inventorySummary() {
        System.out.println("\n=== INVENTORY SUMMARY ===");
        System.out.println("Total Products: " + service.getTotalProductCount());
        System.out.printf("Total Value: $%.2f%n", service.getTotalInventoryValue());
        
        long inStock = service.getAllProducts().stream()
            .filter(Product::isInStock).count();
        System.out.println("In Stock: " + inStock);
        
        long lowStock = service.getLowStockProducts().size();
        System.out.println("Low Stock: " + lowStock);
    }
    
    private Category findCategory(String code) {
        return service.getCategories().stream()
            .filter(c -> c.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElse(null);
    }
    
    private void printProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        for (Product p : products) {
            System.out.println(p);
        }
    }
    
    private int getInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private double getDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public static void main(String[] args) {
        new InventoryMenu().start();
    }
}
```

---

## Running the Mini-Project

```bash
cd 03-collections
javac -d out -sourcepath src src/com/learning/project/**/*.java src/com/learning/project/*.java
java -cp out com.learning.project.ui.InventoryMenu
```

---

## Sample Output

```
📦 INVENTORY MANAGEMENT SYSTEM
===============================

--- MAIN MENU ---
1. View All Products
...

Choice: 1

[P001] Laptop - $999.99 (Qty: 15) [Electronics]
[P002] Smartphone - $699.99 (Qty: 25) [Electronics]
...
```

---

## Collections Concepts Demonstrated

| Concept | Implementation |
|---------|----------------|
| **List** | ArrayList for ordered product storage |
| **Map** | HashMap for O(1) product lookup by ID |
| **Set** | HashSet for unique categories |
| **Iteration** | Enhanced for-loop, forEach |
| **Sorting** | Comparator with lambda expressions |
| **Filtering** | Stream-based filtering |

---

# 🚀 Real-World Project: Enterprise Inventory Management System

## Project Overview

**Duration**: 12-16 hours  
**Difficulty**: Advanced  
**Concepts Used**: Multi-level Collections, Caching, Search Algorithms, Data Structures

This project implements a production-ready inventory system with advanced search, caching, and reporting capabilities.

---

## Project Structure

```
03-collections/src/main/java/com/learning/project/
├── Main.java
├── model/
│   ├── Product.java
│   ├── Category.java
│   ├── Supplier.java
│   └── Warehouse.java
├── service/
│   ├── InventoryService.java
│   ├── SearchService.java
│   └── ReportService.java
├── repository/
│   ├── ProductRepository.java
│   ├── SupplierRepository.java
│   └── WarehouseRepository.java
├── cache/
│   └── InventoryCache.java
├── search/
│   ├── ProductSearchEngine.java
│   └── SearchCriteria.java
├── ui/
│   └── InventoryMenu.java
└── util/
    └── CollectionUtils.java
```

---

## Step 1: Supplier and Warehouse Models

```java
// model/Supplier.java
package com.learning.project.model;

import java.time.LocalDateTime;
import java.util.*;

public class Supplier {
    private String id;
    private String name;
    private String contactEmail;
    private String phone;
    private List<Product> suppliedProducts;
    private double rating;
    
    public Supplier(String id, String name, String contactEmail, String phone) {
        this.id = id;
        this.name = name;
        this.contactEmail = contactEmail;
        this.phone = phone;
        this.suppliedProducts = new ArrayList<>();
        this.rating = 5.0;
    }
    
    public void addProduct(Product product) {
        suppliedProducts.add(product);
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getContactEmail() { return contactEmail; }
    public String getPhone() { return phone; }
    public List<Product> getSuppliedProducts() { return suppliedProducts; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
}

// model/Warehouse.java
package com.learning.project.model;

import java.util.*;

public class Warehouse {
    private String id;
    private String name;
    private String location;
    private Map<String, Integer> productQuantities;
    private int capacity;
    
    public Warehouse(String id, String name, String location, int capacity) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.productQuantities = new HashMap<>();
        this.capacity = capacity;
    }
    
    public boolean addProduct(String productId, int quantity) {
        int current = productQuantities.getOrDefault(productId, 0);
        if (current + quantity > capacity) return false;
        productQuantities.put(productId, current + quantity);
        return true;
    }
    
    public boolean removeProduct(String productId, int quantity) {
        int current = productQuantities.getOrDefault(productId, 0);
        if (current < quantity) return false;
        productQuantities.put(productId, current - quantity);
        return true;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public Map<String, Integer> getProductQuantities() { return productQuantities; }
    public int getCapacity() { return capacity; }
}
```

---

## Step 2: Search Criteria

```java
// search/SearchCriteria.java
package com.learning.project.search;

import java.util.*;

public class SearchCriteria {
    private String nameContains;
    private String categoryCode;
    private Double minPrice;
    private Double maxPrice;
    private Integer minQuantity;
    private Integer maxQuantity;
    private String supplierId;
    private boolean inStockOnly;
    
    private SearchCriteria(Builder builder) {
        this.nameContains = builder.nameContains;
        this.categoryCode = builder.categoryCode;
        this.minPrice = builder.minPrice;
        this.maxPrice = builder.maxPrice;
        this.minQuantity = builder.minQuantity;
        this.maxQuantity = builder.maxQuantity;
        this.supplierId = builder.supplierId;
        this.inStockOnly = builder.inStockOnly;
    }
    
    public static class Builder {
        private String nameContains;
        private String categoryCode;
        private Double minPrice;
        private Double maxPrice;
        private Integer minQuantity;
        private Integer maxQuantity;
        private String supplierId;
        private boolean inStockOnly;
        
        public Builder nameContains(String name) {
            this.nameContains = name;
            return this;
        }
        
        public Builder category(String code) {
            this.categoryCode = code;
            return this;
        }
        
        public Builder priceRange(double min, double max) {
            this.minPrice = min;
            this.maxPrice = max;
            return this;
        }
        
        public Builder quantityRange(int min, int max) {
            this.minQuantity = min;
            this.maxQuantity = max;
            return this;
        }
        
        public Builder supplier(String id) {
            this.supplierId = id;
            return this;
        }
        
        public Builder inStockOnly() {
            this.inStockOnly = true;
            return this;
        }
        
        public SearchCriteria build() {
            return new SearchCriteria(this);
        }
    }
    
    // Getters
    public String getNameContains() { return nameContains; }
    public String getCategoryCode() { return categoryCode; }
    public Double getMinPrice() { return minPrice; }
    public Double getMaxPrice() { return maxPrice; }
    public Integer getMinQuantity() { return minQuantity; }
    public Integer getMaxQuantity() { return maxQuantity; }
    public String getSupplierId() { return supplierId; }
    public boolean isInStockOnly() { return inStockOnly; }
}
```

---

## Step 3: Search Engine Implementation

```java
// search/ProductSearchEngine.java
package com.learning.project.search;

import com.learning.project.model.Product;
import java.util.*;
import java.util.stream.Collectors;

public class ProductSearchEngine {
    private final Map<String, Product> productIndex;
    private final Map<String, List<Product>> categoryIndex;
    private final Map<String, List<Product>> nameIndex;
    
    public ProductSearchEngine(Map<String, Product> products) {
        this.productIndex = new HashMap<>(products);
        this.categoryIndex = new HashMap<>();
        this.nameIndex = new HashMap<>();
        
        buildIndexes();
    }
    
    private void buildIndexes() {
        for (Product p : productIndex.values()) {
            categoryIndex.computeIfAbsent(p.getCategory().getCode(), k -> new ArrayList<>())
                .add(p);
            
            String[] words = p.getName().toLowerCase().split("\\s+");
            for (String word : words) {
                nameIndex.computeIfAbsent(word, k -> new ArrayList<>()).add(p);
            }
        }
    }
    
    public List<Product> search(SearchCriteria criteria) {
        return productIndex.values().stream()
            .filter(p -> matchesCriteria(p, criteria))
            .collect(Collectors.toList());
    }
    
    private boolean matchesCriteria(Product p, SearchCriteria criteria) {
        if (criteria.getNameContains() != null) {
            if (!p.getName().toLowerCase().contains(
                criteria.getNameContains().toLowerCase())) {
                return false;
            }
        }
        
        if (criteria.getCategoryCode() != null) {
            if (!p.getCategory().getCode().equals(criteria.getCategoryCode())) {
                return false;
            }
        }
        
        if (criteria.getMinPrice() != null) {
            if (p.getPrice() < criteria.getMinPrice()) return false;
        }
        
        if (criteria.getMaxPrice() != null) {
            if (p.getPrice() > criteria.getMaxPrice()) return false;
        }
        
        if (criteria.getMinQuantity() != null) {
            if (p.getQuantity() < criteria.getMinQuantity()) return false;
        }
        
        if (criteria.getMaxQuantity() != null) {
            if (p.getQuantity() > criteria.getMaxQuantity()) return false;
        }
        
        if (criteria.isInStockOnly()) {
            if (!p.isInStock()) return false;
        }
        
        return true;
    }
    
    public List<Product> searchByName(String name) {
        return nameIndex.getOrDefault(name.toLowerCase(), new ArrayList<>());
    }
    
    public List<Product> searchByCategory(String categoryCode) {
        return categoryIndex.getOrDefault(categoryCode, new ArrayList<>());
    }
    
    public Map<String, List<Product>> getGroupedByCategory() {
        return new HashMap<>(categoryIndex);
    }
}
```

---

## Step 4: Cache Implementation

```java
// cache/InventoryCache.java
package com.learning.project.cache;

import com.learning.project.model.Product;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;

public class InventoryCache {
    private final Map<String, CachedProduct> cache;
    private final long ttlMillis;
    
    private static class CachedProduct {
        final Product product;
        final long cachedAt;
        
        CachedProduct(Product product) {
            this.product = product;
            this.cachedAt = System.currentTimeMillis();
        }
        
        boolean isExpired(long ttl) {
            return System.currentTimeMillis() - cachedAt > ttl;
        }
    }
    
    public InventoryCache(long ttlSeconds) {
        this.cache = new ConcurrentHashMap<>();
        this.ttlMillis = ttlSeconds * 1000;
    }
    
    public void put(String key, Product product) {
        cache.put(key, new CachedProduct(product));
    }
    
    public Product get(String key) {
        CachedProduct cached = cache.get(key);
        if (cached == null) return null;
        if (cached.isExpired(ttlMillis)) {
            cache.remove(key);
            return null;
        }
        return cached.product;
    }
    
    public void remove(String key) {
        cache.remove(key);
    }
    
    public void clear() {
        cache.clear();
    }
    
    public int size() {
        return cache.size();
    }
    
    public void cleanup() {
        cache.entrySet().removeIf(e -> e.getValue().isExpired(ttlMillis));
    }
}
```

---

## Step 5: Complete Enterprise Service

```java
// service/InventoryService.java (Extended)
package com.learning.project.service;

import com.learning.project.model.*;
import com.learning.project.repository.ProductRepository;
import com.learning.project.search.*;
import com.learning.project.cache.InventoryCache;
import java.util.*;
import java.util.stream.Collectors;

public class InventoryService {
    private ProductRepository repository;
    private InventoryCache cache;
    private ProductSearchEngine searchEngine;
    
    public InventoryService() {
        this.repository = new ProductRepository();
        this.cache = new InventoryCache(300); // 5 minute TTL
        rebuildSearchIndex();
    }
    
    private void rebuildSearchIndex() {
        Map<String, Product> products = new HashMap<>();
        for (Product p : repository.findAll()) {
            products.put(p.getId(), p);
        }
        this.searchEngine = new ProductSearchEngine(products);
    }
    
    public Product getProduct(String id) {
        Product cached = cache.get(id);
        if (cached != null) return cached;
        
        Product product = repository.findById(id);
        if (product != null) {
            cache.put(id, product);
        }
        return product;
    }
    
    public List<Product> search(SearchCriteria criteria) {
        return searchEngine.search(criteria);
    }
    
    public List<Product> searchByName(String name) {
        return searchEngine.searchByName(name);
    }
    
    public Map<String, List<Product>> getProductsGroupedByCategory() {
        return searchEngine.getGroupedByCategory();
    }
    
    public List<Product> getTopProducts(int count, boolean byValue) {
        return repository.findAll().stream()
            .sorted(byValue ? 
                Comparator.comparingDouble(Product::getTotalValue).reversed() :
                Comparator.comparingInt(Product::getQuantity).reversed())
            .limit(count)
            .collect(Collectors.toList());
    }
    
    public void invalidateCache() {
        cache.clear();
    }
    
    public void invalidateCache(String productId) {
        cache.remove(productId);
    }
    
    // ... other methods
}
```

---

## Running the Real-World Project

```bash
cd 03-collections
javac -d out -sourcepath src src/com/learning/project/**/*.java src/com/learning/project/*.java
java -cp out com.learning.project.ui.InventoryMenu
```

---

## Advanced Collections Concepts

| Concept | Implementation |
|---------|----------------|
| **Concurrent Collections** | ConcurrentHashMap in cache |
| **Builder Pattern** | SearchCriteria.Builder |
| **Indexing** | Multi-level Map for fast lookup |
| **Caching** | TTL-based cache with cleanup |
| **Stream API** | Advanced filtering and aggregation |

---

## Extensions

1. Add database persistence with JPA
2. Implement Redis caching
3. Add full-text search with Elasticsearch
4. Implement real-time notifications
5. Add REST API endpoints

---

## Next Steps

After completing this module:
- **Module 4**: Refactor using Stream API
- **Module 8**: Add generic type-safe wrappers
- **Module 5**: Add concurrent access support