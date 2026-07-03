# Abstraction & Interfaces — How It Works

## Step 1: Abstract Class Resolution

```java
public abstract class Shape {
    public abstract double area();
}
```

The compiler:
1. Marks Shape class as `ACC_ABSTRACT` in class file flags
2. Marks `area()` as `ACC_ABSTRACT` in method flags
3. Refuses to allow `new Shape()` — `invokespecial <init>` not allowed for abstract classes

## Step 2: Interface Definition

```java
public interface Drawable {
    void draw();
}
```

The compiler:
1. Creates class file with `ACC_INTERFACE` and `ACC_ABSTRACT` flags
2. Methods are implicitly `public abstract` (flags set automatically)
3. Fields are implicitly `public static final`

## Step 3: Interface Implementation

```java
public class Circle implements Drawable {
    public void draw() { /* ... */ }
}
```

1. Class file records implemented interfaces in `interfaces` array
2. Compiler verifies Circle implements all abstract methods from Drawable
3. At runtime, Circle's itable includes Drawable's method entries

## Step 4: Default Method Resolution

```java
interface A {
    default void hello() { System.out.println("A"); }
}
interface B {
    default void hello() { System.out.println("B"); }
}
class C implements A, B {
    // Must override hello() or compilation error (diamond ambiguity)
    public void hello() { A.super.hello(); }  // Explicit selection
}
```

The compiler detects diamond conflicts and requires explicit override. `A.super.hello()` syntax calls the default method from a specific interface.

## Step 5: Interface Dispatch at Runtime

```java
Drawable d = new Circle();
d.draw();
```

1. `invokeinterface` instruction
2. JVM searches Circle's itable for Drawable.draw() entry
3. Executes Circle's implementation
