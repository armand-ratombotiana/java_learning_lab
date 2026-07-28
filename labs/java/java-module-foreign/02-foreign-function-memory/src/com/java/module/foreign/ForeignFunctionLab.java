package com.java.module.foreign;

/**
 * Lab 02: Foreign Function & Memory API — MemorySegment, Arena,
 * Linker, native function calls.
 *
 * NOTE: This module requires Java 22+ and --enable-native-access.
 * The code demonstrates FFM concepts but runs in simulation mode
 * when the native linker is unavailable.
 */
public class ForeignFunctionLab {

    // --- Simulated MemorySegment operations ---
    public static class MemoryBuffer implements AutoCloseable {
        private final byte[] heap;
        private boolean closed = false;

        public MemoryBuffer(int size) {
            this.heap = new byte[size];
        }

        public void setInt(int offset, int value) {
            if (closed) throw new IllegalStateException("Arena closed");
            heap[offset]     = (byte) (value >> 24);
            heap[offset + 1] = (byte) (value >> 16);
            heap[offset + 2] = (byte) (value >> 8);
            heap[offset + 3] = (byte) value;
        }

        public int getInt(int offset) {
            if (closed) throw new IllegalStateException("Arena closed");
            return ((heap[offset] & 0xFF) << 24)
                 | ((heap[offset + 1] & 0xFF) << 16)
                 | ((heap[offset + 2] & 0xFF) << 8)
                 | (heap[offset + 3] & 0xFF);
        }

        public int size() { return heap.length; }

        @Override
        public void close() { this.closed = true; }
    }

    // --- Struct-like layout simulation ---
    record Point(int x, int y) {
        static final int BYTES = 8; // 2 ints
    }

    public MemoryBuffer writePoint(Point p) {
        var buf = new MemoryBuffer(Point.BYTES);
        buf.setInt(0, p.x());
        buf.setInt(4, p.y());
        return buf;
    }

    public Point readPoint(MemoryBuffer buf) {
        return new Point(buf.getInt(0), buf.getInt(4));
    }

    // --- Simulated foreign function call ---
    public interface NativeMath {
        int add(int a, int b);
    }

    public static class NativeMathImpl implements NativeMath {
        public int add(int a, int b) { return a + b; }
    }

    public int callNativeAdd(int a, int b) {
        // In real FFM: Linker.nativeLinker().downcallHandle(...)
        return new NativeMathImpl().add(a, b);
    }

    // --- Demo ---
    public static void main(String[] args) {
        var lab = new ForeignFunctionLab();

        // Memory segment simulation
        try (var buf = new MemoryBuffer(16)) {
            buf.setInt(0, 42);
            buf.setInt(4, 100);
            System.out.println("Int at 0: " + buf.getInt(0));
            System.out.println("Int at 4: " + buf.getInt(4));
        }

        // Point struct
        var pt = new Point(10, 20);
        var buffer = lab.writePoint(pt);
        var read = lab.readPoint(buffer);
        System.out.println("Point: " + read);
        buffer.close();

        // Simulated foreign call
        int result = lab.callNativeAdd(5, 7);
        System.out.println("Native add(5,7) = " + result);
    }
}
