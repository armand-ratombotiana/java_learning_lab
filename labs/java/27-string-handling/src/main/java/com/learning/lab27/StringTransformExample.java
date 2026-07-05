package com.learning.lab27;

import java.util.stream.*;

/**
 * Demonstrates modern String methods: transform, indent, lines, isBlank, strip.
 */
public class StringTransformExample {

    public static void showStringModern() {
        System.out.println("=== Modern String Methods ===");

        String text = "  Hello\n  World\n  Java 21  ";

        System.out.println("Original:");
        System.out.println("'" + text + "'");

        System.out.println("strip(): '" + text.strip() + "'");
        System.out.println("stripLeading(): '" + text.stripLeading() + "'");
        System.out.println("isBlank(): " + "   ".isBlank());
        System.out.println("isEmpty(): " + "   ".isEmpty());

        System.out.println("lines():");
        text.lines().forEach(l -> System.out.println("  [" + l.strip() + "]"));

        String repeated = "Java ".repeat(3);
        System.out.println("repeated: " + repeated);

        System.out.println("transform: " + "hello".transform(s -> s.toUpperCase()));

        String indented = "Line1\nLine2".indent(4);
        System.out.println("indented:");
        System.out.println(indented);
    }
}
