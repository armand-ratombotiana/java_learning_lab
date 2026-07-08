package com.arch.servicemesh;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ControlPlane {
    private final Map<String, ServiceConfig> serviceConfigs = new ConcurrentHashMap<>();
    private final Map<String, List<String>> routingRules = new ConcurrentHashMap<>();
    private final CertificateManager certManager = new CertificateManager();

    public void registerService(String serviceName, String version, List<String> endpoints) {
        ServiceConfig config = new ServiceConfig(serviceName, version, endpoints);
        serviceConfigs.put(serviceName, config);
    }

    public void addRoutingRule(String serviceName, String rule) {
        routingRules.computeIfAbsent(serviceName, k -> new CopyOnWriteArrayList<>()).add(rule);
    }

    public ServiceConfig getServiceConfig(String serviceName) {
        ServiceConfig config = serviceConfigs.get(serviceName);
        if (config == null) throw new IllegalArgumentException("Unknown service: " + serviceName);
        return config;
    }

    public List<String> getRoutingRules(String serviceName) {
        return routingRules.getOrDefault(serviceName, List.of());
    }

    public String issueCertificate(String serviceName) {
        return certManager.issueCertificate(serviceName);
    }

    public boolean verifyCertificate(String serviceName, String certificate) {
        return certManager.verifyCertificate(serviceName, certificate);
    }

    public Map<String, ServiceConfig> getAllServices() { return Map.copyOf(serviceConfigs); }
}

class ServiceConfig {
    private final String name;
    private final String version;
    private final List<String> endpoints;

    public ServiceConfig(String name, String version, List<String> endpoints) {
        this.name = name; this.version = version; this.endpoints = endpoints;
    }
    public String getName() { return name; }
    public String getVersion() { return version; }
    public List<String> getEndpoints() { return List.copyOf(endpoints); }
}

class CertificateManager {
    private final Map<String, String> certificates = new ConcurrentHashMap<>();

    public String issueCertificate(String serviceName) {
        String cert = "cert_" + serviceName + "_" + System.currentTimeMillis();
        certificates.put(serviceName, cert);
        return cert;
    }

    public boolean verifyCertificate(String serviceName, String certificate) {
        return certificate != null && certificate.equals(certificates.get(serviceName));
    }
}
