# Module 08: Generics - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is Type Erasure?
**Answer**:
Generics were introduced in Java 5. To ensure backward compatibility with older Java versions, the Java compiler uses **Type Erasure**. 
During compilation, all generic type parameters (like `<T>`) are removed (erased) and replaced with their bounds (e.g., `Object` for unbounded types, or the first bound like `Number` for `<T extends Number>`). As a result, at runtime, a `List<String>` and a `List<Integer>` are exactly the same class (`java.util.List`).

### Q2: What is the PECS rule?
**Answer**:
PECS stands for **Producer Extends, Consumer Super**. It dictates how to use wildcards `?` in generic methods to maximize flexibility.
- Use `? extends T` when you only need to **read** (produce) items from a collection. The collection is a producer. (You cannot safely add to it, because you don't know the exact subtype).
- Use `? super T` when you only need to **insert** (consume) items into a collection. The collection is a consumer. (You can safely read from it, but you only know you're getting an `Object`).

### Q3: Why can't you create a generic array like `new T[10]` or `new List<String>[10]`?
**Answer**:
Arrays in Java are covariant and retain their type information at runtime (they throw `ArrayStoreException` if you insert the wrong type). Generics use Type Erasure and do not have type information at runtime. Because arrays need to know their exact type at runtime to enforce safety, and generics erase that type, mixing them is fundamentally unsafe and disallowed by the compiler.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Generic Method with Multiple Bounds
**Problem**: Write a generic method `findMax` that takes a generic Array and returns the maximum element. The elements must be both `Comparable` and `Serializable`.

**Solution**:
```java
// T must extend Comparable to use compareTo.
// The & Serializable enforces the multiple bounds requirement.
public static <T extends Comparable<T> & Serializable> T findMax(T[] array) {
    if (array == null || array.length == 0) return null;
    
    T max = array[0];
    for (int i = 1; i < array.length; i++) {
        if (array[i].compareTo(max) > 0) {
            max = array[i];
        }
    }
    return max;
}
```

### Scenario 2: Applying the PECS Rule
**Problem**: Write a generic method `copyElements` that copies elements from a source list to a destination list. Apply wildcard boundaries so the method is as flexible as possible (e.g., copying a `List<Integer>` into a `List<Number>`).

**Solution**:
```java
// Source produces T (so it extends T)
// Destination consumes T (so it is a superclass of T)
public static <T> void copyElements(List<? extends T> source, List<? super T> destination) {
    for (T element : source) {
        destination.add(element);
    }
}

// Usage:
List<Integer> ints = Arrays.asList(1, 2, 3);
List<Number> nums = new ArrayList<>();
copyElements(ints, nums); // Valid because Integer extends Number
```