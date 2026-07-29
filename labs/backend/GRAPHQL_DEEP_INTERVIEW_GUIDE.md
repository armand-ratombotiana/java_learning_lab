# GraphQL — Deep Interview Guide

## Table of Contents
1. [GraphQL Fundamentals](#graphql-fundamentals)
2. [Schema Definition & Types](#schema-definition--types)
3. [Resolvers & DataLoader](#resolvers--dataloader)
4. [N+1 Problem & Batching](#n1-problem--batching)
5. [Subscriptions, Mutations, Queries](#subscriptions-mutations-queries)
6. [Security: Depth Limiting & Cost Analysis](#security-depth-limiting--cost-analysis)
7. [GraphQL vs REST & Federation](#graphql-vs-rest--federation)
8. [Java Code Examples](#java-code-examples)
9. [15+ Interview Questions](#15-interview-questions)

---

## GraphQL Fundamentals

GraphQL is a query language for APIs developed by Meta in 2012 and open-sourced in 2015. It allows clients to request exactly the data they need.

### Core Principles

| Principle | Description |
|-----------|-------------|
| **Hierarchical** | Queries mirror response shape |
| **Product-centric** | Client describes what it needs |
| **Strongly typed** | Every field has a defined type (scalar, enum, object, interface, union) |
| **Introspective** | Schema is queryable at runtime |
| **Single endpoint** | All operations go through `POST /graphql` |

### Request Flow

```
Client Query (POST /graphql)
    │
    ├── Parser (parse → AST)
    ├── Validation (validate AST against schema)
    ├── Execution (resolve fields top-down)
    │   ├── resolve Query.rootField()
    │   ├── resolve each nested field
    │   └── DataLoader batches DB calls
    └── Response JSON (data + errors)
```

---

## Schema Definition & Types

### Schema Definition Language (SDL)

```graphql
# Type definition
type User {
  id: ID!
  name: String!
  email: String!
  age: Int
  address: Address
  orders: [Order!]!
  createdAt: DateTime!
}

type Address {
  street: String!
  city: String!
  zipCode: String!
}

type Order {
  id: ID!
  total: Float!
  status: OrderStatus!
  items: [OrderItem!]!
}

enum OrderStatus {
  PENDING
  CONFIRMED
  SHIPPED
  DELIVERED
  CANCELLED
}

# Input type for mutations
input CreateUserInput {
  name: String!
  email: String!
  age: Int
}

input OrderFilter {
  status: OrderStatus
  minTotal: Float
  maxTotal: Float
}

# Queries
type Query {
  user(id: ID!): User
  users(page: Int, limit: Int): [User!]!
  searchUsers(query: String!): [User!]!
  orders(filter: OrderFilter): [Order!]!
}

# Mutations
type Mutation {
  createUser(input: CreateUserInput!): User!
  updateUser(id: ID!, input: CreateUserInput!): User!
  deleteUser(id: ID!): Boolean!
  placeOrder(userId: ID!, items: [OrderItemInput!]!): Order!
}

# Subscriptions
type Subscription {
  orderUpdated(userId: ID!): Order!
  newNotification: Notification!
}
```

### Scalar Types

| Scalar | Description | Java Mapping |
|--------|-------------|--------------|
| `Int` | 32-bit integer | `Integer`, `int` |
| `Float` | Double-precision | `Double`, `float` |
| `String` | UTF-8 string | `String` |
| `Boolean` | true/false | `Boolean`, `boolean` |
| `ID` | Unique identifier (serialized as String) | `String`, `Long`, `UUID` |
| `DateTime` | Custom scalar | `java.time.Instant`, `LocalDateTime` |
| `BigDecimal` | Custom scalar | `java.math.BigDecimal` |
| `JSON` | Custom scalar | `Map<String, Object>` |

### Custom Scalar Implementation

```java
@Configuration
public class GraphQLScalarConfig {

    @Bean
    public GraphQLScalarType dateTimeScalar() {
        return GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("ISO 8601 date-time string")
            .coercing(new Coercing<Instant, String>() {
                @Override
                public String serialize(Object data) {
                    if (data instanceof Instant instant) {
                        return instant.toString();
                    }
                    throw new CoercingSerializeException("Expected Instant");
                }

                @Override
                public Instant parseValue(Object input) {
                    if (input instanceof String s) {
                        return Instant.parse(s);
                    }
                    throw new CoercingParseValueException("Expected String");
                }

                @Override
                public Instant parseLiteral(Object input) {
                    if (input instanceof StringValue sv) {
                        return Instant.parse(sv.getValue());
                    }
                    throw new CoercingParseLiteralException("Expected StringValue");
                }
            })
            .build();
    }
}
```

---

## Resolvers & DataLoader

### Field Resolver with Spring for GraphQL

```java
@Controller
public class UserController {

    private final UserRepository userRepository;
    private final OrderService orderService;

    public UserController(UserRepository userRepository, OrderService orderService) {
        this.userRepository = userRepository;
        this.orderService = orderService;
    }

    @QueryMapping
    public User user(@Argument String id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    @QueryMapping
    public List<User> users(@Argument int page, @Argument int limit) {
        return userRepository.findAll(PageRequest.of(page, limit)).getContent();
    }

    @MutationMapping
    public User createUser(@Argument CreateUserInput input) {
        User user = new User();
        user.setName(input.name());
        user.setEmail(input.email());
        user.setAge(input.age());
        return userRepository.save(user);
    }
}
```

### BatchMapping (Spring for GraphQL DataLoader)

```java
@Controller
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @QueryMapping
    public List<Order> orders(@Argument OrderFilter filter) {
        if (filter != null && filter.status() != null) {
            return orderRepository.findByStatus(filter.status());
        }
        return orderRepository.findAll();
    }

    @BatchMapping
    public Map<User, List<Order>> orders(List<User> users) {
        List<UUID> userIds = users.stream().map(User::getId).toList();
        List<Order> orders = orderRepository.findAllByUserIdIn(userIds);
        return users.stream()
            .collect(Collectors.toMap(
                user -> user,
                user -> orders.stream()
                    .filter(order -> order.getUserId().equals(user.getId()))
                    .toList()
            ));
    }
}
```

### Manual DataLoader

```java
@Component
public class OrderDataLoader implements DataLoader<UUID, List<Order>> {

    private final OrderRepository orderRepository;

    public OrderDataLoader(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public String getLoaderName() {
        return "orderLoader";
    }

    @Override
    public CompletionStage<List<Order>> load(UUID userId) {
        return CompletableFuture.supplyAsync(() ->
            orderRepository.findAllByUserId(userId));
    }

    public static <K> DataLoader<K, List<Order>> create(
            OrderRepository orderRepository) {
        return DataLoader.newMappedDataLoader(new MappedBatchLoader<>() {
            @Override
            public CompletionStage<Map<K, List<Order>>> load(Set<K> keys) {
                return CompletableFuture.supplyAsync(() -> {
                    @SuppressWarnings("unchecked")
                    List<UUID> userIds = keys.stream()
                        .map(k -> (UUID) k).toList();
                    List<Order> orders = orderRepository.findAllByUserIdIn(userIds);
                    return keys.stream()
                        .collect(Collectors.toMap(
                            k -> k,
                            k -> orders.stream()
                                .filter(o -> o.getUserId().equals(k))
                                .toList()
                        ));
                });
            }
        }, DataLoaderOptions.newOptions().setBatchingEnabled(true));
    }
}
```

---

## N+1 Problem & Batching

### The Problem

```graphql
query {
  users {
    name
    orders {      # N queries for N users!
      total
    }
  }
}
```

Without batching: 1 query for users + N queries for orders = N+1 queries.

### Solution: DataLoader Batching

Spring for GraphQL's `@BatchMapping` automatically batches:

```java
// Without batching (BAD) — N+1 queries
@SchemaMapping(typeName = "User")
public List<Order> orders(User user) {
    return orderRepository.findByUserId(user.getId()); // 1 query per user
}

// With batching (GOOD)
@BatchMapping
public Map<User, List<Order>> orders(List<User> users) {
    // Single query: SELECT * FROM orders WHERE user_id IN (:userIds)
    List<Order> orders = orderRepository.findAllByUserIdIn(
        users.stream().map(User::getId).toList()
    );
    return users.stream()
        .collect(Collectors.toMap(u -> u,
            u -> orders.stream()
                .filter(o -> o.getUserId().equals(u.getId()))
                .toList()
        ));
}
```

### DataLoader Caching

DataLoader caches results within a single request:

```java
@Component
public class GraphQLConfig implements RuntimeWiringConfigurer {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public GraphQLConfig(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void configure(RuntimeWiring.Builder builder) {
        // Register DataLoaderRegistry per request
    }

    @Bean
    public DataLoaderRegistryFactory dataLoaderRegistryFactory() {
        return () -> {
            DataLoaderRegistry registry = new DataLoaderRegistry();
            registry.register("userLoader", createUserLoader());
            registry.register("orderLoader", createOrderLoader());
            return registry;
        };
    }

    private DataLoader<UUID, User> createUserLoader() {
        return DataLoader.newMappedDataLoader(new MappedBatchLoader<UUID, User>() {
            @Override
            public CompletionStage<Map<UUID, User>> load(Set<UUID> keys) {
                return CompletableFuture.supplyAsync(() -> {
                    List<User> users = userRepository.findAllById(keys);
                    return users.stream()
                        .collect(Collectors.toMap(User::getId, u -> u));
                });
            }
        });
    }

    private DataLoader<UUID, List<Order>> createOrderLoader() {
        return DataLoader.newMappedDataLoader(new MappedBatchLoader<UUID, List<Order>>() {
            @Override
            public CompletionStage<Map<UUID, List<Order>>> load(Set<UUID> keys) {
                return CompletableFuture.supplyAsync(() -> {
                    List<UUID> userIds = List.copyOf(keys);
                    List<Order> orders = orderRepository.findAllByUserIdIn(userIds);
                    return keys.stream()
                        .collect(Collectors.toMap(
                            k -> k,
                            k -> orders.stream()
                                .filter(o -> o.getUserId().equals(k))
                                .toList()
                        ));
                });
            }
        });
    }
}
```

### Batching with Virtual Threads (Java 21)

```java
@BatchMapping
public Map<User, List<Order>> orders(List<User> users, DataLoader<UUID, List<Order>> loader) {
    // Using virtual threads for parallel loading
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
        List<CompletableFuture<List<Order>>> futures = users.stream()
            .map(user -> CompletableFuture.supplyAsync(
                () -> orderRepository.findAllByUserId(user.getId()), executor))
            .toList();

        var allOrders = futures.stream()
            .map(CompletableFuture::join)
            .toList();

        return IntStream.range(0, users.size())
            .boxed()
            .collect(Collectors.toMap(users::get, allOrders::get));
    }
}
```

---

## Subscriptions, Mutations, Queries

### Queries

Fetch data (read-only, GET-like but via POST):

```java
@Controller
public class ProductController {

    @QueryMapping
    public Product product(@Argument String id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @QueryMapping
    public List<Product> searchProducts(@Argument String query,
                                         @Argument int limit) {
        return productRepository.searchByName(query, PageRequest.of(0, limit));
    }
}
```

### Mutations

Create, update, delete — executed sequentially (in order of appearance in request):

```java
@Controller
public class ProductMutationController {

    @MutationMapping
    public Product createProduct(@Argument CreateProductInput input) {
        Product product = new Product();
        product.setName(input.name());
        product.setPrice(input.price());
        product.setCategory(input.category());
        return productRepository.save(product);
    }

    @MutationMapping
    public Product updateProduct(@Argument String id, @Argument UpdateProductInput input) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (input.name() != null) product.setName(input.name());
        if (input.price() != null) product.setPrice(input.price());
        return productRepository.save(product);
    }

    @MutationMapping
    public boolean deleteProduct(@Argument String id) {
        productRepository.deleteById(id);
        return true;
    }
}
```

### Subscriptions

Real-time updates via WebSocket or SSE:

```graphql
subscription {
  orderUpdated(userId: "123") {
    id
    status
    total
  }
}
```

```java
@Controller
public class OrderSubscriptionController {

    private final Sinks.Many<Order> orderSink = Sinks.many()
        .multicast()
        .onBackpressureBuffer();

    @SubscriptionMapping
    public Flux<Order> orderUpdated(@Argument String userId) {
        return orderSink.asFlux()
            .filter(order -> order.getUserId().equals(userId));
    }

    // Called when order status changes
    public void publishOrderUpdate(Order order) {
        orderSink.tryEmitNext(order);
    }
}
```

### Using RSocket for Subscriptions

```java
@Controller
public class RsocketSubscriptionController {

    private final OrderRepository orderRepository;

    @SubscriptionMapping
    public Flux<Order> orderStatusStream(@Argument String status) {
        return Flux.interval(Duration.ofSeconds(1))
            .flatMap(tick ->
                Flux.fromIterable(orderRepository.findByStatus(status)))
            .distinct(Order::getId);
    }
}
```

---

## Security: Depth Limiting & Cost Analysis

### Query Depth Limiting

Prevents deeply nested queries that could overload the server:

```java
@Bean
public Instrumentation depthLimitingInstrumentation() {
    return new MaxQueryDepthInstrumentation(10); // Max depth of 10
}
```

### Query Complexity/Cost Analysis

Assign cost to each field and reject queries above a threshold:

```java
@Component
public class CostAnalysisInstrumentation implements Instrumentation {

    private static final int MAX_COST = 1000;
    private static final int DEFAULT_FIELD_COST = 1;
    private static final int LIST_MULTIPLIER = 10;
    private static final Logger log = LoggerFactory.getLogger(CostAnalysisInstrumentation.class);

    @Override
    public InstrumentationState createState() {
        return new InstrumentationState() {};
    }

    @Override
    public ExecutionInput instrumentExecutionInput(
            ExecutionInput executionInput, InstrumentationExecutionParameters parameters,
            InstrumentationState state) {
        Document document = new Parser().parse(executionInput.getQuery());
        int cost = calculateCost(document);
        log.debug("Query cost: {}", cost);
        if (cost > MAX_COST) {
            throw new GraphQLException("Query too expensive: cost " + cost + " > " + MAX_COST);
        }
        return executionInput;
    }

    private int calculateCost(Document document) {
        return document.getDefinitions().stream()
            .filter(OperationDefinition.class::isInstance)
            .map(OperationDefinition.class::cast)
            .flatMap(def -> def.getSelectionSet().getSelections().stream())
            .mapToInt(this::calculateSelectionCost)
            .sum();
    }

    private int calculateSelectionCost(Selection selection) {
        if (selection instanceof Field field) {
            int cost = DEFAULT_FIELD_COST;
            if (hasListType(field)) {
                cost *= LIST_MULTIPLIER;
            }
            if (field.getSelectionSet() != null) {
                cost += field.getSelectionSet().getSelections().stream()
                    .mapToInt(this::calculateSelectionCost)
                    .sum();
            }
            return cost;
        }
        return 0;
    }

    private boolean hasListType(Field field) {
        return field.getArguments().stream()
            .anyMatch(arg -> "first".equals(arg.getName()) || "limit".equals(arg.getName()));
    }
}
```

### Rate Limiting Persisted Queries

```java
@Component
public class PersistedQueryRegistry {

    private final Map<String, String> persistedQueries = new ConcurrentHashMap<>();

    public PersistedQueryRegistry() {
        persistedQueries.put("homepage",
            "query { currentUser { name recentOrders { id total } } }");
        persistedQueries.put("productDetail",
            "query productDetail($id: ID!) { product(id: $id) { name price description } }");
        persistedQueries.put("checkout",
            "mutation checkout($input: CheckoutInput!) { createOrder(input: $input) { id status } }");
    }

    public Optional<String> getQuery(String hash) {
        return Optional.ofNullable(persistedQueries.get(hash));
    }
}
```

### Authentication in Resolvers

```java
@Controller
public class SecureUserController {

    @QueryMapping
    public User me(@AuthenticationPrincipal JwtPrincipal principal) {
        return userRepository.findById(principal.getSubject())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @QueryMapping
    public List<Order> myOrders(@AuthenticationPrincipal JwtPrincipal principal) {
        return orderRepository.findAllByUserId(UUID.fromString(principal.getSubject()));
    }

    @MutationMapping
    public Order placeOrder(@AuthenticationPrincipal JwtPrincipal principal,
                             @Argument List<OrderItemInput> items) {
        return orderService.placeOrder(UUID.fromString(principal.getSubject()), items);
    }
}
```

---

## GraphQL vs REST & Federation

### GraphQL vs REST

| Aspect | GraphQL | REST |
|--------|---------|------|
| **Endpoint** | Single (`/graphql`) | Multiple (`/users`, `/orders`) |
| **Data fetching** | Client specifies shape | Server defines response |
| **Over/under-fetching** | Eliminated | Common |
| **Versioning** | Evolve schema (deprecate fields) | New endpoints or version headers |
| **Caching** | Complex (per-field) | Simple (URL-based) |
| **File upload** | Non-standard | Built-in multipart |
| **Tooling** | GraphiQL, Apollo Studio | Swagger, Postman |
| **Learning curve** | Steeper | Simpler |
| **Performance** | Risk of expensive queries | Predictable |
| **Type safety** | Native | Via OpenAPI |

### Federation (Apollo Federation)

Distributed GraphQL across multiple services:

```
Gateway
 ├── Product Service (extends Product type)
 ├── Review Service (extends Product type with reviews)
 ├── User Service (defines User type)
 └── Order Service (extends User type with orders)
```

#### Federated Schema Example

```graphql
# Product Service
type Product @key(fields: "id") {
  id: ID!
  name: String!
  price: Float!
}

# Review Service
type Review @key(fields: "id") {
  id: ID!
  product: Product!
  rating: Int!
  text: String!
}

extend type Product @key(fields: "id") {
  id: ID! @external
  reviews: [Review!]!
}
```

```java
// Product service — entity resolver
@Controller
public class ProductReferenceResolver {

    @BatchMapping
    public Map<Product, List<Review>> reviews(List<Product> products) {
        List<Long> productIds = products.stream()
            .map(Product::getId).toList();
        List<Review> reviews = reviewRepository.findAllByProductIdIn(productIds);
        return products.stream()
            .collect(Collectors.toMap(
                p -> p,
                p -> reviews.stream()
                    .filter(r -> r.getProductId().equals(p.getId()))
                    .toList()
            ));
    }
}
```

### Schema Stitching (Legacy vs Federation)

| Approach | Pros | Cons |
|----------|------|------|
| **Schema Stitching** | Simpler setup | Duplicate types, manual merging |
| **Apollo Federation** | Built-in entity resolution, @key/@external | Requires Apollo server |
| **DGS Federation** | Spring-native federation support | DGS-specific |

---

## Java Code Examples

### 1. Complete Spring for GraphQL Application

```java
@SpringBootApplication
public class GraphQLApplication {
    public static void main(String[] args) {
        SpringApplication.run(GraphQLApplication.class, args);
    }
}
```

```yaml
spring:
  graphql:
    graphiql:
      enabled: true
    schema:
      locations: classpath:graphql/**
    websocket:
      path: /graphql
```

### 2. Schema File (`src/main/resources/graphql/schema.graphqls`)

```graphql
type Query {
  book(id: ID!): Book
  books(limit: Int): [Book!]!
  author(id: ID!): Author
  authors: [Author!]!
}

type Mutation {
  addBook(input: BookInput!): Book!
  addAuthor(input: AuthorInput!): Author!
}

type Subscription {
  bookAdded: Book!
}

type Book {
  id: ID!
  title: String!
  isbn: String!
  author: Author!
  publishedYear: Int!
}

type Author {
  id: ID!
  name: String!
  books: [Book!]!
}

input BookInput {
  title: String!
  isbn: String!
  authorId: ID!
  publishedYear: Int!
}

input AuthorInput {
  name: String!
}
```

### 3. DataLoader for Author (Batch Resolver)

```java
@Controller
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @QueryMapping
    public Book book(@Argument String id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
    }

    @QueryMapping
    public List<Book> books(@Argument int limit) {
        return bookRepository.findAll(PageRequest.of(0, Math.min(limit, 100))).getContent();
    }

    @BatchMapping
    public Map<Book, Author> author(List<Book> books) {
        List<String> authorIds = books.stream()
            .map(Book::getAuthorId)
            .distinct()
            .toList();
        List<Author> authors = authorRepository.findAllById(authorIds);
        Map<String, Author> authorMap = authors.stream()
            .collect(Collectors.toMap(Author::getId, a -> a));
        return books.stream()
            .collect(Collectors.toMap(b -> b, b -> authorMap.get(b.getAuthorId())));
    }
}
```

### 4. Subscription with RSocket

```java
@Controller
public class LiveBookController {

    private final Sinks.Many<Book> bookSink = Sinks.many()
        .multicast()
        .onBackpressureBuffer(1024, false);

    @SubscriptionMapping
    public Flux<Book> bookAdded() {
        return bookSink.asFlux()
            .doOnSubscribe(s -> log.info("Client subscribed to bookAdded"))
            .doOnCancel(() -> log.info("Client unsubscribed from bookAdded"))
            .share();
    }

    public void notifyBookAdded(Book book) {
        bookSink.tryEmitNext(book).orThrow();
    }
}
```

### 5. Custom Exception Handler

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

    @ExceptionHandler(ValidationException.class)
    public GraphQLError handleValidation(ValidationException ex) {
        return GraphQLError.newError()
            .message(ex.getMessage())
            .errorType(ErrorType.BAD_REQUEST)
            .locations(List.of())
            .extensions(Map.of(
                "code", "VALIDATION_ERROR",
                "field", ex.getField()
            ))
            .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public GraphQLError handleAccessDenied(AccessDeniedException ex) {
        return GraphQLError.newError()
            .message("Access denied")
            .errorType(ErrorType.FORBIDDEN)
            .build();
    }
}
```

### 6. Context Propagation (Request Scope)

```java
@Component
public class GraphQLContextFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
            .contextWrite(ctx -> {
                String traceId = exchange.getRequest().getHeaders()
                    .getFirst("X-Trace-Id");
                if (traceId == null) traceId = UUID.randomUUID().toString();
                return ctx.put("traceId", traceId);
            });
    }
}

@Controller
public class ContextAwareController {

    @QueryMapping
    public String traceId() {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(ctx -> Mono.deferContextual(Mono::just))
            .map(ctx -> ctx.getOrDefault("traceId", "unknown"))
            .block();
    }
}
```

---

## 15+ Interview Questions

### Basic

1. **What is GraphQL and how does it differ from REST?** — GraphQL is a query language where clients specify exact data needs. Single endpoint, no over/under-fetching, strongly typed schema, client-driven data shape.

2. **Explain the GraphQL type system.** — Scalar types (Int, Float, String, Boolean, ID), object types, input types, enum types, interface, union. Types define the shape of data available in the API.

3. **What is a resolver in GraphQL?** — A function that fetches data for a field. In Spring for GraphQL: `@QueryMapping`, `@MutationMapping`, `@SchemaMapping`, `@BatchMapping`.

### Intermediate

4. **What is the N+1 problem in GraphQL and how do you solve it?** — N+1 queries happen when resolving a list field triggers one DB query per parent item. Solved via DataLoader batching: `@BatchMapping` batches parent keys into a single query: `SELECT * WHERE id IN (:keys)`.

5. **How does DataLoader work?** — Collects individual loads within a single request tick, batches them into one call. Caches results per request. Prevents duplicate loads. Ticker (event-loop or scheduled) triggers batch execution.

6. **Explain GraphQL subscriptions.** — Real-time updates via WebSocket or SSE. Client subscribes, server pushes data when events occur. Spring for GraphQL supports `@SubscriptionMapping` returning `Flux<T>`.

7. **What is Apollo Federation?** — Distributed GraphQL across services. Each service defines part of the schema. Gateway composes them. `@key` defines entity identity, `@external` marks fields from other services, `@extends` adds fields to remote types.

8. **How do you handle authentication in GraphQL?** — Extract JWT from Authorization header in a WebFilter, set authentication in reactive context. Resolvers use `@AuthenticationPrincipal` to get user identity.

### Advanced

9. **Design a query cost analysis system.** — Assign cost weight to fields. Lists cost more (multiplier for `first`/`limit`). Sum all field costs in the query. Reject if exceeds threshold. Instrumentation hooks validate cost before execution.

10. **How do you implement rate limiting for GraphQL?** — Use persisted queries (allow-listed hashes). Rate limit per user/IP at the HTTP layer. For complex queries, cost analysis can also serve as rate limiting.

11. **Explain schema stitching vs federation.** — Stitching: manual merging of schemas via `mergeSchemasAsync`. Federation: automatic composition via `@key` directives, entity resolution, Apollo-specific. Federation is more scalable.

12. **How do you prevent deeply nested queries?** — MaxQueryDepthInstrumentation limits depth. Set max depth (10-15). Combine with cost analysis for comprehensive protection.

13. **Design a GraphQL gateway for a microservices architecture.** — Single gateway endpoint. Each microservice exposes its own GraphQL schema. Federation stitches them. DataLoaders batch cross-service calls. Gateway handles auth, rate limiting, depth limiting.

14. **How do you handle file uploads in GraphQL?** — GraphQL has no native file upload. Use multipart request spec: `operations` field for query, `map` field to map parts to variables. Spring for GraphQL supports `@RequestPart`.

15. **Explain the execution flow of a GraphQL query.** — Parse → Validate → Execute: resolve each field top-down (breadth-first). For each field, call resolver → DataLoader batches → return value → resolve nested fields → serialize response.

16. **How do you implement caching in GraphQL?** — Per-query: HTTP caching headers (ETag). Per-field: DataLoader caches within request. Persistent: Redis-backed resolver caches. CDN: Persisted queries allow CDN caching of GET requests.

17. **What are GraphQL directives and when would you use them?** — `@deprecated`, `@skip`, `@include` (built-in). Custom: `@auth(role: "ADMIN")`, `@cacheControl(maxAge: 60)`, `@format(locale: "en-US")`. Directives transform schema or execution behavior.

18. **How do you version a GraphQL API?** — No explicit versioning. Add new fields with `@deprecated` on old ones. Use `@deprecated(reason: "Use newField instead")`. Clients migrate gradually. Breaking changes require careful planning.