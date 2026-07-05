package com.ds10;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== BloomFilter Demo ===");

        BloomFilter<String> filter = new BloomFilter<>(100, 0.01);
        System.out.println("Configuration: " + filter);
        System.out.println("Bit set size: " + filter.getBitSetSize());
        System.out.println("Hash functions: " + filter.getNumHashFunctions());

        String[] items = {"apple", "banana", "cherry", "date", "elderberry",
                          "fig", "grape", "honeydew", "kiwi", "lemon"};
        for (String item : items) {
            filter.add(item);
        }
        System.out.println("\nAdded " + items.length + " fruits");
        System.out.println("Filter state: " + filter);

        System.out.println("\n=== Membership Tests ===");
        for (String item : items) {
            System.out.println("Contains '" + item + "': " + filter.contains(item) + " (expected: true)");
        }

        String[] notAdded = {"mango", "nectarine", "orange", "papaya", "quince"};
        int falsePositives = 0;
        System.out.println("\n=== False Positive Analysis ===");
        for (String item : notAdded) {
            boolean result = filter.contains(item);
            if (result) {
                falsePositives++;
                System.out.println("Contains '" + item + "': " + result + " (FALSE POSITIVE)");
            } else {
                System.out.println("Contains '" + item + "': " + result + " (correct)");
            }
        }
        System.out.println("\nFalse positives: " + falsePositives + "/" + notAdded.length);

        System.out.println("\n=== Large Scale Test ===");
        BloomFilter<Integer> bigFilter = new BloomFilter<>(10000, 0.001);
        System.out.println("Large filter config: " + bigFilter);

        Random rand = new Random(42);
        for (int i = 0; i < 5000; i++) {
            bigFilter.add(i);
        }
        System.out.println("After adding 5000 items: " + bigFilter);

        int fp = 0;
        int tn = 0;
        for (int i = 5000; i < 10000; i++) {
            if (bigFilter.contains(i)) fp++;
            else tn++;
        }
        System.out.println("False positives on 5000 unseen items: " + fp + "/5000 (" +
                String.format("%.2f", 100.0 * fp / 5000) + "%)");
        System.out.println("Theoretical FPR: " + String.format("%.4f", bigFilter.getCurrentFalsePositiveRate()));

        System.out.println("\n=== No False Negatives ===");
        boolean allFound = true;
        for (int i = 0; i < 5000; i++) {
            if (!bigFilter.contains(i)) {
                allFound = false;
                break;
            }
        }
        System.out.println("All added items found: " + allFound + " (expected: true)");

        System.out.println("\n=== Clear Demo ===");
        BloomFilter<String> tempFilter = new BloomFilter<>(10, 0.05);
        tempFilter.add("test");
        System.out.println("Before clear: " + tempFilter.contains("test"));
        tempFilter.clear();
        System.out.println("After clear: " + tempFilter.contains("test"));
        System.out.println("Elements after clear: " + tempFilter.getAddedElements());
    }
}
