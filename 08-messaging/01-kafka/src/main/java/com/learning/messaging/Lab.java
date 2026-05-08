package com.learning.messaging;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Apache Kafka Concepts ===\n");

        demonstrateTopicsAndPartitions();
        demonstrateProducers();
        demonstrateConsumers();
        demonstrateConsumerGroups();
        demonstrateMessageDelivery();
        demonstrateKafkaEcosystem();
    }

    private static void demonstrateTopicsAndPartitions() {
        System.out.println("--- Topics & Partitions ---");
        System.out.println("Topic  = A named category/feed for messages");
        System.out.println("Partition  = An ordered, immutable sequence of records");
        System.out.println("Offset = Unique sequential ID within a partition");
        System.out.println();
        System.out.println("Example: topic \"orders\" with 3 partitions:");
        System.out.println("  Partition 0: [offset=0: order-1] [offset=1: order-4]");
        System.out.println("  Partition 1: [offset=0: order-2] [offset=1: order-5]");
        System.out.println("  Partition 2: [offset=0: order-3]");
        System.out.println("  Key-based partitioning ensures same-key -> same partition");
    }

    private static void demonstrateProducers() {
        System.out.println("\n--- Producers ---");
        System.out.println("Publish records to a topic");
        System.out.println("acks=0  -> fire-and-forget (fastest, no guarantee)");
        System.out.println("acks=1  -> leader writes to local log");
        System.out.println("acks=all -> all in-sync replicas acknowledge (safest)");
        System.out.println();
        System.out.println("Producer Record: [key: customer-42, value: {\"order\":\"abc\"}]");
        System.out.println("  -> hash(key) % numPartitions = target partition");
    }

    private static void demonstrateConsumers() {
        System.out.println("\n--- Consumers ---");
        System.out.println("Poll records from partitions");
        System.out.println("auto.offset.reset=earliest -> read from beginning");
        System.out.println("auto.offset.reset=latest   -> read new messages only");
        System.out.println("Consumer commits offsets after processing");
        System.out.println();
        System.out.println("At-least-once:  commit after processing (may duplicate)");
        System.out.println("At-most-once:   commit before processing (may lose)");
        System.out.println("Exactly-once:   transactional producer + idempotent consumer");
    }

    private static void demonstrateConsumerGroups() {
        System.out.println("\n--- Consumer Groups ---");
        System.out.println("Group of consumers sharing a subscription to a topic");
        System.out.println("Each partition is assigned to exactly one consumer in the group");
        System.out.println("Enables horizontal scaling of consumption");
        System.out.println();
        System.out.println("Group 'order-processors' consuming 'orders' (3 partitions):");
        System.out.println("  Consumer-A: partitions [0, 1]");
        System.out.println("  Consumer-B: partitions [2]");
        System.out.println("If Consumer-B fails -> rebalance: Consumer-A gets [0, 1, 2]");
    }

    private static void demonstrateMessageDelivery() {
        System.out.println("\n--- Delivery Semantics ---");
        System.out.println("1. Fire-and-Forget        -> producer doesn't wait for ack");
        System.out.println("2. Synchronous Send        -> producer waits for ack");
        System.out.println("3. Asynchronous Send       -> callback on ack/failure");
        System.out.println("4. Transactional Send      -> atomic writes across partitions");
    }

    private static void demonstrateKafkaEcosystem() {
        System.out.println("\n--- Kafka Ecosystem ---");
        System.out.println("Kafka Connect  -> Streaming import/export from external systems");
        System.out.println("Kafka Streams  -> Stream processing library (DSL + Processor API)");
        System.out.println("KSQL          -> Streaming SQL engine atop Kafka");
        System.out.println("Schema Registry-> Manages Avro/Protobuf/JSON schemas");
        System.out.println("ZooKeeper/KRaft-> Cluster coordination & metadata management");
    }
}
