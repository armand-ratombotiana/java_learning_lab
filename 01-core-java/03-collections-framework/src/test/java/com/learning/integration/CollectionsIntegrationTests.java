package com.learning.integration;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.*;

/**
 * Integration tests demonstrating collections working together.
 */
public class CollectionsIntegrationTests {
    
    @Test
    void testListToSetConversion() {
        List<Integer> list = Arrays.asList(1, 2, 2, 3, 3, 3);
        Set<Integer> set = new HashSet<>(list);
        assertThat(set).hasSize(3).containsExactlyInAnyOrder(1, 2, 3);
    }
    
    @Test
    void testMapKeysAsSet() {
        Map<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        
        Set<String> keys = map.keySet();
        assertThat(keys).containsExactlyInAnyOrder("A", "B", "C");
    }
    
    @Test
    void testMapValuesAsList() {
        Map<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        
        Collection<Integer> values = map.values();
        assertThat(values).containsExactlyInAnyOrder(1, 2, 3);
    }
    
    @Test
    void testGroupByOperation() {
        List<String> words = Arrays.asList("Java", "Go", "Python", "Rust", "C");
        
        Map<Integer, List<String>> byLength = new HashMap<>();
        for (String word : words) {
            byLength.computeIfAbsent(word.length(), k -> new ArrayList<>()).add(word);
        }
        
        assertThat(byLength.get(2)).containsExactly("Go");
        assertThat(byLength.get(4)).containsExactly("Java", "Rust");
        assertThat(byLength.get(6)).containsExactly("Python");
    }
    
    @Test
    void testNestedCollections() {
        List<Set<Integer>> listOfSets = new ArrayList<>();
        listOfSets.add(new HashSet<>(Arrays.asList(1, 2, 3)));
        listOfSets.add(new HashSet<>(Arrays.asList(4, 5, 6)));
        
        assertThat(listOfSets).hasSize(2);
        assertThat(listOfSets.get(0)).hasSize(3);
    }
    
    @Test
    void testDeduplication() {
        List<String> items = Arrays.asList("A", "B", "A", "C", "B", "D");
        List<String> unique = new ArrayList<>(new LinkedHashSet<>(items));
        
        assertThat(unique).hasSize(4).containsExactly("A", "B", "C", "D");
    }
    
    @Test
    void testSortingCollectionsByValue() {
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Alice", 95);
        scores.put("Bob", 87);
        scores.put("Charlie", 92);
        
        scores.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
    }
    
    @Test
    void testFilterAndTransform() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
        
        List<Integer> result = new ArrayList<>();
        for (Integer num : numbers) {
            if (num % 2 == 0) {
                result.add(num * num);
            }
        }
        
        assertThat(result).containsExactly(4, 16, 36);
    }
    
    @Test
    void testFrequencyCounter() {
        List<String> words = Arrays.asList("apple", "banana", "apple", "cherry", "banana", "apple");
        
        Map<String, Integer> frequency = new HashMap<>();
        for (String word : words) {
            frequency.put(word, frequency.getOrDefault(word, 0) + 1);
        }
        
        assertThat(frequency.get("apple")).isEqualTo(3);
        assertThat(frequency.get("banana")).isEqualTo(2);
        assertThat(frequency.get("cherry")).isEqualTo(1);
    }
    
    @Test
    void testQueueAsBuffer() {
        Queue<Integer> buffer = new LinkedList<>();
        buffer.offer(1);
        buffer.offer(2);
        buffer.offer(3);
        
        assertThat(buffer.peek()).isEqualTo(1);
        assertThat(buffer.poll()).isEqualTo(1);
        assertThat(buffer).containsExactly(2, 3);
    }
}
