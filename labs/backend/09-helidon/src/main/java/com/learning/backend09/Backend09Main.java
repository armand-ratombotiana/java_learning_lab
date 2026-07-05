package com.learning.backend09;

import io.helidon.common.LogConfig;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.http.Status;
import io.helidon.webserver.http.Routing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helidon SE application entry point.
 *
 * Helidon is a set of Java libraries for writing microservices.
 * Helidon SE (Server-Sent) uses a functional, reactive programming model.
 * Helidon MP (MicroProfile) uses Jakarta EE annotations.
 *
 * Here we use Helidon SE with the Routing builder.
 */
public class Backend09Main {

    private static final Logger log = LoggerFactory.getLogger(Backend09Main.class);

    public static void main(String[] args) {
        LogConfig.configureRuntime();

        WebServer server = WebServer.builder()
            .routing(Backend09Main::createRouting)
            .port(8080)
            .build()
            .start();

        log.info("Helidon SE server started on http://localhost:{}", server.port());
        System.out.println("=== Helidon Lab is running on http://localhost:" + server.port() + " ===");
    }

    /**
     * Configures HTTP routing using the functional Routing API.
     */
    static void createRouting(Routing.Builder routing) {
        routing
            .get("/", (req, res) -> res.send("Welcome to Helidon SE Lab!"))
            .get("/api/greet/{name}", Backend09Main::greet)
            .get("/health", new HealthResource())
            .get("/api/items", new ItemController())
            .post("/api/items", new ItemController());
    }

    /**
     * Simple greeting handler using path parameters.
     */
    private static void greet(ServerRequest req, ServerResponse res) {
        String name = req.path().pathParameters().get("name");
        log.info("Greeting request for: {}", name);
        res.send("Hello, " + name + "! Welcome to Helidon.");
    }
}
