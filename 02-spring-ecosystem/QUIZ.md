# Spring Ecosystem Quiz

## Section 1: Spring Core

**Question 1:** What is the default scope of a Spring bean?
- A) prototype
- B) request
- C) singleton
- D) session

**Answer:** C) singleton

---

**Question 2:** Which annotation is used to indicate a class as a Spring-managed component?
- A) @Service
- B) @Component
- C) @Bean
- D) @Configuration

**Answer:** B) @Component (@Service, @Repository, @Controller are specializations)

---

**Question 3:** What is the difference between @Bean and @Component?
- A) They are identical
- B) @Component is for classes, @Bean is for methods
- C) @Bean creates proxies, @Component doesn't
- D) @Component is for third-party classes

**Answer:** B) @Component marks classes for auto-scanning; @Bean is used in @Configuration classes to explicitly declare beans

---

**Question 4:** Which dependency injection approach is recommended?
- A) Field injection
- B) Setter injection
- C) Constructor injection
- D) Interface injection

**Answer:** C) Constructor injection - enables immutability and easier testing

---

**Question 5:** What does @Profile do?
- A) Enables security
- B) Activates beans only in specific environments
- C) Sets bean priority
- D) Enables caching

**Answer:** B) Activates beans only in specific environments (dev, prod, test)

---

## Section 2: Spring MVC

**Question 6:** What is the DispatcherServlet responsible for?
- A) Database connections
- B) Routing requests to handlers
- C) Managing transactions
- D) Compiling JSP files

**Answer:** B) Routing requests to handlers

---

**Question 7:** Which annotation indicates a REST controller?
- A) @Controller
- B) @RestController
- C) @RequestMapping
- D) @ResponseBody

**Answer:** B) @RestController (combines @Controller and @ResponseBody)

---

**Question 8:** What does @Valid do?
- A) Validates input format
- B) Enables Bean Validation
- C) Checks authentication
- D) Tests performance

**Answer:** B) Enables Bean Validation annotations (@NotNull, @Size, etc.)

---

**Question 9:** Which HTTP method is typically used for updating an existing resource?
- A) GET
- B) POST
- C) PUT
- D) DELETE

**Answer:** C) PUT (or PATCH for partial updates)

---

**Question 10:** What is the purpose of @RequestParam?
- A) Extracts path variables
- B) Extracts query parameters
- C) Extracts request body
- D) Extracts headers

**Answer:** B) Extracts query parameters (e.g., /search?q=term)

---

## Section 3: Spring Data

**Question 11:** What interface does JpaRepository extend?
- A) CrudRepository
- B) Repository
- C) Both A and B
- D) Neither

**Answer:** C) JpaRepository extends both CrudRepository and PagingAndSortingRepository

---

**Question 12:** What does @Transactional(readOnly = true) indicate?
- A) No transaction needed
- B) Optimizes database read operations
- C) Prevents writes
- D) Throws exception on writes

**Answer:** B) Enables optimization for read operations (may use read-only transactions)

---

**Question 13:** Which query method naming convention finds by exact email match?
- A) findByEmailContains
- B) findByEmailEquals
- C) findByEmail
- D) Both B and C

**Answer:** D) Both "findByEmail" and "findByEmailEquals" work; both search for exact match

---

**Question 14:** What does @Modifying indicate in a JPQL query?
- A) The query is read-only
- B) The query modifies data
- C) The query uses native SQL
- D) The query returns multiple results

**Answer:** B) Indicates the query will update, delete, or modify data

---

**Question 15:** What is the purpose of @Query annotation?
- A) Defines entity relationships
- B) Specifies custom JPQL or SQL
- C) Creates indexes
- D) Sets column names

**Answer:** B) Specifies custom JPQL or native SQL queries

---

## Advanced Questions

**Question 16:** What is the difference between @Required and @Autowired?
- A) They are the same
- B) @Required checks for property injection, @Autowired performs injection
- C) @Required is for constructors
- D) @Autowired is deprecated

**Answer:** B) @Required was for property validation (now deprecated), @Autowired performs injection

---

**Question 17:** What does @Primary annotation do?
- A) Marks bean as singleton
- B) Indicates highest priority when multiple beans of same type exist
- C) Enables caching
- D) Sets bean to load on startup

**Answer:** B) Indicates this bean should be preferred when multiple candidates exist

---

**Question 18:** Which AOP advice type runs after method execution completes successfully?
- A) @Before
- B) @After
- C) @AfterReturning
- D) @Around

**Answer:** C) @AfterReturning (runs only on success; @After runs regardless of outcome)

---

**Question 19:** What is the default transaction propagation?
- A) REQUIRES_NEW
- B) REQUIRED
- C) SUPPORTS
- D) NOT_SUPPORTED

**Answer:** B) REQUIRED - uses existing transaction or creates new one

---

**Question 20:** What does @ControllerAdvice do?
- A) Adds security to controllers
- B) Global exception handling for controllers
- C) Intercepts all HTTP requests
- D) Enables CORS

**Answer:** B) Provides global exception handling for @RestController and @Controller classes

---

## Score Interpretation

| Score | Level |
|-------|-------|
| 18-20 | Expert |
| 14-17 | Advanced |
| 10-13 | Intermediate |
| 5-9 | Beginner |
| < 5 | Foundation needed |