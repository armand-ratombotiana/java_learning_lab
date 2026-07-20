package com.javalab.01;
import org.junit.jupiter.api.*;
import java.util.concurrent.Flow.*;
import static org.junit.jupiter.api.Assertions.*;

class MainImplementationTest {
    @Test @DisplayName("Publisher should deliver items to subscriber")
    void testPublisherSubscriber() {
        var publisher = new MainImplementation.SimplePublisher();
        var subscriber = new MainImplementation.SimpleSubscriber();
        publisher.subscribe(subscriber);
        publisher.emit(1); publisher.emit(2); publisher.emit(3);
        publisher.complete();
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        assertEquals(3, subscriber.received.size());
        assertTrue(subscriber.received.contains(1));
        assertTrue(subscriber.received.contains(3));
    }
}
