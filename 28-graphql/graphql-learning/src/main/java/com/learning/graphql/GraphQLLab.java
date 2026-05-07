package com.learning.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@SpringBootApplication
class GraphQLApplication {
    public static void main(String[] args) {
        SpringApplication.run(GraphQLApplication.class, args);
    }
}

@Controller
class ProductGraphQLController {

    @QueryMapping
    public Product productById(String id) {
        return new Product(id, "Sample Product", 99.99);
    }

    record Product(String id, String name, double price) {}
}