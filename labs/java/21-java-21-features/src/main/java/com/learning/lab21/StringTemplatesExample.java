package com.learning.lab21;

/**
 * Demonstrates String templates and other Java 21 text block features.
 * Note: String templates (STR) were preview features and may vary by JDK build.
 */
public class StringTemplatesExample {

    public static void showStringFeatures() {
        System.out.println("=== Java 21 String Features ===");

        String name = "Java";
        int version = 21;

        String formatted = "Welcome to %s %d!".formatted(name, version);
        System.out.println(formatted);

        String repeat = "Java! ".repeat(3);
        System.out.println("Repeat: " + repeat);

        String block = """
                <html>
                    <body>
                        <h1>Java 21</h1>
                    </body>
                </html>
                """;
        System.out.println("Text block:\n" + block);
    }
}
