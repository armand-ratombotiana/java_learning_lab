# How Kubernetes Works

## Control Loop Pattern
1. User submits desired state (YAML) via `kubectl apply -f`.
2. API server validates and stores in etcd.
3. Controllers watch API server for changes.
4. Each controller reconciles current state toward desired state.
5. Scheduler assigns pods to nodes based on resource requests, constraints, and affinities.

## Pod Scheduling
```
User creates Deployment → Deployment controller creates ReplicaSet
→ ReplicaSet controller creates Pod → Scheduler filters/ranks nodes
→ Scheduler binds Pod to Node → kubelet on Node runs containers
```

## Service Networking
- **kube-proxy** watches Services/Endpoints, updates iptables/IPVS rules.
- **CoreDNS** provides DNS-based service discovery (`svc.namespace.svc.cluster.local`).
- **Ingress controller** watches Ingress resources, configures reverse proxy (nginx, Traefik).

## Self-Healing
- **kubelet** reports Pod status to API server.
- If Pod crashes, kubelet restarts it (based on restartPolicy).
- If Node goes down, controller manager reschedules Pods after `pod-eviction-timeout`.
