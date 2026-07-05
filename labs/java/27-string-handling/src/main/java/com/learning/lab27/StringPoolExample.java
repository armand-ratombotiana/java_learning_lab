package com.learning.lab27;

/**
 * Demonstrates String pool, interning, and immutable string behavior.
 */
public class StringPoolExample {

    public static void showStringPool() {
        System.out.println("=== String Pool & Immutability ===");

        String s1 = "Hello";
        String s2 = "Hello";
        String s3 = new String("Hello");
        String s4 = s3.intern();

        System.out.println("s1 == s2: " + (s1 == s2) + " (same pool reference)");
        System.out.println("s1 == s3: " + (s1 == s3) + " (different: heap vs pool)");
        System.out.println("s1 == s3.intern(): " + (s1 == s4) + " (interned back to pool)");

        System.out.println("s1.equals(s3): " + s1.equals(s3) + " (value equality)");

        String concat1 = "Hel" + "lo";
        System.out.println("\"Hel\" + \"lo\" == \"Hello\": " + (concat1 == s1) + " (compile-time constant)");

        String part = "lo";
        String concat2 = "Hel" + part;
        System.out.println("\"Hel\" + part == \"Hello\": " + (concat2 == s1) + " (runtime concat, different)");
    }
}
