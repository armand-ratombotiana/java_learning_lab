package com.learning.lab18;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class IOTest {

    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("lab18-test-");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDir)
            .sorted(Comparator.reverseOrder())
            .forEach(p -> { try { Files.deleteIfExists(p); } catch (IOException ignored) { } });
    }

    @Test
    @DisplayName("FileWriter writes text to file")
    void fileWriterWrites() throws IOException {
        Path file = tempDir.resolve("test.txt");
        try (FileWriter fw = new FileWriter(file.toFile())) {
            fw.write("Hello, File!");
        }
        assertTrue(Files.exists(file));
        assertEquals("Hello, File!", Files.readString(file));
    }

    @Test
    @DisplayName("BufferedReader reads file line by line")
    void bufferedReaderReads() throws IOException {
        Path file = tempDir.resolve("lines.txt");
        Files.writeString(file, "line1\nline2\nline3");
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        assertEquals(List.of("line1", "line2", "line3"), lines);
    }

    @Test
    @DisplayName("Files.writeString and readString roundtrip")
    void filesStringRoundtrip() throws IOException {
        Path file = tempDir.resolve("roundtrip.txt");
        Files.writeString(file, "roundtrip data");
        String content = Files.readString(file);
        assertEquals("roundtrip data", content);
    }

    @Test
    @DisplayName("Files.copy copies files")
    void filesCopy() throws IOException {
        Path source = tempDir.resolve("source.txt");
        Path target = tempDir.resolve("target.txt");
        Files.writeString(source, "copy me");
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        assertTrue(Files.exists(target));
        assertEquals("copy me", Files.readString(target));
    }

    @Test
    @DisplayName("Files.move moves files")
    void filesMove() throws IOException {
        Path source = tempDir.resolve("move.txt");
        Path target = tempDir.resolve("moved.txt");
        Files.writeString(source, "move me");
        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        assertFalse(Files.exists(source));
        assertTrue(Files.exists(target));
    }

    @Test
    @DisplayName("Files.walk traverses directory tree")
    void filesWalk() throws IOException {
        Path subDir = Files.createDirectory(tempDir.resolve("sub"));
        Files.writeString(tempDir.resolve("a.txt"), "a");
        Files.writeString(subDir.resolve("b.txt"), "b");
        var files = Files.walk(tempDir)
            .filter(p -> p.toString().endsWith(".txt"))
            .map(p -> p.getFileName().toString())
            .sorted()
            .toList();
        assertTrue(files.contains("a.txt"));
        assertTrue(files.contains("b.txt"));
    }

    @Test
    @DisplayName("FileChannel reads and writes bytes")
    void fileChannelReadWrite() throws IOException {
        Path file = tempDir.resolve("channel.dat");
        try (var channel = java.nio.channels.FileChannel.open(
                file, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ)) {
            byte[] data = "channel data".getBytes();
            channel.write(java.nio.ByteBuffer.wrap(data));
            channel.position(0);
            java.nio.ByteBuffer buf = java.nio.ByteBuffer.allocate(data.length);
            channel.read(buf);
            assertEquals("channel data", new String(buf.array()));
        }
    }

    @Test
    @DisplayName("Files.createTempDirectory creates unique dirs")
    void createTempDirectory() throws IOException {
        Path tmp = Files.createTempDirectory("unique-");
        assertTrue(Files.exists(tmp));
        Files.deleteIfExists(tmp);
    }
}
