# Generics — Interview Questions

## Beginner

### Q1: What are generics in Java?

Generics allow types to be parameterized, enabling classes, interfaces, and methods to operate on objects of various types while providing compile-time type safety. They eliminate the need for explicit casting and prevent `ClassCastException` at runtime.

### Q2: What is type erasure?

Type erasure is the compiler process where generic type information is removed during compilation. Type parameters are replaced with their bounds (or `Object` if unbounded), and casts are inserted where necessary. This ensures backward compatibility with non-generic code.

### Q3: What is a bounded type parameter?

A type parameter with an upper bound, e.g., `<T extends Number>`. It restricts `T` to be `Number` or a subclass, allowing access to `Number` methods within the generic code.

## Intermediate

### Q4: Explain PECS.

PECS stands for "Producer Extends, Consumer Super." When a collection produces values (you read from it), use `? extends T`. When it consumes values (you write to it), use `? super T`. If you both read and write, use the exact type `T`.

### Q5: Why can't you create `new T[]` or `new T()` in generics?

Because of type erasure. At runtime, `T` is replaced with its bound (or `Object`), so the JVM doesn't have the concrete type information needed to create an instance or array. Use `Object[]` internally with casts, or pass a `Class<T>` token.

### Q6: What are bridge methods?

Bridge methods are synthetic methods the compiler generates when a class implements a generic interface to maintain polymorphic behavior. For example, if `Name implements Comparable<Name>`, the compiler generates `compareTo(Object)` that calls `compareTo(Name)`.

## Advanced

### Q7: Why are generics invariant? Can you provide an example of when you need covariance/contravariance?

Generics are invariant (`List<Integer>` ≠ `List<Number>`) to prevent type safety violations. If they were covariant, you could add a `Double` to a `List<Integer>` via a `List<Number>` reference.

**Covariance needed** (`? extends T`): When you only read items, e.g., `void process(List<? extends Shape> shapes)` — you can pass `List<Circle>` or `List<Rectangle>`.

**Contravariance needed** (`? super T`): When you only add items, e.g., `void addCircles(List<? super Circle> list)` — you can add to `List<Shape>` or `List<Object>`.

### Q8: How would you implement a type-safe heterogenous container?

Use `Class<T>` as keys in a `Map<Class<?>, Object>`:

```java
public class TypeSafeMap {
    private Map<Class<?>, Object> map = new HashMap<>();
    public <T> void put(Class<T> type, T value) { map.put(type, value); }
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) { return (T) map.get(type); }
}
```

### Q9: What is capture conversion?

When the compiler encounters a wildcard `?`, it creates a fresh type variable (capture) representing the specific but unknown type. This allows the compiler to reason about the wildcard within a method body while preventing unsafe operations.

### Q10: Compare Java generics with C++ templates and .NET generics.

**Java**: Type erasure — no runtime generic info, backward compatible, no code bloat, no specialization, primitives require boxing.

**C++**: Templates are textual expansion — full specialization, code bloat, no bounds on type parameters, each instantiation is a separate type.

**.NET**: Reified generics — runtime type info preserved, backward compatible via shared runtime, no code bloat (shared native code), value types supported without boxing (`List<int>`).
