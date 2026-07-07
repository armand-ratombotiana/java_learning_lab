package com.javaacademy.lab49.offheap;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Files;

public class MappedFileExample {

    private static final int FILE_SIZE = 4096;

    public static void main(String[] args) throws Exception {
        Path path = Files.createTempFile("memory_mapped_test", ".dat");
        path.toFile().deleteOnExit();

        try (RandomAccessFile file = new RandomAccessFile(path.toFile(), "rw");
             FileChannel channel = file.getChannel()) {

            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, FILE_SIZE);

            for (int i = 0; i < FILE_SIZE / 4; i++) {
                map.putInt(i * 4, i * 1000);
            }

            map.load();
            for (int i = 0; i < FILE_SIZE / 4; i++) {
                int val = map.getInt(i * 4);
                assert val == i * 1000 : "Mismatch at " + i;
            }

            map.force();
            System.out.println("Mapped file written and verified. Size: " + FILE_SIZE);
        }

        try {
            Files.deleteIfExists(path);
        } catch (Exception e) {
            // Windows may keep file locked; cleanup on exit
        }
    }
}
