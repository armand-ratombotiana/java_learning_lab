package com.db.relational;

import java.util.*;

/**
 * Demonstrates database normalization: 1NF, 2NF, 3NF.
 * Shows a denormalized table and progressively normalizes it.
 */
public class NormalizationDemo {

    // === 0NF (Unnormalized) ===
    // Single table with repeating groups and redundant data.
    record DenormalizedOrder(
        Long orderId,
        String customerName,
        String customerEmail,
        List<String> products,       // "Mouse,Keyboard"  — repeating group
        List<Integer> quantities,
        List<Double> prices
    ) {}

    static DenormalizedOrder exampleDenormalized() {
        return new DenormalizedOrder(
            1L, "Alice Smith", "alice@example.com",
            List.of("Wireless Mouse", "Mechanical Keyboard"),
            List.of(2, 1),
            List.of(29.99, 89.99)
        );
    }

    // === 1NF ===
    // Each cell holds a single value; no repeating groups.
    record OrderRow1NF(
        Long orderId,
        String customerName,
        String customerEmail,
        String product,
        int quantity,
        double price
    ) {}

    static List<OrderRow1NF> toFirstNF(DenormalizedOrder d) {
        List<OrderRow1NF> rows = new ArrayList<>();
        for (int i = 0; i < d.products().size(); i++) {
            rows.add(new OrderRow1NF(
                d.orderId(), d.customerName(), d.customerEmail(),
                d.products().get(i), d.quantities().get(i), d.prices().get(i)
            ));
        }
        return rows;
    }

    // === 2NF ===
    // Separate customers into own table; order items reference orders and products.
    // (already is in 2NF after separating customer — no partial dependencies)

    record Customer2NF(Long id, String name, String email) {}
    record Product2NF(Long id, String name, double price) {}
    record Order2NF(Long id, Long customerId) {}
    record OrderItem2NF(Long id, Long orderId, Long productId, int quantity) {}

    static void showSecondNF() {
        System.out.println("""
            2NF achieved by extracting Customer into its own table.
            Partial dependencies removed: customer_name, customer_email
            no longer repeat for every order item row.
            """);
    }

    // === 3NF ===
    // All non-key attributes depend only on the primary key (no transitive dependencies).

    record Customer3NF(Long id, String name, String email) {}
    record Product3NF(Long id, String name, double price) {}
    record Order3NF(Long id, Long customerId) {}
    record OrderItem3NF(Long id, Long orderId, Long productId, int quantity, double unitPrice) {}

    static void showThirdNF() {
        System.out.println("""
            3NF: Already satisfied because every non-key column
            (unit_price) depends on the full primary key of OrderItem,
            not on another non-key column.

            Normalization levels:
            - 0NF: Repeating groups (list of products in one column)
            - 1NF: Atomic columns, no repeating groups
            - 2NF: No partial dependencies (extract Customer)
            - 3NF: No transitive dependencies
            """);
    }

    public static void main(String[] args) {
        System.out.println("=== Database Normalization Demo ===\n");

        System.out.println("--- 0NF (Denormalized) ---");
        DenormalizedOrder denorm = exampleDenormalized();
        System.out.println(denorm);

        System.out.println("\n--- 1NF (Atomic values) ---");
        List<OrderRow1NF> firstNf = toFirstNF(denorm);
        firstNf.forEach(System.out::println);

        System.out.println("\n--- 2NF ---");
        showSecondNF();

        System.out.println("--- 3NF ---");
        showThirdNF();

        System.out.println("\nFinal normalized schema (3NF):");
        System.out.println("  Customer  (id, name, email)");
        System.out.println("  Product   (id, name, price)");
        System.out.println("  Order     (id, customer_id)");
        System.out.println("  OrderItem (id, order_id, product_id, quantity, unit_price)");
    }
}
