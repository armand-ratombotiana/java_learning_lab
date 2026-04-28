package com.learning.lists;

import java.util.*;

/**
 * Demonstrates list sorting with custom comparators and comparables.
 * Shows natural ordering and custom ordering examples.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class ListComparatorDemo {
    
    public void demonstrateComparators() {
        System.out.println("--- LIST COMPARATOR & SORTING DEMONSTRATION ---");
        
        // Natural ordering (Comparable)
        List<Integer> numbers = Arrays.asList(5, 2, 8, 1, 9, 3);
        System.out.println("Original: " + numbers);
        
        List<Integer> sorted = new ArrayList<>(numbers);
        Collections.sort(sorted);
        System.out.println("Sorted ascending: " + sorted);
        
        Collections.sort(sorted, Collections.reverseOrder());
        System.out.println("Sorted descending: " + sorted);
        
        // String sorting
        List<String> words = Arrays.asList("Java", "Programming", "Collections", "API");
        System.out.println("\nWords: " + words);
        
        Collections.sort(words);
        System.out.println("Sorted: " + words);
        
        Collections.sort(words, Comparator.reverseOrder());
        System.out.println("Reverse sorted: " + words);
        
        // Custom comparator - by length
        Collections.sort(words, Comparator.comparingInt(String::length));
        System.out.println("By length: " + words);
        
        // Custom comparator - by length desc, then alphabetically
        words.sort(
            Comparator.comparingInt(String::length)
            .reversed()
            .thenComparing(Comparator.naturalOrder())
        );
        System.out.println("By length desc, then alphabetically: " + words);
        
        // Parallel sort (for large lists)
        Integer[] bigArray = new Integer[1000];
        Random rand = new Random();
        for (int i = 0; i < bigArray.length; i++) {
            bigArray[i] = rand.nextInt(1000);
        }
        Arrays.parallelSort(bigArray);
        System.out.println("Parallel sort completed on array of 1000 elements");
    }
}
