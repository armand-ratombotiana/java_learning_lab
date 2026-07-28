# Deep Dive: Sealed Classes and Interfaces

## 1. Declaring Sealed Classes

Use `sealed` modifier and `permits` clause:

```java
public sealed class Vehicle permits Car, Truck, Motorcycle { }
```

Three rules for permitted subclasses:
1. Must be in the **same module** (or same package if unnamed module)
2. Must directly extend the sealed class
3. Must be `final`, `sealed`, or `non-sealed`

```java
public final class Car extends Vehicle { }
public sealed class Truck extends Vehicle permits DumpTruck { }
public non-sealed class Motorcycle extends Vehicle { }
public final class DumpTruck extends Truck { }
```

## 2. Sealed Interfaces

Interfaces follow the same pattern:

```java
public sealed interface JsonValue
    permits JsonString, JsonNumber, JsonArray, JsonObject, JsonNull { }

public record JsonString(String value) implements JsonValue { }
public record JsonNumber(double value) implements JsonValue { }
public record JsonArray(List<JsonValue> items) implements JsonValue { }
public record JsonObject(Map<String, JsonValue> fields) implements JsonValue { }
public record JsonNull() implements JsonValue { }
```

## 3. Same-File Declaration

When permitted subclasses are small, declare them in the same file — no `permits` needed:

```java
sealed interface Option<T> {
    record Some<T>(T value) implements Option<T> { }
    record None<T>() implements Option<T> { }
}
```

## 4. Exhaustive Switch with Sealed Types

The compiler knows all permitted subtypes — switch must be exhaustive:

```java
String describe(JsonValue v) {
    return switch (v) {
        case JsonString s -> "string: " + s.value();
        case JsonNumber n -> "number: " + n.value();
        case JsonArray a  -> "array of " + a.items().size();
        case JsonObject o -> "object with " + o.fields().size();
        case JsonNull _   -> "null";
    };
}
```

With `when` clauses (Java 21):

```java
String describeShape(Shape s) {
    return switch (s) {
        case Circle c when c.radius() > 10 -> "Large circle";
        case Circle c -> "Circle r=" + c.radius();
        case Rectangle r when r.width() == r.height() -> "Square";
        case Rectangle r -> "Rectangle";
    };
}
```

## 5. Key Design Considerations

| Strategy | When to use |
|----------|------------|
| `final` subclass | No further extension needed — leaf type |
| `sealed` subclass | Want to extend but control hierarchy |
| `non-sealed` subclass | Unknown or unbounded future extensions |

## 6. Common Pitfalls

- **Forgotten permits compile error**: Every permitted class must be listed
- **Sealed class in different module**: Permitted classes must be in the same module
- **Switch non-exhaustive**: The compiler enforces exhaustiveness — use a `default` or cover all cases
- **Record + Sealed**: Records cannot extend classes, but sealed *interfaces* work perfectly with records
