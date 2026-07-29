package com.java.io.nio.lab01;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * I/O Streams — Main Implementation
 *
 * Demonstrates: FileInputStream/OutputStream, Buffered streams,
 * Data streams, PushbackInputStream, SequenceInputStream.
 */
public class MainImplementation {

    /**
     * Copy file using buffered byte streams.
     */
    public long bufferedCopy(File source, File dest) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(source));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest))) {
            byte[] buf = new byte[8192];
            long total = 0;
            int n;
            while ((n = bis.read(buf)) != -1) {
                bos.write(buf, 0, n);
                total += n;
            }
            return total;
        }
    }

    /**
     * Write and read primitives using DataOutputStream/DataInputStream.
     */
    public byte[] writePrimitives(int i, double d, String s) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos)) {
            dos.writeInt(i);
            dos.writeDouble(d);
            dos.writeUTF(s);
        }
        return baos.toByteArray();
    }

    public List<Object> readPrimitives(byte[] data) throws IOException {
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data))) {
            return List.of(dis.readInt(), dis.readDouble(), dis.readUTF());
        }
    }

    /**
     * PushbackInputStream: find first occurrence of a byte pattern.
     * Returns the byte before the pattern, or -1 if not found.
     */
    public int findPattern(byte[] data, byte[] pattern) throws IOException {
        try (PushbackInputStream pbs = new PushbackInputStream(new ByteArrayInputStream(data), pattern.length)) {
            int prev = -1;
            int b;
            while ((b = pbs.read()) != -1) {
                if (b == pattern[0]) {
                    byte[] lookahead = new byte[pattern.length - 1];
                    int read = pbs.read(lookahead);
                    if (read == pattern.length - 1 && matches(lookahead, pattern, 1)) {
                        return prev;
                    }
                    pbs.unread(b);
                    if (read > 0) pbs.unread(lookahead, 0, read);
                }
                prev = b;
            }
        }
        return -1;
    }

    private boolean matches(byte[] actual, byte[] expected, int offset) {
        for (int i = 0; i < actual.length; i++) {
            if (actual[i] != expected[offset + i]) return false;
        }
        return true;
    }

    /**
     * SequenceInputStream: concatenate multiple streams.
     */
    public byte[] concatenate(byte[]... parts) throws IOException {
        Vector<InputStream> streams = new Vector<>();
        for (byte[] part : parts) {
            streams.add(new ByteArrayInputStream(part));
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (SequenceInputStream sis = new SequenceInputStream(streams.elements())) {
            byte[] buf = new byte[256];
            int n;
            while ((n = sis.read(buf)) != -1) {
                baos.write(buf, 0, n);
            }
        }
        return baos.toByteArray();
    }

    public static void main(String[] args) throws IOException {
        MainImplementation m = new MainImplementation();

        // Test buffered copy
        File tmpSrc = File.createTempFile("src", ".tmp");
        tmpSrc.deleteOnExit();
        File tmpDst = File.createTempFile("dst", ".tmp");
        tmpDst.deleteOnExit();
        try (FileOutputStream fos = new FileOutputStream(tmpSrc)) {
            fos.write("Hello World!".getBytes(StandardCharsets.UTF_8));
        }
        long copied = m.bufferedCopy(tmpSrc, tmpDst);
        assert copied == 12 : "Expected 12 bytes copied, got " + copied;

        // Test data streams
        byte[] primData = m.writePrimitives(42, 3.14, "test");
        List<Object> prims = m.readPrimitives(primData);
        assert prims.get(0).equals(42);
        assert prims.get(1).equals(3.14);
        assert prims.get(2).equals("test");

        // Test pushback
        int before = m.findPattern(new byte[]{1, 2, 3, 4, 5, 6}, new byte[]{4, 5});
        assert before == 3 : "Expected 3, got " + before;

        // Test sequence
        byte[] concat = m.concatenate("AB".getBytes(), "CD".getBytes(), "EF".getBytes());
        assert new String(concat, StandardCharsets.UTF_8).equals("ABCDEF");

        System.out.println("All I/O Streams tests passed.");
    }
}
