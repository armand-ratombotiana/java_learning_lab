# Abstraction and Interfaces — Ultra Deep Dive

## 1. Default Method Resolution Algorithm

Java 8 introduced default methods in interfaces. The resolution algorithm (JLS §15.12.2.5) follows these steps:

### Step-by-Step Resolution

Given a call `x.m()` where `x` has compile-time type `T`:

1. **Check T's superclasses**: Search from `T` upward to `Object` for a concrete `m()`. If found, use it.
2. **Check T's superinterfaces**: If no concrete method found in classes, search all superinterfaces for a most-specific default.
3. **Ambiguity check**: If two unrelated interfaces provide `m()`, error.
4. **Class wins**: A concrete method always beats a default method, even if the interface is "closer".

```java
interface A { default void m() { print("A"); } }
interface B extends A { default void m() { print("B"); } }
interface C extends A { }
class D implements B, C {
    // B.m() is more specific than A.m() — B.m() wins
    // No override needed in D
}

interface X { default void m() { print("X"); } }
interface Y { default void m() { print("Y"); } }
class Z implements X, Y {
    // ERROR: must override m() — X and Y are unrelated
}
```

### The Most-Specific Algorithm

The JLS defines "most specific" using the subtype relation:
- Interface `I` is more specific than `J` if `I extends J` (directly or indirectly)
- If neither is more specific than the other, both are "maximally specific" → ambiguity

### Handling `AbstractMethodError`

If a default method references an abstract method that the implementing class doesn't provide:
```java
interface HasDefault {
    default void implementMe() { helper(); }
    void helper();  // abstract
}
class Concrete implements HasDefault {
    // No helper() → AbstractMethodError at runtime
}
```

The bytecode for `implementMe()` contains an `invokevirtual` for `helper()`. When called on `Concrete`, the vtable has no `helper()` → `AbstractMethodError`.

## 2. Interface Super Calls

Java 8 also introduced `Interface.super.method()` syntax:

```java
interface A {
    default void m() { System.out.println("A"); }
}
class B implements A {
    @Override
    public void m() {
        A.super.m();  // Calls A's default method
    }
}
```

### Bytecode of Interface Super Call

```
  aload_0                          // Load 'this'
  invokespecial A.m:()V            // Calls A.m() directly
```

Wait — `invokespecial`? Yes! Interface super calls use `invokespecial`, not `invokeinterface`. This is the same instruction used for private methods and constructors. It bypasses dynamic dispatch and calls the specific interface's implementation directly.

### Why `invokespecial`?

`invokespecial` is used because we want a *specific* implementation — not virtual dispatch. This is the same mechanism that allows calling a specific parent's implementation in class inheritance:

```java
class Parent { void m() { } }
class Child extends Parent {
    void m() { super.m(); }  // invokespecial Parent.m()
}
```

## 3. Bridge Methods Generated for Interfaces

When a class implements a generic interface, bridge methods may be generated:

```java
interface Comparator<T> {
    int compare(T a, T b);
}
class StringComparator implements Comparator<String> {
    public int compare(String a, String b) { return a.compareTo(b); }
    // Bridge method:
    // public int compare(Object a, Object b) {
    //     return compare((String) a, (String) b);
    // }
}
```

The bridge method has `ACC_BRIDGE | ACC_SYNTHETIC` flags. It exists because at bytecode level, the erased method `compare(Object, Object)` must exist for vtable dispatch.

## 4. Synthetic Methods

The compiler generates synthetic methods for various purposes:

| Purpose | Example | Bytecode Flag |
|---------|---------|--------------|
| Bridge for covariant return | `Object get() { return this.get(); }` | ACC_BRIDGE, ACC_SYNTHETIC |
| Bridge for erased generics | `int compare(Object, Object)` | ACC_BRIDGE, ACC_SYNTHETIC |
| Outer class access to inner private fields | `static int access$000(Outer)` | ACC_SYNTHETIC, ACC_STATIC |
| Enum valueOf | `static Color valueOf(String)` | ACC_SYNTHETIC |
| Lambda body | Private static method for lambda body | ACC_SYNTHETIC |

### Example: Synthetic Accessor

```java
class Outer {
    private int x;
    class Inner {
        void access() {
            x = 42;  // Accesses Outer's private field
        }
    }
}
```

The compiler generates:
```java
class Outer {
    private int x;
    // Synthetic package-private setter:
    static int access$002(Outer outer, int val) {
        return outer.x = val;
    }
}
class Outer$Inner {
    void access() {
        Outer.access$002(Outer.this, 42);
    }
}
```

This is needed because inner classes are compiled to separate class files, and private members are not accessible across class boundaries at the JVM level.

## 5. Interface Static Methods

Since Java 8, interfaces can have static methods:

```java
interface MathUtils {
    static double PI() { return 3.14159; }
    static int max(int a, int b) { return a > b ? a : b; }
}
```

These are invoked with `invokestatic` — just like static methods in classes:

```
  invokestatic MathUtils.PI:()D
```

Unlike default methods, interface static methods are NOT inherited by implementing classes or subinterfaces.

## 6. Interface Private Methods (Java 9+)

Since Java 9, interfaces can have private and private static methods:

```java
interface Logger {
    default void log(String msg) {
        logImpl(msg, Level.INFO);
    }
    private void logImpl(String msg, Level level) {
        // Shared implementation
    }
}
```

