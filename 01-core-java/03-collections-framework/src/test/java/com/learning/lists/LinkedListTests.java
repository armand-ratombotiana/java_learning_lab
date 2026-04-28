package com.learning.lists;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.*;

/**
 * Comprehensive tests for LinkedList implementation and usage.
 */
public class LinkedListTests {
    
    private LinkedList<Integer> list;
    
    @BeforeEach
    void setUp() {
        list = new LinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);
    }
    
    @Test
    void testAddFirst() {
        list.addFirst(0);
        assertThat(list.getFirst()).isEqualTo(0);
        assertThat(list).hasSize(4);
    }
    
    @Test
    void testAddLast() {
        list.addLast(4);
        assertThat(list.getLast()).isEqualTo(4);
        assertThat(list).hasSize(4);
    }
    
    @Test
    void testRemoveFirst() {
        list.removeFirst();
        assertThat(list.getFirst()).isEqualTo(2);
        assertThat(list).hasSize(2);
    }
    
    @Test
    void testRemoveLast() {
        list.removeLast();
        assertThat(list.getLast()).isEqualTo(2);
        assertThat(list).hasSize(2);
    }
    
    @Test
    void testPeek() {
        assertThat(list.peekFirst()).isEqualTo(1);
        assertThat(list.peekLast()).isEqualTo(3);
        assertThat(list).hasSize(3); // Peek doesn't remove
    }
    
    @Test
    void testPoll() {
        assertThat(list.pollFirst()).isEqualTo(1);
        assertThat(list.pollLast()).isEqualTo(3);
        assertThat(list).containsExactly(2);
    }
    
    @Test
    void testAsStack() {
        list.push(4);
        assertThat(list.pop()).isEqualTo(4);
        assertThat(list.peek()).isEqualTo(1);
    }
    
    @Test
    void testDescendingIterator() {
        List<Integer> reversed = new ArrayList<>();
        list.descendingIterator().forEachRemaining(reversed::add);
        assertThat(reversed).containsExactly(3, 2, 1);
    }
    
    @Test
    void testContains() {
        assertThat(list.contains(2)).isTrue();
        assertThat(list.contains(10)).isFalse();
    }
    
    @Test
    void testRemoveValue() {
        list.remove(Integer.valueOf(2));
        assertThat(list).containsExactly(1, 3);
    }
    
    @Test
    void testSize() {
        assertThat(list).hasSize(3);
        list.add(4);
        assertThat(list).hasSize(4);
    }
    
    @Test
    void testGet() {
        assertThat(list.get(0)).isEqualTo(1);
        assertThat(list.get(2)).isEqualTo(3);
    }
    
    @Test
    void testSubList() {
        List<Integer> sublist = list.subList(1, 3);
        assertThat(sublist).containsExactly(2, 3);
    }
    
    @Test
    void testClear() {
        list.clear();
        assertThat(list).isEmpty();
    }
    
    @Test
    void testEmptyListException() {
        LinkedList<Integer> empty = new LinkedList<>();
        assertThatThrownBy(empty::removeFirst)
            .isInstanceOf(NoSuchElementException.class);
    }
}
