# How Records Work

## Declaration and Compilation

When you write:

```java
record Point(int x, int y) {}
```

The Java compiler generates a class equivalent to:

```java
public final class Point extends java.lang.Record {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() { return x; }
    public int y() { return y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
        Point other = (Point) o;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point[x=" + x + ", y=" + y + "]";
    }
}
```

Note: `java.lang.Record` is the common superclass for all records, similar to how `java.lang.Enum` is the common superclass for all enums.

## Compact Constructors

A compact constructor allows validation without repeating the parameter list:

```java
record PositiveInteger(int value) {
    PositiveInteger {
        if (value <= 0) {
            throw new IllegalArgumentException("value must be positive: " + value);
        }
        // Fields are assigned automatically after this block
    }
}
```

The compiler expands this to:

```java
public PositiveInteger(int value) {
    // Compact constructor body inserted here
    if (value <= 0) {
        throw new IllegalArgumentException("value must be positive: " + value);
    }
    // Implicit field assignment
    this.value = value;
}
```

Note: In a compact constructor, you refer to components by their name directly (`value`), not as constructor parameters. The compiler handles the assignment.

## Custom Canonical Constructor

You can also declare a non-compact constructor explicitly:

```java
record Range(int start, int end) {
    public Range(int start, int end) {
        if (start > end) throw new IllegalArgumentException("start > end");
        this.start = start;
        this.end = end;
        // Must assign all fields explicitly
    }
}
```

If you declare a custom canonical constructor, you must assign all components explicitly.

## Non-Canonical Constructors

Records can have additional constructors that delegate to the canonical constructor:

```java
record Color(int red, int green, int blue) {
    public Color(int gray) {
        this(gray, gray, gray);  // Must delegate to canonical constructor
    }
    
    public Color(String hex) {
        this(Integer.parseInt(hex.substring(0, 2), 16),
             Integer.parseInt(hex.substring(2, 4), 16),
             Integer.parseInt(hex.substring(4, 6), 16));
    }
}
```

## Custom Methods

Records can have custom instance and static methods:

```java
record Circle(double radius) {
    public double area() {
        return Math.PI * radius * radius;
    }
    
    public double circumference() {
        return 2 * Math.PI * radius;
    }
    
    public static Circle unitCircle() {
        return new Circle(1.0);
    }
}
```

## Record Serialization

Records have automatic serialization support if they implement `Serializable`:

```java
record Person(String name, int age) implements Serializable {}
```

The serialization form is based on the record components. During deserialization, the canonical constructor is always called with the serialized components. This means:

1. Validation in compact constructors is enforced during deserialization
2. No `readObject()` or `writeObject()` customization (they are ignored)
3. No `readResolve()` or `writeReplace()` (they are ignored)
4. The serialVersionUID is derived from the record's components

## Local Records

Records can be declared inside methods, lambda bodies, or inner classes:

```java
public List<String> analyze(List<Transaction> transactions) {
    record TransactionSummary(String category, double total) {}
    
    return transactions.stream()
        .collect(Collectors.groupingBy(
            Transaction::category,
            Collectors.summingDouble(Transaction::amount)))
        .entrySet().stream()
        .map(e -> new TransactionSummary(e.getKey(), e.getValue()))
        .sorted(Comparator.comparing(TransactionSummary::total).reversed())
        .map(TransactionSummary::toString)
        .toList();
}
```

Local records can capture variables from the enclosing scope (unlike local classes) and are useful for intermediate data structures in stream pipelines.

## Annotations on Records

Record components can be annotated, and the annotations are propagated to the corresponding constructor parameter, field, and accessor method:

```java
record User(
    @NotNull String name,
    @Email String email,
    @Min(0) int age
) {}
```

This is controlled by `@Target` on the annotation definition — annotations with `ElementType.RECORD_COMPONENT` target can be applied to record components.
