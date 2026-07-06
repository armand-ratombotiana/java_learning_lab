package com.javaacademy.lab49.offheap;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.channels.FileChannel.MapMode;

/**
 * Demonstrates memory-mapped file I/O using MappedByteBuffer.
 * A file is mapped into virtual memory, allowing direct read/write
 * without explicit system calls. Changes are eventually flushed to disk.
 * Shows concurrent access by mapping the same file region.
 */
public class MappedFileExample {

    private static final String FILE_NAME = "memory_mapped_test.dat";
    private static final int FILE_SIZE = 4096;

    public static void main(String[] args) throws Exception {
        Path path = Path.of(FILE_NAME);
        Files.deleteIfExists(path);

        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw");
             FileChannel channel = file.getChannel()) {

            MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, FILE_SIZE);

            // Write across the mapped buffer
            for (int i = 0; i < FILE_SIZE / 4; i++) {
                map.putInt(i * 4, i * 1000);
            }

            // Read back and verify
            map.load(); // hint to load into memory
            for (int i = 0; i < FILE_SIZE / 4; i++) {
                int val = map.getInt(i * 4);
                assert val == i * 1000 : "Mismatch at " + i;
            }

            map.force(); // flush to disk
            System.out.println("Mapped file written and verified. Size: " + FILE_SIZE);
        } finally {
            Files.deleteIfExists(path);
        }
    }
}
