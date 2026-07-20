package com.javaacademy.lab49.offheap;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.*;

class OffHeapUltraDeepTest {

    @Test
    void directByteBufferAllocation() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        assertTrue(buffer.isDirect());
        assertEquals(1024, buffer.capacity());
    }

    @Test
    void directByteBufferReadWrite() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(8);
        buffer.putLong(0, 123456789L);
        assertEquals(123456789L, buffer.getLong(0));
    }

    @Test
    void mappedByteBufferFromFileChannel() throws Exception {
        var file = java.io.File.createTempFile("mmap", ".dat");
        file.deleteOnExit();
        try (var channel = new java.io.RandomAccessFile(file, "rw").getChannel()) {
            var mapped = channel.map(java.nio.channels.FileChannel.MapMode.READ_WRITE, 0, 16);
            mapped.putInt(0, 42);
            assertEquals(42, mapped.getInt(0));
        }
    }

    @Test
    void byteBufferOrder() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(0, 0x12345678);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int value = buffer.getInt(0);
        assertNotEquals(0x12345678, value);
    }

    @Test
    void unsafeAllocationNotAccessible() {
        assertThrows(Exception.class, () -> {
            var unsafeClass = Class.forName("sun.misc.Unsafe");
            var field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Object unsafe = field.get(null);
            var method = unsafeClass.getMethod("allocateMemory", long.class);
            long address = (long) method.invoke(unsafe, 1024L);
            assertTrue(address != 0);
            var freeMethod = unsafeClass.getMethod("freeMemory", long.class);
            freeMethod.invoke(unsafe, address);
        });
    }
}
