# Root Cause Analysis: Kubernetes Pod CrashLoop

## RCA ID: RCA-2026-0722-001
## Severity: SEV1 — Critical Production Outage
## Duration: 2 hours 38 minutes
## Total Impact: ~$223,000 revenue loss + 850+ support tickets

## Executive Summary

On July 22, 2026, the order-processing service (v3.2.1) was deployed to the production GKE cluster via ArgoCD automated sync. Within 3 minutes, 47 of 50 pod replicas entered CrashLoopBackOff state due to OOMKilled by cgroup. The incident lasted 2 hours 38 minutes and resulted in $223,000 in lost revenue. The root cause was a JVM memory regression introduced by increasing the thread pool from 200 to 500 threads without corresponding heap limit adjustment, combined with an overly strict liveness probe that prevented successful pod startup.

## What Happened

The deployment v3.2.1 increased the `ThreadPoolTaskExecutor.maxPoolSize` from 200 to 500 threads. Each thread consumes ~2 MB of native memory (stack + kernel overhead), totaling ~1 GB additional native memory. Combined with the 1.2 GB Java heap, total memory consumption exceeded the container memory limit of 2 GB, causing cgroup OOM killer to terminate the pods. Additionally, the liveness probe had `initialDelaySeconds: 5` and `periodSeconds: 5`, which killed pods before the 45-second JVM warmup completed, preventing any pod from reaching a healthy state.

## Direct Cause

Container memory limit (2 GB) was exceeded by the combined Java heap (1.2 GB) and native memory overhead from 500 threads (~1 GB), resulting in OOMKilled (exit code 137).

## The 5 Whys Analysis

### Why 1: Why did pods enter CrashLoopBackOff?

Pods were killed by the Linux cgroup OOM killer because memory usage exceeded the container's memory limit (2 GB). After being killed, kubelet restarted the pod, but it was immediately killed again.

**Evidence**: `kubectl describe pod` output shows:
```
Last State: Terminated
  Reason: OOMKilled
  Exit Code: 137
  Restart Count: 15
```

Container memory limit was configured as:
```yaml
resources:
  limits:
    memory: "2Gi"
```

### Why 2: Why did memory usage exceed the 2 GB limit?

The service v3.2.1 increased the thread pool size from 200 to 500 threads. Each Java thread consumes memory:
- Thread stack: 1 MB (default for x86_64 Linux)
- Thread-local allocations: ~500 KB
- Kernel overhead: ~200 KB
- Per-thread GC overhead: ~300 KB

Total per-thread: ~2 MB. 500 threads × 2 MB = 1 GB native memory overhead. Combined with Java heap (-Xmx1200m) = 1.2 GB, total ≈ 2.2 GB > 2 GB limit.

**Evidence**: Git blame shows the change in `application.yml`:
```properties
# v3.2.0 (working)
spring.task.execution.pool.max-size=200

# v3.2.1 (broken)
spring.task.execution.pool.max-size=500
```

### Why 3: Why was the thread pool increased without corresponding memory adjustment?

