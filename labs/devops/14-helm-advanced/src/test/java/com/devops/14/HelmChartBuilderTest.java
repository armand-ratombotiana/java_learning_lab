package com.devops.fourteen;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

class HelmChartBuilderTest {
    private HelmChartBuilder chart;

    @BeforeEach
    void setUp() {
        chart = new HelmChartBuilder("test-chart", "0.1.0", "Test chart");
    }

    @Test
    @DisplayName("Should create chart with correct metadata")
    void testChartCreation() {
        String chartYaml = chart.renderChartYaml();
        assertTrue(chartYaml.contains("name: test-chart"));
        assertTrue(chartYaml.contains("version: 0.1.0"));
        assertTrue(chartYaml.contains("description: Test chart"));
        assertTrue(chartYaml.contains("apiVersion: v2"));
        assertTrue(chartYaml.contains("type: application"));
    }

    @Test
    @DisplayName("Should render dependencies in Chart.yaml")
    void testDependencies() {
        chart.addDependency("postgresql", "12.x", "https://charts.bitnami.com/bitnami");
        chart.addDependency("redis", "17.x", "https://charts.bitnami.com/bitnami");
        String chartYaml = chart.renderChartYaml();
        assertTrue(chartYaml.contains("postgresql"));
        assertTrue(chartYaml.contains("redis"));
        assertEquals(2, chart.getDependencies().size());
    }

    @Test
    @DisplayName("Should render values YAML correctly")
    void testValuesRendering() {
        chart.setValue("replicaCount", 3);
        chart.setValue("image", Map.of("repository", "myapp", "tag", "latest"));
        String values = chart.renderValuesYaml();
        assertTrue(values.contains("replicaCount: 3"));
        assertTrue(values.contains("repository: myapp"));
        assertTrue(values.contains("tag: latest"));
    }

    @Test
    @DisplayName("Should render deployment template with correct syntax")
    void testDeploymentTemplate() {
        String template = chart.renderDeploymentTemplate();
        assertTrue(template.contains("apiVersion: apps/v1"));
        assertTrue(template.contains("kind: Deployment"));
        assertTrue(template.contains("replicas: {{ .Values.replicaCount }}"));
        assertTrue(template.contains("image:"));
    }

    @Test
    @DisplayName("Should render service template")
    void testServiceTemplate() {
        String template = chart.renderServiceTemplate();
        assertTrue(template.contains("kind: Service"));
        assertTrue(template.contains("ClusterIP"));
    }

    @Nested
    @DisplayName("Value rendering edge cases")
    class ValueEdgeCases {
        @Test
        @DisplayName("Should handle nested maps")
        void testNestedMaps() {
            chart.setValue("resources", Map.of("requests", Map.of("cpu", "100m", "memory", "128Mi")));
            String values = chart.renderValuesYaml();
            assertTrue(values.contains("resources:"));
            assertTrue(values.contains("requests:"));
            assertTrue(values.contains("cpu: 100m"));
        }
    }
}
