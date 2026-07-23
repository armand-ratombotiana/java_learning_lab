package collectionsdeep;

import java.util.*;

/**
 * Custom HashMap implementation with collision handling and resize.
 * 
 * Demonstrates: hash calculation, separate chaining, load factor, dynamic resizing.
 * 
 * Time: O(1) average, O(n) worst case
 * Space: O(capacity + entries)
 */
public class HashMapInternal<K, V> {
    private static final int INITIAL_CAP = 16;
    private static final double LOAD_FACTOR = 0.75;
    
    @SuppressWarnings("unchecked")
    private Node<K, V>[] table = new Node[INITIAL_CAP];
    private int size = 0;

    private static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;
        Node(K k, V v) { key = k; value = v; }
    }

    private int index(K key) {
        int h = key.hashCode();
        return (h ^ (h >>> 16)) & (table.length - 1);
    }

    public void put(K key, V value) {
        int i = index(key);
        Node<K, V> head = table[i];
        if (head == null) {
            table[i] = new Node<>(key, value);
            size++;
        } else {
            Node<K, V> cur = head;
            while (true) {
                if (Objects.equals(cur.key, key)) { cur.value = value; return; }
                if (cur.next == null) break;
                cur = cur.next;
            }
            cur.next = new Node<>(key, value);
            size++;
        }
        if ((double) size / table.length > LOAD_FACTOR) resize();
    }

    public V get(K key) {
        Node<K, V> cur = table[index(key)];
        while (cur != null) {
            if (Objects.equals(cur.key, key)) return cur.value;
            cur = cur.next;
        }
        return null;
    }

    public V remove(K key) {
        int i = index(key);
        Node<K, V> cur = table[i], prev = null;
        while (cur != null) {
            if (Objects.equals(cur.key, key)) {
                if (prev == null) table[i] = cur.next;
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
        Node<K, V>[] old = table;
        table = new Node[old.length * 2];
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
        HashMapInternal<String, Integer> m = new HashMapInternal<>();
        for (int i = 0; i < 100; i++) m.put("k" + i, i);
        assert m.size() == 100;
        assert m.get("k50") == 50;
        assert m.get("missing") == null;
        m.put("k50", 500);
        assert m.get("k50") == 500;
        m.remove("k50");
        assert m.get("k50") == null;
        assert m.size() == 99;
        System.out.println("All HashMapInternal (deep) tests passed.");
    }
}