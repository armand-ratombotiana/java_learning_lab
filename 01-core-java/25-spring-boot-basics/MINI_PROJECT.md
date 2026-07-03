# Module 25: Spring Boot Basics - Mini Project

**Project Name**: Minimalist Book Library API  
**Difficulty Level**: Intermediate  
**Estimated Time**: 2 hours

---

## 🎯 Objective
Create a fully functional REST API from scratch using Spring Boot, demonstrating the use of Spring Web, Auto-Configuration, Dependency Injection, and Externalized Properties.

## 📝 Requirements

### Core Features

1. **Project Setup**:
   - Initialize a Spring Boot project (using Spring Initializr or manually via `pom.xml`).
   - Include the `spring-boot-starter-web` dependency.

2. **Domain Model**:
   - Create a simple `Book` class (or record) with fields: `id` (Long), `title` (String), `author` (String), `isbn` (String).

3. **Data Layer (In-Memory)**:
   - Create a `@Repository` class named `BookRepository`.
   - Instead of a real database, use a `ConcurrentHashMap<Long, Book>` to store books in memory.
   - Implement basic CRUD methods: `save(Book)`, `findById(Long)`, `findAll()`, `deleteById(Long)`.

4. **Service Layer**:
   - Create a `@Service` class named `BookService`.
   - Inject the `BookRepository` using Constructor Injection (do not use `@Autowired` on the field).
   - Add simple business logic (e.g., ensure the ISBN is not empty before saving).

5. **REST Controller**:
   - Create a `@RestController` named `BookController` mapped to `/api/books`.
   - Inject the `BookService`.
   - Implement the following endpoints:
     - `GET /api/books` -> Returns all books.
     - `POST /api/books` -> Creates a new book.
     - `GET /api/books/{id}` -> Returns a specific book.

6. **Externalized Configuration**:
   - In `src/main/resources/application.properties`, change the default server port to `8081`.
   - Add a custom property `library.name=My Minimalist Library`.
   - Inject this property into the Controller using `@Value("${library.name}")` and create a `GET /api/books/info` endpoint that returns it.

---

## 💡 Solution Blueprint

1. **POM setup**:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-web</artifactId>
   </dependency>
   ```

2. **Repository**:
   ```java
   @Repository
   public class BookRepository {
       private final Map<Long, Book> db = new ConcurrentHashMap<>();
       private final AtomicLong idGenerator = new AtomicLong();
       
       public Book save(Book book) {
           long id = idGenerator.incrementAndGet();
           book.setId(id);
           db.put(id, book);
           return book;
       }
       // ... other methods
   }
   ```

3. **Service (Constructor Injection)**:
   ```java
   @Service
   public class BookService {
       private final BookRepository repository;
       
       public BookService(BookRepository repository) {
           this.repository = repository;
       }
       // ...
   }
   ```

4. **Controller & Properties**:
   ```java
   @RestController
   @RequestMapping("/api/books")
   public class BookController {
       private final BookService service;
       
       @Value("${library.name}")
       private String libraryName;
       
       public BookController(BookService service) { this.service = service; }
       
       @GetMapping("/info")
       public String getInfo() { return "Welcome to " + libraryName; }
       
       // CRUD endpoints mapped with @GetMapping, @PostMapping, etc.
   }
   ```