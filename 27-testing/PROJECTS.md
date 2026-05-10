# Testing Module - PROJECTS.md

---

# Mini-Project: Unit and Integration Testing

## Project Overview

**Duration**: 5-6 hours  
**Difficulty**: Intermediate  
**Concepts Used**: JUnit 5, Mockito, Integration Tests, Test Coverage

This mini-project demonstrates comprehensive testing strategies.

---

## Project Structure

```
27-testing/
├── pom.xml
├── src/main/java/com/learning/
│   ├── service/
│   │   └── ProductService.java
│   └── model/
│       └── Product.java
└── src/test/java/com/learning/
    ├── ProductServiceTest.java
    └── IntegrationTest.java
```

---

## POM.xml

```xml
<project>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
        </dependency>
    </dependencies>
</project>
```

---

## Implementation

```java
// service/ProductService.java
package com.learning.service;

import com.learning.model.Product;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;

@Service
public class ProductService {
    
    private final Map<String, Product> products = new HashMap<>();
    
    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }
    
    public Product getProductById(String id) {
        return products.get(id);
    }
    
    public Product saveProduct(Product product) {
        products.put(product.getId(), product);
        return product;
    }
    
    public void deleteProduct(String id) {
        products.remove(id);
    }
}
```

```java
// test/ProductServiceTest.java
package com.learning;

import com.learning.service.ProductService;
import com.learning.model.Product;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @InjectMocks
    private ProductService productService;
    
    @Test
    void testSaveProduct() {
        Product product = new Product("1", "Laptop", new BigDecimal("1299.99"));
        
        Product saved = productService.saveProduct(product);
        
        assertNotNull(saved);
        assertEquals("Laptop", saved.getName());
    }
    
    @Test
    void testGetAllProducts() {
        productService.saveProduct(new Product("1", "Laptop", new BigDecimal("1299.99")));
        productService.saveProduct(new Product("2", "Phone", new BigDecimal("899.99")));
        
        List<Product> products = productService.getAllProducts();
        
        assertEquals(2, products.size());
    }
    
    @Test
    void testGetProductById() {
        productService.saveProduct(new Product("1", "Laptop", new BigDecimal("1299.99")));
        
        Product product = productService.getProductById("1");
        
        assertNotNull(product);
        assertEquals("Laptop", product.getName());
    }
}
```

```java
// Main.java
package com.learning;

public class Main {
    public static void main(String[] args) {}
}
```

---

## Build Instructions

```bash
cd 27-testing
mvn test
```

---

# Real-World Project: Integration Testing

```java
// IntegrationTest.java
@SpringBootTest
class IntegrationTest {
    
    @Test
    void contextLoads() {
        // Verify application context loads
    }
}
```

---

## Build Instructions

```bash
cd 27-testing
mvn clean test
```