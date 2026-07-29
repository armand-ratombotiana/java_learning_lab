# PROBLEM WALKTHROUGH: Implement REST API with HATEOAS and Pagination

## Problem Statement

Design and implement a RESTful API for a book catalog with full HATEOAS (Hypermedia as the Engine of Application State) support and pagination. The API should expose discoverable resources where clients navigate via links rather than hardcoded URLs.

**Requirements:**
- CRUD operations for `Book` and `Author` resources
- Paginated collection endpoints with `page`, `size`, `sort` parameters
- HATEOAS links: `self`, `next`, `prev`, `first`, `last`, related resources
- Support for `application/hal+json` content type
- Search endpoint with filtering
- Collection resources include `page` metadata

**Constraints:**
- Use Spring Boot 3.x with Spring HATEOAS
- Java 21+ records for DTOs
- In-memory data store (no JPA dependency)
- HAL format for hypermedia responses

---

## Step-by-Step Solution

### Step 1: Domain Model (Records)

```java
public record Book(
    String id,
    String title,
    String isbn,
    String authorId,
    String authorName,
    BigDecimal price,
    LocalDate publishedDate,
    Set<String> tags
) {}

public record Author(
    String id,
    String name,
    String biography,
    LocalDate birthDate
) {}

public record PageMetadata(
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {}
```

### Step 2: In-Memory Repository

```java
@Repository
public class BookRepository {

    private final ConcurrentHashMap<String, Book> store = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Book save(Book book) {
        String id = book.id() != null ? book.id() : String.valueOf(idCounter.getAndIncrement());
        Book saved = new Book(id, book.title(), book.isbn(), book.authorId(),
            book.authorName(), book.price(), book.publishedDate(), book.tags());
        store.put(id, saved);
        return saved;
    }

    public Optional<Book> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Book> findAll() {
        return List.copyOf(store.values());
    }

    public Page<Book> findAll(Pageable pageable) {
        List<Book> all = new ArrayList<>(store.values());

        // Sort
        if (pageable.getSort().isSorted()) {
            all.sort((b1, b2) -> {
                for (Sort.Order order : pageable.getSort()) {
                    int cmp = switch (order.getProperty()) {
                        case "title" -> b1.title().compareTo(b2.title());
                        case "price" -> b1.price().compareTo(b2.price());
                        case "publishedDate" -> b1.publishedDate().compareTo(b2.publishedDate());
                        default -> 0;
                    };
                    if (cmp != 0) return order.isAscending() ? cmp : -cmp;
                }
                return 0;
            });
        }

        int total = all.size();
        int from = Math.min((int) pageable.getOffset(), total);
        int to = Math.min(from + pageable.getPageSize(), total);
        List<Book> content = from <= to ? all.subList(from, to) : List.of();

        return new PageImpl<>(content, pageable, total);
    }

    public List<Book> search(String query, String tag, BigDecimal minPrice, BigDecimal maxPrice) {
        return store.values().stream()
            .filter(b -> query == null || b.title().toLowerCase().contains(query.toLowerCase())
                || b.authorName().toLowerCase().contains(query.toLowerCase()))
            .filter(b -> tag == null || b.tags().contains(tag))
            .filter(b -> minPrice == null || b.price().compareTo(minPrice) >= 0)
            .filter(b -> maxPrice == null || b.price().compareTo(maxPrice) <= 0)
            .toList();
    }

    public void deleteById(String id) {
        store.remove(id);
    }

    public long count() {
        return store.size();
    }
}

@Repository
public class AuthorRepository {
    private final ConcurrentHashMap<String, Author> store = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Author save(Author author) {
        String id = author.id() != null ? author.id() : String.valueOf(idCounter.getAndIncrement());
        Author saved = new Author(id, author.name(), author.biography(), author.birthDate());
        store.put(id, saved);
        return saved;
    }

    public Optional<Author> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Author> findAll() { return List.copyOf(store.values()); }
    public long count() { return store.size(); }
}
```

### Step 3: HATEOAS DTOs (RepresentationModel)

