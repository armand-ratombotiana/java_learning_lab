package com.learning.vertx.webclient;

public class VertxWebClientLab {

    public static void main(String[] args) {
        System.out.println("=== Vert.x Web Client Lab ===\n");

        System.out.println("1. Vert.x Server Example:");
        System.out.println("   Vertx vertx = Vertx.vertx();");
        System.out.println("   WebClient client = WebClient.create(vertx);");
        System.out.println("   Router router = Router.router(vertx);");
        System.out.println("   router.get(\"/api/users\").handler(ctx -> ctx.json(users));");
        System.out.println("   router.get(\"/fetch/external\").handler(ctx -> {");
        System.out.println("       client.getAbs(url).send(ar -> { ... });");
        System.out.println("   });");

        System.out.println("\n2. Vert.x Features:");
        System.out.println("   - Event-driven, non-blocking architecture");
        System.out.println("   - Polyglot (Java, Kotlin, JavaScript, Groovy)");
        System.out.println("   - Web client for HTTP requests");
        System.out.println("   - Event bus for messaging");
        System.out.println("   - Verticles for deployment units");

        System.out.println("\n=== Vert.x Web Client Lab Complete ===");
    }
}