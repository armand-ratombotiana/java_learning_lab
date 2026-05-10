# Javalin Projects

This directory contains hands-on projects using Javalin, a simple and lightweight web framework for Java and Kotlin. Javalin is known for its expressive API, minimal setup requirements, and excellent developer experience.

## Project Overview

Javalin provides a modern approach to building web applications with support for REST APIs, WebSockets, and real-time communication. This module covers two projects of increasing complexity.

---

# Mini-Project: Blog API (2-4 Hours)

## Project Description

Build a RESTful blog API using Javalin with in-memory storage. This project demonstrates Javalin's routing, request handling, JSON serialization, and plugin system.

## Technologies Used

- Javalin 6.1.3
- Java 21
- Maven
- Jackson for JSON
- In-memory blog post storage

## Implementation Steps

### Step 1: Create Project Structure

```bash
mkdir javalin-blog-api
cd javalin-blog-api
mkdir -p src/main/java/com/learning/blog/{model,service,resource}
mkdir -p src/main/resources
mkdir -p src/test/java/com/learning/blog
```

### Step 2: Create pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>javalin-blog-api</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <name>Javalin Blog API</name>
    <description>Blog REST API with Javalin</description>
    
    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <javalin.version>6.1.3</javalin.version>
        <jackson.version>2.16.0</jackson.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>io.javalin</groupId>
            <artifactId>javalin</artifactId>
            <version>${javalin.version}</version>
        </dependency>
        <dependency>
            <groupId>io.javalin</groupId>
            <artifactId>javalin-rendering</artifactId>
            <version>${javalin.version}</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>${jackson.version}</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>2.0.9</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals><goal>shade</goal></goals>
                        <configuration>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.learning.blog.Main</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 3: Create Blog Post Model

```java
package com.learning.blog.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class BlogPost {
    
    private Long id;
    private String title;
    private String content;
    private String author;
    private String category;
    private String[] tags;
    private boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int viewCount;
    
    public BlogPost() {
        this.published = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public BlogPost(String title, String content, String author) {
        this();
        this.title = title;
        this.content = content;
        this.author = author;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getContent() { return content; }
    public void setContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String[] getTags() { return tags; }
    public void setTags(String[] tags) { this.tags = tags; }
    
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) {
        this.published = published;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    
    public void incrementViewCount() {
        this.viewCount++;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlogPost blogPost = (BlogPost) o;
        return Objects.equals(id, blogPost.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
```

### Step 4: Create Blog Service

```java
package com.learning.blog.service;

import com.learning.blog.model.BlogPost;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class BlogService {
    
    private final Map<Long, BlogPost> postStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public BlogPost createPost(BlogPost post) {
        Long id = idGenerator.getAndIncrement();
        post.setId(id);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        postStore.put(id, post);
        return post;
    }
    
    public Optional<BlogPost> getPostById(Long id) {
        return Optional.ofNullable(postStore.get(id))
            .map post -> {
                post.incrementViewCount();
                return post;
            });
    }
    
    public List<BlogPost> getAllPosts() {
        return new ArrayList<>(postStore.values());
    }
    
    public List<BlogPost> getPublishedPosts() {
        return postStore.values().stream()
            .filter(BlogPost::isPublished)
            .sorted(Comparator.comparing(BlogPost::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }
    
    public List<BlogPost> getPostsByAuthor(String author) {
        return postStore.values().stream()
            .filter(post -> post.getAuthor().equalsIgnoreCase(author))
            .collect(Collectors.toList());
    }
    
    public List<BlogPost> getPostsByCategory(String category) {
        return postStore.values().stream()
            .filter(post -> post.getCategory().equalsIgnoreCase(category))
            .collect(Collectors.toList());
    }
    
    public List<BlogPost> searchPosts(String query) {
        String lowerQuery = query.toLowerCase();
        return postStore.values().stream()
            .filter(post -> 
                post.getTitle().toLowerCase().contains(lowerQuery) ||
                post.getContent().toLowerCase().contains(lowerQuery))
            .collect(Collectors.toList());
    }
    
    public List<BlogPost> getPostsByTag(String tag) {
        return postStore.values().stream()
            .filter(post -> post.getTags() != null)
            .filter(post -> Arrays.asList(post.getTags())
                .stream()
                .anyMatch(t -> t.equalsIgnoreCase(tag)))
            .collect(Collectors.toList());
    }
    
    public Optional<BlogPost> updatePost(Long id, BlogPost postUpdate) {
        BlogPost existing = postStore.get(id);
        if (existing == null) {
            return Optional.empty();
        }
        
        if (postUpdate.getTitle() != null) {
            existing.setTitle(postUpdate.getTitle());
        }
        if (postUpdate.getContent() != null) {
            existing.setContent(postUpdate.getContent());
        }
        if (postUpdate.getCategory() != null) {
            existing.setCategory(postUpdate.getCategory());
        }
        if (postUpdate.getTags() != null) {
            existing.setTags(postUpdate.getTags());
        }
        
        postStore.put(id, existing);
        return Optional.of(existing);
    }
    
    public Optional<BlogPost> publishPost(Long id) {
        BlogPost post = postStore.get(id);
        if (post == null) {
            return Optional.empty();
        }
        post.setPublished(true);
        return Optional.of(post);
    }
    
    public Optional<BlogPost> unpublishPost(Long id) {
        BlogPost post = postStore.get(id);
        if (post == null) {
            return Optional.empty();
        }
        post.setPublished(false);
        return Optional.of(post);
    }
    
    public Optional<BlogPost> deletePost(Long id) {
        return Optional.ofNullable(postStore.remove(id));
    }
    
    public long getPostCount() {
        return postStore.size();
    }
    
    public long getPublishedPostCount() {
        return postStore.values().stream()
            .filter(BlogPost::isPublished)
            .count();
    }
}
```

