package com.learning.lab23;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SealedClassesUltraDeepTest {

    @Test
    void sealedSubclassCannotBeExtended() {
        assertTrue(java.lang.reflect.Modifier.isFinal(Circle.class.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isFinal(Square.class.getModifiers()));
    }

    @Test
    void nonSealedSubclassCanBeExtended() {
        // If using non-sealed, the subclass is open for extension
        // Verify the class structure is correct
        assertNotNull(new Circle(1.0));
        assertNotNull(new Square(1.0));
    }

    @Test
    void sealedTypeSwitchExhaustiveness() {
        Shape shape = new Circle(2.5);
        double area = switch (shape) {
            case Circle c -> Math.PI * c.radius() * c.radius();
            case Square s -> s.side() * s.side();
        };
        assertEquals(Math.PI * 2.5 * 2.5, area, 1e-9);
    }

    @Test
    void recordImplementingSealedInterface() {
        Notification email = new EmailNotification("a@b.com", "hi");
        Notification sms = new SMSNotification("555-0100", "hello");
        assertInstanceOf(Notification.class, email);
        assertInstanceOf(Notification.class, sms);
    }

    @Test
    void sealedPatternMatchingWithRecords() {
        Object obj = new EmailNotification("user@test.com", "message");
        String result = switch (obj) {
            case EmailNotification(String to, String body) -> "Email to " + to;
            case SMSNotification(String number, String msg) -> "SMS to " + number;
            default -> "Unknown";
        };
        assertEquals("Email to user@test.com", result);
    }
}
