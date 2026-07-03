# Generics — Theoretical Foundation

## Generic Classes

A generic class is defined with one or more type parameters in angle brackets:

```java
public class Box<T> {
    private T value;

    public void set(T value) { this.value = value; }
    public T get() { return value; }
}
```

Usage: `Box<String> stringBox = new Box<>();` — the compiler knows `get()` returns `String`.

## Generic Methods

Type parameters can be declared at the method level:

```java
public <T> T identity(T value) {
    return value;
}

public <T extends Comparable<T>> T max(T a, T b) {
    return a.compareTo(b) > 0 ? a : b;
}
```

The type parameter is inferred from the arguments or specified explicitly: `this.<String>identity("x")`.

## Bounded Type Parameters

Restrict the types that can be used as type arguments:

```java
// Upper bound: T must be Number or a subclass
public <T extends Number> double sum(List<T> list) { ... }

// Multiple bounds:
public <T extends Comparable<T> & Serializable> void sort(T[] arr) { ... }
```

## Wildcards

Use `?` to represent an unknown type:

| Syntax | Meaning | Example |
|--------|---------|---------|
| `?` | Unbounded wildcard | `List<?>` |
| `? extends T` | Upper bounded | `List<? extends Number>` |
| `? super T` | Lower bounded | `List<? super Integer>` |

### Unbounded Wildcard

```java
public void printAll(List<?> list) {
    for (Object o : list) System.out.println(o);
}
```

Cannot add elements (except null) because the type is unknown.

### Upper Bounded Wildcard (`? extends T`)

Readable — you know elements are at least `T`:

```java
public double sum(List<? extends Number> numbers) {
    double s = 0;
    for (Number n : numbers) s += n.doubleValue();
    return s;
}
```

Cannot add (except null) — could be `List<Integer>`, `List<Double>`, etc.

### Lower Bounded Wildcard (`? super T`)

Writable — you can safely add `T` elements:

```java
public void addNumbers(List<? super Integer> list) {
    list.add(1);
    list.add(2);
}
```

Reading returns `Object` — the actual type could be `Integer`, `Number`, or `Object`.

## PECS Principle

**P**roducer **E**xtends, **C**onsumer **S**uper.

- If a parameter is a **producer** of `T` values (you read from it), use `? extends T`
- If a parameter is a **consumer** of `T` values (you write to it), use `? super T`
- If both, use exact type `T` (no wildcard)

```java
public void copy(List<? extends T> src, List<? super T> dest) {
    for (T item : src) dest.add(item);
}
```

## Type Erasure

Generics are a compile-time feature. The compiler erases type parameters to their bounds or `Object`:

```java
// Source:
Box<String> box = new Box<>();
String s = box.get();

// Erased to:
Box box = new Box();
String s = (String) box.get();
```

Type parameters are replaced with their leftmost bound (or `Object` if unbounded). Bridge methods are generated to preserve polymorphism.

## Type Erasure Implications

1. **No `new T()`**: Cannot instantiate type parameters
2. **No `new T[]`**: Cannot create arrays of type parameters
3. **No `instanceof`**: Cannot check type at runtime
4. **No primitives**: Cannot use `int`, `double` etc. (use wrapper classes)
5. **No static fields of type parameter**: Static fields are shared across all parameterizations
6. **Bridge methods**: Compiler generates synthetic methods for type safety

```java
// Cannot do:
public class MyClass<T> {
    private T value = new T();       // Error
    private T[] arr = new T[10];     // Error
    public boolean isString() { return value instanceof String; } // Error
}
```

## Generic Interfaces

```java
public interface Comparable<T> {
    int compareTo(T o);
}

public interface List<E> extends Collection<E> {
    E get(int index);
    boolean add(E e);
}
```

## Raw Types

Using a generic type without type parameters is a raw type (allowed for backward compatibility, but generates compiler warnings):

```java
Box box = new Box();       // Raw type — warning
box.set("hello");          // Unchecked warning
String s = (String) box.get(); // Manual cast needed
```

Always use parameterized types for type safety.
