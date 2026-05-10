package com.learning.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class VertxSolution {

    private final Vertx vertx;

    public VertxSolution(Vertx vertx) {
        this.vertx = vertx;
    }

    public static VertxSolution create() {
        return new VertxSolution(Vertx.vertx());
    }

    public EventBus getEventBus() {
        return vertx.eventBus();
    }

    public void publish(String address, Object message) {
        getEventBus().publish(address, message);
    }

    public void send(String address, Object message, io.vertx.core.Handler<io.vertx.core.AsyncResult<io.vertx.core.eventbus.Message>> handler) {
        getEventBus().send(address, message, handler);
    }

    public void registerConsumer(String address, io.vertx.core.Handler<io.vertx.core.eventbus.Message> handler) {
        getEventBus().consumer(address, handler);
    }

    public HttpServer createHttpServer() {
        return vertx.createHttpServer();
    }

    public void createHttpServerAndListen(int port, io.vertx.core.Handler<HttpServer> handler) {
        createHttpServer().listen(port, handler);
    }

    public Router createRouter() {
        return Router.router(vertx);
    }

    public void route(Router router, String path, io.vertx.core.Handler<RoutingContext> handler) {
        router.get(path).handler(handler);
    }

    public static class JsonHandler implements io.vertx.core.Handler<RoutingContext> {
        @Override
        public void handle(RoutingContext rc) {
            JsonObject json = new JsonObject()
                .put("message", "Hello from Vert.x");
            rc.response()
                .putHeader("Content-Type", "application/json")
                .end(json.encode());
        }
    }

    public static class JsonPathHandler implements io.vertx.core.Handler<RoutingContext> {
        @Override
        public void handle(RoutingContext rc) {
            String name = rc.pathParam("name");
            JsonObject json = new JsonObject()
                .put("message", "Hello, " + name + "!");
            rc.response()
                .putHeader("Content-Type", "application/json")
                .end(json.encode());
        }
    }
}