### Step 5: Create Blog Resource

```java
package com.learning.blog.resource;

import com.learning.blog.model.BlogPost;
import com.learning.blog.service.BlogService;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseExceptions;
import java.util.List;

public class BlogResource {
    
    private final BlogService blogService;
    
    public BlogResource(BlogService blogService) {
        this.blogService = blogService;
    }
    
    void getAllPosts(Context ctx) {
        String published = ctx.queryParam("published");
        
        List<BlogPost> posts;
        if ("true".equalsIgnoreCase(published)) {
            posts = blogService.getPublishedPosts();
        } else {
            posts = blogService.getAllPosts();
        }
        
        ctx.json(posts);
    }
    
    void getPostById(Context ctx) {
        Long id = ctx.pathParam("id", Long.class).get();
        
        blogService.getPostById(id)
            .ifPresentOrElse(
                ctx::json,
                () -> ctx.status(404).result("Post not found")
            );
    }
    
    void getPostsByAuthor(Context ctx) {
        String author = ctx.pathParam("author");
        
        List<BlogPost> posts = blogService.getPostsByAuthor(author);
        ctx.json(posts);
    }
    
    void getPostsByCategory(Context ctx) {
        String category = ctx.pathParam("category");
        
        List<BlogPost> posts = blogService.getPostsByCategory(category);
        ctx.json(posts);
    }
    
    void getPostsByTag(Context ctx) {
        String tag = ctx.pathParam("tag");
        
        List<BlogPost> posts = blogService.getPostsByTag(tag);
        ctx.json(posts);
    }
    
    void searchPosts(Context ctx) {
        String query = ctx.queryParam("q");
        
        if (query == null || query.isBlank()) {
            ctx.status(400).result("Query parameter 'q' is required");
            return;
        }
        
        List<BlogPost> posts = blogService.searchPosts(query);
        ctx.json(posts);
    }
    
    void createPost(Context ctx) {
        BlogPost post = ctx.bodyAsClass(BlogPost.class);
        
        if (post.getTitle() == null || post.getTitle().isBlank()) {
            ctx.status(400).result("Title is required");
            return;
        }
        
        if (post.getContent() == null || post.getContent().isBlank()) {
            ctx.status(400).result("Content is required");
            return;
        }
        
        BlogPost created = blogService.createPost(post);
        ctx.status(201).json(created);
    }
    
    void updatePost(Context ctx) {
        Long id = ctx.pathParam("id", Long.class).get();
        
        BlogPost postUpdate = ctx.bodyAsClass(BlogPost.class);
        
        blogService.updatePost(id, postUpdate)
            .ifPresentOrElse(
                ctx::json,
                () -> ctx.status(404).result("Post not found")
            );
    }
    
    void deletePost(Context ctx) {
        Long id = ctx.pathParam("id", Long.class).get();
        
        blogService.deletePost(id)
            .ifPresentOrElse(
                post -> ctx.status(204).result(""),
                () -> ctx.status(404).result("Post not found")
            );
    }
    
    void publishPost(Context ctx) {
        Long id = ctx.pathParam("id", Long.class).get();
        
        blogService.publishPost(id)
            .ifPresentOrElse(
                ctx::json,
                () -> ctx.status(404).result("Post not found")
            );
    }
    
    void unpublishPost(Context ctx) {
        Long id = ctx.pathParam("id", Long.class).get();
        
        blogService.unpublishPost(id)
            .ifPresentOrElse(
                ctx::json,
                () -> ctx.status(404).result("Post not found")
            );
    }
    
    void getStats(Context ctx) {
        ctx.json(new StatsResponse(
            blogService.getPostCount(),
            blogService.getPublishedPostCount()
        ));
    }
    
    public static class StatsResponse {
        public long totalPosts;
        public long publishedPosts;
        
        public StatsResponse() {}
        
        public StatsResponse(long totalPosts, long publishedPosts) {
            this.totalPosts = totalPosts;
            this.publishedPosts = publishedPosts;
        }
    }
}
```

