package com.net.http;

import java.util.*;
import java.util.concurrent.*;

public class HttpClientSim {

    public static class HttpRequest {
        public final String method;
        public final String path;
        public final Map<String, String> headers;
        public final String body;

        public HttpRequest(String method, String path, Map<String, String> headers, String body) {
            this.method = method;
            this.path = path;
            this.headers = headers;
            this.body = body;
        }

        @Override
        public String toString() {
            return method + " " + path + " HTTP/1.1\n" + headers + "\n\n" + body;
        }
    }

    public static class HttpResponse {
        public final int statusCode;
        public final String statusText;
        public final Map<String, String> headers;
        public final String body;

        public HttpResponse(int statusCode, String statusText, Map<String, String> headers, String body) {
            this.statusCode = statusCode;
            this.statusText = statusText;
            this.headers = headers;
            this.body = body;
        }

        @Override
        public String toString() {
            return "HTTP/1.1 " + statusCode + " " + statusText + "\n" + headers + "\n\n" + body;
        }
    }

    public static class SimpleClient {
        private final String baseUrl;

        public SimpleClient(String baseUrl) { this.baseUrl = baseUrl; }

        public HttpResponse get(String path) {
            return send(new HttpRequest("GET", path, new HashMap<>(), null));
        }

        public HttpResponse post(String path, String body, String contentType) {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", contentType);
            headers.put("Content-Length", String.valueOf(body.length()));
            return send(new HttpRequest("POST", path, headers, body));
        }

        private HttpResponse send(HttpRequest req) {
            System.out.println("Sending: " + req.method + " " + baseUrl + req.path);
            Map<String, String> respHeaders = new HashMap<>();
            respHeaders.put("Server", "SimServer/1.0");
            respHeaders.put("Date", new Date().toString());
            return new HttpResponse(200, "OK", respHeaders,
                "Response to " + req.method + " " + req.path);
        }
    }

    public static void main(String[] args) {
        SimpleClient client = new SimpleClient("http://api.example.com");

        HttpResponse resp1 = client.get("/users/1");
        System.out.println("Response: " + resp1.statusCode + " " + resp1.statusText);
        System.out.println("Body: " + resp1.body);

        HttpResponse resp2 = client.post("/users", "{\"name\":\"Alice\"}", "application/json");
        System.out.println("Response: " + resp2.statusCode + " " + resp2.statusText);
    }
}
