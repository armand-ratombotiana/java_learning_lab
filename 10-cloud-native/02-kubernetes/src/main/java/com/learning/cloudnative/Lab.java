package com.learning.cloudnative;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Kubernetes Concepts ===\n");

        demonstrateArchitecture();
        demonstratePods();
        demonstrateWorkloads();
        demonstrateNetworking();
        demonstrateConfiguration();
        demonstrateStorage();
    }

    private static void demonstrateArchitecture() {
        System.out.println("--- Architecture ---");
        System.out.println("Control Plane (Master):");
        System.out.println("  kube-apiserver  -> REST API entry point");
        System.out.println("  etcd            -> Distributed key-value store (cluster state)");
        System.out.println("  kube-scheduler  -> Assigns pods to nodes");
        System.out.println("  kube-controller-manager -> Runs controllers (deployments, etc.)");
        System.out.println();
        System.out.println("Worker Nodes:");
        System.out.println("  kubelet     -> Node agent, manages pods");
        System.out.println("  kube-proxy  -> Network proxy (iptables/IPVS)");
        System.out.println("  Container Runtime -> Docker, containerd, CRI-O");
    }

    private static void demonstratePods() {
        System.out.println("\n--- Pods ---");
        System.out.println("Smallest deployable unit in K8s");
        System.out.println("Contains one or more containers sharing:");
        System.out.println("  - Network namespace (same IP, localhost communication)");
        System.out.println("  - Storage volumes");
        System.out.println("  - Lifecycle (started/stopped together)");
        System.out.println();
        System.out.println("Use multi-container pods for sidecar pattern:");
        System.out.println("  Main container (app) + Sidecar (logging, proxy, metrics)");
    }

    private static void demonstrateWorkloads() {
        System.out.println("\n--- Workload Resources ---");
        System.out.println("Deployment      -> Stateless apps, rolling updates, replicas");
        System.out.println("StatefulSet     -> Stateful apps, stable network identity, ordered");
        System.out.println("DaemonSet       -> Runs one pod per node (logging, monitoring)");
        System.out.println("Job/CronJob     -> Batch processing, scheduled tasks");
        System.out.println();
        System.out.println("Deployment strategy: RollingUpdate (default), Recreate");
        System.out.println("  maxSurge=25%, maxUnavailable=25% for zero-downtime deploys");
    }

    private static void demonstrateNetworking() {
        System.out.println("\n--- Networking ---");
        System.out.println("Service types:");
        System.out.println("  ClusterIP   -> Internal cluster IP (default)");
        System.out.println("  NodePort    -> Expose on each node's IP:port");
        System.out.println("  LoadBalancer-> Cloud provider load balancer");
        System.out.println("  ExternalName-> DNS alias");
        System.out.println();
        System.out.println("Ingress -> HTTP/HTTPS routing (path/host-based)");
        System.out.println("NetworkPolicy -> Firewall rules between pods");
        System.out.println("CNI plugins: Calico, Cilium, Flannel, Weave");
    }

    private static void demonstrateConfiguration() {
        System.out.println("\n--- Configuration & Secrets ---");
        System.out.println("ConfigMap  -> Non-sensitive config (env vars, files)");
        System.out.println("Secret     -> Sensitive data (base64 encoded, encrypted at rest)");
        System.out.println();
        System.out.println("Mounting options:");
        System.out.println("  As environment variables (ENV)");
        System.out.println("  As volume-mounted files (/etc/config/app.properties)");
        System.out.println();
        System.out.println("Resource limits: requests (guaranteed) + limits (burst cap)");
        System.out.println("  HPA (Horizontal Pod Autoscaler) based on CPU/memory/custom metrics");
    }

    private static void demonstrateStorage() {
        System.out.println("\n--- Storage ---");
        System.out.println("PersistentVolume (PV)       -> Storage provisioned in cluster");
        System.out.println("PersistentVolumeClaim (PVC) -> Request for storage by pod");
        System.out.println("StorageClass               -> Dynamic provisioning (e.g., SSD, HDD)");
        System.out.println();
        System.out.println("Access modes: ReadWriteOnce, ReadOnlyMany, ReadWriteMany");
        System.out.println("Storage drivers: EBS, EFS, GCE PD, Azure Disk, NFS, hostPath");
    }
}
