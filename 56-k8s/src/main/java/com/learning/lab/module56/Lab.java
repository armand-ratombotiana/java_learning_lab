package com.learning.lab.module56;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 56: Kubernetes Lab ===\n");

        System.out.println("1. K8s Client (Fabric8):");
        System.out.println("   - DefaultKubernetesClient");
        System.out.println("   - programmatic resource management");
        System.out.println("   - Watch resources");

        System.out.println("\n2. Deployment Management:");
        System.out.println("   - createDeployment()");
        System.out.println("   - scaleDeployment()");
        System.out.println("   - rollBack()");
        System.out.println("   - Rolling updates");

        System.out.println("\n3. Service Operations:");
        System.out.println("   - createService()");
        System.out.println("   - Service type: ClusterIP, NodePort, LoadBalancer");
        System.out.println("   - Ingress management");

        System.out.println("\n4. ConfigMap and Secrets:");
        System.out.println("   - createConfigMap()");
        System.out.println("   - createSecret()");
        System.out.println("   - Volume mounts");

        System.out.println("\n5. Pod Management:");
        System.out.println("   - listPods()");
        System.out.println("   - podLogs()");
        System.out.println("   - execInPod()");

        System.out.println("\n6. Custom Resources:");
        System.out.println("   - CustomResourceDefinition");
        System.out.println("   - GenericClient for CRDs");
        System.out.println("   - Operator pattern");

        System.out.println("\n7. Java Operator SDK:");
        System.out.println("   - @KubernetesOperator");
        System.out.println("   - Reconciler pattern");
        System.out.println("   - Event source handling");

        System.out.println("\n=== Kubernetes Lab Complete ===");
    }
}