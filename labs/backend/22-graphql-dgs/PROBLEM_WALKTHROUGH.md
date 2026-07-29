# PROBLEM WALKTHROUGH: Implement GraphQL Resolver with DataLoader Batching

## Problem Statement

Implement a GraphQL API for a library management system using Spring for GraphQL, with efficient DataLoader-based batching to solve the N+1 query problem. The system should:

- Define GraphQL schema for `Author`, `Book`, and `Review` types
- Implement `@BatchMapping` resolvers to batch database queries
- Implement a custom `DataLoader` for cross-service data fetching
- Support nested queries without N+1 performance degradation
- Handle subscriptions for real-time updates (new reviews)
- Implement query complexity analysis and depth limiting
- Include mutation operations with input validation

**Constraints:**
- Spring for GraphQL (spring-boot-starter-graphql)
- Java 21+ with records
- In-memory repositories
- No DGS Framework (standard Spring for GraphQL)

---

## Step-by-Step Solution

### Step 1: GraphQL Schema (`schema.graphqls`)

```graphql
type Query {
    author(id: ID!): Author
    authors: [Author!]!
    book(id: ID!): Book
    books(limit: Int, offset: Int): [Book!]!
    searchBooks(query: String!): [Book!]!
    topRatedBooks(minRating: Float!, limit: Int): [Book!]!
}

type Mutation {
    addBook(input: BookInput!): Book!
    addReview(input: ReviewInput!): Review!
    updateAuthor(id: ID!, input: AuthorInput!): Author!
    deleteBook(id: ID!): Boolean!
}

type Subscription {
    reviewAdded(bookId: ID!): Review!
    bookAdded: Book!
}

type Author {
    id: ID!
    name: String!
    birthDate: String
    biography: String
    books: [Book!]!
    averageRating: Float
}

type Book {
    id: ID!
    title: String!
    isbn: String!
    publishedYear: Int!
    author: Author!
    reviews: [Review!]!
    averageRating: Float
    reviewCount: Int
}

type Review {
    id: ID!
    bookId: ID!
    reviewerName: String!
    rating: Int!
    comment: String
    createdAt: String!
}

input BookInput {
    title: String!
    isbn: String!
    publishedYear: Int!
    authorId: ID!
}

input ReviewInput {
    bookId: ID!
    reviewerName: String!
    rating: Int!
    comment: String
}

input AuthorInput {
    name: String
    birthDate: String
    biography: String
}
```

### Step 2: Domain Records (Java 21)

```java
public record Author(
    String id,
    String name,
    String birthDate,
    String biography
) {}

public record Book(
    String id,
    String title,
    String isbn,
    int publishedYear,
    String authorId
) {}

public record Review(
    String id,
    String bookId,
    String reviewerName,
    int rating,
    String comment,
    Instant createdAt
) {}
```

### Step 3: Repositories

