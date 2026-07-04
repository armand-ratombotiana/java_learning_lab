# How Linked Lists Work

## Node Structure

Every linked list is built from nodes. A node is an object with:

1. **Data**: the stored value (can be primitive, object, or null)
2. **Pointer(s)**: references to neighboring nodes

```java
// Singly linked node
class Node<E> {
    E data;
    Node<E> next;

    Node(E data) {
        this.data = data;
        this.next = null;
    }
}

// Doubly linked node
class Node<E> {
    E data;
    Node<E> prev;
    Node<E> next;

    Node(E data) {
        this.data = data;
        this.prev = null;
        this.next = null;
    }
}
```

## Insertion Operations

### Insert at Head (Singly Linked)
```java
// O(1)
void addFirst(E element) {
    Node<E> newNode = new Node<>(element);
    newNode.next = head;
    head = newNode;
    if (tail == null) tail = head;
    size++;
}
```

### Insert at Tail (Singly Linked with tail pointer)
```java
// O(1) with tail pointer
void addLast(E element) {
    Node<E> newNode = new Node<>(element);
    if (tail == null) {
        head = tail = newNode;
    } else {
        tail.next = newNode;
        tail = newNode;
    }
    size++;
}
```

### Insert After a Known Node (Doubly Linked)
```java
// O(1)
void insertAfter(Node<E> node, E element) {
    Node<E> newNode = new Node<>(element);
    newNode.prev = node;
    newNode.next = node.next;
    if (node.next != null) {
        node.next.prev = newNode;
    } else {
        tail = newNode;
    }
    node.next = newNode;
    size++;
}
```

## Deletion Operations

### Delete Head (Singly Linked)
```java
// O(1)
E removeFirst() {
    if (head == null) throw new NoSuchElementException();
    E data = head.data;
    head = head.next;
    if (head == null) tail = null;
    size--;
    return data;
}
```

### Delete Known Node (Doubly Linked)
```java
// O(1)
void removeNode(Node<E> node) {
    if (node.prev != null) node.prev.next = node.next;
    else head = node.next;
    if (node.next != null) node.next.prev = node.prev;
    else tail = node.prev;
    size--;
}
```

## Traversal

```java
// Iterative traversal — O(n)
Node<E> current = head;
while (current != null) {
    process(current.data);
    current = current.next;
}

// Recursive traversal
void traverse(Node<E> node) {
    if (node == null) return;
    process(node.data);
    traverse(node.next);
}
```

## Cycle Detection (Floyd's Algorithm)

```java
boolean hasCycle(Node head) {
    Node slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) return true;
    }
    return false;
}
```
