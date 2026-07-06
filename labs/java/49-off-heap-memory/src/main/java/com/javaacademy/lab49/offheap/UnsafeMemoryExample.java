package com.javaacademy.lab49.offheap;

import sun.misc.Unsafe;
import java.lang.reflect.Field;

/**
 * Demonstrates sun.misc.Unsafe for off-heap memory allocation.
 * WARNING: Unsafe API is internal, may be removed in future JDKs.
 * Use only for learning; prefer Foreign Memory API (MemorySegment)
 * or DirectByteBuffer for production code.
 */
public class UnsafeMemoryExample {

    private static final Unsafe UNSAFE;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Cannot get Unsafe instance", e);
        }
    }

    public static void main(String[] args) {
        long address = UNSAFE.allocateMemory(128);

        try {
            UNSAFE.putLong(address, 0x1234567890ABCDEFL);
            UNSAFE.putLong(address + 8, 0xFEDCBA0987654321L);
            UNSAFE.putInt(address + 16, 42);

            long val1 = UNSAFE.getLong(address);
            long val2 = UNSAFE.getLong(address + 8);
            int val3 = UNSAFE.getInt(address + 16);

            System.out.printf("Unsafe: val1=0x%X val2=0x%X val3=%d%n", val1, val2, val3);

            // CAS example
            long offset = 24;
            UNSAFE.putLong(address + offset, 100L);
            boolean casSuccess = UNSAFE.compareAndSwapLong(null, address + offset, 100L, 200L);
            System.out.println("CAS: " + casSuccess + " -> value=" + UNSAFE.getLong(address + offset));
        } finally {
            UNSAFE.freeMemory(address);
        }
    }
}
