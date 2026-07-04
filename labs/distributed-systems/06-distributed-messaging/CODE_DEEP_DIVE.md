# Distributed Messaging: Code Deep Dive

## Building a Custom Message Broker (Simple)

```java
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class SimpleMessageBroker {
    private final Map<String, List<Message>> topics = new ConcurrentHashMap<>();
    private final Map<String, List<Subscriber>> subscribers = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    public void createTopic(String topic) {
        topics.putIfAbsent(topic, new CopyOnWriteArrayList<>());
        subscribers.putIfAbsent(topic, new CopyOnWriteArrayList<>());
    }
    
    public void publish(String topic, Message message) {
        List<Message> topicMessages = topics.get(topic);
        if (topicMessages == null) throw new IllegalArgumentException("Topic not found");
        
        topicMessages.add(message);
        
        // Deliver to subscribers
        List<Subscriber> subs = subscribers.get(topic);
        for (Subscriber sub : subs) {
            executor.submit(() -> {
                try {
                    sub.deliver(message);
                } catch (Exception e) {
                    System.err.println("Delivery failed: " + e.getMessage());
                }
            });
        }
    }
    
    public void subscribe(String topic, Subscriber subscriber) {
        subscribers.get(topic).add(subscriber);
        // Deliver existing messages
        List<Message> existing = topics.get(topic);
        subscriber.assign(existing.size()); // Starting offset
    }
    
    public void unsubscribe(String topic, Subscriber subscriber) {
        subscribers.get(topic).remove(subscriber);
    }
    
    static class Message {
        final byte[] payload;
        final long timestamp = System.currentTimeMillis();
        
        Message(byte[] payload) { this.payload = payload; }
    }
    
    interface Subscriber {
        void deliver(Message message);
        void assign(int offset);
    }
}
```
