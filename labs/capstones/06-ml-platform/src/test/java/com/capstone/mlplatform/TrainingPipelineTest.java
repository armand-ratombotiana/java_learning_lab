package com.capstone.mlplatform;

import org.junit.jupiter.api.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TrainingPipelineTest {
    private TrainingPipeline pipeline;

    @BeforeEach
    void setUp() { pipeline = new TrainingPipeline(); }

    @Test void testStartRun() {
        var config = new TrainingPipeline.TrainingConfig("my-pipeline", "random_forest",
            Map.of("n_estimators", 100, "max_depth", 10), 0.8);
        var run = pipeline.startRun(config);
        assertEquals("RUNNING", run.status().name());
        assertNotNull(run.runId());
    }

    @Test void testCompleteRun() {
        var config = new TrainingPipeline.TrainingConfig("pipe", "linear", Map.of(), 0.8);
        var run = pipeline.startRun(config);
        var completed = pipeline.completeRun(run.runId(), Map.of("accuracy", 0.95, "f1", 0.93));
        assertEquals("COMPLETED", completed.status().name());
        assertEquals(0.95, completed.metrics().get("accuracy"));
    }

    @Test void testFailRun() {
        var config = new TrainingPipeline.TrainingConfig("pipe", "cnn", Map.of(), 0.8);
        var run = pipeline.startRun(config);
        var failed = pipeline.failRun(run.runId(), Map.of("loss", Double.MAX_VALUE));
        assertEquals("FAILED", failed.status().name());
    }

    @Test void testGetRunsByPipeline() {
        pipeline.startRun(new TrainingPipeline.TrainingConfig("pipe1", "lr", Map.of(), 0.8));
        pipeline.startRun(new TrainingPipeline.TrainingConfig("pipe1", "lr", Map.of(), 0.8));
        assertEquals(2, pipeline.getRunsByPipeline("pipe1").size());
    }

    @Test void testInvalidRun() {
        assertThrows(IllegalArgumentException.class, () -> pipeline.completeRun("nonexistent", Map.of()));
    }
}
