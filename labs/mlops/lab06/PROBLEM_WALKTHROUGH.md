# Problem Walkthrough: Kubernetes for ML

## Problem 1: Manifest Generation for the Fraud Model Platform — Company: Stripe
### Interview Scenario
"You're at Stripe. The fraud detection model from Lab 05 ships as `mlops-model-server:v1` and needs to go onto Kubernetes — 3 replicas, rolling updates, probes wired to `/healthz` and `/readyz`, an HPA that scales 2-10 replicas at 60% CPU, and a ConfigMap carrying the model threshold and batch settings. Your team keeps hand-editing YAML and the probes drift out of sync with the server's endpoints. Generate all four manifests programmatically from Java, exactly like the lab's `KubernetesLab`, and print them for review."

### The Problem
1. Generate a `Deployment` for `fraud-detector` with 3 replicas, `RollingUpdate` (maxSurge 1, maxUnavailable 0), and the lab's resource requests/limits.
2. Wire liveness and readiness probes to the Lab 05 endpoints with the lab's delays and periods.
3. Generate a `ClusterIP` Service on port 80 → targetPort 8080.
4. Generate an `autoscaling/v2` HPA with min 2 / max 10 replicas at 60% average CPU utilization.
5. Generate a ConfigMap with `MODEL_THRESHOLD=0.85`, `BATCH_SIZE=32`, `CACHE_TTL_SECONDS=3600`, `LOG_LEVEL=INFO`.
6. Print all manifests with headers so the output doubles as review documentation.

### Solution Walkthrough
- Step 1: Reuse the lab's `generateDeployment(name, image, replicas, cpuRequest, memRequest, cpuLimit, memLimit)` — the template includes strategy, probes, and resources in one place, keeping them consistent by construction.
- Step 2: Pass the production values: image `mlops-model-server:v1`, 3 replicas, requests `256m`/`512Mi`, limits `1`/`1Gi` — matching the guide's manifest.
- Step 3: Generate the Service with `generateService("fraud-detector", 80, 8080)` — port 80 in front of the container's 8080, `ClusterIP` type.
- Step 4: Generate the HPA with `generateHPA("fraud-detector", 2, 10, 60)` — the `averageUtilization` target that scales on CPU.
- Step 5: Build the ConfigMap with a `LinkedHashMap` so the four settings print in stable order, then `generateConfigMap` renders each as a quoted value.
- Step 6: Print all four manifests under `--- Deployment ---` / `--- Service ---` / `--- HPA ---` / `--- ConfigMap ---` headers and close with the deploy commands (`kubectl apply -f k8s-manifests/`).
- Step 7: Note that the lab additionally writes these to `k8s-manifests/` via `Files.writeString`; the walkthrough prints only, so it is side-effect free.

### Code
```java
package com.mlops.lab06;

import java.util.*;

public class KubernetesWalkthrough {

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
            sb.append(String.format("  %s: \"%s\"%n", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String modelName = "fraud-detector";

        System.out.println("=== Kubernetes for ML — Manifest Generation ===\n");

        String deployment = generateDeployment(
                modelName, "mlops-model-server:v1", 3,
                "256m", "512Mi", "1", "1Gi");
        System.out.println("--- Deployment ---");
        System.out.println(deployment);

        String service = generateService(modelName, 80, 8080);
        System.out.println("--- Service ---");
        System.out.println(service);

        String hpa = generateHPA(modelName, 2, 10, 60);
        System.out.println("--- HPA ---");
        System.out.println(hpa);

        Map<String, String> modelConfig = new LinkedHashMap<>();
        modelConfig.put("MODEL_THRESHOLD", "0.85");
        modelConfig.put("BATCH_SIZE", "32");
        modelConfig.put("CACHE_TTL_SECONDS", "3600");
        modelConfig.put("LOG_LEVEL", "INFO");
        String configMap = generateConfigMap(modelName, modelConfig);
        System.out.println("--- ConfigMap ---");
        System.out.println(configMap);

        System.out.println("=== Deploy with: kubectl apply -f k8s-manifests/ ===");
    }
}
```

