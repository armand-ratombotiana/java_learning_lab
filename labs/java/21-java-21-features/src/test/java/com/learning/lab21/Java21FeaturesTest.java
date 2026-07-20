package com.learning.lab21;

import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class Java21FeaturesTest {

    @Test
    @DisplayName("SequencedCollection addFirst and getFirst")
    void sequencedCollectionAddFirst() {
        SequencedCollection<String> seq = new LinkedHashSet<>();
        seq.add("middle");
        seq.addFirst("first");
        seq.addLast("last");
        assertEquals("first", seq.getFirst());
        assertEquals("last", seq.getLast());
    }

    @Test
    @DisplayName("SequencedCollection reversed order")
    void sequencedCollectionReversed() {
        SequencedCollection<String> seq = new LinkedHashSet<>(List.of("a", "b", "c"));
        var reversed = seq.reversed();
        assertEquals(List.of("c", "b", "a"), new ArrayList<>(reversed));
    }

    @Test
    @DisplayName("SequencedMap putFirst and putLast")
    void sequencedMapPutFirstLast() {
        SequencedMap<String, Integer> map = new LinkedHashMap<>();
        map.put("b", 2);
        map.putFirst("a", 1);
        map.putLast("c", 3);
        assertEquals("a", map.firstEntry().getKey());
        assertEquals("c", map.lastEntry().getKey());
    }

    @Test
    @DisplayName("Record pattern matching in instanceof")
    void recordPatternInstanceOf() {
        Object obj = new Point(3, 4);
        if (obj instanceof Point(int x, int y)) {
            assertEquals(3, x);
            assertEquals(4, y);
        } else {
            fail("Should match record pattern");
        }
    }

    @Test
    @DisplayName("Pattern matching switch with records")
    void patternMatchingSwitch() {
        Object obj = new Point(1, 2);
        String result = switch (obj) {
            case Point(int x, int y) -> "Point(" + x + "," + y + ")";
            case null -> "null";
            default -> "other";
        };
        assertEquals("Point(1,2)", result);
    }

    @Test
    @DisplayName("Pattern matching switch on string")
    void patternMatchingSwitchString() {
        String s = "hello";
        int length = switch (s) {
            case null -> 0;
            case String str when str.length() > 5 -> str.length();
            case String str -> str.length();
        };
        assertEquals(5, length);
    }

    @Test
    @DisplayName("String template via STR processor")
    void stringTemplate() {
        String name = "World";
        String result = STR."Hello \{name}!";
        assertEquals("Hello World!", result);
    }
}

record Point(int x, int y) {}
