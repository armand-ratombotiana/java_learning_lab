package com.apex.apt;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ApexRestfulService {
    public record ResourceTemplate(String name, String uriTemplate, String method, String sourceType, String source) {}
    public record ApiResponse(int status, String body, Map<String, String> headers) {}
    public record OAuth2Client(String name, String tokenUrl, String clientId, String scope) {}

    private final Map<String, ResourceTemplate> templates = new ConcurrentHashMap<>();
    private final Map<String, Object> dataStore = new ConcurrentHashMap<>();
    private final List<String> requestLog = new ArrayList<>();

    public void registerTemplate(ResourceTemplate t) { templates.put(t.name(), t); }

    public ApiResponse handleRequest(String templateName, Map<String, String> pathParams, Map<String, String> queryParams, String body) {
        requestLog.add(templateName + " " + pathParams + " " + queryParams);
        var template = templates.get(templateName);
        if (template == null) return new ApiResponse(404, "{\"error\":\"Not found\"}", Map.of("Content-Type", "application/json"));

        return switch (template.method().toUpperCase()) {
            case "GET" -> handleGet(template, pathParams, queryParams);
            case "POST" -> handlePost(template, body);
            case "PUT" -> handlePut(template, pathParams, body);
            case "DELETE" -> handleDelete(template, pathParams);
            default -> new ApiResponse(405, "{\"error\":\"Method not allowed\"}", Map.of());
        };
    }

    private ApiResponse handleGet(ResourceTemplate t, Map<String, String> pathParams, Map<String, String> queryParams) {
        var key = buildKey(t.name(), pathParams);
        var data = dataStore.get(key);
        var sb = new StringBuilder();
        sb.append("{\"resource\":\"").append(t.name()).append("\",\"data\":");
        if (data != null) sb.append(data.toString());
        else sb.append("\"no data\"");
        if (!queryParams.isEmpty()) {
            sb.append(",\"params\":").append(queryParams);
        }
        sb.append(",\"offset\":0,\"limit\":100,\"hasMore\":false}");
        return new ApiResponse(200, sb.toString(), Map.of("Content-Type", "application/json", "ETag", hash(sb.toString())));
    }

    private ApiResponse handlePost(ResourceTemplate t, String body) {
        var key = UUID.randomUUID().toString();
        dataStore.put(t.name() + ":" + key, body);
        return new ApiResponse(201, "{\"id\":\"" + key + "\"}", Map.of("Content-Type", "application/json"));
    }

    private ApiResponse handlePut(ResourceTemplate t, Map<String, String> pathParams, String body) {
        var key = buildKey(t.name(), pathParams);
        dataStore.put(key, body);
        return new ApiResponse(200, "{\"updated\":true}", Map.of("Content-Type", "application/json"));
    }

    private ApiResponse handleDelete(ResourceTemplate t, Map<String, String> pathParams) {
        var key = buildKey(t.name(), pathParams);
        dataStore.remove(key);
        return new ApiResponse(200, "{\"deleted\":true}", Map.of("Content-Type", "application/json"));
    }

    private String buildKey(String name, Map<String, String> params) {
        return name + ":" + String.join("_", params.values());
    }

    public List<String> getRequestLog() { return List.copyOf(requestLog); }
    public void clearLog() { requestLog.clear(); }

    private String hash(String s) { return Integer.toHexString(s.hashCode()); }

    public OAuth2Client registerOAuth2Client(String name, String tokenUrl, String clientId, String scope) {
        return new OAuth2Client(name, tokenUrl, clientId, scope);
    }

    public static ApexRestfulService createSample() {
        var svc = new ApexRestfulService();
        svc.registerTemplate(new ResourceTemplate("employees", "employees/{id}", "GET", "SQL", "SELECT * FROM employees WHERE emp_id = :id"));
        svc.registerTemplate(new ResourceTemplate("employees_create", "employees", "POST", "PLSQL", "INSERT INTO employees..."));
        svc.registerTemplate(new ResourceTemplate("employees_update", "employees/{id}", "PUT", "PLSQL", "UPDATE employees SET..."));
        svc.registerTemplate(new ResourceTemplate("employees_delete", "employees/{id}", "DELETE", "PLSQL", "DELETE FROM employees..."));
        return svc;
    }
}