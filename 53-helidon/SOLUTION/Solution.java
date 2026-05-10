package com.learning.helidon;

import io.helidon.webserver.WebServer;
import io.helidon.webserver.Routing;
import io.helidon.webserver.http.HttpRouting;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class HelidonSolution {

    public WebServer createServer(Routing routing) {
        return WebServer.builder()
            .routing(routing)
            .build();
    }

    public void startServer(WebServer server) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        server.start().thenAccept(ws -> {
            System.out.println("Server started at: " + ws.port());
            latch.countDown();
        });
        latch.await(30, TimeUnit.SECONDS);
    }

    public static class HelloRoute implements io.helidon.webserver.http.Handler {
        @Override
        public void handle(io.helidon.webserver.http.ServerRequest req,
                          io.helidon.webserver.http.ServerResponse res) {
            res.send("Hello from Helidon");
        }
    }

    public static class GreetingRoute implements io.helidon.webserver.http.Handler {
        @Override
        public void handle(io.helidon.webserver.http.ServerRequest req,
                          io.helidon.webserver.http.ServerResponse res) {
            String name = req.path().pathParameters().get("name").value();
            res.send("Hello, " + name + "!");
        }
    }

    public static class HealthRoute implements io.helidon.webserver.http.Handler {
        @Override
        public void handle(io.helidon.webserver.http.ServerRequest req,
                          io.helidon.webserver.http.ServerResponse res) {
            res.send("{\"status\":\"UP\"}");
        }
    }

    public HttpRouting.Builder createRouting() {
        return HttpRouting.builder()
            .get("/hello", new HelloRoute())
            .get("/greet/{name}", new GreetingRoute())
            .get("/health", new HealthRoute());
    }
}