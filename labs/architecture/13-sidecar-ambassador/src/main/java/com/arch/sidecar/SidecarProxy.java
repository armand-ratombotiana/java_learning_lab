package com.arch.sidecar;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SidecarProxy {
    private final Map<String, ServiceEndpoint> serviceCache = new ConcurrentHashMap<>();
    private final ServiceRegistry registry;
    private final MetricCollector metrics;

    public SidecarProxy(ServiceRegistry registry, MetricCollector metrics) {
        this.registry = registry;
        this.metrics = metrics;
    }

    public String forwardRequest(String serviceName, String request) {
        long start = System.nanoTime();
        try {
            ServiceEndpoint endpoint = discoverService(serviceName);
            String response = endpoint.call(request);
            metrics.recordSuccess(serviceName, System.nanoTime() - start);
            return response;
        } catch (Exception e) {
            metrics.recordFailure(serviceName);
            throw new ProxyException("Request to " + serviceName + " failed: " + e.getMessage());
        }
    }

    public boolean healthCheck(String serviceName) {
        try {
            ServiceEndpoint endpoint = discoverService(serviceName);
            return endpoint.isHealthy();
        } catch (Exception e) {
            return false;
        }
    }

    private ServiceEndpoint discoverService(String serviceName) {
        return serviceCache.computeIfAbsent(serviceName, registry::lookup);
    }

    public Map<String, Object> getMetrics() {
        return metrics.getSnapshot();
    }
}

class ServiceEndpoint {
    private final String host;
    private final int port;
    private final boolean tls;

    public ServiceEndpoint(String host, int port, boolean tls) {
        this.host = host;
        this.port = port;
        this.tls = tls;
    }

    public String call(String request) {
        return "Response from " + host + ":" + port + " for: " + request;
    }

    public boolean isHealthy() { return true; }

    public String getAddress() { return (tls ? "https://" : "http://") + host + ":" + port; }
}

class ServiceRegistry {
    public ServiceEndpoint lookup(String serviceName) {
        return switch (serviceName) {
            case "payment" -> new ServiceEndpoint("payment-svc", 8081, true);
            case "orders" -> new ServiceEndpoint("orders-svc", 8082, true);
            case "inventory" -> new ServiceEndpoint("inventory-svc", 8083, false);
            default -> throw new IllegalArgumentException("Unknown service: " + serviceName);
        };
    }
}

class MetricCollector {
    private final Map<String, long[]> metrics = new ConcurrentHashMap<>();

    public void recordSuccess(String service, long latency) {
        metrics.computeIfAbsent(service, k -> new long[2]);
        long[] data = metrics.get(service);
        data[0]++; data[1] += latency;
    }

    public void recordFailure(String service) {
        metrics.computeIfAbsent(service, k -> new long[2]);
        metrics.get(service);
    }

    public Map<String, Object> getSnapshot() {
        Map<String, Object> snapshot = new java.util.HashMap<>();
        metrics.forEach((svc, data) -> {
            snapshot.put(svc + "_requests", data[0]);
            if (data[0] > 0) {
                snapshot.put(svc + "_avg_latency", data[1] / data[0]);
            }
        });
        return snapshot;
    }
}

class ProxyException extends RuntimeException {
    public ProxyException(String message) { super(message); }
}
