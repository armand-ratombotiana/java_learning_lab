package com.javaacademy.lab40.bestpractices;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class TryWithResourcesExample {

    public String readFile(String path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Path.of(path))) {
            return reader.readLine();
        }
    }

    public String readFileMulti(String path1, String path2) throws IOException {
        try (BufferedReader r1 = Files.newBufferedReader(Path.of(path1));
             BufferedReader r2 = Files.newBufferedReader(Path.of(path2))) {
            return r1.readLine() + " " + r2.readLine();
        }
    }

    public void writeToFile(String path, String content) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(path))) {
            writer.write(content);
        }
    }

    public Properties loadProperties(String path) throws IOException {
        try (InputStream in = Files.newInputStream(Path.of(path))) {
            Properties props = new Properties();
            props.load(in);
            return props;
        }
    }

    public String readWithCustomException(String path) {
        try (BufferedReader reader = Files.newBufferedReader(Path.of(path))) {
            return reader.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read: " + path, e);
        }
    }

    public void multipleResources() throws IOException {
        Path tmpDir = Files.createTempDirectory("try-with-resources");
        Path file1 = tmpDir.resolve("a.txt");
        Path file2 = tmpDir.resolve("b.txt");
        try (BufferedWriter w1 = Files.newBufferedWriter(file1);
             BufferedWriter w2 = Files.newBufferedWriter(file2);
             BufferedReader r1 = Files.newBufferedReader(file1)) {
            w1.write("content a");
            w2.write("content b");
            r1.readLine();
        } finally {
            Files.deleteIfExists(file1);
            Files.deleteIfExists(file2);
            Files.deleteIfExists(tmpDir);
        }
    }
}
