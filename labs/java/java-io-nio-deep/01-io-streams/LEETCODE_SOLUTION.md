# LeetCode 12: Integer to Roman (I/O Streams Context)

> **Difficulty**: Medium | **Category**: I/O Streams — Serialization Format

## Problem

Write a program that reads a file containing integers (one per line) and writes their Roman numeral equivalents to an output file.

## Solution

Demonstrates `BufferedReader` for input and `BufferedWriter` with `FileWriter` for output.

```java
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Integer to Roman numeral converter — file I/O version.
 *
 * Reads integers from input file, writes Roman numerals to output file.
 */
public class IntegerToRomanFileIO {

    private static final int[] VALUES = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private static final String[] SYMBOLS = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

    public static String intToRoman(int num) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < VALUES.length; i++) {
            while (num >= VALUES[i]) {
                num -= VALUES[i];
                sb.append(SYMBOLS[i]);
            }
        }
        return sb.toString();
    }

    public void convertFile(String inputPath, String outputPath) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(inputPath), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outputPath), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    int num = Integer.parseInt(line);
                    writer.write(intToRoman(num));
                    writer.newLine();
                } catch (NumberFormatException e) {
                    writer.write("Error: \"" + line + "\" is not a valid integer");
                    writer.newLine();
                }
            }
        }
    }

    // ─────────────────────
    // Verification
    // ─────────────────────
    public static void main(String[] args) throws IOException {
        // Create temp input file
        File input = File.createTempFile("input", ".txt");
        input.deleteOnExit();
        File output = File.createTempFile("output", ".txt");
        output.deleteOnExit();

        try (BufferedWriter w = new BufferedWriter(new FileWriter(input))) {
            w.write("3\n58\n1994\n10\ninvalid\n0\n");
        }

        new IntegerToRomanFileIO().convertFile(input.getAbsolutePath(), output.getAbsolutePath());

        try (BufferedReader r = new BufferedReader(new FileReader(output))) {
            assert r.readLine().equals("III");
            assert r.readLine().equals("LVIII");
            assert r.readLine().equals("MCMXCIV");
            assert r.readLine().equals("X");
            assert r.readLine().startsWith("Error:");
            assert r.readLine().equals("");
        }

        System.out.println("All tests passed.");
    }
}
```

## Key I/O Concepts

| Concept | Used In |
|---------|---------|
| FileInputStream / FileOutputStream | Raw byte-level file access |
| InputStreamReader / OutputStreamWriter | Byte-to-character bridge |
| BufferedReader / BufferedWriter | Buffering for performance |
| try-with-resources | Auto-closing streams |
