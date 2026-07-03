# Step-by-Step Guide to Pattern Matching

## Step 1: instanceof Pattern Matching

Start with familiar code and transform it:

```java
// Old way (Java 8-15)
public static void printLength(Object obj) {
    if (obj instanceof String) {
        String s = (String) obj;
        System.out.println(s.length());
    }
}

// New way (Java 16+)
public static void printLength(Object obj) {
    if (obj instanceof String s) {
        System.out.println(s.length());
    }
}
```

## Step 2: Switch with Type Patterns

```java
public static String describe(Object obj) {
    return switch (obj) {
        case null -> "null";
        case Integer i -> "Integer: " + i;
        case Long l -> "Long: " + l;
        case Double d -> "Double: " + d;
        case String s -> "String: " + s;
        case int[] arr -> "int array of length " + arr.length;
        default -> "Unknown type: " + obj.getClass().getName();
    };
}

// Test
System.out.println(describe(42));        // Integer: 42
System.out.println(describe("hello"));   // String: hello
System.out.println(describe(null));      // null
System.out.println(describe(3.14));      // Double: 3.14
```

## Step 3: Add Guards

```java
public static String classifyNumber(Number num) {
    return switch (num) {
        case Integer i when i > 0 -> "Positive integer";
        case Integer i when i == 0 -> "Zero";
        case Integer i -> "Negative integer";
        case Double d when d > 0 -> "Positive double";
        case Double d when d == 0.0 -> "Zero double";
        case Double d -> "Negative double";
        default -> "Other number";
    };
}
```

## Step 4: Create Records for Pattern Matching

```java
// Create a simple hierarchy
record Point(int x, int y) {}
record Circle(Point center, double radius) {}
record Rectangle(Point topLeft, Point bottomRight) {}

// Pattern matching with records
public static void inspect(Object obj) {
    switch (obj) {
        case Circle(Point center, double r) -> 
            System.out.println("Circle at (" + center.x() + "," + center.y() + ") r=" + r);
        case Rectangle(Point tl, Point br) ->
            System.out.println("Rectangle (" + tl.x() + "," + tl.y() + ")-(" + br.x() + "," + br.y() + ")");
        case Point(int x, int y) ->
            System.out.println("Point at (" + x + "," + y + ")");
        default -> System.out.println("Unknown: " + obj);
    }
}
```

## Step 5: Nested Record Patterns

```java
// Deep deconstruction
if (obj instanceof Circle(Point(int x, int y), double r)) {
    System.out.println("Circle center=(" + x + "," + y + ") radius=" + r);
}
```

## Step 6: Exhaustive Switch with Sealed Types

```java
sealed interface Vehicle permits Car, Bike, Truck {}
record Car(int seats) implements Vehicle {}
record Bike(boolean hasSidecar) implements Vehicle {}
record Truck(int payloadKg) implements Vehicle {}

public static String describe(Vehicle v) {
    return switch (v) {
        case Car(var seats) -> "Car with " + seats + " seats";
        case Bike(var sidecar) -> "Bike" + (sidecar ? " with sidecar" : "");
        case Truck(var payload) -> "Truck carrying " + payload + "kg";
    };
    // No default needed! If we add a new Vehicle subtype,
    // the compiler forces us to update this method.
}
```

## Step 7: Combining instanceof and Switch

```java
public static String process(Object obj) {
    // You can mix traditional if-instanceof with switch
    if (obj instanceof String s && s.length() > 10) {
        return "Long string: " + s;
    }
    
    return switch (obj) {
        case String s -> "Short string: " + s;
        case Integer i -> "Number: " + i;
        case null -> "Null";
        default -> "Other";
    };
}
```

## Step 8: Switch Expressions Returning Values

```java
record Order(double amount, String status) {}

public double calculateDiscount(Order order) {
    // Pattern matching can use record components directly
    return switch (order) {
        case Order(var amount, var status) when amount > 1000 && "PREMIUM".equals(status) 
            -> amount * 0.15;
        case Order(var amount, var status) when "PREMIUM".equals(status) 
            -> amount * 0.10;
        case Order(var amount, var status) when amount > 1000 
            -> amount * 0.05;
        default -> 0;
    };
}
```

## Step 9: Exception Handling with Patterns

```java
public static void safeProcess() {
    try {
        riskyOperation();
    } catch (Exception e) {
        switch (e) {
            case IOException ioe when ioe.getMessage() != null 
                -> System.err.println("IO error: " + ioe.getMessage());
            case IOException ioe 
                -> System.err.println("IO error occurred");
            case IllegalArgumentException iae 
                -> System.err.println("Invalid argument: " + iae.getMessage());
            case RuntimeException re 
                -> System.err.println("Runtime error: " + re);
            case null 
                -> System.err.println("Caught null exception");
            default 
                -> System.err.println("Unknown error: " + e);
        }
    }
}

private static void riskyOperation() throws IOException {
    // ...
}
```

## Step 10: Complete Transformation

Transform an old-style if-else chain into modern pattern matching:

```java
// BEFORE: Verbose if-else chain
public String processOld(Object obj) {
    if (obj == null) return "null";
    if (obj instanceof String) {
        String s = (String) obj;
        if (s.length() > 5) return "Long string: " + s;
        else return "Short string: " + s;
    }
    if (obj instanceof Integer) {
        Integer i = (Integer) obj;
        if (i > 0) return "Positive: " + i;
        else if (i == 0) return "Zero";
        else return "Negative: " + i;
    }
    return "Unknown: " + obj;
}

// AFTER: Concise pattern matching
public String processNew(Object obj) {
    return switch (obj) {
        case null -> "null";
        case String s when s.length() > 5 -> "Long string: " + s;
        case String s -> "Short string: " + s;
        case Integer i when i > 0 -> "Positive: " + i;
        case Integer i when i == 0 -> "Zero";
        case Integer i -> "Negative: " + i;
        default -> "Unknown: " + obj;
    };
}
```
