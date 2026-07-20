package com.capstone.spark;

import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SparkContextTest {
    private SparkContext sc;

    @BeforeEach
    void setUp() { sc = new SparkContext("test-app", "local[*]"); }

    @Test void testParallelize() {
        var rdd = sc.parallelize(List.of(1, 2, 3, 4, 5));
        assertEquals(5, rdd.count());
    }

    @Test void testEmptyRDD() {
        var rdd = sc.emptyRDD();
        assertEquals(0, rdd.count());
    }

    @Test void testConfig() {
        sc.setConfig("spark.executor.memory", "2g");
        assertEquals("2g", sc.getConfig("spark.executor.memory"));
        assertEquals("default", sc.getConfig("nonexistent", "default"));
    }

    @Test void testAppName() {
        assertEquals("test-app", sc.getAppName());
        assertEquals("local[*]", sc.getMaster());
    }

    @Test void testStop() {
        assertFalse(sc.isStopped());
        sc.stop();
        assertTrue(sc.isStopped());
    }

    @Test void testGenerateIds() {
        long id1 = sc.generateRDDId();
        long id2 = sc.generateJobId();
        assertTrue(id2 > 0);
    }
}
