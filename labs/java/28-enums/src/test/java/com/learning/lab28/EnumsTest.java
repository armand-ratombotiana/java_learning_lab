package com.learning.lab28;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class EnumsTest {

    enum Day { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }

    @Test
    @DisplayName("Enum constants have ordinal values")
    void enumOrdinal() {
        assertEquals(0, Day.MONDAY.ordinal());
        assertEquals(6, Day.SUNDAY.ordinal());
    }

    @Test
    @DisplayName("Enum name returns constant name")
    void enumName() {
        assertEquals("MONDAY", Day.MONDAY.name());
    }

    @Test
    @DisplayName("Enum valueOf returns constant by name")
    void enumValueOf() {
        assertEquals(Day.FRIDAY, Day.valueOf("FRIDAY"));
    }

    @Test
    @DisplayName("Enum values returns all constants")
    void enumValues() {
        Day[] days = Day.values();
        assertEquals(7, days.length);
    }

    @Test
    @DisplayName("Enum with abstract methods per constant")
    void enumAbstractMethod() {
        Operation add = Operation.PLUS;
        assertEquals(5, add.apply(2, 3));
        Operation multiply = Operation.TIMES;
        assertEquals(6, multiply.apply(2, 3));
    }

    @Test
    @DisplayName("Enum with fields and constructor")
    void enumWithFields() {
        assertEquals(1, Status.NEW.getCode());
        assertEquals("In Progress", Status.IN_PROGRESS.getLabel());
    }

    @Test
    @DisplayName("EnumMap stores enum-keyed values")
    void enumMap() {
        var map = new java.util.EnumMap<>(Day.class);
        map.put(Day.MONDAY, "Start");
        map.put(Day.FRIDAY, "End");
        assertEquals("Start", map.get(Day.MONDAY));
        assertEquals("End", map.get(Day.FRIDAY));
    }

    @Test
    @DisplayName("EnumSet stores enum sets efficiently")
    void enumSet() {
        var weekend = java.util.EnumSet.of(Day.SATURDAY, Day.SUNDAY);
        assertTrue(weekend.contains(Day.SATURDAY));
        assertTrue(weekend.contains(Day.SUNDAY));
        assertFalse(weekend.contains(Day.MONDAY));
    }

    @Test
    @DisplayName("Switch on enum")
    void switchOnEnum() {
        Day day = Day.SATURDAY;
        String type = switch (day) {
            case SATURDAY, SUNDAY -> "weekend";
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "weekday";
        };
        assertEquals("weekend", type);
    }
}

enum Operation {
    PLUS { int apply(int a, int b) { return a + b; } },
    MINUS { int apply(int a, int b) { return a - b; } },
    TIMES { int apply(int a, int b) { return a * b; } };

    abstract int apply(int a, int b);
}

enum Status {
    NEW(1, "New"),
    IN_PROGRESS(2, "In Progress"),
    DONE(3, "Done");

    private final int code;
    private final String label;

    Status(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() { return code; }
    public String getLabel() { return label; }
}
