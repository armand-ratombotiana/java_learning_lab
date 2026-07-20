package com.learning.lab28;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class EnumsUltraDeepTest {

    enum Color { RED, GREEN, BLUE }

    @Test
    void enumEqualityUsingEquals() {
        assertEquals(Color.RED, Color.RED);
        assertNotEquals(Color.RED, Color.GREEN);
    }

    @Test
    void enumCompareToByOrdinal() {
        assertTrue(Color.RED.compareTo(Color.GREEN) < 0);
        assertTrue(Color.BLUE.compareTo(Color.GREEN) > 0);
    }

    @Test
    void enumSwitchWithExhaustiveness() {
        Color c = Color.GREEN;
        String hex = switch (c) {
            case RED -> "#FF0000";
            case GREEN -> "#00FF00";
            case BLUE -> "#0000FF";
        };
        assertEquals("#00FF00", hex);
    }

    @Test
    void enumMapSizeMatchesEnumConstants() {
        var map = new EnumMap<Color, String>(Color.class);
        assertEquals(0, map.size());
        map.put(Color.RED, "red");
        assertEquals(1, map.size());
    }

    @Test
    void enumSetComplementOf() {
        var all = EnumSet.allOf(Color.class);
        var red = EnumSet.of(Color.RED);
        var complement = EnumSet.complementOf(red);
        assertEquals(2, complement.size());
        assertTrue(complement.contains(Color.GREEN));
        assertTrue(complement.contains(Color.BLUE));
    }

    @Test
    void enumConstantsAreSingletons() {
        assertSame(Color.RED, Color.valueOf("RED"));
    }

    @Test
    void enumDeclaringClass() {
        assertSame(Color.class, Color.RED.getDeclaringClass());
    }
}