### Step 6: Create Main Application

```java
package com.learning.blog;

import com.learning.blog.model.BlogPost;
import com.learning.blog.resource.BlogResource;
import com.learning.blog.service.BlogService;
import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.http.staticfiles.Location;

public class Main {
    
    public static void main(String[] args) {
        BlogService blogService = new BlogService();
        BlogResource blogResource = new BlogResource(blogService);
        
        // Initialize with sample data
        initializeSampleData(blogService);
        
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH);
            config.jsonMapper(new io.javalin.jackson.JacksonJsonMapper());
        }).routes(() -> {
            ApiBuilder.get("/api/posts", blogResource::getAllPosts);
            ApiBuilder.get("/api/posts/stats", blogResource::getStats);
            ApiBuilder.get("/api/posts/search", blogResource::searchPosts);
            ApiBuilder.get("/api/posts/{id}", blogResource::getPostById);
            ApiBuilder.get("/api/posts/author/{author}", blogResource::getPostsByAuthor);
            ApiBuilder.get("/api/posts/category/{category}", blogResource::getPostsByCategory);
            ApiBuilder.get("/api/posts/tag/{tag}", blogResource::getPostsByTag);
            ApiBuilder.post("/api/posts", blogResource::createPost);
            ApiBuilder.put("/api/posts/{id}", blogResource::updatePost);
            ApiBuilder.delete("/api/posts/{id}", blogResource::deletePost);
            ApiBuilder.patch("/api/posts/{id}/publish", blogResource::publishPost);
            ApiBuilder.patch("/api/posts/{id}/unpublish", blogResource::unpublishPost);
        }).start(8080);
        
        System.out.println("Blog API started at http://localhost:8080");
        System.out.println("API endpoints:");
        System.out.println("  GET    /api/posts           - List all posts");
        System.out.println("  GET    /api/posts/{id}      - Get post by ID");
        System.out.println("  GET    /api/posts/search?q=  - Search posts");
        System.out.println("  POST   /api/posts           - Create post");
        System.out.println("  PUT    /api/posts/{id}      - Update post");
        System.out.println("  DELETE /api/posts/{id}      - Delete post");
    }
    
    private static void initializeSampleData(BlogService service) {
        BlogPost post1 = new BlogPost(
            "Getting Started with Javalin",
            "Javalin is a lightweight web framework for Java and Kotlin. " +
            "It provides an expressive API for building web applications...",
            "john.doe"
        );
        post1.setCategory("Tutorial");
        post1.setTags(new String[]{"java", "javalin", "web"});
        post1.setPublished(true);
        
        BlogPost post2 = new BlogPost(
            "Building REST APIs",
            "Learn how to build scalable REST APIs using modern Java frameworks. " +
            "This guide covers best practices...",
            "jane.smith"
        );
        post2.setCategory("Guide");
        post2.setTags(new String[]{"rest", "api", "backend"});
        post2.setPublished(true);
        
        BlogPost post3 = new BlogPost(
            "Introduction to Microservices",
            "Microservices architecture is a design pattern that structures " +
            "an application as a collection of loosely coupled services...",
            "john.doe"
        );
        post3.setCategory("Architecture");
        post3.setTags(new String[]{"microservices", "architecture", "design"});
        post3.setPublished(true);
        
        service.createPost(post1);
        service.createPost(post2);
        service.createPost(post3);
    }
}
```

### Step 7: Run and Test

```bash
# Build project
cd javalin-blog-api
mvn clean package

# Run application
java -jar target/javalin-blog-api-1.0.0.jar

# Test API
# Get all posts
curl http://localhost:8080/api/posts

# Get published posts only
curl "http://localhost:8080/api/posts?published=true"

# Search posts
curl "http://localhost:8080/api/posts/search?q=Javalin"

# Get posts by author
curl http://localhost:8080/api/posts/author/john.doe

# Get posts by category
curl http://localhost:8080/api/posts/category/Tutorial

# Get post by ID
curl http://localhost:8080/api/posts/1

# Create new post
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "New Blog Post",
    "content": "This is the content of my new blog post.",
    "author": "author@example.com",
    "category": "Technology",
    "tags": ["java", "development"]
  }'

# Update post
curl -X PUT http://localhost:8080/api/posts/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Title",
    "content": "Updated content here."
  }'

# Publish post
curl -X PATCH http://localhost:8080/api/posts/1/publish

# Delete post
curl -X DELETE http://localhost:8080/api/posts/1

# Get statistics
curl http://localhost:8080/api/posts/stats
```

