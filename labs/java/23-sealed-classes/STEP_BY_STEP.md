# Step-by-Step Guide to Sealed Classes

## Step 1: Define a Simple Sealed Interface

Start with a basic sealed interface:

```java
public sealed interface Animal permits Dog, Cat, Bird {}
```

Now define the permitted subtypes:

```java
public final class Dog implements Animal {}
public final class Cat implements Animal {}
public final class Bird implements Animal {}
```

Each subtype must be in the same package (or module) and be declared `final`, `sealed`, or `non-sealed`.

## Step 2: Use Records as Permitted Subtypes

Records are implicitly `final`, making them natural sealed subtypes:

```java
public sealed interface Shape permits Circle, Rectangle, Triangle {}

public record Circle(double radius) implements Shape {}
public record Rectangle(double width, double height) implements Shape {}
public record Triangle(double base, double height) implements Shape {}
```

## Step 3: Exhaustive Switch with Pattern Matching

```java
class ShapeCalculator {
    public double area(Shape shape) {
        return switch (shape) {
            case Circle(var r) -> Math.PI * r * r;
            case Rectangle(var w, var h) -> w * h;
            case Triangle(var b, var h) -> 0.5 * b * h;
        };  // No default needed!
    }
    
    public static void main(String[] args) {
        var calculator = new ShapeCalculator();
        
        var circle = new Circle(5.0);
        var rect = new Rectangle(4.0, 6.0);
        var tri = new Triangle(3.0, 8.0);
        
        System.out.println("Circle area: " + calculator.area(circle));
        System.out.println("Rectangle area: " + calculator.area(rect));
        System.out.println("Triangle area: " + calculator.area(tri));
    }
}
```

## Step 4: Create a Nested Sealed Hierarchy

```java
public sealed interface Vehicle permits Car, Truck, Motorcycle {}

// Car is sealed — it has its own subtypes
public sealed interface Car extends Vehicle permits Sedan, SUV, SportsCar {}
public record Sedan(int doors) implements Car {}
public record SUV(boolean fourWheelDrive) implements Car {}
public record SportsCar(double topSpeed) implements Car {}

// Truck is final — no subtypes
public record Truck(int payloadKg) implements Vehicle {}

// Motorcycle is non-sealed — anyone can extend
public non-sealed interface Motorcycle extends Vehicle {}
public record SportBike(int cc) implements Motorcycle {}
public record Cruiser(int cc) implements Motorcycle {}
```

## Step 5: Exhaustive Switch Over Nested Hierarchy

```java
class VehicleProcessor {
    public String describe(Vehicle v) {
        return switch (v) {
            case Sedan(var doors) -> "Sedan with " + doors + " doors";
            case SUV(var awd) -> "SUV " + (awd ? "with AWD" : "without AWD");
            case SportsCar(var speed) -> "Sports car, top speed " + speed + " mph";
            case Truck(var payload) -> "Truck, payload " + payload + " kg";
            // Motorcycle is non-sealed, so we need a catch-all for it
            case Motorcycle m -> "Generic motorcycle";
        };
        // Note: Because Car is sealed (Sedan, SUV, SportsCar),
        // we cover all Car subtypes individually.
        // Truck is final — one case.
        // Motorcycle is non-sealed — single case or default.
    }
}
```

## Step 6: Add Non-Sealed for Extensibility

If you need to allow external extension of a sealed hierarchy, use `non-sealed`:

```java
public sealed interface PaymentMethod 
    permits CreditCard, PayPal, Crypto, CustomPayment {}

public record CreditCard(String last4, String expiry) implements PaymentMethod {}
public record PayPal(String email) implements PaymentMethod {}
public record Crypto(String walletAddress) implements PaymentMethod {}

// Allow third-party payment method implementations
public non-sealed interface CustomPayment extends PaymentMethod {
    String getTransactionDetails();
}

// Now external code can create custom payment methods:
public class ApplePay implements CustomPayment {
    public String getTransactionDetails() { return "Apple Pay transaction"; }
}
```

## Step 7: Sealed Abstract Class

Sealed classes can be abstract classes with shared implementation:

```java
public sealed abstract class Document 
    permits PDFDocument, WordDocument, SpreadsheetDocument {
    
    protected final String name;
    protected final byte[] content;
    
    public Document(String name, byte[] content) {
        this.name = name;
        this.content = content.clone();
    }
    
    public abstract String getExtension();
    public abstract String getMimeType();
    
    public String getName() { return name; }
    public int getSize() { return content.length; }
}

public final class PDFDocument extends Document {
    public PDFDocument(String name, byte[] content) {
        super(name, content);
    }
    
    public String getExtension() { return "pdf"; }
    public String getMimeType() { return "application/pdf"; }
}

// etc.
```

## Step 8: Reflection with Sealed Classes

```java
public class SealedReflectionDemo {
    public static void main(String[] args) {
        Class<Shape> shapeClass = Shape.class;
        
        System.out.println("Is sealed: " + shapeClass.isSealed());
        System.out.println("Permitted subclasses:");
        
        for (Class<?> permitted : shapeClass.getPermittedSubclasses()) {
            System.out.println("  - " + permitted.getSimpleName()
                + " (" + (permitted.isSealed() ? "sealed" : 
                          permitted.isInterface() && !permitted.isSealed() ? "non-sealed" : "final")
                + ")");
        }
    }
}
```

## Step 9: Compilation Error Demonstration

What happens when you don't handle all subtypes?

```java
// Add a new shape:
public record Pentagon(double side) implements Shape {}

// The following switch NOW fails to compile:
public double area(Shape shape) {
    return switch (shape) {
        case Circle(var r) -> Math.PI * r * r;
        case Rectangle(var w, var h) -> w * h;
        case Triangle(var b, var h) -> 0.5 * b * h;
        // Missing Pentagon!
        // Compiler error: "the switch expression does not cover all possible values"
    };
}
```

The fix: either add a `case Pentagon` or add a `default` clause.
