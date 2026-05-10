# GraphQL Module (28-graphql variant) - PROJECTS.md

---

# Mini-Project: GraphQL API with Data Loaders

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: GraphQL Schema, Data Loaders, N+1 Problem Solving, Mutations, Custom Scalars

This mini-project demonstrates GraphQL with Spring Boot and data loaders to solve the N+1 query problem common in GraphQL APIs.

---

## Project Structure

```
28-graphql/
├── pom.xml
├── src/main/kotlin/com/learning/
│   ├── Main.kt
│   ├── schema/
│   │   └── schema.graphqls
│   ├── service/
│   │   └── AuthorService.kt
│   └── dto/
│       └── AuthorDto.kt
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
    <artifactId>graphql-dataloader-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    
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
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
</project>
```

---

## Step 2: GraphQL Schema

```graphql
# schema/schema.graphqls

type Query {
    authors: [Author!]!
    author(id: ID!): Author
    books: [Book!]!
    book(id: ID!): Book
}

type Mutation {
    createAuthor(input: AuthorInput!): Author!
    updateAuthor(id: ID!, input: AuthorInput!): Author
    deleteAuthor(id: ID!): Boolean!
    createBook(input: BookInput!): Book!
    addBookToAuthor(authorId: ID!, bookId: ID!): Author
}

type Author {
    id: ID!
    name: String!
    email: String!
    birthDate: Date
    books: [Book!]!
    bookCount: Int!
}

type Book {
    id: ID!
    title: String!
    isbn: String!
    publishedDate: Date
    author: Author!
    genre: Genre!
}

enum Genre {
    FICTION
    NON_FICTION
    SCIENCE_FICTION
    FANTASY
    MYSTERY
    ROMANCE
}

input AuthorInput {
    name: String!
    email: String!
    birthDate: Date
}

input BookInput {
    title: String!
    isbn: String!
    authorId: ID!
    genre: Genre!
}

scalar Date
```

---

## Step 3: Data Models

```kotlin
// dto/Author.kt
package com.learning.dto

import java.time.LocalDate

data class Author(
    val id: String,
    val name: String,
    val email: String,
    val birthDate: LocalDate?,
    val books: List<Book> = emptyList()
)

data class Book(
    val id: String,
    val title: String,
    val isbn: String,
    val publishedDate: LocalDate?,
    val authorId: String,
    val genre: Genre
)

enum class Genre {
    FICTION,
    NON_FICTION,
    SCIENCE_FICTION,
    FANTASY,
    MYSTERY,
    ROMANCE
}

data class AuthorInput(
    val name: String,
    val email: String,
    val birthDate: LocalDate?
)

data class BookInput(
    val title: String,
    val isbn: String,
    val authorId: String,
    val genre: Genre
)
```

---

## Step 4: Data Loaders (N+1 Solution)

```kotlin
// service/AuthorDataLoader.kt
package com.learning.service

import com.learning.dto.Author
import com.learning.dto.Book
import com.learning.dto.Genre
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

@Configuration
class AuthorDataLoader {
    
    private val authors = mutableMapOf<String, Author>()
    private val books = mutableMapOf<String, Book>()
    
    init {
        // Initialize sample data
        val author1 = Author("1", "Alice Johnson", "alice@example.com", LocalDate.of(1980, 5, 15))
        val author2 = Author("2", "Bob Smith", "bob@example.com", LocalDate.of(1975, 8, 22))
        
        authors["1"] = author1
        authors["2"] = author2
        
        books["1"] = Book(
            "1", "Kotlin in Action", "978-1-234-56789-0", 
            LocalDate.of(2020, 3, 15), "1", Genre.FICTION
        )
        books["2"] = Book(
            "2", "Modern Programming", "978-1-234-56789-1", 
            LocalDate.of(2021, 6, 20), "1", Genre.NON_FICTION
        )
        books["3"] = Book(
            "3", "Java Essentials", "978-1-234-56789-2", 
            LocalDate.of(2019, 1, 10), "2", Genre.NON_FICTION
        )
    }
    
    @Bean
    fun dataLoaderRegistry(): DataLoaderRegistry {
        val registry = DataLoaderRegistry()
        
        // DataLoader for batch loading authors
        val authorLoader = DataLoader<String, Author> { keys ->
            CompletableFuture.supplyAsync {
                keys.map { authors[it] }
            }
        }
        
        // DataLoader for batch loading books by author
        val booksByAuthorLoader = DataLoader<String, List<Book>> { authorIds ->
            CompletableFuture.supplyAsync {
                authorIds.map { authorId ->
                    books.values.filter { it.authorId == authorId }
                }
            }
        }
        
        registry.register("authorLoader", authorLoader)
        registry.register("booksByAuthorLoader", booksByAuthorLoader)
        
        return registry
    }
}
```

