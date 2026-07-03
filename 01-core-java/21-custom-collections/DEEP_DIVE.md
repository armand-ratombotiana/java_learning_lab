# Deep Dive: Custom Collections Implementation

## 1. Why Implement Custom Collections?
The Java Collections Framework (JCF) is extensive, but sometimes you need a data structure tailored to a highly specific use case.
*   **Memory Optimization**: Standard collections wrap primitives in objects (e.g., `Integer` instead of `int`). A custom collection can store primitives directly, saving massive amounts of memory.
*   **Specialized Behavior**: You might need a `List` that automatically evicts the oldest elements when a capacity is reached (a Ring Buffer), or a `Set` that maintains elements in a very specific, non-standard order.
*   **Domain-Specific Constraints**: A collection that only accepts elements meeting complex business rules.

## 2. The Abstract Base Classes
Java provides abstract skeletal implementations to minimize the effort required to build custom collections. You rarely implement `List` or `Set` from scratch; instead, you extend these abstract classes.

### `AbstractCollection<E>`
To implement an unmodifiable collection, you only need to provide implementations for two methods:
1.  `iterator()`
2.  `size()`
To make it modifiable, you must also override `add(E e)` (which throws `UnsupportedOperationException` by default) and ensure the iterator implements `remove()`.

### `AbstractList<E>`
Used for collections backed by a random-access data store (like an array).
*   **Unmodifiable**: Implement `get(int index)` and `size()`.
*   **Modifiable**: Implement `set(int, E)`, `add(int, E)`, and `remove(int)`.

### `AbstractSequentialList<E>`
Used for collections backed by a sequential-access data store (like a linked list).
*   You must implement the `listIterator(int index)` and `size()` methods.

### `AbstractSet<E>`
Same requirements as `AbstractCollection`. It simply enforces the `Set` contract (no duplicates) in its `equals` and `hashCode` implementations.

### `AbstractMap<K,V>`
*   You must implement the `entrySet()` method, which returns a `Set` view of the map's mappings.
*   For a modifiable map, you must also override `put(K, V)`.

## 3. Implementing a Custom Iterator
The `Iterator` is the heart of any collection. When building a custom collection, you must build a custom iterator.

```java
public class ArrayBasedList<E> extends AbstractList<E> {
    private Object[] data = new Object[10];
    private int size = 0;

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            @SuppressWarnings("unchecked")
            public E next() {
                if (cursor >= size) throw new NoSuchElementException();
                return (E) data[cursor++];
            }
        };
    }
    // ... get(), size(), add() implementations
}
```

## 4. Performance Considerations
When building custom collections, you must deeply understand Big O notation.
*   **Array-backed**: Fast `get(int)` ($O(1)$), slow `add(int)` in the middle ($O(N)$ due to shifting).
*   **Node-backed (Linked)**: Slow `get(int)` ($O(N)$), fast `add(int)` if you already have the node reference ($O(1)$).
*   **Hash-backed**: Requires careful management of the load factor and collision resolution (chaining vs. open addressing) to maintain $O(1)$ lookup times.

## 5. Fail-Fast Iterators and `modCount`
Standard Java collections use a `modCount` (modification count) integer. Every time the collection is structurally modified (an element is added or removed), `modCount` increments.
When an `Iterator` is created, it saves the current `modCount`. On every call to `next()`, it checks if its saved count matches the collection's count. If they don't match, it throws a `ConcurrentModificationException`. You must implement this logic in custom collections to prevent unpredictable behavior during concurrent modifications.