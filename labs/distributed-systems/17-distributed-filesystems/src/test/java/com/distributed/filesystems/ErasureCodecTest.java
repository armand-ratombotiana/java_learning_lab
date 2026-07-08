package com.distributed.filesystems;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ErasureCodecTest {

    @Test
    void testEncodeDecode() {
        ErasureCodec codec = new ErasureCodec(4, 2);
        byte[] original = "Hello Distributed Filesystem!".getBytes();
        byte[][] fragments = codec.encode(original);
        boolean[] available = {true, true, true, true, false, false};
        byte[] decoded = codec.decode(fragments, available);
        assertArrayEquals(original, decoded);
    }

    @Test
    void testStorageOverhead() {
        ErasureCodec codec = new ErasureCodec(4, 2);
        assertEquals(1.5, codec.getStorageOverhead(), 0.01);
    }

    @Test
    void testNotEnoughFragments() {
        ErasureCodec codec = new ErasureCodec(4, 2);
        byte[][] fragments = codec.encode("test".getBytes());
        boolean[] available = {true, false, false, false, false, false};
        assertThrows(IllegalStateException.class, () -> codec.decode(fragments, available));
    }

    @Test
    void testEmptyData() {
        ErasureCodec codec = new ErasureCodec(2, 1);
        byte[] fragments = codec.encode("".getBytes())[0];
        assertNotNull(fragments);
    }
}
