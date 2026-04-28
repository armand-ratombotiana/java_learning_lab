package com.learning.lists;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.*;

/**
 * Comprehensive tests for ArrayList implementation and usage.
 */
public class ArrayListTests {
    
    private List<String> list;
    
    @BeforeEach
    void setUp() {
        list = new ArrayList<>();
        list.add("Alpha");
        list.add("Beta");
        list.add("Gamma");
    }
    
    @Test
    void testAdd() {
        list.add("Delta");
        assertThat(list).hasSize(4).contains("Delta");
    }
    
    @Test
    void testAddAtIndex() {
        list.add(1, "Beta-new");
        assertThat(list.get(1)).isEqualTo("Beta-new");
        assertThat(list).hasSize(4);
    }
    
    @Test
    void testGet() {
        assertThat(list.get(0)).isEqualTo("Alpha");
        assertThat(list.get(2)).isEqualTo("Gamma");
    }
    
    @Test
    void testSet() {
        list.set(1, "BETA");
        assertThat(list.get(1)).isEqualTo("BETA");
    }
    
    @Test
    void testRemove() {
        list.remove("Beta");
        assertThat(list).hasSize(2).doesNotContain("Beta");
    }
    
    @Test
    void testRemoveByIndex() {
        String removed = list.remove(0);
        assertThat(removed).isEqualTo("Alpha");
        assertThat(list).hasSize(2);
    }
    
    @Test
    void testContains() {
        assertThat(list.contains("Alpha")).isTrue();
        assertThat(list.contains("Unknown")).isFalse();
    }
    
    @Test
    void testIndexOf() {
        assertThat(list.indexOf("Beta")).isEqualTo(1);
        assertThat(list.indexOf("Unknown")).isEqualTo(-1);
    }
    
    @Test
    void testSize() {
        assertThat(list).hasSize(3);
        list.add("Delta");
        assertThat(list).hasSize(4);
    }
    
    @Test
    void testIsEmpty() {
        List<String> empty = new ArrayList<>();
        assertThat(empty.isEmpty()).isTrue();
        assertThat(list.isEmpty()).isFalse();
    }
    
    @Test
    void testClear() {
        list.clear();
        assertThat(list).isEmpty();
    }
    
    @Test
    void testIteration() {
        List<String> iterated = new ArrayList<>();
        for (String item : list) {
            iterated.add(item);
        }
        assertThat(iterated).isEqualTo(list);
    }
    
    @Test
    void testSubList() {
        List<String> sublist = list.subList(0, 2);
        assertThat(sublist).containsExactly("Alpha", "Beta");
    }
    
    @Test
    void testAddAll() {
        list.addAll(Arrays.asList("Delta", "Epsilon"));
        assertThat(list).hasSize(5);
    }
    
    @Test
    void testSorting() {
        List<Integer> numbers = new ArrayList<>(Arrays.asList(5, 2, 8, 1));
        Collections.sort(numbers);
        assertThat(numbers).containsExactly(1, 2, 5, 8);
    }
    
    @Test
    void testStream() {
        long count = list.stream().filter(s -> s.startsWith("A")).count();
        assertThat(count).isEqualTo(1);
    }
}
