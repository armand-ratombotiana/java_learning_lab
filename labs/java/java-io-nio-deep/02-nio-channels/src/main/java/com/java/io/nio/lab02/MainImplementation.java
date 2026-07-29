package com.java.io.nio.lab02;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * NIO Channels — Main Implementation
 *
 * Demonstrates: FileChannel, SocketChannel (local), scatter/gather,
 * memory-mapped files, transferTo/transferFrom (zero-copy).
 */
public class MainImplementation {

    /**
     * FileChannel: read file into String.
     */
    public String readFile(String path) throws IOException {
        try (FileChannel channel = FileChannel.open(Path.of(path), StandardOpenOption.READ)) {
            ByteBuffer buf = ByteBuffer.allocate((int) channel.size());
            channel.read(buf);
            buf.flip();
            return StandardCharsets.UTF_8.decode(buf).toString();
        }
    }

    /**
     * FileChannel: write String to file.
     */
    public void writeFile(String path, String content) throws IOException {
        try (FileChannel channel = FileChannel.open(
                Path.of(path), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            ByteBuffer buf = ByteBuffer.wrap(content.getBytes(StandardCharsets.UTF_8));
            channel.write(buf);
        }
    }

    /**
     * Scatter read: read file into multiple buffers.
     */
    public ByteBuffer[] scatterRead(String path, int... bufferSizes) throws IOException {
        try (FileChannel channel = FileChannel.open(Path.of(path), StandardOpenOption.READ)) {
            ByteBuffer[] buffers = new ByteBuffer[bufferSizes.length];
            for (int i = 0; i < bufferSizes.length; i++) {
                buffers[i] = ByteBuffer.allocate(bufferSizes[i]);
            }
            channel.read(buffers);
            for (ByteBuffer buf : buffers) buf.flip();
            return buffers;
        }
    }

    /**
     * Gather write: write from multiple buffers.
     */
    public long gatherWrite(String path, ByteBuffer... buffers) throws IOException {
        try (FileChannel channel = FileChannel.open(
                Path.of(path), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            return channel.write(buffers);
        }
    }

    /**
     * Memory-mapped file: read/write directly.
     */
    public void memoryMappedWrite(String path, int position, int value) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(path, "rw");
             FileChannel channel = raf.getChannel()) {
            MappedByteBuffer mbb = channel.map(FileChannel.MapMode.READ_WRITE, 0, position + 4);
            mbb.putInt(position, value);
        }
    }

    public int memoryMappedRead(String path, int position) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(path, "r");
             FileChannel channel = raf.getChannel()) {
            MappedByteBuffer mbb = channel.map(FileChannel.MapMode.READ_ONLY, 0, position + 4);
            return mbb.getInt(position);
        }
    }

    /**
     * Zero-copy transfer (transferTo).
     */
    public long transferTo(String source, String dest) throws IOException {
        try (FileChannel src = FileChannel.open(Path.of(source), StandardOpenOption.READ);
             FileChannel dst = FileChannel.open(
                 Path.of(dest), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            return src.transferTo(0, src.size(), dst);
        }
    }

    public static void main(String[] args) throws IOException {
        MainImplementation m = new MainImplementation();
        Path tmp = Files.createTempFile("nio-test", ".txt");
        tmp.toFile().deleteOnExit();

        // Write and read
        m.writeFile(tmp.toString(), "Hello NIO Channels!");
        String content = m.readFile(tmp.toString());
        assert content.equals("Hello NIO Channels!") : "Read mismatch: " + content;

        // Scatter read
        m.writeFile(tmp.toString(), "ABCDEFGHIJ");
        ByteBuffer[] scattered = m.scatterRead(tmp.toString(), 4, 4, 2);
        assert new String(scattered[0].array(), 0, scattered[0].remaining()).equals("ABCD");
        assert new String(scattered[1].array(), 0, scattered[1].remaining()).equals("EFGH");
        assert new String(scattered[2].array(), 0, scattered[2].remaining()).equals("IJ");

        // Memory-mapped
        Path mmapFile = Files.createTempFile("mmap", ".bin");
        mmapFile.toFile().deleteOnExit();
        m.memoryMappedWrite(mmapFile.toString(), 0, 42);
        m.memoryMappedWrite(mmapFile.toString(), 4, 100);
        assert m.memoryMappedRead(mmapFile.toString(), 0) == 42;
        assert m.memoryMappedRead(mmapFile.toString(), 4) == 100;

        // Zero-copy transfer
        Path dest = Files.createTempFile("transfer-dst", ".txt");
        dest.toFile().deleteOnExit();
        m.writeFile(tmp.toString(), "Zero-copy test");
        long transferred = m.transferTo(tmp.toString(), dest.toString());
        String destContent = m.readFile(dest.toString());
        assert destContent.equals("Zero-copy test");

        System.out.println("All NIO Channels tests passed.");
    }
}
