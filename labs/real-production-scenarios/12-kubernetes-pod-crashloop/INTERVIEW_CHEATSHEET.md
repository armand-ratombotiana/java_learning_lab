# Interview Cheatsheet: K8s Pod CrashLoop

## Key Diagnostic Commands
- `kubectl describe pod <name>` — events, conditions, container status
- `kubectl logs -p <pod>` — previous instance logs
- `kubectl logs -f <pod> --tail=50` — live logs
- `kubectl get events --sort-by=.lastTimestamp` — cluster events
- `kubectl exec -it <pod> -- /bin/sh` — shell into pod
- `kubectl get pods -o wide` — node assignment, IPs

## Common Metrics to Check
- Pod restart count
- CrashLoopBackOff count
- Container exit code (137=OOM, 139=SIGSEGV, 143=SIGTERM)
- Resource limits vs. usage (CPU, memory)
- Liveness/readiness probe failures
- Node resource pressure (disk, memory, PID)

## Typical Root Causes
- OOM kill (memory limit exceeded)
- Missing config/secret/volume mount
- Startup dependency not ready (database, service)
- Liveness probe too aggressive
- Application startup error (invalid config, port conflict)
- Resource limits too low
- Image pull error (auth, name, tag)

## Interview Question Patterns
- "How do you debug a CrashLoopBackOff?"
- "Design a pod with proper liveness and readiness probes"
- "How do you implement graceful shutdown in K8s?"
- "What's the difference between liveness and readiness probes?"

## STAR Story Template
**S**: All pods in CrashLoopBackOff after deployment — service down
**T**: Restore service and fix deployment
**A**: Found missing config map for new DB connection string, added ConfigMap, set init container to wait for dependencies
**R**: Service restored, added pre-flight validation in CI/CD pipeline
