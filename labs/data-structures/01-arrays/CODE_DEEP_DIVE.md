# Code Deep Dive: Arrays in Java

## Custom Dynamic Array Implementation

```java
public class DynamicArray<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elements;
    private int size;

    public DynamicArray() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Capacity: " + initialCapacity);
        }
        this.elements = new Object[initialCapacity];
        this.size = 0;
    }

    public void add(E element) {
        if (size == elements.length) {
            grow();
        }
        elements[size++] = element;
    }

    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        if (size == elements.length) {
            grow();
        }
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = element;
        size++;
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        return (E) elements[index];
    }

    @SuppressWarnings("unchecked")
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        E oldValue = (E) elements[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elements, index + 1, elements, index, numMoved);
        }
        elements[--size] = null;  // allow GC
        return oldValue;
    }

    private void grow() {
        int oldCapacity = elements.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);  // 1.5x
        elements = Arrays.copyOf(elements, newCapacity);
    }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }
}
```

## Key Points

- **`System.arraycopy`** is a native intrinsic — always prefer it over manual loops
- **Nulling removed elements** (`elements[--size] = null`) prevents memory leaks in generic arrays
- **1.5x growth** is a compromise: 2x wastes memory, 1.25x copies too frequently
- **Type safety** via `@SuppressWarnings("unchecked")` — the array is `Object[]`, cast on get