```java
@Repository
public class AuthorRepository {
    private final ConcurrentHashMap<String, Author> store = new ConcurrentHashMap<>();

    public AuthorRepository() {
        save(new Author("author-1", "J.K. Rowling", "1965-07-31",
            "British author, best known for Harry Potter"));
        save(new Author("author-2", "George R.R. Martin", "1948-09-20",
            "American novelist, known for A Song of Ice and Fire"));
        save(new Author("author-3", "Isaac Asimov", "1920-01-02",
            "American writer and biochemistry professor"));
    }

    public Author save(Author author) {
        String id = author.id() != null ? author.id() : UUID.randomUUID().toString();
        Author saved = new Author(id, author.name(), author.birthDate(), author.biography());
        store.put(id, saved);
        return saved;
    }

    public Optional<Author> findById(String id) { return Optional.ofNullable(store.get(id)); }

    public List<Author> findAllById(Collection<String> ids) {
        return ids.stream().map(store::get).filter(Objects::nonNull).toList();
    }

    public List<Author> findAll() { return List.copyOf(store.values()); }
}

@Repository
public class BookRepository {
    private final ConcurrentHashMap<String, Book> store = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    public BookRepository() {
        save(new Book(null, "Harry Potter and the Philosopher's Stone",
            "978-0747532699", 1997, "author-1"));
        save(new Book(null, "Harry Potter and the Chamber of Secrets",
            "978-0439064873", 1998, "author-1"));
        save(new Book(null, "A Game of Thrones", "978-0553103540", 1996, "author-2"));
        save(new Book(null, "A Clash of Kings", "978-0553108033", 1998, "author-2"));
        save(new Book(null, "Foundation", "978-0553293357", 1951, "author-3"));
        save(new Book(null, "I, Robot", "978-0553294385", 1950, "author-3"));
    }

    public Book save(Book book) {
        String id = book.id() != null ? book.id() : String.valueOf(counter.getAndIncrement());
        Book saved = new Book(id, book.title(), book.isbn(), book.publishedYear(), book.authorId());
        store.put(id, saved);
        return saved;
    }

    public Optional<Book> findById(String id) { return Optional.ofNullable(store.get(id)); }

    public List<Book> findAll() { return List.copyOf(store.values()); }

    public List<Book> findAllByAuthorId(String authorId) {
        return store.values().stream().filter(b -> b.authorId().equals(authorId)).toList();
    }

    public List<Book> findAllByAuthorIdIn(Collection<String> authorIds) {
        Set<String> idSet = Set.copyOf(authorIds);
        return store.values().stream().filter(b -> idSet.contains(b.authorId())).toList();
    }

    public List<Book> search(String query) {
        return store.values().stream()
            .filter(b -> b.title().toLowerCase().contains(query.toLowerCase()))
            .toList();
    }

    public void deleteById(String id) { store.remove(id); }
}

@Repository
public class ReviewRepository {
    private final ConcurrentHashMap<String, Review> store = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    public Review save(Review review) {
        String id = review.id() != null ? review.id() : String.valueOf(counter.getAndIncrement());
        Review saved = new Review(id, review.bookId(), review.reviewerName(),
            review.rating(), review.comment(), review.createdAt());
        store.put(id, saved);
        return saved;
    }

    public List<Review> findByBookId(String bookId) {
        return store.values().stream()
            .filter(r -> r.bookId().equals(bookId))
            .sorted(Comparator.comparing(Review::createdAt).reversed())
            .toList();
    }

    public Map<String, List<Review>> findByBookIds(Collection<String> bookIds) {
        Set<String> idSet = Set.copyOf(bookIds);
        return store.values().stream()
            .filter(r -> idSet.contains(r.bookId()))
            .collect(Collectors.groupingBy(Review::bookId));
    }

    public Optional<Review> findLatestForBook(String bookId) {
        return findByBookId(bookId).stream().findFirst();
    }
}
```

### Step 4: DataLoader Configuration

```java
@Configuration
public class DataLoaderConfig {

    @Bean
    public DataLoaderRegistryFactory dataLoaderRegistryFactory(
            BookRepository bookRepository,
            ReviewRepository reviewRepository) {
        return () -> {
            DataLoaderRegistry registry = new DataLoaderRegistry();

            // Batch loader for books by author ID
            registry.register("booksByAuthorLoader",
                createBatchLoader(bookRepository::findAllByAuthorIdIn));

            // Batch loader for reviews by book ID
            registry.register("reviewsByBookLoader",
                createBatchLoader(reviewRepository::findByBookIds));

            return registry;
        };
    }

    private static <K, V> DataLoader<K, V> createBatchLoader(
            Function<Collection<K>, Map<K, V>> batchFunction) {
        return DataLoader.newMappedDataLoader(new MappedBatchLoader<K, V>() {
            @Override
            public CompletionStage<Map<K, V>> load(Set<K> keys) {
                return CompletableFuture.supplyAsync(() ->
                    batchFunction.apply(List.copyOf(keys)));
            }
        });
    }
}
```

### Step 5: Author Resolver (with BatchMapping)

