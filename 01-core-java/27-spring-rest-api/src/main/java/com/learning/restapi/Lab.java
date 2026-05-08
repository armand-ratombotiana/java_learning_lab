package com.learning.restapi;

import java.util.*;
import java.util.function.*;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== REST API Lab (Conceptual) ===\n");

        restFundamentals();
        requestResponse();
        resourceDesign();
        errorHandling();
        versioningPagination();
    }

    static void restFundamentals() {
        System.out.println("--- REST Fundamentals ---");

        for (var m : List.of("GET (idempotent, safe)", "POST (not idempotent)",
                "PUT (idempotent)", "PATCH (not idempotent)", "DELETE (idempotent)"))
            System.out.println("  " + m);

        System.out.println("\n  Richardson Maturity Model:");
        for (var l : List.of("Level 0: HTTP as transport (SOAP-like)",
                "Level 1: Resources (multiple endpoints)",
                "Level 2: HTTP verbs per resource",
                "Level 3: HATEOAS (links in responses)"))
            System.out.println("  " + l);

        System.out.println("\n  Resource naming:");
        System.out.println("""
            GET    /api/users          List
            GET    /api/users/{id}     Single
            POST   /api/users          Create
            PUT    /api/users/{id}     Full update
            PATCH  /api/users/{id}     Partial update
            DELETE /api/users/{id}     Delete""");
    }

    static void requestResponse() {
        System.out.println("\n--- Request/Response Handling ---");

        record Request(String method, String path) {}
        record Response(int status, String body, Map<String, String> headers) {}

        class Router {
            final Map<String, Function<Request, Response>> routes = new LinkedHashMap<>();
            void get(String p, Function<Request, Response> h) { routes.put("GET:" + p, h); }
            void post(String p, Function<Request, Response> h) { routes.put("POST:" + p, h); }
            Response dispatch(Request r) {
                var h = routes.get(r.method() + ":" + r.path());
                if (h == null) return new Response(404, "{\"error\":\"Not Found\"}", Map.of("Content-Type", "application/json"));
                return h.apply(r);
            }
        }

        var router = new Router();
        router.get("/api/users", r -> new Response(200, "[{\"id\":1,\"name\":\"Alice\"}]", Map.of("Content-Type", "application/json")));
        router.post("/api/users", r -> new Response(201, "{\"id\":3,\"name\":\"Charlie\"}", Map.of("Location", "/api/users/3")));

        var r1 = router.dispatch(new Request("GET", "/api/users"));
        System.out.println("  GET /api/users -> " + r1.status() + " " + r1.body().substring(0, 20) + "...");
        var r2 = router.dispatch(new Request("POST", "/api/users"));
        System.out.println("  POST /api/users -> " + r2.status() + " " + r2.headers());
        var r3 = router.dispatch(new Request("GET", "/api/unknown"));
        System.out.println("  GET /api/unknown -> " + r3.status() + " " + r3.body());
    }

    static void resourceDesign() {
        System.out.println("\n--- Resource Design ---");

        System.out.println("  Content types:");
        for (var c : List.of("JSON: application/json", "XML: application/xml",
                "Problem: application/problem+json", "Collection: data + page + size + total"))
            System.out.println("  " + c);

        System.out.println("\n  Content negotiation (Accept header):");
        System.out.println("""
            Accept: application/json  -> JSON response
            Accept: application/xml   -> XML response
            Accept: */*               -> default (JSON)""");

        System.out.println("\n  Resource representations:");
        System.out.println("""
            User JSON:
            {"id":1, "name":"Alice", "email":"alice@x.com", "roles":["USER"]}

            Error:
            {"timestamp":"...", "status":400, "error":"Bad Request", "path":"/api/users"}""");
    }

    static void errorHandling() {
        System.out.println("\n--- Error Handling ---");

        for (var c : List.of("200 OK", "201 Created", "204 No Content",
                "400 Bad Request", "401 Unauthorized", "403 Forbidden",
                "404 Not Found", "409 Conflict", "422 Unprocessable Entity",
                "429 Too Many Requests", "500 Internal Server Error", "503 Service Unavailable"))
            System.out.println("  " + c);

        System.out.println("\n  Problem Details (RFC 7807):");
        System.out.println("""
            {"type":"about:blank", "title":"Not Found", "status":404,
             "detail":"User 999 not found", "instance":"/api/users/999"}""");
    }

    static void versioningPagination() {
        System.out.println("\n--- Versioning & Pagination ---");

        for (var v : List.of("URI Path: /api/v1/users",
                "Header: Accept: application/vnd.api+json;version=1",
                "Query: /api/users?version=1",
                "Content: Accept: application/vnd.myapp.v1+json"))
            System.out.println("  " + v);

        System.out.println("\n  Paginated response:");
        System.out.println("""
            GET /api/users?page=2&size=20
            {
              "data": [...],
              "page": 2, "size": 20, "totalElements": 142, "totalPages": 8,
              "links": {"self": "...", "first": "...", "prev": "...", "next": "...", "last": "..."}
            }""");

        System.out.println("\n  Filtering and sorting:");
        for (var p : List.of("?status=active", "?sort=-createdAt,name",
                "?fields=id,name,email", "?search=john"))
            System.out.println("  " + p);

        System.out.println("\n  Security: CORS headers, HTTPS, rate limiting, JWT auth, input validation");
    }
}
