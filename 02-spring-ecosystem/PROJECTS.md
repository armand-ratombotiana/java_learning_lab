# Spring Ecosystem Projects

This directory contains projects exploring the broader Spring ecosystem beyond Spring Boot, including Spring Framework core, Spring Cloud, Spring Security, Spring Data, and integration patterns.

## Mini-Project: Spring Data JPA with Complex Queries (2-4 hours)

### Overview

Build a comprehensive data access layer using Spring Data JPA with advanced query techniques, custom repositories, auditing, and query method generation. This project demonstrates Spring Data's powerful features for enterprise data management.

### Project Structure

```
spring-data-jpa-projects/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── springecosystem/
    │   │           └── data/
    │   │               ├── SpringDataApplication.java
    │   │               ├── entity/
    │   │               ├── repository/
    │   │               ├── service/
    │   │               ├── controller/
    │   │               └── config/
    │   └── resources/
    │       └── application.yml
```

### Implementation

```java
package com.springecosystem.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories
public class SpringDataApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringDataApplication.class, args);
    }
}
```

```java
package com.springecosystem.data.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
@EntityListeners(AuditingEntityListener.class)
public class Author {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(length = 1000)
    private String bio;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    @Version
    private Long version;

    public Author() {
    }

    public Author(String name, String email, String bio) {
        this.name = name;
        this.email = email;
        this.bio = bio;
    }

    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.setAuthor(null);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public String getUpdatedBy() { return updatedBy; }
}
```

```java
package com.springecosystem.data.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@EntityListeners(AuditingEntityListener.class)
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @Column(name = "page_count")
    private Integer pageCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @ManyToMany
    @JoinTable(
        name = "book_categories",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private java.util.Set<Category> categories = new java.util.HashSet<>();

    @OneToOne(mappedBy = "book")
    private BookDetails details;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Book() {
    }

    public Book(String title, String description, BigDecimal price, String isbn) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.isbn = isbn;
        this.status = BookStatus.AVAILABLE;
    }

    public void addCategory(Category category) {
        categories.add(category);
        category.getBooks().add(this);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
        category.getBooks().remove(this);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public LocalDate getPublishedDate() { return publishedDate; }
    public void setPublishedDate(LocalDate publishedDate) { this.publishedDate = publishedDate; }

    public Integer getPageCount() { return pageCount; }
    public void setPageCount(Integer pageCount) { this.pageCount = pageCount; }

    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }

    public Publisher getPublisher() { return publisher; }
    public void setPublisher(Publisher publisher) { this.publisher = publisher; }

    public java.util.Set<Category> getCategories() { return categories; }
    public void setCategories(java.util.Set<Category> categories) { this.categories = categories; }

    public BookDetails getDetails() { return details; }
    public void setDetails(BookDetails details) { this.details = details; }

    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

enum BookStatus {
    AVAILABLE, RENTED, RESERVED, OUT_OF_STOCK
}
```

```java
package com.springecosystem.data.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "publishers")
public class Publisher {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String website;

    @Column(name = "established_year")
    private Integer establishedYear;

    @OneToMany(mappedBy = "publisher")
    private java.util.List<Book> books;

    public Publisher() {
    }

    public Publisher(String name, String website, Integer establishedYear) {
        this.name = name;
        this.website = website;
        this.establishedYear = establishedYear;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public Integer getEstablishedYear() { return establishedYear; }
    public void setEstablishedYear(Integer establishedYear) { this.establishedYear = establishedYear; }

    public java.util.List<Book> getBooks() { return books; }
    public void setBooks(java.util.List<Book> books) { this.books = books; }
}
```

```java
package com.springecosystem.data.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToMany(mappedBy = "categories")
    private java.util.Set<Book> books = new java.util.HashSet<>();

    public Category() {
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public java.util.Set<Book> getBooks() { return books; }
    public void setBooks(java.util.Set<Book> books) { this.books = books; }
}
```

