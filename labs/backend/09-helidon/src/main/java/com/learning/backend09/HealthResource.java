package com.learning.backend09;

import io.helidon.http.Status;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Health check handler for Helidon.
 *
 * Implements Handler functional interface for request processing.
 * Returns a JSON health status response.
 */
public class HealthResource implements Handler {

    private static final Logger log = LoggerFactory.getLogger(HealthResource.class);

    @Override
    public void handle(ServerRequest req, ServerResponse res) {
        log.info("Health check requested");

        String json = """
            {
                "status": "UP",
                "service": "helidon-lab",
                "timestamp": "%s"
            }
            """.formatted(java.time.Instant.now().toString());

        res.status(Status.OK_200);
        res.header("Content-Type", "application/json");
        res.send(json);
    }
}
