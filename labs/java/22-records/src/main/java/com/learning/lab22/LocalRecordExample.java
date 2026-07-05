package com.learning.lab22;

import java.util.*;

/**
 * Demonstrates local records (records declared inside methods) and records with custom methods.
 */
public class LocalRecordExample {

    public static void showLocalRecords() {
        System.out.println("=== Local Records ===");

        record Item(String name, double price) {
            public String formattedPrice() {
                return "$" + String.format("%.2f", price);
            }
        }

        List<Item> items = Arrays.asList(
            new Item("Apple", 0.99),
            new Item("Banana", 1.49),
            new Item("Cherry", 2.99)
        );

        System.out.println("Shopping list:");
        for (Item item : items) {
            System.out.println("  " + item.name() + " - " + item.formattedPrice());
        }
    }
}
