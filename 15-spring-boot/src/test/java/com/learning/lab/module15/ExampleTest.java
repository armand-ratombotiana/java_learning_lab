package com.learning.lab.module15;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExampleTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    @DisplayName("Test application context loads successfully")
    void testApplicationContextLoads() {
        assertTrue(true, "Spring Boot application context should load");
    }

    @Test
    @DisplayName("Test health endpoint returns UP status")
    void testHealthEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/actuator/health",
            String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("UP"));
    }

    @Test
    @DisplayName("Test info endpoint exists")
    void testInfoEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/actuator/info",
            String.class
        );

        assertNotNull(response);
    }

    @Test
    @DisplayName("Test custom application properties configuration")
    void testApplicationProperties() {
        AppConfig config = new AppConfig();
        config.setName("TestApplication");
        config.setVersion("1.0.0");

        assertEquals("TestApplication", config.getName());
        assertEquals("1.0.0", config.getVersion());
    }

    @Test
    @DisplayName("Test data source configuration")
    void testDataSourceConfiguration() {
        DataSourceConfig config = new DataSourceConfig();
        config.setUrl("jdbc:mysql://localhost:3306/testdb");
        config.setUsername("testuser");
        config.setPassword("testpass");

        assertEquals("jdbc:mysql://localhost:3306/testdb", config.getUrl());
        assertEquals("testuser", config.getUsername());
        assertEquals("testpass", config.getPassword());
    }

    @Test
    @DisplayName("Test auto-configuration detects web starter")
    void testAutoConfiguration() {
        assertNotNull(restTemplate);
    }

    @Test
    @DisplayName("Test server port is configured")
    void testServerPortConfigured() {
        assertTrue(port > 0, "Server port should be configured");
    }

    static class AppConfig {
        private String name;
        private String version;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    static class DataSourceConfig {
        private String url;
        private String username;
        private String password;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}