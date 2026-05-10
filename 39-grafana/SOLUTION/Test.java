package com.learning.grafana;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GrafanaSolutionTest {

    private GrafanaSolution solution;

    @BeforeEach
    void setUp() {
        solution = new GrafanaSolution();
    }

    @Test
    void testCreateDashboard() {
        Map<String, Object> dashboard = solution.createDashboard("My Dashboard");
        assertEquals("My Dashboard", dashboard.get("title"));
        assertEquals(1, dashboard.get("version"));
    }

    @Test
    void testCreateDataSource() {
        Map<String, Object> ds = solution.createDataSource("Prometheus", "prometheus", "http://localhost:9090");
        assertEquals("Prometheus", ds.get("name"));
        assertEquals("prometheus", ds.get("type"));
        assertEquals("http://localhost:9090", ds.get("url"));
    }

    @Test
    void testCreatePanel() {
        Map<String, Object> panel = solution.createPanel("CPU Usage", "Prometheus", "rate(cpu_usage[5m])");
        assertEquals("CPU Usage", panel.get("title"));
        assertEquals("graph", panel.get("type"));
    }

    @Test
    void testCreatePrometheusQuery() {
        Map<String, Object> query = solution.createPrometheusQuery("rate(requests_total[5m])", "{{service}}");
        assertTrue(query.containsKey("expr"));
        assertEquals("{{service}}", query.get("legendFormat"));
    }

    @Test
    void testConfigureAlerts() {
        Map<String, Object> panel = solution.createPanel("Test", "Prometheus", "test");
        solution.configureAlerts(panel, "gt");
        assertNotNull(panel.get("alert"));
    }
}