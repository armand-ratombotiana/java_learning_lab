package com.learning.camel;

import java.util.*;
import java.util.function.*;

public class Lab {
    record Exchange(Map<String, Object> headers, StringBuilder body) {}

    @FunctionalInterface
    interface Processor { void process(Exchange exchange); }

    static class Route {
        final String from;
        final List<Processor> processors = new ArrayList<>();

        Route(String from) { this.from = from; }

        Route filter(Predicate<Exchange> predicate) {
            processors.add(ex -> { if (!predicate.test(ex)) ex.body.setLength(0); });
            return this;
        }

        Route transform(Function<String, String> func) {
            processors.add(ex -> { var b = ex.body.toString(); if (!b.isEmpty()) ex.body.replace(0, b.length(), func.apply(b)); });
            return this;
        }

        Route enrich(String key, String value) {
            processors.add(ex -> ex.headers.put(key, value));
            return this;
        }

        Route split(String delimiter) {
            processors.add(ex -> {
                var parts = ex.body.toString().split(delimiter);
                ex.body.setLength(0);
                ex.body.append(String.join("\n", parts));
            });
            return this;
        }

        void send(String msg) {
            var ex = new Exchange(new HashMap<>(), new StringBuilder(msg));
            System.out.println("[" + from + "] Received: " + msg);
            for (var p : processors) p.process(ex);
            if (!ex.body.isEmpty()) System.out.println("[" + from + "] Result: " + ex.body);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Apache Camel Concepts: Routes, Processors, EIP ===");

        var route = new Route("file:input/orders")
            .filter(ex -> ex.body.toString().contains("ORDER"))
            .transform(String::toUpperCase)
            .enrich("timestamp", String.valueOf(System.currentTimeMillis()))
            .split(",");

        route.send("ORDER:apple,50");
        route.send("LOG:test,1");

        System.out.println("\n--- Content-Based Routing ---");
        var choice = new Route("direct:start");
        choice.processors.add(ex -> {
            var b = ex.body.toString();
            if (b.contains("URGENT")) ex.headers.put("channel", "fast");
            else if (b.contains("BULK")) ex.headers.put("channel", "batch");
            else ex.headers.put("channel", "normal");
            System.out.println("  Routed to: " + ex.headers.get("channel"));
        });
        choice.send("URGENT:processNow");
        choice.send("BULK:items100");
        choice.send("ROUTINE:dailyTask");

        System.out.println("\n--- Wire Tap (Simulated) ---");
        route.processors.add(0, ex -> System.out.println("  [WireTap] Logging: " + ex.body));
        route.send("ORDER:monitor,1");

        System.out.println("\n--- Recipient List ---");
        var recipients = List.of("jms:queue:A", "jms:queue:B");
        route.processors.add(ex -> {
            System.out.println("  [RecipientList] Sending to:");
            recipients.forEach(r -> System.out.println("    " + r));
        });
        route.send("ORDER:broadcast,99");
    }
}
