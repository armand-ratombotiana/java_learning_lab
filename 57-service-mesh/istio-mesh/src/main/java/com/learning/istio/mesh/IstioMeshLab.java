package com.learning.istio.mesh;

public class IstioMeshLab {

    public static void main(String[] args) {
        System.out.println("=== Istio Service Mesh Lab ===\n");

        System.out.println("1. Traffic Splitting:");
        System.out.println("   apiVersion: networking.istio.io/v1alpha3");
        System.out.println("   kind: VirtualService");
        System.out.println("   spec:");
        System.out.println("     http:");
        System.out.println("     - route:");
        System.out.println("       - destination: host: backend, subset: v1, weight: 80");
        System.out.println("       - destination: host: backend, subset: v2, weight: 20");

        System.out.println("\n2. Circuit Breaker:");
        System.out.println("   apiVersion: networking.istio.io/v1alpha3");
        System.out.println("   kind: DestinationRule");
        System.out.println("   spec:");
        System.out.println("     trafficPolicy:");
        System.out.println("       connectionPool:");
        System.out.println("         tcp: maxConnections: 100");
        System.out.println("         http: http1MaxPendingRequests: 100");

        System.out.println("\n=== Istio Service Mesh Lab Complete ===");
    }
}