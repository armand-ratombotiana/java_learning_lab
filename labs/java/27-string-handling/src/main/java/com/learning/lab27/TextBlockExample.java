package com.learning.lab27;

/**
 * Demonstrates text blocks (Java 13+), formatted(), and modern string methods.
 */
public class TextBlockExample {

    public static void showTextBlocks() {
        System.out.println("=== Text Blocks & formatted ===");

        String json = """
                {
                    "name": "%s",
                    "version": %d,
                    "features": ["pattern matching", "records", "sealed classes"]
                }
                """.formatted("Java", 21);

        System.out.println("JSON text block:");
        System.out.println(json);

        String html = """
                <html>
                    <body>
                        <h1>%s</h1>
                        <p>%s</p>
                    </body>
                </html>
                """.formatted("Java 21 Features", "Modern Java in action");

        System.out.println("HTML text block:");
        System.out.println(html);

        String sql = """
                SELECT id, name, email
                FROM users
                WHERE active = %s
                ORDER BY name
                """.formatted(true);

        System.out.println("SQL text block:");
        System.out.println(sql);
    }
}
