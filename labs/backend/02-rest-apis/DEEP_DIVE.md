# Deep Dive — REST Maturity Model, HATEOAS, Content Negotiation, Caching, and API Tradeoffs

## Richardson Maturity Model (RMM)

The Richardson Maturity Model defines 4 levels of REST API maturity:

### Level 0: The Swamp of POX (Plain Old XML)

- Single URI (`POST /api`)
- Single HTTP method (all `POST`)
- Everything in the XML body
- Example: SOAP XML-RPC over HTTP

```xml
<POST /api>
  <getUserRequest>
    <id>123</id>
  </getUserRequest>
</POST>
```

### Level 1: Resources

- Multiple URIs for distinct resources
- Still uses single HTTP method
```xml
POST /users/123
POST /users/123/orders
```

### Level 2: HTTP Verbs (Most "REST" APIs)

- Uses HTTP methods correctly: GET, POST, PUT, PATCH, DELETE
- Uses HTTP status codes properly
- Uses headers (Content-Type, Accept)
- This is where most production APIs operate

```
GET    /users/{id}      → 200 OK
POST   /users           → 201 Created
PUT    /users/{id}      → 200 OK / 204 No Content
DELETE /users/{id}      → 204 No Content / 404 Not Found
```

### Level 3: HATEOAS (Hypermedia as Engine of Application State)

- Each response includes links to discover next actions
- Client navigates the API through hypermedia links, not documentation
- The ultimate goal of REST

```json
{
  "id": 123,
  "name": "John Doe",
  "links": [
    { "rel": "self", "href": "/users/123" },
    { "rel": "orders", "href": "/users/123/orders" },
    { "rel": "friends", "href": "/users/123/friends" }
  ]
}
```

## HATEOAS Implementation with Spring HATEOAS

```java
@GetMapping("/users/{id}")
public EntityModel<User> getUser(@PathVariable Long id) {
    User user = userService.findById(id);

    EntityModel<User> model = EntityModel.of(user);
    model.add(linkTo(methodOn(UserController.class).getUser(id)).withSelfRel());
    model.add(linkTo(methodOn(UserController.class).getUserOrders(id)).withRel("orders"));
    model.add(linkTo(methodOn(UserController.class).getUserFriends(id)).withRel("friends"));

    return model;
}
```

### Collection Model with HATEOAS

```java
@GetMapping("/users")
public CollectionModel<EntityModel<User>> getUsers() {
    List<EntityModel<User>> users = userService.findAll().stream()
        .map(user -> {
            EntityModel<User> model = EntityModel.of(user);
            model.add(linkTo(methodOn(UserController.class).getUser(user.getId())).withSelfRel());
            return model;
        })
        .toList();

    return CollectionModel.of(users, linkTo(methodOn(UserController.class).getUsers()).withSelfRel());
}
```

### Affordances (beyond links)

Spring HATEOAS 2.x adds affordances that describe what actions are possible:

```java
EntityModel<User> model = EntityModel.of(user);
Affordance delete = Affordances.afford(model)
    .withLink(linkTo(methodOn(UserController.class).deleteUser(id)))
    .withLink(linkTo(methodOn(UserController.class).updateUser(id, null)));
model.add(delete.toLink());
```

## Content Negotiation

Content negotiation determines the media type to respond with based on client request.

### Strategy Pattern

Spring uses `ContentNegotiationManager` with configurable strategies:

```java
// ContentNegotiationManager delegates to ordered strategies:
public interface ContentNegotiationStrategy {
    List<MediaType> resolveMediaTypes(NativeWebRequest request);
}
```

### Built-in Strategies

| Strategy | Description | Example |
|----------|-------------|---------|
| `AcceptHeaderContentNegotiationStrategy` | Uses `Accept` HTTP header | `Accept: application/json` |
| `ParameterContentNegotiationStrategy` | Uses query parameter | `?format=xml` |
| `HeaderParameterContentNegotiationStrategy` | Uses a custom header | `X-Format: json` |
| `FixedContentNegotiationStrategy` | Always returns one type | Always JSON |

