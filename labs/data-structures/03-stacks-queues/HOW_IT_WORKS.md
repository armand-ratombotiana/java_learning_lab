# How Stacks & Queues Work

## Array-Based Stack

```java
public class ArrayStack<E> {
    private static final int DEFAULT_CAPACITY = 16;
    private E[] elements;
    private int top;  // index of next insertion

    @SuppressWarnings("unchecked")
    public ArrayStack() {
        elements = (E[]) new Object[DEFAULT_CAPACITY];
        top = 0;
    }

    public void push(E element) {
        if (top == elements.length) grow();
        elements[top++] = element;
    }

    public E pop() {
        if (top == 0) throw new EmptyStackException();
        E element = elements[--top];
        elements[top] = null;  // prevent memory leak
        return element;
    }

    public E peek() {
        if (top == 0) throw new EmptyStackException();
        return elements[top - 1];
    }
}
```

## Circular Array Queue

```java
public class CircularQueue<E> {
    private E[] elements;
    private int head, tail, size;
    private int capacity;

    @SuppressWarnings("unchecked")
    public CircularQueue(int capacity) {
        this.capacity = capacity;
        elements = (E[]) new Object[capacity];
        head = tail = size = 0;
    }

    public boolean enqueue(E element) {
        if (isFull()) return false;
        elements[tail] = element;
        tail = (tail + 1) % capacity;
        size++;
        return true;
    }

    public E dequeue() {
        if (isEmpty()) throw new NoSuchElementException();
        E element = elements[head];
        elements[head] = null;
        head = (head + 1) % capacity;
        size--;
        return element;
    }

    public boolean isEmpty() { return size == 0; }
    public boolean isFull() { return size == capacity; }
}
```

## Queue Using Two Stacks

```java
public class QueueFromStacks<E> {
    private final Stack<E> in = new Stack<>();
    private final Stack<E> out = new Stack<>();

    public void enqueue(E element) {
        in.push(element);
    }

    public E dequeue() {
        if (out.isEmpty()) {
            while (!in.isEmpty()) out.push(in.pop());
        }
        if (out.isEmpty()) throw new NoSuchElementException();
        return out.pop();
    }

    public E peek() {
        if (out.isEmpty()) {
            while (!in.isEmpty()) out.push(in.pop());
        }
        return out.peek();
    }
}
```

Each element is moved from `in` to `out` at most once, giving O(1) amortized time per operation.