---

## Step 5: Resolver with DataLoader

```kotlin
// service/AuthorResolver.kt
package com.learning.service

import com.learning.dto.*
import com.learning.dto.AuthorInput
import com.learning.dto.BookInput
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.springframework.graphql.data.method.annotation.*
import org.springframework.stereotype.Controller
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.CompletableFuture

@Controller
class AuthorResolver(
    private val dataLoaderRegistry: DataLoaderRegistry
) {
    private val authors = mutableMapOf<String, Author>()
    private val books = mutableMapOf<String, Book>()
    
    init {
        // Initialize sample data
        authors["1"] = Author("1", "Alice Johnson", "alice@example.com", LocalDate.of(1980, 5, 15))
        authors["2"] = Author("2", "Bob Smith", "bob@example.com", LocalDate.of(1975, 8, 22))
        
        books["1"] = Book("1", "Kotlin in Action", "978-1-234-56789-0", LocalDate.of(2020, 3, 15), "1", Genre.FICTION)
        books["2"] = Book("2", "Java Programming", "978-1-234-56789-1", LocalDate.of(2021, 6, 20), "1", Genre.NON_FICTION)
        books["3"] = Book("3", "Spring Boot Guide", "978-1-234-56789-2", LocalDate.of(2019, 1, 10), "2", Genre.NON_FICTION)
    }
    
    @QueryMapping
    fun authors(): List<Author> = authors.values.toList()
    
    @QueryMapping
    fun author(@Argument id: String): Author? = authors[id]
    
    @QueryMapping
    fun books(): List<Book> = books.values.toList()
    
    @QueryMapping
    fun book(@Argument id: String): Book? = books[id]
    
    @MutationMapping
    fun createAuthor(@Argument input: AuthorInput): Author {
        val id = UUID.randomUUID().toString()
        val author = Author(id, input.name, input.email, input.birthDate)
        authors[id] = author
        return author
    }
    
    @MutationMapping
    fun updateAuthor(@Argument id: String, @Argument input: AuthorInput): Author? {
        val existing = authors[id] ?: return null
        val updated = existing.copy(name = input.name, email = input.email, birthDate = input.birthDate)
        authors[id] = updated
        return updated
    }
    
    @MutationMapping
    fun deleteAuthor(@Argument id: String): Boolean {
        return authors.remove(id) != null
    }
    
    @MutationMapping
    fun createBook(@Argument input: BookInput): Book {
        val id = UUID.randomUUID().toString()
        val book = Book(id, input.title, input.isbn, LocalDate.now(), input.authorId, input.genre)
        books[id] = book
        return book
    }
    
    @BatchMapping(field = "books", loader = "booksByAuthorLoader")
    fun getBooksByAuthor(keys: List<String>): List<List<Book>> {
        return keys.map { authorId ->
            books.values.filter { it.authorId == authorId }
        }
    }
    
    @SchemaMapping(typeName = "Author", field = "books")
    fun getAuthorBooks(author: Author): List<Book> {
        return books.values.filter { it.authorId == author.id }
    }
    
    @SchemaMapping(typeName = "Author", field = "bookCount")
    fun getBookCount(author: Author): Int {
        return books.values.count { it.authorId == author.id }
    }
}
```

---

## Step 6: Main Application

```kotlin
// Main.kt
package com.learning

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Main::class.java, *args)
        }
    }
}
```

---

## Build Instructions

```bash
cd 28-graphql
mvn clean compile
mvn spring-boot:run

# Test at GraphiQL UI: http://localhost:8080/graphiql

# Example queries:
query {
    authors {
        id
        name
        bookCount
        books {
            title
        }
    }
}
```

