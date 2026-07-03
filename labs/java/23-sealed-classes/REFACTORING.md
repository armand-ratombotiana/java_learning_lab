# Refactoring with Sealed Classes

## From Javadoc Convention to Sealed Classes

**Before**: Inheritance documented via Javadoc only
```java
/**
 * This interface should only be implemented by
 * Circle, Rectangle, and Triangle.
 */
public interface Shape {
    double area();
}
```

**After**: Inheritance enforced by compiler
```java
public sealed interface Shape permits Circle, Rectangle, Triangle {
    double area();
}
```

## From Package-Private Hierarchy

**Before**: Using package-private visibility to control inheritance
```java
// All in the same package
abstract class Shape { public abstract double area(); }
public class Circle extends Shape { ... }
public class Rectangle extends Shape { ... }

// Hidden from users but still extensible within the package
```

**After**: Explicit sealed control
```java
public sealed interface Shape permits Circle, Rectangle {
    double area();
}
```

Advantages:
- Types can be in different packages (same module)
- The hierarchy is documented in the source code
- No need to hide the base type

## From Enum Simulation

**Before**: Using enums with per-constant data
```java
enum ShapeType { CIRCLE, RECTANGLE, TRIANGLE }

class Shape {
    final ShapeType type;
    final double[] params;  // [radius], [width, height], [base, height]
    
    Shape(ShapeType type, double... params) {
        this.type = type;
        this.params = params;
    }
    
    double area() {
        return switch (type) {
            case CIRCLE -> Math.PI * params[0] * params[0];
            case RECTANGLE -> params[0] * params[1];
            case TRIANGLE -> 0.5 * params[0] * params[1];
        };
    }
}
```

**After**: Clean sealed hierarchy with records
```java
sealed interface Shape permits Circle, Rectangle, Triangle {}
record Circle(double radius) implements Shape {}
record Rectangle(double width, double height) implements Shape {}
record Triangle(double base, double height) implements Shape {}
```

Benefits:
- Type-safe: each shape has its own typed fields
- No runtime type discrimination needed
- Extensible without modifying existing records

## From Visitor Pattern

**Before**: The classic Visitor pattern for type-safe operations
```java
interface ShapeVisitor<R> {
    R visitCircle(Circle c);
    R visitRectangle(Rectangle r);
    R visitTriangle(Triangle t);
}

interface Shape {
    <R> R accept(ShapeVisitor<R> visitor);
}

record Circle(double r) implements Shape {
    public <R> R accept(ShapeVisitor<R> v) { return v.visitCircle(this); }
}
```

**After**: Pattern matching switch with sealed types
```java
sealed interface Shape permits Circle, Rectangle, Triangle {}

double area(Shape s) {
    return switch (s) {
        case Circle(var r) -> Math.PI * r * r;
        case Rectangle(var w, var h) -> w * h;
        case Triangle(var b, var h) -> 0.5 * b * h;
    };
}
```

The Visitor pattern was the workaround for Java's lack of algebraic data types and pattern matching. Sealed classes and pattern matching make it largely obsolete for new code.

## From Type-Safe Heterogeneous Container

**Before**: Using Class objects as keys
```java
class Favorites {
    private Map<Class<?>, Object> map = new HashMap<>();
    
    public <T> void put(Class<T> type, T value) {
        map.put(Objects.requireNonNull(type), value);
    }
    
    public <T> T get(Class<T> type) {
        return type.cast(map.get(type));
    }
}
```

**After**: Using sealed types with records
```java
sealed interface ConfigValue permits StringConfig, IntConfig, BoolConfig {}
record StringConfig(String value) implements ConfigValue {}
record IntConfig(int value) implements ConfigValue {}
record BoolConfig(boolean value) implements ConfigValue {}
```

## Migration Checklist

1. **Identify restricted hierarchies**: Find where you've used documentation or convention to control inheritance
2. **Check subtype locations**: Ensure all subtypes are in the same module (or package for unnamed modules)
3. **Add sealed keyword**: Apply to the base class/interface
4. **Add permits clause**: List all known subtypes
5. **Classify subtypes**: Add `final`, `sealed`, or `non-sealed` to each subtype
6. **Update switch statements**: Remove `default` from switches that can be exhaustive
7. **Test exhaustiveness**: Verify the compiler catches missing cases
8. **Document the sealed hierarchy**: The `permits` clause is self-documenting

## Caution: Backward Compatibility

Sealed classes restrict future subclassing. Before sealing a public class, consider:
- Are there external subclasses you don't know about? (Check your dependency tree)
- Do you need to allow third-party extensions? (Use non-sealed subtype as an extension point)
- Are you willing to bump major version when adding new permitted subtypes?
