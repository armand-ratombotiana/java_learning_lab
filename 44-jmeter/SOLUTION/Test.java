package com.learning.jmeter;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.CSVDataSet;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JMeterSolutionTest {

    private JMeterSolution solution;

    @BeforeEach
    void setUp() {
        solution = new JMeterSolution();
    }

    @Test
    void testCreateHttpSampler() {
        HTTPSamplerProxy sampler = solution.createHttpSampler("test", "example.com", "/api", "GET");
        assertEquals("test", sampler.getName());
        assertEquals("example.com", sampler.getDomain());
        assertEquals("/api", sampler.getPath());
    }

    @Test
    void testCreateThreadGroup() {
        ThreadGroup tg = solution.createThreadGroup("load-test", 10, 5, 100);
        assertEquals("load-test", tg.getName());
        assertEquals(10, tg.getNumThreads());
    }

    @Test
    void testCreateTestPlan() {
        TestPlan plan = solution.createTestPlan("my-plan", true);
        assertEquals("my-plan", plan.getName());
        assertTrue(plan.isEnabled());
    }

    @Test
    void testCreateCsvDataSet() {
        CSVDataSet csv = solution.createCsvDataSet("data.csv", "username,password", ",");
        assertNotNull(csv);
    }

    @Test
    void testCreateHeaderManager() {
        HeaderManager hm = solution.createHeaderManager(Map.of("Content-Type", "application/json"));
        assertNotNull(hm);
    }

    @Test
    void testCreateUserDefinedVariables() {
        Arguments args = solution.createUserDefinedVariables(Map.of("baseUrl", "http://localhost"));
        assertNotNull(args);
    }
}