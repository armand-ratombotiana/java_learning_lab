package com.learning.lab18;

import java.io.*;

/**
 * Demonstrates FileReader, FileWriter, and BufferedReader for character stream I/O.
 */
public class FileReaderWriterExample {

    public static void showFileReaderWriter() throws IOException {
        System.out.println("=== FileReader / FileWriter / BufferedReader ===");

        String filename = "io_example.txt";

        try (FileWriter fw = new FileWriter(filename);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("Hello, Java I/O!");
            bw.newLine();
            bw.write("Line 2 of the file");
            System.out.println("Written to " + filename);
        }

        try (FileReader fr = new FileReader(filename);
             BufferedReader br = new BufferedReader(fr)) {
            String line;
            System.out.println("Reading file:");
            while ((line = br.readLine()) != null) {
                System.out.println("  " + line);
            }
        }

        new File(filename).delete();
        System.out.println("Cleanup complete");
    }
}
