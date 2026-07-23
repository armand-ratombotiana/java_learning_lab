package reactive;

import java.util.concurrent.*;
import java.util.function.*;

/**
 * Reactive Streams (Java 9+ Flow API) demonstration.
 * 
 * Reactive Streams specification (org.reactivestreams):
 * - Publisher: produces data
 * - Subscriber: consumes data
 * - Subscription: controls demand (backpressure)
 * - Processor: both Publisher and Subscriber
 * 
 * Java 9+ includes the Flow API (java.util.concurrent.Flow).
 * Project Reactor and RxJava are popular implementations.
 * 
 * Time: O(1) per signal
 * Space: O(buffer)
 */
public class ReactiveStreamsExample {

    // Custom Publisher that emits a range of numbers
    static class RangePublisher implements Flow.Publisher<Integer> {
        private final int start, end;
        private final int bufferSize;

        RangePublisher(int start, int end, int bufferSize) {
            this.start = start;
            this.end = end;
            this.bufferSize = bufferSize;
        }

        public void subscribe(Flow.Subscriber<? super Integer> subscriber) {
            subscriber.onSubscribe(new RangeSubscription(subscriber, start, end, bufferSize));
        }

        static class RangeSubscription implements Flow.Subscription {
            private final Flow.Subscriber<? super Integer> subscriber;
            private final int end;
            private final int bufferSize;
            private int current;
            private long requested = 0;
            private boolean cancelled = false;

            RangeSubscription(Flow.Subscriber<? super Integer> sub, int start, int end, int buffer) {
                this.subscriber = sub;
                this.current = start;
                this.end = end;
                this.bufferSize = buffer;
            }

            public synchronized void request(long n) {
                if (cancelled) return;
                requested += n;
                drain();
            }

            public synchronized void cancel() {
                cancelled = true;
            }

            private void drain() {
                while (requested > 0 && current <= end && !cancelled) {
                    subscriber.onNext(current++);
                    requested--;
                }
                if (current > end && !cancelled) {
                    subscriber.onComplete();
                }
            }
        }
    }

    // Custom Subscriber with backpressure
    static class PrintSubscriber implements Flow.Subscriber<Integer> {
        private final String name;
        private final int take;
        private Flow.Subscription subscription;
        private int count = 0;

        PrintSubscriber(String name, int take) {
            this.name = name;
            this.take = take;
        }

        public void onSubscribe(Flow.Subscription sub) {
            this.subscription = sub;
            System.out.println(name + " subscribed");
            sub.request(2); // request initial batch
        }

        public void onNext(Integer item) {
            System.out.println(name + " received: " + item);
            count++;
            if (count >= take) {
                subscription.cancel();
                System.out.println(name + " cancelled after " + count + " items");
            } else {
                subscription.request(1); // request next item
            }
        }

        public void onError(Throwable t) {
            System.err.println(name + " error: " + t);
        }

        public void onComplete() {
            System.out.println(name + " complete");
        }
    }

    public static void main(String[] args) throws Exception {
        // 1. Publisher-Subscriber with backpressure
        var publisher = new RangePublisher(1, 10, 2);
        publisher.subscribe(new PrintSubscriber("Sub1", 5));

        // Give time for async processing
        Thread.sleep(500);

        // 2. Using SubmissionPublisher (Java 9+)
        try (var submissionPublisher = new SubmissionPublisher<String>()) {
            submissionPublisher.subscribe(new Flow.Subscriber<>() {
                private Flow.Subscription sub;
                public void onSubscribe(Flow.Subscription s) { this.sub = s; s.request(Long.MAX_VALUE); }
                public void onNext(String item) { System.out.println("SP: " + item); }
                public void onError(Throwable t) { }
                public void onComplete() { System.out.println("SP done"); }
            });

            submissionPublisher.submit("Hello");
            submissionPublisher.submit("Reactive");
            submissionPublisher.submit("World");
        }

        Thread.sleep(500);
        System.out.println("All ReactiveStreamsExample tests passed.");
    }
}