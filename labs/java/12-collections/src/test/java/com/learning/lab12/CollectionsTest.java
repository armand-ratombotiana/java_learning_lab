package com.learning.lab12;

import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class CollectionsTest {

    @Test
    @DisplayName("ArrayList stores and retrieves elements by index")
    void arrayListGetByIndex() {
        List<String> list = new ArrayList<>(List.of("A", "B", "C"));
        assertEquals("B", list.get(1));
    }

    @Test
    @DisplayName("ArrayList remove reduces size")
    void arrayListRemove() {
        List<String> list = new ArrayList<>(List.of("A", "B", "C"));
        list.remove(0);
        assertEquals(2, list.size());
        assertEquals("B", list.get(0));
    }

    @Test
    @DisplayName("LinkedList supports addFirst/addLast")
    void linkedListAddFirstLast() {
        LinkedList<Integer> list = new LinkedList<>();
        list.add(10);
        list.addFirst(5);
        list.addLast(15);
        assertEquals(5, list.getFirst());
        assertEquals(15, list.getLast());
    }

    @Test
    @DisplayName("HashSet eliminates duplicates")
    void hashSetNoDuplicates() {
        Set<String> set = new HashSet<>();
        set.add("X");
        set.add("Y");
        set.add("X");
        assertEquals(2, set.size());
    }

    @Test
    @DisplayName("TreeSet maintains sorted order")
    void treeSetSortedOrder() {
        TreeSet<Integer> set = new TreeSet<>(Set.of(3, 1, 4, 1, 5));
        assertEquals(1, set.first());
        assertEquals(5, set.last());
    }

    @Test
    @DisplayName("HashMap stores key-value pairs")
    void hashMapPutGet() {
        Map<String, Integer> map = new HashMap<>();
        map.put("Alice", 30);
        map.put("Bob", 25);
        assertEquals(30, map.get("Alice"));
        assertTrue(map.containsKey("Bob"));
    }

    @Test
    @DisplayName("HashMap computeIfAbsent adds missing key")
    void hashMapComputeIfAbsent() {
        Map<String, Integer> map = new HashMap<>(Map.of("A", 1));
        map.computeIfAbsent("B", k -> 2);
        map.computeIfAbsent("A", k -> 99);
        assertEquals(1, map.get("A"));
        assertEquals(2, map.get("B"));
    }

    @Test
    @DisplayName("TreeMap maintains key order")
    void treeMapKeyOrder() {
        Map<String, Integer> map = new TreeMap<>();
        map.put("Charlie", 30);
        map.put("Alice", 25);
        map.put("Bob", 35);
        List<String> keys = new ArrayList<>(map.keySet());
        assertEquals(List.of("Alice", "Bob", "Charlie"), keys);
    }

    @Test
    @DisplayName("Queue follows FIFO order")
    void queueFifoOrder() {
        Queue<String> q = new LinkedList<>();
        q.offer("First");
        q.offer("Second");
        assertEquals("First", q.poll());
        assertEquals("Second", q.poll());
        assertNull(q.poll());
    }

    @Test
    @DisplayName("Deque supports stack LIFO operations")
    void dequeStackLifo() {
        Deque<String> stack = new ArrayDeque<>();
        stack.push("Bottom");
        stack.push("Top");
        assertEquals("Top", stack.pop());
        assertEquals("Bottom", stack.pop());
    }
}