```java
@Controller
public class AuthorResolver {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorResolver(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @QueryMapping
    public Author author(@Argument String id) {
        return authorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + id));
    }

    @QueryMapping
    public List<Author> authors() {
        return authorRepository.findAll();
    }

    // Batch loading: resolves books for multiple authors in a single query
    @BatchMapping
    public Map<Author, List<Book>> books(List<Author> authors) {
        List<String> authorIds = authors.stream().map(Author::id).toList();
        List<Book> allBooks = bookRepository.findAllByAuthorIdIn(authorIds);
        return authors.stream()
            .collect(Collectors.toMap(
                author -> author,
                author -> allBooks.stream()
                    .filter(b -> b.authorId().equals(author.id()))
                    .toList()
            ));
    }

    @SchemaMapping(typeName = "Author")
    public double averageRating(Author author) {
        List<Book> books = bookRepository.findAllByAuthorId(author.id());
        if (books.isEmpty()) return 0.0;
        return books.stream()
            .flatMap(book -> reviewRepository.findByBookId(book.id()).stream())
            .mapToInt(Review::rating)
            .average()
            .orElse(0.0);
    }
}
```

### Step 6: Book Resolver (with Manual DataLoader)

```java
@Controller
public class BookResolver {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    public BookResolver(BookRepository bookRepository, ReviewRepository reviewRepository) {
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
    }

    @QueryMapping
    public Book book(@Argument String id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
    }

    @QueryMapping
    public List<Book> books(@Argument int limit, @Argument int offset) {
        List<Book> all = bookRepository.findAll();
        return all.subList(Math.min(offset, all.size()),
            Math.min(offset + Math.max(limit, 10), all.size()));
    }

    @QueryMapping
    public List<Book> searchBooks(@Argument String query) {
        return bookRepository.search(query);
    }

    @QueryMapping
    public List<Book> topRatedBooks(@Argument double minRating, @Argument int limit) {
        return bookRepository.findAll().stream()
            .filter(book -> {
                List<Review> reviews = reviewRepository.findByBookId(book.id());
                if (reviews.isEmpty()) return false;
                double avg = reviews.stream().mapToInt(Review::rating).average().orElse(0);
                return avg >= minRating;
            })
            .limit(Math.max(limit, 10))
            .toList();
    }

    // Batch loading via DataLoader from registry
    @SchemaMapping(typeName = "Book")
    public CompletableFuture<Author> author(Book book,
                                             DataLoader<String, Author> loader) {
        return loader.load(book.authorId());
    }

    // Batch loading reviews using DataLoader
    @SchemaMapping(typeName = "Book")
    public CompletableFuture<List<Review>> reviews(Book book,
                                                     DataLoader<String, List<Review>> loader) {
        return loader.load(book.id());
    }

    // Computed fields
    @SchemaMapping(typeName = "Book")
    public double averageRating(Book book) {
        List<Review> reviews = reviewRepository.findByBookId(book.id());
        if (reviews.isEmpty()) return 0.0;
        return reviews.stream().mapToInt(Review::rating).average().orElse(0.0);
    }

    @SchemaMapping(typeName = "Book")
    public int reviewCount(Book book) {
        return reviewRepository.findByBookId(book.id()).size();
    }
}
```

### Step 7: Mutation Resolver

```java
@Controller
public class BookMutationResolver {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final ReviewRepository reviewRepository;
    private final Sinks.Many<Book> bookSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Sinks.Many<Review> reviewSink = Sinks.many().multicast().onBackpressureBuffer();

    public BookMutationResolver(BookRepository bookRepository,
                                 AuthorRepository authorRepository,
                                 ReviewRepository reviewRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.reviewRepository = reviewRepository;
    }

    @MutationMapping
    public Book addBook(@Argument BookInput input) {
        if (input.title() == null || input.title().isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (input.isbn() == null || input.isbn().isBlank()) {
            throw new IllegalArgumentException("ISBN is required");
        }
        if (authorRepository.findById(input.authorId()).isEmpty()) {
            throw new IllegalArgumentException("Author not found: " + input.authorId());
        }

        Book book = bookRepository.save(new Book(null, input.title(), input.isbn(),
            input.publishedYear(), input.authorId()));
        bookSink.tryEmitNext(book);
        return book;
    }

    @MutationMapping
    public Review addReview(@Argument ReviewInput input) {
        if (input.rating() < 1 || input.rating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        if (bookRepository.findById(input.bookId()).isEmpty()) {
            throw new IllegalArgumentException("Book not found: " + input.bookId());
        }

        Review review = reviewRepository.save(new Review(null, input.bookId(),
            input.reviewerName(), input.rating(), input.comment(), Instant.now()));
        reviewSink.tryEmitNext(review);
        return review;
    }

    @MutationMapping
    public Author updateAuthor(@Argument String id, @Argument AuthorInput input) {
        return authorRepository.findById(id).map(existing -> {
            Author updated = new Author(id,
                input.name() != null ? input.name() : existing.name(),
                input.birthDate() != null ? input.birthDate() : existing.birthDate(),
                input.biography() != null ? input.biography() : existing.biography());
            return authorRepository.save(updated);
        }).orElseThrow(() -> new ResourceNotFoundException("Author not found: " + id));
    }

    @MutationMapping
    public boolean deleteBook(@Argument String id) {
        if (bookRepository.findById(id).isEmpty()) return false;
        bookRepository.deleteById(id);
        return true;
    }

    // Sinks for subscription events
    public Sinks.Many<Book> getBookSink() { return bookSink; }
    public Sinks.Many<Review> getReviewSink() { return reviewSink; }
}
```

