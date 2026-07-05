package com.learning.lab18;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;

/**
 * Demonstrates NIO Channels and ByteBuffer for high-performance I/O.
 */
public class ChannelExample {

    public static void showChannels() throws Exception {
        System.out.println("=== NIO Channels ===");

        String filename = "channel_example.bin";
        String message = "Hello, NIO Channels!";

        try (FileChannel writeChannel = FileChannel.open(
                Paths.get(filename), 
                StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            
            ByteBuffer buffer = ByteBuffer.allocate(128);
            buffer.put(message.getBytes());
            buffer.flip();
            
            int bytesWritten = writeChannel.write(buffer);
            System.out.println("Written " + bytesWritten + " bytes via channel");
        }

        try (FileChannel readChannel = FileChannel.open(
                Paths.get(filename), StandardOpenOption.READ)) {
            
            ByteBuffer buffer = ByteBuffer.allocate(128);
            int bytesRead = readChannel.read(buffer);
            buffer.flip();
            
            byte[] data = new byte[bytesRead];
            buffer.get(data);
            System.out.println("Read via channel: " + new String(data, java.nio.charset.StandardCharsets.UTF_8));
        }

        Files.delete(Paths.get(filename));
    }
}
