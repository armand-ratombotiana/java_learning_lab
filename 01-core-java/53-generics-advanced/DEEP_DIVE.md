# Deep Dive: Advanced Generics

## 1. Beyond `<T>`
Basic generics allow us to parameterize classes and methods (e.g., `List<String>`). However, building robust, reusable frameworks requires advanced constraints and bounds to ensure type safety while maintaining flexibility.

## 2. Recursive Type Bounds
A recursive type bound occurs when a type parameter is bounded by an expression that involves itself. This is extremely common in the Java standard library, particularly with the `Comparable` interface.

```java
// The standard Comparable interface
public interface Comparable<T> {
    public int compareTo(T o);
}
```
If we want to write a method that finds the maximum element in a collection, the elements must be comparable *to themselves*.

```java
// The <T extends Comparable<T>> is a recursive type bound.
// It means: T must be a type that implements Comparable, 
// and the type it can compare itself to must also be T.
public static <T extends Comparable<T>> T max(Collection<T> coll) {
    T max = coll.iterator().next();
    for (T item : coll) {
        if (item.compareTo(max) > 0) { max = item; }
    }
    return max;
}
```

## 3. The "Self-Type" Idiom (Curiously Recurring Template Pattern)
In Java, method chaining (the Builder pattern) is difficult when inheritance is involved.

```java
class Builder {
    public Builder setA() { return this; }
}
class SubBuilder extends Builder {
    public SubBuilder setB() { return this; }
}

// Usage fails!
SubBuilder b = new SubBuilder();
b.setA().setB(); // ERROR! setA() returns 'Builder', which doesn't have setB()
```

To solve this, we use the **Self-Type Idiom** (also known in C++ as CRTP). We parameterize the base class with the exact type of the subclass.

```java
// Base Builder
abstract class Builder<SELF extends Builder<SELF>> {
    
    // Abstract method to return the correct 'this' reference
    protected abstract SELF self();

    public SELF setA() {
        System.out.println("Setting A");
        return self(); // Returns the specific subclass type!
    }
}

// Sub Builder
class SubBuilder extends Builder<SubBuilder> {
    
    @Override
    protected SubBuilder self() {
        return this; 
    }

    public SubBuilder setB() {
        System.out.println("Setting B");
        return self();
    }
}

// Usage succeeds!
SubBuilder b = new SubBuilder();
b.setA().setB(); // Valid! setA() now returns 'SubBuilder'
```

## 4. Multiple Bounds
Sometimes a type parameter must implement multiple interfaces. Java allows multiple bounds using the `&` operator.

```java
// T must implement both Runnable and AutoCloseable
public static <T extends Runnable & AutoCloseable> void executeAndClose(T task) throws Exception {
    task.run();
    task.close();
}
```
*   **Rule**: If one of the bounds is a Class, it must be listed *first*. The rest must be Interfaces. (e.g., `<T extends MyClass & InterfaceA & InterfaceB>`).

## 5. Wildcards and PECS (Producer Extends, Consumer Super)
Wildcards (`?`) allow for flexibility when passing generic collections.
*   **`? extends T` (Upper Bound)**: Read-Only. You can safely read `T` from the collection, but you cannot add anything to it (because the compiler doesn't know the exact subtype).
*   **`? super T` (Lower Bound)**: Write-Only. You can safely add `T` to the collection, but you can only read `Object` from it.

**The PECS Rule**:
*   **P**roducer **E**xtends: If the collection *produces* data for your method to read, use `? extends T`.
*   **C**onsumer **S**uper: If the collection *consumes* data that your method writes, use `? super T`.

```java
// 'src' is a Producer (we read from it). 'dest' is a Consumer (we write to it).
public static <T> void copy(List<? extends T> src, List<? super T> dest) {
    for (T item : src) {
        dest.add(item);
    }
}
```