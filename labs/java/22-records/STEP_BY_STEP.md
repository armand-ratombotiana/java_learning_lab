# Step-by-Step Guide to Records

## Step 1: Define Your First Record

Create a simple record for a point in 2D space:

```java
record Point(int x, int y) {}
```

That's it. You now have a class with:
- A constructor `Point(int, int)`
- Accessors `x()` and `y()`
- `equals()`, `hashCode()`, and `toString()`

Create an instance and test it:

```java
public class RecordDemo {
    public static void main(String[] args) {
        Point p1 = new Point(3, 4);
        Point p2 = new Point(3, 4);
        Point p3 = new Point(5, 6);
        
        System.out.println(p1);           // Point[x=3, y=4]
        System.out.println(p1.x());       // 3
        System.out.println(p1.equals(p2)); // true
        System.out.println(p1.equals(p3)); // false
        System.out.println(p1.hashCode()); // Same as p2.hashCode()
    }
}
```

## Step 2: Add Validation with a Compact Constructor

```java
record PositivePoint(int x, int y) {
    PositivePoint {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException(
                "Coordinates must be positive: (" + x + ", " + y + ")");
        }
    }
}
```

Test validation:

```java
var p = new PositivePoint(5, 10);  // OK
var invalid = new PositivePoint(-1, 5);  // throws IllegalArgumentException
```

## Step 3: Add Custom Methods

```java
record Rectangle(double width, double height) {
    public double area() {
        return width * height;
    }
    
    public double perimeter() {
        return 2 * (width + height);
    }
    
    public boolean isSquare() {
        return width == height;
    }
    
    public Rectangle scale(double factor) {
        return new Rectangle(width * factor, height * factor);
    }
}

// Usage
var rect = new Rectangle(5, 3);
System.out.println(rect.area());       // 15.0
System.out.println(rect.perimeter());  // 16.0
System.out.println(rect.isSquare());   // false

var larger = rect.scale(2.0);
System.out.println(larger);            // Rectangle[width=10.0, height=6.0]
```

## Step 4: Add Non-Canonical Constructors

```java
record Color(int red, int green, int blue) {
    // Additional constructors must delegate to canonical constructor
    public Color(int gray) {
        this(gray, gray, gray);
    }
    
    public Color(String hex) {
        this(
            Integer.parseInt(hex.substring(0, 2), 16),
            Integer.parseInt(hex.substring(2, 4), 16),
            Integer.parseInt(hex.substring(4, 6), 16)
        );
    }
}

// Usage
var white = new Color(255);        // (255, 255, 255)
var purple = new Color("800080");  // (128, 0, 128)
```

## Step 5: Create a Local Record

Use a record inside a method for temporary data:

```java
import java.util.*;

public class LocalRecordExample {
    public static void main(String[] args) {
        List<String> words = List.of("apple", "banana", "apricot", "blueberry", "avocado");
        
        // Local record — scoped to this method
        record WordStats(String firstLetter, String word, int length) {}
        
        Map<String, List<WordStats>> grouped = new HashMap<>();
        for (String word : words) {
            String first = word.substring(0, 1);
            grouped.computeIfAbsent(first, k -> new ArrayList<>())
                   .add(new WordStats(first, word, word.length()));
        }
        
        grouped.forEach((letter, stats) -> {
            System.out.println("Words starting with '" + letter + "':");
            stats.forEach(s -> System.out.println("  " + s.word() + " (" + s.length() + " chars)"));
        });
    }
}
```

## Step 6: Implement an Interface

```java
interface Drawable {
    String render();
}

record Circle(double x, double y, double radius) implements Drawable {
    public String render() {
        return String.format("Circle at (%.1f, %.1f) radius %.1f", x, y, radius);
    }
}

record Rectangle(double x, double y, double width, double height) implements Drawable {
    public String render() {
        return String.format("Rectangle at (%.1f, %.1f) %.1fx%.1f", x, y, width, height);
    }
}

// Usage
List<Drawable> shapes = List.of(
    new Circle(0, 0, 5),
    new Rectangle(1, 2, 10, 20)
);
shapes.forEach(d -> System.out.println(d.render()));
```

## Step 7: Records with Serialization

```java
import java.io.*;

public record SerializablePerson(String name, int age) implements Serializable {}

public class SerializationDemo {
    public static void main(String[] args) throws Exception {
        var person = new SerializablePerson("Alice", 30);
        
        // Serialize
        try (var oos = new ObjectOutputStream(new FileOutputStream("person.ser"))) {
            oos.writeObject(person);
        }
        
        // Deserialize
        try (var ois = new ObjectInputStream(new FileInputStream("person.ser"))) {
            var deserialized = (SerializablePerson) ois.readObject();
            System.out.println(deserialized);  // SerializablePerson[name=Alice, age=30]
            System.out.println(person.equals(deserialized));  // true
        }
    }
}
```

## Step 8: Records with Pattern Matching

```java
// Declare records
record Address(String street, String city, String zip) {}
record Customer(String name, Address address) {}

// Pattern matching in action
public class PatternMatchingDemo {
    public static void main(String[] args) {
        Object obj = new Customer("Alice", new Address("123 Main St", "Springfield", "12345"));
        
        // Nested record pattern matching
        if (obj instanceof Customer(
            String name,
            Address(String street, String city, String zip)
        )) {
            System.out.printf("%s lives at %s in %s (ZIP: %s)%n", name, street, city, zip);
        }
    }
}
```

## Step 9: Records with Lombok `@Builder` Alternative

If you need a builder pattern with records:

```java
public record ComplexConfig(
    String host,
    int port,
    boolean useTls,
    long timeoutMs,
    int maxRetries
) {
    public static final class ConfigBuilder {
        private String host;
        private int port = 8080;
        private boolean useTls = true;
        private long timeoutMs = 5000;
        private int maxRetries = 3;
        
        public ConfigBuilder host(String host) { this.host = host; return this; }
        public ConfigBuilder port(int port) { this.port = port; return this; }
        public ConfigBuilder useTls(boolean useTls) { this.useTls = useTls; return this; }
        public ConfigBuilder timeoutMs(long timeoutMs) { this.timeoutMs = timeoutMs; return this; }
        public ConfigBuilder maxRetries(int maxRetries) { this.maxRetries = maxRetries; return this; }
        
        public ComplexConfig build() {
            return new ComplexConfig(host, port, useTls, timeoutMs, maxRetries);
        }
    }
}

// Usage: new ComplexConfig.ConfigBuilder().host("localhost").build()
```
