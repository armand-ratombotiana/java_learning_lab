# Kubernetes — Internals

## etcd (Consistent Key-Value Store)

- **Raft consensus**: 3-5 member cluster for HA
- **Storage**: WAL (write-ahead log) + snapshot
- **Keyspace**: /registry/{resource_type}/{namespace}/{name}
- **Watch**: Clients can subscribe to key changes (e.g., new pod → scheduler notified)
- **Performance**: ~10K writes/sec, ~100K reads/sec

```
/registry/deployments/default/java-app  →  {full deployment spec}
/registry/pods/default/java-app-abc123  →  {full pod status}
/registry/services/default/java-app-svc →  {full service spec}
```

## Scheduler Internals

### Scheduling Algorithm
```
1. Filtering (predicates):
   - Node has enough CPU/memory for pod's requests
   - Node matches nodeSelector/affinity
   - Node tolerates pod's taints
   - Node doesn't have conflicting ports

2. Scoring (priorities):
   - Most requested: prefer nodes with least free resources (bin-packing)
   - Least requested: spread pods across nodes
   - Pod affinity/anti-affinity
   - Node affinity

3. Binding:
   - Scheduler writes pod→node binding to API Server
   - kubelet picks up the binding
```

## kubelet

### Pod Lifecycle Manager
```
kubelet watches API Server for pods assigned to this node
  └── SyncLoop (every 1s):
      1. Create pod sandbox (CNI network namespace)
      2. Pull container images
      3. Start init containers (sequentially)
      4. Start regular containers (in parallel)
      5. Run liveness/readiness/startup probes
      6. Update pod status to API Server
      7. If pod deleted → graceful shutdown (SIGTERM, wait, SIGKILL)
```

## kube-proxy

### Traffic Routing
```
kube-proxy watches Services and Endpoints
  └── Updates iptables/IPVS rules:
      Service VIP (10.96.0.1:80) → random pod IP (10.0.0.2:8080)

Modes:
  - iptables (default): update iptables rules on service change
  - IPVS: Linux Virtual Server (more efficient for large clusters)
  - userspace: user-mode proxy (legacy, slow)
```

## Container Runtime Interface (CRI)

```
kubelet ──► CRI (gRPC) ──► containerd
                              │
                         runc / kata / gVisor
                              │
                         container processes

Before v1.24: Docker was a CRI shim (dockershim)
After v1.24: containerd is default CRI runtime
```

## CNI (Container Network Interface)

```
Network plugin (Calico, Flannel, Weave, AWS VPC CNI):
  1. Creates veth pair (host ↔ pod)
  2. Assigns pod IP from cluster CIDR
  3. Configures routing rules
  4. Enforces network policies (if applicable)

AWS VPC CNI: Each pod gets a VPC IP address (no NAT)
  - Pods reachable directly via VPC networking
  - Same security groups as EC2 instances
  - Max pods = ENI limits per instance type
```
