# Kubernetes Performance

## Control Plane Performance
- **etcd**: Key factor — use SSD storage, keep cluster under 8GB database size, defrag periodically.
- **API Server**: Scales horizontally; cache size and request rate impact performance.
- **Scheduler**: Supports scheduler profiles and multiple schedulers for large clusters.

## Node-Level Optimization
- **Container runtime**: containerd generally outperforms dockerd.
- **CNI plugin**: Calico (eBPF) > Cilium > Flannel in performance; choose based on requirements.
- **kube-proxy mode**: IPVS > iptables for large clusters (better scale, lower latency).

## Pod-Level Tuning
- **Resource limits**: Set appropriate CPU/memory requests and limits.
- **HPA configuration**: Tune stabilization window, metrics, and scale-up/down behavior.
- **Pod anti-affinity**: Spread pods across nodes for HA.
- **Topology Spread Constraints**: Distribute pods based on zones/regions.

## Large Cluster Considerations (1000+ nodes)
- Use node pools/auto-scaling.
- Enable Event-Driven Autoscaling (KEDA).
- Use Namespace resource quotas to prevent noisy neighbors.
- Consider Cilium with eBPF for improved networking performance.
