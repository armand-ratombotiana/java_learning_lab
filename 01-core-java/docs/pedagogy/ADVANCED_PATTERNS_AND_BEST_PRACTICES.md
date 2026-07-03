# Advanced Patterns & Best Practices for Production Java

<div align="center">

![Patterns](https://img.shields.io/badge/Patterns-Advanced-blue?style=for-the-badge)
![Focus](https://img.shields.io/badge/Focus-Production%20Ready-orange?style=for-the-badge)
![Level](https://img.shields.io/badge/Level-Senior-red?style=for-the-badge)

**Advanced Patterns and Best Practices for Production Java Systems**

</div>

---

## Table of Contents

1. [Concurrency Patterns](#concurrency-patterns)
2. [Resilience Patterns](#resilience-patterns)
3. [Performance Patterns](#performance-patterns)
4. [Data Access Patterns](#data-access-patterns)
5. [Architectural Patterns](#architectural-patterns)
6. [Best Practices](#best-practices)

---

## Concurrency Patterns

### Pattern 1: Thread-Safe Lazy Initialization

#### Problem
Need to initialize expensive resource lazily and thread-safely.

#### Solution
```java
// Option 1: Double-checked locking (correct implementation)
public class LazyInitializer<T> {
    private volatile T instance;
    private final Supplier<T> supplier;
    
    public LazyInitializer(Supplier<T> supplier) {
        this.supplier = supplier;
    }
    
    public T get() {
        if (instance == null) {
            synchronized(this) {
                if (instance == null) {
                    instance = supplier.get();
                }
            }
        }
        return instance;
    }
}

// Option 2: Class loader initialization (preferred)
public class LazyInitializerClassLoader<T> {
    private final Supplier<T> supplier;
    
    public LazyInitializerClassLoader(Supplier<T> supplier) {
        this.supplier = supplier;
    }
    
    public T get() {
        return Holder.instance;
    }
    
    private class Holder {
        static final Object instance = supplier.get();
    }
}

// Option 3: Java 9+ - VarHandle
public class LazyInitializerVarHandle<T> {
    private static final VarHandle INSTANCE;
    private T instance;
    
    static {
        try {
            INSTANCE = MethodHandles.lookup()
                .findVarHandle(LazyInitializerVarHandle.class, "instance", Object.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    
    public T get(Supplier<T> supplier) {
        T result = (T) INSTANCE.getAcquire(this);
        if (result == null) {
            result = supplier.get();
            if (!INSTANCE.compareAndSet(this, null, result)) {
                result = (T) INSTANCE.getAcquire(this);
            }
        }
        return result;
    }
}
```

#### Trade-offs
- **Double-checked locking:** Simple, but requires volatile
- **Class loader:** Thread-safe by design, no synchronization
- **VarHandle:** Most efficient, but more complex

#### When to Use
- Expensive resource initialization
- Singleton pattern
- Connection pools

---

### Pattern 2: Producer-Consumer with Backpressure

#### Problem
Need to handle producer-consumer with flow control.

#### Solution
```java
// Using BlockingQueue
public class ProducerConsumer<T> {
    private final BlockingQueue<T> queue;
    private final int capacity;
    
    public ProducerConsumer(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
        this.capacity = capacity;
    }
    
    public void produce(T item) throws InterruptedException {
        queue.put(item); // Blocks if queue is full
    }
    
    public T consume() throws InterruptedException {
        return queue.take(); // Blocks if queue is empty
    }
    
    public int getQueueSize() {
        return queue.size();
    }
}

// Using Flow API (Java 9+)
public class ReactiveProducerConsumer<T> {
    private final SubmissionPublisher<T> publisher;
    
    public ReactiveProducerConsumer(int maxBufferCapacity) {
        this.publisher = new SubmissionPublisher<>(
            ForkJoinPool.commonPool(),
            maxBufferCapacity
        );
    }
    
    public void produce(T item) {
        publisher.submit(item);
    }
    
    public void subscribe(Consumer<T> consumer) {
        publisher.subscribe(new Subscriber<T>() {
            private Subscription subscription;
            
            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                subscription.request(1);
            }
            
            @Override
            public void onNext(T item) {
                consumer.accept(item);
                subscription.request(1);
            }
            
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }
            
            @Override
            public void onComplete() {
                System.out.println("Complete");
            }
        });
    }
}
```

#### Trade-offs
- **BlockingQueue:** Simple, synchronous
- **Flow API:** Asynchronous, more complex

#### When to Use
- Data pipeline processing
- Rate limiting
- Load balancing

---

### Pattern 3: Actor Model

#### Problem
Need to handle concurrent message processing with isolation.

#### Solution
```java
// Simple actor implementation
public abstract class Actor {
    private final BlockingQueue<Message> mailbox;
    private final ExecutorService executor;
    
    public Actor() {
        this.mailbox = new LinkedBlockingQueue<>();
        this.executor = Executors.newSingleThreadExecutor();
    }
    
    public void send(Message message) throws InterruptedException {
        mailbox.put(message);
    }
    
    public void start() {
        executor.submit(this::processMessages);
    }
    
    private void processMessages() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = mailbox.take();
                onReceive(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    protected abstract void onReceive(Message message);
    
    public void stop() {
        executor.shutdown();
    }
}

// Example actor
public class CounterActor extends Actor {
    private int count = 0;
    
    @Override
    protected void onReceive(Message message) {
        if (message instanceof IncrementMessage) {
            count++;
            System.out.println("Count: " + count);
        }
    }
}
```

#### Trade-offs
- **Isolation:** Each actor has its own thread
- **Scalability:** Limited by thread count
- **Complexity:** More complex than direct calls

#### When to Use
- Concurrent state management
- Message-driven systems
- Microservices

---

## Resilience Patterns

### Pattern 1: Circuit Breaker

#### Problem
Need to prevent cascading failures in distributed systems.

#### Solution
```java
public class CircuitBreaker<T> {
    private enum State { CLOSED, OPEN, HALF_OPEN }
    
    private State state = State.CLOSED;
    private int failureCount = 0;
    private int successCount = 0;
    private long lastFailureTime = 0;
    private final int failureThreshold;
    private final int successThreshold;
    private final long timeoutMillis;
    private final Object lock = new Object();
    
    public CircuitBreaker(int failureThreshold, int successThreshold, long timeoutMillis) {
        this.failureThreshold = failureThreshold;
        this.successThreshold = successThreshold;
        this.timeoutMillis = timeoutMillis;
    }
    
    public T execute(Supplier<T> supplier) throws Exception {
        synchronized(lock) {
            if (state == State.OPEN) {
                if (System.currentTimeMillis() - lastFailureTime > timeoutMillis) {
                    state = State.HALF_OPEN;
                    successCount = 0;
                } else {
                    throw new CircuitBreakerOpenException("Circuit breaker is open");
                }
            }
        }
        
        try {
            T result = supplier.get();
            onSuccess();
            return result;
        } catch (Exception e) {
            onFailure();
            throw e;
        }
    }
    
    private void onSuccess() {
        synchronized(lock) {
            failureCount = 0;
            if (state == State.HALF_OPEN) {
                successCount++;
                if (successCount >= successThreshold) {
                    state = State.CLOSED;
                }
            }
        }
    }
    
    private void onFailure() {
        synchronized(lock) {
            failureCount++;
            lastFailureTime = System.currentTimeMillis();
            if (failureCount >= failureThreshold) {
                state = State.OPEN;
            }
        }
    }
}

// Usage
public class ResilientService {
    private final CircuitBreaker<String> breaker;
    
    public ResilientService() {
        this.breaker = new CircuitBreaker<>(5, 2, 60000);
    }
    
    public String callRemoteService() throws Exception {
        return breaker.execute(() -> {
            // Call remote service
            return "response";
        });
    }
}
```

#### Trade-offs
- **Availability:** Fails fast instead of hanging
- **Consistency:** May return stale data
- **Complexity:** Additional state management

#### When to Use
- Remote service calls
- Database connections
- External API calls

---

### Pattern 2: Retry with Exponential Backoff

#### Problem
Need to retry failed operations with increasing delays.

#### Solution
```java
public class RetryPolicy {
    private final int maxRetries;
    private final long initialDelayMillis;
    private final double backoffMultiplier;
    private final long maxDelayMillis;
    
    public RetryPolicy(int maxRetries, long initialDelayMillis, 
                      double backoffMultiplier, long maxDelayMillis) {
        this.maxRetries = maxRetries;
        this.initialDelayMillis = initialDelayMillis;
        this.backoffMultiplier = backoffMultiplier;
        this.maxDelayMillis = maxDelayMillis;
    }
    
    public <T> T execute(Supplier<T> supplier) throws Exception {
        long delay = initialDelayMillis;
        Exception lastException = null;
        
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                return supplier.get();
            } catch (Exception e) {
                lastException = e;
                if (attempt < maxRetries) {
                    Thread.sleep(delay);
                    delay = Math.min((long)(delay * backoffMultiplier), maxDelayMillis);
                }
            }
        }
        
        throw lastException;
    }
}

// Usage
public class ResilientClient {
    private final RetryPolicy retryPolicy;
    
    public ResilientClient() {
        this.retryPolicy = new RetryPolicy(3, 100, 2.0, 10000);
    }
    
    public String callWithRetry() throws Exception {
        return retryPolicy.execute(() -> {
            // Call that might fail
            return "response";
        });
    }
}
```

#### Trade-offs
- **Reliability:** Increases success rate
- **Latency:** Adds delay on failures
- **Idempotency:** Requires idempotent operations

#### When to Use
- Transient failures
- Network calls
- Database operations

---

### Pattern 3: Bulkhead Pattern

#### Problem
Need to isolate resources to prevent cascading failures.

#### Solution
```java
public class BulkheadExecutor {
    private final ExecutorService executor;
    private final Semaphore semaphore;
    private final int maxConcurrent;
    
    public BulkheadExecutor(int maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
        this.semaphore = new Semaphore(maxConcurrent);
        this.executor = Executors.newFixedThreadPool(maxConcurrent);
    }
    
    public <T> Future<T> submit(Callable<T> task) throws InterruptedException {
        semaphore.acquire();
        return executor.submit(() -> {
            try {
                return task.call();
            } finally {
                semaphore.release();
            }
        });
    }
    
    public void shutdown() {
        executor.shutdown();
    }
}

// Usage
public class BulkheadService {
    private final BulkheadExecutor bulkhead;
    
    public BulkheadService() {
        this.bulkhead = new BulkheadExecutor(10);
    }
    
    public Future<String> callWithBulkhead() throws InterruptedException {
        return bulkhead.submit(() -> {
            // Isolated operation
            return "response";
        });
    }
}
```

#### Trade-offs
- **Isolation:** Prevents resource exhaustion
- **Throughput:** Limits concurrent operations
- **Complexity:** Additional resource management

#### When to Use
- Thread pool management
- Resource isolation
- Preventing cascading failures

---

## Performance Patterns

### Pattern 1: Object Pool

#### Problem
Need to reuse expensive objects to reduce allocation overhead.

#### Solution
```java
public class ObjectPool<T> {
    private final Queue<T> available;
    private final Set<T> inUse;
    private final Supplier<T> factory;
    private final Consumer<T> reset;
    private final int maxSize;
    
    public ObjectPool(Supplier<T> factory, Consumer<T> reset, int maxSize) {
        this.factory = factory;
        this.reset = reset;
        this.maxSize = maxSize;
        this.available = new ConcurrentLinkedQueue<>();
        this.inUse = ConcurrentHashMap.newKeySet();
        
        // Pre-populate pool
        for (int i = 0; i < maxSize; i++) {
            available.offer(factory.get());
        }
    }
    
    public T acquire() {
        T object = available.poll();
        if (object == null) {
            object = factory.get();
        }
        inUse.add(object);
        return object;
    }
    
    public void release(T object) {
        if (inUse.remove(object)) {
            reset.accept(object);
            available.offer(object);
        }
    }
    
    public int getAvailableCount() {
        return available.size();
    }
}

// Usage
public class ConnectionPool {
    private final ObjectPool<Connection> pool;
    
    public ConnectionPool(String url, int poolSize) {
        this.pool = new ObjectPool<>(
            () -> createConnection(url),
            Connection::reset,
            poolSize
        );
    }
    
    public Connection getConnection() {
        return pool.acquire();
    }
    
    public void releaseConnection(Connection conn) {
        pool.release(conn);
    }
}
```

#### Trade-offs
- **Performance:** Reduces allocation overhead
- **Memory:** Keeps objects in memory
- **Complexity:** Additional management

#### When to Use
- Database connections
- Thread pools
- Buffer management

---

### Pattern 2: Caching Strategy

#### Problem
Need to cache frequently accessed data with expiration.

#### Solution
```java
public class CacheWithExpiration<K, V> {
    private static class CacheEntry<V> {
        final V value;
        final long expirationTime;
        volatile long lastAccessTime;
        
        CacheEntry(V value, long ttlMillis) {
            this.value = value;
            this.expirationTime = System.currentTimeMillis() + ttlMillis;
            this.lastAccessTime = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
    
    private final ConcurrentHashMap<K, CacheEntry<V>> cache;
    private final long ttlMillis;
    private final ScheduledExecutorService executor;
    
    public CacheWithExpiration(long ttlMillis, long cleanupIntervalMillis) {
        this.cache = new ConcurrentHashMap<>();
        this.ttlMillis = ttlMillis;
        this.executor = Executors.newScheduledThreadPool(1);
        
        // Schedule cleanup
        executor.scheduleAtFixedRate(
            this::cleanup,
            cleanupIntervalMillis,
            cleanupIntervalMillis,
            TimeUnit.MILLISECONDS
        );
    }
    
    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, ttlMillis));
    }
    
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) return null;
        
        if (entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        
        entry.lastAccessTime = System.currentTimeMillis();
        return entry.value;
    }
    
    private void cleanup() {
        cache.entrySet().removeIf(e -> e.getValue().isExpired());
    }
    
    public void shutdown() {
        executor.shutdown();
    }
}
```

#### Trade-offs
- **Performance:** Reduces computation
- **Consistency:** May return stale data
- **Memory:** Keeps data in memory

#### When to Use
- Expensive computations
- Database queries
- API responses

---

## Data Access Patterns

### Pattern 1: Repository Pattern

#### Problem
Need to abstract data access logic from business logic.

#### Solution
```java
public interface Repository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void delete(T entity);
}

public class UserRepository implements Repository<User, Long> {
    private final DataSource dataSource;
    
    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public User save(User user) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO users (name, email) VALUES (?, ?)")) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
    
    @Override
    public Optional<User> findById(Long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM users WHERE id = ?")) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
        return Optional.empty();
    }
    
    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("email")
        );
    }
}
```

#### Trade-offs
- **Abstraction:** Decouples business logic from data access
- **Complexity:** Additional layer
- **Flexibility:** Easy to switch implementations

#### When to Use
- Data access abstraction
- Testing with mocks
- Multiple data sources

---

### Pattern 2: Query Object Pattern

#### Problem
Need to build complex queries dynamically.

#### Solution
```java
public class QueryBuilder {
    private final StringBuilder query;
    private final List<Object> parameters;
    
    public QueryBuilder(String baseQuery) {
        this.query = new StringBuilder(baseQuery);
        this.parameters = new ArrayList<>();
    }
    
    public QueryBuilder where(String condition, Object... params) {
        query.append(" WHERE ").append(condition);
        parameters.addAll(Arrays.asList(params));
        return this;
    }
    
    public QueryBuilder and(String condition, Object... params) {
        query.append(" AND ").append(condition);
        parameters.addAll(Arrays.asList(params));
        return this;
    }
    
    public QueryBuilder orderBy(String column) {
        query.append(" ORDER BY ").append(column);
        return this;
    }
    
    public QueryBuilder limit(int limit) {
        query.append(" LIMIT ").append(limit);
        return this;
    }
    
    public String build() {
        return query.toString();
    }
    
    public List<Object> getParameters() {
        return parameters;
    }
}

// Usage
public class UserQueryBuilder {
    public static List<User> findActiveUsers(DataSource dataSource, String nameFilter) {
        QueryBuilder qb = new QueryBuilder("SELECT * FROM users")
            .where("status = ?", "ACTIVE")
            .and("name LIKE ?", "%" + nameFilter + "%")
            .orderBy("created_at DESC")
            .limit(100);
        
        // Execute query with qb.build() and qb.getParameters()
        return new ArrayList<>();
    }
}
```

#### Trade-offs
- **Flexibility:** Easy to build complex queries
- **Type Safety:** Less type-safe than ORM
- **Performance:** May generate inefficient queries

#### When to Use
- Dynamic query building
- Complex filtering
- Reporting queries

---

## Architectural Patterns

### Pattern 1: Layered Architecture

#### Problem
Need to organize code into logical layers.

#### Solution
```
┌─────────────────────────────────────┐
│     Presentation Layer              │
│  (Controllers, REST endpoints)      │
└─────────────────────────────────────┘
           ↓
┌─────────────────────────────────────┐
│     Application Layer               │
│  (Services, business logic)         │
└─────────────────────────────────────┘
           ↓
┌─────────────────────────────────────┐
│     Domain Layer                    │
│  (Entities, value objects)          │
└─────────────────────────────────────┘
           ↓
┌─────────────────────────────────────┐
│     Persistence Layer               │
│  (Repositories, data access)        │
└─────────────────────────────────────┘
```

#### Implementation
```java
// Presentation Layer
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return userService.findById(id)
            .map(user -> ResponseEntity.ok(toDTO(user)))
            .orElse(ResponseEntity.notFound().build());
    }
}

// Application Layer
@Service
public class UserService {
    private final UserRepository repository;
    
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }
}

// Domain Layer
public class User {
    private Long id;
    private String name;
    private String email;
}

// Persistence Layer
@Repository
public class UserRepository {
    // Data access logic
}
```

#### Trade-offs
- **Organization:** Clear separation of concerns
- **Complexity:** More layers to manage
- **Performance:** Additional method calls

#### When to Use
- Medium to large applications
- Team development
- Long-term maintenance

---

### Pattern 2: Microservices Architecture

#### Problem
Need to scale and deploy services independently.

#### Solution
```
┌──────────────────┐
│  User Service    │
│  - User mgmt     │
│  - Auth          │
└──────────────────┘
         ↓
┌──────────────────┐
│  Order Service   │
│  - Order mgmt    │
│  - Fulfillment   │
└──────────────────┘
         ↓
┌──────────────────┐
│  Payment Service │
│  - Payments      │
│  - Billing       │
└──────────────────┘
```

#### Implementation
```java
// User Service
@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
}

// Order Service (calls User Service)
@Service
public class OrderService {
    private final RestTemplate restTemplate;
    
    public Order createOrder(Long userId, OrderRequest request) {
        // Call User Service to validate user
        User user = restTemplate.getForObject(
            "http://user-service/api/users/" + userId,
            User.class
        );
        
        // Create order
        return orderRepository.save(new Order(user, request));
    }
}
```

#### Trade-offs
- **Scalability:** Scale services independently
- **Complexity:** Distributed system challenges
- **Latency:** Network calls between services

#### When to Use
- Large applications
- Independent scaling needs
- Multiple teams

---

## Best Practices

### 1. Error Handling

```java
// ✅ Good: Specific exceptions
public class UserService {
    public User findById(Long id) throws UserNotFoundException {
        return repository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
    }
}

// ❌ Bad: Generic exceptions
public class UserService {
    public User findById(Long id) throws Exception {
        return repository.findById(id).orElse(null);
    }
}
```

### 2. Logging

```java
// ✅ Good: Structured logging
logger.info("User created", 
    Map.of("userId", user.getId(), "email", user.getEmail()));

// ❌ Bad: String concatenation
logger.info("User created: " + user.getId() + " " + user.getEmail());
```

### 3. Testing

```java
// ✅ Good: Comprehensive testing
@Test
public void testUserCreation() {
    User user = userService.create(new UserRequest("John", "john@example.com"));
    assertNotNull(user.getId());
    assertEquals("John", user.getName());
}

// ❌ Bad: No testing
// No tests written
```

### 4. Documentation

```java
// ✅ Good: Clear documentation
/**
 * Creates a new user with the given request.
 * 
 * @param request the user creation request
 * @return the created user
 * @throws UserAlreadyExistsException if user with email already exists
 */
public User create(UserRequest request) {
    // Implementation
}

// ❌ Bad: No documentation
public User create(UserRequest request) {
    // Implementation
}
```

---

<div align="center">

## Advanced Patterns & Best Practices

**Production-Ready Java Development**

**Concurrency | Resilience | Performance | Architecture**

---

**Key Patterns Covered:**
- Concurrency Patterns
- Resilience Patterns
- Performance Patterns
- Data Access Patterns
- Architectural Patterns

---

**Ready for Production Systems!**

⭐ **Build Robust, Scalable Applications**

</div>

(ending readme)