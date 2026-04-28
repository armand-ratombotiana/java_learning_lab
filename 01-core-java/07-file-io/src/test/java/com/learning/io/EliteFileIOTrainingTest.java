package com.learning.io;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for Elite File I/O Training
 *
 * Tests all 12 file I/O patterns with:
 * - Normal case scenarios
 * - Edge cases
 * - Error handling
 * - Performance characteristics
 *
 * Total: 50 test methods
 *
 * @author Elite Java Learning Platform
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Elite File I/O Training Tests")
class EliteFileIOTrainingTest {

    @TempDir
    Path tempDir;

    // ========================================================================
    // PROBLEM 1: Read Large File Line by Line Tests
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("Problem 1: Read File Stats - Small File")
    void testReadFileLazily_SmallFile() throws IOException {
        Path testFile = tempDir.resolve("test.txt");
        List<String> lines = List.of("Line 1", "Line 2", "Line 3");
        Files.write(testFile, lines);

        EliteFileIOTraining.FileStats stats =
            EliteFileIOTraining.readFileLazily(testFile);

        assertEquals(3, stats.getLineCount());
        assertTrue(stats.getTotalChars() >= 18); // "Line 1Line 2Line 3"
        assertTrue(stats.getFileSize() > 0);
    }

    @Test
    @Order(2)
    @DisplayName("Problem 1: Read File Stats - Empty File")
    void testReadFileLazily_EmptyFile() throws IOException {
        Path testFile = tempDir.resolve("empty.txt");
        Files.createFile(testFile);

        EliteFileIOTraining.FileStats stats =
            EliteFileIOTraining.readFileLazily(testFile);

        assertEquals(0, stats.getLineCount());
        assertEquals(0, stats.getTotalChars());
        assertEquals(0, stats.getFileSize());
    }

    @Test
    @Order(3)
    @DisplayName("Problem 1: Read File Stats - Large File")
    void testReadFileLazily_LargeFile() throws IOException {
        Path testFile = tempDir.resolve("large.txt");

        // Create file with 1000 lines
        try (BufferedWriter writer = Files.newBufferedWriter(testFile)) {
            for (int i = 0; i < 1000; i++) {
                writer.write("This is line number " + i);
                writer.newLine();
            }
        }

        EliteFileIOTraining.FileStats stats =
            EliteFileIOTraining.readFileLazily(testFile);

        assertEquals(1000, stats.getLineCount());
        assertTrue(stats.getTotalChars() > 19000);
    }

    // ========================================================================
    // PROBLEM 2: Copy File with Progress Tests
    // ========================================================================

    @Test
    @Order(4)
    @DisplayName("Problem 2: Copy File - Basic")
    void testCopyFileWithProgress_Basic() throws IOException {
        Path source = tempDir.resolve("source.txt");
        Path destination = tempDir.resolve("dest.txt");

        Files.writeString(source, "Test content for copying");

        List<Long> progressUpdates = new ArrayList<>();
        EliteFileIOTraining.copyFileWithProgress(source, destination,
            (bytes, total) -> progressUpdates.add(bytes));

        assertTrue(Files.exists(destination));
        assertEquals(Files.readString(source), Files.readString(destination));
        assertTrue(progressUpdates.size() > 0);
    }

    @Test
    @Order(5)
    @DisplayName("Problem 2: Copy File - Large File with Progress")
    void testCopyFileWithProgress_LargeFile() throws IOException {
        Path source = tempDir.resolve("large_source.txt");
        Path destination = tempDir.resolve("large_dest.txt");

        // Create 1MB file
        byte[] data = new byte[1024 * 1024];
        Arrays.fill(data, (byte) 'A');
        Files.write(source, data);

        List<Long> progressUpdates = new ArrayList<>();
        EliteFileIOTraining.copyFileWithProgress(source, destination,
            (bytes, total) -> {
                progressUpdates.add(bytes);
                assertTrue(bytes <= total);
            });

        assertEquals(Files.size(source), Files.size(destination));
        assertTrue(progressUpdates.size() > 1); // Should have multiple updates
    }

    @Test
    @Order(6)
    @DisplayName("Problem 2: Copy File - No Progress Callback")
    void testCopyFileWithProgress_NoCallback() throws IOException {
        Path source = tempDir.resolve("source2.txt");
        Path destination = tempDir.resolve("dest2.txt");

        Files.writeString(source, "Test content");

        assertDoesNotThrow(() ->
            EliteFileIOTraining.copyFileWithProgress(source, destination, null)
        );

        assertTrue(Files.exists(destination));
    }

