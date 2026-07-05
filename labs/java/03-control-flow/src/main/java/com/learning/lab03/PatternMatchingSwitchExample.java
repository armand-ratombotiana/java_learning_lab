package com.learning.lab03;

/**
 * Demonstrates pattern matching for switch (Java 21 feature previewed in earlier versions).
 * Included here for control-flow context alongside traditional switch.
 */
public class PatternMatchingSwitchExample {

    public static void showPatternSwitch() {
        System.out.println("=== Pattern Matching switch ===");

        Object[] values = {"Hello", 42, 3.14, null, new int[]{1,2,3}};

        for (Object obj : values) {
            String desc = switch (obj) {
                case null -> "Null value";
                case String s when s.length() > 3 -> "Long string: " + s;
                case String s -> "Short string: " + s;
                case Integer i -> "Integer: " + (i * 2);
                case Double d -> "Double: " + d;
                default -> "Other type: " + obj.getClass().getSimpleName();
            };
            System.out.println("  " + desc);
        }
    }
}
