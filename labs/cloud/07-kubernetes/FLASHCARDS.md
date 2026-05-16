# Kubernetes - FLASHCARDS

## Core Concepts

### Card 1
**Q:** What is a Pod?
**A:** Smallest deployable unit; contains one or more containers sharing network/storage.

### Card 2
**Q:** What is the control plane?
**A:** Management components: kube-apiserver, etcd, scheduler, controller-manager.

### Card 3
**Q:** What does kubelet do?
**A:** Agent on each node that ensures containers are running as specified.

### Card 4
**Q:** What's the difference between Deployment and ReplicaSet?
**A:** Deployment manages ReplicaSets; ReplicaSet ensures pod count. Use Deployment.

## Workloads

### Card 5
**Q:** When use StatefulSet vs Deployment?
**A:** StatefulSet for stateful apps needing stable identity/persistent storage.

### Card 6
**Q:** What does DaemonSet ensure?
**A:** One pod runs on each node (or subset). Good for logging, monitoring agents.

### Card 7
**Q:** What is Job vs CronJob?
**A:** Job runs to completion; CronJob runs on schedule.

### Card 8
**Q:** How scale Deployment?
**A:** kubectl scale deployment name --replicas=N

## Services

### Card 9
**Q:** ClusterIP service type?
**A:** Internal-only; default. Pods can access via cluster DNS.

### Card 10
**Q:** NodePort service type?
**A:** Exposes on each node's IP at static port (30000-32767).

### Card 11
**Q:** LoadBalancer service type?
**A:** Provisions external load balancer (cloud provider).

### Card 12
**Q:** Headless Service?
**A:** No cluster IP; DNS returns pod IPs directly. For stateful apps.

## Configuration

### Card 13
**Q:** ConfigMap use case?
**A:** Non-sensitive configuration: app settings, environment variables, config files.

### Card 14
**Q:** Secret vs ConfigMap?
**A:** Secret is for sensitive data, base64 encoded (not encrypted by default).

### Card 15
**Q:** Inject ConfigMap as env?
**A:** envFrom.configMapRef.name=my-config

### Card 16
**Q:** Inject Secret as volume?
**A:** volumes.secret.secretName=my-secret; volumeMounts.mountPath=/secrets

## Networking

### Card 17
**Q:** What is Ingress?
**A:** HTTP/HTTPS route to services; provides load balancing, SSL termination.

### Card 18
**Q:** NetworkPolicy purpose?
**A:** Firewall rules for pod-to-pod traffic (ingress/egress).

### Card 19
**Q:** How pods communicate?
**A:** Via service DNS: service-name.namespace.svc.cluster.local

### Card 20
**Q:** What does kube-proxy do?
**A:** Maintains network rules for service traffic routing.

## Storage

### Card 21
**Q:** PV vs PVC?
**A:** PV is cluster resource; PVC is request for storage by pods.

### Card 22
**Q:** Access modes?
**A:** ReadWriteOnce (single node), ReadWriteMany (many nodes), ReadOnlyMany.

### Card 23
**Q:** StorageClass purpose?
**A:** Dynamic provisioning of PVs based on storage type (SSD, network, etc.).

### Card 24
**Q:** EmptyDir volume?
**A:** Ephemeral storage; lives as long as pod. Good for temp data, shared data.

## Scaling

### Card 25
**Q:** HorizontalPodAutoscaler metrics?
**A:** CPU utilization, memory utilization, custom metrics.

### Card 26
**Q:** RollingUpdate strategy settings?
**A:** maxSurge (extra pods), maxUnavailable (unavailable pods). Default 25%.

### Card 27
**Q:** Rollback command?
**A:** kubectl rollout undo deployment/name

### Card 28
**Q:** Deployment update strategy types?
**A:** RollingUpdate (default), Recreate.

## Security

### Card 29
**Q:** SecurityContext examples?
**A:** runAsNonRoot, runAsUser, readOnlyRootFilesystem, allowPrivilegeEscalation.

### Card 30
**Q:** PodDisruptionBudget purpose?
**A:** Ensures min pods available during voluntary disruptions (drains, updates).

### Card 31
**Q:** LimitRange purpose?
**A:** Set default/limits for compute resources per namespace.

### Card 32
**Q:** ResourceQuota purpose?
**A:** Limit total resource consumption per namespace.

## Observability

### Card 33
**Q:** Liveness probe purpose?
**A:** Is container alive? Restart if fails.

### Card 34
**Q:** Readiness probe purpose?
**A:** Can container serve traffic? Remove from service endpoints if fails.

### Card 35
**Q:** Startup probe purpose?
**A:** For slow-starting containers; delays other probes until app is ready.

## Commands

### Card 36
**Q:** List all pods?
**A:** kubectl get pods

### Card 37
**Q:** Describe a pod?
**A:** kubectl describe pod name

### Card 38
**Q:** Get pod logs?
**A:** kubectl logs -f pod-name

### Card 39
**Q:** Execute command in pod?
**A:** kubectl exec -it pod-name -- /bin/bash

### Card 40
**Q:** Apply config file?
**A:** kubectl apply -f manifest.yaml

---

**Total: 40 flashcards**