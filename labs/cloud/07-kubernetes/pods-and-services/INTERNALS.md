# Kubernetes Services Internals

## 🔍 How Services Find Pods: Labels and Selectors
A Service does not keep a hardcoded list of Pod IPs. It uses **Labels and Selectors**.
When you deploy a Pod, you attach metadata to it (e.g., `app: backend`).
When you create a Service, you define a Selector (e.g., "Find all Pods with `app: backend`").

Kubernetes continuously monitors the cluster. Whenever a Pod with that label is created or destroyed, K8s automatically updates an internal object called an **EndpointSlice**, which contains the current, real-time list of IP addresses for those specific Pods.

## ⚙️ Kube-Proxy and iptables
How does the traffic actually get routed from the Service's virtual IP to the physical Pod's IP?

Every Worker Node in the cluster runs a daemon called `kube-proxy`.
1. `kube-proxy` talks to the API Server and watches for changes to Services and Endpoints.
2. When a Service is created, it is assigned a virtual IP (ClusterIP). This IP doesn't actually exist on any network interface. It is purely an abstraction.
3. `kube-proxy` writes rules into the Linux kernel's **iptables** (or IPVS) on that specific node.
4. The rule says: "If any traffic hits this node destined for the virtual Service IP, intercept it, pick a random Pod IP from the Endpoint list, and rewrite the destination IP (NAT) to that specific Pod."

This means the load balancing happens completely within the Linux kernel of the node where the request originated. It is incredibly fast and decentralized.

## 🚪 Types of Services

### 1. ClusterIP (The Default)
Provides an internal IP address. The Service is only reachable from *within* the cluster. Used for internal communication (e.g., Frontend talking to Database).

### 2. NodePort
Opens a specific port (between 30000-32767) on every single Worker Node in the cluster. If you hit the IP address of *any* node on that port, `iptables` will route you to the Service, and then to the Pod. Used primarily for local development or legacy architectures.

### 3. LoadBalancer
The standard way to expose an app to the public internet. It automatically provisions a cloud provider's external load balancer (e.g., AWS ALB, Google Cloud Load Balancer), assigns it a public IP, and routes traffic from that public IP into the cluster via NodePorts.