This allows code sharing between default methods without exposing the helper in the public API. The methods are compiled with `ACC_PRIVATE` flag.

## 7. Marker Interfaces vs Annotations

Before annotations (Java 5), marker interfaces were the only way to tag a class:

```java
// Marker interface (pre-Java 5):
class MyClass implements Serializable, Cloneable { }

// Annotation equivalent (Java 5+):
@Serializable
@Cloneable
class MyClass { }
```

### Why Markers Still Exist

1. **Type checking**: `Serializable` can be used as a type — `void write(Serializable s)` won't accept non-serializable types
2. **Runtime checks**: `instanceof` is fast; reflective annotation checks are slower
3. **Inheritance**: A marker interface automatically applies to all subclasses

## 8. Functional Interfaces and @FunctionalInterface

A functional interface has exactly one abstract method:

```java
@FunctionalInterface
interface Predicate<T> {
    boolean test(T t);
    // Default and static methods are ignored for the count
    default Predicate<T> negate() { return t -> !test(t); }
}
```

The `@FunctionalInterface` annotation is optional but causes a compile error if the interface has more than one abstract method.

### Lambda Conversion

Lambdas are compiled using `invokedynamic` with `LambdaMetafactory`. The lambda's target is the single abstract method of the functional interface.

## 9. Constant Pool and Interfaces

Interface method references are stored in the constant pool differently:

| Type | Constant Pool Entry | Example |
|------|--------------------|---------|
| Class method | `CONSTANT_Methodref` | `ArrayList.add:(Object)Z` |
| Interface method | `CONSTANT_InterfaceMethodref` | `List.add:(Object)Z` |

The difference between `invokevirtual` (takes `Methodref`) and `invokeinterface` (takes `InterfaceMethodref`) is encoded in the constant pool entry type.

## 10. Interface Constants and Constant Pool

Interfaces can declare constants (implicitly `public static final`):

```java
interface Constants {
    int MAX = 100;        // public static final int MAX = 100;
    String NAME = "app";  // public static final String NAME = "app";
}
```

These are inlined by the compiler at compile time for primitive and String constants:
```java
if (value > Constants.MAX) { ... }
// Bytecode: sipush 100 (no field access!)
```

This means changing `Constants.MAX` in the interface requires recompilation of all implementing classes.

## 11. Interface and Abstract Class: JVM Memory Comparison

```java
// Abstract class:
abstract class Shape {
    int x, y;
    abstract double area();
    double perimeter() { return 0; }  // virtual method with implementation
}
// Each instance: 12B header + 8B fields = 20B (+ padding to 24B)
// VTable: contains area() and perimeter()

// Interface:
interface Shape {
    double area();
    default double perimeter() { return 0; }
}
// No instance fields (unless it's a static final constant)
// Each implementing class pays for its own fields
```

Abstract classes are better for shared state; interfaces are better for shared behavior without state.

## 12. The `@FunctionalInterface` Contract

The `@FunctionalInterface` annotation (JLS §9.6.4.9) requires:

1. The interface has exactly one abstract method
2. Methods from `Object` (toString, equals, hashCode) don't count
3. Default methods don't count (they're not abstract)
4. Static methods don't count

```java
@FunctionalInterface
interface Valid {
    void doit();
    String toString();  // Object method — doesn't break functional contract
    default void log() { }  // Default — doesn't break contract
}
```

## 13. Marker Interfaces and the `Serializable` Contract

`Serializable` is a marker interface with special JVM support:

```java
// java.io.Serializable has NO methods
// But the JVM's ObjectStreamClass checks for this interface
// Classes without Serializable get NotSerializableException
```

The `java.io.ObjectOutputStream` checks for the `Serializable` marker at runtime:
```java
if (!(obj instanceof Serializable)) {
    throw new NotSerializableException(obj.getClass().getName());
}
```

## 14. Proxy Classes (Dynamic Proxies)

`java.lang.reflect.Proxy` creates synthetic classes that implement arbitrary interfaces at runtime:

```java
InvocationHandler handler = (proxy, method, args) -> {
    System.out.println("Called: " + method.getName());
    return null;
};
List<String> proxy = (List<String>) Proxy.newProxyInstance(
    List.class.getClassLoader(),
    new Class[]{List.class},
    handler
);
proxy.size();  // Prints: "Called: size"
```

The proxy class is generated at runtime using the `sun.misc.ProxyGenerator`. It extends `java.lang.reflect.Proxy` and implements all specified interfaces through a single `InvocationHandler`.

## 15. Adapting with Adapter Interfaces

The Adapter pattern provides default empty implementations:

```java
interface Listener {
    void onStart();
    void onData(String data);
    void onEnd();
}
// Java 8+ default methods make adapters unnecessary:
interface Listener {
    default void onStart() {}
    default void onData(String data) {}
    default void onEnd() {}
}
class MyListener implements Listener {
    @Override
    public void onData(String data) { process(data); }
}
```

## 10. Interface Evolution and Binary Compatibility

Adding a default method to an interface is a **source-compatible but binary-incompatible** change (in some contexts):

```java
// Version 1:
interface MathOp {
    int apply(int a, int b);
}
// Version 2 (adds default method):
interface MathOp {
    int apply(int a, int b);
    default int twice(int a) { return apply(a, a); }
}
```

Classes compiled against v1 will NOT know about `twice()` at runtime — but if they're recompiled against v2, they'll have it. This is the key to Java's backward compatibility guarantees.
