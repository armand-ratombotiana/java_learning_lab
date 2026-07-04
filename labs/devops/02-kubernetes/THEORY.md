# Kubernetes Theory

## Core Concepts
- **Cluster**: Set of nodes (masters + workers) running containerized applications.
- **Pod**: Smallest deployable unit — one or more containers sharing network/storage.
- **Deployment**: Declarative update for Pods and ReplicaSets (rolling updates, rollbacks).
- **Service**: Stable network endpoint to access a set of Pods (ClusterIP, NodePort, LoadBalancer).
- **ConfigMap**: Key-value config data injected into Pods as env vars or volumes.
- **Secret**: Similar to ConfigMap but base64-encoded, intended for sensitive data.
- **Ingress**: HTTP/HTTPS routing rules to Services (with optional TLS termination).

## Control Plane Components
- **kube-apiserver**: REST API frontend (all components communicate through it).
- **etcd**: Distributed key-value store (cluster state).
- **kube-scheduler**: Assigns Pods to Nodes based on resource requirements.
- **kube-controller-manager**: Runs controller processes (Deployment, ReplicaSet, etc.).
- **cloud-controller-manager**: Integrates with cloud provider APIs.

## Node Components
- **kubelet**: Agent that ensures containers are running in Pods.
- **kube-proxy**: Network proxy maintaining network rules on each node.
- **Container runtime**: containerd, CRI-O, or Docker (via cri-dockerd).