## Expected Output

The mini-project produces:
- Full CRUD operations for blog posts
- Search functionality
- Category and tag filtering
- Publishing workflow
- Statistics endpoint

---

# Real-World Project: Real-Time Chat Application (8+ Hours)

## Project Description

Build a complete real-time chat application using Javalin with WebSockets, JWT authentication, message persistence, and room-based chat rooms.

## Architecture

```
┌─────────────────────────────────────────┐
│         Javalin WebSocket Server         │
├─────────────────────────────────────────┤
│  - HTTP Server (REST API)                 │
│  - WebSocket Handler                    │
│  - JWT Auth Filter                      │
│  - Message Persistence                  │
└─────────────────────────────────────────┘
              │
    ┌─────────┴─────────┐
    │                   │
┌───▼───┐        ┌────▼────┐
│Room 1 │        │Room 2 │
└───────┘        └────────┘
```

## Implementation Steps

### Step 1: Create Chat Application Project

```bash
mkdir javalin-chat-app
cd javalin-chat-app
mkdir -p src/main/java/com/learning/chat/{model,service,resource,security,websocket}
mkdir -p src/main/resources
mkdir -p src/test/java/com/learning/chat
```

### Step 2: Create pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>javalin-chat-app</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <name>Javalin Chat Application</name>
    <description>Real-time chat application with Javalin</description>
    
    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <javalin.version>6.1.3</javalin.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>io.javalin</groupId>
            <artifactId>javalin</artifactId>
            <version>${javalin.version}</version>
        </dependency>
        <dependency>
            <groupId>io.javalin</groupId>
            <artifactId>javalin-jwt</artifactId>
            <version>${javalin.version}</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.16.0</version>
        </dependency>
        <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcprov-jdk18on</artifactId>
            <version>1.75</version>
        </dependency>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.10.1</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals><goal>shade</goal></goals>
                        <configuration>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.learning.chat.Main</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 3: Create User and Message Models

```java
package com.learning.chat.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    
    private Long id;
    private String username;
    private String password;
    private String displayName;
    private String email;
    private String avatarUrl;
    private boolean online;
    private LocalDateTime createdAt;
    private LocalDateTime lastSeen;
    
    public User() {
        this.createdAt = LocalDateTime.now();
        this.lastSeen = LocalDateTime.now();
        this.online = false;
    }
    
    public User(String username, String password, String displayName) {
        this();
        this.username = username;
        this.password = password;
        this.displayName = displayName;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    
    public boolean isOnline() { return online; }
    public void setOnline(boolean online) { this.online = online; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastSeen() { return lastSeen; }
    public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
```

```java
package com.learning.chat.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message {
    
    private Long id;
    private Long roomId;
    private Long senderId;
    private String senderUsername;
    private String content;
    private MessageType type;
    private LocalDateTime timestamp;
    private boolean edited;
    private LocalDateTime editedAt;
    private boolean deleted;
    
    public Message() {
        this.type = MessageType.TEXT;
        this.timestamp = LocalDateTime.now();
    }
    
    public Message(Long roomId, Long senderId, String senderUsername, String content) {
        this();
        this.roomId = roomId;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.content = content;
    }
    
    public enum MessageType {
        TEXT,
        IMAGE,
        FILE,
        SYSTEM,
        JOIN,
        LEAVE
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    
    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public boolean isEdited() { return edited; }
    public void setEdited(boolean edited) { this.edited = edited; }
    
    public LocalDateTime getEditedAt() { return editedAt; }
    public void setEditedAt(LocalDateTime editedAt) { this.editedAt = editedAt; }
    
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
```

