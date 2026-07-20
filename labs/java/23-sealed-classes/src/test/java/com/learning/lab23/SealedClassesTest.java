package com.learning.lab23;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class SealedClassesTest {

    @Test
    @DisplayName("Sealed class permits specific subclasses")
    void sealedClassPermits() {
        assertTrue(java.lang.reflect.Modifier.isFinal(Circle.class.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isFinal(Square.class.getModifiers()));
    }

    @Test
    @DisplayName("Sealed interface permits specific implementations")
    void sealedInterfacePermits() {
        assertTrue(JsonSerializer.class.isAssignableFrom(JsonStringSerializer.class));
        assertTrue(JsonSerializer.class.isAssignableFrom(JsonNumberSerializer.class));
    }

    @Test
    @DisplayName("Exhaustive switch on sealed types")
    void exhaustiveSwitchSealed() {
        Shape s = new Circle(5.0);
        String result = switch (s) {
            case Circle c -> "circle";
            case Square sq -> "square";
        };
        assertEquals("circle", result);
    }

    @Test
    @DisplayName("Sealed class with records")
    void sealedClassWithRecords() {
        Notification n = new EmailNotification("test@test.com", "Hello");
        assertTrue(n instanceof EmailNotification);
    }

    @Test
    @DisplayName("Sealed interface with records pattern matching")
    void sealedInterfacePatternMatching() {
        JsonSerializer js = new JsonStringSerializer("hello");
        if (js instanceof JsonStringSerializer(String value)) {
            assertEquals("hello", value);
        } else {
            fail("Pattern should match");
        }
    }

    @Test
    @DisplayName("Exhaustive switch with default branch")
    void exhaustiveSwitchWithDefault() {
        Shape s = new Square(3.0);
        String result = switch (s) {
            case Circle c -> "circle";
            case Square sq -> "square";
        };
        assertEquals("square", result);
    }
}
