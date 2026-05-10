package com.learning.streaming;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StreamingSolutionTest {

    private StreamingSolution solution;

    @BeforeEach
    void setUp() {
        solution = new StreamingSolution();
    }

    @Test
    void testCreateJob() {
        StreamingSolution.StreamJob job = solution.createJob("test-job");
        assertEquals("test-job", job.getName());
    }

    @Test
    void testJobChaining() {
        StreamingSolution.StreamJob job = solution.createJob("test")
            .source("kafka")
            .sink("elasticsearch");
        assertNotNull(job);
    }

    @Test
    void testMapOperator() {
        StreamingSolution.StreamOperator op = solution.createMapOperator("double", x -> (Integer) x * 2);
        assertEquals("map", op.getType());
        assertEquals(4, op.apply(2));
    }

    @Test
    void testFilterOperator() {
        StreamingSolution.StreamOperator op = solution.createFilterOperator("even", x -> (Integer) x % 2 == 0);
        assertEquals("filter", op.getType());
        assertEquals(4, op.apply(4));
        assertNull(op.apply(3));
    }

    @Test
    void testTumblingWindow() {
        StreamingSolution.Window window = solution.createTumblingWindow(1000);
        assertEquals("tumbling", window.getType());
        assertEquals(1000, window.getSize());
    }

    @Test
    void testSlidingWindow() {
        StreamingSolution.Window window = solution.createSlidingWindow(5000, 1000);
        assertEquals("sliding", window.getType());
        assertEquals(5000, window.getSize());
        assertEquals(1000, window.getSlide());
    }

    @Test
    void testWatermark() {
        StreamingSolution.Watermark watermark = solution.createWatermark(5000);
        assertEquals(5000, watermark.getDelay());
    }

    @Test
    void testCheckpoint() {
        StreamingSolution.Checkpoint checkpoint = solution.createCheckpoint("1min");
        assertEquals("1min", checkpoint.getInterval());
    }

    @Test
    void testStormTopology() {
        StreamingSolution.StormTopology topo = solution.createTopology()
            .addSpout("source", solution.createSpout("spout"))
            .addBolt("process", solution.createBolt("bolt", "source"));
        assertNotNull(topo);
    }
}