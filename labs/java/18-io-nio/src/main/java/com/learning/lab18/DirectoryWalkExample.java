package com.learning.lab18;

import java.nio.file.*;
import java.util.stream.*;

/**
 * Demonstrates NIO directory walking and file tree traversal.
 */
public class DirectoryWalkExample {

    public static void showDirectoryWalk() throws Exception {
        System.out.println("=== Directory Walk ===");

        Path currentDir = Paths.get(".").toRealPath();

        System.out.println("Walking directory (depth 2): " + currentDir);
        try (Stream<Path> stream = Files.walk(currentDir, 2)) {
            stream
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(p -> System.out.println("  " + p.getFileName()));
        }

        System.out.println("\nListing immediate files:");
        try (Stream<Path> list = Files.list(currentDir)) {
            list.filter(Files::isRegularFile)
                .limit(5)
                .forEach(p -> System.out.println("  " + p.getFileName()));
        }

        System.out.println("\nFile attributes:");
        Path testFile = Paths.get("temp_test.txt");
        try {
            Files.createFile(testFile);
            System.out.println("  Created: " + testFile);
            System.out.println("  Size: " + Files.size(testFile));
            System.out.println("  Is readable: " + Files.isReadable(testFile));
            Files.delete(testFile);
            System.out.println("  Deleted: " + testFile);
        } catch (FileAlreadyExistsException e) {
            Files.delete(testFile);
        }
    }
}
