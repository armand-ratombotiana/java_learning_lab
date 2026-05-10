package com.learning.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

class WireMockSolutionTest {

    private WireMockServer wireMockServer;
    private WireMockSolution solution;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        solution = new WireMockSolution(wireMockServer);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testStubGetRequest() {
        solution.stubGetRequest("/api/test", 200, "{\"status\":\"ok\"}");
        String response = given().get("http://localhost:8080/api/test").get().getBody().asString();
        assertTrue(response.contains("ok"));
    }

    @Test
    void testStubPostRequest() {
        solution.stubPostRequest("/api/create", "{\"name\":\"test\"}", 201, "{\"id\":1}");
        String response = given()
            .body("{\"name\":\"test\"}")
            .post("http://localhost:8080/api/create")
            .get().getBody().asString();
        assertTrue(response.contains("id"));
    }

    @Test
    void testStubWithHeaders() {
        solution.stubWithHeaders("/api/header", Map.of("Content-Type", "application/json"), "header-ok");
        String response = given()
            .header("Content-Type", "application/json")
            .get("http://localhost:8080/api/header")
            .get().getBody().asString();
        assertTrue(response.contains("header-ok"));
    }

    @Test
    void testStubWithDelay() {
        solution.stubWithDelay("/api/delay", 100, "delayed-response");
        long start = System.currentTimeMillis();
        String response = given().get("http://localhost:8080/api/delay").get().getBody().asString();
        long duration = System.currentTimeMillis() - start;
        assertTrue(duration >= 100);
    }

    @Test
    void testVerifyRequest() {
        solution.stubGetRequest("/api/verify", 200, "ok");
        given().get("http://localhost:8080/api/verify").get();
        solution.verifyRequest("/api/verify", 1);
    }
}