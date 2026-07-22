# Incident Report: Kubernetes Pod CrashLoop

## Incident ID: INC-2026-0722-K8S
## Date: July 22, 2026
## Severity: SEV1 (Critical)

## Timeline (All times UTC)

### 10:23 — Deployment Triggered
- ArgoCD detects drift in `production` namespace for `order-processor` Deployment
- Automated sync policy triggers update to v3.2.1
- New ReplicaSet created with 50 replicas; old ReplicaSet scaled down
- The new image `gcr.io/acmecorp/order-processor:v3.2.1` has a JVM thread pool increase (200 → 500 threads) without corresponding heap increase

### 10:24 — First Pod Failures
- Pods begin entering CrashLoopBackOff state
- container status: `OOMKilled` (exit code 137)
- Prometheus alert fires: `KubePodCrashLooping`

### 10:25 — Monitoring Alert
- Alertmanager sends notification to Slack #ops-alerts:
  ```
  [FIRING] KubePodCrashLooping
  namespace: production, deployment: order-processor
  47/50 pods in CrashLoopBackOff
  ```
- On-call SRE acknowledges within 30 seconds

### 10:26 — Initial Diagnosis (SRE)
- SRE checks pod status:
  ```bash
  kubectl get pods -n production -l app=order-processor
  ```
- Output shows 47 pods with `CrashLoopBackOff`, 3 with `Running` (newly started)
- Checks previous pod logs:
  ```bash
  kubectl logs -n production order-processor-7d8f9c-abc12 --previous
  ```

### 10:28 — OOMKilled Identified
- Logs show no Java application error — only kernel OOM kill messages
- `kubectl describe pod` output shows:
  ```
  Last State: Terminated
    Reason: OOMKilled
    Exit Code: 137
  ```
- Container resource limits: memory=2Gi, cpu=2
- SRE suspects memory limit is too low

### 10:31 — Resource Usage Investigation
- SRE checks resource metrics:
  ```bash
  kubectl top pod -n production -l app=order-processor
  ```
- Remaining running pods show memory usage climbing to 2.0+ GB
- CPU usage at 180% of limit (throttling detected)

### 10:35 — Liveness Probe Investigation
- SRE checks liveness probe configuration:
  ```bash
  kubectl get pod order-processor-7d8f9c-abc12 -n production -o yaml | grep -A 10 livenessProbe
  ```
- Configuration:
  ```yaml
  livenessProbe:
    httpGet:
      path: /actuator/health
      port: 8080
    initialDelaySeconds: 5
    periodSeconds: 5
    timeoutSeconds: 3
    failureThreshold: 3
  ```
- JVM startup time: ~45 seconds (measured via startup logs)
- Pods killed at 20 seconds (5 + 3*5) before JVM responds to health checks

### 10:38 — Rollback Decision
- SRE decides to rollback to v3.2.0
- ArgoCD rollback initiated:
  ```bash
  argocd app rollback order-processor --prune
  ```

### 10:42 — Rollback Complete
- v3.2.0 pods start successfully (50/50 Running)
- Order processing rate returns to normal (1,200/min)
- OOMKilled pods no longer occurring

### 10:45 — Root Cause Analysis Begins
- Engineering team identifies JVM thread pool change as root cause
- Thread pool increased from 200 to 500 threads in PR #4189
- Each thread consumes ~2 MB of native memory + Java heap
- 500 threads × 2 MB = 1 GB native memory overhead
- Combined with 1.2 GB Java heap = 2.2 GB total > 2 GB limit

### 11:02 — Deeper Investigation: CPU Throttling
- Team checks CPU throttling metrics:
  ```bash
  # PromQL query
  sum(rate(container_cpu_cfs_throttled_seconds_total{namespace="production",pod=~"order-processor.*"}[5m]))
  ```
- Throttling was > 450% under load — contributing factor to slow startup
- cAdvisor metrics confirm CPU starvation during initialization

### 11:15 — Liveness Probe Analysis
- Team reviews liveness probe design
- JIT compilation + class loading + Spring context initialization = 45 seconds
- Probe timing: initialDelaySeconds=5 + periodSeconds=5 × failureThreshold=3 = 20 seconds
- 20 seconds << 45 seconds — all pods killed before JVM ready
- CrashLoopBackOff exponential backoff prevents recovery

### 11:30 — Code Review: Thread Pool Configuration
- Java application team reviews the change:
  - Old: `ThreadPoolTaskExecutor` with `maxPoolSize=200`
  - New: `ThreadPoolTaskExecutor` with `maxPoolSize=500`
  - No corresponding `-Xmx` or `-XX:MaxRAMPercentage` adjustment
  - No load testing with new thread pool size
  - No resource profiling in CI pipeline

### 12:01 — Fix Plan
- Three changes identified:
  1. Increase memory limit to 4 GB (profiling-based)
  2. Increase liveness probe initialDelaySeconds to 60
  3. Add startupProbe for slow-starting JVM

### 12:30 — Hotfix Release (v3.2.2)
- Application team releases v3.2.2 with reverted thread pool (200 threads)
- Resource requests/limits updated:
  ```yaml
  resources:
    requests:
      memory: "1Gi"
      cpu: "500m"
    limits:
      memory: "4Gi"
      cpu: "2"
  ```
- Probe configuration updated:
  ```yaml
  startupProbe:
    httpGet:
      path: /actuator/health
      port: 8080
    initialDelaySeconds: 5
    periodSeconds: 5
    failureThreshold: 12
  livenessProbe:
    httpGet:
      path: /actuator/health
      port: 8080
    initialDelaySeconds: 60
    periodSeconds: 15
    timeoutSeconds: 5
    failureThreshold: 3
  ```

