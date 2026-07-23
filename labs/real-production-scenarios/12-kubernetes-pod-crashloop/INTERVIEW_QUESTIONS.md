# Lab 12 — Kubernetes Pod CrashLoop: Interview Questions

**Q1: What is a CrashLoopBackOff in Kubernetes?**

**Answer:** CrashLoopBackOff is a pod status indicating the container has started and crashed repeatedly. Kubernetes applies exponential backoff to restart attempts (10s, 20s, 40s, 80s, 160s, 300s max). The pod can never stabilize because the underlying issue (config error, resource limit, missing dependency) keeps causing the container to exit. Common causes: missing environment variable, invalid config, app fails during startup check, out of memory, missing volume mount, invalid command/args.

**Q2: How do you debug a pod in CrashLoopBackOff?**

**Answer:** 1) `kubectl describe pod <name>` — check Events for last crash reason. 2) `kubectl logs <name> --previous` — see logs from the crashed container (most important). 3) `kubectl get events` — cluster-level events. 4) Check container exit code: 0 = intentional stop, 1 = app error, 137 = OOM kill, 139 = segfault, 143 = SIGTERM. 5) Check resource limits — OOM kills show as exit code 137. 6) Check config maps and secrets — missing env vars cause app startup failure. 7) Check liveness/readiness probe configuration.

**Q3: What container exit codes are most common in CrashLoopBackOff?**

**Answer:** Exit code 0: container stopped intentionally (no work to do, batch job completed). Exit code 1: application error (uncaught exception, panic, startup failure). Exit code 137: SIGKILL (OOM kill — container exceeded memory limit). Exit code 139: SIGSEGV (segmentation fault — memory corruption, native code issue). Exit code 143: SIGTERM (graceful shutdown initiated, normal if pod is being terminated). Exit code 255: unknown error, often application-specific.

**Q4: How do liveness and readiness probes affect pod restarts?**

**Answer:** Liveness probe: if fails, Kubernetes restarts the container (can cause CrashLoop if probe is too strict or misconfigured). Readiness probe: if fails, pod is removed from Service endpoints but NOT restarted. Common issues: 1) Liveness probe timeout too short for slow-starting app. 2) Liveness probe depending on external service (if DB is slow, liveness fails → restart → more load on DB). 3) Readiness probe endpoint not implemented. 4) Probe path requires authentication. 5) Probe uses wrong port.

**Q5: Your pod is in CrashLoopBackOff with exit code 137. What does this mean and how do you fix it?**

**Answer:** Exit code 137 = SIGKILL = OOM (out of memory). The container exceeded its memory limit and was killed by the kernel OOM killer. Fix: 1) Check current memory limit (`kubectl describe pod`). 2) Check actual memory usage from metrics (if available before crash). 3) Increase memory limit in the deployment spec. 4) Alternatively: fix memory leak in the application (check heap usage, GC logs). 5) Add memory request to ensure pod gets enough memory from the node. 6) Monitor memory usage after fix.

**Q6: Design a Kubernetes pod with proper health checks and resource limits.**

**Answer:**
```yaml
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: app
    resources:
      requests:
        memory: "256Mi"
        cpu: "250m"
      limits:
        memory: "512Mi"
        cpu: "500m"
    livenessProbe:
      httpGet:
        path: /healthz
        port: 8080
      initialDelaySeconds: 15
      periodSeconds: 10
      timeoutSeconds: 3
    readinessProbe:
      httpGet:
        path: /readyz
        port: 8080
      initialDelaySeconds: 5
      periodSeconds: 5
      timeoutSeconds: 2
    startupProbe:
      httpGet:
        path: /startupz
        port: 8080
      initialDelaySeconds: 3
      periodSeconds: 2
      failureThreshold: 30
```
Startup probe prevents liveness probe from killing pod during slow startup (up to 60 seconds). Readiness probe removes from service until fully ready.

**Q7: What is the difference between pod restart policy and liveness probe?**

**Answer:** Restart policy (Always, OnFailure, Never) controls whether the kubelet restarts the container when it exits (regardless of reason). Liveness probe controls whether Kubernetes considers the container "healthy" and restarts it if it's running but unresponsive. Restart policy catches crashes; liveness probe catches hangs. Both can cause restarts, but for different reasons. Use Always for services (most common), OnFailure for batch jobs, Never for one-shot jobs.

**Q8: How do you debug an application that starts, serves requests for 5 minutes, then crashes?**

**Answer:** This pattern suggests a gradual resource exhaustion (memory leak, connection leak, file descriptor leak) or periodic task failure. Debug: 1) Check logs from the previous run: `kubectl logs --previous`. 2) Check if crash timing correlates with specific events (GC, batch processing, periodic DB query). 3) Enable GC logging, thread dump on OOM. 4) Check resource usage trends (memory growing, connection count increasing). 5) Check if the crash is OOM (exit 137) or application error (exit 1). 6) Simulate locally with the same input load.

**Q9: What metrics would you monitor for pod health?**

**Answer:** 1) Pod restart count (CrashLoop detection). 2) Container exit codes. 3) Restart interval (time between restarts — decreasing means worsening). 4) Resource usage vs. limits (memory, CPU). 5) Liveness/readiness probe success rate. 6) Pod phase transitions (Pending → Running → CrashLoopBackOff). 7) OOM kill count (exit code 137). 8) Node resource pressure (CPU, memory, disk, inodes, PID pressure).

**Q10: Your deployment rollout is stuck — the new pods keep crashing. How do you roll back?**

**Answer:** 1) `kubectl rollout undo deployment/<name>` — roll back to previous revision. 2) `kubectl rollout status deployment/<name>` — check rollout status. 3) `kubectl rollout history deployment/<name>` — see revision history. 4) `kubectl rollout undo deployment/<name> --to-revision=N` — roll back to specific revision. 5) If multiple bad rollouts, keep rolling back until a stable version is found. 6) After rollback, verify pods are healthy. 7) Debug the bad revision in a non-production environment.
