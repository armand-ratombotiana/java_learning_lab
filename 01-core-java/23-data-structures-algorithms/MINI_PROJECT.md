# Module 23: Data Structures & Algorithms - Mini Project

**Project Name**: Custom HashMap Implementation  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Understand the internal workings of Hash Tables by implementing a custom, generic `HashMap` from scratch, handling hash code generation, bucket arrays, and collision resolution via Linked Lists (Chaining).

## 📝 Requirements

### Core Features
1. **The Entry Node**:
   - Create a generic static inner class `Node<K, V>`.
   - It should contain fields for `K key`, `V value`, `int hash`, and a reference to the `Node<K, V> next` node (for chaining).

2. **The Hash Table**:
   - Create a class `CustomHashMap<K, V>`.
   - It should be backed by an array of `Node<K, V>`: `private Node<K,V>[] table;`.
   - Define a default initial capacity (e.g., 16) and a load factor (e.g., 0.75).
   - Keep track of the total number of items stored (`int size`).

3. **Core Methods**:
   - `public void put(K key, V value)`:
     - Calculate the hash using `Math.abs(key.hashCode()) % table.length`.
     - Go to that index in the array.
     - If it's empty, create a new Node and place it there.
     - If it's not empty (a collision), traverse the Linked List. If the key already exists, update its value. If you reach the end of the list, append a new Node.
   - `public V get(K key)`:
     - Calculate the hash to find the bucket.
     - Traverse the Linked List at that bucket. If you find the key (using `.equals()`), return the value. If not found, return `null`.
   - `public boolean remove(K key)`:
     - Find the bucket. Traverse the list. If the key is found, update the `next` pointers to remove the node from the list. Decrement `size`.

4. **Dynamic Resizing (Bonus)**:
   - Inside the `put` method, check if `(float) size / table.length > loadFactor`.
   - If true, double the size of the array. You must re-hash and re-insert every existing node into the new array because the array length (which determines the index) has changed.

---

## 💡 Solution Blueprint (Partial)

```java
public class CustomHashMap<K, V> {
    private static final int INITIAL_CAPACITY = 16;
    private Node<K, V>[] table;
    private int size = 0;

    public CustomHashMap() {
        table = new Node[INITIAL_CAPACITY];
    }

    static class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    public void put(K key, V value) {
        if (key == null) return; // Simplified: No null keys for now
        int hash = Math.abs(key.hashCode());
        int index = hash % table.length;

        Node<K, V> newNode = new Node<>(hash, key, value, null);
        if (table[index] == null) {
            table[index] = newNode;
        } else {
            Node<K, V> current = table[index];
            while (true) {
                if (current.key.equals(key)) {
                    current.value = value; // Update existing
                    return;
                }
                if (current.next == null) break;
                current = current.next;
            }
            current.next = newNode; // Append to end of chain
        }
        size++;
    }
}
```