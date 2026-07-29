package com.java.io.nio.lab05;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * File Visitor & Watcher — Main Implementation
 *
 * Demonstrates: Files.walkFileTree, SimpleFileVisitor, WatchService,
 * Files.walk, directory monitoring.
 */
public class MainImplementation {

    /**
     * Find all files with a given extension using walkFileTree.
     */
    public List<Path> findByExtension(Path startDir, String extension) throws IOException {
        List<Path> results = new ArrayList<>();
        Files.walkFileTree(startDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.toString().endsWith(extension)) {
                    results.add(file);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;  // skip inaccessible files
            }
        });
        return results;
    }

    /**
     * Calculate total size of all files in a directory tree.
     */
    public long totalSize(Path startDir) throws IOException {
        long[] size = {0};
        Files.walkFileTree(startDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                size[0] += attrs.size();
                return FileVisitResult.CONTINUE;
            }
        });
        return size[0];
    }

    /**
     * Delete directory tree recursively.
     */
    public void deleteTree(Path startDir) throws IOException {
        Files.walkFileTree(startDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Monitor directory for changes using WatchService.
     * Returns the first event kind and filename within the timeout.
     */
    public String watchDirectory(Path dir, long timeoutMs) throws IOException, InterruptedException {
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            dir.register(watcher,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);

            WatchKey key = watcher.poll(timeoutMs, TimeUnit.MILLISECONDS);
            if (key == null) return "timeout";

            for (WatchEvent<?> event : key.pollEvents()) {
                Path filename = (Path) event.context();
                key.reset();
                return event.kind().name() + ": " + filename;
            }
            return "no-events";
        }
    }

    /**
     * List directory contents using Files.walk (Stream API).
     */
    public List<String> listRecursive(Path startDir) throws IOException {
        try (Stream<Path> stream = Files.walk(startDir)) {
            return stream
                .map(p -> startDir.relativize(p).toString())
                .filter(s -> !s.isEmpty())
                .sorted()
                .collect(Collectors.toList());
        }
    }

    public static void main(String[] args) throws Exception {
        MainImplementation m = new MainImplementation();

        // Create temp directory with test files
        Path tmpDir = Files.createTempDirectory("filevisitor-test");
        tmpDir.toFile().deleteOnExit();

        Path subDir = Files.createDirectory(tmpDir.resolve("sub"));
        Files.writeString(tmpDir.resolve("a.txt"), "hello");
        Files.writeString(tmpDir.resolve("b.java"), "class B {}");
        Files.writeString(subDir.resolve("c.txt"), "world");

        // Find by extension
        List<Path> txtFiles = m.findByExtension(tmpDir, ".txt");
        assert txtFiles.size() == 2 : "Expected 2 txt files, got " + txtFiles.size();

        // Total size
        long size = m.totalSize(tmpDir);
        assert size > 0 : "Expected positive size";

        // List recursive
        List<String> listing = m.listRecursive(tmpDir);
        assert listing.contains("a.txt");
        assert listing.contains("b.java");
        assert listing.contains("sub");
        assert listing.contains("sub\\c.txt") || listing.contains("sub/c.txt");

        // WatchService test
        Path watchDir = Files.createTempDirectory("watch-test");
        watchDir.toFile().deleteOnExit();
        String event = m.watchDirectory(watchDir, 500);
        assert event.equals("timeout") : "Expected timeout with no events";

        // Trigger and watch
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try { return m.watchDirectory(watchDir, 2000); }
            catch (Exception e) { return "error"; }
        });
        Thread.sleep(200);
        Files.writeString(watchDir.resolve("newfile.txt"), "new");
        String watchResult = future.get();
        assert watchResult.startsWith("ENTRY_CREATE") || watchResult.startsWith("ENTRY_MODIFY")
            : "Expected create/modify event, got: " + watchResult;

        // Delete tree
        Path deleteDir = Files.createTempDirectory("delete-test");
        Files.writeString(deleteDir.resolve("f.txt"), "delete me");
        m.deleteTree(deleteDir);
        assert !Files.exists(deleteDir) : "Directory should have been deleted";

        System.out.println("All File Visitor & Watcher tests passed.");
    }
}
