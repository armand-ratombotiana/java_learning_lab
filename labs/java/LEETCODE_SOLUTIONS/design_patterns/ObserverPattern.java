package design_patterns;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer Pattern — defines a one-to-many dependency between objects.
 * When one object changes state, all its dependents are notified.
 * 
 * Java's built-in: java.util.Observer/Observable (deprecated),
 * or use PropertyChangeListener, or modern Flow API (Java 9+).
 * 
 * Time: O(n) for notify where n = observers
 * Space: O(n)
 */
public class ObserverPattern {

    // Observer interface
    interface Observer {
        void update(String message);
    }

    // Subject (Observable)
    static class NewsPublisher {
        private final List<Observer> observers = new ArrayList<>();
        private String latestNews;

        void subscribe(Observer o) { observers.add(o); }
        void unsubscribe(Observer o) { observers.remove(o); }

        void publish(String news) {
            this.latestNews = news;
            observers.forEach(o -> o.update(news));
        }
    }

    // Concrete observers
    static class EmailSubscriber implements Observer {
        private final String name;
        EmailSubscriber(String name) { this.name = name; }
        public void update(String message) {
            System.out.println(name + " received: " + message);
        }
    }

    // Modern Java 9+ Flow API approach
    static class NewsPublisherFlow {
        private final java.util.concurrent.SubmissionPublisher<String> publisher =
            new java.util.concurrent.SubmissionPublisher<>();

        java.util.concurrent.Flow.Publisher<String> getPublisher() { return publisher; }

        void publish(String news) { publisher.submit(news); }

        void close() { publisher.close(); }
    }

    static class NewsSubscriberFlow implements java.util.concurrent.Flow.Subscriber<String> {
        private final String name;
        private java.util.concurrent.Flow.Subscription subscription;

        NewsSubscriberFlow(String name) { this.name = name; }

        public void onSubscribe(java.util.concurrent.Flow.Subscription sub) {
            this.subscription = sub;
            sub.request(1); // request one item
        }

        public void onNext(String item) {
            System.out.println(name + " (Flow) received: " + item);
            subscription.request(1); // request next
        }

        public void onError(Throwable t) { t.printStackTrace(); }
        public void onComplete() { System.out.println(name + " done"); }
    }

    public static void main(String[] args) throws Exception {
        // Traditional observer
        NewsPublisher publisher = new NewsPublisher();
        EmailSubscriber alice = new EmailSubscriber("Alice");
        EmailSubscriber bob = new EmailSubscriber("Bob");
        publisher.subscribe(alice);
        publisher.subscribe(bob);
        publisher.publish("Java 21 released!");

        // Flow API
        NewsPublisherFlow flowPub = new NewsPublisherFlow();
        flowPub.getPublisher().subscribe(new NewsSubscriberFlow("Charlie"));
        flowPub.publish("Virtual threads are here!");
        flowPub.close();

        System.out.println("All ObserverPattern tests passed.");
    }
}