package com.learning.pulsar;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class Lab {
    record Message(String key, byte[] data, long publishTime, String producerName) {
        String body() { return new String(data); }
    }

    static class Topic {
        final String name;
        final List<Subscription> subscriptions = new CopyOnWriteArrayList<>();
        final List<Message> messages = new CopyOnWriteArrayList<>();

        Topic(String name) { this.name = name; }

        void publish(Message msg) {
            messages.add(msg);
            for (var sub : subscriptions) sub.deliver(msg);
            System.out.println("  [Topic:" + name + "] Published: " + msg.body());
        }

        Subscription subscribe(String name, SubscriptionType type) {
            var sub = new Subscription(name, type, this);
            subscriptions.add(sub);
            return sub;
        }
    }

    enum SubscriptionType { EXCLUSIVE, SHARED, FAILOVER }

    static class Subscription {
        final String name;
        final SubscriptionType type;
        final Topic topic;
        final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
        int lastRead = -1;

        Subscription(String name, SubscriptionType type, Topic topic) {
            this.name = name;
            this.type = type;
            this.topic = topic;
        }

        void deliver(Message msg) {
            switch (type) {
                case EXCLUSIVE -> queue.offer(msg);
                case SHARED -> queue.offer(msg);
                case FAILOVER -> queue.offer(msg);
            }
        }

        Optional<Message> receive() {
            var msg = queue.poll();
            if (msg != null) lastRead++;
            return Optional.ofNullable(msg);
        }

        void acknowledge() { lastRead++; }

        void replay() {
            for (int i = 0; i <= lastRead; i++) queue.offer(topic.messages.get(i));
        }
    }

    static class Producer {
        final String name;
        final Topic topic;

        Producer(String name, Topic topic) { this.name = name; this.topic = topic; }

        void send(String key, String body) {
            topic.publish(new Message(key, body.getBytes(), System.currentTimeMillis(), name));
        }
    }

    static class PulsarClient {
        final Map<String, Topic> topics = new ConcurrentHashMap<>();

        Topic createTopic(String name) {
            return topics.computeIfAbsent(name, Topic::new);
        }

        Producer createProducer(String name, Topic topic) {
            return new Producer(name, topic);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Apache Pulsar: Messaging Concepts ===");

        var client = new PulsarClient();
        var ordersTopic = client.createTopic("persistent://public/default/orders");
        var notificationsTopic = client.createTopic("persistent://public/default/notifications");

        // Producers
        var orderProducer = client.createProducer("order-producer-1", ordersTopic);
        var notifProducer = client.createProducer("notif-producer-1", notificationsTopic);

        // Subscriptions
        System.out.println("\n--- Exclusive Subscription ---");
        var exclusiveSub = ordersTopic.subscribe("order-processor-1", SubscriptionType.EXCLUSIVE);

        orderProducer.send("order-1", "{\"id\":1,\"item\":\"laptop\",\"qty\":1}");
        orderProducer.send("order-2", "{\"id\":2,\"item\":\"mouse\",\"qty\":5}");

        exclusiveSub.receive().ifPresent(m -> System.out.println("  [Exclusive] Consumed: " + m.body()));
        exclusiveSub.receive().ifPresent(m -> System.out.println("  [Exclusive] Consumed: " + m.body()));

        // Shared subscription
        System.out.println("\n--- Shared Subscription ---");
        var sharedSub1 = ordersTopic.subscribe("order-worker-1", SubscriptionType.SHARED);
        var sharedSub2 = ordersTopic.subscribe("order-worker-2", SubscriptionType.SHARED);

        orderProducer.send("order-3", "{\"id\":3,\"item\":\"keyboard\",\"qty\":2}");
        orderProducer.send("order-4", "{\"id\":4,\"item\":\"monitor\",\"qty\":1}");

        sharedSub1.receive().ifPresent(m -> System.out.println("  [Shared-1] Consumed: " + m.body()));
        sharedSub2.receive().ifPresent(m -> System.out.println("  [Shared-2] Consumed: " + m.body()));

        // Multi-topic
        System.out.println("\n--- Multi-Tenancy ---");
        var tenantTopic = client.createTopic("persistent://tenant-a/ns1/alerts");
        var tenantProducer = client.createProducer("alert-producer", tenantTopic);
        var tenantSub = tenantTopic.subscribe("alert-consumer", SubscriptionType.EXCLUSIVE);
        tenantProducer.send("alert-1", "{\"severity\":\"critical\",\"msg\":\"CPU overload\"}");
        tenantSub.receive().ifPresent(m -> System.out.println("  [Tenant-A] " + m.body()));

        // Message replay (dead letter / retry)
        System.out.println("\n--- Message Replay (Retry) ---");
        var retryTopic = client.createTopic("persistent://public/default/retry");
        var retrySub = retryTopic.subscribe("retry-handler", SubscriptionType.EXCLUSIVE);
        var retryProd = client.createProducer("retry-producer", retryTopic);
        retryProd.send("retry-1", "{\"attempt\":1,\"msg\":\"process this\"}");
        var msg = retrySub.receive().orElseThrow();
        System.out.println("  [Retry] Received: " + msg.body() + " - processing failed, will retry...");
        retrySub.replay();
        retrySub.receive().ifPresent(m -> System.out.println("  [Retry] Replayed: " + m.body()));

        // Geo-replication
        System.out.println("\n--- Geo-Replication (Simulated) ---");
        var usWest = client.createTopic("persistent://pulsar/us-west/events");
        var usEast = client.createTopic("persistent://pulsar/us-east/events");

        var usWestProd = client.createProducer("us-west-producer", usWest);
        usWestProd.send("evt-1", "{\"region\":\"us-west\",\"event\":\"deploy\"}");

        System.out.println("  [Geo-Replication] us-west message replicated to us-east");
        System.out.println("  us-west messages: " + usWest.messages.size());
        System.out.println("  Total topics: " + client.topics.size());
    }
}
