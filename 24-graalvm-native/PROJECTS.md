# GraalVM Native Image Module - PROJECTS.md

---

# Mini-Project: Native Image CLI Application

## Project Overview

**Duration**: 4-5 hours  
**Difficulty**: Intermediate  
**Concepts Used**: GraalVM Native Image, Native Compilation, Reflection Configuration, Substrate VM

This mini-project demonstrates building a native executable from a Java application using GraalVM Native Image.

---

## Project Structure

```
24-graalvm-native/
├── pom.xml
└── graalvm-native-image/
    └── src/main/java/com/learning/graalvm/
        └── Lab.java
```

---

## Step 1: POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>graalvm-native-image</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <graalvm.version>23.1.2</graalvm.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.graalvm.buildtools</groupId>
                <artifactId>native-maven-plugin</artifactId>
                <version>0.9.28</version>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## Step 2: Java Application

```java
package com.learning.graalvm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Lab {
    
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(Lab.class)
            .headless(false)
            .run(args);
        
        System.out.println("Native Image Application Started!");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Available Processors: " + Runtime.getRuntime().availableProcessors());
        
        SpringApplication.exit(context, () -> 0);
    }
}
```

---

## Build Instructions

```bash
# Install GraalVM first
# https://www.graalvm.org/downloads/

# Set GRAALVM_HOME
export GRAALVM_HOME=/path/to/graalvm

# Build native image
cd 24-graalvm-native/graalvm-native-image
mvn package -Pnative

# Run native executable
./target/demo
```

---

# Real-World Project: Spring Boot Microservice Native Deployment

This comprehensive project demonstrates building a production-ready Spring Boot microservice as a native executable.

---

## Complete Implementation

```java
// Product Service with MongoDB
package com.example.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository MongoRepository;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;
import java.util.List;

@SpringBootApplication
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}

@Document(collection = "products")
class Product {
    @Id private String id;
    private String name;
    private double price;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}

interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByName(String name);
}

@Service
class ProductService {
    private final ProductRepository repository;
    
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }
    
    public List<Product> search(String name) {
        return repository.findByName(name);
    }
}
```

---

## Build Instructions

```bash
# Build with Native Profile
cd 24-graalvm-native
mvn clean package -DskipTests -Pnative

# Run native executable
./graalvm-native-image/target/product-service

# Verify startup time improvement
time ./graalvm-native-image/target/product-service
```

---

## Benefits

- **Fast startup**: ~100ms vs 2-3 seconds
- **Lower memory**: ~50MB vs 200MB+
- **Small footprint**: Single executable
- **Security**: Reduced attack surface