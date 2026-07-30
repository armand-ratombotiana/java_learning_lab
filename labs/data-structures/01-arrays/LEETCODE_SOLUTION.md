# Dynamic Array (ArrayList from Scratch)

**Problem:** Design and implement a dynamic array (similar to `java.util.ArrayList`) that grows automatically when full. Support the following operations:

- `add(value)` — append an element
- `get(index)` — retrieve element at index
- `set(index, value)` — update element at index
- `remove(index)` — delete element at index, shift left
- `size()` — return current number of elements
- `isEmpty()` — check if empty
- `contains(value)` — check if value exists
- `indexOf(value)` — return first index of value or -1
- `clear()` — remove all elements
- `trimToSize()` — shrink capacity to current size

## Java Solution

```java
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A dynamic array (resizable-array) implementation similar to {@code java.util.ArrayList}.
 * Provides amortized O(1) add, O(1) amortized remove from end, O(n) remove from
 * arbitrary position, and O(1) get/set.
 *
 * <p>This implementation uses a backing Object[] array that doubles when full and
 * shrinks by half when only 25% full (lazy shrinking strategy).</p>
 *
 * <h2>Complexity Analysis</h2>
 * <ul>
 *   <li><b>add(value)</b> — O(1) amortized, O(n) worst-case when resizing</li>
 *   <li><b>get(index)</b> — O(1)</li>
 *   <li><b>set(index, value)</b> — O(1)</li>
 *   <li><b>remove(index)</b> — O(n) due to shifting elements</li>
 *   <li><b>size()</b> — O(1)</li>
 *   <li><b>isEmpty()</b> — O(1)</li>
 *   <li><b>contains(value)</b> — O(n)</li>
 *   <li><b>indexOf(value)</b> — O(n)</li>
 *   <li><b>clear()</b> — O(n)</li>
 *   <li><b>trimToSize()</b> — O(n)</li>
 * </ul>
 *
 * <b>Space:</b> O(n)
 *
 * @param <E> the type of elements in this list
 */
public class DynamicArray<E> implements Iterable<E> {

    private static final int DEFAULT_CAPACITY = 10;
    private static final int SHRINK_THRESHOLD = 4; // ratio capacity / size

    private Object[] data;
    private int count;

    /** Constructs an empty dynamic array with default capacity of 10. */
    public DynamicArray() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Constructs an empty dynamic array with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the array
     * @throws IllegalArgumentException if initialCapacity is negative
     */
    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal capacity: " + initialCapacity);
        }
        this.data = new Object[initialCapacity];
        this.count = 0;
    }

    // ---- Core operations ----

    /**
     * Appends the specified element to the end of this list.
     *
     * @param element element to be appended
     * @return {@code true} (as specified by {@link java.util.Collection#add})
     */
    public boolean add(E element) {
        if (count == data.length) {
            resize(data.length * 2);
        }
        data[count++] = element;
        return true;
    }

    /**
     * Returns the element at the specified position.
     *
     * @param index index of the element to return
     * @return the element at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @SuppressWarnings("unchecked")
    public E get(int index) {
        checkIndex(index);
        return (E) data[index];
    }

    /**
     * Replaces the element at the specified position with the specified element.
     *
     * @param index   index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @SuppressWarnings("unchecked")
    public E set(int index, E element) {
        checkIndex(index);
        E old = (E) data[index];
        data[index] = element;
        return old;
    }

    /**
     * Removes the element at the specified position, shifting any subsequent
     * elements to the left.
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        checkIndex(index);
        E old = (E) data[index];
        int numMoved = count - index - 1;
        if (numMoved > 0) {
            System.arraycopy(data, index + 1, data, index, numMoved);
        }
        data[--count] = null; // clear for GC
        shrinkIfNeeded();
        return old;
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements
     */
    public int size() {
        return count;
    }

    /**
     * Returns {@code true} if this list contains no elements.
     *
     * @return {@code true} if this list is empty
     */
    public boolean isEmpty() {
        return count == 0;
    }

    /**
     * Returns {@code true} if this list contains the specified element.
     *
     * @param element element whose presence in this list is to be tested
     * @return {@code true} if this list contains the specified element
     */
    public boolean contains(Object element) {
        return indexOf(element) >= 0;
    }

    /**
     * Returns the index of the first occurrence of the specified element,
     * or -1 if this list does not contain the element.
     *
     * @param element element to search for
     * @return the index of the first occurrence, or -1
     */
    public int indexOf(Object element) {
        for (int i = 0; i < count; i++) {
            if (Objects.equals(data[i], element)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Removes all elements from this list. The array capacity is preserved.
     */
    public void clear() {
        for (int i = 0; i < count; i++) {
            data[i] = null;
        }
        count = 0;
    }

    /**
     * Trims the capacity of this dynamic array instance to be the current size.
     */
    public void trimToSize() {
        if (count < data.length) {
            resize(data.length == 0 ? DEFAULT_CAPACITY : count);
        }
    }

    // ---- Internal helpers ----

    private void checkIndex(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + count);
        }
    }

    private void resize(int newCapacity) {
        if (newCapacity < count) {
            newCapacity = count;
        }
        Object[] newData = new Object[newCapacity];
        System.arraycopy(data, 0, newData, 0, count);
        data = newData;
    }

    private void shrinkIfNeeded() {
        if (count > 0 && count <= data.length / SHRINK_THRESHOLD && data.length > DEFAULT_CAPACITY) {
            resize(data.length / 2);
        }
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * @return an iterator
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private int cursor = 0;
            @Override
            public boolean hasNext() {
                return cursor < count;
            }
            @Override
            @SuppressWarnings("unchecked")
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                return (E) data[cursor++];
            }
        };
    }

    /**
     * Returns a sequential {@code Stream} over the elements in this list.
     *
     * @return a sequential stream
     */
    public Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < count; i++) {
            sb.append(data[i]);
            if (i < count - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
```