```java
public class BookModel extends RepresentationModel<BookModel> {
    private String id;
    private String title;
    private String isbn;
    private String authorId;
    private String authorName;
    private BigDecimal price;
    private LocalDate publishedDate;
    private Set<String> tags;

    // Getters and setters (required by Jackson/HATEOAS serialization)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public LocalDate getPublishedDate() { return publishedDate; }
    public void setPublishedDate(LocalDate publishedDate) { this.publishedDate = publishedDate; }
    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }

    public static BookModel from(Book book) {
        BookModel model = new BookModel();
        model.setId(book.id());
        model.setTitle(book.title());
        model.setIsbn(book.isbn());
        model.setAuthorId(book.authorId());
        model.setAuthorName(book.authorName());
        model.setPrice(book.price());
        model.setPublishedDate(book.publishedDate());
        model.setTags(book.tags());
        return model;
    }
}

public class AuthorModel extends RepresentationModel<AuthorModel> {
    private String id;
    private String name;
    private String biography;
    private LocalDate birthDate;
    private List<BookModel> books;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public List<BookModel> getBooks() { return books; }
    public void setBooks(List<BookModel> books) { this.books = books; }

    public static AuthorModel from(Author author) {
        AuthorModel model = new AuthorModel();
        model.setId(author.id());
        model.setName(author.name());
        model.setBiography(author.biography());
        model.setBirthDate(author.birthDate());
        return model;
    }
}

public class PagedBookModel extends RepresentationModel<PagedBookModel> {
    private List<BookModel> books;
    private PageMetadata page;

    public List<BookModel> getBooks() { return books; }
    public void setBooks(List<BookModel> books) { this.books = books; }
    public PageMetadata getPage() { return page; }
    public void setPage(PageMetadata page) { this.page = page; }
}
```

### Step 4: HATEOAS Link Builder

```java
@Component
public class BookLinkBuilder {

    public Link buildSelfLink(String bookId) {
        return linkTo(methodOn(BookController.class).getBook(bookId)).withSelfRel();
    }

    public Link buildAuthorLink(String authorId) {
        return linkTo(methodOn(AuthorController.class).getAuthor(authorId))
            .withRel("author");
    }

    public Link buildCollectionLink() {
        return linkTo(methodOn(BookController.class)
            .getAllBooks(0, 20, "title,asc"))
            .withRel("books");
    }

    public Link buildSearchLink() {
        return linkTo(methodOn(BookController.class)
            .searchBooks(null, null, null, null))
            .withRel("search");
    }

    public List<Link> buildPaginationLinks(Page<?> page, String sort) {
        List<Link> links = new ArrayList<>();
        int current = page.getNumber();
        int total = page.getTotalPages();

        links.add(linkTo(methodOn(BookController.class)
            .getAllBooks(current, page.getSize(), sort)).withSelfRel());

        if (page.hasPrevious()) {
            links.add(linkTo(methodOn(BookController.class)
                .getAllBooks(0, page.getSize(), sort)).withRel("first"));
            links.add(linkTo(methodOn(BookController.class)
                .getAllBooks(current - 1, page.getSize(), sort)).withRel("prev"));
        }
        if (page.hasNext()) {
            links.add(linkTo(methodOn(BookController.class)
                .getAllBooks(current + 1, page.getSize(), sort)).withRel("next"));
            links.add(linkTo(methodOn(BookController.class)
                .getAllBooks(total - 1, page.getSize(), sort)).withRel("last"));
        }
        return links;
    }
}
```

### Step 5: Book Controller with HATEOAS

