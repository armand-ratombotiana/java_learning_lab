# Pattern Matching Code Deep Dive

This lab provides pure Java examples of how Pattern Matching, Records, and Sealed Classes combine to create highly expressive, declarative code.

## 💻 Pure Java Implementation

```java file="labs/java/modern-features/pattern-matching/SOLUTION/ModernJavaDemo.java"
package java.modernfeatures.patternmatching;

/**
 * A demonstration of modern Java data-oriented programming features.
 * Requires Java 21+.
 */
public class ModernJavaDemo {

    // 1. Sealed Interface: Exhaustively defines the hierarchy
    public sealed interface Shape permits Circle, Rectangle, Triangle {}

    // 2. Records: Transparent, immutable data carriers
    public record Circle(double radius) implements Shape {}
    public record Rectangle(double width, double height) implements Shape {}
    public record Triangle(double base, double height) implements Shape {}

    // Another record for nested deconstruction
    public record ColoredShape(String color, Shape shape) {}

    /**
     * Demonstrates Pattern Matching for instanceof (Java 16+)
     */
    public static void printShapeDetails(Shape shape) {
        // OLD WAY (Pre-Java 16):
        // if (shape instanceof Circle) {
        //     Circle c = (Circle) shape;
        //     System.out.println("Circle radius: " + c.radius());
        // }

        // NEW WAY: Test, declare, and cast in one step
        if (shape instanceof Circle c) {
            System.out.println("Circle radius: " + c.radius());
        } else if (shape instanceof Rectangle r) {
            System.out.println("Rectangle area: " + (r.width() * r.height()));
        }
    }

    /**
     * Demonstrates Pattern Matching for switch with Record Deconstruction (Java 21+)
     */
    public static double calculateArea(Shape shape) {
        // Because Shape is a sealed interface, the compiler knows this switch is exhaustive.
        // There is no need for a 'default' branch!
        return switch (shape) {
            // Record pattern: extracts the components directly
            case Circle(double r) -> Math.PI * r * r;
            case Rectangle(double w, double h) -> w * h;
            case Triangle(double b, double h) -> 0.5 * b * h;
        };
    }

    /**
     * Demonstrates Nested Record Deconstruction and Guard Clauses (Java 21+)
     */
    public static void describeColoredShape(ColoredShape cs) {
        switch (cs) {
            // Guard clause (when): Adds additional conditions to the pattern match
            case ColoredShape(String color, Circle(double r)) when r > 10.0 -> 
                System.out.println("A large " + color + " circle.");
                
            case ColoredShape(String color, Circle(double r)) -> 
                System.out.println("A normal " + color + " circle.");
                
            case ColoredShape(String color, Rectangle(double w, double h)) -> 
                System.out.println("A " + color + " rectangle.");
                
            case ColoredShape(String color, Triangle t) -> 
                System.out.println("A " + color + " triangle. Base: " + t.base());
                
            // We must handle null explicitly if we want to avoid NullPointerException in switch
            case null -> System.out.println("The shape is null.");
        }
    }

    public static void main(String[] args) {
        Shape c = new Circle(5.0);
        Shape r = new Rectangle(4.0, 5.0);
        
        System.out.println("--- InstanceOf Pattern Matching ---");
        printShapeDetails(c);
        
        System.out.println("\n--- Switch Expression Pattern Matching ---");
        System.out.println("Circle Area: " + calculateArea(c));
        System.out.println("Rectangle Area: " + calculateArea(r));
        
        System.out.println("\n--- Nested Deconstruction & Guards ---");
        ColoredShape bigRedCircle = new ColoredShape("Red", new Circle(15.0));
        ColoredShape smallBlueCircle = new ColoredShape("Blue", new Circle(5.0));
        
        describeColoredShape(bigRedCircle);
        describeColoredShape(smallBlueCircle);
    }
}
```

## 🔍 Key Takeaways
1. **No Default Branch**: Look at `calculateArea()`. Because `Shape` is `sealed` and we covered `Circle`, `Rectangle`, and `Triangle`, the Java compiler guarantees exhaustiveness. If another developer adds a `Hexagon` class to the `Shape` interface later, the code will fail to compile here, forcing them to handle the new case. This eliminates a huge class of runtime bugs.
2. **Record Deconstruction**: Look at `case Circle(double r)`. We didn't just match the type `Circle`; we reached inside it and extracted its `radius` component into a new variable `r` in a single line.
3. **Guard Clauses (`when`)**: Look at `describeColoredShape()`. The `when` keyword allows us to add arbitrary boolean logic to a pattern match. This replaces clunky nested `if` statements inside switch cases.