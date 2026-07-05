package com.net.rest;

import java.util.*;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;

public class RestClient {

    public static class ApiResponse {
        public final int statusCode;
        public final String body;
        public final Map<String, List<String>> headers;

        public ApiResponse(int statusCode, String body, Map<String, List<String>> headers) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = headers;
        }

        public boolean isSuccess() { return statusCode >= 200 && statusCode < 300; }
        public boolean isNotFound() { return statusCode == 404; }
    }

    public static class RestApiClient {
        private final String baseUrl;
        private final Map<String, String> defaultHeaders = new HashMap<>();

        public RestApiClient(String baseUrl) {
            this.baseUrl = baseUrl;
            defaultHeaders.put("Accept", "application/json");
        }

        public void setAuth(String token) {
            defaultHeaders.put("Authorization", "Bearer " + token);
        }

        public ApiResponse get(String path, Map<String, String> queryParams) {
            String url = baseUrl + path;
            if (queryParams != null && !queryParams.isEmpty()) {
                StringBuilder qs = new StringBuilder("?");
                queryParams.forEach((k, v) -> qs.append(k).append("=").append(v).append("&"));
                url += qs.substring(0, qs.length() - 1);
            }
            System.out.println("GET " + url);
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("content-type", List.of("application/json"));
            return new ApiResponse(200, "{\"id\":1,\"name\":\"Alice\"}", headers);
        }

        public ApiResponse post(String path, String jsonBody) {
            String url = baseUrl + path;
            System.out.println("POST " + url + " | body: " + jsonBody);
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("content-type", List.of("application/json"));
            return new ApiResponse(201, "{\"id\":2,\"name\":\"Bob\"}", headers);
        }

        public ApiResponse put(String path, String jsonBody) {
            System.out.println("PUT " + baseUrl + path + " | body: " + jsonBody);
            return new ApiResponse(200, "{\"status\":\"updated\"}", new HashMap<>());
        }

        public ApiResponse delete(String path) {
            System.out.println("DELETE " + baseUrl + path);
            return new ApiResponse(204, "", new HashMap<>());
        }
    }

    public static void main(String[] args) {
        RestApiClient client = new RestApiClient("https://api.example.com/v1");
        client.setAuth("token-123");

        ApiResponse users = client.get("/users", Map.of("page", "1", "limit", "10"));
        System.out.println("GET status: " + users.statusCode + " body: " + users.body);

        ApiResponse created = client.post("/users", "{\"name\":\"Bob\",\"email\":\"bob@test.com\"}");
        System.out.println("POST status: " + created.statusCode + " body: " + created.body);

        client.delete("/users/1");
    }
}
