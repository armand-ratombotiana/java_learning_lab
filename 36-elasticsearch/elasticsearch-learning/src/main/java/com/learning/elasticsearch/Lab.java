package com.learning.elasticsearch;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class Lab {
    static class InvertedIndex {
        final Map<String, List<Integer>> index = new TreeMap<>();
        final List<Map<String, Object>> documents = new ArrayList<>();

        int addDocument(Map<String, Object> doc) {
            int id = documents.size();
            documents.add(doc);
            for (var entry : doc.entrySet()) {
                String field = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    for (String token : ((String) value).toLowerCase().split("\\W+")) {
                        if (token.isEmpty()) continue;
                        String term = field + ":" + token;
                        index.computeIfAbsent(term, k -> new ArrayList<>()).add(id);
                    }
                } else if (value instanceof Number || value instanceof Boolean) {
                    String term = field + ":" + value;
                    index.computeIfAbsent(term, k -> new ArrayList<>()).add(id);
                } else if (value instanceof List) {
                    for (Object item : (List<?>) value) {
                        String term = field + ":" + item;
                        index.computeIfAbsent(term, k -> new ArrayList<>()).add(id);
                    }
                }
            }
            return id;
        }

        List<Integer> search(String query) {
            String[] parts = query.toLowerCase().split(":");
            if (parts.length == 2) {
                return index.getOrDefault(parts[0] + ":" + parts[1], List.of());
            }
            List<Integer> results = new ArrayList<>();
            for (var entry : index.entrySet()) {
                if (entry.getKey().contains(query.toLowerCase())) {
                    results.addAll(entry.getValue());
                }
            }
            return results.stream().distinct().collect(Collectors.toList());
        }

        List<Integer> boolMust(String... terms) {
            Set<Integer> result = null;
            for (String term : terms) {
                var ids = search(term);
                if (result == null) result = new HashSet<>(ids);
                else result.retainAll(ids);
            }
            return result == null ? List.of() : new ArrayList<>(result);
        }

        List<Integer> boolShould(String... terms) {
            Set<Integer> result = new HashSet<>();
            for (String term : terms) result.addAll(search(term));
            return new ArrayList<>(result);
        }

        Map<String, Long> termsAggregation(String field) {
            Map<String, Long> termCounts = new HashMap<>();
            for (var doc : documents) {
                Object val = doc.get(field);
                String[] terms;
                if (val instanceof String s)
                    terms = s.toLowerCase().split("\\W+");
                else if (val != null)
                    terms = new String[]{val.toString().toLowerCase()};
                else
                    continue;
                for (String term : terms) {
                    if (!term.isEmpty())
                        termCounts.merge(term, 1L, Long::sum);
                }
            }
            return termCounts;
        }

        Map<String, Double> statsAggregation(String field) {
            var stats = documents.stream()
                .filter(d -> d.get(field) instanceof Number)
                .mapToDouble(d -> ((Number) d.get(field)).doubleValue())
                .summaryStatistics();
            return Map.of("count", (double) stats.getCount(), "avg", stats.getAverage(),
                "min", stats.getMin(), "max", stats.getMax(), "sum", stats.getSum());
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Elasticsearch Concepts Lab ===\n");

        InvertedIndex index = new InvertedIndex();

        System.out.println("1. Indexing Documents:");
        index.addDocument(Map.of("title", "Laptop Gaming Pro", "price", 1500, "category", "electronics", "tags", List.of("gaming", "laptop")));
        index.addDocument(Map.of("title", "Smartphone X", "price", 900, "category", "electronics", "tags", List.of("mobile", "phone")));
        index.addDocument(Map.of("title", "Wireless Mouse", "price", 50, "category", "accessories", "tags", List.of("wireless", "mouse")));
        index.addDocument(Map.of("title", "Mechanical Keyboard", "price", 120, "category", "accessories", "tags", List.of("keyboard", "mechanical")));
        index.addDocument(Map.of("title", "4K Monitor 27 inch", "price", 400, "category", "electronics", "tags", List.of("monitor", "4k")));
        System.out.println("   Indexed " + index.documents.size() + " documents");
        System.out.println("   Inverted index size: " + index.index.size() + " terms");

        System.out.println("\n2. Term Query (title:laptop):");
        index.search("title:laptop").forEach(id -> System.out.println("   [" + id + "] " + index.documents.get(id).get("title")));

        System.out.println("\n3. Full-Text Search (contains 'keyboard'):");
        index.search("keyboard").forEach(id -> System.out.println("   [" + id + "] " + index.documents.get(id).get("title")));

        System.out.println("\n4. Boolean MUST query (electronics + price range):");
        var results = index.boolMust("category:electronics");
        results.stream()
            .filter(id -> ((Number) index.documents.get(id).get("price")).doubleValue() > 100)
            .forEach(id -> System.out.println("   [" + id + "] " + index.documents.get(id).get("title") + " - $" + index.documents.get(id).get("price")));

        System.out.println("\n5. Boolean SHOULD (gaming OR 4k):");
        index.boolShould("gaming", "4k").forEach(id ->
            System.out.println("   [" + id + "] " + index.documents.get(id).get("title")));

        System.out.println("\n6. Terms Aggregation (by category):");
        var catAgg = index.termsAggregation("category");
        catAgg.forEach((term, count) -> System.out.println("   " + term + ": " + count));

        System.out.println("\n7. Metrics Aggregation (price stats):");
        var stats = index.statsAggregation("price");
        System.out.println("   count: " + stats.get("count").longValue());
        System.out.println("   avg: $" + String.format("%.2f", stats.get("avg")));
        System.out.println("   min: $" + stats.get("min").longValue());
        System.out.println("   max: $" + stats.get("max").longValue());

        System.out.println("\n8. Mapping / Analyzer concepts:");
        System.out.println("   Text field -> analyzed (tokenized, lowercased)");
        System.out.println("   Keyword field -> not analyzed (exact match)");
        System.out.println("   Standard analyzer: tokenize on word boundaries, lowercase");

        System.out.println("\n9. Shards & Replication:");
        System.out.println("   Index split into 5 primary shards");
        System.out.println("   Each shard has 1 replica for HA");
        System.out.println("   Shard routing: shard = hash(routing) % num_primary_shards");

        System.out.println("\n10. Relevance Scoring (TF-IDF/BM25):");
        System.out.println("    score = IDF * tfNorm * fieldNorm");
        System.out.println("    BM25: b=0.75, k1=1.2");

        System.out.println("\n=== Lab Complete ===");
    }
}
