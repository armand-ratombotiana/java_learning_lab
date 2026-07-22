# Incident Response Runbook Checklist: Pod CrashLoop

## Incident ID: ____________________
## Date: ____________________
## Responder: ____________________

## Severity Classification

- [ ] **P0/SEV1**: >25% of pods crashlooping, revenue-impacting
- [ ] **P1/SEV2**: <25% of pods crashlooping, partial degradation
- [ ] **P2/SEV3**: Single pod crashlooping, non-critical

## Immediate Response (First 5 Minutes)

### Detection
- [ ] Confirm alert from Prometheus:
  ```bash
  kubectl get pods -n <namespace> | grep -E "CrashLoop|OOM|Error"
  ```
- [ ] Identify affected deployment and namespace
- [ ] Count affected vs healthy pods
- [ ] Identify when the crashlooping started
- [ ] Check recent deployments:
  ```bash
  kubectl rollout history deployment/<name> -n <namespace>
  ```

### Declaration
- [ ] Declare incident in PagerDuty (SEV1 if revenue-impacting)
- [ ] Create Slack channel: #inc-<incident-short-name>
- [ ] Post initial situation report:
  ```
  INC-XXXX | SEV1 | Pod CrashLoop
  Deployment: [name]
  Affected pods: [X]/[Y] in CrashLoopBackOff
  Started at: [time]
  Impact: [service degradation / complete outage]
  ```
- [ ] Notify SRE team lead

## Assessment (5-15 Minutes)

### Pod Diagnostics
- [ ] Describe affected pods:
  ```bash
  kubectl describe pod <pod-name> -n <namespace>
  ```
- [ ] Check termination reason (OOMKilled, Error, Probe Failure)
- [ ] Check previous pod logs:
  ```bash
  kubectl logs <pod-name> -n <namespace> --previous
  ```
- [ ] Check current pod logs (might show startup progress):
  ```bash
  kubectl logs <pod-name> -n <namespace>
  ```

### Resource Analysis
- [ ] Check resource limits:
  ```bash
  kubectl describe pod <pod-name> -n <namespace> | grep -A 5 "Limits"
  ```
- [ ] Check actual resource usage:
  ```bash
  kubectl top pod <pod-name> -n <namespace>
  ```
- [ ] Check node resource pressure:
  ```bash
  kubectl describe node <node-name> | grep -A 10 "Resources"
  ```
- [ ] Check cAdvisor metrics for OOM events

### Probe Analysis
- [ ] Check liveness probe configuration:
  ```bash
  kubectl get pod <pod-name> -n <namespace> -o yaml | grep -A 15 livenessProbe
  ```
- [ ] Check startup probe configuration:
  ```bash
  kubectl get pod <pod-name> -n <namespace> -o yaml | grep -A 15 startupProbe
  ```
- [ ] Calculate: `initialDelaySeconds + (periodSeconds × failureThreshold)` vs actual startup time
- [ ] Check application startup logs for initialization duration

### Deployment Analysis
- [ ] Check recent changes:
  ```bash
  kubectl rollout history deployment/<name> -n <namespace>
  ```
- [ ] Compare current vs previous version:
  ```bash
  kubectl diff -f previous-deployment.yaml
  ```
- [ ] Check ArgoCD sync status:
  ```bash
  argocd app get <app-name>
  ```
- [ ] Review CI/CD pipeline output for recent commit

## Rapid Remediation (5-20 Minutes)

### Path A: Rollback (Preferred — Fastest Recovery)
- [ ] Rollback via ArgoCD:
  ```bash
  argocd app rollback <app-name> --prune
  ```
- [ ] Or via kubectl:
  ```bash
  kubectl rollout undo deployment/<name> -n <namespace>
  ```
- [ ] Verify rollback success:
  ```bash
  kubectl rollout status deployment/<name> -n <namespace>
  ```
- [ ] Confirm all pods Running:
  ```bash
  kubectl wait --for=condition=Ready pods -n <namespace> -l app=<app> --timeout=120s
  ```

### Path B: Resource Limit Adjustment (If OOMKilled)
- [ ] Edit deployment resource limits:
  ```bash
  kubectl edit deployment <name> -n <namespace>
  ```
- [ ] Increase memory limit by 100% initially
- [ ] Increase CPU limit if throttling detected
- [ ] Wait for new pods to roll out
- [ ] Monitor memory usage

