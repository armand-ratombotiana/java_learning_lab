package com.learning.micronaut;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class MicronautSolutionTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void testHelloEndpoint() {
        String result = client.toBlocking()
            .retrieve(HttpRequest.GET("/hello"));
        assertEquals("Hello from Micronaut", result);
    }

    @Test
    void testGreetingEndpoint() {
        String result = client.toBlocking()
            .retrieve(HttpRequest.GET("/greet/World"));
        assertEquals("Hello, World!", result);
    }
}