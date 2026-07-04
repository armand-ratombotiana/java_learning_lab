# Common Mistakes with Linked Lists

## NullPointerException on Tail Operations

```java
// WRONG — assumes tail is not null
void addLast(E element) {
    tail.next = new Node<>(element);  // NPE if tail is null
    tail = tail.next;
}

// CORRECT
void addLast(E element) {
    if (tail == null) {
        head = tail = new Node<>(element);
    } else {
        tail.next = new Node<>(element);
        tail = tail.next;
    }
}
```

## Forgetting to Update Tail on Removal

```java
// WRONG — tail still points to removed node
E removeLast() {
    // ... unlink last node ...
    return data;
    // tail still references old node!
}

// CORRECT
E removeLast() {
    E data = tail.data;
    // find second-to-last node
    Node<E> current = head;
    while (current.next != tail) current = current.next;
    current.next = null;
    tail = current;  // ← CRITICAL
    size--;
    return data;
}
```

## Infinite Loop in Circular List Traversal

```java
// WRONG — never terminates for circular list
void printList(Node head) {
    Node current = head;
    while (current != null) {       // never null in circular list
        System.out.println(current.data);
        current = current.next;
    }
}

// CORRECT (stop when we return to head)
void printCircular(Node head) {
    if (head == null) return;
    Node current = head;
    do {
        System.out.println(current.data);
        current = current.next;
    } while (current != head);
}
```

## Losing Reference to Rest of List

```java
// WRONG — head.next is garbage-collected
void removeHead() {
    Node newHead = head.next;
    head = newHead;
    // No one references old head — but its next pointer still works
}

// Actually this is fine for singly linked, but for doubly:
// WRONG — forget to update newHead.prev
void removeHeadDoubly() {
    head = head.next;
    // head.prev still points to old head!
}

// CORRECT
void removeHeadDoubly() {
    head = head.next;
    if (head != null) head.prev = null;
}
```

## Not Using equals() for Comparison

```java
// WRONG — reference comparison
if (current.data == searchValue) { ... }

// CORRECT (handles nulls)
if (Objects.equals(current.data, searchValue)) { ... }
```

## Concurrent Modification Without Iterator

```java
// WRONG — ConcurrentModificationException
for (String s : linkedList) {
    if (s.equals("remove")) linkedList.remove(s);
}

// CORRECT
Iterator<String> it = linkedList.iterator();
while (it.hasNext()) {
    if (it.next().equals("remove")) it.remove();
}
```
