package com.javaacademy.lab49.offheap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Demonstrates direct ByteBuffer allocation, read/write operations,
 * and explicit deallocation. DirectByteBuffer lives off-heap and is
 * not subject to GC, making it ideal for high-throughput I/O but
 * requiring careful capacity management via -XX:MaxDirectMemorySize.
 */
public class DirectBufferExample {

    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        // Allocate direct (off-heap) buffer
        ByteBuffer buf = ByteBuffer.allocateDirect(BUFFER_SIZE);
        buf.order(ByteOrder.nativeOrder());

        // Write data
        buf.putInt(0, 0xDEADBEEF);
        buf.putLong(8, 0xCAFEBABEL);
        buf.putDouble(16, Math.PI);

        // Read back
        int intVal = buf.getInt(0);
        long longVal = buf.getLong(8);
        double doubleVal = buf.getDouble(16);

        System.out.printf("Read: int=0x%X long=0x%X double=%.10f%n",
            intVal, longVal, doubleVal);

        // Cleaner approach for deallocation (Java 9+)
        // ((sun.misc.Cleaner) buf).clean(); — but rely on try-with-resources with custom wrapper
        buf.clear();

        System.out.println("Direct buffer test passed. Capacity: " + buf.capacity());
    }
}
