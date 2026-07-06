package com.javaacademy.lab51.advancedconcurrency;

import java.util.concurrent.*;
import java.util.concurrent.Flow.*;

/**
 * Demonstrates the Reactive Streams Flow API (java.util.concurrent.Flow).
 * Implements a custom Publisher that emits integers and a Subscriber
 * that processes them with backpressure support.
 */
public class ReactiveStreamExample {

    public static void main(String[] args) throws InterruptedException {
        var publisher = new RangePublisher(1, 20);
        var subscriber = new PrintSubscriber(3); // request 3 at a time

        publisher.subscribe(subscriber);

        Thread.sleep(1000);
        System.out.println("Reactive stream demo complete.");
    }

    static class RangePublisher implements Publisher<Integer> {
        private final int start, end;

        RangePublisher(int start, int end) { this.start = start; this.end = end; }

        public void subscribe(Subscriber<? super Integer> subscriber) {
            var subscription = new RangeSubscription(subscriber, start, end);
            subscriber.onSubscribe(subscription);
        }
    }

    static class RangeSubscription implements Subscription {
        private final Subscriber<? super Integer> subscriber;
        private final int end;
        private int current;
        private long requested = 0;
        private boolean cancelled = false;

        RangeSubscription(Subscriber<? super Integer> subscriber, int start, int end) {
            this.subscriber = subscriber;
            this.current = start;
            this.end = end;
        }

        public synchronized void request(long n) {
            if (n <= 0) {
                subscriber.onError(new IllegalArgumentException("request > 0 required"));
                return;
            }
            requested += n;
            drain();
        }

        public void cancel() { cancelled = true; }

        private void drain() {
            Thread.ofVirtual().start(() -> {
                while (!cancelled && requested > 0 && current <= end) {
                    synchronized (this) {
                        if (requested == 0 || cancelled) break;
                        subscriber.onNext(current++);
                        requested--;
                    }
                }
                if (current > end && !cancelled) {
                    subscriber.onComplete();
                }
            });
        }
    }

    static class PrintSubscriber implements Subscriber<Integer> {
        private final int initialRequest;
        private Subscription subscription;

        PrintSubscriber(int initialRequest) { this.initialRequest = initialRequest; }

        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            subscription.request(initialRequest);
        }

        public void onNext(Integer item) {
            System.out.println("Received: " + item);
            // Request more after processing
            subscription.request(1);
        }

        public void onError(Throwable t) { System.err.println("Error: " + t.getMessage()); }
        public void onComplete() { System.out.println("Stream complete"); }
    }
}
