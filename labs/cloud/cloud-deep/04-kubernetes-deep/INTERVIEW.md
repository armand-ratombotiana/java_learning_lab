# Interview Questions — Kubernetes Deep

## Beginner

Q: What are the phases of a pod lifecycle?
A: Pending, Running, Succeeded, Failed, CrashLoopBackOff, Unknown.

Q: What is the difference between a controller and an operator?
A: Controllers manage built-in resources; operators manage custom resources with domain-specific logic.

## Intermediate

Q: How does the Kubernetes scheduler work?
A: Filtering (predicates) to find feasible nodes, scoring (priorities) to rank them, binding to select the best node.

Q: What are taints and tolerations?
A: Taints repel pods from nodes; tolerations allow pods to tolerate specific taints.

## Advanced

Q: Design an operator that manages a distributed database cluster.
A: CRD defines cluster spec (replicas, storage, version), controller reconciles creating StatefulSet, Services, PVCs, handles scaling and failover.

Q: How do admission webhooks work and when would you use each type?
A: MutatingWebhook modifies resources before storage; ValidatingWebhook validates resources. Used for sidecar injection, policy enforcement, defaulting.
