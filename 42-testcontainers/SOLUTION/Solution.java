package com.learning.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;

import java.util.Map;

public class TestcontainersSolution {

    public PostgreSQLContainer createPostgresContainer() {
        PostgreSQLContainer container = new PostgreSQLContainer("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");
        return container;
    }

    public MySQLContainer createMySQLContainer() {
        MySQLContainer container = new MySQLContainer("mysql:8")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");
        return container;
    }

    public KafkaContainer createKafkaContainer() {
        KafkaContainer container = new KafkaContainer("confluentinc/cp-kafka:7.4.0");
        return container;
    }

    public GenericContainer createGenericContainer(String image) {
        GenericContainer container = new GenericContainer(image)
            .withExposedPorts(8080);
        return container;
    }

    public void configureContainer(GenericContainer container, Map<String, String> env) {
        env.forEach(container::withEnv);
    }

    public String getJdbcUrl(JdbcDatabaseContainer<?> container) {
        return container.getJdbcUrl();
    }

    public String getUsername(JdbcDatabaseContainer<?> container) {
        return container.getUsername();
    }

    public String getPassword(JdbcDatabaseContainer<?> container) {
        return container.getPassword();
    }
}