# GraphQL Module - PROJECTS.md

---

# Mini-Project: GraphQL API Implementation

## Project Overview

**Duration**: 4-5 hours  
**Difficulty**: Intermediate  
**Concepts Used**: GraphQL Schema, Resolvers, Queries, Mutations, Data Fetchers

This mini-project demonstrates GraphQL with Spring Boot for building flexible APIs.

---

## Project Structure

```
26-graphql/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── schema/
│   │   └── schema.graphqls
│   ├── resolver/
│   │   └── QueryResolver.java
│   └── model/
│       └── Product.java
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
            <artifactId>spring-boot-starter-graphql</artifactId>
        </dependency>
    </dependencies>
</project>
```

---

## Implementation

```java
// schema/schema.graphqls
type Query {
    products: [Product]
    product(id: ID!): Product
}

type Mutation {
    createProduct(name: String!, price: Float!): Product
}

type Product {
    id: ID!
    name: String!
    price: Float!
}
```

```java
// resolver/QueryResolver.java
package com.learning.resolver;

import com.learning.model.Product;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class QueryResolver {
    
    private final List<Product> products = List.of(
        new Product("1", "Laptop", 1299.99),
        new Product("2", "Phone", 899.99)
    );
    
    @QueryMapping
    public List<Product> products() {
        return products;
    }
    
    @QueryMapping
    public Product product(@Argument String id) {
        return products.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
}
```

```java
// Main.java
package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

---

## Build Instructions

```bash
cd 26-graphql
mvn spring-boot:run
# Access: http://localhost:8080/graphiql
```

---

# Real-World Project

```java
// Advanced GraphQL with Mutations
@MutationMapping
public Product createProduct(@Argument InputProduct input) {
    return new Product(input.name(), input.price());
}
```

---

## Build Instructions

```bash
cd 26-graphql
mvn clean compile
mvn spring-boot:run
```