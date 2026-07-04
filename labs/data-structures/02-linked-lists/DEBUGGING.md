# Debugging Linked Lists

## Common Issues

| Symptom | Likely Cause |
|---------|-------------|
| Infinite loop in traversal | Cycle in list (self-loop or tail→head) |
| NullPointerException | Head/tail not updated after removal |
| Missing elements after deletion | Forgot to update prev/next references |
| `ConcurrentModificationException` | Modified list during iteration |

## Debugging Techniques

### Print the List

```java
void printList(Node head) {
    Node current = head;
    int count = 0;
    while (current != null) {
        System.out.println("[" + count + "]: " + current.data);
        current = current.next;
        if (count++ > 100) {  // safety for cycles
            System.out.println("… (cycle detected)");
            break;
        }
    }
}
```

### Verify Invariants

```java
boolean isValid() {
    if (head == null && tail == null) return size == 0;
    if (head == null || tail == null) return false;
    // Find tail by traversal
    Node<E> current = head;
    while (current.next != null) current = current.next;
    return current == tail;
}
```

### Cycle Detection

```java
// Floyd's Tortoise and Hare
boolean hasCycle(Node head) {
    Node slow = head;
    Node fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) return true;
    }
    return false;
}
```

### Unit Testing

```java
@Test
void addLastThenGet() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    list.addLast(1);
    list.addLast(2);
    list.addLast(3);
    assertEquals(3, list.size());
    assertEquals(Integer.valueOf(1), list.get(0));
    assertEquals(Integer.valueOf(3), list.get(2));
}

@Test
void removeFromMiddle() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    list.addLast(1); list.addLast(2); list.addLast(3);
    assertTrue(list.remove(2));
    assertEquals(2, list.size());
    assertEquals(Integer.valueOf(1), list.get(0));
    assertEquals(Integer.valueOf(3), list.get(1));
}

@Test
void reverseList() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    list.addLast(1); list.addLast(2); list.addLast(3);
    list.reverse();
    assertEquals(Integer.valueOf(3), list.get(0));
    assertEquals(Integer.valueOf(1), list.get(2));
}
```
