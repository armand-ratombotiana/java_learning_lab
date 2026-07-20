package com.javalab.01;

import java.util.concurrent.Flow.*;
import java.util.concurrent.SubmissionPublisher;

public class MainImplementation {
    
    public static class SimplePublisher implements Publisher<Integer> {
        private final SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>();
        public void subscribe(Subscriber<? super Integer> subscriber) { publisher.subscribe(subscriber); }
        public void emit(int value) { publisher.submit(value); }
        public void complete() { publisher.close(); }
    }
    
    public static class SimpleSubscriber implements Subscriber<Integer> {
        private Subscription subscription;
        public final java.util.List<Integer> received = new java.util.concurrent.CopyOnWriteArrayList<>();
        public void onSubscribe(Subscription s) { this.subscription = s; s.request(1); }
        public void onNext(Integer item) { received.add(item); subscription.request(1); }
        public void onError(Throwable t) { t.printStackTrace(); }
        public void onComplete() { }
    }
}