```java
package com.learning.chat.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoom {
    
    private Long id;
    private String name;
    private String description;
    private String createdBy;
    private Set<Long> members = ConcurrentHashMap.newKeySet();
    private Set<Long> moderators = ConcurrentHashMap.newKeySet();
    private boolean isPrivate;
    private LocalDateTime createdAt;
    
    public ChatRoom() {
        this.createdAt = LocalDateTime.now();
    }
    
    public ChatRoom(String name, String description, String createdBy) {
        this();
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
    }
    
    public boolean hasMember(Long userId) {
        return members.contains(userId);
    }
    
    public boolean isModerator(Long userId) {
        return moderators.contains(userId);
    }
    
    public void addMember(Long userId) {
        members.add(userId);
    }
    
    public void removeMember(Long userId) {
        members.remove(userId);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public Set<Long> getMembers() { return members; }
    public void setMembers(Set<Long> members) { this.members = members; }
    
    public Set<Long> getModerators() { return moderators; }
    public void setModerators(Set<Long> moderators) { this.moderators = moderators; }
    
    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
```

### Step 4: Create Services

```java
package com.learning.chat.service;

import com.learning.chat.model.User;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserService {
    
    private final Map<Long, User> userStore = new ConcurrentHashMap<>();
    private final Map<String, User> usernameIndex = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public User register(String username, String password, String displayName) {
        if (usernameIndex.containsKey(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        User user = new User(username, password, displayName);
        user.setId(idGenerator.getAndIncrement());
        
        userStore.put(user.getId(), user);
        usernameIndex.put(username, user);
        
        return user;
    }
    
    public Optional<User> authenticate(String username, String password) {
        User user = usernameIndex.get(username);
        
        if (user != null && user.getPassword().equals(password)) {
            user.setOnline(true);
            user.setLastSeen(java.time.LocalDateTime.now());
            return Optional.of(user);
        }
        
        return Optional.empty();
    }
    
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userStore.get(id));
    }
    
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usernameIndex.get(username));
    }
    
    public List<User> getOnlineUsers() {
        return userStore.values().stream()
            .filter(User::isOnline)
            .toList();
    }
    
    public void setOnlineStatus(Long userId, boolean online) {
        User user = userStore.get(userId);
        if (user != null) {
            user.setOnline(online);
            if (online) {
                user.setLastSeen(java.time.LocalDateTime.now());
            }
        }
    }
    
    public List<User> searchUsers(String query) {
        String lowerQuery = query.toLowerCase();
        return userStore.values().stream()
            .filter(u -> u.getUsername().toLowerCase().contains(lowerQuery) ||
                       (u.getDisplayName() != null && 
                        u.getDisplayName().toLowerCase().contains(lowerQuery)))
            .toList();
    }
}
```

```java
package com.learning.chat.service;

import com.learning.chat.model.ChatRoom;
import com.learning.chat.model.Message;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ChatService {
    
    private final Map<Long, ChatRoom> roomStore = new ConcurrentHashMap<>();
    private final Map<Long, Message> messageStore = new ConcurrentHashMap<>();
    private final AtomicLong roomIdGenerator = new AtomicLong(1);
    private final AtomicLong messageIdGenerator = new AtomicLong(1);
    
    public ChatRoom createRoom(String name, String description, String createdBy) {
        ChatRoom room = new ChatRoom(name, description, createdBy);
        room.setId(roomIdGenerator.getAndIncrement());
        
        roomStore.put(room.getId(), room);
        
        return room;
    }
    
    public Optional<ChatRoom> getRoomById(Long id) {
        return Optional.ofNullable(roomStore.get(id));
    }
    
    public List<ChatRoom> getAllRooms() {
        return new ArrayList<>(roomStore.values());
    }
    
    public List<ChatRoom> getPublicRooms() {
        return roomStore.values().stream()
            .filter(room -> !room.isPrivate())
            .collect(Collectors.toList());
    }
    
    public List<ChatRoom> getRoomsForUser(Long userId) {
        return roomStore.values().stream()
            .filter(room -> room.hasMember(userId))
            .collect(Collectors.toList());
    }
    
    public Message sendMessage(Long roomId, Long senderId, String senderUsername, String content) {
        ChatRoom room = roomStore.get(roomId);
        if (room == null) {
            throw new RuntimeException("Room not found");
        }
        
        Message message = new Message(roomId, senderId, senderUsername, content);
        message.setId(messageIdGenerator.getAndIncrement());
        
        messageStore.put(message.getId(), message);
        
        return message;
    }
    
    public Optional<Message> getMessageById(Long id) {
        return Optional.ofNullable(messageStore.get(id));
    }
    
    public List<Message> getMessagesByRoom(Long roomId, int limit, int offset) {
        return messageStore.values().stream()
            .filter(m -> m.getRoomId().equals(roomId) && !m.isDeleted())
            .sorted(Comparator.comparing(Message::getTimestamp))
            .skip(offset)
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    public Optional<Message> editMessage(Long messageId, Long userId, String newContent) {
        Message message = messageStore.get(messageId);
        
        if (message == null) {
            return Optional.empty();
        }
        
        if (!message.getSenderId().equals(userId)) {
            throw new RuntimeException("Cannot edit message from another user");
        }
        
        message.setContent(newContent);
        message.setEdited(true);
        message.setEditedAt(LocalDateTime.now());
        
        return Optional.of(message);
    }
    
    public Optional<Message> deleteMessage(Long messageId, Long userId) {
        Message message = messageStore.get(messageId);
        
        if (message == null) {
            return Optional.empty();
        }
        
        if (!message.getSenderId().equals(userId)) {
            throw new RuntimeException("Cannot delete message from another user");
        }
        
        message.setDeleted(true);
        
        return Optional.of(message);
    }
    
    public void joinRoom(Long roomId, Long userId) {
        ChatRoom room = roomStore.get(roomId);
        if (room != null) {
            room.addMember(userId);
        }
    }
    
    public void leaveRoom(Long roomId, Long userId) {
        ChatRoom room = roomStore.get(roomId);
        if (room != null) {
            room.removeMember(userId);
        }
    }
}
```

