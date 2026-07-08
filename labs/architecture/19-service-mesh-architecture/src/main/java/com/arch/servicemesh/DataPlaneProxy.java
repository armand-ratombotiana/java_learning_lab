package com.arch.servicemesh;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class DataPlaneProxy {
    private final ControlPlane controlPlane;
    private final Map<String, AtomicLong> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> errorCounts = new ConcurrentHashMap<>();

    public DataPlaneProxy(ControlPlane controlPlane) {
        this.controlPlane = controlPlane;
    }

    public ProxyResponse routeRequest(String sourceService, String targetService, String request) {
        requestCounts.computeIfAbsent(targetService, k -> new AtomicLong()).incrementAndGet();
        try {
            ServiceConfig config = controlPlane.getServiceConfig(targetService);
            List<String> routingRules = controlPlane.getRoutingRules(targetService);
            String selectedEndpoint = selectEndpoint(config.getEndpoints(), routingRules);
            String certificate = controlPlane.issueCertificate(sourceService);
            return new ProxyResponse(selectedEndpoint, "OK", certificate, 200);
        } catch (Exception e) {
            errorCounts.computeIfAbsent(targetService, k -> new AtomicLong()).incrementAndGet();
            return new ProxyResponse(null, e.getMessage(), null, 503);
        }
    }

    private String selectEndpoint(List<String> endpoints, List<String> rules) {
        if (endpoints.isEmpty()) throw new RuntimeException("No endpoints available");
        return endpoints.get(0);
    }

    public long getRequestCount(String service) { return requestCounts.getOrDefault(service, new AtomicLong()).get(); }
    public long getErrorCount(String service) { return errorCounts.getOrDefault(service, new AtomicLong()).get(); }
    public double getErrorRate(String service) {
        long total = getRequestCount(service);
        return total > 0 ? (double) getErrorCount(service) / total : 0.0;
    }
}

record ProxyResponse(String endpoint, String status, String certificate, int statusCode) {}