The engineering team made the thread pool change to address a production incident (#INC-2026-0719) where order processing was bottlenecked on thread availability. The change was reviewed and approved but:
1. No memory impact assessment was performed
2. No load testing with 500 threads was conducted
3. The relationship between thread count and native memory was not documented
4. The CI pipeline did not include resource profiling (memory, CPU)

**Evidence**: PR #4189 review comments show no discussion of memory impact. The PR was approved based on functionality ("thread pool size increase resolves thread starvation") without resource consideration.

### Why 4: Why did the liveness probe prevent recovery?

The liveness probe configuration was:
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

Pod lifecycle:
- t=0: Pod starts, JVM begins initialization (load classes, JIT compile, init Spring context)
- t=5: First liveness probe sent to /actuator/health — JVM not ready, connection refused
- t=10: Second probe — still not ready
- t=15: Third probe — still not ready (failureThreshold=3 reached)
- t=20: Pod killed by kubelet, CrashLoopBackOff starts

JVM warmup takes ~45 seconds based on application startup logs. With initialDelaySeconds=5 and 3 failures × 5s period = 15 seconds, the probe kills pods at t+20 seconds — far short of the 45 seconds needed.

**Evidence**: Application startup log from a successful pod:
```
2026-07-22 10:42:30.123 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat initialized on port 8080 (http)
2026-07-22 10:42:48.456 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat started on port 8080 (http)
2026-07-22 10:43:15.789 [main] INFO  c.a.o.OrderProcessorApplication - Started in 45.678 seconds
```

### Why 5: Why was there no startup probe or canary deployment?

**Organizational Root Cause**: The team had no standard for:
1. **Startup probes**: The Kubernetes deployment template (used by all 47 microservices) did not include a `startupProbe`. The team was unaware of this Kubernetes feature (stable since v1.20).

2. **Probe configuration guidelines**: The team had no documented standards for `initialDelaySeconds`, `periodSeconds`, or `failureThreshold`. Each team set these values arbitrarily.

3. **Canary deployments**: ArgoCD was configured with `automated.syncPolicy: true` and `automated.prune: true`, meaning all changes were applied to all replicas simultaneously. No gradual rollout policy was configured.

4. **Resource profiling**: The CI pipeline ran unit and integration tests but did not run resource profiling (memory, CPU). Memory regressions were undetectable in CI.

**Systemic Root Cause**: The organization lacked:
1. Standardized probe configuration for JVM workloads
2. Startup probe knowledge and enforcement
3. Canary/rolling update policy for production deployments
4. Resource profiling in CI/CD pipeline
5. Resource limit validation against application requirements
6. Load testing requirements for significant configuration changes

## Contributing Factors

1. **Auto-deploy at peak hours**: ArgoCD was configured to sync automatically, with no deployment window restriction
2. **No resource quotas**: The namespace had no ResourceQuota or LimitRange to validate resource configurations
3. **No HPA limits**: Horizontal Pod Autoscaler could scale beyond what the cluster could support
4. **Missing cAdvisor alerts**: CPU throttling alerts were not configured despite the metric being available
5. **No JVM container awareness**: The JVM was not configured with `-XX:+UseContainerSupport` or `-XX:MaxRAMPercentage`

## Verification

The root cause was verified by:
1. `kubectl describe pod` showing OOMKilled reason and exit code 137
2. Git blame showing thread pool change in PR #4189
3. JVM startup logs showing 45-second initialization time
4. Probe timing: 5 + (3 × 5) = 20 seconds < 45 seconds
5. Absence of `startupProbe` in deployment YAML
6. ArgoCD configuration showing automated sync without canary
7. CI pipeline configuration showing no resource profiling step

## Detailed RCA Evidence

### 1. OOMKilled Evidence

The following `kubectl describe pod` output confirms OOMKilled as the termination reason:

```
$ kubectl describe pod order-processor-7d8f9c-abc12 -n production

Status: Failed
Reason: CrashLoopBackOff
Message: Back-off 2m40s restarting failed container=order-processor

Containers:
  order-processor:
    Container ID: docker://abc123...
    Image: gcr.io/acmecorp/order-processor:v3.2.1
    State: Waiting
      Reason: CrashLoopBackOff
    Last State: Terminated
      Reason: OOMKilled
      Exit Code: 137
      Restart Count: 15
    Limits:
      cpu: 2
      memory: 2Gi
    Requests:
      cpu: 500m
      memory: 1Gi
```

Exit code 137 (128 + 9 = SIGKILL) indicates the process was killed by the OOM killer. The memory limit of 2 GB was reached.

### 2. Memory Profiling Data

```
Container memory usage trajectory (from cAdvisor):

t=0s:   512 MB  (JVM starting, loading classes)
t=5s:   1,200 MB (heap allocated to -Xmx1200m)
t=10s:  1,500 MB (thread pool creating 500 threads)
t=15s:  1,800 MB (JIT compilation + Spring loading)
t=18s:  2,000 MB (all memory consumed)
t=19s:  2,048 MB (OOM — cgroup limit reached)

Memory breakdown at OOM:
  Java heap: 1,200 MB (59%)
  Thread stacks: 500 MB (24%)
  Metaspace: 180 MB (9%)
  Code cache: 48 MB (2%)
  Direct buffers: 32 MB (2%)
  Socket buffers: 16 MB (1%)
  JIT overhead: 72 MB (3%)
  Total: 2,048 MB
```

### 3. Liveness Probe Failure Analysis

The probe timing analysis shows the exact failure sequence:

```
Generation 1 (v3.2.1):
  Pod order-processor-7d8f9c-x1y2z:
    t=0: ContainerStarted
    t=5: Probe(HTTP GET /actuator/health) → connection refused
    t=10: Probe(HTTP GET /actuator/health) → connection refused  
    t=15: Probe(HTTP GET /actuator/health) → connection refused
    t=20: kubelet kills container (liveness probe failed)
    t=21: Container recreated (restart #1)
    
    t=21: ContainerStarted (CrashLoopBackOff - 10s backoff)
    ... cycle repeats ...

All 47 pods in the same state → complete outage.
3 pods still in initial startup phase (not yet killed) → 3/50 "Running" but not healthy.
```

### 4. Thread Pool Regression Analysis

The code change that caused the regression:

```
File: src/main/java/com/acmecorp/orderprocessor/config/ThreadPoolConfig.java

// PR #4189 — Change committed July 20, 2026
// Author: developer@acmecorp.com
// Reviewers: reviewer1, reviewer2
// Description: "Increase thread pool to resolve thread starvation in order processing"

- private int maxPoolSize = 200;
+ private int maxPoolSize = 500;  // 150% increase

// No corresponding change to:
// - -Xmx heap setting
// - Container memory limit
// - Load test configuration
// - Resource profiling
```

### 5. CI Pipeline Gap

The CI pipeline configuration confirmed no resource profiling:

```yaml
# .github/workflows/ci.yml (before fix)
jobs:
  build:
    steps:
      - uses: actions/checkout@v3
      - name: Build
        run: mvn package
      - name: Unit tests
        run: mvn test
      - name: Integration tests
        run: mvn verify
      # No resource profiling step!
```

### 6. Organizational Inertia

The probe configuration issue had been raised previously:

```
Ticket OPS-4123 (February 2026):
  Title: "Update liveness probe guidelines for Java services"
  Description: "JVM services take 30-45 seconds to start. 
    Our standard probe config (initialDelaySeconds: 5) is insufficient."
  Status: Closed — "Won't Fix"
  Reason: "Existing config has worked so far. Will revisit if issues arise."
```

### 7. Missing Admission Controller

The Kubernetes cluster had no OPA Gatekeeper or Kyverno policy to enforce probe configuration:

```
$ kubectl get constraints
No resources found in default namespace.

$ kubectl get validatingwebhookconfigurations
NAME                                     AGE
gke-exec-authn-webhook                   287d
# No admission controller for resource validation
```

No policy existed to:
- Require startupProbe for all deployments
- Validate minimum initialDelaySeconds
- Enforce resource limits matching JVM configuration
- Require canary strategy for production deployments

## Recommendations Matrix

| Priority | Recommendation | Effort | Impact | Owner | Timeline |
|----------|---------------|--------|--------|-------|----------|
| P0 | Add startupProbe to all JVM deployments | 2h | Critical | DevOps | Week 1 |
| P0 | Update liveness probe initialDelaySeconds | 1h | Critical | DevOps | Week 1 |
| P0 | Set -XX:+UseContainerSupport for all JVMs | 2h | Critical | App Team | Week 1 |
| P1 | Implement resource profiling in CI | 16h | High | DevOps | Week 2 |
| P1 | Configure canary deployments in ArgoCD | 8h | High | CI/CD Team | Week 2 |
| P1 | Add OPA Gatekeeper admission policies | 16h | High | Platform | Week 3 |
| P2 | CPU throttling alerts | 4h | Medium | Observability | Week 3 |
| P2 | Resource limit validation in pipeline | 8h | Medium | DevOps | Week 4 |
| P2 | JVM thread count best practices | 4h | Medium | App Team | Week 4 |
| P3 | Load testing for thread pool changes | 24h | Low | QA Team | Month 2 |
| P3 | PodDisruptionBudget for all deployments | 4h | Low | SRE | Month 2 |

## Recommendations Summary

1. **Immediate**: Add startup probes to all JVM-based deployments
2. **Immediate**: Review and update all liveness probe configurations
3. **Short-term**: Implement resource profiling in CI pipeline
4. **Short-term**: Configure canary deployments in ArgoCD
5. **Medium-term**: Create probe configuration standards for JVM workloads
6. **Medium-term**: Implement resource limit validation in deployment pipeline
7. **Long-term**: Adopt JVM container awareness flags (-XX:+UseContainerSupport)
8. **Long-term**: Implement load testing for all configuration changes
