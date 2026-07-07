package com.algo.lab36;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BloomFilterVariantsTest {

    @Test
    void testCountingBloomAddContains() {
        CountingBloomFilter cbf = new CountingBloomFilter(100, 0.01);
        cbf.add("hello");
        assertTrue(cbf.contains("hello"));
        assertFalse(cbf.contains("world"));
    }

    @Test
    void testCountingBloomRemove() {
        CountingBloomFilter cbf = new CountingBloomFilter(100, 0.01);
        cbf.add("removeMe");
        assertTrue(cbf.contains("removeMe"));
        cbf.remove("removeMe");
        assertFalse(cbf.contains("removeMe"));
    }

    @Test
    void testCountingBloomMultipleAdd() {
        CountingBloomFilter cbf = new CountingBloomFilter(10, 0.01);
        cbf.add("a"); cbf.add("a"); cbf.add("a");
        assertTrue(cbf.contains("a"));
        cbf.remove("a");
        assertTrue(cbf.contains("a"));
        cbf.remove("a");
        assertTrue(cbf.contains("a"));
        cbf.remove("a");
        assertFalse(cbf.contains("a"));
    }

    @Test
    void testScalableBloom() {
        ScalableBloomFilter sbf = new ScalableBloomFilter(0.01);
        for (int i = 0; i < 5000; i++) sbf.add("key-" + i);
        assertTrue(sbf.contains("key-0"));
        assertTrue(sbf.contains("key-4999"));
        assertFalse(sbf.contains("key-9999"));
    }

    @Test
    void testScalableBloomGrows() {
        ScalableBloomFilter sbf = new ScalableBloomFilter(0.1);
        assertEquals(1, sbf.filterCount());
        for (int i = 0; i < 10000; i++) sbf.add("item-" + i);
        assertTrue(sbf.filterCount() > 1);
    }

    @Test
    void testCuckooInsertContains() {
        CuckooFilter cf = new CuckooFilter(100);
        assertTrue(cf.insert("hello"));
        assertTrue(cf.contains("hello"));
        assertFalse(cf.contains("world"));
    }

    @Test
    void testCuckooDelete() {
        CuckooFilter cf = new CuckooFilter(100);
        cf.insert("deleteMe");
        assertTrue(cf.contains("deleteMe"));
        assertTrue(cf.delete("deleteMe"));
        assertFalse(cf.contains("deleteMe"));
    }

    @Test
    void testCuckooBulkInsert() {
        CuckooFilter cf = new CuckooFilter(500);
        int inserted = 0;
        for (int i = 0; i < 400; i++) {
            if (cf.insert("elem-" + i)) inserted++;
        }
        assertTrue(inserted >= 350);
        assertTrue(cf.contains("elem-0"));
        assertTrue(cf.contains("elem-399"));
    }
}
