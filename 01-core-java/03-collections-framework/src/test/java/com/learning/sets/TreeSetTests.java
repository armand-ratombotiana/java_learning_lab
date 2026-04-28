package com.learning.sets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.*;

/**
 * Comprehensive tests for TreeSet implementation and usage.
 */
public class TreeSetTests {
    
    private TreeSet<Integer> treeSet;
    
    @BeforeEach
    void setUp() {
        treeSet = new TreeSet<>();
        treeSet.addAll(Arrays.asList(5, 2, 8, 1, 9));
    }
    
    @Test
    void testOrdering() {
        assertThat(treeSet).containsExactly(1, 2, 5, 8, 9);
    }
    
    @Test
    void testFirst() {
        assertThat(treeSet.first()).isEqualTo(1);
    }
    
    @Test
    void testLast() {
        assertThat(treeSet.last()).isEqualTo(9);
    }
    
    @Test
    void testHead() {
        assertThat(treeSet.headSet(5)).containsExactly(1, 2);
    }
    
    @Test
    void testTail() {
        assertThat(treeSet.tailSet(5)).containsExactly(5, 8, 9);
    }
    
    @Test
    void testSubSet() {
        assertThat(treeSet.subSet(2, 8)).containsExactly(2, 5);
    }
    
    @Test
    void testHigher() {
        assertThat(treeSet.higher(5)).isEqualTo(8);
    }
    
    @Test
    void testLower() {
        assertThat(treeSet.lower(5)).isEqualTo(2);
    }
    
    @Test
    void testCeiling() {
        assertThat(treeSet.ceiling(4)).isEqualTo(5);
    }
    
    @Test
    void testFloor() {
        assertThat(treeSet.floor(6)).isEqualTo(5);
    }
    
    @Test
    void testDescending() {
        assertThat(treeSet.descendingSet()).containsExactly(9, 8, 5, 2, 1);
    }
    
    @Test
    void testPollFirst() {
        assertThat(treeSet.pollFirst()).isEqualTo(1);
        assertThat(treeSet).doesNotContain(1);
    }
    
    @Test
    void testPollLast() {
        assertThat(treeSet.pollLast()).isEqualTo(9);
        assertThat(treeSet).doesNotContain(9);
    }
    
    @Test
    void testReverseOrder() {
        Set<Integer> reverseSet = new TreeSet<>(Collections.reverseOrder());
        reverseSet.addAll(Arrays.asList(5, 2, 8, 1));
        assertThat(reverseSet).containsExactly(8, 5, 2, 1);
    }
    
    @Test
    void testStringTreeSet() {
        TreeSet<String> words = new TreeSet<>(
            Arrays.asList("Zebra", "Apple", "Mango", "Banana")
        );
        assertThat(words).containsExactly("Apple", "Banana", "Mango", "Zebra");
    }
}