### Step 5: Create JWT Security

```java
package com.learning.chat.security;

import io.javalin.security.Role;
import java.util.Base64;
import java.util.Set;

public class JwtUtil {
    
    private static final String SECRET = "mySecretKey123456789";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours
    
    public static String generateToken(String username, Long userId) {
        String payload = username + ":" + userId + ":" + System.currentTimeMillis();
        return Base64.getEncoder().encodeToString(payload.getBytes());
    }
    
    public static boolean validateToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            String[] parts = decoded.split(":");
            return parts.length >= 3;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static String getUsernameFromToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            return decoded.split(":")[0];
        } catch (Exception e) {
            return null;
        }
    }
    
    public static Long getUserIdFromToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            return Long.parseLong(decoded.split(":")[1]);
        } catch (Exception e) {
            return null;
        }
    }
    
    public enum UserRole implements Role {
        USER,
        ADMIN,
        MODERATOR
    }
}
```

### Step 6: Create WebSocket Handler

```java
package com.learning.chat.websocket;

import com.learning.chat.model.Message;
import com.learning.chat.security.JwtUtil;
import com.learning.chat.service.ChatService;
import com.learning.chat.service.UserService;
import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsHandler;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler {
    
    private final Map<Long, Set<WsContext>> roomConnections = new ConcurrentHashMap<>();
    private final Map<WsContext, ConnectionInfo> connectionInfo = new ConcurrentHashMap<>();
    private final ChatService chatService;
    private final UserService userService;
    private final Gson gson = new Gson();
    
    public ChatWebSocketHandler(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }
    
    public WsHandler handleWebSocket() {
        return ws -> ws.onConnect(this::handleConnect)
                      .onMessage(this::handleMessage)
                      .onClose(this::handleClose)
                      .onError(this::handleError);
    }
    
    private void handleConnect(WsContext ctx) {
        String token = ctx.queryParam("token");
        if (token == null || !JwtUtil.validateToken(token)) {
            ctx.close();
            return;
        }
        
        Long userId = JwtUtil.getUserIdFromToken(token);
        String username = JwtUtil.getUsernameFromToken(token);
        
        connectionInfo.put(ctx, new ConnectionInfo(userId, username));
        userService.setOnlineStatus(userId, true);
        
        ctx.send(gson.toJson(new WebSocketMessage("WELCOME", 
            "Connected as " + username, username)));
    }
    
    private void handleMessage(WsContext ctx, String messageStr) {
        ConnectionInfo info = connectionInfo.get(ctx);
        if (info == null) return;
        
        ClientMessage clientMsg = gson.fromJson(messageStr, ClientMessage.class);
        
        switch (clientMsg.type) {
            case "JOIN_ROOM" -> handleJoinRoom(ctx, info, clientMsg.roomId);
            case "LEAVE_ROOM" -> handleLeaveRoom(ctx, info, clientMsg.roomId);
            case "CHAT_MESSAGE" -> handleChatMessage(ctx, info, clientMsg.roomId, clientMsg.content);
            case "TYPING" -> handleTyping(ctx, info, clientMsg.roomId);
        }
    }
    
    private void handleJoinRoom(WsContext ctx, ConnectionInfo info, Long roomId) {
        roomConnections.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet())
                     .add(ctx);
        
        Message joinMessage = chatService.sendMessage(
            roomId, info.userId(), info.username(),
            info.username() + " joined the room"
        );
        joinMessage.setType(Message.MessageType.SYSTEM);
        
        broadcastToRoom(roomId, joinMessage);
    }
    
    private void handleLeaveRoom(WsContext ctx, ConnectionInfo info, Long roomId) {
        Set<WsContext> connections = roomConnections.get(roomId);
        if (connections != null) {
            connections.remove(ctx);
            
            Message leaveMessage = chatService.sendMessage(
                roomId, info.userId(), info.username(),
                info.username() + " left the room"
            );
            leaveMessage.setType(Message.MessageType.SYSTEM);
            
            broadcastToRoom(roomId, leaveMessage);
        }
    }
    
    private void handleChatMessage(WsContext ctx, ConnectionInfo info, Long roomId, String content) {
        Message message = chatService.sendMessage(
            roomId, info.userId(), info.username(), content
        );
        
        broadcastToRoom(roomId, message);
    }
    
    private void handleTyping(WsContext ctx, ConnectionInfo info, Long roomId) {
        WebSocketMessage typingMsg = new WebSocketMessage(
            "TYPING", info.username() + " is typing..."
        );
        
        Set<WsContext> connections = roomConnections.get(roomId);
        if (connections != null) {
            connections.stream()
                .filter(c -> !c.equals(ctx))
                .forEach(c -> c.send(gson.toJson(typingMsg)));
        }
    }
    
    private void handleClose(WsContext ctx) {
        ConnectionInfo info = connectionInfo.remove(ctx);
        if (info != null) {
            userService.setOnlineStatus(info.userId(), false);
            
            roomConnections.values().forEach(connections -> {
                connections.remove(ctx);
            });
        }
    }
    
    private void handleError(WsContext ctx, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }
    
    private void broadcastToRoom(Long roomId, Message message) {
        Set<WsContext> connections = roomConnections.get(roomId);
        if (connections != null) {
            String json = gson.toJson(new WebSocketMessage("MESSAGE", message));
            connections.forEach(c -> c.send(json));
        }
    }
    
    record ConnectionInfo(Long userId, String username) {}
    
    record ClientMessage(String type, Long roomId, String content) {}
    
    record WebSocketMessage(String type, String content) {}
}
```

