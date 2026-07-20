package com.learning.lab21;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class Java21FeaturesUltraDeepTest {

    @Test
    void sequencedCollectionRemoveFirstLast() {
        SequencedCollection<String> seq = new ArrayDeque<>(List.of("a", "b", "c"));
        assertEquals("a", seq.removeFirst());
        assertEquals("c", seq.removeLast());
        assertEquals(1, seq.size());
    }

    @Test
    void sequencedMapPollFirstLast() {
        SequencedMap<String, Integer> map = new LinkedHashMap<>();
        map.put("x", 1);
        map.put("y", 2);
        var first = map.pollFirstEntry();
        assertEquals("x", first.getKey());
        assertEquals(1, map.size());
    }

    @Test
    void nestedRecordPattern() {
        record Container(Point p, String label) {}
        Object obj = new Container(new Point(10, 20), "test");
        if (obj instanceof Container(Point(int x, int y), String label)) {
            assertEquals(10, x);
            assertEquals(20, y);
            assertEquals("test", label);
        } else {
            fail("Nested pattern should match");
        }
    }

    @Test
    void patternSwitchWithNullGuard() {
        Object obj = null;
        String result = switch (obj) {
            case null -> "null";
            case Point p -> "point";
            default -> "other";
        };
        assertEquals("null", result);
    }

    @Test
    void patternSwitchWithSealedTypes() {
        sealed interface Shape permits Circle, Rect {}
        record Circle(double radius) implements Shape {}
        record Rect(double w, double h) implements Shape {}
        Shape s = new Circle(5.0);
        String desc = switch (s) {
            case Circle c -> "circle";
            case Rect r -> "rect";
        };
        assertEquals("circle", desc);
    }
}