### Path C: Probe Tuning (If Probe Failures)
- [ ] Edit deployment probe configuration
- [ ] Increase initialDelaySeconds (2x current value)
- [ ] Add/update startupProbe with 60s failure threshold
- [ ] Reduce liveness probe frequency (increase periodSeconds)
- [ ] Apply changes and monitor

### Path D: Image Pull Issues
- [ ] Check image pull policy:
  ```bash
  kubectl describe pod <pod-name> -n <namespace> | grep "Image"
  ```
- [ ] Verify image exists in registry
- [ ] Check registry credentials (imagePullSecrets)
- [ ] Test pull locally:
  ```bash
  docker pull <image>:<tag>
  ```

## Investigation (20-60 Minutes)

### Deep Dive — OOMKilled
- [ ] Review JVM heap dump (if available)
- [ ] Check Java thread dump for thread count
- [ ] Review recent code changes (thread pools, connection pools)
- [ ] Run memory profiler in staging
- [ ] Compare memory baseline vs current

### Deep Dive — Probe Failure
- [ ] Check application health endpoint:
  ```bash
  kubectl exec <pod-name> -n <namespace> -- curl -s http://localhost:8080/actuator/health
  ```
- [ ] Review application initialization logic
- [ ] Check database connection startup
- [ ] Check external service dependencies during startup

### Deep Dive — Application Error
- [ ] Review application logs from previous pod:
  ```bash
  kubectl logs <pod-name> -n <namespace> --previous --tail=100
  ```
- [ ] Check if error is related to configuration
- [ ] Check database migrations
- [ ] Check feature flags / toggles

## Recovery Verification

- [ ] All target pods Running and Ready:
  ```bash
  kubectl get pods -n <namespace> | grep <app>
  ```
- [ ] Health endpoint returns 200:
  ```bash
  kubectl exec <pod-name> -n <namespace> -- curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health
  ```
- [ ] Resource usage within limits:
  ```bash
  kubectl top pod -n <namespace> -l app=<app>
  ```
- [ ] No OOMKilled events:
  ```bash
  kubectl get events -n <namespace> --field-selector reason=OOMKilling
  ```
- [ ] Monitoring alerts resolved
- [ ] Downstream services recovered
- [ ] Customer-facing functionality restored

## Post-Incident Actions

### Documentation
- [ ] Update incident report with timeline
- [ ] Document root cause using 5 Whys
- [ ] Update runbook with any new findings
- [ ] Add new monitoring checks to master checklist
- [ ] Update deployment template with probe standards

### Code/Configuration Fixes
- [ ] Add/update startup probe in deployment YAML
- [ ] Add resource profiling to CI/CD pipeline
- [ ] Fix resource limits based on profiling data
- [ ] Add CPU throttling alerts
- [ ] Configure canary deployment in ArgoCD

### Process Improvements
- [ ] Review change management for deployment config changes
- [ ] Update deployment review checklist
- [ ] Create resource profiling standard
- [ ] Update probe configuration guidelines
- [ ] Schedule load testing for thread pool changes

## Long-Term Preventive Actions

### Architecture
- [ ] Implement HPA with proper min/max replicas
- [ ] Implement PDB (PodDisruptionBudget)
- [ ] Add cluster-autoscaler for node scaling
- [ ] Implement vertical pod autoscaler for recommendation
- [ ] Use limit ranges for namespace default limits

### Monitoring
- [ ] Add synthetic monitoring for deployment rollouts
- [ ] Implement automated rollback on crashloop detection
- [ ] Add JVM metrics (heap, threads, GC) to monitoring
- [ ] Create deployment health dashboard
- [ ] Implement deployment canary analysis

## Escalation Contacts

| Role | Name | Phone | Email |
|------|------|-------|-------|
| SRE Lead | | | |
| App Team Lead | | | |
| DB Admin | | | |
| CI/CD Lead | | | |
| VP Engineering | | | |
| Cloud Provider Support | | | |

## Lessons Learned

### What went well:
- _________________________________________________________________

### What went wrong:
- _________________________________________________________________

### What we will improve:
- _________________________________________________________________

## Sign-Off

- [ ] Incident report completed: ____________________
- [ ] Root cause analysis completed: ____________________
- [ ] All action items assigned: ____________________
- [ ] Post-mortem scheduled: ____________________
- [ ] Final approval by SRE Director: ____________________

---

*This checklist references Google Kubernetes Engine documentation, Kubernetes SIG recommendations, and Netlfix Tech Blog on container resource tuning.*
