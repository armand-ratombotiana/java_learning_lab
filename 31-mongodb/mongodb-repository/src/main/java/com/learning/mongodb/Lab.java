package com.learning.mongodb;

import java.util.*;
import java.util.stream.*;

public class Lab {
    static class Document {
        Map<String, Object> fields = new LinkedHashMap<>();
        Document(String id, String name, double price, String... tags) {
            fields.put("_id", id); fields.put("name", name);
            fields.put("price", price); fields.put("tags", Arrays.asList(tags));
        }
        Object get(String k) { return fields.get(k); }
        public String toString() { return fields.toString(); }
    }

    static class Collection {
        String name;
        List<Document> docs = new ArrayList<>();
        Map<String, SortedMap<Object, List<Document>>> indexes = new HashMap<>();

        Collection(String name) { this.name = name; }

        void insert(Document d) { docs.add(d); }

        List<Document> find(String field, Object value) {
            return docs.stream().filter(d -> Objects.equals(d.get(field), value)).collect(Collectors.toList());
        }

        List<Document> findWithIndex(String field, Object value) {
            var idx = indexes.computeIfAbsent(field, k -> new TreeMap<>());
            idx.computeIfAbsent(value, k -> docs.stream().filter(d -> Objects.equals(d.get(field), k)).collect(Collectors.toList()));
            return idx.getOrDefault(value, List.of());
        }

        void createIndex(String field) {
            var idx = new TreeMap<Object, List<Document>>();
            for (var d : docs) {
                Object val = d.get(field);
                if (val == null) continue;
                if (val instanceof List) {
                    for (Object v : (List<?>) val)
                        idx.computeIfAbsent(v, k -> new ArrayList<>()).add(d);
                } else {
                    idx.computeIfAbsent(val, k -> new ArrayList<>()).add(d);
                }
            }
            indexes.put(field, idx);
        }

        Map<String, Long> aggregateCount(String field) {
            return docs.stream().collect(Collectors.groupingBy(
                d -> String.valueOf(d.get(field)), Collectors.counting()));
        }

        DoubleSummaryStatistics aggregateStats(String field) {
            return docs.stream().mapToDouble(d -> ((Number) d.get(field)).doubleValue()).summaryStatistics();
        }

        List<Document> findWithProjection(String field, Object value, String... projections) {
            return find(field, value).stream().map(d -> {
                var proj = new Document(null, null, 0);
                for (String p : projections)
                    if (d.get(p) != null) proj.fields.put(p, d.get(p));
                return proj;
            }).collect(Collectors.toList());
        }
    }

    public static void main(String[] args) {
        System.out.println("=== MongoDB Concepts Lab ===\n");

        Collection products = new Collection("products");

        products.insert(new Document("1", "Laptop", 1200, "electronics", "computers"));
        products.insert(new Document("2", "Phone", 800, "electronics", "mobile"));
        products.insert(new Document("3", "Tablet", 500, "electronics", "tablets"));
        products.insert(new Document("4", "Keyboard", 100, "electronics", "accessories"));
        products.insert(new Document("5", "Monitor", 300, "electronics", "displays"));

        System.out.println("1. Documents (BSON-like structure):");
        products.docs.forEach(d -> System.out.println("   " + d));

        System.out.println("\n2. Query - find by price=800:");
        products.find("price", 800).forEach(d -> System.out.println("   " + d));

        System.out.println("\n3. Creating index on 'price' field...");
        products.createIndex("price");

        System.out.println("   Indexed query - find by price=500:");
        products.findWithIndex("price", 500).forEach(d -> System.out.println("   " + d));

        System.out.println("\n4. Aggregation - count by tags:");
        for (var e : products.aggregateCount("name").entrySet())
            System.out.println("   " + e.getKey() + ": " + e.getValue());

        System.out.println("\n5. Aggregation - price statistics:");
        var stats = products.aggregateStats("price");
        System.out.println("   Count: " + stats.getCount());
        System.out.println("   Avg: $" + String.format("%.2f", stats.getAverage()));
        System.out.println("   Min: $" + stats.getMin());
        System.out.println("   Max: $" + stats.getMax());

        System.out.println("\n6. Projection (name, price only):");
        products.findWithProjection("price", 800, "name", "price")
            .forEach(d -> System.out.println("   " + d));

        System.out.println("\n7. Multi-key index on tags array:");
        products.createIndex("tags");
        System.out.println("   Indexed query for tag 'electronics':");
        products.findWithIndex("tags", "electronics")
            .forEach(d -> System.out.println("   " + d));

        System.out.println("\n8. $regex-like search (name containing 'top'):");
        products.docs.stream()
            .filter(d -> ((String) d.get("name")).toLowerCase().contains("top"))
            .forEach(d -> System.out.println("   " + d));

        System.out.println("\n=== Lab Complete ===");
    }
}