```java
package com.springecosystem.data.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "book_details")
public class BookDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "language")
    private String language;

    @Column(name = "edition")
    private String edition;

    @Column(name = "format")
    private String format;

    @Column(name = "weight_grams")
    private Integer weightGrams;

    @Column(name = "dimensions")
    private String dimensions;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    public BookDetails() {
    }

    public BookDetails(String language, String edition, String format) {
        this.language = language;
        this.edition = edition;
        this.format = format;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public Integer getWeightGrams() { return weightGrams; }
    public void setWeightGrams(Integer weightGrams) { this.weightGrams = weightGrams; }

    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
}
```

```java
package com.springecosystem.data.repository;

import com.springecosystem.data.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    Optional<Author> findByEmail(String email);
    
    List<Author> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.books WHERE a.id = :id")
    Optional<Author> findByIdWithBooks(@Param("id") Long id);
    
    @Query("SELECT a FROM Author a WHERE a.name LIKE %:name% ORDER BY a.name")
    List<Author> searchByName(@Param("name") String name, Pageable pageable);
    
    @Query("SELECT a FROM Author a WHERE SIZE(a.books) >= :minBooks")
    List<Author> findAuthorsWithMinimumBooks(@Param("minBooks") int minBooks);
    
    @Query(value = "SELECT * FROM authors WHERE LOWER(name) LIKE LOWER(CONCAT('%', :pattern, '%'))", 
           nativeQuery = true)
    List<Author> findByNamePatternNative(@Param("pattern") String pattern);
    
    Page<Author> findAll(Pageable pageable);
    
    @Query("SELECT COUNT(a) FROM Author a WHERE a.name LIKE %:name%")
    long countByNameContaining(@Param("name") String name);
}
```

```java
package com.springecosystem.data.repository;

import com.springecosystem.data.entity.Book;
import com.springecosystem.data.entity.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    List<Book> findByIsbn(String isbn);
    
    List<Book> findByAuthorId(Long authorId);
    
    List<Book> findByPublisherId(Long publisherId);
    
    @Query("SELECT b FROM Book b WHERE b.price BETWEEN :minPrice AND :maxPrice")
    List<Book> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT b FROM Book b WHERE b.publishedDate >= :startDate AND b.publishedDate <= :endDate")
    List<Book> findByPublishedDateRange(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);
    
    @Query("SELECT b FROM Book b JOIN b.categories c WHERE c.name = :categoryName")
    List<Book> findByCategoryName(@Param("categoryName") String categoryName);
    
    @Query("SELECT b FROM Book b WHERE b.status = :status")
    Page<Book> findByStatus(@Param("status") BookStatus status, Pageable pageable);
    
    @Query("SELECT b FROM Book b JOIN b.author a WHERE a.name = :authorName")
    List<Book> findByAuthorName(@Param("authorName") String authorName);
    
    @Query("SELECT b FROM Book b ORDER BY b.price DESC")
    List<Book> findAllOrderByPriceDesc(Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.description LIKE %:keyword%")
    Page<Book> searchBooks(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT COUNT(b) FROM Book b WHERE b.author.id = :authorId")
    long countByAuthorId(@Param("authorId") Long authorId);
    
    @Query("SELECT AVG(b.price) FROM Book b WHERE b.publisher.id = :publisherId")
    BigDecimal averagePriceByPublisher(@Param("publisherId") Long publisherId);
}
```

```java
package com.springecosystem.data.repository;

import com.springecosystem.data.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    
    Optional<Publisher> findByName(String name);
    
    Optional<Publisher> findByWebsite(String website);
    
    List<Publisher> findByEstablishedYearGreaterThanEqual(Integer year);
    
    List<Publisher> findByNameContainingIgnoreCase(String namePattern);
}
```

