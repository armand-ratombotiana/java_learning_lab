package com.javalab.01;

import java.util.HashMap;

public final class MapExample {

    private MapExample() {
    }

    public static void main(String[] args) {
        System.out.println("=== HashMapSeparateChaining Demo ===");
        demoMapSeparateChaining();

        System.out.println("\n=== HashMapOpenAddressing Demo ===");
        demoMapOpenAddressing();

        System.out.println("\n=== java.util.HashMap Demo ===");
        demoJavaMap();

        System.out.println("\n=== Collision Demonstration ===");
        demoCollisions();

        System.out.println("\n=== Rehashing Demonstration ===");
        demoRehashing();
    }

    private static void demoMapSeparateChaining() {
        HashMapSeparateChaining<String, Integer> map = new HashMapSeparateChaining<>();
        map.put("Alice", 90);
        map.put("Bob", 85);
        map.put("Charlie", 95);
        System.out.println("Alice's score: " + map.get("Alice"));
        System.out.println("Contains Charlie? " + map.containsKey("Charlie"));
        map.remove("Bob");
        System.out.println("After removing Bob, size: " + map.size());
        System.out.println("Map contents: " + map);
    }

    private static void demoMapOpenAddressing() {
        HashMapOpenAddressing<String, Integer> map = new HashMapOpenAddressing<>();
        map.put("Alice", 90);
        map.put("Bob", 85);
        map.put("Charlie", 95);
        System.out.println("Alice's score: " + map.get("Alice"));
        System.out.println("Contains Charlie? " + map.containsKey("Charlie"));
        map.remove("Bob");
        System.out.println("After removing Bob, size: " + map.size());
        System.out.println("Map contents: " + map);
    }

    private static void demoJavaMap() {
        HashMap<String, Integer> map = new HashMap<>();
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
