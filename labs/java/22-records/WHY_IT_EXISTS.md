# Why Records Exist

## The Problem: Boilerplate Data Carriers

Java developers have long needed a simple way to create "data carriers" — classes whose primary purpose is to aggregate values. Before records, the standard approach was verbose:

```java
public class Point {
    private final int x;
    private final int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    
    @Override
    public String toString() {
        return "Point{x=" + x + ", y=" + y + "}";
    }
}
```

This is ~30 lines for a simple 2-field value object. Multiply this by hundreds of such classes in a typical enterprise project, and the boilerplate becomes a significant maintenance burden. IDEs could generate these methods, but the generated code still cluttered source files and had to be regenerated when fields changed.

## The Problem: Serialization Security

Traditional serialization of data objects has a well-known security issue: the deserialization process bypasses the constructor, so validation logic in constructors is not executed during deserialization. Attackers can exploit this to create objects in invalid states.

Records solve this by requiring deserialization to go through the canonical constructor, ensuring all validation logic is always applied.

## The Problem: Broken equals() and hashCode()

In practice, many manually-implemented `equals()` and `hashCode()` methods are buggy:
- They're not updated when new fields are added
- They use inconsistent field sets
- They violate the contract (e.g., `equals()` uses fields that `hashCode()` doesn't)

Records eliminate this entire category of bugs by deriving `equals()` and `hashCode()` automatically from the component list.

## The Problem: intent Ambiguity

When a developer declares a class `class Point { ... }`, there's no syntactic distinction between:
- A **data carrier** (transparent, immutable aggregate)
- An **abstract data type** (opaque, encapsulates behavior)

Records make this intent explicit: `record Point(int x, int y) {}` clearly declares "this is a data carrier whose value is its state."

## The Design Philosophy

Records embody the principle that **the representation is the API**. For data carriers, hiding the internal state behind getters adds no value — the state is the value. Records make this explicit and automatic.

This philosophy extends to:
- **Accessors**: Named after the component (e.g., `point.x()`), not `getX()`
- **Construction**: The canonical constructor defines the state
- **Equality**: Value-based, not identity-based
- **Destructuring**: Via record patterns, enabling algebraic decomposition

## Records vs. Lombok

Project Lombok's `@Data`, `@Value`, and `@Builder` annotations addressed the boilerplate problem before records existed. However, records have advantages:
- **Standardized**: No additional dependency or annotation processor
- **IDE integration**: Every IDE has built-in support
- **Serialization security**: Automatic secure serialization
- **Pattern matching**: Native support for record patterns
- **Local records**: Can be declared inside methods
- **Future-proof**: Evolve with the Java platform

Records do not replace Lombok entirely — Lombok's `@Builder`, `@SuperBuilder`, and inheritance support go beyond what records can do — but they eliminate the most common use case for `@Data`/`@Value`.