### Step 8: Subscription Resolver

```java
@Controller
public class ReviewSubscriptionResolver {

    private final BookMutationResolver mutationResolver;
    private final ReviewRepository reviewRepository;

    public ReviewSubscriptionResolver(BookMutationResolver mutationResolver,
                                       ReviewRepository reviewRepository) {
        this.mutationResolver = mutationResolver;
        this.reviewRepository = reviewRepository;
    }

    @SubscriptionMapping
    public Flux<Review> reviewAdded(@Argument String bookId) {
        return mutationResolver.getReviewSink().asFlux()
            .filter(review -> review.bookId().equals(bookId))
            .doOnSubscribe(s -> System.out.println("Client subscribed to reviewAdded for book " + bookId))
            .doOnCancel(() -> System.out.println("Client unsubscribed from reviewAdded for book " + bookId))
            .share();
    }

    @SubscriptionMapping
    public Flux<Book> bookAdded() {
        return mutationResolver.getBookSink().asFlux()
            .doOnSubscribe(s -> System.out.println("Client subscribed to bookAdded"))
            .share();
    }
}
```

### Step 9: DataLoader Wiring Configuration

```java
@Configuration
public class GraphQLConfig implements RuntimeWiringConfigurer {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    public GraphQLConfig(BookRepository bookRepository, ReviewRepository reviewRepository) {
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void configure(RuntimeWiring.Builder builder) {
        // Register DataLoaderRegistry per GraphQL request
    }

    @Bean
    public DataLoaderRegistryFactory dataLoaderRegistryFactory() {
        return () -> {
            DataLoaderRegistry registry = new DataLoaderRegistry();

            // Author DataLoader — batch loads authors by ID
            registry.register("authorLoader",
                DataLoader.newMappedDataLoader(new MappedBatchLoader<String, Author>() {
                    @Override
                    public CompletionStage<Map<String, Author>> load(Set<String> keys) {
                        return CompletableFuture.supplyAsync(() -> {
                            List<Author> authors = authorRepository.findAllById(keys);
                            return authors.stream()
                                .collect(Collectors.toMap(Author::id, a -> a));
                        });
                    }
                }));

            // Review DataLoader — batch loads reviews by book ID
            registry.register("reviewsLoader",
                DataLoader.newMappedDataLoader(new MappedBatchLoader<String, List<Review>>() {
                    @Override
                    public CompletionStage<Map<String, List<Review>>> load(Set<String> keys) {
                        return CompletableFuture.supplyAsync(() ->
                            reviewRepository.findByBookIds(keys));
                    }
                }));

            return registry;
        };
    }
}

@Controller
public class BookDataLoaderController {

    @SchemaMapping(typeName = "Book")
    public CompletableFuture<Author> author(Book book,
            @LocalContextValue DataLoader<String, Author> authorLoader) {
        return authorLoader.load(book.authorId());
    }

    @SchemaMapping(typeName = "Book")
    public CompletableFuture<List<Review>> reviews(Book book,
            @LocalContextValue DataLoader<String, List<Review>> reviewsLoader) {
        return reviewsLoader.load(book.id());
    }
}
```

