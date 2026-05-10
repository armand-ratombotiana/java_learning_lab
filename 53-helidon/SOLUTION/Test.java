package com.learning.helidon;

import io.helidon.webserver.WebServer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HelidonSolutionTest {

    @Test
    void testCreateServer() {
        HelidonSolution solution = new HelidonSolution();
        WebServer server = solution.createServer(
            io.helidon.webserver.Routing.builder().build()
        );
        assertNotNull(server);
    }

    @Test
    void testCreateRouting() {
        HelidonSolution solution = new HelidonSolution();
        var routing = solution.createRouting();
        assertNotNull(routing);
    }

    @Test
    void testHelloRoute() {
        var route = new HelidonSolution.HelloRoute();
        assertNotNull(route);
    }

    @Test
    void testGreetingRoute() {
        var route = new HelidonSolution.GreetingRoute();
        assertNotNull(route);
    }
}