### 12:45 — Canary Deployment
- v3.2.2 deployed with canary strategy (20%, then 50%, then 100%)
- ArgoCD rollback policy configured: `autoSync: false` for production
- Gradual rollout over 15 minutes

### 13:01 — Incident Resolved
- All 50 pods stable with v3.2.2
- Order processing at normal rate (1,200/min)
- No OOMKilled or CrashLoopBackOff events
- Monitoring confirmation: `KubePodCrashLooping` alert resolved
- Resource profiling pipeline added to CI/CD

## Detailed Resource Analysis

### JVM Memory Configuration

The JVM was started without container-aware memory flags:

```
# Incorrect JVM flags (before):
java -Xmx1200m -Xms1200m -jar order-processor.jar

# Correct JVM flags (after):
java -XX:+UseContainerSupport \
     -XX:MaxRAMPercentage=75.0 \
     -XX:InitialRAMPercentage=50.0 \
     -XX:MinRAMPercentage=25.0 \
     -jar order-processor.jar
```

The `-Xmx1200m` flag ignored the cgroup limit of 2 GB. With `-XX:+UseContainerSupport`, the JVM would automatically detect the cgroup memory limit and size the heap accordingly.

### Thread Pool Memory Impact

Each thread in the JVM consumes memory in multiple areas:

| Memory Area | Per-Thread Cost | 200 Threads | 500 Threads | Delta |
|------------|----------------|-------------|-------------|-------|
| Thread stack | 1 MB (default) | 200 MB | 500 MB | +300 MB |
| Thread-local allocation | 512 KB | 100 MB | 250 MB | +150 MB |
| Thread object overhead | 2 KB | 0.4 MB | 1 MB | +0.6 MB |
| GC thread references | ~1 KB | 0.2 MB | 0.5 MB | +0.3 MB |
| **Total native memory** | ~1.5 MB | **300.6 MB** | **751.5 MB** | **+450.9 MB** |

The 300 MB increase in thread overhead directly caused the OOM when combined with the 1.2 GB heap (total 1.95 GB vs 2 GB limit).

### Rollback Execution

The actual rollback commands and their timing:

```bash
# 10:38 — Rollback decision
kubectl rollout undo deployment/order-processor -n production

# 10:38 — Check rollout history
kubectl rollout history deployment/order-processor -n production

# 10:39 — Monitor rollback progress
kubectl rollout status deployment/order-processor -n production --watch

# 10:41 — New pods starting (v3.2.0)
kubectl get pods -n production -l app=order-processor

# 10:42 — All pods running
kubectl wait --for=condition=Ready pod -n production -l app=order-processor --timeout=120s

# 10:43 — Verify order processing resumes
kubectl logs -n production -l app=order-processor --tail=50 | grep "Processed order"
```

### Monitoring Metrics Captured

| Metric | Value at Peak | Normal | Recovery Time |
|--------|--------------|--------|---------------|
| Pod restart rate | 15/min | 0/min | 15 min post-rollback |
| Unavailable replicas | 50 | 0 | 15 min |
| OOMKilled events | 47 | 0 | Immediate post-rollback |
| CPU throttling | 450% | < 20% | 10 min |
| Memory usage ratio | 100% | 55% | Immediate |
| Order processing rate | 0/min | 1,200/min | 17 min |
| 99th percentile latency | N/A | 200ms | N/A (no traffic served) |
| Error rate | 100% | < 0.1% | 15 min |

### Canary Deployment Configuration (Post-Fix)

```yaml
# ArgoCD Rollout with canary strategy
apiVersion: argoproj.io/v1alpha1
kind: Rollout
metadata:
  name: order-processor
spec:
  strategy:
    canary:
      steps:
        - setWeight: 10
        - pause: {duration: 3m}
          analysis:
            templates:
              - templateName: success-rate
              - templateName: error-rate
              - templateName: memory-usage
        - setWeight: 25
        - pause: {duration: 3m}
          analysis:
            templates:
              - templateName: success-rate
              - templateName: memory-usage
        - setWeight: 50
        - pause: {duration: 5m}
        - setWeight: 75
        - pause: {duration: 5m}
        - setWeight: 100
```

### Analysis Templates for Canary

```yaml
apiVersion: argoproj.io/v1alpha1
kind: AnalysisTemplate
metadata:
  name: memory-usage
spec:
  metrics:
    - name: memory-usage
      successCondition: result < 80
      provider:
        prometheus:
          query: |
            max(container_memory_working_set_bytes{container="order-processor",namespace="production"})
            / max(kube_pod_container_resource_limits{resource="memory",container="order-processor",namespace="production"})
            * 100
---
apiVersion: argoproj.io/v1alpha1
kind: AnalysisTemplate
metadata:
  name: error-rate
spec:
  metrics:
    - name: error-rate
      successCondition: result < 1
      provider:
        prometheus:
          query: |
            sum(rate(http_requests_total{status=~"5..",namespace="production",app="order-processor"}[5m]))
            / sum(rate(http_requests_total{namespace="production",app="order-processor"}[5m]))
            * 100
```

## Post-Incident Actions

1. Implement resource profiling in CI pipeline (memory + CPU)
2. Add startup probe to all Java deployments
3. Review and update liveness/readiness probe guidelines
4. Configure automatic canary deployments in ArgoCD
5. Implement CPU throttling alerts (container_cpu_cfs_throttled_seconds_total)
6. Add JVM-specific resource recommendations to deployment template
7. Create resource limit validation in CI/CD pipeline
8. Schedule load testing for all services with >100% thread pool changes
9. Add OPA Gatekeeper policy to enforce startup probes
10. Implement PodDisruptionBudget for all production deployments
