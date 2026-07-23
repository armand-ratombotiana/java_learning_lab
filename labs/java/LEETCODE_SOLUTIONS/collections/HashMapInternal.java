package collections;

/**
 * Custom HashMap implementation — demonstrates internal mechanics
 * 
 * Features: put, get, remove, resize, hash collision handling via linked list
 * 
 * Approach 1: Separate chaining with linked list
 * Approach 2: Could use tree (like Java 8+) for O(log n) worst case
 * 
 * Time: O(1) average, O(n) worst case per operation
 * Space: O(capacity + entries)
 */
public class HashMapInternal<K, V> {
    private static final int INITIAL_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;
    
    @SuppressWarnings("unchecked")
    private Node<K, V>[] buckets = new Node[INITIAL_CAPACITY];
    private int size = 0;

    private static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;
        Node(K key, V value) { this.key = key; this.value = value; }
    }

    private int hash(K key) {
        int h = key.hashCode();
        return (h ^ (h >>> 16)) & (buckets.length - 1);
    }

    public void put(K key, V value) {
        int idx = hash(key);
        Node<K, V> head = buckets[idx];
        if (head == null) {
            buckets[idx] = new Node<>(key, value);
            size++;
        } else {
            Node<K, V> cur = head;
            while (true) {
                if (cur.key.equals(key)) { cur.value = value; return; }
                if (cur.next == null) break;
                cur = cur.next;
            }
            cur.next = new Node<>(key, value);
            size++;
        }
        if ((double) size / buckets.length > LOAD_FACTOR) resize();
    }

    public V get(K key) {
        int idx = hash(key);
        Node<K, V> cur = buckets[idx];
        while (cur != null) {
            if (cur.key.equals(key)) return cur.value;
            cur = cur.next;
        }
        return null;
    }

    public V remove(K key) {
        int idx = hash(key);
        Node<K, V> cur = buckets[idx];
        Node<K, V> prev = null;
        while (cur != null) {
            if (cur.key.equals(key)) {
                if (prev == null) buckets[idx] = cur.next;
                else prev.next = cur.next;
                size--;
                return cur.value;
            }
            prev = cur;
            cur = cur.next;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] old = buckets;
        buckets = new Node[old.length * 2];
        size = 0;
        for (Node<K, V> head : old) {
            Node<K, V> cur = head;
            while (cur != null) {
                put(cur.key, cur.value);
                cur = cur.next;
            }
        }
    }

    public int size() { return size; }

    public static void main(String[] args) {
        HashMapInternal<String, Integer> map = new HashMapInternal<>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        assert map.get("one") == 1;
        assert map.get("two") == 2;
        assert map.get("three") == 3;
        assert map.get("missing") == null;
        assert map.size() == 3;
        map.put("one", 100);
        assert map.get("one") == 100;
        assert map.size() == 3;
        map.remove("two");
        assert map.get("two") == null;
        assert map.size() == 2;
        System.out.println("All HashMapInternal tests passed.");
    }
}