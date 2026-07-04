# Code Deep Dive: Singly Linked List

## Full Implementation

```java
public class SinglyLinkedList<E> {
    private Node<E> head;
    private Node<E> tail;
    private int size;

    private static class Node<E> {
        E data;
        Node<E> next;

        Node(E data) {
            this.data = data;
            this.next = null;
        }
    }

    public SinglyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void addFirst(E element) {
        Node<E> newNode = new Node<>(element);
        newNode.next = head;
        head = newNode;
        if (tail == null) tail = head;
        size++;
    }

    public void addLast(E element) {
        Node<E> newNode = new Node<>(element);
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    public E removeFirst() {
        if (head == null) throw new NoSuchElementException();
        E data = head.data;
        head = head.next;
        if (head == null) tail = null;
        size--;
        return data;
    }

    public boolean remove(Object o) {
        if (head == null) return false;
        if (Objects.equals(head.data, o)) {
            removeFirst();
            return true;
        }
        Node<E> current = head;
        while (current.next != null) {
            if (Objects.equals(current.next.data, o)) {
                current.next = current.next.next;
                if (current.next == null) tail = current;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public E get(int index) {
        checkIndex(index);
        Node<E> current = head;
        for (int i = 0; i < index; i++) current = current.next;
        return current.data;
    }

    public void reverse() {
        Node<E> prev = null;
        Node<E> current = head;
        tail = head;
        while (current != null) {
            Node<E> next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }
        head = prev;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
}
```

## Key Design Decisions

- **Tail pointer**: enables O(1) `addLast` — without it, adding at end is O(n)
- **Generic type E**: type-safe; use `Node<E>` inner class for clarity
- **Null-safety**: check for null references on head/tail before operations
- **`tail = head` on first insert**: maintains tail invariant on single-element list
- **Reverse by pointer manipulation**: no extra space, O(n) time
