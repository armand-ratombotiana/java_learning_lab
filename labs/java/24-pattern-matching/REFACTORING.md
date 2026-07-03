# Refactoring with Pattern Matching

## Refactoring instanceof Chains

### Before: Traditional if-else chain
```java
public String process(Object obj) {
    if (obj instanceof String) {
        String s = (String) obj;
        return "String: " + s.toUpperCase();
    } else if (obj instanceof Integer) {
        Integer i = (Integer) obj;
        return "Integer: " + (i * 2);
    } else if (obj instanceof Boolean) {
        Boolean b = (Boolean) obj;
        return "Boolean: " + b;
    } else {
        return "Unknown";
    }
}
```

### After: Pattern matching switch
```java
public String process(Object obj) {
    return switch (obj) {
        case String s   -> "String: " + s.toUpperCase();
        case Integer i  -> "Integer: " + (i * 2);
        case Boolean b  -> "Boolean: " + b;
        case null       -> "null";
        default         -> "Unknown";
    };
}
```

## Refactoring Chained instanceof with Conditions

### Before
```java
public double calculateDiscount(Object customer) {
    if (customer instanceof PremiumCustomer) {
        PremiumCustomer pc = (PremiumCustomer) customer;
        if (pc.years() > 5) {
            return pc.totalSpent() * 0.20;
        }
        return pc.totalSpent() * 0.15;
    } else if (customer instanceof RegularCustomer) {
        RegularCustomer rc = (RegularCustomer) customer;
        if (rc.totalSpent() > 1000) {
            return rc.totalSpent() * 0.10;
        }
        return rc.totalSpent() * 0.05;
    }
    return 0;
}
```

### After
```java
public double calculateDiscount(Customer customer) {
    return switch (customer) {
        case PremiumCustomer(var years, var total) when years > 5 -> total * 0.20;
        case PremiumCustomer(var years, var total) -> total * 0.15;
        case RegularCustomer(var total) when total > 1000 -> total * 0.10;
        case RegularCustomer(var total) -> total * 0.05;
        case null -> 0;
    };
}
```

## Refactoring Visitor Pattern

### Before: Visitor pattern (many files)
```java
// ShapeVisitor.java
interface ShapeVisitor<R> {
    R visitCircle(Circle c);
    R visitRectangle(Rectangle r);
    R visitTriangle(Triangle t);
}

// Shape.java
interface Shape {
    <R> R accept(ShapeVisitor<R> visitor);
}

// Circle.java, Rectangle.java, Triangle.java — each with accept() implementation
```

### After: Pattern matching (single method)
```java
sealed interface Shape permits Circle, Rectangle, Triangle {}

<R> R process(Shape s, Function<Circle, R> fCircle, 
              Function<Rectangle, R> fRect, Function<Triangle, R> fTri) {
    return switch (s) {
        case Circle c -> fCircle.apply(c);
        case Rectangle r -> fRect.apply(r);
        case Triangle t -> fTri.apply(t);
    };
}
```

## Refactoring Nested if-instanceof for Data Extraction

### Before
```java
if (obj instanceof Line) {
    Line line = (Line) obj;
    Point start = line.start();
    Point end = line.end();
    if (start != null && end != null) {
        int x1 = start.x();
        int y1 = start.y();
        int x2 = end.x();
        int y2 = end.y();
        double distance = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
        System.out.println("Distance: " + distance);
    }
}
```

### After
```java
if (obj instanceof Line(Point(int x1, int y1), Point(int x2, int y2))) {
    double distance = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
    System.out.println("Distance: " + distance);
}
```

## Refactoring Enum Switch with Data

### Before
```java
enum HttpMethod {
    GET, POST, PUT, DELETE
}

Response handle(HttpMethod method, String path, String body) {
    return switch (method) {
        case GET -> handleGet(path);
        case POST -> handlePost(path, body);
        case PUT -> handlePut(path, body);
        case DELETE -> handleDelete(path);
    };
}
```

### After (using records with sealed types for richer data)
```java
sealed interface HttpMethod permits Get, Post, Put, Delete {}
record Get(Map<String, String> params) implements HttpMethod {}
record Post(String body, String contentType) implements HttpMethod {}
record Put(String body, String contentType) implements HttpMethod {}
record Delete() implements HttpMethod {}

Response handle(HttpMethod method) {
    return switch (method) {
        case Get(var params) -> handleGet(params);
        case Post(var body, var ct) -> handlePost(body, ct);
        case Put(var body, var ct) -> handlePut(body, ct);
        case Delete __ -> handleDelete();
    };
}
```

## Migration Checklist

1. **Identify instanceof+cast patterns**: Search for `instanceof.*{` and `(Type)`
2. **Replace with instanceof patterns**: Start with simple `if (x instanceof Type variable)`
3. **Convert if-else chains**: Replace long chains with switch expressions
4. **Add sealed classes**: Where appropriate, seal the type hierarchy
5. **Remove default cases**: Once sealed, remove `default` to enable exhaustiveness checking
6. **Add null handling**: Ensure null is handled explicitly in all switches
7. **Remove Visitor patterns**: Replace with switches on sealed hierarchies
8. **Test exhaustiveness**: Add new subtypes and verify compile errors

## Benefits of Refactoring

- **Reduced code**: 60-80% fewer lines for type-dispatching code
- **Compile-time safety**: Missing cases are now compile errors, not runtime bugs
- **Better null safety**: Explicit null handling
- **Cleaner extraction**: Record patterns eliminate manual deconstruction
- **Easier evolution**: Adding a new type creates errors at every usage site
