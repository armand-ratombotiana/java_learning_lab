# Debugging Records

## Common Compilation Errors

### "Records do not allow non-canonical instance fields"
Records cannot have instance fields beyond the components declared in the header:

```java
// ERROR: Extra instance field
record Point(int x, int y) {
    private int distanceFromOrigin;  // COMPILER ERROR!
}
```

**Fix**: Move to a compact constructor computation or use a method:

```java
record Point(int x, int y) {
    public double distanceFromOrigin() {
        return Math.sqrt(x * x + y * y);
    }
}
```

### "Modifier 'final' not allowed on record component"
Record components are implicitly final:

```java
record Point(final int x, final int y) {}  // COMPILER WARNING
```

**Fix**: Remove the redundant `final` keyword.

### "Illegal declaration of compact constructor"
Compact constructor syntax requires no parameter list and no access modifier (it inherits the record's access):

```java
record Point(int x, int y) {
    // ERROR:
    public Point(int x, int y) {
        // ...
    }
    
    // CORRECT:
    Point {
        // compact constructor — no parameters, no modifiers
    }
}
```

## Debugging Serialization Issues

### InvalidObjectException During Deserialization
If your compact constructor throws an exception on validation, serialization will fail:

```java
record Person(String name, int age) implements Serializable {
    public Person {
        if (age < 0) throw new IllegalArgumentException("Age cannot be negative");
    }
}

// Serialize
var p = new Person("Alice", 30);
// ... serialize to disk

// After modifying validation:
record Person(String name, int age) implements Serializable {
    public Person {
        if (age < 0 || age > 150) throw new IllegalArgumentException("Invalid age");
    }
}

// Deserialize — objects serialized before the validation change may now fail!
```

### SerialVersionUID Mismatch
Adding, removing, or renaming components changes the implicit serialVersionUID. If compatibility is needed, use `@Serial` annotation with a static serialVersionUID field:

```java
record Person(String name, int age) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
```

## Debugging Equality Issues

### Component Order in Equality
Remember that records compare components in declaration order. If you have:

```java
record Version(int major, int minor) {}

var v1 = new Version(1, 2);
var v2 = new Version(2, 1);
v1.equals(v2);  // false — correctly
```

### Comparing Records with Floating-Point Components
Floating-point equality can be surprising:

```java
record Measurement(double value) {}

var m1 = new Measurement(0.1 + 0.2);
var m2 = new Measurement(0.3);
m1.equals(m2);  // false — 0.1 + 0.2 = 0.30000000000000004, not 0.3
```

**Fix**: Use `BigDecimal` for exact decimal values, or implement a custom equals in a traditional class.

## Debugging Accessor Performance

Record accessors are normally inlined by the JIT. If profiling shows accessor methods as hotspots, check:

1. **Escape analysis**: Are records escaping the method? If so, they're heap-allocated
2. **Inlining**: Is the JIT able to inline the accessor? Use `-XX:+PrintInlining` to check
3. **Safepoints**: Are accessors causing safepoint poll overhead?

## Debugging with Reflection

```java
import java.lang.reflect.*;

public class RecordReflectionDebug {
    public static void main(String[] args) {
        record Sample(int id, String name) {}
        
        Class<Sample> clazz = Sample.class;
        System.out.println("Is record: " + clazz.isRecord());
        
        for (RecordComponent rc : clazz.getRecordComponents()) {
            System.out.println("Component: " + rc.getName() + 
                             " : " + rc.getType().getSimpleName());
        }
        
        // Get all methods
        for (Method m : clazz.getDeclaredMethods()) {
            System.out.println("Method: " + m.getName() + " → " + m.getReturnType().getSimpleName());
        }
    }
}
```

## Debugging with IDE

Most modern IDEs provide:
- **Structure view**: Shows record components and generated methods
- **Generate menu**: Shows what's already generated
- **Refactoring**: Renaming components updates constructors, accessors, equals, hashCode, and toString
- **Debugger**: Record instances display all components clearly

If a record doesn't behave as expected, check:
1. Is the record compiled with the correct Java version?
2. Are there custom methods that override generated methods?
3. Is serialization involved (with versioning issues)?
