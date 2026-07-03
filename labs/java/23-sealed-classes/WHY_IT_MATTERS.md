# Why Sealed Classes Matter

## Impact on Code Quality

### Compile-Time Exhaustiveness
The single biggest benefit of sealed classes is compile-time exhaustiveness verification. Consider a payment system:

```java
sealed interface PaymentMethod permits CreditCard, PayPal, Crypto, BankTransfer {}

class PaymentProcessor {
    public PaymentResult process(PaymentMethod method, Money amount) {
        return switch (method) {
            case CreditCard cc -> processCard(cc, amount);
            case PayPal pp -> processPayPal(pp, amount);
            case Crypto c -> processCrypto(c, amount);
            case BankTransfer bt -> processTransfer(bt, amount);
            // If we add a new payment method, the compiler forces us to handle it here
        };
    }
}
```

If a developer later adds `ApplePay implements PaymentMethod`, the compiler immediately flags every `switch` over `PaymentMethod` as non-exhaustive. This prevents runtime errors where a new payment method silently falls through to a `default` case.

### Documentation as Code
The `permits` clause is self-documenting. Anyone reading the code immediately knows the entire family of related types. This is more reliable than Javadoc, which can become outdated.

### API Stability
Library authors can publish sealed hierarchies with confidence that downstream code cannot create unauthorized extensions. This is crucial for:
- **Security frameworks**: Only approved authentication providers
- **Configuration systems**: Only recognized configuration sources
- **Event systems**: Only defined event types

## Impact on Domain-Driven Design

Sealed classes make DDD concepts directly expressible in code:

```java
// DDD: Value object as a sum type
sealed interface OrderStatus permits Pending, Confirmed, Shipped, Delivered, Cancelled {}

record Pending() implements OrderStatus {}
record Confirmed(LocalDateTime confirmedAt) implements OrderStatus {}
record Shipped(String trackingNumber, LocalDateTime shippedAt) implements OrderStatus {}
record Delivered(LocalDateTime deliveredAt) implements OrderStatus {}
record Cancelled(String reason, LocalDateTime cancelledAt) implements OrderStatus {}
```

Each state has different associated data, and transitions between states can be modeled and verified.

## Impact on Functional Programming

Sealed classes bring algebraic data types (ADTs) to Java, enabling a functional programming style:

```java
// JSON as an ADT
sealed interface JsonValue permits JsonNull, JsonBool, JsonNumber, JsonString, JsonArray, JsonObject {}

record JsonNull() implements JsonValue {}
record JsonBool(boolean value) implements JsonValue {}
record JsonNumber(double value) implements JsonValue {}
record JsonString(String value) implements JsonValue {}
record JsonArray(List<JsonValue> elements) implements JsonValue {}
record JsonObject(Map<String, JsonValue> properties) implements JsonValue {}

// Exhaustive serialization
String serialize(JsonValue val) {
    return switch (val) {
        case JsonNull __ -> "null";
        case JsonBool(var b) -> String.valueOf(b);
        case JsonNumber(var n) -> String.valueOf(n);
        case JsonString(var s) -> "\"" + escape(s) + "\"";
        case JsonArray(var arr) -> "[" + arr.stream().map(this::serialize).collect(Collectors.joining(",")) + "]";
        case JsonObject(var props) -> "{" + props.entrySet().stream()
            .map(e -> "\"" + e.getKey() + "\":" + serialize(e.getValue()))
            .collect(Collectors.joining(",")) + "}";
    };
}
```

## Business Value

- **Reduced runtime errors**: Exhaustiveness checking catches missing cases at compile time
- **Self-documenting APIs**: The `permits` clause explicitly defines the type hierarchy
- **Controlled extensibility**: APIs evolve with predictable impact
- **Better domain modeling**: Complex business rules about type restrictions are encoded in the type system
- **IDE support**: IDEs can show the full list of permitted subtypes and auto-complete switch cases
- **Performance**: Closed hierarchies enable JIT optimization of virtual dispatch

## Impact on Testing

Sealed classes reduce the testing burden:
- No need to test with "unknown subclass" edge cases for exhaustive switches
- The compiler guarantees all cases are handled
- Adding a new subtype creates compile-time errors at every usage point, making the impact visible before testing
