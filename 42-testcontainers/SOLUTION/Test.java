package com.learning.testcontainers;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestcontainersSolutionTest {

    @Test
    void testCreatePostgresContainer() {
        TestcontainersSolution solution = new TestcontainersSolution();
        PostgreSQLContainer container = solution.createPostgresContainer();
        assertNotNull(container);
        assertEquals("postgres:15", container.getDockerImageName());
    }

    @Test
    void testCreateMySQLContainer() {
        TestcontainersSolution solution = new TestcontainersSolution();
        var container = solution.createMySQLContainer();
        assertNotNull(container);
    }

    @Test
    void testCreateKafkaContainer() {
        TestcontainersSolution solution = new TestcontainersSolution();
        KafkaContainer container = solution.createKafkaContainer();
        assertNotNull(container);
    }

    @Test
    void testCreateGenericContainer() {
        TestcontainersSolution solution = new TestcontainersSolution();
        GenericContainer container = solution.createGenericContainer("nginx:latest");
        assertNotNull(container);
        assertEquals("nginx:latest", container.getDockerImageName());
    }

    @Test
    void testConfigureContainer() {
        TestcontainersSolution solution = new TestcontainersSolution();
        GenericContainer container = solution.createGenericContainer("nginx");
        solution.configureContainer(container, Map.of("ENV_VAR", "value"));
    }

    @Test
    void testGetJdbcUrl() {
        TestcontainersSolution solution = new TestcontainersSolution();
        PostgreSQLContainer container = solution.createPostgresContainer();
        String url = solution.getJdbcUrl(container);
        assertTrue(url.contains("jdbc:postgresql"));
    }
}