### Configuration

```java
@Configuration
public class ContentNegotiationConfig implements WebMvcConfigurer {
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .defaultContentType(MediaType.APPLICATION_JSON)
            .mediaType("json", MediaType.APPLICATION_JSON)
            .mediaType("xml", MediaType.APPLICATION_XML)
            .parameterName("format");        // ?format=json
    }
}
```

### Custom Strategy

```java
@Component
public class ProfileContentNegotiationStrategy implements ContentNegotiationStrategy {
    @Override
    public List<MediaType> resolveContentTypes(NativeWebRequest request) {
        String profile = request.getHeader("X-Profile");
        if ("beta".equals(profile)) {
            return List.of(MediaType.valueOf("application/vnd.myapp.v2+json"));
        }
        return List.of(MediaType.APPLICATION_JSON);
    }
}
```

## HTTP Caching

### ETag (Entity Tag)

An ETag is a hash of the response body:

```java
@GetMapping("/users/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id) {
    User user = userService.findById(id);
    String etag = generateETag(user);

    return ResponseEntity
        .ok()
        .eTag(etag)
        .body(user);
}
```

**Client-side: `If-None-Match` header:**
```
GET /users/123
If-None-Match: "abc123"
```

Server returns `304 Not Modified` if ETag matches. Key for `@RequestMapping` handlers, `ShallowEtagHandlerFilter` can automate this:

```java
@Bean
public Filter shallowEtagHeaderFilter() {
    return new ShallowEtagHeaderFilter();
}
```

### Last-Modified

Represents the last time a resource was modified.

```java
@GetMapping("/users/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id, WebRequest webRequest) {
    User user = userService.findById(id);
    long lastModified = user.getLastModifiedDate().getTime();

    if (webRequest.checkNotModified(lastModified)) {
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
    }

    return ResponseEntity
        .ok()
        .lastModified(lastModified)
        .body(user);
}
```

### Cache-Control

```java
@GetMapping("/cache-demo")
public ResponseEntity<String> cacheControl() {
    return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)
            .mustRevalidate()
            .sMaxAge(30, TimeUnit.MINUTES))
        .body("This response is cached");
}
```

## Idempotency and Safety Semantics

### HTTP Method Classification

| Method | Safe | Idempotent | Description |
|--------|------|-----------|-------------|
| GET | Yes | Yes | Retrieve data, no side effects |
| HEAD | Yes | Yes | Like GET, only headers |
| OPTIONS | Yes | Yes | What methods are available |
| TRACE | Yes | Yes | Diagnostic trace |
| PUT | No | Yes | Replace resource, same result each time |
| DELETE | No | Yes | Remove resource, same result each time |
| POST | No | No | Create or process, not guaranteed idempotent |
| PATCH | No | No (by default) | Partial update, not necessarily idempotent |

### Implementing Idempotency Keys

For POST endpoints that should be idempotent (payment processing, order creation):

```java
@PostMapping("/payments")
public ResponseEntity<Payment> createPayment(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                              @RequestBody PaymentRequest request) {
    Payment existing = paymentService.findByIdempotencyKey(idempotencyKey);
    if (existing != null) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(existing);
    }
    Payment payment = paymentService.processPayment(request, idempotencyKey);
    return ResponseEntity.status(HttpStatus.CREATED).body(payment);
}
```

### PUT vs PATCH

PUT replaces the entire resource; PATCH applies a partial update:

```java
// PUT: Replace entire user
@PutMapping("/users/{id}")
public User replaceUser(@PathVariable Long id, @RequestBody User user) {
    return userService.replace(id, user);
}

// PATCH: Apply partial update
@PatchMapping("/users/{id}")
public User patchUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
    return userService.partialUpdate(id, updates);
}
```