### Expected Output
```
=== Kubernetes for ML — Manifest Generation ===

--- Deployment ---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: fraud-detector
  labels:
    app: fraud-detector
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: fraud-detector
  template:
    metadata:
      labels:
        app: fraud-detector
    spec:
      containers:
      - name: model-server
        image: mlops-model-server:v1
        ports:
        - containerPort: 8080
        resources:
          requests:
            cpu: "256m"
            memory: "512Mi"
          limits:
            cpu: "1"
            memory: "1Gi"
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

--- Service ---
apiVersion: v1
kind: Service
metadata:
  name: fraud-detector-service
spec:
  selector:
    app: fraud-detector
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: ClusterIP

--- HPA ---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: fraud-detector-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: fraud-detector
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 60

--- ConfigMap ---
apiVersion: v1
kind: ConfigMap
metadata:
  name: fraud-detector-config
data:
  MODEL_THRESHOLD: "0.85"
  BATCH_SIZE: "32"
  CACHE_TTL_SECONDS: "3600"
  LOG_LEVEL: "INFO"

=== Deploy with: kubectl apply -f k8s-manifests/ ===
```

---

## Problem 2: Rolling Update Sequence — Company: Netflix
### Interview Scenario
"You're at Netflix. The recommendation model v1 → v2 rollout must never drop below 3 ready replicas. Walk the rolling-update arithmetic with the lab's strategy values and prove the invariant."

### The Problem
1. Explain `maxSurge: 1` + `maxUnavailable: 0` against a 3-replica deployment.
2. Simulate the transition step by step and verify the ready-count invariant.
3. Print the pod ledger for each transition.

### Solution Walkthrough
- Step 1: Apply the strategy contract at every transition: total pods never exceed 4 (desired 3 + `maxSurge` 1) and ready pods never drop below 3 (`maxUnavailable` 0).
- Step 2: Encode the ledger: surge a new pod (total 4), wait for it to pass `/readyz`, then drain one old pod — ready count returns to 3.
- Step 3: Print the invariant check `ready >= 3` and `total <= 4` after every transition.

### Code
```java
int ready = 3, oldPods = 3, newPods = 0;
int step = 0;
System.out.printf("Step %d: total=%d ready=%d old=%d new=%d (invariant OK)%n",
        step, oldPods + newPods, ready, oldPods, newPods);
while (oldPods > 0) {
    newPods++;                       // maxSurge allows +1 above desired
    step++;
    System.out.printf("Step %d: surge new pod -> total=%d ready=%d%n",
            step, oldPods + newPods, ready);
    ready++;                         // new pod reports ready via /readyz
    oldPods--;                       // maxUnavailable=0 allows draining one old pod
    ready--;                         // drained pod leaves the ready set
    step++;
    System.out.printf("Step %d: new pod ready, old drained -> total=%d ready=%d %s%n",
            step, oldPods + newPods, ready,
            ready >= 3 && oldPods + newPods <= 4 ? "(invariant OK)" : "(INVARIANT BROKEN)");
}
```
### Expected Output
```
Step 0: total=3 ready=3 old=3 new=0 (invariant OK)
Step 1: surge new pod -> total=4 ready=3
Step 2: new pod ready, old drained -> total=3 ready=3 (invariant OK)
Step 3: surge new pod -> total=4 ready=3
Step 4: new pod ready, old drained -> total=3 ready=3 (invariant OK)
Step 5: surge new pod -> total=4 ready=3
Step 6: new pod ready, old drained -> total=3 ready=3 (invariant OK)
```

---

## Problem 3: HPA Replica Calculation — Company: Amazon
### Interview Scenario
"You're at Amazon. The serving HPA (min 2, max 10, target 60% CPU) reports 95% utilization on 4 replicas. Compute the desired replica count by hand and in code."

### The Problem
1. Apply the HPA formula: `desired = ceil(current × currentUtil / targetUtil)`.
2. Clamp to [min, max].
3. Print the result for the given numbers.

### Solution Walkthrough
- Step 1: `ceil(4 × 95 / 60) = ceil(6.33) = 7` replicas.
- Step 2: Clamp to [2, 10] — 7 stays in range, no clamping needed.
- Step 3: Print the arithmetic so the code matches the hand calculation.

### Code
```java
int currentReplicas = 4;
double utilization = 95.0;
int targetUtilization = 60;
int minReplicas = 2, maxReplicas = 10;

int desired = (int) Math.ceil(currentReplicas * utilization / targetUtilization);
int clamped = Math.max(minReplicas, Math.min(maxReplicas, desired));
System.out.printf("Desired: ceil(4 x 95 / 60) = %d%n", desired);
System.out.printf("Clamped to [%d, %d]: %d%n", minReplicas, maxReplicas, clamped);
```
### Expected Output
```
Desired: ceil(4 x 95 / 60) = 7
Clamped to [2, 10]: 7
```