### Step 7: Create REST API Resources

```java
package com.learning.chat.resource;

import com.learning.chat.model.ChatRoom;
import com.learning.chat.model.Message;
import com.learning.chat.model.User;
import com.learning.chat.security.JwtUtil;
import com.learning.chat.service.ChatService;
import com.learning.chat.service.UserService;
import io.javalin.http.Context;

public class ChatResource {
    
    private final UserService userService;
    private final ChatService chatService;
    
    public ChatResource(UserService userService, ChatService chatService) {
        this.userService = userService;
        this.chatService = chatService;
    }
    
    void register(Context ctx) {
        RegisterRequest request = ctx.bodyAsClass(RegisterRequest.class);
        
        try {
            User user = userService.register(
                request.username,
                request.password,
                request.displayName
            );
            
            String token = JwtUtil.generateToken(user.getUsername(), user.getId());
            
            ctx.json(new AuthResponse(token, user.getId(), user.getUsername()));
        } catch (Exception e) {
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        }
    }
    
    void login(Context ctx) {
        LoginRequest request = ctx.bodyAsClass(LoginRequest.class);
        
        userService.authenticate(request.username, request.password)
            .ifPresentOrElse(
                user -> {
                    String token = JwtUtil.generateToken(user.getUsername(), user.getId());
                    ctx.json(new AuthResponse(token, user.getId(), user.getUsername()));
                },
                () -> ctx.status(401).json(new ErrorResponse("Invalid credentials"))
            );
    }
    
    void createRoom(Context ctx) {
        CreateRoomRequest request = ctx.bodyAsClass(CreateRoomRequest.class);
        String username = ctx.attribute("username");
        
        ChatRoom room = chatService.createRoom(
            request.name,
            request.description,
            username
        );
        
        ctx.status(201).json(room);
    }
    
    void getRooms(Context ctx) {
        ctx.json(chatService.getAllRooms());
    }
    
    void getRoomMessages(Context ctx) {
        Long roomId = ctx.pathParam("id", Long.class).get();
        int limit = ctx.queryParam("limit", Integer.class).orElse(50);
        int offset = ctx.queryParam("offset", Integer.class).orElse(0);
        
        ctx.json(chatService.getMessagesByRoom(roomId, limit, offset));
    }
    
    void editMessage(Context ctx) {
        Long messageId = ctx.pathParam("id", Long.class).get();
        Long userId = ctx.attribute("userId");
        EditMessageRequest request = ctx.bodyAsClass(EditMessageRequest.class);
        
        chatService.editMessage(messageId, userId, request.content())
            .ifPresentOrElse(
                ctx::json,
                () -> ctx.status(404).result("Message not found")
            );
    }
    
    void deleteMessage(Context ctx) {
        Long messageId = ctx.pathParam("id", Long.class).get();
        Long userId = ctx.attribute("userId");
        
        chatService.deleteMessage(messageId, userId)
            .ifPresentOrElse(
                msg -> ctx.status(204).result(""),
                () -> ctx.status(404).result("Message not found")
            );
    }
    
    static class RegisterRequest {
        public String username;
        public String password;
        public String displayName;
    }
    
    static class LoginRequest {
        public String username;
        public String password;
    }
    
    static class AuthResponse {
        public String token;
        public Long userId;
        public String username;
        
        public AuthResponse(String token, Long userId, String username) {
            this.token = token;
            this.userId = userId;
            this.username = username;
        }
    }
    
    static class CreateRoomRequest {
        public String name;
        public String description;
    }
    
    static class EditMessageRequest {
        public String content;
    }
    
    static class ErrorResponse {
        public String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
```

