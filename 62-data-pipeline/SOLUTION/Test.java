package com.learning.datapipeline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DataPipelineSolutionTest {

    private DataPipelineSolution solution;

    @BeforeEach
    void setUp() {
        solution = new DataPipelineSolution();
    }

    @Test
    void testCreatePipeline() {
        DataPipelineSolution.Pipeline pipeline = solution.createPipeline("test-pipeline");
        assertEquals("test-pipeline", pipeline.getStages().isEmpty() ? "empty" : pipeline.getStages().get(0).getName());
    }

    @Test
    void testPipelineWithStage() {
        DataPipelineSolution.Pipeline pipeline = solution.createPipeline("test")
            .addStage(solution.createStage("stage1", data -> data));
        assertEquals(1, pipeline.getStages().size());
    }

    @Test
    void testPipelineExecute() {
        DataPipelineSolution.Pipeline pipeline = solution.createPipeline("test")
            .addStage(solution.createStage("stage1", data -> data));
        List<Object> result = pipeline.execute(Arrays.asList(1, 2, 3));
        assertEquals(3, result.size());
    }

    @Test
    void testCreateETLJob() {
        DataPipelineSolution.ETLJob job = solution.createETLJob("db", "transform", "s3");
        assertEquals("db", job.getSource());
        assertEquals("transform", job.getTransform());
        assertEquals("s3", job.getDestination());
    }

    @Test
    void testCreateDataSource() {
        DataPipelineSolution.DataSource source = solution.createDataSource("postgres", "jdbc:...");
        assertEquals("postgres", source.getType());
        assertEquals("jdbc:...", source.getConnection());
    }

    @Test
    void testCreateDataSink() {
        DataPipelineSolution.DataSink sink = solution.createDataSink("s3", "s3://bucket");
        assertEquals("s3", sink.getType());
    }

    @Test
    void testCreateDataFlow() {
        DataPipelineSolution.DataSource source = solution.createDataSource("kafka", "localhost:9092");
        DataPipelineSolution.DataSink sink = solution.createDataSink("elasticsearch", "localhost:9200");
        DataPipelineSolution.DataFlow flow = solution.createDataFlow(source, sink);
        assertNotNull(flow);
    }
}