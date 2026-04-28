package com.learning.sets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.*;

/**
 * Comprehensive tests for HashSet implementation and usage.
 */
public class HashSetTests {
    
    private Set<String> set;
    
    @BeforeEach
    void setUp() {
        set = new HashSet<>();
        set.add("Apple");
        set.add("Banana");
        set.add("Cherry");
    }
    
    @Test
    void testAdd() {
        set.add("Date");
        assertThat(set).hasSize(4).contains("Date");
    }
    
    @Test
    void testAddDuplicate() {
        boolean added = set.add("Apple");
        assertThat(added).isFalse();
        assertThat(set).hasSize(3); // No increase
    }
    
    @Test
    void testRemove() {
        set.remove("Banana");
        assertThat(set).hasSize(2).doesNotContain("Banana");
    }
    
    @Test
    void testContains() {
        assertThat(set.contains("Apple")).isTrue();
        assertThat(set.contains("Unknown")).isFalse();
    }
    
    @Test
    void testSize() {
        assertThat(set).hasSize(3);
    }
    
    @Test
    void testIsEmpty() {
        Set<String> empty = new HashSet<>();
        assertThat(empty.isEmpty()).isTrue();
        assertThat(set.isEmpty()).isFalse();
    }
    
    @Test
    void testClear() {
        set.clear();
        assertThat(set).isEmpty();
    }
    
    @Test
    void testIteration() {
        Set<String> iterated = new HashSet<>(set);
        assertThat(iterated).hasSize(3).containsAll(set);
    }
    
    @Test
    void testUnion() {
        Set<String> other = new HashSet<>(Arrays.asList("Banana", "Date"));
        set.addAll(other);
        assertThat(set).containsExactlyInAnyOrder("Apple", "Banana", "Cherry", "Date");
    }
    
    @Test
    void testIntersection() {
        Set<String> other = new HashSet<>(Arrays.asList("Banana", "Cherry", "Date"));
        set.retainAll(other);
        assertThat(set).containsExactlyInAnyOrder("Banana", "Cherry");
    }
    
    @Test
    void testDifference() {
        Set<String> other = new HashSet<>(Arrays.asList("Banana", "Cherry"));
        set.removeAll(other);
        assertThat(set).containsExactly("Apple");
    }
    
    @Test
    void testContainsAll() {
        Set<String> subset = new HashSet<>(Arrays.asList("Apple", "Banana"));
        assertThat(set.containsAll(subset)).isTrue();
    }
    
    @Test
    void testAddAll() {
        set.addAll(Arrays.asList("Date", "Elderberry"));
        assertThat(set).hasSize(5);
    }
    
    @Test
    void testWithNullElement() {
        set.add(null);
        assertThat(set).contains((String) null);
    }
    
    @Test
    void testUniquenessBehavior() {
        Set<Integer> numbers = new HashSet<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(1);
        numbers.add(3);
        numbers.add(2);
        assertThat(numbers).hasSize(3).containsExactlyInAnyOrder(1, 2, 3);
    }
}