---

# Real-World Project: GraphQL Federation

## Project Overview

**Duration**: 8+ hours  
**Difficulty**: Advanced  
**Concepts Used**: GraphQL Federation, Apollo Federation, Custom Resolvers, @key directive, Entity Resolution, Gateway

This comprehensive project implements a federated GraphQL architecture using Apollo Federation, where multiple services contribute to a unified supergraph.

---

## Complete Implementation

```kotlin
// User Service (Subgraph)
package com.learning.user

import org.springframework.graphql.data.method.annotation.*
import org.springframework.stereotype.Controller

// This represents a subgraph in a federated architecture
// The @key directive defines the primary key for federation
// @extends marks entities from other services
// @external marks fields owned by other services

/*
# Schema for User Service
type User @key(id: "id") @extends {
    id: ID! @external
    reviews: [Review!]!
}

type Query {
    me: User
    users: [User!]!
    user(id: ID!): User
}
*/

@Controller
class UserGraphQLController {
    
    private val users = mutableMapOf<String, User>()
    
    @QueryMapping
    fun me(): User = users["1"] ?: User("1", "John Doe", "john@example.com")
    
    @QueryMapping
    fun users(): List<User> = users.values.toList()
    
    @QueryMapping
    fun user(@Argument id: String): User? = users[id]
    
    @SchemaMapping(typeName = "User", field = "reviews")
    fun getUserReviews(user: User): List<Review> {
        // Fetch reviews for user - usually from another service in federation
        return listOf(
            Review("1", user.id, "Great product!", 5),
            Review("2", user.id, "Good value", 4)
        )
    }
}

data class User(val id: String, val name: String, val email: String)
data class Review(val id: String, val userId: String, val text: String, val rating: Int)
```

```kotlin
// Product Service (Subgraph)
package com.learning.product

import org.springframework.graphql.data.method.annotation.*
import org.springframework.stereotype.Controller
import java.math.BigDecimal

/*
# Schema for Product Service
type Product @key(id: "id") {
    id: ID!
    name: String!
    price: Float!
    reviews: [Review!]!
    averageRating: Float!
}

type Review @key(id: "id") {
    id: ID!
    text: String!
    rating: Int!
    product: Product @reference
    user: User @reference
}
*/

@Controller
class ProductGraphQLController {
    
    private val products = mutableMapOf<String, Product>()
    private val reviews = mutableMapOf<String, Review>()
    
    init {
        products["1"] = Product("1", "Kotlin Book", BigDecimal("29.99"))
        products["2"] = Product("2", "Java Course", BigDecimal("49.99"))
        
        reviews["1"] = Review("1", "Great book!", 5, "1", "user-1")
        reviews["2"] = Review("2", "Very helpful", 4, "1", "user-2")
    }
    
    @QueryMapping
    fun products(): List<Product> = products.values.toList()
    
    @QueryMapping
    fun product(@Argument id: String): Product? = products[id]
    
    @SchemaMapping(typeName = "Product", field = "reviews")
    fun getProductReviews(product: Product): List<Review> {
        return reviews.values.filter { it.productId == product.id }
    }
    
    @SchemaMapping(typeName = "Product", field = "averageRating")
    fun getAverageRating(product: Product): Float {
        return reviews.values
            .filter { it.productId == product.id }
            .map { it.rating }
            .average()
            .toFloat()
    }
    
    @SchemaMapping(typeName = "Review", field = "product")
    fun getReviewProduct(review: Review): Product? {
        return products[review.productId]
    }
}

data class Product(val id: String, val name: String, val price: BigDecimal)

data class Review(
    val id: String, 
    val text: String, 
    val rating: Int, 
    val productId: String, 
    val userId: String
)
```

---

## Federation Gateway

```kotlin
// Gateway Configuration
package com.learning.gateway

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/*
# Apollo Gateway Configuration (YAML)
# federation-config.yaml

services:
  - name: users
    url: http://user-service:8081/graphql
    type: user
  - name: products
    url: http://product-service:8082/graphql
    type: product

supergraph:
  routes:
    - path: /graphql
      service: users
    - path: /graphql
      service: products
*/

@Configuration
class GatewayConfig {
    
    @Bean
    fun routes(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route("graphql") { r ->
                r.path("/graphql")
                    .uri("http://localhost:8080")
            }
            .build()
    }
}
```

