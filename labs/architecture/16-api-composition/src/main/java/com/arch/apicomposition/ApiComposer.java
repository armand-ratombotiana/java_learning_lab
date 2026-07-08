package com.arch.apicomposition;

import java.util.*;
import java.util.concurrent.*;

public class ApiComposer {
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Map<String, ServiceClient> services = new ConcurrentHashMap<>();

    public void registerService(String name, ServiceClient client) {
        services.put(name, client);
    }

    public ComposedResponse compose(String userId, List<String> serviceNames) {
        List<CompletableFuture<ServiceResponse>> futures = new ArrayList<>();
        for (String serviceName : serviceNames) {
            ServiceClient client = services.get(serviceName);
            if (client != null) {
                futures.add(CompletableFuture.supplyAsync(() -> {
                    try { return client.fetch(userId); }
                    catch (Exception e) { return new ServiceResponse(serviceName, null, e.getMessage()); }
                }, executor));
            }
        }
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            // timeout - collect partial results
        }
        ComposedResponse response = new ComposedResponse(userId);
        for (CompletableFuture<ServiceResponse> future : futures) {
            try {
                ServiceResponse sr = future.getNow(new ServiceResponse("unknown", null, "timeout"));
                response.addServiceResponse(sr);
            } catch (Exception e) {
                response.addServiceResponse(new ServiceResponse("unknown", null, e.getMessage()));
            }
        }
        return response;
    }

    public void shutdown() { executor.shutdown(); }
}

record ServiceResponse(String serviceName, Object data, String error) {}
record ComposedResponse(String userId) {
    private final List<ServiceResponse> responses = new ArrayList<>();
    public ComposedResponse(String userId) { this.userId = userId; }
    public void addServiceResponse(ServiceResponse sr) { responses.add(sr); }
    public List<ServiceResponse> getResponses() { return List.copyOf(responses); }
    public boolean hasErrors() { return responses.stream().anyMatch(r -> r.error() != null); }
}

interface ServiceClient {
    ServiceResponse fetch(String userId);
}
