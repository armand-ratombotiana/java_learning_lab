package com.learning.backend10;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * JAX-RS resource using Quarkus.
 *
 * Quarkus supports both JAX-RS annotations (@Path, @GET, etc.)
 * and Spring Web annotations (@RestController, @GetMapping, etc.).
 *
 * @Path defines the base URI for this resource.
 * @GET / POST / PUT / DELETE map HTTP methods.
 * @Produces / @Consumes define media types.
 */
@Path("/api/greet")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GreetingResource {

    private static final Logger log = LoggerFactory.getLogger(GreetingResource.class);

    @GET
    public Map<String, String> greetDefault() {
        log.info("GET /api/greet");
        return Map.of("message", "Hello from Quarkus!");
    }

    @GET
    @Path("/{name}")
    public Map<String, String> greetName(@PathParam("name") String name) {
        log.info("GET /api/greet/{}", name);
        return Map.of("message", "Hello, " + name + "! Welcome to Quarkus.");
    }

    @POST
    public Map<String, Object> echoGreeting(Map<String, String> body) {
        String name = body.getOrDefault("name", "World");
        log.info("POST /api/greet with name={}", name);
        return Map.of(
            "message", "Hello, " + name + "!",
            "method", "POST"
        );
    }
}