### Step 10: Instrumentation (Depth Limiting & Complexity)

```java
@Component
public class CustomInstrumentation implements Instrumentation {

    private static final int MAX_DEPTH = 8;
    private static final int MAX_COMPLEXITY = 500;

    @Override
    public ExecutionInput instrumentExecutionInput(
            ExecutionInput executionInput,
            InstrumentationExecutionParameters parameters,
            InstrumentationState state) {
        Document document = new Parser().parse(executionInput.getQuery());
        validateDepth(document);
        validateComplexity(document);
        return executionInput;
    }

    void validateDepth(Document document) {
        for (Definition definition : document.getDefinitions()) {
            if (definition instanceof OperationDefinition op) {
                int depth = calculateDepth(op.getSelectionSet());
                if (depth > MAX_DEPTH) {
                    throw new GraphQLException(
                        "Query exceeds max depth of " + MAX_DEPTH + " (got " + depth + ")");
                }
            }
        }
    }

    int calculateDepth(SelectionSet selectionSet) {
        if (selectionSet == null) return 0;
        return selectionSet.getSelections().stream()
            .mapToInt(sel -> {
                if (sel instanceof Field field) {
                    return 1 + calculateDepth(field.getSelectionSet());
                }
                return 1;
            })
            .max()
            .orElse(0);
    }

    void validateComplexity(Document document) {
        int complexity = document.getDefinitions().stream()
            .filter(OperationDefinition.class::isInstance)
            .map(OperationDefinition.class::cast)
            .flatMap(def -> def.getSelectionSet().getSelections().stream())
            .mapToInt(sel -> calculateFieldCost(sel, 1))
            .sum();
        if (complexity > MAX_COMPLEXITY) {
            throw new GraphQLException(
                "Query complexity " + complexity + " exceeds max of " + MAX_COMPLEXITY);
        }
    }

    int calculateFieldCost(Selection selection, int depthFactor) {
        if (selection instanceof Field field) {
            boolean isList = field.getArguments().stream()
                .anyMatch(a -> "limit".equals(a.getName()) || "first".equals(a.getName()));
            int cost = depthFactor * (isList ? 10 : 1);
            if (field.getSelectionSet() != null) {
                cost += field.getSelectionSet().getSelections().stream()
                    .mapToInt(sel -> calculateFieldCost(sel, depthFactor + 1))
                    .sum();
            }
            return cost;
        }
        return depthFactor;
    }
}
```

### Step 11: Exception Handler

```java
@ControllerAdvice
public class GraphQLExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public GraphQLError handleNotFound(ResourceNotFoundException ex) {
        return GraphQLError.newError()
            .message(ex.getMessage())
            .errorType(ErrorType.NOT_FOUND)
            .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public GraphQLError handleValidation(IllegalArgumentException ex) {
        return GraphQLError.newError()
            .message(ex.getMessage())
            .errorType(ErrorType.BAD_REQUEST)
            .extensions(Map.of("code", "VALIDATION_ERROR"))
            .build();
    }
}

class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
```

### Step 12: Application Configuration

```yaml
spring:
  graphql:
    graphiql:
      enabled: true
    schema:
      locations: classpath:graphql/**
    websocket:
      path: /graphql
    cors:
      allowed-origins: "*"
    path: /graphql
```

---

## Complexity Analysis

| Scenario | Without Batching | With DataLoader Batching |
|----------|-----------------|------------------------|
| Query N authors + their books | N+1 queries (1 authors + N books) | 2 queries (1 authors + 1 books) |
| Query M books + their reviews | M+1 queries | 2 queries |
| Query N authors + M books each + K reviews each | 1 + N + N*M queries | 3 queries |
| Mutation + subscription publish | O(1) operation | O(1) + O(s) broadcast to s subscribers |
| Depth analysis | O(F) where F = fields in query | Same |
| DataLoader cache | N/A | O(N) memory per request |

---

## Follow-Up Questions