```java
package com.springecosystem.data.service;

import com.springecosystem.data.entity.Author;
import com.springecosystem.data.entity.Book;
import com.springecosystem.data.entity.BookStatus;
import com.springecosystem.data.repository.AuthorRepository;
import com.springecosystem.data.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LibraryService {
    
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public LibraryService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    // Author operations
    public Author createAuthor(Author author) {
        return authorRepository.save(author);
    }

    public Optional<Author> getAuthorById(Long id) {
        return authorRepository.findByIdWithBooks(id);
    }

    public Optional<Author> getAuthorByEmail(String email) {
        return authorRepository.findByEmail(email);
    }

    public List<Author> searchAuthors(String name) {
        return authorRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Author> getProlificAuthors(int minBooks) {
        return authorRepository.findAuthorsWithMinimumBooks(minBooks);
    }

    // Book operations
    public Book createBook(Book book, Long authorId) {
        return authorRepository.findById(authorId)
            .map(author -> {
                book.setAuthor(author);
                return bookRepository.save(book);
            })
            .orElseThrow(() -> new RuntimeException("Author not found: " + authorId));
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public List<Book> searchBooks(String keyword, Pageable pageable) {
        return bookRepository.searchBooks(keyword, pageable);
    }

    public List<Book> getBooksByAuthor(Long authorId) {
        return bookRepository.findByAuthorId(authorId);
    }

    public List<Book> getBooksByPublisher(Long publisherId) {
        return bookRepository.findByPublisherId(publisherId);
    }

    public List<Book> getBooksByCategory(String categoryName) {
        return bookRepository.findByCategoryName(categoryName);
    }

    public Page<Book> getBooksByStatus(BookStatus status, Pageable pageable) {
        return bookRepository.findByStatus(status, pageable);
    }

    public List<Book> getBooksByPriceRange(BigDecimal min, BigDecimal max) {
        return bookRepository.findByPriceRange(min, max);
    }

    public List<Book> getRecentlyPublishedBooks(LocalDate start, LocalDate end) {
        return bookRepository.findByPublishedDateRange(start, end);
    }

    public List<Book> getMostExpensiveBooks(Pageable pageable) {
        return bookRepository.findAllOrderByPriceDesc(pageable);
    }

    // Statistics
    public long countBooksByAuthor(Long authorId) {
        return bookRepository.countByAuthorId(authorId);
    }

    public BigDecimal getAveragePriceByPublisher(Long publisherId) {
        return bookRepository.averagePriceByPublisher(publisherId);
    }

    // Batch operations
    public List<Author> createAuthors(List<Author> authors) {
        return authorRepository.saveAll(authors);
    }

    public List<Book> createBooks(List<Book> books) {
        return bookRepository.saveAll(books);
    }

    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
```

### application.yml

```yaml
spring:
  application:
    name: spring-data-jpa
  datasource:
    url: jdbc:h2:mem:library
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
  h2:
    console:
      enabled: true

logging:
  level:
    org.springframework.data.jpa: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>

    <groupId>com.springecosystem</groupId>
    <artifactId>spring-data-jpa-projects</artifactId>
    <version>1.0.0</version>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
</project>
```

### Build and Run

```bash
cd spring-data-jpa-projects
mvn clean compile
mvn spring-boot:run
```

---

## Real-World Project: Spring Cloud Microservices Configuration (8+ hours)

### Overview

Build a comprehensive microservices infrastructure using Spring Cloud components including Config Server, Service Discovery with Eureka, API Gateway with Zuul, Circuit Breaker with Resilience4j, and Distributed Tracing. This project demonstrates enterprise-grade Spring ecosystem integration patterns.

### Architecture Components

```
spring-cloud-ecosystem/
├── config-server/           # Centralized Configuration
├── eureka-server/          # Service Registry
├── api-gateway/            # Gateway with Zuul
├── service-a/              # Sample Service A
├── service-b/              # Sample Service B
├── circuit-breaker-demo/   # Resilience4j Demo
└── config-repo/            # Configuration Repository
```

### Implementation

```java
package com.springecosystem.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

```yaml
# config-server application.yml
server:
  port: 8888

spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-repo/config-repo
          default-label: main
          search-paths: config/*
          username: your-username
          password: your-password
```

```java
package com.springecosystem.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

```yaml
# eureka-server application.yml
server:
  port: 8761

spring:
  application:
    name: eureka-server

eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://localhost:8761/eureka/
  server:
    enable-self-preservation: true
    eviction-interval-timer-in-ms: 5000
```

```java
package com.springecosystem.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
```

```yaml
# api-gateway application.yml
server:
  port: 8080

spring:
  application:
    name: api-gateway

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true

zuul:
  ignored-services: '*'
  prefix: /api
  routes:
    service-a:
      path: /service-a/**
      service-id: service-a
      strip-prefix: true
    service-b:
      path: /service-b/**
      service-id: service-b
      strip-prefix: true
  host:
    connect-timeout-millis: 5000
    socket-timeout-millis: 10000
  sensitive-headers: Cookie,Set-Cookie
  retryable: true

ribbon:
  ConnectTimeout: 3000
  ReadTimeout: 5000
  MaxAutoRetries: 2
  MaxAutoRetriesNextServer: 3
```

```java
package com.springecosystem.servicea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceAApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceAApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

```java
package com.springecosystem.servicea.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/message")
public class ServiceAController {
    
    private final RestTemplate restTemplate;

    @Autowired
    public ServiceAController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/{id}")
    public String getMessage(@PathVariable String id) {
        try {
            String response = restTemplate.getForObject(
                "http://service-b/hello/" + id, String.class);
            return "Service A response: " + response;
        } catch (Exception e) {
            return "Service A: Service B unavailable - " + e.getMessage();
        }
    }

    @GetMapping("/retry/{id}")
    public String getMessageWithRetry(@PathVariable String id) {
        String response = restTemplate.getForObject(
            "http://service-b/hello/" + id, String.class);
        return "Service A response: " + response;
    }

    @GetMapping("/fallback")
    public String fallback() {
        return "Service A: Fallback activated - Circuit breaker is open";
    }
}
```

```yaml
# service-a application.yml
server:
  port: 8081

spring:
  application:
    name: service-a

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true

resilience4j:
  circuitbreaker:
    instances:
      serviceB:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
  retry:
    instances:
      serviceB:
        maxAttempts: 3
        waitDuration: 1s
        enableExponentialBackoff: true
        exponentialBackoffMultiplier: 2

management:
  endpoints:
    web:
      exposure:
        include: health,circuitbreakers,retry
  health:
    circuitbreakers:
      enabled: true
```

```java
package com.springecosystem.serviceb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceBApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceBApplication.class, args);
    }
}
```

```java
package com.springecosystem.serviceb.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/hello")
public class ServiceBController {
    
    private final Random random = new Random();

    @GetMapping("/{id}")
    public String hello(@PathVariable String id) {
        int delay = random.nextInt(2000);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Hello from Service B - Request ID: " + id;
    }

    @GetMapping("/random-fail")
    public String randomFail() {
        if (random.nextBoolean()) {
            throw new RuntimeException("Random failure");
        }
        return "Success from Service B";
    }

    @GetMapping("/health")
    public String health() {
        return "Service B is healthy";
    }
}
```

```yaml
# service-b application.yml
server:
  port: 8082

spring:
  application:
    name: service-b
  config:
    import: optional:http://localhost:8888/config

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

### pom.xml for Service A

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>

    <groupId>com.springecosystem</groupId>
    <artifactId>service-a</artifactId>
    <version>1.0.0</version>

    <properties>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

### Build and Run

```bash
# Start Config Server
cd config-server
mvn spring-boot:run

# Start Eureka Server
cd eureka-server
mvn spring-boot:run

# Start API Gateway
cd api-gateway
mvn spring-boot:run

# Start Service A
cd service-a
mvn spring-boot:run

# Start Service B
cd service-b
mvn spring-boot:run
```

### Test the System

```bash
# Access through API Gateway
curl http://localhost:8080/api/service-a/message/1
curl http://localhost:8080/api/service-b/hello/1

# Check Eureka
curl http://localhost:8761

# Check Circuit Breaker Health
curl http://localhost:8081/actuator/health
curl http://localhost:8081/actuator/circuitbreakers
```

---

## Additional Learning Resources

- Spring Data JPA Documentation: https://spring.io/projects/spring-data-jpa
- Spring Cloud Documentation: https://spring.io/projects/spring-cloud
- Spring Security: https://spring.io/projects/spring-security
- Spring Batch: https://spring.io/projects/spring-batch