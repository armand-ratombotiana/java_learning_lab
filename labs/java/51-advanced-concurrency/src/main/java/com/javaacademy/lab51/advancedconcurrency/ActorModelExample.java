package com.javaacademy.lab51.advancedconcurrency;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple actor model simulation using a mailbox pattern.
 * Each actor has a dedicated queue (mailbox) and a virtual thread
 * that processes messages sequentially. Demonstrates the foundation
 * of actor-based concurrency without external libraries.
 */
public class ActorModelExample {

    interface Actor {
        void tell(String message);
    }

    static class SimpleActor implements Actor {
        private final String name;
        private final BlockingQueue<String> mailbox = new LinkedBlockingQueue<>();
        private final AtomicInteger processed = new AtomicInteger();

        SimpleActor(String name) {
            this.name = name;
            Thread.ofVirtual().name("actor-" + name).start(() -> {
                while (true) {
                    try {
                        String msg = mailbox.take();
                        process(msg);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }

        public void tell(String message) {
            mailbox.offer(message);
        }

        private void process(String msg) {
            System.out.println(name + " processed: " + msg + " (total: " + processed.incrementAndGet() + ")");
        }
    }

    public static void main(String[] args) throws Exception {
        var actorA = new SimpleActor("Alpha");
        var actorB = new SimpleActor("Beta");

        for (int i = 0; i < 10; i++) {
            actorA.tell("msg-" + i + "-to-A");
            actorB.tell("msg-" + i + "-to-B");
        }

        Thread.sleep(1000);
        System.out.println("Actor demo complete.");
    }
}
