package com.learning.vertx;

import io.vertx.core.Vertx;

public class VertxLab {

    public static void main(String[] args) {
        System.out.println("=== Vert.x Learning Lab ===\n");

        demonstrateVertxInstance();
        demonstrateEventLoop();
    }

    private static void demonstrateVertxInstance() {
        System.out.println("--- Vert.x Instance ---");
        Vertx vertx = Vertx.vertx();
        System.out.println("Vert.x instance created: " + vertx);
        vertx.close();
    }

    private static void demonstrateEventLoop() {
        System.out.println("\n--- Event Loop ---");
        Vertx.vertx().createHttpServer()
            .requestHandler(req -> req.response()
                .putHeader("content-type", "text/plain")
                .end("Hello from Vert.x!"))
            .listen(8080)
            .onSuccess(server -> System.out.println("Server started on port 8080"))
            .onFailure(err -> System.out.println("Failed: " + err.getMessage()));
    }
}