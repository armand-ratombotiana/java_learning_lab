package com.learning.lab12;

import java.util.*;

/**
 * Demonstrates HashMap, TreeMap — key-value storage, ordering, and map operations.
 */
public class MapExample {

    public static void showMaps() {
        System.out.println("=== HashMap & TreeMap ===");

        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("Alice", 30);
        hashMap.put("Bob", 25);
        hashMap.put("Charlie", 35);
        System.out.println("HashMap: " + hashMap);
        System.out.println("Age of Alice: " + hashMap.get("Alice"));
        System.out.println("Contains key 'Bob'? " + hashMap.containsKey("Bob"));

        Map<String, Integer> treeMap = new TreeMap<>();
        treeMap.put("Charlie", 35);
        treeMap.put("Alice", 30);
        treeMap.put("Bob", 25);
        System.out.println("TreeMap (sorted by key): " + treeMap);

        System.out.print("Iterating map entries: ");
        for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
            System.out.print(entry.getKey() + "=" + entry.getValue() + " ");
        }
        System.out.println();

        hashMap.computeIfAbsent("David", k -> 40);
        hashMap.computeIfPresent("Alice", (k, v) -> v + 1);
        System.out.println("After compute operations: " + hashMap);
    }
}
