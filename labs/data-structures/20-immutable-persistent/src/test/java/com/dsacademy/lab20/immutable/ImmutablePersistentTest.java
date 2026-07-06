package com.dsacademy.lab20.immutable;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

public class ImmutablePersistentTest {

    @Test
    void testPersistentListAddAndHead() {
        PersistentList<Integer> list = PersistentList.empty();
        list = list.add(1).add(2).add(3);
        assertEquals(3, list.size());
        assertEquals(3, list.head());
        assertEquals(2, list.tail().head());
        assertEquals(1, list.tail().tail().head());
        assertTrue(list.tail().tail().tail().isEmpty());
    }

    @Test
    void testPersistentListStructuralSharing() {
        PersistentList<Integer> list1 = PersistentList.empty().add(1).add(2);
        PersistentList<Integer> list2 = list1.add(3);
        assertEquals(2, list1.size());
        assertEquals(3, list2.size());
        assertTrue(list1.tail() == list2.tail().tail());
    }

    @Test
    void testPersistentListGetAndSet() {
        PersistentList<String> list = PersistentList.empty().add("a").add("b").add("c");
        assertEquals("b", list.get(1));
        PersistentList<String> updated = list.set(1, "x");
        assertEquals("x", updated.get(1));
        assertEquals("b", list.get(1));
    }

    @Test
    void testPersistentListIteration() {
        PersistentList<Integer> list = PersistentList.empty().add(1).add(2).add(3);
        List<Integer> result = new ArrayList<>();
        for (int v : list) result.add(v);
        assertEquals(Arrays.asList(3, 2, 1), result);
    }

    @Test
    void testPersistentListRemove() {
        PersistentList<Integer> list = PersistentList.empty().add(1).add(2).add(3);
        PersistentList<Integer> removed = list.remove();
        assertEquals(2, removed.size());
        assertEquals(2, removed.head());
    }

    @Test
    void testPersistentStack() {
        PersistentStack<Integer> stack = PersistentStack.empty();
        stack = stack.push(1).push(2).push(3);
        assertEquals(3, stack.size());
        assertEquals(3, stack.peek());
        stack = stack.pop();
        assertEquals(2, stack.peek());
        stack = stack.pop();
        assertEquals(1, stack.peek());
        assertFalse(stack.isEmpty());
        stack = stack.pop();
        assertTrue(stack.isEmpty());
    }

    @Test
    void testPersistentStackStructuralSharing() {
        PersistentStack<Integer> s1 = PersistentStack.empty().push(1).push(2);
        PersistentStack<Integer> s2 = s1.push(3);
        assertEquals(2, s1.size());
        assertEquals(3, s2.size());
        assertEquals(s1, s2.pop().rest);
    }

    @Test
    void testPersistentBinaryTree() {
        PersistentBinaryTree<Integer> tree = PersistentBinaryTree.empty();
        tree = tree.insert(5).insert(3).insert(7).insert(1).insert(9);
        assertEquals(5, tree.size());
        assertTrue(tree.contains(5));
        assertTrue(tree.contains(3));
        assertTrue(tree.contains(9));
        assertFalse(tree.contains(4));
    }

    @Test
    void testPersistentBinaryTreeStructuralSharing() {
        PersistentBinaryTree<Integer> t1 = PersistentBinaryTree.empty().insert(5).insert(3);
        PersistentBinaryTree<Integer> t2 = t1.insert(7);
        assertTrue(t2.left() == t1.left());
    }

    @Test
    void testImmutablePair() {
        ImmutablePair<String, Integer> pair = ImmutablePair.of("hello", 42);
        assertEquals("hello", pair.first);
        assertEquals(42, pair.second);
        assertEquals("(hello, 42)", pair.toString());
    }

    @Test
    void testImmutablePairEquality() {
        assertEquals(ImmutablePair.of(1, "a"), ImmutablePair.of(1, "a"));
        assertNotEquals(ImmutablePair.of(1, "a"), ImmutablePair.of(2, "a"));
    }

    @Test
    void testEmptyListBehavior() {
        PersistentList<Integer> empty = PersistentList.empty();
        assertTrue(empty.isEmpty());
        assertEquals(0, empty.size());
        assertThrows(IllegalStateException.class, () -> empty.head());
        assertThrows(IllegalStateException.class, () -> empty.remove());
    }

    @Test
    void testThreadSafety() throws InterruptedException {
        PersistentList<Integer> list = PersistentList.empty();
        for (int i = 0; i < 100; i++) list = list.add(i);
        final PersistentList<Integer> shared = list;
        Thread t1 = new Thread(() -> { for (int v : shared) assertTrue(v >= 0); });
        Thread t2 = new Thread(() -> { for (int v : shared) assertTrue(v < 100); });
        t1.start(); t2.start();
        t1.join(); t2.join();
    }
}
