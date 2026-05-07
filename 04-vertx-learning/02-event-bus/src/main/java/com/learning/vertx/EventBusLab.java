package com.learning.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

public class EventBusLab {

    public static void main(String[] args) {
        System.out.println("=== Vert.x Event Bus Lab ===\n");

        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new MessageReceiver());
        vertx.deployVerticle(new MessageSender());

        try { Thread.sleep(2000); } catch (Exception e) {}
        vertx.close();
    }
}

class MessageReceiver extends AbstractVerticle {
    @Override
    public void start() {
        EventBus eb = vertx.eventBus();
        eb.consumer("test.address", msg -> {
            System.out.println("Received: " + msg.body());
            msg.reply("Message received!");
        });
    }
}

class MessageSender extends AbstractVerticle {
    @Override
    public void start() {
        EventBus eb = vertx.eventBus();
        eb.request("test.address", "Hello Event Bus", reply -> {
            System.out.println("Reply: " + reply.result().body());
        });
    }
}