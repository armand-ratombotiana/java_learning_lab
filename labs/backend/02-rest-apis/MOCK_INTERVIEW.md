# Mock Interview: REST APIs (Lab 02)

**Role:** Backend Engineer (Mid-Level)  
**Duration:** 45 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What are the key principles of RESTful API design?

**Candidate:** REST (Representational State Transfer) has six key constraints:
1. **Uniform Interface** — resources identified in requests, manipulation through representations, self-descriptive messages, HATEOAS
2. **Stateless** — each request contains all necessary information, no client context on server
3. **Cacheable** — responses must implicitly or explicitly label themselves as cacheable or non-cacheable
4. **Client-Server** — separation of concerns, clients don't care about data storage
5. **Layered System** — client can't tell if it's connected directly to the end server or an intermediary
6. **Code on Demand** (optional) — server can extend client functionality via executable code

**Interviewer:** How do you handle validation in REST APIs with Spring Boot?

**Candidate:** Spring Boot supports Bean Validation 3.0 (Jakarta Validation) through `@Valid` annotation on request body parameters. For example:
```java
@PostMapping("/users")
public ResponseEntity<User> createUser(@Valid @RequestBody User user) { ... }
```
The `User` class uses annotations like `@NotBlank`, `@Email`, `@Size`, `@Pattern` on fields. Custom validators can be created by implementing `ConstraintValidator`. For group validation, `@Validated` with groups can apply different validation rules for create vs update scenarios.

**Interviewer:** Good. How would you handle errors consistently?

**Candidate:** I use a `@ControllerAdvice` with `@ExceptionHandler` methods to return consistent error responses. A custom `ErrorResponse` DTO with fields like `timestamp`, `status`, `error`, `message`, `path`, and optional `errors` list for field-level validation errors. Spring Boot also provides `ResponseEntityExceptionHandler` which can be extended for consistent handling of built-in exceptions.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** Design a REST API for a library management system. Consider books, members, and borrowing.

**Candidate:**

**Resources:**
- `/api/books` — book catalog
- `/api/members` — library members
- `/api/loans` — book borrow/return tracking

**Endpoints:**
```http
GET    /api/books?genre={genre}&author={author}&page={page}&size={size}
GET    /api/books/{id}
POST   /api/books
PUT    /api/books/{id}
PATCH  /api/books/{id}/status
DELETE /api/books/{id}
GET    /api/members/{id}/loans?status=active
POST   /api/loans — { memberId, bookId, dueDate }
PUT    /api/loans/{id}/return
```

**Design decisions:**
- Pagination via `page` and `size` query parameters with `Pageable` in Spring
- Filtering via query parameters, not in the path
- PATCH for partial updates (changing only book status)
- Hypermedia links in responses for discoverability
- ETags for optimistic concurrency on book updates

**Interviewer:** How would you implement filtering and sorting efficiently for the books endpoint?

**Candidate:** Spring Data JPA's `Specification` pattern for dynamic queries. I'd create a `BookSpecification` builder that composes `JpaSpecificationExecutor` predicates dynamically:
```java
public class BookSpecification {
    public static Specification<Book> hasGenre(String genre) {
        return (root, query, cb) -> genre == null ? null : cb.equal(root.get("genre"), genre);
    }
    public static Specification<Book> hasAuthor(String author) {
        return (root, query, cb) -> author == null ? null : cb.like(root.get("author"), "%" + author + "%");
    }
}
```
Sorting would use Spring's `Sort` parameter: `sort=title,asc&sort=publishDate,desc`. For high-traffic endpoints, I'd add Redis caching with invalidation on book updates.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** You have a REST endpoint that needs to handle 10,000 concurrent file uploads. How do you design it?

**Candidate:** For high-concurrency file uploads:

1. **Use multipart streaming** instead of buffering entire files in memory. Configure `spring.servlet.multipart.max-request-size` but also `file-size-threshold` to control when files spill to disk.

2. **Async processing:** Accept the upload request synchronously but return immediately with a `202 Accepted` and a `location` header pointing to a processing status endpoint. Process the file asynchronously:
```java
@PostMapping("/uploads")
public ResponseEntity<UploadResponse> upload(@RequestParam MultipartFile file) {
    String uploadId = uploadService.submitForProcessing(file);
    return ResponseEntity.accepted()
        .header("Location", "/api/uploads/" + uploadId + "/status")
        .body(new UploadResponse(uploadId, "PENDING"));
}
```

3. **Chunked uploads** for large files: Accept `Content-Type: application/octet-stream` with a chunk index, reassemble server-side.

4. **Stream directly to storage:** Don't save to local disk. Use `InputStream` to stream directly to S3 or Azure Blob:
```java
@PostMapping("/uploads/stream")
public ResponseEntity<Void> uploadStream(InputStream inputStream) {
    storageService.streamToCloud(inputStream);
    return ResponseEntity.ok().build();
}
```

5. **Rate limiting** per user to prevent abuse using `Bucket4j` or `Resilience4J` rate limiter.

6. **Connection pooling:** Increase Tomcat's `max-connections` and `max-threads` appropriately. For WebFlux, the non-blocking model handles this much better (Lab 15).

**Interviewer:** How would you version a REST API without breaking existing clients?

**Candidate:** Three main approaches:

1. **URI versioning:** `/api/v1/books`, `/api/v2/books` — simple but pollutes URI space
2. **Header versioning:** `Accept: application/vnd.company.books.v1+json` — clean URI but harder to test
3. **Query parameter versioning:** `/api/books?version=1` — simple but encourages caching issues

I prefer a combination: URI versioning for major versions (v1, v2) with content negotiation for minor versions. Major version changes require new endpoints and coexistence period. Minor changes (adding fields) are backward compatible. I'd maintain a version migration guide and deprecation timeline, using `@RequestMapping` on separate controller classes for each version with shared service layer.

**Interviewer:** How do you handle N+1 queries in REST API responses that return nested resources?

**Candidate:** The N+1 problem occurs when fetching a list of parent entities and then lazily loading child entities one-by-one. Solutions:

1. **`JOIN FETCH` in JPQL:** `SELECT b FROM Book b JOIN FETCH b.author` — eager fetch in single query
2. **`@EntityGraph`:** `@EntityGraph(attributePaths = {"author", "reviews"})` — declarative fetch plan
3. **DTO projections:** Create `BookSummary` DTO with only needed fields, avoids loading full entity graph
4. **Batch fetching:** `@BatchSize(size = 10)` on collection — reduces queries to `ceil(N / batchSize)`
5. **Spring Data REST projections:** `@Projection` interfaces to control what's exposed

For REST APIs specifically, I'd also consider:
- Using DataLoader pattern (like GraphQL's DGS, Lab 22) to batch parent-child lookups
- Exposing separate endpoints for nested resources and letting clients compose
- Supporting `?include=author,reviews` query parameter for selective eager loading

---

## Interviewer Feedback

**Strengths:**
- Solid understanding of REST constraints and best practices
- Good design for high-concurrency scenarios
- Practical versioning strategies

**Areas to Improve:**
- Should mention idempotency handling for POST endpoints (idempotency keys)
- Could discuss HATEOAS implementation in Spring Boot more concretely

**Verdict:** Strong Hire

---

## Follow-Up Questions

1. How would you implement idempotency for a payment API?
2. What's the difference between `@Controller` and `@RestController`?
3. How do you handle partial updates with PATCH vs PUT with complete replacement?
4. Design a bulk API endpoint that accepts 1000 items per request.
5. How would you implement pagination for a cursor-based API?

---

*Lab 02 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
