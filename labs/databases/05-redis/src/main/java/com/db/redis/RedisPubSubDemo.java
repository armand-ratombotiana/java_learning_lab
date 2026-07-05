package com.db.redis;

import redis.clients.jedis.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates Redis Pub/Sub with Jedis.
 *
 * One thread subscribes to channels; another publishes messages.
 * Concepts: SUBSCRIBE, PUBLISH, UNSUBSCRIBE, pattern subscriptions.
 *
 * Requires: Redis running on localhost:6379
 */
public class RedisPubSubDemo {

    static final String REDIS_HOST = "localhost";
    static final int REDIS_PORT = 6379;

    public static void main(String[] args) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        // --- Subscriber thread ---
        Thread subscriber = new Thread(() -> {
            try (Jedis subJedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
                subJedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        System.out.printf("  [Subscriber] Received on '%s': %s%n", channel, message);
                        if ("exit".equalsIgnoreCase(message)) {
                            latch.countDown();
                            this.unsubscribe();
                        }
                    }

                    @Override
                    public void onSubscribe(String channel, int subscribedChannels) {
                        System.out.printf("  [Subscriber] Subscribed to '%s'%n", channel);
                    }

                    @Override
                    public void onUnsubscribe(String channel, int subscribedChannels) {
                        System.out.printf("  [Subscriber] Unsubscribed from '%s'%n", channel);
                    }
                }, "news", "alerts");
            }
        }, "subscriber-thread");
        subscriber.start();

        // Give subscriber time to connect
        TimeUnit.MILLISECONDS.sleep(500);

        // --- Publisher ---
        try (Jedis pubJedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            System.out.println("  [Publisher] Sending messages...");
            pubJedis.publish("news", "Hello from Redis Pub/Sub!");
            pubJedis.publish("alerts", "Server CPU at 85%");
            pubJedis.publish("news", "Java 21 released");
            pubJedis.publish("alerts", "Memory usage warning");

            // Use pattern subscription example (publish to a matching channel)
            pubJedis.publish("news:sports", "Team A wins!");
            System.out.println("  [Publisher] 'news:sports' published but not received (no matching sub)");

            pubJedis.publish("alerts", "exit");
        }

        latch.await(3, TimeUnit.SECONDS);
        subscriber.join(1000);
        System.out.println("\n=== Pub/Sub demo completed ===");
    }
}
