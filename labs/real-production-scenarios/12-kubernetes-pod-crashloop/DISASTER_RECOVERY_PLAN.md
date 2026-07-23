# Lab 12 — Kubernetes Pod CrashLoop: Disaster Recovery Plan

## Recovery Objectives

| Metric | Target |
|--------|--------|
| RTO | 5 minutes (increase resources / rollback) |
| RPO | 0 (no data loss — pods are stateless) |
| MTD | 15 minutes before cascading failures |

## Scenarios

### Scenario A: OOM CrashLoop

**Trigger:** Exit code 137, pods exceed memory limit
**Recovery:**
1. `kubectl describe pod` — verify OOMKilled reason
2. Increase memory limit in deployment spec: `resources.limits.memory`
3. `kubectl apply -f deployment.yaml` — rolling update with new limits
4. Verify pods start and stabilize
5. Investigate root cause (memory leak or insufficient limit)

### Scenario B: Config Error CrashLoop

**Trigger:** Exit code 1 or 2, app fails on startup
**Recovery:**
1. `kubectl logs --previous` — check crash logs
2. `kubectl describe pod` — check events
3. Check ConfigMaps and Secrets are correctly mounted
4. Check environment variables in pod spec
5. Check startup command and arguments
6. Fix configuration and re-deploy

### Scenario C: Missing Dependency CrashLoop

**Trigger:** App crashes because database/cache/API is not available
**Recovery:**
1. Check dependency health (is DB up? is Redis up?)
2. If dependency is down: restore it first
3. If dependency is up but unreachable: check network policy, DNS, service endpoints
4. Add startupProbe with higher initialDelaySeconds to wait for dependencies

## Runbook

```yaml
symptoms:
  - "Pod status: CrashLoopBackOff"
  - "Restarts increasing rapidly"

diagnosis:
  - "kubectl logs <pod> --previous"
  - "kubectl describe pod <pod> — check exit code"
  - "Exit code 137 = OOM → increase memory"
  - "Exit code 1 = app error → check config/logs"
  - "Exit code 139 = segfault → check native code"

mitigation:
  - "OOM: increase memory limit"
  - "Config error: fix ConfigMap/Secret/environment"
  - "Dependency: restore downstream service"

fix:
  - "Fix the underlying issue (code/config)"
  - "Add proper resource requests/limits"
  - "Add startupProbe for slow-starting apps"
  - "Add proper liveness probe (not dependent on external services)"
```
