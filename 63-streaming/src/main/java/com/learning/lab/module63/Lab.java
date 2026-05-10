package com.learning.lab.module63;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 63: Streaming Lab ===\n");

        System.out.println("1. Apache Flink:");
        System.out.println("   - DataStream API: unbounded streams");
        System.out.println("   - DataSet API: bounded data");
        System.out.println("   - Table API & SQL");
        System.out.println("   - Exactly-once processing");

        System.out.println("\n2. Flink Operators:");
        System.out.println("   - Source: Kafka, socket, file");
        System.out.println("   - Transform: map, filter, flatMap");
        System.out.println("   - Keyed: keyBy, window");
        System.out.println("   - Sink: print, write, Kafka");

        System.out.println("\n3. Windowing:");
        System.out.println("   - Tumbling: non-overlapping");
        System.out.println("   - Sliding: overlapping");
        System.out.println("   - Session: gap-based");
        System.out.println("   - Global: all events");

        System.out.println("\n4. State Management:");
        System.out.println("   - Keyed state: per key");
        System.out.println("   - Operator state: per instance");
        System.out.println("   - Checkpointing");
        System.out.println("   - Savepoints");

        System.out.println("\n5. Apache Storm:");
        System.out.println("   - Spout: data source");
        System.out.println("   - Bolt: processing unit");
        System.out.println("   - Topology: spouts + bolts");
        System.out.println("   - Trident for micro-batching");

        System.out.println("\n6. Fault Tolerance:");
        System.out.println("   - Checkpoint interval");
        System.out.println("   - Exactly-once vs at-least-once");
        System.out.println("   - State recovery");

        System.out.println("\n7. Performance:");
        System.out.println("   - Parallelism settings");
        System.out.println("   - Task manager slots");
        System.out.println("   - Network buffering");

        System.out.println("\n=== Streaming Lab Complete ===");
    }
}