### Step 8: Create Main Application

```java
package com.learning.chat;

import com.learning.chat.model.ChatRoom;
import com.learning.chat.resource.ChatResource;
import com.learning.chat.security.JwtUtil;
import com.learning.chat.service.ChatService;
import com.learning.chat.service.UserService;
import com.learning.chat.websocket.ChatWebSocketHandler;
import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;

public class Main {
    
    public static void main(String[] args) {
        UserService userService = new UserService();
        ChatService chatService = new ChatService();
        ChatResource chatResource = new ChatResource(userService, chatService);
        ChatWebSocketHandler wsHandler = new ChatWebSocketHandler(chatService, userService);
        
        // Initialize sample rooms
        initializeSampleRooms(chatService);
        
        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new io.javalin.jackson.JacksonJsonMapper());
        }).before(ctx -> {
            String path = ctx.path();
            if (path.startsWith("/api/")) {
                String token = ctx.header("Authorization");
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                    if (JwtUtil.validateToken(token)) {
                        ctx.attribute("username", JwtUtil.getUsernameFromToken(token));
                        ctx.attribute("userId", JwtUtil.getUserIdFromToken(token));
                    }
                }
            }
        }).routes(() -> {
            // Auth routes
            ApiBuilder.post("/api/auth/register", chatResource::register);
            ApiBuilder.post("/api/auth/login", chatResource::login);
            
            // Room routes
            ApiBuilder.get("/api/rooms", chatResource::getRooms);
            ApiBuilder.post("/api/rooms", chatResource::createRoom);
            ApiBuilder.get("/api/rooms/{id}/messages", chatResource::getRoomMessages);
            
            // Message routes
            ApiBuilder.put("/api/messages/{id}", chatResource::editMessage);
            ApiBuilder.delete("/api/messages/{id}", chatResource::deleteMessage);
            
            // WebSocket
            ApiBuilder.ws("/ws", wsHandler.handleWebSocket());
        }).start(8080);
        
        System.out.println("Chat application started at http://localhost:8080");
    }
    
    private static void initializeSampleRooms(ChatService service) {
        ChatRoom general = service.createRoom(
            "general",
            "General discussion room",
            "system"
        );
        
        ChatRoom tech = service.createRoom(
            "tech",
            "Technical discussions",
            "system"
        );
        
        ChatRoom random = service.createRoom(
            "random",
            "Random chat room",
            "system"
        );
    }
}
```

### Step 9: Run and Test

```bash
# Build project
cd javalin-chat-app
mvn clean package

# Run application
java -jar target/javalin-chat-app-1.0.0.jar

# Test REST API
# Register user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "password": "password123",
    "displayName": "User One"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "password": "password123"
  }'

# Create room
curl -X POST http://localhost:8080/api/rooms \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "my-room",
    "description": "My private room"
  }'

# Get rooms
curl http://localhost:8080/api/rooms

# Test WebSocket connection
# Use browser or websocket client to connect to ws://localhost:8080/ws?token=<TOKEN>
```

## Expected Output

The real-world project produces:
- User authentication with JWT
- Room-based chat with multiple rooms
- Real-time messaging via WebSockets
- Message persistence
- Typing indicators

## Build Instructions Summary

| Step | Command | Time |
|------|---------|------|
| Setup project | Maven setup | 15 min |
| Create models | Domain classes | 1 hour |
| Create services | Business logic | 1.5 hours |
| Create REST API | JAX-RS style | 1 hour |
| Create WebSocket | Real-time | 2 hours |
| Configure security | JWT | 1 hour |
| Test integration | Manual testing | 3 hours |

Total estimated time: 8-10 hours