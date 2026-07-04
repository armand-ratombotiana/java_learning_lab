# Refactoring Linked Lists

## Replace Raw Node Manipulation with Standard Library

```java
// Before — manual implementation used everywhere
class TaskNode {
    TaskNode next;
    Runnable task;
}
TaskNode head = null;
// ... manual add, remove, traverse ...

// After
Deque<Runnable> taskQueue = new LinkedList<>();
taskQueue.addLast(task);
```

## Encapsulate Remove Pattern

```java
// Before — inline
if (current.next.data.equals(target)) {
    current.next = current.next.next;
}

// After
public boolean remove(Object o) {
    Node<E> current = head;
    while (current.next != null) {
        if (Objects.equals(current.next.data, o)) {
            current.next = current.next.next;
            size--;
            return true;
        }
        current = current.next;
    }
    return false;
}
```

## Use Sentinel Nodes

```java
// Before — null checks everywhere
void add(E e) {
    if (head == null) {
        head = new Node<>(e);
    } else {
        Node last = getLast();
        last.next = new Node<>(e);
    }
}

// After — sentinel simplifies
void add(E e) {
    Node newNode = new Node<>(e);
    sentinel.prev.next = newNode;
    newNode.prev = sentinel.prev;
    newNode.next = sentinel;
    sentinel.prev = newNode;
}
```

## Extract Generic Interface

```java
// Before — concrete class
SinglyLinkedList<String> list = new SinglyLinkedList<>();

// After — program to interface
List<String> list = new LinkedList<>();
// or
Deque<String> deque = new LinkedList<>();
```

## Replace Recursion with Iteration

```java
// Before — recursion (risk of StackOverflow)
int size(Node n) {
    return n == null ? 0 : 1 + size(n.next);
}

// After — iteration
int size() {
    int count = 0;
    for (Node n = head; n != null; n = n.next) count++;
    return count;
}
```
