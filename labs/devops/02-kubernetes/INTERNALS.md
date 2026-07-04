# Kubernetes Internals

## etcd
- Distributed, consistent key-value store based on Raft consensus protocol.
- Stores all cluster state (secrets, configmaps, pod specs, etc.).
- Should be backed up regularly; 3-5 node cluster recommended for production.

## API Server (kube-apiserver)
- RESTful API with watch support for real-time notifications.
- Authentication (TLS certs, tokens, OIDC) → Authorization (RBAC, ABAC) → Admission Controllers.
- All requests go through admission controllers (MutatingWebhook → ValidatingWebhook).

## Scheduler (kube-scheduler)
- **Filtering**: Find nodes that satisfy Pod resource requests, node selectors, affinity rules.
- **Scoring**: Rank remaining nodes based on priority functions (resource fit, locality, taints).
- **Binding**: Write binding decision to API server.

## Controller Manager
Each controller is a control loop:
```
Informer ← API Server (watch)
   ↓
Work Queue → Processing → API Server Update
```

## kubelet
- **PLEG (Pod Lifecycle Event Generator)**: Detects container state changes via container runtime.
- **CRI**: Pluggable interface to container runtimes (containerd, CRI-O).
- **cAdvisor**: Built-in container resource metrics collection.
