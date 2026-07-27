# Container Orchestration MOCK_INTERVIEW.md

## Scenario 1: Orchestrator Selection
Your company is moving from VMs to containers. You need to choose an orchestrator.

**Questions**:
1. Compare Kubernetes, Docker Swarm, and Nomad.
2. When would you choose Nomad over Kubernetes?
3. What factors influence the choice?
4. How would you handle the migration from Swarm to K8s?

**Expected approach**: K8s for complex microservices, ecosystem, auto-scaling. Nomad for simpler ops, batch jobs, multi-platform. Swarm for small Docker-only teams. Migration: parallel run, traffic splitting, gradual migration per service. Factors: team skills, scale, complexity, ecosystem needs.

## Scenario 2: Resource Management
Your cluster has nodes with varying resources (some with GPUs, some with SSDs).

**Questions**:
1. How do you schedule GPU workloads?
2. How do you use node labels, taints, and tolerations?
3. How do you set resource requests and limits?
4. What's Quality of Service (QoS) in Kubernetes?

**Expected approach**: GPU nodes tainted with `nvidia.com/gpu:NoSchedule`, tolerations on GPU pods. Resource requests for baseline, limits for burst. QoS: Guaranteed (requests = limits), Burstable (requests < limits), BestEffort (no requests/limits). Use Extended Resources for custom resources.

## Scenario 3: Cluster Networking
Pods across nodes can't communicate. Services aren't resolving DNS.

**Questions**:
1. How does Kubernetes cluster networking work?
2. What CNI plugins would you consider?
3. How would you debug pod-to-pod communication issues?
4. How does kube-proxy work?

**Expected approach**: CNI allocates IPs, sets up routing. Calico (NetworkPolicy), Cilium (eBPF, L7), Flannel (simple). Debug: check CNI pods, CNI config, `/etc/cni/net.d/`, kubelet logs. kube-proxy modes: IPTables, IPVS (better at scale), userspace (legacy).

## Scenario 4: Cluster Upgrades
You need to upgrade Kubernetes from 1.27 to 1.29 with zero downtime.

**Questions**:
1. What's the upgrade process?
2. How do you upgrade worker nodes?
3. How do you handle API deprecations between versions?
4. What's the rollback plan if upgrade fails?

**Expected approach**: Upgrade control plane first (one node at a time), then worker nodes. Use `kubeadm upgrade` or managed K8s (EKS, AKS, GKE). Worker nodes: cordon, drain, upgrade, uncordon. Check API deprecations via `kubectl convert`. Rollback: restore etcd from backup, reinstall previous version.

## Scenario 5: Disaster Recovery
A regional cloud provider outage takes down your cluster.

**Questions**:
1. Design a multi-region/multi-cluster architecture.
2. How do you handle etcd backups and restoration?
3. What's the RTO and RPO for your design?
4. How do you test disaster recovery?

**Expected approach**: Multi-region active-passive or active-active with global load balancer. etcd backup: snapshot every hour, store in object store. Restore: create new cluster, restore etcd from snapshot. Test: kill a region, verify failover, restore. Chaos experiments for DR validation.

## Key Orchestration Interview Questions
1. Explain the difference between orchestration and scheduling.
2. How does Kubernetes compare to a traditional data center orchestration?
3. Explain bin packing vs spread scheduling strategies.
4. How does Kubernetes handle node failures?
5. What's the pod lifecycle? (Pending → ContainerCreating → Running → Succeeded/Failed)
6. Explain cluster federation (KubeFed).
7. How do you manage multi-tenancy in a cluster?
8. What are operator patterns? Give examples.
9. Explain the Kubernetes control loop.
10. How does resource quota work?

## Whiteboard Challenge
Design a multi-cluster, multi-region container orchestration platform for a global SaaS application with 100+ microservices. Consider networking, storage, CI/CD, security, and disaster recovery.

## Follow-up
1. How would you handle cluster capacity planning?
2. How would you implement cost allocation across teams?
3. How would you handle cluster sprawl?