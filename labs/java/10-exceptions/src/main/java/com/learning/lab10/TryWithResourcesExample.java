package com.learning.lab10;

import java.io.*;

/**
 * Demonstrates try-with-resources (ARM — Automatic Resource Management).
 */
public class TryWithResourcesExample {

    public static void showTryWithResources() {
        System.out.println("=== try-with-resources ===");

        String filename = "example.txt";

        try (FileWriter fw = new FileWriter(filename);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("Hello, try-with-resources!");
            System.out.println("Written to file: " + filename);
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();
            System.out.println("Read from file: " + line);
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }

        new File(filename).delete();

        System.out.println("Custom resource demo:");
        try (CustomResource resource = new CustomResource()) {
            resource.doWork();
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}

class CustomResource implements AutoCloseable {
    public void doWork() {
        System.out.println("  CustomResource working...");
    }

    @Override
    public void close() {
        System.out.println("  CustomResource closed automatically");
    }
}
