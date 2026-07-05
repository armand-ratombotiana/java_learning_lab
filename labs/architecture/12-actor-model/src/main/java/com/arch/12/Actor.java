package com.arch.actor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Actor implements Runnable {
    protected final Mailbox mailbox = new Mailbox();
    private volatile boolean running = true;

    public abstract void onMessage(Object message);

    public void send(Object message) {
        mailbox.put(message);
    }

    public void run() {
        while (running) {
            Object message = mailbox.take();
            if (message == null) break;
            onMessage(message);
        }
    }

    public void stop() {
        running = false;
        mailbox.put(null);
    }

    public static class Mailbox {
        private final BlockingQueue<Object> queue = new LinkedBlockingQueue<>();

        public void put(Object msg) {
            try {
                queue.put(msg);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public Object take() {
            try {
                return queue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
    }
}