## Test Cases

```java
/**
 * Unit tests for DynamicArray.
 */
public class DynamicArrayTest {

    public static void main(String[] args) {
        // --- Test 1: add and get ---
        DynamicArray<Integer> arr = new DynamicArray<>();
        assert arr.isEmpty() : "should be empty initially";
        for (int i = 1; i <= 20; i++) {
            arr.add(i);
        }
        assert arr.size() == 20 : "size should be 20";
        assert arr.get(0) == 1 : "first element should be 1";
        assert arr.get(19) == 20 : "last element should be 20";

        // --- Test 2: set ---
        arr.set(0, 100);
        assert arr.get(0) == 100 : "after set, index 0 should be 100";

        // --- Test 3: remove from middle ---
        DynamicArray<Integer> arr2 = new DynamicArray<>();
        for (int i = 1; i <= 5; i++) arr2.add(i);
        int removed = arr2.remove(2); // remove 3
        assert removed == 3 : "removed element should be 3";
        assert arr2.size() == 4 : "size should now be 4";
        assert arr2.get(2) == 4 : "element at index 2 should now be 4";

        // --- Test 4: remove from end ---
        DynamicArray<Integer> arr3 = new DynamicArray<>();
        for (int i = 1; i <= 3; i++) arr3.add(i);
        int last = arr3.remove(2);
        assert last == 3 : "removed last element should be 3";

        // --- Test 5: remove from front ---
        DynamicArray<Integer> arr4 = new DynamicArray<>();
        for (int i = 1; i <= 3; i++) arr4.add(i);
        int first = arr4.remove(0);
        assert first == 1 : "removed first element should be 1";
        assert arr4.get(0) == 2 : "new first should be 2";

        // --- Test 6: contains and indexOf ---
        DynamicArray<String> strs = new DynamicArray<>();
        strs.add("apple"); strs.add("banana"); strs.add("cherry");
        assert strs.contains("banana") : "should contain banana";
        assert !strs.contains("grape") : "should not contain grape";
        assert strs.indexOf("banana") == 1 : "banana should be at index 1";
        assert strs.indexOf("grape") == -1 : "grape should not be found";

        // --- Test 7: clear ---
        strs.clear();
        assert strs.isEmpty() : "should be empty after clear";
        assert strs.size() == 0 : "size should be 0";

        // --- Test 8: trimToSize ---
        DynamicArray<Integer> arr5 = new DynamicArray<>(100);
        for (int i = 0; i < 10; i++) arr5.add(i);
        arr5.trimToSize();
        // capacity after trim should be 10; verify by adding more elements
        for (int i = 10; i < 20; i++) arr5.add(i);
        assert arr5.size() == 20 : "size should be 20 after adding more";

        // --- Test 9: iterator ---
        DynamicArray<Integer> arr6 = new DynamicArray<>();
        for (int i = 1; i <= 3; i++) arr6.add(i);
        int sum = 0;
        for (int val : arr6) sum += val;
        assert sum == 6 : "sum of 1+2+3 should be 6";

        // --- Test 10: exception on out-of-bounds ---
        try {
            arr.get(100);
            assert false : "should have thrown IndexOutOfBoundsException";
        } catch (IndexOutOfBoundsException e) { /* expected */ }

        System.out.println("All DynamicArray tests passed!");
    }
}
```

## Amortized Analysis

The **amortized cost** of an `add` operation is **O(1)** even though an individual add may trigger an O(n) resize. Using the **accounting method**:

- Assign a "coin" of 3 units per add.
- 1 unit pays for the immediate insertion.
- 2 units are saved for future resizing.

When the array grows from size n to 2n:
- The n saved coins (from the last n insertions) pay for copying n elements.
- After the resize, the array has 2n capacity with n elements.

This gives a **total cost of O(n) for n insertions**, i.e., **O(1) amortized per insertion**.

The shrink strategy ensures memory is not wasted: when the array is only 25% full, it halves the capacity. This guarantees that the load factor stays between 25% and 100%, so wasted space is at most O(n).
