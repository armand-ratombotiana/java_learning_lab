package com.learning.sparkflink;

import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;
import java.util.function.*;

public class Lab {

    static class RDD<T> {
        private final List<T> data;
        RDD(List<T> data) { this.data = data; }

        <R> RDD<R> map(Function<T, R> f) {
            return new RDD<>(data.stream().map(f).collect(Collectors.toList()));
        }

        RDD<T> filter(java.util.function.Predicate<T> p) {
            return new RDD<>(data.stream().filter(p).collect(Collectors.toList()));
        }

        Map<T, Long> countByValue() {
            return data.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        }

        T reduce(BinaryOperator<T> op) {
            return data.stream().reduce(op).orElseThrow();
        }

        List<T> collect() { return data; }
    }

    static class DataFrame {
        private final List<Map<String, Object>> rows;

        DataFrame(List<Map<String, Object>> rows) { this.rows = rows; }

        DataFrame filter(String column, Object value) {
            return new DataFrame(rows.stream()
                .filter(r -> Objects.equals(r.get(column), value))
                .collect(Collectors.toList()));
        }

        DataFrame select(String... columns) {
            return new DataFrame(rows.stream()
                .map(r -> {
                    var m = new HashMap<String, Object>();
                    for (var c : columns) m.put(c, r.get(c));
                    return m;
                })
                .collect(Collectors.toList()));
        }

        <R> DataFrame groupBy(String column, String aggColumn, Function<List<Object>, R> agg) {
            var grouped = rows.stream()
                .collect(Collectors.groupingBy(r -> r.get(column),
                    Collectors.mapping(r -> r.get(aggColumn), Collectors.toList())));
            var result = new ArrayList<Map<String, Object>>();
            grouped.forEach((key, values) -> {
                var m = new HashMap<String, Object>();
                m.put(column, key);
                m.put(aggColumn + "_" + agg, agg.apply(values));
                result.add(m);
            });
            return new DataFrame(result);
        }

        void show() { rows.forEach(System.out::println); }
    }

    public static void main(String[] args) {
        System.out.println("=== Spark & Flink Lab ===\n");

        sparkRdd();
        dataFrames();
        transformations();
        streamProcessing();
        flinkConcepts();
    }

    static void sparkRdd() {
        System.out.println("--- Spark RDD (Resilient Distributed Dataset) ---");
        var rdd = new RDD<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        var evens = rdd.filter(n -> n % 2 == 0);
        var squared = evens.map(n -> n * n);
        var sum = squared.reduce(Integer::sum);

        System.out.println("  Original: " + rdd.collect());
        System.out.println("  Filter even: " + evens.collect());
        System.out.println("  Map square: " + squared.collect());
        System.out.println("  Reduce sum: " + sum);

        System.out.println("""
  RDD operations:
  Transformations: map, filter, flatMap, distinct, union (lazy)
  Actions: reduce, collect, count, saveAsTextFile (eager)
  RDD lineage: DAG of transformations for fault recovery
    """);
    }

    static void dataFrames() {
        System.out.println("\n--- Spark DataFrame (Dataset<Row>) ---");
        var data = List.of(
            Map.<String, Object>of("name", "Alice", "age", 30, "city", "NYC"),
            Map.<String, Object>of("name", "Bob", "age", 25, "city", "SF"),
            Map.<String, Object>of("name", "Carol", "age", 35, "city", "NYC"),
            Map.<String, Object>of("name", "Dave", "age", 28, "city", "SF")
        );
        var df = new DataFrame(data);

        System.out.println("  All data:");
        df.show();
        System.out.println("  Filtered (city=NYC):");
        df.filter("city", "NYC").show();
        System.out.println("  Selected (name, age):");
        df.select("name", "age").show();
        System.out.println("  Grouped (city, avg age):");
        df.groupBy("city", "age", vals -> vals.stream()
            .mapToInt(v -> (Integer) v).average().orElse(0)).show();
    }

    static void transformations() {
        System.out.println("\n--- Transformations & Actions ---");
        var wordCount = """
            spark flink spark hadoop
            flink spark hadoop spark
            spark spark flink
            """;

        var counts = Arrays.stream(wordCount.split("\\s+"))
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        System.out.println("  Word count:");
        counts.forEach((word, count) -> System.out.println("    " + word + ": " + count));

        System.out.println("""
  Lazy evaluation:
  Transformations build DAG, no computation until action
  Catalyst optimizer: query plan optimization
  Tungsten: off-heap memory + code generation

  Physical plan: how to execute (shuffle, sort, join)
  Shuffle: data redistribution across partitions (expensive)
  Narrow dependency: each partition contributes to one output partition
  Wide dependency: each partition contributes to multiple output partitions (shuffle)
    """);
    }

    static void streamProcessing() {
        System.out.println("\n--- Stream Processing ---");
        class Event {
            final long timestamp;
            final String key;
            final int value;
            Event(String key, int value) { this.timestamp = System.currentTimeMillis(); this.key = key; this.value = value; }
            public String toString() { return key + "=" + value + " @ " + timestamp; }
        }

        var events = new LinkedBlockingQueue<Event>();
        var results = new CopyOnWriteArrayList<String>();

        var processor = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    var e = events.poll(100, TimeUnit.MILLISECONDS);
                    if (e != null) results.add("Processed: " + e);
                }
            } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
        });
        processor.start();

        for (int i = 0; i < 5; i++) events.offer(new Event("k" + i, i * 10));
        try { processor.join(); } catch (InterruptedException e) {}
        results.forEach(r -> System.out.println("  " + r));

        System.out.println("""
  Spark Streaming (micro-batch):
  Flink (true streaming, event-at-a-time):
  - Event time processing
  - Watermarks for late data handling
  - Stateful processing (ValueState, MapState)
  - Savepoints for fault tolerance
    """);
    }

    static void flinkConcepts() {
        System.out.println("--- Apache Flink Concepts ---");
        System.out.println("""
  Flink vs Spark Streaming:

  Flink:                     | Spark:
  True streaming             | Micro-batch (milliseconds)
  Event-time native          | Event-time via watermark
  Stateful via KeyedState    | Stateful via mapWithState
  Savepoints for upgrade     | Checkpoints
  Flink SQL                  | Spark SQL

  Flink runtime:
  JobManager (coordinator) -> TaskManagers (workers)
  Slot = unit of parallelism
  Exactly-once semantics via checkpointing

  Stream processing:
  DataStream API: map, flatMap, filter, keyBy, window
  Flink SQL: SELECT * FROM clicks GROUP BY TUMBLE(ts, INTERVAL '1' HOUR)
  CEP (Complex Event Processing): pattern matching on streams

  State backends:
  MemoryStateBackend: in-memory (dev only)
  FsStateBackend: on heap, checkpoint to filesystem
  RocksDBStateBackend: off-heap, disk-backed (large state)
    """);
    }
}