```java
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;
    private final BookLinkBuilder linkBuilder;

    public BookController(BookRepository bookRepository, BookLinkBuilder linkBuilder) {
        this.bookRepository = bookRepository;
        this.linkBuilder = linkBuilder;
    }

    @GetMapping
    public ResponseEntity<PagedBookModel> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title,asc") String sort) {

        Sort sorting = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sorting);
        Page<Book> bookPage = bookRepository.findAll(pageable);

        PagedBookModel pagedModel = new PagedBookModel();
        pagedModel.setBooks(bookPage.getContent().stream()
            .map(this::toModelWithLinks)
            .toList());
        pagedModel.setPage(new PageMetadata(
            bookPage.getNumber(),
            bookPage.getSize(),
            bookPage.getTotalElements(),
            bookPage.getTotalPages(),
            bookPage.isFirst(),
            bookPage.isLast()
        ));

        // Add pagination links
        pagedModel.add(linkBuilder.buildPaginationLinks(bookPage, sort));

        // Add top-level navigation links
        pagedModel.add(linkBuilder.buildSearchLink());
        pagedModel.add(linkTo(methodOn(AuthorController.class).getAllAuthors()).withRel("authors"));

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookModel> getBook(@PathVariable String id) {
        return bookRepository.findById(id)
            .map(book -> ResponseEntity.ok(toModelWithLinks(book)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookModel>> searchBooks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        List<Book> results = bookRepository.search(query, tag, minPrice, maxPrice);
        List<BookModel> models = results.stream()
            .map(this::toModelWithLinks)
            .toList();

        return ResponseEntity.ok(models);
    }

    @PostMapping
    public ResponseEntity<BookModel> createBook(@RequestBody CreateBookRequest request) {
        Book book = new Book(null, request.title(), request.isbn(), request.authorId(),
            request.authorName(), request.price(), request.publishedDate(), request.tags());
        Book saved = bookRepository.save(book);
        BookModel model = toModelWithLinks(saved);
        return ResponseEntity.created(
            linkTo(methodOn(BookController.class).getBook(saved.id())).toUri())
            .body(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookModel> updateBook(@PathVariable String id,
                                                  @RequestBody CreateBookRequest request) {
        return bookRepository.findById(id).map(existing -> {
            Book updated = bookRepository.save(new Book(id, request.title(), request.isbn(),
                request.authorId(), request.authorName(), request.price(),
                request.publishedDate(), request.tags()));
            return ResponseEntity.ok(toModelWithLinks(updated));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        if (bookRepository.findById(id).isPresent()) {
            bookRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    BookModel toModelWithLinks(Book book) {
        BookModel model = BookModel.from(book);
        model.add(linkBuilder.buildSelfLink(book.id()));
        model.add(linkBuilder.buildAuthorLink(book.authorId()));
        model.add(linkBuilder.buildCollectionLink());
        return model;
    }

    private Sort parseSort(String sortParam) {
        String[] parts = sortParam.split(",");
        String property = parts[0];
        boolean asc = parts.length < 2 || "asc".equalsIgnoreCase(parts[1]);
        return asc ? Sort.by(property).ascending() : Sort.by(property).descending();
    }
}
```

### Step 6: Author Controller

```java
@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorController(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public ResponseEntity<List<AuthorModel>> getAllAuthors() {
        List<AuthorModel> models = authorRepository.findAll().stream()
            .map(this::toModelWithLinks)
            .toList();
        return ResponseEntity.ok(models);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorModel> getAuthor(@PathVariable String id) {
        return authorRepository.findById(id)
            .map(author -> ResponseEntity.ok(toModelWithLinks(author)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<List<BookModel>> getAuthorBooks(@PathVariable String id) {
        return authorRepository.findById(id).map(author -> {
            List<BookModel> books = bookRepository.findAll().stream()
                .filter(b -> b.authorId().equals(id))
                .map(book -> {
                    BookModel model = BookModel.from(book);
                    model.add(linkTo(methodOn(BookController.class).getBook(book.id())).withSelfRel());
                    return model;
                })
                .toList();
            return ResponseEntity.ok(books);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AuthorModel> createAuthor(@RequestBody CreateAuthorRequest request) {
        Author author = new Author(null, request.name(), request.biography(), request.birthDate());
        Author saved = authorRepository.save(author);
        return ResponseEntity.created(
            linkTo(methodOn(AuthorController.class).getAuthor(saved.id())).toUri())
            .body(toModelWithLinks(saved));
    }

    AuthorModel toModelWithLinks(Author author) {
        AuthorModel model = AuthorModel.from(author);
        model.add(linkTo(methodOn(AuthorController.class).getAuthor(author.id())).withSelfRel());
        model.add(linkTo(methodOn(AuthorController.class).getAuthorBooks(author.id())).withRel("books"));
        return model;
    }
}
```

