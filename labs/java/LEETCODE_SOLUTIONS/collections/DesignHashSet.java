package collections;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * LeetCode 705 - Design HashSet
 * 
 * Design a hash set without using any built-in hash table libraries.
 * 
 * Approach 1: Boolean array (for bounded range)
 * Approach 2: Bucket array with linked list chaining (generic)
 * 
 * Time: O(1) average, O(n) worst case for approach 2
 * Space: O(capacity + elements)
 */
public class DesignHashSet {

    // Approach: Bucket with chaining
    static class MyHashSet {
        private static final int SIZE = 769; // prime to reduce collisions
        private final LinkedList<Integer>[] buckets;

        @SuppressWarnings("unchecked")
        MyHashSet() {
            buckets = new LinkedList[SIZE];
        }

        private int hash(int key) {
            return key % SIZE;
        }

        public void add(int key) {
            int idx = hash(key);
            if (buckets[idx] == null) buckets[idx] = new LinkedList<>();
            if (!buckets[idx].contains(key)) buckets[idx].add(key);
        }

        public void remove(int key) {
            int idx = hash(key);
            if (buckets[idx] == null) return;
            buckets[idx].removeFirstOccurrence(key);
        }

        public boolean contains(int key) {
            int idx = hash(key);
            if (buckets[idx] == null) return false;
            return buckets[idx].contains(key);
        }
    }

    // Approach: Boolean array (simpler, fixed range)
    static class MyHashSetBool {
        private final boolean[] arr = new boolean[1_000_001];

        public void add(int key) { arr[key] = true; }
        public void remove(int key) { arr[key] = false; }
        public boolean contains(int key) { return arr[key]; }
    }

    public static void main(String[] args) {
        MyHashSet set = new MyHashSet();
        set.add(1);
        set.add(2);
        assert set.contains(1);
        assert set.contains(2);
        assert !set.contains(3);
        set.remove(2);
        assert !set.contains(2);
        assert set.contains(1);
        set.add(1000000);
        assert set.contains(1000000);

        MyHashSetBool set2 = new MyHashSetBool();
        set2.add(5);
        assert set2.contains(5);
        set2.remove(5);
        assert !set2.contains(5);

        System.out.println("All HashSet tests passed.");
    }
}