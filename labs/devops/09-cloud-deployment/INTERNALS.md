# Cloud Deployment Internals

## AWS EKS Architecture
- **Control plane**: AWS-managed, multi-AZ, auto-scales, API server + etcd.
- **Worker nodes**: EC2 instances in auto-scaling groups or Fargate (serverless).
- **Networking**: VPC CNI plugin assigns VPC IPs to pods (native VPC networking).
- **IAM**: IRSA (IAM Roles for Service Accounts) via OIDC provider.
- **Storage**: EBS CSI driver, EFS CSI driver (NFS), FSx for Lustre.

## GKE Architecture
- **Control plane**: Google-managed (Kubernetes on GCE).
- **Node auto-scaling**: Cluster Autoscaler + Node Auto-Repair + Auto-Upgrade.
- **Networking**: VPC-native (alias IP ranges) or routes-based.
- **Identity**: Workload Identity (GCP IAM for K8s service accounts).
- **Storage**: Compute Engine persistent disks, Filestore, Cloud Storage FUSE.

## AKS Architecture
- **Control plane**: Azure-managed, free (no control plane cost).
- **Node pools**: Linux and Windows node pools, Virtual Nodes (ACI).
- **Networking**: kubenet (default) or Azure CNI (advanced).
- **Identity**: Azure AD integration (managed AAD).
- **Storage**: Azure Disk, Azure Files (SMB), Azure NetApp Files.

## Shared Responsibility Model
```
┌────────────────────────────────────────────────┐
│            Customer Responsibility              │
│  - Application, data, configuration            │
│  - Network policies, RBAC, secrets             │
│  - Container images, CI/CD                     │
├────────────────────────────────────────────────┤
│        Shared Responsibility                   │
│  - Identity and access management              │
│  - Compliance, monitoring                      │
├────────────────────────────────────────────────┤
│         Cloud Provider Responsibility          │
│  - Physical infrastructure, facility security  │
│  - Compute, storage, networking hardware       │
│  - Managed service availability                │
└────────────────────────────────────────────────┘
```
