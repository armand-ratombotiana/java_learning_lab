package com.learning.utilities;

import java.util.*;

/**
 * Demonstrates Collections utility class - static factory and helper methods.
 * Includes immutable collections, synchronized collections, binary search, sorting.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class CollectionsUtilityDemo {
    
    public void demonstrateUtilities() {
        System.out.println("--- COLLECTIONS UTILITY DEMONSTRATION ---");
        
        // Unmodifiable collections
        List<String> original = Arrays.asList("A", "B", "C");
        List<String> immutable = Collections.unmodifiableList(original);
        System.out.println("Immutable list: " + immutable);
        
        // Synchronized collections
        List<String> syncList = Collections.synchronizedList(new ArrayList<>(original));
        System.out.println("Synchronized list: " + syncList);
        
        // Singleton
        List<String> singleton = Collections.singletonList("OnlyOne");
        System.out.println("Singleton: " + singleton);
        
        // Empty collections
        List<Object> empty = Collections.emptyList();
        System.out.println("Empty list: " + empty);
        System.out.println("Is empty: " + empty.isEmpty());
        
        // Copy
        List<String> source = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));
        List<String> dest = Arrays.asList(new String[5]);
        Collections.copy(dest, source.subList(0, 3));
        System.out.println("After copy: " + dest);
        
        // Rotation
        List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        Collections.rotate(numbers, 2);
        System.out.println("After rotate(2): " + numbers);
        
        // Shuffle
        List<Integer> toShuffle = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        Collections.shuffle(toShuffle);
        System.out.println("After shuffle: " + toShuffle);
        
        // Reverse
        List<String> words = new ArrayList<>(Arrays.asList("Java", "Python", "C++", "Rust"));
        Collections.reverse(words);
        System.out.println("After reverse: " + words);
        
        // Binary search
        List<Integer> sorted = new ArrayList<>(Arrays.asList(1, 3, 5, 7, 9));
        System.out.println("Binary search for 5: " + Collections.binarySearch(sorted, 5));
        System.out.println("Binary search for 6: " + Collections.binarySearch(sorted, 6));
        
        // Frequency and disjoint
        List<Integer> freq = Arrays.asList(1, 2, 2, 3, 3, 3, 4);
        System.out.println("Frequency of 3: " + Collections.frequency(freq, 3));
        
        System.out.println("Collections utility methods demonstrated");
    }
}
