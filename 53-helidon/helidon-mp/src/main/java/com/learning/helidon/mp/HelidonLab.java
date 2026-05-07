package com.learning.helidon.mp;

public class HelidonLab {

    public static void main(String[] args) {
        System.out.println("=== Helidon MP Lab ===\n");

        System.out.println("1. Helidon Routing Example:");
        System.out.println("   HttpRouting routing = HttpRouting.builder()");
        System.out.println("       .get(\"/hello\", (req, res) -> res.send(\"Hello from Helidon!\"))");
        System.out.println("       .get(\"/api/tasks\", (req, res) -> res.send(getTasks()))");
        System.out.println("       WebServer server = WebServer.builder().port(8080).routing(routing).build();");
        System.out.println("       server.start();");

        System.out.println("\n2. Helidon Features:");
        System.out.println("   - Microprofile support");
        System.out.println("   - Reactive programming model");
        System.out.println("   - Built-in health and metrics");
        System.out.println("   - Configuration with YAML/properties");
        System.out.println("   - Native image ready");

        System.out.println("\n=== Helidon MP Lab Complete ===");
    }
}