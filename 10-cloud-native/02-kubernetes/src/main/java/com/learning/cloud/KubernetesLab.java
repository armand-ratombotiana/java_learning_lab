package com.learning.cloud;

public class KubernetesLab {

    public static void main(String[] args) {
        System.out.println("=== Kubernetes Learning Lab ===\n");

        demonstrateK8sResources();
        demonstrateDeployments();
        demonstrateServices();
    }

    private static void demonstrateK8sResources() {
        System.out.println("--- Kubernetes Resources ---");
        System.out.println("Pod - Smallest deployable unit");
        System.out.println("Deployment - Manages ReplicaSets");
        System.out.println("Service - Network abstraction");
        System.out.println("ConfigMap - Configuration data");
        System.out.println("Secret - Sensitive data");
        System.out.println("Ingress - HTTP routing");
    }

    private static void demonstrateDeployments() {
        System.out.println("\n--- Deployment YAML ---");
        System.out.println("""
        apiVersion: apps/v1
        kind: Deployment
        metadata:
          name: my-app
        spec:
          replicas: 3
          selector:
            matchLabels:
              app: my-app
          template:
            metadata:
              labels:
                app: my-app
            spec:
              containers:
              - name: my-app
                image: myapp:latest
                ports:
                - containerPort: 8080
        """);
    }

    private static void demonstrateServices() {
        System.out.println("\n--- Service Types ---");
        System.out.println("ClusterIP - Internal cluster communication");
        System.out.println("NodePort - External via node port");
        System.out.println("LoadBalancer - External load balancer");
        System.out.println("ExternalName - DNS alias");
    }
}