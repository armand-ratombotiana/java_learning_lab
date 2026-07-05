package com.learning.lab18;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Demonstrates NIO Files and Paths API for modern file I/O.
 */
public class NioFilesExample {

    public static void showNioFiles() throws Exception {
        System.out.println("=== NIO Files & Paths ===");

        Path path = Paths.get("nio_example.txt");

        List<String> lines = Arrays.asList("Line 1 from NIO", "Line 2 from NIO", "Line 3");
        Files.write(path, lines, StandardCharsets.UTF_8);
        System.out.println("Written " + lines.size() + " lines to " + path.toAbsolutePath());

        List<String> readLines = Files.readAllLines(path, StandardCharsets.UTF_8);
        System.out.println("Read back:");
        readLines.forEach(l -> System.out.println("  " + l));

        System.out.println("File size: " + Files.size(path) + " bytes");
        System.out.println("Is regular file: " + Files.isRegularFile(path));
        System.out.println("Last modified: " + Files.getLastModifiedTime(path));

        Files.copy(path, Paths.get("nio_copy.txt"), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Copied to nio_copy.txt");

        Files.delete(path);
        Files.delete(Paths.get("nio_copy.txt"));
        System.out.println("Files cleaned up");
    }
}
