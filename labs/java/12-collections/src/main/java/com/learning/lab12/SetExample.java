package com.learning.lab12;

import java.util.*;

/**
 * Demonstrates HashSet, TreeSet — uniqueness, ordering, and operations.
 */
public class SetExample {

    public static void showSets() {
        System.out.println("=== HashSet & TreeSet ===");

        Set<String> hashSet = new HashSet<>();
        hashSet.add("Banana");
        hashSet.add("Apple");
        hashSet.add("Cherry");
        hashSet.add("Banana");
        System.out.println("HashSet (no order, no duplicates): " + hashSet);

        Set<String> treeSet = new TreeSet<>();
        treeSet.add("Banana");
        treeSet.add("Apple");
        treeSet.add("Cherry");
        treeSet.add("Banana");
        System.out.println("TreeSet (sorted): " + treeSet);

        Set<Integer> numbers = new HashSet<>(Set.of(3, 1, 4, 1, 5, 9, 2, 6));
        System.out.println("Numbers set: " + numbers);
        System.out.println("Contains 4? " + numbers.contains(4));
        System.out.println("Contains 7? " + numbers.contains(7));

        TreeSet<Integer> sortedNums = new TreeSet<>(numbers);
        System.out.println("First: " + sortedNums.first() + ", Last: " + sortedNums.last());
        System.out.println("Higher than 5: " + sortedNums.higher(5));
        System.out.println("Lower than 5: " + sortedNums.lower(5));
    }
}
