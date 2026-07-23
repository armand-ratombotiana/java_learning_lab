package com.prod.solutions.diskspace;

import java.io.*;
import java.nio.file.*;
import java.util.zip.GZIPOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Demonstrates a configurable log rotation mechanism with compression.
 * Prevents disk-full incidents by automatically rotating, compressing,
 * and cleaning up old log files.
 *
 * In production: use logrotate (Linux), log4j2 RollingFileAppender,
 * or logback SizeAndTimeBasedRollingPolicy.
 */
public class LogRotationExample {

    static class LogRotator {
        private final Path logDir;
        private final long maxFileSizeBytes;
        private final int maxHistory;
        private final boolean compressOldFiles;
        private Path currentLogFile;
        private long currentSize;

        public LogRotator(Path logDir, long maxFileSizeMB, int maxHistory, boolean compress) {
            this.logDir = logDir;
            this.maxFileSizeBytes = maxFileSizeMB * 1024 * 1024;
            this.maxHistory = maxHistory;
            this.compressOldFiles = compress;
            this.currentLogFile = logDir.resolve("app.log");
            this.currentSize = 0;
        }

        public void write(String line) throws IOException {
            byte[] bytes = (line + System.lineSeparator()).getBytes();
            currentSize += bytes.length;

            if (currentSize >= maxFileSizeBytes) {
                rotate();
            }

            Files.write(currentLogFile, bytes, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }

        void rotate() throws IOException {
            String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            Path rotatedFile = logDir.resolve("app." + timestamp + ".log");

            Files.move(currentLogFile, rotatedFile);
            currentSize = 0;

            if (compressOldFiles) {
                compress(rotatedFile);
            }

            cleanupOldFiles();
        }

        void compress(Path file) throws IOException {
            Path gzFile = file.resolveSibling(file.getFileName().toString() + ".gz");
            try (FileInputStream fis = new FileInputStream(file.toFile());
                 FileOutputStream fos = new FileOutputStream(gzFile.toFile());
                 GZIPOutputStream gzos = new GZIPOutputStream(fos)) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    gzos.write(buffer, 0, len);
                }
            }
            Files.delete(file);
            System.out.printf("  Compressed: %s -> %s (%.1f KB -> %.1f KB)%n",
                    file.getFileName(), gzFile.getFileName(),
                    Files.size(file) / 1024.0, Files.size(gzFile) / 1024.0);
        }

        void cleanupOldFiles() throws IOException {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDir, "app.*.log*")) {
                Path[] files = StreamSupport.stream(stream.spliterator(), false)
                        .sorted(Path::compareTo)
                        .toArray(Path[]::new);

                while (files.length > maxHistory) {
                    Files.deleteIfExists(files[0]);
                    System.out.printf("  Deleted old log: %s%n", files[0].getFileName());
                    files = Arrays.copyOfRange(files, 1, files.length);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Path logDir = Files.createTempDirectory("logrotation-demo");
        logDir.toFile().deleteOnExit();

        LogRotator rotator = new LogRotator(logDir, 1, 3, true); // 1MB, keep 3, compress

        System.out.println("=== Log Rotation Demo ===\n");

        // Write enough data to trigger rotation
        for (int i = 0; i < 20000; i++) {
            rotator.write("app LOG LINE %d: " + "x".repeat(100) + " [INFO] request processed OK".formatted(i));
        }

        System.out.println("\nLog directory contents:");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDir)) {
            for (Path p : stream) {
                System.out.printf("  %s (%d bytes)%n", p.getFileName(), Files.size(p));
            }
        }

        System.out.printf("%nWithout rotation: single log file would exceed disk space.%n");
        System.out.printf("With rotation: logs are rotated at %d MB, compressed, and old files cleaned.%n", 1);

        deleteDirectory(logDir);
    }

    static void deleteDirectory(Path dir) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path p : stream) {
                Files.deleteIfExists(p);
            }
        }
        Files.deleteIfExists(dir);
    }
}
