package collections;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LeetCode 146 - LRU Cache
 * 
 * Design a data structure that follows the Least Recently Used (LRU) cache constraint.
 * 
 * Approach 1: LinkedHashMap (override removeEldestEntry) — simple and built-in
 * Approach 2: HashMap + DoublyLinkedList — for full control
 * 
 * Time: O(1) for get and put
 * Space: O(capacity)
 */
public class LRUCache {

    // Approach 1: Using LinkedHashMap (simplest)
    static class LRUCacheLHM<K, V> extends LinkedHashMap<K, V> {
        private final int maxCapacity;

        LRUCacheLHM(int capacity) {
            super(capacity, 0.75f, true); // access-order = true
            this.maxCapacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > maxCapacity;
        }

        public V get(Object key) { return super.get(key); }
        public void put(K key, V value) { super.put(key, value); }
    }

    // Approach 2: Manual HashMap + DoublyLinkedList for control
    static class LRUCacheManual {
        private final Map<Integer, Node> map = new java.util.HashMap<>();
        private final int capacity;
        private final Node head = new Node(0, 0);
        private final Node tail = new Node(0, 0);

        private static class Node {
            int key, value;
            Node prev, next;
            Node(int k, int v) { key = k; value = v; }
        }

        LRUCacheManual(int capacity) {
            this.capacity = capacity;
            head.next = tail;
            tail.prev = head;
        }

        public int get(int key) {
            Node node = map.get(key);
            if (node == null) return -1;
            remove(node);
            insert(node);
            return node.value;
        }

        public void put(int key, int value) {
            if (map.containsKey(key)) {
                remove(map.get(key));
            } else if (map.size() == capacity) {
                Node lru = tail.prev;
                remove(lru);
                map.remove(lru.key);
            }
            Node node = new Node(key, value);
            map.put(key, node);
            insert(node);
        }

        private void insert(Node node) {
            Node next = head.next;
            head.next = node;
            node.prev = head;
            node.next = next;
            next.prev = node;
        }

        private void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    public static void main(String[] args) {
        // Test LinkedHashMap approach
        LRUCacheLHM<String, Integer> cache = new LRUCacheLHM<>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        assert cache.get("a") == 1;
        cache.put("c", 3); // evicts "b"
        assert cache.get("b") == null;
        assert cache.get("c") == 3;
        assert cache.get("a") == 1;

        // Test Manual approach
        LRUCacheManual cache2 = new LRUCacheManual(2);
        cache2.put(1, 1);
        cache2.put(2, 2);
        assert cache2.get(1) == 1;
        cache2.put(3, 3);
        assert cache2.get(2) == -1;
        cache2.put(4, 4);
        assert cache2.get(1) == -1;
        assert cache2.get(3) == 3;
        assert cache2.get(4) == 4;

        System.out.println("All LRUCache tests passed.");
    }
}