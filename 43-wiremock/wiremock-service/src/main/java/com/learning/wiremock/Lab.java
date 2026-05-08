package com.learning.wiremock;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class Lab {
    record HttpRequest(String method, String path, Map<String, String> headers, String body) {
        String param(String name) {
            try {
                var query = URI.create("http://localhost" + path).getQuery();
                if (query == null) return null;
                return Arrays.stream(query.split("&"))
                    .map(p -> p.split("=", 2))
                    .filter(p -> p[0].equals(name))
                    .findFirst().map(p -> p.length > 1 ? p[1] : "").orElse(null);
            } catch (Exception e) { return null; }
        }
    }

    record HttpResponse(int status, Map<String, String> headers, String body) {}

    record StubMapping(Supplier<Boolean> matchFn, Supplier<HttpResponse> responseFn, String description) {}

    static class WireMockServer {
        List<StubMapping> stubs = new CopyOnWriteArrayList<>();
        List<HttpRequest> servedRequests = new CopyOnWriteArrayList<>();

        void givenThat(StubMapping stub) { stubs.add(stub); }

        HttpResponse handle(HttpRequest request) {
            servedRequests.add(request);
            for (var stub : stubs) {
                if (stub.matchFn().get()) return stub.responseFn().get();
            }
            return new HttpResponse(404, Map.of("Content-Type", "text/plain"), "No matching stub");
        }

        HttpResponse get(String path) {
            return handle(new HttpRequest("GET", path, new LinkedHashMap<>(), ""));
        }

        HttpResponse post(String path, String body) {
            return handle(new HttpRequest("POST", path, Map.of("Content-Type", "application/json"), body));
        }

        List<HttpRequest> getServedRequests() { return servedRequests; }

        void reset() { stubs.clear(); servedRequests.clear(); }

        int countRequestsMatching(String method, String pathPattern) {
            return (int) servedRequests.stream()
                .filter(r -> r.method().equals(method) && r.path().contains(pathPattern.replace("*", "")))
                .count();
        }

        StubMapping stubForGet(String pathPattern, int status, String body) {
            return new StubMapping(
                () -> true,
                () -> new HttpResponse(status, Map.of("Content-Type", "application/json"), body),
                "GET " + pathPattern
            );
        }
    }

    static class ResponseTemplate {
        static String templatize(String template, Map<String, String> params) {
            var result = template;
            for (var e : params.entrySet())
                result = result.replace("{{" + e.getKey() + "}}", e.getValue());
            return result;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== WireMock Concepts Lab ===\n");

        WireMockServer wiremock = new WireMockServer();

        System.out.println("1. Stubbing HTTP endpoints:");
        wiremock.givenThat(new StubMapping(
            () -> true,
            () -> new HttpResponse(200, Map.of("Content-Type", "application/json"),
                "{\"id\":1,\"name\":\"WireMock\",\"version\":\"3.x\"}"),
            "GET /api/resource"
        ));

        var resp = wiremock.get("/api/resource");
        System.out.println("   GET /api/resource -> " + resp.status() + " " + resp.body());

        System.out.println("\n2. Response templating:");
        wiremock.givenThat(new StubMapping(
            () -> true,
            () -> {
                HttpRequest req = wiremock.getServedRequests().isEmpty() ? null :
                    wiremock.getServedRequests().get(wiremock.getServedRequests().size() - 1);
                String id = (req != null && req.path().contains("user/")) ?
                    req.path().substring(req.path().lastIndexOf('/') + 1) : "0";
                String body = "{\"userId\":\"" + id + "\",\"name\":\"User " + id + "\"}";
                return new HttpResponse(200, Map.of("Content-Type", "application/json"), body);
            },
            "GET /api/user/{id}"
        ));

        var userResp = wiremock.get("/api/user/42");
        System.out.println("   GET /api/user/42 -> " + userResp.body());

        System.out.println("\n3. Verification (request counting):");
        wiremock.get("/api/resource");
        wiremock.get("/api/resource");
        wiremock.get("/api/user/1");
        System.out.println("   Requests to /api/resource: " + wiremock.countRequestsMatching("GET", "/api/resource"));
        System.out.println("   Total served requests: " + wiremock.getServedRequests().size());

        System.out.println("\n4. Simulating errors / fault injection:");
        wiremock.givenThat(new StubMapping(
            () -> true,
            () -> new HttpResponse(500, Map.of("Content-Type", "application/json"),
                "{\"error\":\"Internal Server Error\",\"code\":\"SVC-001\"}"),
            "POST /api/orders/error"
        ));

        var errResp = wiremock.post("/api/orders/error", "{}");
        System.out.println("   POST /api/orders/error -> " + errResp.status() + " " + errResp.body());

        System.out.println("\n5. Delayed responses (simulating slow services):");
        wiremock.givenThat(new StubMapping(
            () -> true,
            () -> {
                try { Thread.sleep(200); } catch (InterruptedException e) {}
                return new HttpResponse(200, Map.of("Content-Type", "text/plain"), "Slow response (200ms)");
            },
            "GET /api/slow"
        ));

        long start = System.currentTimeMillis();
        var slowResp = wiremock.get("/api/slow");
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("   GET /api/slow -> " + slowResp.body() + " (" + elapsed + "ms)");

        System.out.println("\n6. Stateful stubs:");
        wiremock.givenThat(new StubMapping(
            () -> wiremock.countRequestsMatching("GET", "/api/state") < 2,
            () -> new HttpResponse(200, Map.of(), "First state"),
            "GET /api/state (first 2 calls)"
        ));
        wiremock.givenThat(new StubMapping(
            () -> wiremock.countRequestsMatching("GET", "/api/state") >= 2,
            () -> new HttpResponse(200, Map.of(), "Subsequent state"),
            "GET /api/state (after 2 calls)"
        ));

        System.out.println("   Call 1: " + wiremock.get("/api/state").body());
        System.out.println("   Call 2: " + wiremock.get("/api/state").body());
        System.out.println("   Call 3: " + wiremock.get("/api/state").body());

        System.out.println("\n7. Record-Playback:");
        System.out.println("   WireMock can record from real API -> generate stubs");
        System.out.println("   java -jar wiremock.jar --record-mappings --proxy-all=\"http://real-api.com\"");

        System.out.println("\n8. Stub Priority / Matching precedence:");
        System.out.println("   Higher priority stubs matched first");
        System.out.println("   Exact match > URL pattern > wildcard");

        wiremock.reset();
        System.out.println("\n   Reset done, stub count: " + wiremock.stubs.size());

        System.out.println("\n=== Lab Complete ===");
    }
}
