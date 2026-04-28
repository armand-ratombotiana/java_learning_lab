package com.learning.maps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Comprehensive tests for ConcurrentHashMap implementation and usage.
 */
public class ConcurrentHashMapTests {
    
    private Map<String, Integer> concurrentMap;
    
    @BeforeEach
    void setUp() {
        concurrentMap = new ConcurrentHashMap<>();
        concurrentMap.put("Java", 10);
        concurrentMap.put("Python", 7);
        concurrentMap.put("Go", 5);
    }
    
    @Test
    void testPut() {
        concurrentMap.put("Rust", 3);
        assertThat(concurrentMap).hasSize(4).containsEntry("Rust", 3);
    }
    
    @Test
    void testGet() {
        assertThat(concurrentMap.get("Java")).isEqualTo(10);
    }
    
    @Test
    void testPutIfAbsent() {
        concurrentMap.putIfAbsent("Java", 20);
        assertThat(concurrentMap.get("Java")).isEqualTo(10); // Not updated
        
        concurrentMap.putIfAbsent("C++", 8);
        assertThat(concurrentMap.get("C++")).isEqualTo(8); // Added
    }
    
    @Test
    void testComputeIfPresent() {
        concurrentMap.computeIfPresent("Java", (k, v) -> v + 5);
        assertThat(concurrentMap.get("Java")).isEqualTo(15);
    }
    
    @Test
    void testComputeIfAbsent() {
        concurrentMap.computeIfAbsent("Ruby", k -> 4);
        assertThat(concurrentMap.get("Ruby")).isEqualTo(4);
    }
    
    @Test
    void testMerge() {
        concurrentMap.merge("Java", 1, Integer::sum);
        assertThat(concurrentMap.get("Java")).isEqualTo(11);
    }
    
    @Test
    void testRemove() {
        concurrentMap.remove("Python");
        assertThat(concurrentMap).hasSize(2).doesNotContainKey("Python");
    }
    
    @Test
    void testContainsKey() {
        assertThat(concurrentMap.containsKey("Java")).isTrue();
        assertThat(concurrentMap.containsKey("Unknown")).isFalse();
    }
    
    @Test
    void testSize() {
        assertThat(concurrentMap).hasSize(3);
    }
    
    @Test
    void testIteration() {
        concurrentMap.forEach((k, v) -> 
            assertThat(k).isNotNull()
        );
    }
    
    @Test
    void testNoNullKeys() {
        assertThatThrownBy(() -> concurrentMap.put(null, 1))
            .isInstanceOf(NullPointerException.class);
    }
    
    @Test
    void testNoNullValues() {
        assertThatThrownBy(() -> concurrentMap.put("Test", null))
            .isInstanceOf(NullPointerException.class);
    }
    
    @Test
    void testGetOrDefault() {
        assertThat(concurrentMap.getOrDefault("Unknown", 0)).isEqualTo(0);
    }
    
    @Test
    void testSizeAndUpdate() {
        assertThat(concurrentMap).hasSize(3);
        concurrentMap.put("NewLang", 2);
        assertThat(concurrentMap).hasSize(4);
    }
}
