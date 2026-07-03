# Architectural Patterns with Pattern Matching

## Algebraic Effect Handler Architecture

Pattern matching enables clean separation of operations and their implementations:

```java
// Core algebra (operations)
sealed interface Expression 
    permits Value, ReadLine, WriteLine, Sequence
    record Value<T>(T v) implements Expression {}
    record ReadLine() implements Expression {}
    record WriteLine(String line) implements Expression {}
    record Sequence(Expression first, Expression second) implements Expression {}
    
// Interpreter (handles all operations exhaustively)
interface IO {
    String readLine();
    void writeLine(String s);
}

<T> T interpret(Expression<T> expr, IO io) {
    return switch (expr) {
        case Value<T>(var v) -> v;
        case ReadLine __ -> (T) io.readLine();
        case WriteLine(var line) -> { io.writeLine(line); yield null; }
        case Sequence(var first, var second) -> {
            interpret(first, io);
            yield interpret(second, io);
        }
    };
}
```

## Pattern Matching as Control Flow Replacement

Traditional patterns like Strategy, Chain of Responsibility, and Interpreter can be replaced with pattern matching:

### Strategy Pattern → Switch Expression

```java
// Traditional Strategy Pattern:
sealed interface ShippingStrategy permits 
    StandardShipping, ExpressShipping, OvernightShipping {}
    
double calculateShipping(ShippingStrategy strategy, double weight) {
    return switch (strategy) {
        case StandardShipping(var rate) -> rate * weight;
        case ExpressShipping(var base, var perKg) -> base + perKg * weight;
        case OvernightShipping(var base, var premium) -> base + premium;
    };
}
```

### Chain of Responsibility → Switch Patterns

```java
sealed interface ValidationRule permits 
    NonEmptyRule, EmailFormatRule, MaxLengthRule, AgeRangeRule {}
    
List<String> validate(String value, List<ValidationRule> rules) {
    return rules.stream()
        .flatMap(rule -> switch (rule) {
            case NonEmptyRule __ when value.isEmpty() 
                -> Stream.of("Value must not be empty");
            case EmailFormatRule __ when !value.contains("@")
                -> Stream.of("Invalid email format");
            case MaxLengthRule(var max) when value.length() > max
                -> Stream.of("Exceeds max length of " + max);
            case AgeRangeRule(var min, var max) when !isValidAge(value, min, max)
                -> Stream.of("Age must be between " + min + " and " + max);
            default -> Stream.empty();
        })
        .toList();
}
```

## Layered Architecture with DTOs

Pattern matching works naturally with layered architectures:

```java
// API Layer — DTOs as records
record CreateUserRequest(String name, String email, String role) {}
record UserResponse(long id, String name, String email) {}
record ErrorResponse(int code, String message) {}

// Controller — pattern matching for request handling
class UserController {
    public Object createUser(CreateUserRequest request) {
        return switch (request) {
            case CreateUserRequest(var name, var email, var role) 
                when name == null || name.isBlank() 
                -> new ErrorResponse(400, "Name required");
            case CreateUserRequest(var name, var email, var role) 
                when !email.contains("@") 
                -> new ErrorResponse(400, "Invalid email");
            case CreateUserRequest(var name, var _, var _) 
                -> userService.createUser(name, email, role);
        };
    }
}
```

## Repository Pattern with Pattern Matching

```java
// Query objects as sealed types
sealed interface Query<T> permits 
    FindById<T>, FindAll<T>, FindByCriteria<T> {}
    
record FindById<T>(long id) implements Query<T> {}
record FindAll<T>(int page, int size) implements Query<T> {}
record FindByCriteria<T>(String field, Object value) implements Query<T> {}

// Unified repository with pattern matching
class UniversalRepository {
    private final JdbcTemplate jdbc;
    
    public <T> List<T> execute(Query<T> query, String table, RowMapper<T> mapper) {
        return switch (query) {
            case FindById(var id) -> List.of(
                jdbc.queryForObject("SELECT * FROM " + table + " WHERE id = ?", mapper, id));
            case FindAll(var page, var size) -> jdbc.query(
                "SELECT * FROM " + table + " LIMIT ? OFFSET ?", mapper, size, page * size);
            case FindByCriteria(var field, var value) -> jdbc.query(
                "SELECT * FROM " + table + " WHERE " + field + " = ?", mapper, value);
        };
    }
}
```

## Event-Driven Architecture

```java
// Domain events as sealed types
sealed interface DomainEvent 
    permits UserRegistered, OrderPlaced, PaymentReceived, InventoryUpdated {}

record UserRegistered(String userId, String email, Instant when) implements DomainEvent {}
record OrderPlaced(String orderId, String userId, List<OrderItem> items, Instant when) implements DomainEvent {}
record PaymentReceived(String transactionId, String orderId, Money amount, Instant when) implements DomainEvent {}
record InventoryUpdated(String productId, int quantity, int newStock, Instant when) implements DomainEvent {}

// Event processor with exhaustive handling
class EventProcessor {
    private final List<EventHandler> handlers = new ArrayList<>();
    
    public void process(DomainEvent event) {
        // Log all events
        log.info("Processing event: {}", event.getClass().getSimpleName());
        
        // Dispatch to handlers based on type
        switch (event) {
            case UserRegistered(var uid, var email, var when) -> {
                handlers.forEach(h -> h.onUserRegistered(uid, email, when));
            }
            case OrderPlaced(var oid, var uid, var items, var when) -> {
                handlers.forEach(h -> h.onOrderPlaced(oid, uid, items, when));
            }
            case PaymentReceived(var tid, var oid, var amt, var when) -> {
                handlers.forEach(h -> h.onPaymentReceived(tid, oid, amt, when));
            }
            case InventoryUpdated(var pid, var qty, var stock, var when) -> {
                handlers.forEach(h -> h.onInventoryUpdated(pid, qty, stock, when));
            }
        }
    }
}
```

## Functional Pipeline Architecture

Pattern matching enables clean data pipeline definitions:

```java
sealed interface PipelineStep<I, O> 
    permits MapStep, FilterStep, FlatMapStep, PeekStep, CollectStep {}

record MapStep<I, O>(Function<I, O> mapper) implements PipelineStep<I, O> {}
record FilterStep<I>(Predicate<I> predicate) implements PipelineStep<I, I> {}
record FlatMapStep<I, O>(Function<I, Stream<O>> mapper) implements PipelineStep<I, O> {}
record PeekStep<I>(Consumer<I> consumer) implements PipelineStep<I, I> {}
record CollectStep<I, O>(Collector<I, ?, O> collector) implements PipelineStep<I, O> {}

class Pipeline<I, O> {
    private final List<PipelineStep<I, ?>> steps;
    
    public O execute(Stream<I> input) {
        Stream<?> current = input;
        for (var step : steps) {
            current = switch ((PipelineStep<Object, Object>) step) {
                case MapStep<Object, Object>(var mapper) -> current.map(mapper);
                case FilterStep<Object>(var pred) -> current.filter(pred);
                case FlatMapStep<Object, Object>(var mapper) -> current.flatMap(mapper);
                case PeekStep<Object>(var consumer) -> current.peek(consumer);
                case CollectStep<Object, Object>(var collector) -> 
                    Stream.of(current.collect(collector));
            };
        }
        return (O) current.findFirst().orElseThrow();
    }
}
```