### Step 7: Request DTOs

```java
public record CreateBookRequest(
    String title,
    String isbn,
    String authorId,
    String authorName,
    BigDecimal price,
    LocalDate publishedDate,
    Set<String> tags
) {}

public record CreateAuthorRequest(
    String name,
    String biography,
    LocalDate birthDate
) {}
```

### Step 8: Configuration

```java
@Configuration
public class HateoasConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .defaultContentType(MediaTypes.HAL_JSON)
            .mediaType("hal+json", MediaTypes.HAL_JSON)
            .favorParameter(true)
            .parameterName("format")
            .ignoreAcceptHeader(false);
    }
}

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public DataInitializer(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    public void run(String... args) {
        Author tolkien = authorRepository.save(
            new Author(null, "J.R.R. Tolkien", "English writer and philologist",
                LocalDate.of(1892, 1, 3)));
        Author asimov = authorRepository.save(
            new Author(null, "Isaac Asimov", "American writer and biochemist",
                LocalDate.of(1920, 1, 2)));

        bookRepository.save(new Book(null, "The Hobbit", "978-0547928227",
            tolkien.id(), tolkien.name(), new BigDecimal("14.99"),
            LocalDate.of(1937, 9, 21), Set.of("fantasy", "adventure")));
        bookRepository.save(new Book(null, "The Fellowship of the Ring", "978-0547928210",
            tolkien.id(), tolkien.name(), new BigDecimal("16.99"),
            LocalDate.of(1954, 7, 29), Set.of("fantasy", "epic")));
        bookRepository.save(new Book(null, "Foundation", "978-0553293357",
            asimov.id(), asimov.name(), new BigDecimal("9.99"),
            LocalDate.of(1951, 6, 1), Set.of("sci-fi", "classic")));
        bookRepository.save(new Book(null, "I, Robot", "978-0553294385",
            asimov.id(), asimov.name(), new BigDecimal("8.99"),
            LocalDate.of(1950, 12, 2), Set.of("sci-fi", "robots")));
    }
}
```

### Step 9: Sample HAL+JSON Response

```json
GET /api/books?page=0&size=2&sort=title,asc
{
  "books": [
    {
      "id": "4",
      "title": "Foundation",
      "isbn": "978-0553293357",
      "authorId": "2",
      "authorName": "Isaac Asimov",
      "price": 9.99,
      "publishedDate": "1951-06-01",
      "tags": ["classic", "sci-fi"],
      "_links": {
        "self": { "href": "http://localhost:8080/api/books/4" },
        "author": { "href": "http://localhost:8080/api/authors/2" },
        "books": { "href": "http://localhost:8080/api/books?page=0&size=20&sort=title,asc" }
      }
    },
    {
      "id": "1",
      "title": "I, Robot",
      "isbn": "978-0553294385",
      "authorId": "2",
      "authorName": "Isaac Asimov",
      "price": 8.99,
      "publishedDate": "1950-12-02",
      "tags": ["robots", "sci-fi"],
      "_links": {
        "self": { "href": "http://localhost:8080/api/books/4" },
        "author": { "href": "http://localhost:8080/api/authors/2" },
        "books": { "href": "http://localhost:8080/api/books?page=0&size=20&sort=title,asc" }
      }
    }
  ],
  "page": {
    "page": 0,
    "size": 2,
    "totalElements": 4,
    "totalPages": 2,
    "first": true,
    "last": false
  },
  "_links": {
    "self": { "href": "http://localhost:8080/api/books?page=0&size=2&sort=title,asc" },
    "first": { "href": "http://localhost:8080/api/books?page=0&size=2&sort=title,asc" },
    "next": { "href": "http://localhost:8080/api/books?page=1&size=2&sort=title,asc" },
    "last": { "href": "http://localhost:8080/api/books?page=1&size=2&sort=title,asc" },
    "search": { "href": "http://localhost:8080/api/books/search" },
    "authors": { "href": "http://localhost:8080/api/authors" }
  }
}
```

---

