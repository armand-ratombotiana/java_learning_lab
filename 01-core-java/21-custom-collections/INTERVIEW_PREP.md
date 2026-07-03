# Interview Preparation: Custom Collections

This document covers advanced questions related to collection internals, iterators, and memory management.

## Q1: Why do standard Java collections (like `ArrayList`) use a `modCount` variable, and how does it work?
**Answer:**
`modCount` (modification count) is used to provide **fail-fast iterators**.
Every time the collection is structurally modified (e.g., an element is added or removed), the collection's `modCount` integer is incremented.
When an `Iterator` is created, it takes a snapshot of the current `modCount` and stores it in `expectedModCount`. During every call to `next()` or `remove()` on the iterator, it checks if `modCount == expectedModCount`. If they do not match, it means another thread (or the same thread, bypassing the iterator) modified the collection, and the iterator immediately throws a `ConcurrentModificationException` to prevent unpredictable behavior or data corruption.

## Q2: If you implement a custom array-backed `List` and write a `remove(int index)` method, why must you explicitly set the last element of the array to `null` after shifting the elements?
**Answer:**
To prevent a **Memory Leak** (specifically, lingering references).
When you remove an element from the middle of an array, you shift all subsequent elements one position to the left. However, the last slot in the array still holds a reference to the object that used to be there. Even though you decrement your `size` variable (meaning the collection logically ignores that slot), the array itself still holds a strong reference to the object. Because of this reference, the Garbage Collector cannot reclaim the object's memory. By explicitly setting `data[size] = null`, you break that strong reference, allowing the GC to clean it up.

## Q3: Why does `ArrayList` store its elements in an `Object[]` instead of an `E[]` (a generic array)?
**Answer:**
Because of **Type Erasure** and array covariance.
In Java, arrays are covariant (an `Integer[]` is a subtype of `Number[]`), but generics are invariant (`List<Integer>` is NOT a subtype of `List<Number>`). Furthermore, generic type information is erased at runtime.
If Java allowed `new E[10]`, the JVM wouldn't know what type of array to actually create at runtime. Therefore, generic array creation is forbidden by the compiler. Standard collections work around this by creating an `Object[]` and casting the elements to `E` only when they are returned from methods like `get(int index)`.

## Q4: What is the difference between extending `AbstractCollection` and extending `AbstractList`?
**Answer:**
*   `AbstractCollection` provides a skeletal implementation for a generic collection. You must implement `iterator()` and `size()`. It is suitable for collections where order doesn't matter or elements are not accessed by an index (like a Bag or a custom Set).
*   `AbstractList` provides a skeletal implementation specifically for collections backed by a random-access data store (like an array). It provides an implementation of `iterator()` for you, based on the `get(int index)` method. You must implement `get(int index)` and `size()`.

## Q5: How would you implement a custom collection that holds primitive `int` values to save memory, rather than using `List<Integer>`?
**Answer:**
`List<Integer>` requires autoboxing, meaning every `int` is wrapped in an `Integer` object, adding significant memory overhead (object header, padding) and causing cache misses.
To build a primitive collection, you cannot implement the standard `java.util.List` interface because it requires generic objects. Instead, you would build a custom class (e.g., `IntList`) backed by a primitive `int[]` array. You would provide methods like `void add(int value)` and `int get(int index)`. Libraries like Eclipse Collections or fastutil provide these out of the box.