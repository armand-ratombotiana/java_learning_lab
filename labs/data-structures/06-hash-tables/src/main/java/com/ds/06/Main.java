package com.ds06;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== HashMapChaining Demo ===");
        HashMapChaining<String, Integer> chainMap = new HashMapChaining<>();
        chainMap.put("Alice", 25);
        chainMap.put("Bob", 30);
        chainMap.put("Charlie", 35);
        chainMap.put("Diana", 28);
        System.out.println("Map: " + chainMap);
        System.out.println("Get Alice: " + chainMap.get("Alice"));
        System.out.println("Contains Bob: " + chainMap.containsKey("Bob"));
        System.out.println("Contains Eve: " + chainMap.containsKey("Eve"));
        chainMap.put("Alice", 26);
        System.out.println("After update Alice: " + chainMap.get("Alice"));
        chainMap.remove("Charlie");
        System.out.println("After remove Charlie: " + chainMap);
        System.out.println("Size: " + chainMap.size());
        System.out.println("Keys: " + chainMap.keySet());
        System.out.println("Values: " + chainMap.values());

        System.out.println("\n=== HashMapOpenAddressing Demo ===");
        HashMapOpenAddressing<String, String> openMap = new HashMapOpenAddressing<>();
        openMap.put("US", "Washington");
        openMap.put("UK", "London");
        openMap.put("JP", "Tokyo");
        openMap.put("BR", "Brasilia");
        System.out.println("Map: " + openMap);
        System.out.println("Get US: " + openMap.get("US"));
        System.out.println("Contains JP: " + openMap.containsKey("JP"));
        openMap.remove("UK");
        System.out.println("After remove UK: " + openMap);
        System.out.println("Size: " + openMap.size());
        System.out.println("Keys: " + openMap.keySet());

        System.out.println("\n=== Large Insert (Resize) Demo ===");
        HashMapChaining<Integer, String> big = new HashMapChaining<>();
        for (int i = 0; i < 1000; i++) {
            big.put(i, "Value-" + i);
        }
        System.out.println("Size after 1000 inserts: " + big.size());
        System.out.println("Get 500: " + big.get(500));
        System.out.println("Get 999: " + big.get(999));

        System.out.println("\n=== Null Key Demo ===");
        HashMapChaining<String, String> nullMap = new HashMapChaining<>();
        nullMap.put(null, "nullValue");
        System.out.println("Get null: " + nullMap.get(null));
        System.out.println("Contains null: " + nullMap.containsKey(null));
    }
}
