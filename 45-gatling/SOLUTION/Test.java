package com.learning.gatling;

import io.gatling.core.session.Session;
import io.gatling.http.Predef.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GatlingSolutionTest {

    private GatlingSolution solution;

    @BeforeEach
    void setUp() {
        solution = new GatlingSolution();
    }

    @Test
    void testCreateGetRequest() {
        var request = solution.createGetRequest("http://api.example.com/users");
        assertNotNull(request);
    }

    @Test
    void testCreatePostRequest() {
        var request = solution.createPostRequest(
            "http://api.example.com/users",
            "{\"name\":\"test\"}"
        );
        assertNotNull(request);
    }

    @Test
    void testCreatePutRequest() {
        var request = solution.createPutRequest(
            "http://api.example.com/users/1",
            "{\"name\":\"updated\"}"
        );
        assertNotNull(request);
    }

    @Test
    void testCreateDeleteRequest() {
        var request = solution.createDeleteRequest("http://api.example.com/users/1");
        assertNotNull(request);
    }

    @Test
    void testCreateScenario() {
        var request = solution.createGetRequest("http://api.example.com/test");
        var chain = solution.createScenario("Test Scenario", request);
        assertNotNull(chain);
    }

    @Test
    void testSetSessionVariable() {
        Session mockSession = mock(Session.class);
        when(mockSession.set(anyString(), any())).thenReturn(mockSession);
        Session result = solution.setSessionVariable(mockSession, "userId", "123");
        assertNotNull(result);
    }
}