## Complexity Analysis

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|-----------------|
| GET /books (paginated) | O(N log N) sort + O(pageSize) slice | O(pageSize) |
| GET /books/{id} | O(1) hash lookup | O(1) |
| POST /books | O(1) insert | O(1) |
| PUT /books/{id} | O(1) update | O(1) |
| DELETE /books/{id} | O(1) remove | O(1) |
| GET /search | O(N) full scan | O(k) results |
| GET /authors/{id}/books | O(N) filter | O(k) results |

---

## Follow-Up Questions

1. **How would you add ETag support for caching?** — Return `ResponseEntity.ok().eTag(computeHash(model))`. Validate `If-None-Match` header. For collection endpoints, ETag represents the page hash.

2. **How do you handle partial updates (PATCH)?** — Accept `Map<String, Object>` of fields to update. Merge with existing resource. Return updated representation with links.

3. **How would you version this API?** — URL-based (`/api/v1/books`), header-based (`Accept: application/vnd.books.v1+json`), or query param (`?version=1`). HATEOAS links should include version in base URI.

4. **How do you secure HATEOAS APIs?** — Links should respect authentication: filter links based on user roles. A regular user doesn't see "admin" action links. Use `LinkBuilder` with security context.

5. **How would you implement conditional requests (If-Modified-Since)?** — Track `lastModified` per resource. Return `304 Not Modified` if client cache is fresh. Include `Last-Modified` header in responses.

---

## Test Cases

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnPaginatedBooksWithHateoasLinks() throws Exception {
        mockMvc.perform(get("/api/books?page=0&size=2&sort=title,asc")
                .accept("application/hal+json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.books").isArray())
            .andExpect(jsonPath("$.books.length()").value(2))
            .andExpect(jsonPath("$.page.page").value(0))
            .andExpect(jsonPath("$.page.totalElements").value(4))
            .andExpect(jsonPath("$._links.self").exists())
            .andExpect(jsonPath("$._links.next").exists())
            .andExpect(jsonPath("$._links.first").exists())
            .andExpect(jsonPath("$._links.last").exists())
            .andExpect(jsonPath("$.books[0]._links.self").exists())
            .andExpect(jsonPath("$.books[0]._links.author").exists());
    }

    @Test
    void shouldReturnSingleBookWithLinks() throws Exception {
        String response = mockMvc.perform(get("/api/books/1")
                .accept("application/hal+json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$._links.self.href").value(containsString("/api/books/1")))
            .andReturn().getResponse().getContentAsString();
    }

    @Test
    void shouldReturn404ForMissingBook() throws Exception {
        mockMvc.perform(get("/api/books/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateBookAndReturnLocation() throws Exception {
        var request = new CreateBookRequest("New Book", "123-456", "1", "Author",
            new BigDecimal("19.99"), LocalDate.now(), Set.of("fiction"));
        String body = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept("application/hal+json"))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.title").value("New Book"))
            .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    void shouldDeleteBook() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
            .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/books/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldSearchBooks() throws Exception {
        mockMvc.perform(get("/api/books/search?query=hobbit"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("The Hobbit"));
    }

    @Test
    void shouldFilterByTag() throws Exception {
        mockMvc.perform(get("/api/books/search?tag=fantasy"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnAuthorWithBookLinks() throws Exception {
        mockMvc.perform(get("/api/authors/1")
                .accept("application/hal+json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("J.R.R. Tolkien"))
            .andExpect(jsonPath("$._links.self").exists())
            .andExpect(jsonPath("$._links.books").exists());
    }

    @Test
    void shouldReturnAuthorBooks() throws Exception {
        mockMvc.perform(get("/api/authors/1/books"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0]._links.self").exists());
    }
}
```

---

## Summary

This implementation demonstrates:
- **HATEOAS** with Spring HATEOAS and HAL format
- **Pagination** with `Page`, `Pageable`, and navigation links (`first`, `prev`, `next`, `last`)
- **Discoverable resources**: clients navigate via `_links` without hardcoded URLs
- **Search and filtering**: query parameters with HATEOAS search link
- **Resource relationships**: books link to authors, authors link to their books, collection links on every resource