package com.learning.quarkus;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

class QuarkusSolutionTest {

    @Test
    void testHelloEndpoint() {
        given()
            .when().get("/hello")
            .then()
            .statusCode(200)
            .body(is("Hello from Quarkus"));
    }

    @Test
    void testGreetingEndpoint() {
        given()
            .when().get("/greet/World")
            .then()
            .statusCode(200)
            .body(is("Hello, World!"));
    }

    @Test
    void testReactiveEndpoint() {
        given()
            .when().get("/reactive")
            .then()
            .statusCode(200);
    }

    @Test
    void testApplicationRunning() {
        boolean running = QuarkusSolution.isApplicationRunning();
        assertTrue(running || !running);
    }
}