## REST vs GraphQL vs gRPC Tradeoffs

### Comparison Table

| Feature | REST | GraphQL | gRPC |
|---------|------|---------|------|
| Protocol | HTTP/1.1, HTTP/2 | HTTP/1.1, HTTP/2 | HTTP/2 |
| Data format | JSON, XML, YAML | JSON | Protobuf (binary) |
| Serialization | Client chooses via Accept | Server-controlled | Protobuf schema |
| Schema | OpenAPI | SDL (Schema Definition Language) | .proto files |
| Learning curve | Low | Medium | Medium |
| Caching | HTTP-native (ETag, Cache-Control) | Custom or persisted | No native caching |
| Over-fetching | Common | Eliminated | Handled |
| Under-fetching | Requires multiple calls | Eliminated | Handled |
| Tooling ecosystem | Very mature | Mature | Mature |
| Browser support | Native | Native (Fetch API) | Requires gRPC-web proxy |
| Streaming | SSE, WebSocket (separate) | Subscription (WebSocket) | Native bidirectional streaming |
| Performance | Good (with HTTP/2) | Good | Excellent (binary + HTTP/2) |
| Complexity | Low | Medium | High (proto compilation) |

### When to Choose Each

**Choose REST when:**
- Simple CRUD operations
- Caching is important
- Public API with broad client base
- Team is less experienced

**Choose GraphQL when:**
- Multiple clients need different data shapes
- Mobile app with bandwidth constraints
- Many related entities need to be fetched together
- Tools: Apollo, DGS, Relay

**Choose gRPC when:**
- Internal microservice-to-microservice communication
- High-performance streaming needed
- Polyglot services (multi-language)
- Tools: Protocol Buffers, Protobuf compiler, Grpc Spring Boot Starter

### REST API Versioning Strategies

| Strategy | Example | Path | Header | Cache |
|----------|---------|------|--------|-------|
| URI versioning | `/v1/users/123` | Simple | No | Broken |
| Request parameter | `/users/123?version=1` | Simple | Simple | Broken |
| Custom header | `X-API-Version: 1` | Clean | Complex | Works |
| Accept header | `Accept: application/vnd.myapp.v1+json` | Clean | Complex | Works |

### Best Practice: Media Type Versioning

```java
@GetMapping(value = "/users/{id}", 
            produces = "application/vnd.myapp.v1+json")
public User getUserV1(@PathVariable Long id) { ... }

@GetMapping(value = "/users/{id}", 
            produces = "application/vnd.myapp.v2+json")
public UserV2 getUserV2(@PathVariable Long id) { ... }
```

## Additional REST Patterns

### Conditional Requests with ETag

```java
@GetMapping("/users/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id,
                                     @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {
    User user = userService.findById(id);
    String currentEtag = computeETag(user);

    if (ifNoneMatch != null && ifNoneMatch.equals(currentEtag)) {
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
    }

    return ResponseEntity.ok().eTag(currentEtag).body(user);
}
```

### Idempotent POST with Idempotency-Key

```java
@PostMapping("/orders")
public ResponseEntity<Order> createOrder(@RequestHeader("Idempotency-Key") UUID idempotencyKey,
                                          @RequestBody OrderRequest request) {
    Order existing = orderService.findByIdempotencyKey(idempotencyKey);
    if (existing != null) {
        return ResponseEntity.ok(existing); // Return existing, not conflict
    }

    Order order = orderService.createOrder(request, idempotencyKey);
    return ResponseEntity.status(HttpStatus.CREATED).body(order);
}
```

This technique is critical for payment processing, where duplicate charges must be prevented.

### Safe Methods and Cache Headers

```java
@GetMapping("/status")
public ResponseEntity<Status> status() {
    // Safe method — no side effects, suitable for caching
    return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(5, TimeUnit.SECONDS)
            .mustRevalidate())
        .body(service.getStatus());
}
```