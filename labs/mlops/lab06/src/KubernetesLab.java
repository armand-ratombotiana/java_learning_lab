package com.mlops.lab06;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Kubernetes for ML — Lab 06.
 * <p>
 * Demonstrates K8s deployment concepts for ML models by generating
 * Kubernetes manifest files (YAML) programmatically from Java.
 * Covers Deployments, Services, HPAs, and ConfigMaps.
 */
public class KubernetesLab {

    /** Generates a Kubernetes Deployment YAML for a model server. */
    static String generateDeployment(String name, String image, int replicas,
                                      String cpuRequest, String memRequest,
                                      String cpuLimit, String memLimit) {
        return String.format("""
                apiVersion: apps/v1
                kind: Deployment
                metadata:
                  name: %s
                  labels:
                    app: %s
                spec:
                  replicas: %d
                  strategy:
                    type: RollingUpdate
                    rollingUpdate:
                      maxSurge: 1
                      maxUnavailable: 0
                  selector:
                    matchLabels:
                      app: %s
                  template:
                    metadata:
                      labels:
                        app: %s
                    spec:
                      containers:
                      - name: model-server
                        image: %s
                        ports:
                        - containerPort: 8080
                        resources:
                          requests:
                            cpu: "%s"
                            memory: "%s"
                          limits:
                            cpu: "%s"
                            memory: "%s"
                        livenessProbe:
                          httpGet:
                            path: /healthz
                            port: 8080
                          initialDelaySeconds: 10
                          periodSeconds: 15
                        readinessProbe:
                          httpGet:
                            path: /readyz
                            port: 8080
                          initialDelaySeconds: 5
                          periodSeconds: 10
                """, name, name, replicas, name, name, image,
                cpuRequest, memRequest, cpuLimit, memLimit);
    }

    /** Generates a Kubernetes Service YAML. */
    static String generateService(String name, int port, int targetPort) {
        return String.format("""
                apiVersion: v1
                kind: Service
                metadata:
                  name: %s-service
                spec:
                  selector:
                    app: %s
                  ports:
                  - protocol: TCP
                    port: %d
                    targetPort: %d
                  type: ClusterIP
                """, name, name, port, targetPort);
    }

    /** Generates a HorizontalPodAutoscaler YAML. */
    static String generateHPA(String name, int minReplicas, int maxReplicas, int cpuPercent) {
        return String.format("""
                apiVersion: autoscaling/v2
                kind: HorizontalPodAutoscaler
                metadata:
                  name: %s-hpa
                spec:
                  scaleTargetRef:
                    apiVersion: apps/v1
                    kind: Deployment
                    name: %s
                  minReplicas: %d
                  maxReplicas: %d
                  metrics:
                  - type: Resource
                    resource:
                      name: cpu
                      target:
                        type: Utilization
                        averageUtilization: %d
                """, name, name, minReplicas, maxReplicas, cpuPercent);
    }

    /** Generates a ConfigMap for model configuration. */
    static String generateConfigMap(String name, Map<String, String> config) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("""
                apiVersion: v1
                kind: ConfigMap
                metadata:
                  name: %s-config
                data:
                """, name));
        for (Map.Entry<String, String> entry : config.entrySet()) {
            sb.append(String.format("  %s: "%s"%n", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        String modelName = "fraud-detector";

        System.out.println("=== Kubernetes for ML — Manifest Generation ===\n");

        // Generate deployment manifest
        String deployment = generateDeployment(
                modelName, "mlops-model-server:v1", 3,
                "256m", "512Mi", "1", "1Gi");
        System.out.println("--- Deployment ---");
        System.out.println(deployment);

        // Generate service manifest
        String service = generateService(modelName, 80, 8080);
        System.out.println("--- Service ---");
        System.out.println(service);

        // Generate HPA manifest
        String hpa = generateHPA(modelName, 2, 10, 60);
        System.out.println("--- HPA ---");
        System.out.println(hpa);

        // Generate ConfigMap
        Map<String, String> modelConfig = new LinkedHashMap<>();
        modelConfig.put("MODEL_THRESHOLD", "0.85");
        modelConfig.put("BATCH_SIZE", "32");
        modelConfig.put("CACHE_TTL_SECONDS", "3600");
        modelConfig.put("LOG_LEVEL", "INFO");
        String configMap = generateConfigMap(modelName, modelConfig);
        System.out.println("--- ConfigMap ---");
        System.out.println(configMap);

        // Write manifests to files
        Path outputDir = Paths.get("k8s-manifests");
        Files.createDirectories(outputDir);
        Files.writeString(outputDir.resolve("deployment.yaml"), deployment, StandardCharsets.UTF_8);
        Files.writeString(outputDir.resolve("service.yaml"), service, StandardCharsets.UTF_8);
        Files.writeString(outputDir.resolve("hpa.yaml"), hpa, StandardCharsets.UTF_8);
        Files.writeString(outputDir.resolve("configmap.yaml"), configMap, StandardCharsets.UTF_8);

        System.out.println("=== Manifests written to k8s-manifests/ ===");
        System.out.println("Deploy with: kubectl apply -f k8s-manifests/");
    }
}
