package com.learning.maps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.*;

/**
 * Comprehensive tests for HashMap implementation and usage.
 */
public class HashMapTests {
    
    private Map<String, Integer> map;
    
    @BeforeEach
    void setUp() {
        map = new HashMap<>();
        map.put("Alice", 95);
        map.put("Bob", 87);
        map.put("Charlie", 92);
    }
    
    @Test
    void testPut() {
        map.put("Diana", 98);
        assertThat(map).hasSize(4).containsEntry("Diana", 98);
    }
    
    @Test
    void testGet() {
        assertThat(map.get("Alice")).isEqualTo(95);
        assertThat(map.get("Unknown")).isNull();
    }
    
    @Test
    void testPutUpdate() {
        map.put("Alice", 100);
        assertThat(map.get("Alice")).isEqualTo(100);
        assertThat(map).hasSize(3);
    }
    
    @Test
    void testRemove() {
        map.remove("Bob");
        assertThat(map).hasSize(2).doesNotContainKey("Bob");
    }
    
    @Test
    void testContainsKey() {
        assertThat(map.containsKey("Alice")).isTrue();
        assertThat(map.containsKey("Unknown")).isFalse();
    }
    
    @Test
    void testContainsValue() {
        assertThat(map.containsValue(95)).isTrue();
        assertThat(map.containsValue(100)).isFalse();
    }
    
    @Test
    void testSize() {
        assertThat(map).hasSize(3);
        map.put("Diana", 98);
        assertThat(map).hasSize(4);
    }
    
    @Test
    void testIsEmpty() {
        Map<String, Integer> empty = new HashMap<>();
        assertThat(empty.isEmpty()).isTrue();
        assertThat(map.isEmpty()).isFalse();
    }
    
    @Test
    void testClear() {
        map.clear();
        assertThat(map).isEmpty();
    }
    
    @Test
    void testKeySet() {
        assertThat(map.keySet()).containsExactlyInAnyOrder("Alice", "Bob", "Charlie");
    }
    
    @Test
    void testValues() {
        assertThat(map.values()).containsExactlyInAnyOrder(95, 87, 92);
    }
    
    @Test
    void testEntrySet() {
        assertThat(map.entrySet()).hasSize(3);
    }
    
    @Test
    void testPutIfAbsent() {
        map.putIfAbsent("Alice", 100);
        assertThat(map.get("Alice")).isEqualTo(95); // Not updated
        
        map.putIfAbsent("Diana", 98);
        assertThat(map.get("Diana")).isEqualTo(98); // Added
    }
    
    @Test
    void testGetOrDefault() {
        assertThat(map.getOrDefault("Alice", 0)).isEqualTo(95);
        assertThat(map.getOrDefault("Diana", 0)).isEqualTo(0);
    }
    
    @Test
    void testReplace() {
        map.replace("Alice", 100);
        assertThat(map.get("Alice")).isEqualTo(100);
    }
    
    @Test
    void testIterationWithLambda() {
        List<String> keys = new ArrayList<>();
        map.forEach((k, v) -> keys.add(k));
        assertThat(keys).hasSize(3);
    }
    
    @Test
    void testWithNullKey() {
        map.put(null, 50);
        assertThat(map.get(null)).isEqualTo(50);
    }
}
