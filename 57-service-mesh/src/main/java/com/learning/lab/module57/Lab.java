package com.learning.lab.module57;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 57: Service Mesh Lab ===\n");

        System.out.println("1. Istio Architecture:");
        System.out.println("   - Control plane: istiod");
        System.out.println("   - Data plane: Envoy sidecar");
        System.out.println("   - mTLS between services");

        System.out.println("\n2. Traffic Management:");
        System.out.println("   - VirtualService: routing rules");
        System.out.println("   - DestinationRule: subsets, load balancing");
        System.out.println("   - Gateway: ingress control");
        System.out.println("   - ServiceEntry: external services");

        System.out.println("\n3. Sidecar Injection:");
        System.out.println("   - Automatic: istio-injection=enabled");
        System.out.println("   - Manual: istio-sidecar-injector");
        System.out.println("   - Native sidecar alternatives");

        System.out.println("\n4. Observability:");
        System.out.println("   - Kiali: service graph");
        System.out.println("   - Jaeger: distributed tracing");
        System.out.println("   - Prometheus: metrics");
        System.out.println("   - Grafana: dashboards");

        System.out.println("\n5. Security:");
        System.out.println("   - PeerAuthentication: mTLS strict/permissive");
        System.out.println("   - RequestAuthentication: JWT validation");
        System.out.println("   - AuthorizationPolicy: RBAC for services");

        System.out.println("\n6. Circuit Breaking:");
        System.out.println("   - DestinationRule connection pool");
        System.out.println("   - Outlier detection");
        System.out.println("   - Retry policies");

        System.out.println("\n7. Telemetry:");
        System.out.println("   - Request tracing");
        System.out.println("   - Metrics collection");
        System.out.println("   - Access logging");

        System.out.println("\n=== Service Mesh Lab Complete ===");
    }
}