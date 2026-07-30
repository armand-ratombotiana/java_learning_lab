package com.distributedsystems.deep.lab08;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * ServiceMeshLab — implements sidecar proxy simulation, mTLS handshake,
 * weighted virtual service routing, and circuit breaker.
 */
public class ServiceMeshLab {

    static class SidecarProxy {
        final String serviceName;
        final Map<String, String> routingTable = new HashMap<>();
        final AtomicLong requestCount = new AtomicLong(0);
        SidecarProxy(String name) { this.serviceName = name; }
        void addRoute(String from, String to) { routingTable.put(from, to); }
        String handleRequest(String dest, String payload) {
            requestCount.incrementAndGet();
            String actualDest = routingTable.getOrDefault(dest, dest);
            return "[" + serviceName + " proxy] " + payload + " -> " + actualDest;
        }
    }

    static class Certificate {
        final String identity; final long issuedAt;
        Certificate(String identity) { this.identity = identity; this.issuedAt = System.currentTimeMillis(); }
        boolean verify(Certificate other) { return other.issuedAt > 0; }
    }

    static class MtlsHandshake {
        static boolean handshake(Certificate client, Certificate server) { return client.verify(server) && server.verify(client); }
    }

    static class WeightedRoute { final String destination; final int weight;
        WeightedRoute(String d, int w) { destination = d; weight = w; } }

    static class VirtualService {
        final List<WeightedRoute> routes = new ArrayList<>(); final Random rnd = new Random();
        void addRoute(WeightedRoute route) { routes.add(route); }
        String route() {
            int total = routes.stream().mapToInt(r -> r.weight).sum();
            int roll = rnd.nextInt(total), acc = 0;
            for (var r : routes) { acc += r.weight; if (roll < acc) return r.destination; }
            return routes.get(routes.size() - 1).destination;
        }
    }

    static class CircuitBreaker {
        enum State { CLOSED, OPEN, HALF_OPEN }
        volatile State state = State.CLOSED;
        int failureCount = 0; final int threshold = 3; final long openTimeoutMs = 1000; long openedAt = 0;

        synchronized boolean allowRequest() {
            if (state == State.OPEN && System.currentTimeMillis() - openedAt > openTimeoutMs) state = State.HALF_OPEN;
            return state != State.OPEN;
        }
        synchronized void onSuccess() { if (state == State.HALF_OPEN) state = State.CLOSED; failureCount = 0; }
        synchronized void onFailure() {
            if (++failureCount >= threshold) { state = State.OPEN; openedAt = System.currentTimeMillis(); System.out.println("  Circuit OPEN"); }
        }
    }

    public static void main(String[] args) {
        var proxy = new SidecarProxy("reviews");
        proxy.addRoute("reviews.default.svc", "reviews-v2");
        System.out.println("Sidecar: " + proxy.handleRequest("reviews.default.svc", "GET /reviews"));

        var clientCert = new Certificate("spiffe://cluster.local/ns/default/sa/frontend");
        var serverCert = new Certificate("spiffe://cluster.local/ns/default/sa/backend");
        System.out.println("mTLS handshake: " + (MtlsHandshake.handshake(clientCert, serverCert) ? "SUCCESS" : "FAILED"));

        var vs = new VirtualService(); vs.addRoute(new WeightedRoute("reviews-v1", 90)); vs.addRoute(new WeightedRoute("reviews-v2", 10));
        Map<String,Integer> stats = new HashMap<>(); for (int i = 0; i < 1000; i++) stats.merge(vs.route(), 1, Integer::sum);
        System.out.println("Canary: " + stats);

        CircuitBreaker cb = new CircuitBreaker();
        for (int i = 0; i < 10; i++) {
            if (!cb.allowRequest()) { System.out.println("  Req " + i + ": rejected"); continue; }
            if (i < 4) { cb.onFailure(); System.out.println("  Req " + i + ": FAILED"); }
            else { cb.onSuccess(); System.out.println("  Req " + i + ": OK"); }
        }
    }
}