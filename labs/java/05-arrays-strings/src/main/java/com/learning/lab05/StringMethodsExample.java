package com.learning.lab05;

/**
 * Demonstrates String methods and StringBuilder for efficient string manipulation.
 */
public class StringMethodsExample {

    public static void showStrings() {
        System.out.println("=== String Methods ===");

        String str = "  Hello, Java World!  ";
        System.out.println("Original: '" + str + "'");
        System.out.println("trim(): '" + str.trim() + "'");
        System.out.println("toLowerCase(): " + str.toLowerCase().trim());
        System.out.println("toUpperCase(): " + str.toUpperCase().trim());
        System.out.println("length(): " + str.length());
        System.out.println("charAt(1): " + str.charAt(1));
        System.out.println("substring(8, 12): " + str.substring(8, 12));
        System.out.println("contains('Java'): " + str.contains("Java"));
        System.out.println("startsWith('  He'): " + str.startsWith("  He"));
        System.out.println("indexOf('Java'): " + str.indexOf("Java"));
        System.out.println("replace('Java', 'Kotlin'): " + str.replace("Java", "Kotlin").trim());

        String joined = String.join(", ", "A", "B", "C");
        System.out.println("String.join: " + joined);

        System.out.println();
        System.out.println("=== StringBuilder ===");

        StringBuilder sb = new StringBuilder();
        sb.append("Hello");
        sb.append(" ");
        sb.append("World");
        sb.append("!");
        System.out.println("StringBuilder result: " + sb);
        sb.insert(5, " Beautiful");
        System.out.println("After insert: " + sb);
        sb.reverse();
        System.out.println("After reverse: " + sb);
    }
}