1. **How does DataLoader avoid duplicate loads?** — DataLoader caches by key within a single request execution. If the same entity is requested multiple times (e.g., same author for two books), it only calls the batch function once.

2. **How would you handle DataLoader in a federated GraphQL architecture?** — Each service has its own DataLoader. The gateway uses entity resolvers (`@key` directive) that call batch endpoints across services.

3. **What happens when DataLoader's batch function throws?** — The `CompletionStage` completes exceptionally. Each individual `load()` gets the same exception. The GraphQL response includes errors for those fields.

4. **How do you handle N+1 for deeply nested queries (e.g., authors → books → reviews → reviewer)?** — Use DataLoader at every level. Each level batches its own query. The total queries = number of distinct levels, not the product of cardinalities.

5. **How would you add Redis caching to DataLoader?** — Implement a `MappedBatchLoader` that checks Redis before the DB. Cache results with TTL. DataLoader per-request cache handles de-duplication; Redis provides cross-request caching.

---

## Test Cases

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class GraphQLApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldQueryAuthorsWithBooks() throws Exception {
        var query = """
            query {
                authors {
                    id
                    name
                    books {
                        id
                        title
                    }
                }
            }
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + query.replace("\"", "\\\"")
                    .replace("\n", "\\n") + "\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.authors").isArray())
            .andExpect(jsonPath("$.data.authors[0].books").isArray())
            .andExpect(jsonPath("$.data.authors[0].books[0].title").isString());
    }

    @Test
    void shouldQueryBookWithAuthorAndReviews() throws Exception {
        var query = """
            query { book(id: "1") {
                id, title, author { name }, reviews { rating, reviewerName }
            }}
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + escapeQuery(query) + "\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.book.title").isString());
    }

    @Test
    void shouldAddBookViaMutation() throws Exception {
        var mutation = """
            mutation { addBook(input: {
                title: "New Book", isbn: "123-456", publishedYear: 2024, authorId: "author-1"
            }) { id title isbn } }
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + escapeQuery(mutation) + "\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.addBook.title").value("New Book"));
    }

    @Test
    void shouldAddReview() throws Exception {
        var mutation = """
            mutation { addReview(input: {
                bookId: "1", reviewerName: "Alice", rating: 5, comment: "Great!"
            }) { id rating reviewerName } }
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + escapeQuery(mutation) + "\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.addReview.rating").value(5));
    }

    @Test
    void shouldRejectInvalidRating() throws Exception {
        var mutation = """
            mutation { addReview(input: {
                bookId: "1", reviewerName: "Bob", rating: 6, comment: "Invalid"
            }) { id } }
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + escapeQuery(mutation) + "\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errors[0]").exists());
    }

    @Test
    void shouldRejectDeeplyNestedQuery() throws Exception {
        var deepQuery = """
            query { authors { books { author { books { author { name } } } } } }
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + escapeQuery(deepQuery) + "\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errors[0]").exists());
    }

    @Test
    void shouldSearchBooks() throws Exception {
        var query = """
            query { searchBooks(query: "Foundation") { id title } }
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + escapeQuery(query) + "\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.searchBooks[0].title")
                .value("Foundation"));
    }

    @Test
    void shouldDeleteBook() throws Exception {
        var mutation = """
            mutation { deleteBook(id: "1") }
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + escapeQuery(mutation) + "\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.deleteBook").value(true));
    }

    private String escapeQuery(String query) {
        return query.replace("\"", "\\\"").replace("\n", "\\n").trim();
    }
}
```

---

## Summary

This GraphQL implementation demonstrates:
- **DataLoader batching**: `@BatchMapping` and custom `DataLoader` for solving N+1
- **Spring for GraphQL**: `@QueryMapping`, `@MutationMapping`, `@SubscriptionMapping`, `@SchemaMapping`
- **Batch resolvers**: batching by author ID and book ID eliminates N+1
- **Subscriptions**: real-time updates via `Sinks.Many` with filtering
- **Security**: query depth limiting and complexity analysis
- **Error handling**: `@ControllerAdvice` for consistent GraphQL errors
- **Computed fields**: `averageRating`, `reviewCount` resolved via `@SchemaMapping`