package com.learning.lab.module07;

import java.io.*;
import java.nio.file.*;
import java.util.stream.*;

public class Lab {
    public static void main(String[] args) throws IOException {
        System.out.println("=== Module 07: Files & I/O ===");
        fileOperationsDemo();
        streamDemo();
        bufferedIOdemo();
        objectSerializationDemo();
        nioDemo();
    }

    static void fileOperationsDemo() throws IOException {
        System.out.println("\n--- File Operations ---");
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.writeString(tempFile, "Hello, Java!");
        String content = Files.readString(tempFile);
        System.out.println("Read from file: " + content);

        File file = tempFile.toFile();
        System.out.println("Exists: " + file.exists());
        System.out.println("Size: " + file.length() + " bytes");

        Files.deleteIfExists(tempFile);
        System.out.println("File deleted");
    }

    static void streamDemo() throws IOException {
        System.out.println("\n--- I/O Streams ---");
        Path tempFile = Files.createTempFile("stream", ".txt");
        Files.writeString(tempFile, "Line1\nLine2\nLine3");

        try (BufferedReader reader = Files.newBufferedReader(tempFile)) {
            reader.lines().forEach(System.out::println);
        }

        Files.deleteIfExists(tempFile);
    }

    static void bufferedIOdemo() throws IOException {
        System.out.println("\n--- Buffered I/O ---");
        Path tempFile = Files.createTempFile("buffer", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.toFile()))) {
            writer.write("Buffered write 1");
            writer.newLine();
            writer.write("Buffered write 2");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Read: " + line);
            }
        }

        Files.deleteIfExists(tempFile);
    }

    static void objectSerializationDemo() throws IOException, ClassNotFoundException {
        System.out.println("\n--- Object Serialization ---");
        Path tempFile = Files.createTempFile("serial", ".dat");

        Person p = new Person("John", 30);
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(tempFile))) {
            oos.writeObject(p);
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(tempFile))) {
            Person deserialized = (Person) ois.readObject();
            System.out.println("Deserialized: " + deserialized.name + ", " + deserialized.age);
        }

        Files.deleteIfExists(tempFile);
    }

    static void nioDemo() throws IOException {
        System.out.println("\n--- NIO ---");
        Path dir = Files.createTempDirectory("nio_test");
        Path file = dir.resolve("nio_file.txt");
        Files.writeString(file, "NIO content");

        DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
        stream.forEach(p -> System.out.println("Entry: " + p.getFileName()));

        Files.walk(dir).forEach(p -> {
            try { Files.deleteIfExists(p); } catch (IOException e) {}
        });
    }
}

class Person implements Serializable {
    String name;
    int age;
    Person(String name, int age) { this.name = name; this.age = age; }
}