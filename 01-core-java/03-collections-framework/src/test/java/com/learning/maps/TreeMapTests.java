package com.learning.maps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.*;

/**
 * Comprehensive tests for TreeMap implementation and usage.
 */
public class TreeMapTests {
    
    private TreeMap<Integer, String> treeMap;
    
    @BeforeEach
    void setUp() {
        treeMap = new TreeMap<>();
        treeMap.put(5, "Five");
        treeMap.put(2, "Two");
        treeMap.put(8, "Eight");
        treeMap.put(1, "One");
    }
    
    @Test
    void testOrdering() {
        assertThat(treeMap.keySet()).containsExactly(1, 2, 5, 8);
    }
    
    @Test
    void testFirstKey() {
        assertThat(treeMap.firstKey()).isEqualTo(1);
    }
    
    @Test
    void testLastKey() {
        assertThat(treeMap.lastKey()).isEqualTo(8);
    }
    
    @Test
    void testHeadMap() {
        assertThat(treeMap.headMap(5).keySet()).containsExactly(1, 2);
    }
    
    @Test
    void testTailMap() {
        assertThat(treeMap.tailMap(5).keySet()).containsExactly(5, 8);
    }
    
    @Test
    void testSubMap() {
        assertThat(treeMap.subMap(2, 8).keySet()).containsExactly(2, 5);
    }
    
    @Test
    void testHigherKey() {
        assertThat(treeMap.higherKey(5)).isEqualTo(8);
    }
    
    @Test
    void testLowerKey() {
        assertThat(treeMap.lowerKey(5)).isEqualTo(2);
    }
    
    @Test
    void testCeilingKey() {
        assertThat(treeMap.ceilingKey(4)).isEqualTo(5);
    }
    
    @Test
    void testFloorKey() {
        assertThat(treeMap.floorKey(6)).isEqualTo(5);
    }
    
    @Test
    void testDescendingKeySet() {
        assertThat(treeMap.descendingKeySet()).containsExactly(8, 5, 2, 1);
    }
    
    @Test
    void testPollFirstEntry() {
        Map.Entry<Integer, String> entry = treeMap.pollFirstEntry();
        assertThat(entry.getKey()).isEqualTo(1);
        assertThat(treeMap).doesNotContainKey(1);
    }
    
    @Test
    void testPollLastEntry() {
        Map.Entry<Integer, String> entry = treeMap.pollLastEntry();
        assertThat(entry.getKey()).isEqualTo(8);
        assertThat(treeMap).doesNotContainKey(8);
    }
    
    @Test
    void testReverseOrder() {
        TreeMap<Integer, String> reverseMap = new TreeMap<>(Collections.reverseOrder());
        reverseMap.put(5, "Five");
        reverseMap.put(2, "Two");
        reverseMap.put(8, "Eight");
        
        assertThat(reverseMap.keySet()).containsExactly(8, 5, 2);
    }
}
