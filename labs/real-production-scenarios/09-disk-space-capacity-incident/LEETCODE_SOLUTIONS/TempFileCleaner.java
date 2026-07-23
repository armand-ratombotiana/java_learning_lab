package com.prod.solutions.diskspace;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;

/**
 * Demonstrates a temp file management system that cleans up stale
 * temporary files based on age, size thresholds, and disk pressure.
 *
 * In production: cron job or scheduled task that runs temp file cleanup,
 * combined with monitoring for temp directory growth.
 */
public class TempFileCleaner {

    static class CleanupConfig {
        final Path tempDir;
        final long maxAgeSeconds;
        final long maxTotalSizeBytes;
        final boolean dryRun;

        CleanupConfig(Path tempDir, long maxAgeMinutes, long maxTotalSizeMB, boolean dryRun) {
            this.tempDir = tempDir;
            this.maxAgeSeconds = maxAgeMinutes * 60;
            this.maxTotalSizeBytes = maxTotalSizeMB * 1024L * 1024L;
            this.dryRun = dryRun;
        }
    }

    static class CleanupResult {
        long filesDeleted;
        long bytesFreed;
        long errors;

        void printSummary() {
            System.out.printf("  Deleted: %d files, %.2f MB freed%n",
                    filesDeleted, bytesFreed / (1024.0 * 1024));
            if (errors > 0) {
                System.out.printf("  Errors: %d%n", errors);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("=== Temporary File Cleaner Demo ===\n");

        // Create a temp directory with some test files
        Path tempDir = Files.createTempDirectory("tempcleaner-demo");
        createTestFiles(tempDir);

        CleanupConfig config = new CleanupConfig(tempDir, 1, 100, false); // 1 min old, 100MB limit

        System.out.printf("Temp directory: %s%n", tempDir);
        printDiskUsage(tempDir);

        CleanupResult before = calculateUsage(tempDir);
        System.out.printf("Before cleanup: %d files, %.2f MB%n",
                before.filesDeleted, before.bytesFreed / (1024.0 * 1024));

        // Run cleanup
        CleanupResult result = cleanupTempFiles(config);

        CleanupResult after = calculateUsage(tempDir);
        System.out.printf("After cleanup:  %d files, %.2f MB%n",
                after.filesDeleted, after.bytesFreed / (1024.0 * 1024));

        System.out.printf("%nCleanup result: ");
        result.printSummary();

        System.out.printf("%nWithout temp file cleanup: disk fills with stale temporary files.%n");
        System.out.printf("With scheduled cleanup: stale files removed based on age, size, and disk pressure.%n");

        deleteDirectory(tempDir);
    }

    static CleanupResult cleanupTempFiles(CleanupConfig config) throws IOException {
        CleanupResult result = new CleanupResult();
        Instant cutoff = Instant.now().minus(config.maxAgeSeconds, ChronoUnit.SECONDS);
        long totalSize = calculateTotalSize(config.tempDir);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(config.tempDir)) {
            for (Path file : stream) {
                if (!Files.isRegularFile(file)) continue;

                try {
                    FileTime lastModified = Files.getLastModifiedTime(file);
                    boolean isOld = lastModified.toInstant().isBefore(cutoff);
                    boolean isOverLimit = totalSize > config.maxTotalSizeBytes;

                    if (isOld || isOverLimit) {
                        if (!config.dryRun) {
                            long size = Files.size(file);
                            Files.deleteIfExists(file);
                            result.filesDeleted++;
                            result.bytesFreed += size;
                            totalSize -= size;
                            System.out.printf("  Deleted: %s (%d bytes, age=%ds)%n",
                                    file.getFileName(), size,
                                    ChronoUnit.SECONDS.between(lastModified.toInstant(), Instant.now()));
                        } else {
                            System.out.printf("  Would delete: %s (dry run)%n", file.getFileName());
                        }
                    }
                } catch (IOException e) {
                    result.errors++;
                }
            }
        }

        return result;
    }

    static CleanupResult calculateUsage(Path dir) throws IOException {
        CleanupResult r = new CleanupResult();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path p : stream) {
                if (Files.isRegularFile(p)) {
                    r.filesDeleted++;
                    r.bytesFreed += Files.size(p);
                }
            }
        }
        return r;
    }

    static long calculateTotalSize(Path dir) throws IOException {
        CleanupResult r = calculateUsage(dir);
        return r.bytesFreed;
    }

    static void createTestFiles(Path dir) throws IOException {
        // Create some "old" files
        for (int i = 0; i < 5; i++) {
            Path f = dir.resolve("temp_" + i + ".tmp");
            Files.write(f, new byte[1024 * 1024]); // 1MB each
            Files.setLastModifiedTime(f, FileTime.from(Instant.now().minus(2, ChronoUnit.HOURS)));
        }
        // Create some "new" files
        for (int i = 0; i < 3; i++) {
            Path f = dir.resolve("recent_" + i + ".tmp");
            Files.write(f, new byte[512 * 1024]); // 512KB each
        }
    }

    static void printDiskUsage(Path dir) throws IOException {
        CleanupResult r = calculateUsage(dir);
        System.out.printf("Usage: %d files, %.2f MB%n",
                r.filesDeleted, r.bytesFreed / (1024.0 * 1024));
    }

    static void deleteDirectory(Path dir) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path p : stream) Files.deleteIfExists(p);
        }
        Files.deleteIfExists(dir);
    }
}