---

## Subscription Support

```kotlin
// Subscription Controller
package com.learning.subscription

import org.springframework.graphql.data.method.annotation.*
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@Controller
class SubscriptionController {
    
    @SubscriptionMapping
    fun onNewReview(): Flux<ReviewEvent> {
        return Flux.interval(Duration.ofSeconds(5))
            .map { 
                ReviewEvent(
                    "review-${it}",
                    "New review for product ${it % 3}",
                    (it % 5 + 1).toInt()
                )
            }
    }
    
    @SubscriptionMapping
    fun onProductUpdate(): Flux<ProductUpdate> {
        return Flux.interval(Duration.ofSeconds(10))
            .map {
                ProductUpdate(
                    "product-${it % 3}",
                    listOf(PriceChanged(BigDecimal(100 + it), "New price"))
                )
            }
    }
}

data class ReviewEvent(
    val reviewId: String,
    val text: String,
    val rating: Int
)

data class ProductUpdate(
    val productId: String,
    val changes: List<Any>
)
```

---

## Custom Scalars

```kotlin
// Custom Date Scalar
package com.learning.scalar

import graphql.language.StringValue
import graphql.schema.*
import org.springframework.graphql.scalar.GraphqlScalars
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Configuration
class ScalarConfiguration {
    
    @Bean
    fun dateScalar(): GraphQLScalarType {
        return GraphQLScalarType.newScalar()
            .name("Date")
            .description("Java LocalDate")
            .coercing(object : Coercing<LocalDate, String> {
                override fun serialize(dataFetcherResult: Any): String {
                    return (dataFetcherResult as LocalDate).format(DateTimeFormatter.ISO_DATE)
                }
                
                override fun parseValue(input: Any): LocalDate {
                    return LocalDate.parse(input as String, DateTimeFormatter.ISO_DATE)
                }
                
                override fun parseLiteral(input: Any): LocalDate {
                    return LocalDate.parse((input as StringValue).getValue(), DateTimeFormatter.ISO_DATE)
                }
            })
            .build()
    }
}
```

---

## Error Handling

```kotlin
// GraphQL Exception Handler
package com.learning.exception

import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.schema.DataFetcherEnvironment

class CustomExceptionHandler : DataFetcherExceptionHandler {
    
    override fun handle(exception: DataFetcherExceptionHandlerParameters) {
        val exception_ = exception.exception
        
        // Custom error classification
        val error = when (exception_) {
            is NotFoundException -> ClassicException(
                ClassicException.Type.DataFetchingException,
                "NOT_FOUND",
                exception_.message ?: "Resource not found"
            )
            is ValidationException -> ClassicException(
                ClassicException.Type.ValidationError,
                "VALIDATION_ERROR",
                exception_.message ?: "Validation failed"
            )
            else -> ClassicException(
                ClassicException.Type.DataFetchingException,
                "INTERNAL_ERROR",
                "An unexpected error occurred"
            )
        }
        
        exception_.completeExceptionally(error)
    }
}

class NotFoundException(message: String) : RuntimeException(message)
class ValidationException(message: String) : RuntimeException(message)
```

---

## Build Instructions (Real-World Project)

```bash
cd 28-graphql

# Run user service
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"

# Run product service
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8082"

# Run gateway
# npm install @apollo/gateway @apollo/subgraphs
# node gateway.js

# GraphQL Playground: http://localhost:4000
```

---

## Summary

This module demonstrates:

1. **Data Loaders**: Solving N+1 query problems in GraphQL
2. **Mutations**: Creating, updating, deleting data
3. **Custom Scalars**: Date, DateTime, JSON handling
4. **Federation**: Multi-service GraphQL with Apollo Federation
5. **Subscriptions**: Real-time updates with WebSocket
6. **Error Handling**: Custom exception handling in GraphQL

These skills enable building production-ready GraphQL APIs with proper performance characteristics and scalable architectures.