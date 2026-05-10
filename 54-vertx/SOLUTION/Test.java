package com.learning.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VertxSolutionTest {

    private VertxSolution solution;
    private Vertx mockVertx;
    private EventBus mockEventBus;

    @BeforeEach
    void setUp() {
        mockVertx = mock(Vertx.class);
        mockEventBus = mock(EventBus.class);
        when(mockVertx.eventBus()).thenReturn(mockEventBus);
        solution = new VertxSolution(mockVertx);
    }

    @Test
    void testCreate() {
        VertxSolution created = VertxSolution.create();
        assertNotNull(created);
    }

    @Test
    void testGetEventBus() {
        EventBus eventBus = solution.getEventBus();
        assertEquals(mockEventBus, eventBus);
    }

    @Test
    void testPublish() {
        solution.publish("test.address", "message");
        verify(mockEventBus).publish(eq("test.address"), eq("message"));
    }

    @Test
    void testRegisterConsumer() {
        io.vertx.core.Handler handler = mock(io.vertx.core.Handler.class);
        solution.registerConsumer("test", handler);
        verify(mockEventBus).consumer(eq("test"), any());
    }

    @Test
    void testCreateHttpServer() {
        HttpServer server = solution.createHttpServer();
        assertNotNull(server);
    }

    @Test
    void testCreateRouter() {
        Router router = solution.createRouter();
        assertNotNull(router);
    }
}