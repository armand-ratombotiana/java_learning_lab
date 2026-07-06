package com.javaacademy.collections.hashmap;

import java.util.HashMap;

/**
 * Demonstrates usage of the custom HashMap implementations
 * ({@link HashMapSeparateChaining} and {@link HashMapOpenAddressing})
 * alongside the standard {@link java.util.HashMap}.
 *
 * <p>This class illustrates the API consistency across implementations and
 * highlights key differences in behaviour, performance characteristics,
 * and internal collision-resolution strategies.
 */
public final class MapExample {

    private MapExample() {
    }

    public static void main(String[] args) {
        System.out.println("=== HashMapSeparateChaining Demo ===");
        demoMap(new HashMapSeparateChaining<>());

        System.out.println("\n=== HashMapOpenAddressing Demo ===");
        demoMap(new HashMapOpenAddressing<>());

        System.out.println("\n=== java.util.HashMap Demo ===");
        demoMap(new HashMap<String, Integer>());

        System.out.println("\n=== Collision Demonstration ===");
        demoCollisions();

        System.out.println("\n=== Rehashing Demonstration ===");
        demoRehashing();
    }

    private static void demoMap(HashMapSeparateChaining<String, Integer> map) {
        map.put("Alice", 90);
        map.put("Bob", 85);
        map.put("Charlie", 95);
        System.out.println("Alice's score: " + map.get("Alice"));
        System.out.println("Contains Charlie? " + map.containsKey("Charlie"));
        map.remove("Bob");
        System.out.println("After removing Bob, size: " + map.size());
        System.out.println("Map contents: " + map);
    }

    private static void demoMap(HashMapOpenAddressing<String, Integer> map) {
        map.put("Alice", 90);
        map.put("Bob", 85);
        map.put("Charlie", 95);
        System.out.println("Alice's score: " + map.get("Alice"));
        System.out.println("Contains Charlie? " + map.containsKey("Charlie"));
        map.remove("Bob");
        System.out.println("After removing Bob, size: " + map.size());
        System.out.println("Map contents: " + map);
    }

    private static void demoMap(HashMap<String, Integer> map) {
        map.put("Alice", 90);
        map.put("Bob", 85);
        map.put("Charlie", 95);
        System.out.println("Alice's score: " + map.get("Alice"));
        System.out.println("Contains Charlie? " + map.containsKey("Charlie"));
        map.remove("Bob");
        System.out.println("After removing Bob, size: " + map.size());
        System.out.println("Map contents: " + map);
    }

    private static void demoCollisions() {
        HashMapSeparateChaining<Integer, String> map = new HashMapSeparateChaining<>(4);

        for (int i = 0; i < 10; i++) {
            map.put(i, "Value-" + i);
        }
        System.out.println("Inserted 10 entries into capacity-4 map (load factor triggers rehash)");
        System.out.println("Size: " + map.size());
        for (int i = 0; i < 10; i++) {
            System.out.println("  Key " + i + " -> " + map.get(i));
        }
    }

    private static void demoRehashing() {
        HashMapSeparateChaining<String, String> map = new HashMapSeparateChaining<>(4);
        int initialCapacity = 4;

        for (int i = 1; i <= 20; i++) {
            map.put("Key" + i, "Value" + i);
        }
        System.out.println("Inserted 20 entries starting from capacity " + initialCapacity);
        System.out.println("Final size: " + map.size());
        System.out.println("All entries retrievable: " + map.containsKey("Key20"));
    }
}
