package com.learning.messaging;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== NATS Messaging Concepts ===\n");

        demonstrateCoreModel();
        demonstratePubSub();
        demonstrateRequestReply();
        demonstrateQueueGroups();
        demonstrateJetStream();
    }

    private static void demonstrateCoreModel() {
        System.out.println("--- Core NATS Model ---");
        System.out.println("NATS = Cloud-native, high-performance messaging system");
        System.out.println("Design principle: 'At-most-once' delivery in core NATS");
        System.out.println("Subjects = Lightweight topic strings (hierarchical)");
        System.out.println();
        System.out.println("Subject naming: \"order.us.east\"");
        System.out.println("  Wildcard * matches one token: \"order.*.east\"");
        System.out.println("  Wildcard > matches remaining:  \"order.us.>\"");
        System.out.println();
        System.out.println("ATMoST (Always Trust Me Over TCP) protocol:");
        System.out.println("  Lightweight, text-based, with optional binary payload framing");
    }

    private static void demonstratePubSub() {
        System.out.println("\n--- Publish/Subscribe ---");
        System.out.println("Publisher sends message to a subject");
        System.out.println("All subscribers on that subject receive the message");
        System.out.println("No persistence, no guaranteed delivery (core NATS)");
        System.out.println();
        System.out.println("Flow: Publisher -> NATS Server -> Subscriber-A, Subscriber-B");
        System.out.println("If Subscriber-B is offline, message is lost (core NATS)");
    }

    private static void demonstrateRequestReply() {
        System.out.println("\n--- Request-Reply ---");
        System.out.println("Publisher includes reply-to subject in message headers");
        System.out.println("Subscriber processes and publishes response to reply subject");
        System.out.println("Publisher waits for response (typically with timeout)");
        System.out.println();
        System.out.println("Request flow:");
        System.out.println("  1. Client publishes to \"rpc.calc\" with reply-to \"_INBOX.abc\"");
        System.out.println("  2. Calculator service receives, computes, responds to \"_INBOX.abc\"");
        System.out.println("  3. Client receives response on its inbox subject");
        System.out.println("Inbox subjects are ephemeral, unique per request");
    }

    private static void demonstrateQueueGroups() {
        System.out.println("\n--- Queue Groups ---");
        System.out.println("Load balancing across subscribers (competing consumers)");
        System.out.println("All subscribers with same queue name share messages");
        System.out.println("Each message goes to exactly one member of the group");
        System.out.println();
        System.out.println("Subject \"orders\" with queue group \"workers\":");
        System.out.println("  worker-A receives order-1, order-4");
        System.out.println("  worker-B receives order-2, order-5");
        System.out.println("  worker-C receives order-3, order-6");
    }

    private static void demonstrateJetStream() {
        System.out.println("\n--- JetStream (Persistence Layer) ---");
        System.out.println("Adds persistence, Exactly-Once, and replay to NATS");
        System.out.println("Key concepts:");
        System.out.println("  Stream    -> Append-only log of messages (like Kafka topic)");
        System.out.println("  Consumer  -> Subscription to a stream with delivery semantics");
        System.out.println("  Bucket    -> Key-value store (Object Store, KV Store)");
        System.out.println();
        System.out.println("Delivery guarantees:");
        System.out.println("  Push consumer -> stream pushes to endpoint");
        System.out.println("  Pull consumer -> client pulls batches (for work queues)");
        System.out.println("  ACK policies: Explicit, All, or None");
        System.out.println("  MaxDeliver: maximum retry attempts before dead-letter");
    }
}
