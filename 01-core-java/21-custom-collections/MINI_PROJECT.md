# Mini Project: Custom Ring Buffer (Circular Queue)

## Objective
Build a custom, modifiable collection by extending `AbstractCollection`. You will implement a Ring Buffer (Circular Queue) that has a fixed capacity. When the buffer is full, adding a new element automatically overwrites the oldest element.

## Prerequisites
*   Java 17+

## Step 1: The Class Skeleton
Extend `AbstractCollection` and define the internal state. We use an `Object[]` array because generic arrays are not allowed in Java.

```java
import java.util.AbstractCollection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RingBuffer<E> extends AbstractCollection<E> {
    private final Object[] elements;
    private int head = 0; // Points to the oldest element
    private int tail = 0; // Points to the next insertion spot
    private int size = 0;
    private int modCount = 0; // For fail-fast iterator

    public RingBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be > 0");
        this.elements = new Object[capacity];
    }

    @Override
    public int size() {
        return size;
    }
}
```

## Step 2: Implement the `add` Method
Override the `add` method to implement the circular overwrite logic.

```java
    @Override
    public boolean add(E e) {
        elements[tail] = e;
        tail = (tail + 1) % elements.length; // Wrap around
        
        if (size < elements.length) {
            size++;
        } else {
            // Buffer is full, head must move forward to overwrite the oldest
            head = (head + 1) % elements.length;
        }
        
        modCount++; // Structural modification!
        return true;
    }
```

## Step 3: Implement the Custom Iterator
The iterator is the hardest part. It must traverse from `head` to `tail` (handling wrap-around) and enforce the fail-fast contract using `modCount`.

```java
    @Override
    public Iterator<E> iterator() {
        return new RingBufferIterator();
    }

    private class RingBufferIterator implements Iterator<E> {
        private int cursor = head;
        private int elementsReturned = 0;
        private final int expectedModCount = modCount; // Snapshot of modCount

        @Override
        public boolean hasNext() {
            return elementsReturned < size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            // 1. Fail-Fast Check
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException("Buffer modified during iteration");
            }
            
            // 2. Bounds Check
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            // 3. Retrieve and advance
            E element = (E) elements[cursor];
            cursor = (cursor + 1) % elements.length; // Wrap around
            elementsReturned++;
            
            return element;
        }
        
        // Optional: Implement remove() if desired, but throw exception for simplicity here
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove not supported by this iterator");
        }
    }
```

## Step 4: Test the Ring Buffer
Create a `Main` class to test the overwriting behavior and the fail-fast iterator.

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("--- Testing Ring Buffer ---");
        RingBuffer<String> buffer = new RingBuffer<>(3);

        buffer.add("A");
        buffer.add("B");
        buffer.add("C");
        System.out.println("Size: " + buffer.size() + ", Elements: " + buffer); 
        // Expected: [A, B, C]

        buffer.add("D"); // Should overwrite 'A'
        System.out.println("Size: " + buffer.size() + ", Elements: " + buffer); 
        // Expected: [B, C, D]

        buffer.add("E"); // Should overwrite 'B'
        System.out.println("Size: " + buffer.size() + ", Elements: " + buffer); 
        // Expected: [C, D, E]

        System.out.println("\n--- Testing Fail-Fast Iterator ---");
        try {
            for (String s : buffer) {
                System.out.println("Iterating: " + s);
                if (s.equals("D")) {
                    buffer.add("F"); // Modifying collection while iterating!
                }
            }
        } catch (java.util.ConcurrentModificationException e) {
            System.out.println("Caught ConcurrentModificationException! Fail-fast works.");
        }
    }
}
```

## Expected Output
```text
--- Testing Ring Buffer ---
Size: 3, Elements: [A, B, C]
Size: 3, Elements: [B, C, D]
Size: 3, Elements: [C, D, E]

--- Testing Fail-Fast Iterator ---
Iterating: C
Iterating: D
Caught ConcurrentModificationException! Fail-fast works.
```