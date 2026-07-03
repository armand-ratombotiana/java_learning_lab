# Edge Cases & Pitfalls: Custom Collections

Building custom collections exposes you to low-level memory and algorithmic bugs that are normally handled by the JDK.

## 1. The Memory Leak (Lingering References)
*   **The Scenario**: You build a custom array-backed `List`. You implement the `remove(int index)` method by shifting all elements to the left to fill the gap, and decrementing the `size` variable.
*   **The Pitfall**: You shifted the elements, but you left the last element in the array pointing to the object that was just "removed". Because the array still holds a reference to it, the Garbage Collector cannot reclaim that object's memory, even though the `size` variable says it's no longer in the list.
*   **Mitigation**: Always explicitly `null` out references when an element is removed from an array-backed collection.
    ```java
    // Inside remove(int index)
    System.arraycopy(data, index + 1, data, index, size - index - 1);
    data[--size] = null; // CRITICAL: Clear to let GC do its work
    ```

## 2. Breaking the `equals()` and `hashCode()` Contract
*   **The Scenario**: You build a custom `Set` implementation, but you forget to override `equals()` and `hashCode()`, or you implement them incorrectly.
*   **The Pitfall**: The Java Collections Framework relies heavily on these methods. If two `Set`s contain the exact same elements but your custom `Set` doesn't evaluate to `true` when compared to a `HashSet` using `equals()`, you break the `Set` contract. Furthermore, if you put your custom collection inside a `HashMap` as a key, it will be lost if `hashCode()` isn't implemented based on the elements.
*   **Mitigation**: Always extend `AbstractSet`, `AbstractList`, or `AbstractMap`. These base classes provide robust, specification-compliant implementations of `equals()` and `hashCode()` out of the box.

## 3. Ignoring the Fail-Fast Contract
*   **The Scenario**: You write a custom iterator for your collection. You do not implement `modCount` tracking.
*   **The Pitfall**: Thread A is iterating over your collection. Thread B adds an element to the collection, causing the underlying array to resize and shift. Thread A's iterator continues running, but its internal cursor is now pointing to the wrong data, leading to silent data corruption, skipped elements, or `ArrayIndexOutOfBoundsException`.
*   **Mitigation**: Implement `modCount`. In your `add/remove` methods, increment `modCount`. In your Iterator's constructor, save `expectedModCount = modCount`. In every call to `next()`, check `if (modCount != expectedModCount) throw new ConcurrentModificationException();`.

## 4. Unsafe Array Casting
*   **The Scenario**: You need an internal array to store generic types: `E[] data`.
*   **The Pitfall**: Java does not allow generic array creation (`new E[10]`). If you create an `Object[]` and cast it (`(E[]) new Object[10]`), the compiler gives an unchecked cast warning. If you ever expose this array to the outside world, it will cause a `ClassCastException`.
*   **Mitigation**: Keep the array as `Object[]` internally. Only cast individual elements when they are retrieved from the array and returned to the caller (`return (E) data[index]`). Never return the internal array itself.

## 5. Violating Collection Interface Contracts
*   **The Scenario**: You implement `List.add(E e)` but you decide that your custom list shouldn't accept nulls, so you silently ignore nulls: `if (e == null) return false;`.
*   **The Pitfall**: The `List` interface contract states that if a collection refuses to add an element for any reason other than it being a duplicate, it *must* throw an exception (e.g., `NullPointerException` or `IllegalArgumentException`). Silently failing breaks the expectations of any client code using the `List` interface.
*   **Mitigation**: Read the Javadoc for the interface methods carefully. Adhere strictly to the specified return values and exception types.