    // ========================================================================
    // PROBLEM 3: Find Files by Pattern Tests
    // ========================================================================

    @Test
    @Order(7)
    @DisplayName("Problem 3: Find Files by Pattern - Regex")
    void testFindFilesByPattern_Regex() throws IOException {
        // Create test directory structure
        Files.createDirectories(tempDir.resolve("subdir"));
        Files.createFile(tempDir.resolve("test1.txt"));
        Files.createFile(tempDir.resolve("test2.txt"));
        Files.createFile(tempDir.resolve("test.log"));
        Files.createFile(tempDir.resolve("subdir/test3.txt"));

        List<Path> results = EliteFileIOTraining.findFilesByPattern(
            tempDir, ".*\\.txt");

        assertEquals(3, results.size());
        assertTrue(results.stream().allMatch(p ->
            p.getFileName().toString().endsWith(".txt")));
    }

    @Test
    @Order(8)
    @DisplayName("Problem 3: Find Files by Glob Pattern")
    void testFindFilesByGlob() throws IOException {
        Files.createFile(tempDir.resolve("file1.java"));
        Files.createFile(tempDir.resolve("file2.java"));
        Files.createFile(tempDir.resolve("file3.txt"));

        List<Path> results = EliteFileIOTraining.findFilesByGlob(
            tempDir, "*.java");

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p ->
            p.getFileName().toString().endsWith(".java")));
    }

    @Test
    @Order(9)
    @DisplayName("Problem 3: Find Files - No Matches")
    void testFindFilesByPattern_NoMatches() throws IOException {
        Files.createFile(tempDir.resolve("test.txt"));

        List<Path> results = EliteFileIOTraining.findFilesByPattern(
            tempDir, ".*\\.pdf");

        assertEquals(0, results.size());
    }

    // ========================================================================
    // PROBLEM 4: Watch Directory Tests
    // ========================================================================

    @Test
    @Order(10)
    @DisplayName("Problem 4: Directory Watcher - Create Event")
    void testDirectoryWatcher_CreateEvent() throws IOException, InterruptedException {
        EliteFileIOTraining.DirectoryWatcher watcher =
            new EliteFileIOTraining.DirectoryWatcher(tempDir);

        // Create file after watcher is set up
        Files.createFile(tempDir.resolve("newfile.txt"));

        // Poll for events
        List<EliteFileIOTraining.FileEvent> events = watcher.pollEvents(1000);

        assertFalse(events.isEmpty());
        assertTrue(events.stream().anyMatch(e ->
            e.getFileName().equals("newfile.txt")));

        watcher.close();
    }

    @Test
    @Order(11)
    @DisplayName("Problem 4: Directory Watcher - No Events")
    void testDirectoryWatcher_NoEvents() throws IOException, InterruptedException {
        EliteFileIOTraining.DirectoryWatcher watcher =
            new EliteFileIOTraining.DirectoryWatcher(tempDir);

        // Don't create any files
        List<EliteFileIOTraining.FileEvent> events = watcher.pollEvents(500);

        assertTrue(events.isEmpty());

        watcher.close();
    }

    // ========================================================================
    // PROBLEM 5: File Comparison Tests
    // ========================================================================

    @Test
    @Order(12)
    @DisplayName("Problem 5: Compare Files - Identical Small Files")
    void testCompareFiles_IdenticalSmall() throws IOException {
        Path file1 = tempDir.resolve("file1.txt");
        Path file2 = tempDir.resolve("file2.txt");

        String content = "Test content for comparison";
        Files.writeString(file1, content);
        Files.writeString(file2, content);

        assertTrue(EliteFileIOTraining.compareFiles(file1, file2));
    }

    @Test
    @Order(13)
    @DisplayName("Problem 5: Compare Files - Different Content")
    void testCompareFiles_DifferentContent() throws IOException {
        Path file1 = tempDir.resolve("file1.txt");
        Path file2 = tempDir.resolve("file2.txt");

        Files.writeString(file1, "Content A");
        Files.writeString(file2, "Content B");

        assertFalse(EliteFileIOTraining.compareFiles(file1, file2));
    }

    @Test
    @Order(14)
    @DisplayName("Problem 5: Compare Files - Different Sizes")
    void testCompareFiles_DifferentSizes() throws IOException {
        Path file1 = tempDir.resolve("file1.txt");
        Path file2 = tempDir.resolve("file2.txt");

        Files.writeString(file1, "Short");
        Files.writeString(file2, "Much longer content");

        assertFalse(EliteFileIOTraining.compareFiles(file1, file2));
    }

    @Test
    @Order(15)
    @DisplayName("Problem 5: Compare Files - Large Identical Files")
    void testCompareFiles_LargeIdentical() throws IOException {
        Path file1 = tempDir.resolve("large1.txt");
        Path file2 = tempDir.resolve("large2.txt");

        // Create 5MB files
        byte[] data = new byte[5 * 1024 * 1024];
        Arrays.fill(data, (byte) 'X');

        Files.write(file1, data);
        Files.write(file2, data);

        assertTrue(EliteFileIOTraining.compareFiles(file1, file2));
    }

    // ========================================================================
    // PROBLEM 6: Serialization Tests
    // ========================================================================

    @Test
    @Order(16)
    @DisplayName("Problem 6: Serialize and Deserialize Object")
    void testSerializeDeserialize() throws IOException, ClassNotFoundException {
        Path file = tempDir.resolve("user.ser");

        EliteFileIOTraining.SerializableUser original =
            new EliteFileIOTraining.SerializableUser(
                "testuser", "test@example.com", "secret123");
        original.incrementLoginCount();

        EliteFileIOTraining.serializeObject(original, file);

        EliteFileIOTraining.SerializableUser deserialized =
            EliteFileIOTraining.deserializeObject(file,
                EliteFileIOTraining.SerializableUser.class);

        assertEquals(original.getUsername(), deserialized.getUsername());
        assertEquals(original.getEmail(), deserialized.getEmail());
        assertEquals(original.getLoginCount(), deserialized.getLoginCount());
        assertNull(deserialized.getPassword()); // Transient field
    }

    @Test
    @Order(17)
    @DisplayName("Problem 6: Serialize - Transient Field Not Saved")
    void testSerialize_TransientField() throws IOException, ClassNotFoundException {
        Path file = tempDir.resolve("user2.ser");

        EliteFileIOTraining.SerializableUser user =
            new EliteFileIOTraining.SerializableUser(
                "user2", "user2@test.com", "password");

        EliteFileIOTraining.serializeObject(user, file);
        EliteFileIOTraining.SerializableUser loaded =
            EliteFileIOTraining.deserializeObject(file,
                EliteFileIOTraining.SerializableUser.class);

        assertNull(loaded.getPassword());
    }

    // ========================================================================
    // PROBLEM 7: CSV Tests
    // ========================================================================

    @Test
    @Order(18)
    @DisplayName("Problem 7: CSV Reader - Basic")
    void testCSVReader_Basic() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        List<String> lines = List.of(
            "Name,Age,City",
            "Alice,30,NYC",
            "Bob,25,LA"
        );
        Files.write(csvFile, lines);

        EliteFileIOTraining.CSVReader reader =
            new EliteFileIOTraining.CSVReader(csvFile);

        List<String> header = reader.readRow();
        assertEquals(List.of("Name", "Age", "City"), header);

        List<String> row1 = reader.readRow();
        assertEquals(List.of("Alice", "30", "NYC"), row1);

        reader.close();
    }

    @Test
    @Order(19)
    @DisplayName("Problem 7: CSV Reader - Quoted Fields")
    void testCSVReader_QuotedFields() throws IOException {
        Path csvFile = tempDir.resolve("quoted.csv");
        Files.writeString(csvFile, "\"Name\",\"Age, Years\",\"City\"\n");

        EliteFileIOTraining.CSVReader reader =
            new EliteFileIOTraining.CSVReader(csvFile);

        List<String> row = reader.readRow();
        assertEquals(List.of("Name", "Age, Years", "City"), row);

        reader.close();
    }

    @Test
    @Order(20)
    @DisplayName("Problem 7: CSV Reader - Escaped Quotes")
    void testCSVReader_EscapedQuotes() throws IOException {
        Path csvFile = tempDir.resolve("escaped.csv");
        Files.writeString(csvFile, "\"She said \"\"Hello\"\"\",Data\n");

        EliteFileIOTraining.CSVReader reader =
            new EliteFileIOTraining.CSVReader(csvFile);

        List<String> row = reader.readRow();
        assertEquals("She said \"Hello\"", row.get(0));

        reader.close();
    }

    @Test
    @Order(21)
    @DisplayName("Problem 7: CSV Writer - Basic")
    void testCSVWriter_Basic() throws IOException {
        Path csvFile = tempDir.resolve("output.csv");

        EliteFileIOTraining.CSVWriter writer =
            new EliteFileIOTraining.CSVWriter(csvFile);

        writer.writeRow(List.of("Name", "Age", "City"));
        writer.writeRow(List.of("Alice", "30", "NYC"));
        writer.close();

        List<String> lines = Files.readAllLines(csvFile);
        assertEquals(2, lines.size());
        assertTrue(lines.get(0).contains("Name"));
    }

    @Test
    @Order(22)
    @DisplayName("Problem 7: CSV Writer - Fields with Commas")
    void testCSVWriter_FieldsWithCommas() throws IOException {
        Path csvFile = tempDir.resolve("commas.csv");

        EliteFileIOTraining.CSVWriter writer =
            new EliteFileIOTraining.CSVWriter(csvFile);

        writer.writeRow(List.of("Name", "Address, City"));
        writer.close();

        String content = Files.readString(csvFile);
        assertTrue(content.contains("\"Address, City\""));
    }

    // ========================================================================
    // PROBLEM 8: Compression Tests
    // ========================================================================

    @Test
    @Order(23)
    @DisplayName("Problem 8: Compress and Decompress File")
    void testCompressDecompress() throws IOException {
        Path original = tempDir.resolve("original.txt");
        Path compressed = tempDir.resolve("compressed.gz");
        Path decompressed = tempDir.resolve("decompressed.txt");

        String content = "This is test content for compression testing. ".repeat(100);
        Files.writeString(original, content);

        EliteFileIOTraining.compressFile(original, compressed);
        assertTrue(Files.exists(compressed));
        assertTrue(Files.size(compressed) < Files.size(original));

        EliteFileIOTraining.decompressFile(compressed, decompressed);
        assertEquals(Files.readString(original), Files.readString(decompressed));
    }

    @Test
    @Order(24)
    @DisplayName("Problem 8: Create ZIP Archive")
    void testCreateZipArchive() throws IOException {
        Path file1 = tempDir.resolve("file1.txt");
        Path file2 = tempDir.resolve("file2.txt");
        Files.writeString(file1, "Content 1");
        Files.writeString(file2, "Content 2");

        Path zipFile = tempDir.resolve("archive.zip");
        Map<String, Path> files = Map.of(
            "entry1.txt", file1,
            "entry2.txt", file2
        );

        EliteFileIOTraining.createZipArchive(zipFile, files);

        assertTrue(Files.exists(zipFile));
        assertTrue(Files.size(zipFile) > 0);
    }

    // ========================================================================
    // PROBLEM 9: Memory-Mapped File Tests
    // ========================================================================

    @Test
    @Order(25)
    @DisplayName("Problem 9: Memory-Mapped File - Read")
    void testMemoryMappedFile_Read() throws IOException {
        Path file = tempDir.resolve("mapped.dat");
        Files.write(file, "Hello World".getBytes());

        EliteFileIOTraining.MemoryMappedFile mmf =
            new EliteFileIOTraining.MemoryMappedFile(file, true);

        assertEquals('H', (char) mmf.readByte(0));
        assertEquals('e', (char) mmf.readByte(1));

        byte[] data = mmf.read(0, 5);
        assertEquals("Hello", new String(data));

        mmf.close();
    }

    @Test
    @Order(26)
    @DisplayName("Problem 9: Memory-Mapped File - Write")
    void testMemoryMappedFile_Write() throws IOException {
        Path file = tempDir.resolve("mapped_write.dat");
        Files.write(file, new byte[100]);

        EliteFileIOTraining.MemoryMappedFile mmf =
            new EliteFileIOTraining.MemoryMappedFile(file, false);

        mmf.writeByte(0, (byte) 'A');
        mmf.write(1, "BC".getBytes());
        mmf.force();

        byte[] result = mmf.read(0, 3);
        assertEquals("ABC", new String(result));

        mmf.close();
    }

    // ========================================================================
    // PROBLEM 10: File Lock Tests
    // ========================================================================

    @Test
    @Order(27)
    @DisplayName("Problem 10: File Lock - Acquire Exclusive")
    void testFileLock_AcquireExclusive() throws IOException {
        Path file = tempDir.resolve("locked.txt");
        Files.writeString(file, "Lock test");

        EliteFileIOTraining.FileLockManager lockMgr =
            new EliteFileIOTraining.FileLockManager(file);

        assertTrue(lockMgr.acquireExclusiveLock(1000));
        assertTrue(lockMgr.isLocked());

        lockMgr.releaseLock();
        assertFalse(lockMgr.isLocked());

        lockMgr.close();
    }

    @Test
    @Order(28)
    @DisplayName("Problem 10: File Lock - Acquire Shared")
    void testFileLock_AcquireShared() throws IOException {
        Path file = tempDir.resolve("shared_lock.txt");
        Files.writeString(file, "Shared lock test");

        EliteFileIOTraining.FileLockManager lockMgr =
            new EliteFileIOTraining.FileLockManager(file);

        assertTrue(lockMgr.acquireSharedLock(1000));
        assertTrue(lockMgr.isLocked());

        lockMgr.close();
    }

    // ========================================================================
    // PROBLEM 11: Asynchronous File I/O Tests
    // ========================================================================

    @Test
    @Order(29)
    @DisplayName("Problem 11: Async File Read")
    void testReadFileAsync() throws IOException, ExecutionException, InterruptedException {
        Path file = tempDir.resolve("async.txt");
        String content = "Asynchronous file reading test";
        Files.writeString(file, content);

        CompletableFuture<String> future =
            EliteFileIOTraining.readFileAsync(file);

        String result = future.get();
        assertEquals(content, result);
    }

    @Test
    @Order(30)
    @DisplayName("Problem 11: Async File Read - Non-existent File")
    void testReadFileAsync_NonExistent() {
        Path file = tempDir.resolve("nonexistent.txt");

        CompletableFuture<String> future =
            EliteFileIOTraining.readFileAsync(file);

        assertThrows(ExecutionException.class, future::get);
    }

    // ========================================================================
    // PROBLEM 12: Directory Copy Tests
    // ========================================================================

    @Test
    @Order(31)
    @DisplayName("Problem 12: Copy Directory - Basic")
    void testCopyDirectory_Basic() throws IOException {
        Path source = tempDir.resolve("source_dir");
        Path dest = tempDir.resolve("dest_dir");

        Files.createDirectories(source);
        Files.createFile(source.resolve("file1.txt"));
        Files.createFile(source.resolve("file2.txt"));

        EliteFileIOTraining.copyDirectory(source, dest, false);

        assertTrue(Files.exists(dest.resolve("file1.txt")));
        assertTrue(Files.exists(dest.resolve("file2.txt")));
    }

    @Test
    @Order(32)
    @DisplayName("Problem 12: Copy Directory - With Subdirectories")
    void testCopyDirectory_WithSubdirs() throws IOException {
        Path source = tempDir.resolve("source_tree");
        Path dest = tempDir.resolve("dest_tree");

        Files.createDirectories(source.resolve("subdir1"));
        Files.createDirectories(source.resolve("subdir2"));
        Files.createFile(source.resolve("root.txt"));
        Files.createFile(source.resolve("subdir1/file1.txt"));
        Files.createFile(source.resolve("subdir2/file2.txt"));

        EliteFileIOTraining.copyDirectory(source, dest, false);

        assertTrue(Files.exists(dest.resolve("root.txt")));
        assertTrue(Files.exists(dest.resolve("subdir1/file1.txt")));
        assertTrue(Files.exists(dest.resolve("subdir2/file2.txt")));
    }

    @Test
    @Order(33)
    @DisplayName("Problem 12: Copy Directory - Preserve Attributes")
    void testCopyDirectory_PreserveAttributes() throws IOException {
        Path source = tempDir.resolve("source_attrs");
        Path dest = tempDir.resolve("dest_attrs");

        Files.createDirectories(source);
        Path file = Files.createFile(source.resolve("file.txt"));
        Files.writeString(file, "Content");

        EliteFileIOTraining.copyDirectory(source, dest, true);

        assertTrue(Files.exists(dest.resolve("file.txt")));
    }

    // ========================================================================
    // Additional Edge Case Tests
    // ========================================================================

    @Test
    @Order(34)
    @DisplayName("Edge Case: FileStats - Single Line No Newline")
    void testFileStats_SingleLineNoNewline() throws IOException {
        Path file = tempDir.resolve("single_line.txt");
        Files.writeString(file, "Single line without newline");

        EliteFileIOTraining.FileStats stats =
            EliteFileIOTraining.readFileLazily(file);

        assertEquals(1, stats.getLineCount());
    }

    @Test
    @Order(35)
    @DisplayName("Edge Case: CSV - Empty Fields")
    void testCSV_EmptyFields() throws IOException {
        Path csvFile = tempDir.resolve("empty_fields.csv");
        Files.writeString(csvFile, "A,,C\n");

        EliteFileIOTraining.CSVReader reader =
            new EliteFileIOTraining.CSVReader(csvFile);

        List<String> row = reader.readRow();
        assertEquals(3, row.size());
        assertEquals("A", row.get(0));
        assertEquals("", row.get(1));
        assertEquals("C", row.get(2));

        reader.close();
    }

    @Test
    @Order(36)
    @DisplayName("Edge Case: Find Files - Deep Nesting")
    void testFindFiles_DeepNesting() throws IOException {
        Path deep = tempDir.resolve("a/b/c/d/e");
        Files.createDirectories(deep);
        Files.createFile(deep.resolve("deep.txt"));

        List<Path> results = EliteFileIOTraining.findFilesByPattern(
            tempDir, ".*\\.txt");

        assertEquals(1, results.size());
        assertTrue(results.get(0).toString().contains("deep.txt"));
    }

    @Test
    @Order(37)
    @DisplayName("Edge Case: Compression - Empty File")
    void testCompression_EmptyFile() throws IOException {
        Path original = tempDir.resolve("empty_orig.txt");
        Path compressed = tempDir.resolve("empty_comp.gz");
        Path decompressed = tempDir.resolve("empty_decomp.txt");

        Files.createFile(original);

        EliteFileIOTraining.compressFile(original, compressed);
        EliteFileIOTraining.decompressFile(compressed, decompressed);

        assertEquals(0, Files.size(decompressed));
    }

    @Test
    @Order(38)
    @DisplayName("Integration: Read, Process, Write CSV")
    void testIntegration_CSVProcessing() throws IOException {
        Path input = tempDir.resolve("input.csv");
        Path output = tempDir.resolve("output.csv");

        // Write input CSV
        EliteFileIOTraining.CSVWriter writer =
            new EliteFileIOTraining.CSVWriter(input);
        writer.writeRow(List.of("Name", "Score"));
        writer.writeRow(List.of("Alice", "95"));
        writer.writeRow(List.of("Bob", "87"));
        writer.close();

        // Read and process
        EliteFileIOTraining.CSVReader reader =
            new EliteFileIOTraining.CSVReader(input);
        List<List<String>> allRows = reader.readAll();
        reader.close();

        // Write output
        EliteFileIOTraining.CSVWriter outWriter =
            new EliteFileIOTraining.CSVWriter(output);
        for (List<String> row : allRows) {
            outWriter.writeRow(row);
        }
        outWriter.close();

        assertTrue(Files.exists(output));
        assertEquals(3, Files.readAllLines(output).size());
    }

    @Test
    @Order(39)
    @DisplayName("Performance: Compare Large Files")
    void testPerformance_LargeFileComparison() throws IOException {
        Path file1 = tempDir.resolve("large_perf1.txt");
        Path file2 = tempDir.resolve("large_perf2.txt");

        // Create 20MB files
        byte[] data = new byte[20 * 1024 * 1024];
        new Random(42).nextBytes(data);

        Files.write(file1, data);
        Files.write(file2, data);

        long start = System.currentTimeMillis();
        boolean result = EliteFileIOTraining.compareFiles(file1, file2);
        long duration = System.currentTimeMillis() - start;

        assertTrue(result);
        assertTrue(duration < 5000); // Should complete in < 5 seconds
    }

    @Test
    @Order(40)
    @DisplayName("Stress Test: Multiple Concurrent File Operations")
    void testStress_ConcurrentOperations() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            final int index = i;
            futures.add(executor.submit(() -> {
                try {
                    Path file = tempDir.resolve("concurrent_" + index + ".txt");
                    Files.writeString(file, "Content " + index);

                    String content = Files.readString(file);
                    assertEquals("Content " + index, content);

                    Files.delete(file);
                } catch (IOException e) {
                    fail("Concurrent operation failed: " + e.getMessage());
                }
            }));
        }

        for (Future<?> future : futures) {
            assertDoesNotThrow(() -> future.get());
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }
}
