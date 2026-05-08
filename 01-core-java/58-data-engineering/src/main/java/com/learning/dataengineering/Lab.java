package com.learning.dataengineering;

import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;
import java.util.function.*;

public class Lab {

    record User(long id, String name, String email, String city, double spend) {}

    static class DataPipeline {
        private final List<Function<List<User>, List<User>>> transforms = new ArrayList<>();
        private final List<Consumer<List<User>>> sinks = new ArrayList<>();

        DataPipeline addTransform(Function<List<User>, List<User>> transform) {
            transforms.add(transform);
            return this;
        }

        DataPipeline addSink(Consumer<List<User>> sink) {
            sinks.add(sink);
            return this;
        }

        void execute(List<User> data) {
            var stream = data;
            for (var t : transforms) stream = t.apply(stream);
            for (var s : sinks) s.accept(stream);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Data Engineering Lab ===\n");

        etlPipeline();
        streamProcessing();
        batchProcessing();
        dataQuality();
        dataWarehousing();
    }

    static void etlPipeline() {
        System.out.println("--- ETL Pipeline ---");
        var users = generateUsers();

        var pipeline = new DataPipeline()
            .addTransform(data -> data.stream()
                .filter(u -> u.spend() > 100)
                .collect(Collectors.toList()))
            .addTransform(data -> data.stream()
                .map(u -> new User(u.id(), u.name().toUpperCase(), u.email(), u.city(), u.spend()))
                .collect(Collectors.toList()))
            .addSink(data -> System.out.println("  High-value users (" + data.size() + "):"))
            .addSink(data -> data.forEach(u ->
                System.out.printf("    %s (%s) - $%.2f%n", u.name(), u.city(), u.spend())));

        System.out.println("  ETL: Extract -> Transform (filter, map) -> Load (sink)");
        pipeline.execute(users);
    }

    static void streamProcessing() {
        System.out.println("\n--- Stream Processing (Windowing) ---");
        System.out.println("""
  Tumbling window (fixed, non-overlapping):
    [---5min---][---5min---][---5min---]
    Use: hourly aggregates, daily reports

  Sliding window (fixed, overlapping):
    [---5min---]
       [---5min---]
          [---5min---]
    Use: moving average (every 1min, window 5min)

  Session window (dynamic, based on activity gaps):
    [5 events][gap > 30s][3 events]
    Use: user session analysis

  Concepts:
  Event time vs processing time vs ingestion time
  Watermark: when to close a window (handling late data)
  Kafka Streams / Flink / Spark Structured Streaming
    """);
    }

    static void batchProcessing() {
        System.out.println("\n--- Batch Processing ---");
        var users = generateUsers();

        var totalSpend = users.stream().mapToDouble(User::spend).sum();
        var avgSpend = users.stream().mapToDouble(User::spend).average().orElse(0);
        var byCity = users.stream()
            .collect(Collectors.groupingBy(User::city, Collectors.summingDouble(User::spend)));
        var topUser = users.stream().max(Comparator.comparingDouble(User::spend));

        System.out.printf("  Total spend: $%.2f%n", totalSpend);
        System.out.printf("  Average spend: $%.2f%n", avgSpend);
        System.out.println("  Spend by city:");
        byCity.forEach((city, spend) -> System.out.printf("    %s: $%.2f%n", city, spend));
        topUser.ifPresent(u -> System.out.printf("  Top customer: %s ($%.2f)%n", u.name(), u.spend()));

        System.out.println("""
  Batch frameworks: Apache Spark, Apache Beam, Spring Batch
  Chunk-oriented processing: read -> process -> write
  Partitioning: split data into chunks for parallel processing
    """);
    }

    static void dataQuality() {
        System.out.println("\n--- Data Quality Checks ---");
        var users = new ArrayList<>(generateUsers());
        users.add(new User(0, "", null, "Unknown", -1));

        var errors = new ArrayList<String>();
        var valid = users.stream().filter(u -> {
            boolean ok = true;
            if (u.name().isBlank()) { errors.add("Empty name for " + u.id()); ok = false; }
            if (u.email() == null) { errors.add("Null email for " + u.id()); ok = false; }
            if (u.spend() < 0) { errors.add("Negative spend for " + u.id()); ok = false; }
            return ok;
        }).toList();

        System.out.println("  Valid records: " + valid.size() + "/" + users.size());
        System.out.println("  Quality errors:");
        errors.forEach(e -> System.out.println("    " + e));

        System.out.println("""
  Quality dimensions: completeness, accuracy, consistency, timeliness
  Checks: null check, schema validation, range check, uniqueness
  Tools: Apache Griffin, Great Expectations, Deequ
    """);
    }

    static void dataWarehousing() {
        System.out.println("\n--- Data Warehousing Concepts ---");
        System.out.println("""
  Star Schema:
    Fact table: sales (date_key, product_key, customer_key, amount)
    Dimension: date (year, quarter, month, day)
    Dimension: product (name, category, price)
    Dimension: customer (name, city, segment)

  ETL vs ELT:
    ETL: Transform before loading into warehouse
    ELT: Load raw data, transform in warehouse (modern approach)

  MPP databases: Snowflake, Redshift, BigQuery, ClickHouse
  Data lake: S3 / ADLS + Hive / Presto / Trino
  Lakehouse: Delta Lake, Iceberg, Hudi (ACID on data lake)

  Slowly Changing Dimensions (SCD):
  Type 1: overwrite (no history)
  Type 2: add row with valid_from/valid_to
  Type 3: add previous value column
    """);
    }

    static List<User> generateUsers() {
        return List.of(
            new User(1, "Alice", "alice@test.com", "NYC", 500.00),
            new User(2, "Bob", "bob@test.com", "SF", 250.00),
            new User(3, "Carol", "carol@test.com", "NYC", 800.00),
            new User(4, "Dave", "dave@test.com", "SF", 150.00),
            new User(5, "Eve", "eve@test.com", "CHI", 600.00)
        );
    }
}
