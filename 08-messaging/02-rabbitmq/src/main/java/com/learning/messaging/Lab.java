package com.learning.messaging;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== RabbitMQ Concepts ===\n");

        demonstrateArchitecture();
        demonstrateExchanges();
        demonstrateBindings();
        demonstrateQueues();
        demonstrateMessageFlow();
        demonstrateAdvancedFeatures();
    }

    private static void demonstrateArchitecture() {
        System.out.println("--- Architecture ---");
        System.out.println("Producer -> Exchange -> (Bindings) -> Queues -> Consumer");
        System.out.println();
        System.out.println("Message = Header (routing_key, delivery_mode) + Body (payload)");
        System.out.println("Broker  = RabbitMQ server managing exchanges and queues");
        System.out.println("Connection = TCP link between app and broker");
        System.out.println("Channel    = Multiplexed lightweight connection over a TCP link");
    }

    private static void demonstrateExchanges() {
        System.out.println("\n--- Exchange Types ---");
        System.out.println("1. Direct     -> Routes to queue whose binding key matches exactly");
        System.out.println("   Example: routing_key=\"error\" -> queue \"error-queue\"");
        System.out.println();
        System.out.println("2. Topic      -> Routes by wildcard pattern matching");
        System.out.println("   * matches one word, # matches zero or more");
        System.out.println("   Example: \"us.#\" matches \"us.ny.buy\", \"*.ny.*\" matches \"order.ny.buy\"");
        System.out.println();
        System.out.println("3. Fanout     -> Routes to ALL bound queues (broadcast)");
        System.out.println("   Example: exchange -> queue-A, queue-B, queue-C (all receive)");
        System.out.println();
        System.out.println("4. Headers    -> Routes based on header attributes (not routing key)");
    }

    private static void demonstrateBindings() {
        System.out.println("\n--- Bindings ---");
        System.out.println("Binding = a link between an exchange and a queue");
        System.out.println("Defines the routing rule (routing key or pattern)");
        System.out.println();
        System.out.println("Example bindings:");
        System.out.println("  Exchange \"order.topic\" -> Queue \"order-eu\"  with key \"order.eu.#\"");
        System.out.println("  Exchange \"order.topic\" -> Queue \"order-us\"  with key \"order.us.#\"");
        System.out.println("  Exchange \"order.fanout\" -> Queue \"audit-log\" (fanout -> no key)");
    }

    private static void demonstrateQueues() {
        System.out.println("\n--- Queue Types ---");
        System.out.println("1. Classic Queue -> Standard replicated queue (mirrored)");
        System.out.println("2. Quorum Queue  -> High consistency, RAFT-based replication");
        System.out.println("3. Stream Queue  -> Append-only log (immutable, replayable)");
        System.out.println();
        System.out.println("Queue properties: durable (survives restart),");
        System.out.println("  exclusive (single consumer), auto-delete (last consumer gone)");
        System.out.println("Dead Letter Queue (DLQ) -> rejected/expired messages go here");
    }

    private static void demonstrateMessageFlow() {
        System.out.println("\n--- Message Flow ---");
        System.out.println("1. Producer publishes to exchange with routing_key");
        System.out.println("2. Exchange matches routing_key against bindings");
        System.out.println("3. Message placed into matched queue(s)");
        System.out.println("4. Consumer pulls message (basic.get) or is pushed (basic.consume)");
        System.out.println("5. Consumer sends basic.ack (acknowledge) when done");
        System.out.println("6. If nack/connection lost -> message re-queued (or DLQ)");
    }

    private static void demonstrateAdvancedFeatures() {
        System.out.println("\n--- Advanced Features ---");
        System.out.println("Publisher Confirms -> Producer knows message was accepted");
        System.out.println("Consumer Prefetch   -> Controls how many unacked messages per consumer");
        System.out.println("Alternate Exchange  -> Routes unroutable messages to fallback");
        System.out.println("Shovel / Federation -> Cross-cluster message replication");
        System.out.println("Management Plugin   -> HTTP API + Web UI for monitoring");
    }
}
