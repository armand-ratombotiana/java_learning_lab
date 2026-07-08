# 13 — GCP Fundamentals — Theory

## Overview
Google Cloud Platform (GCP) offers a suite of cloud computing services running on Google's infrastructure. This lab covers Compute Engine, Cloud Storage, Cloud SQL, GKE, and GCP IAM.

## 1. Compute Engine

### Machine Types
- **General Purpose**: E2, N2, N2D for balanced workloads
- **Compute Optimized**: C2, C2D, C3 for CPU-intensive workloads
- **Memory Optimized**: M1, M2, M3 for memory-intensive workloads
- **Accelerator Optimized**: A2, G2 for GPU workloads
- **Shared Core**: E2-micro, E2-small, E2-medium for low-cost entry

### Predefined vs Custom Machine Types
- **Predefined**: Fixed vCPU/memory ratios (e.g., n2-standard-4 = 4 vCPU, 16 GB)
- **Custom**: Choose exact vCPU (1-96) and memory (0.5-6.5 GB per vCPU)

### Pricing Models
- **On-Demand**: Pay per second (minimum 1 minute)
- **Committed Use**: 1- or 3-year commitment for up to 57% discount
- **Preemptible/Spot**: Up to 91% discount, instances can be terminated within 30 seconds
- **Sole-tenant**: Dedicated physical servers for compliance

### Images and Boot Disks
- **Public Images**: Ubuntu, Debian, CentOS, RHEL, Windows Server, Container-Optimized OS
- **Custom Images**: User-created images with pre-installed software
- **Boot Disks**: Persistent Disk (PD Standard, PD Balanced, PD SSD)

## 2. Cloud Storage

### Storage Classes
- **Standard**: High-performance for frequently accessed data
- **Nearline**: Data accessed less than once per 30 days
- **Coldline**: Data accessed less than once per 90 days
- **Archive**: Data accessed less than once per 365 days

### Object Management
- **Buckets**: Global namespace with regional or multi-regional location
- **Objects**: Any file type, up to 5 TB per object
- **Versioning**: Keep multiple versions of objects
- **Lifecycle Management**: Automate transition and deletion
- **Object Change Notification**: Pub/Sub notifications on object changes

### Consistency Model
GCP Cloud Storage provides strong consistency for read-after-write, read-after-metadata-update, and read-after-delete operations.

### Encryption
- **Google-managed**: Default encryption at rest
- **Customer-managed**: Cloud KMS keys
- **Customer-supplied**: User-provided encryption keys
- **Client-side**: Encrypt before upload

## 3. Cloud SQL

### Database Engines
- **MySQL**: 5.7 and 8.0, compatible with MySQL workloads
- **PostgreSQL**: 13, 14, 15, compatible with PostgreSQL workloads
- **SQL Server**: 2017, 2019 Enterprise and Standard editions

### Features
- **Automated Backups**: Point-in-time recovery, 7-day retention (default)
- **High Availability**: Synchronous replication across zones
- **Read Replicas**: Cross-region and cross-zone replicas
- **Cloud SQL Proxy**: Secure access without authorized networks
- **Private IP**: Connect via VPC without public internet exposure

### Maintenance
Automatic maintenance windows (choose a day/time preference). Maintenance can cause a brief outage (HA minimizes this).

## 4. GKE (Google Kubernetes Engine)

### Cluster Types
- **Standard**: User-managed node pools, full cluster control
- **Autopilot**: Fully managed cluster (nodes, scaling, security)

### Node Configuration
- **Node Pools**: Groups of identical nodes (can have multiple pools)
- **Machine Types**: Any Compute Engine machine type
- **Autoscaling**: Node auto-provisioning and cluster autoscaler
- **Spot VMs**: Preemptible nodes for cost savings

### GKE Features
- **Workload Identity**: Grant GKE workloads access to GCP services
- **Config Connector**: Manage GCP resources via Kubernetes CRDs
- **Cloud Logging/Monitoring**: Integrated observability
- **GKE Gateway**: Next-gen ingress with L4/L7 routing
- **Binary Authorization**: Deploy only signed container images

## 5. GCP IAM

### Role Types
- **Basic Roles**: Owner, Editor, Viewer (broad permissions)
- **Predefined Roles**: Fine-grained roles for specific services (e.g., roles/storage.objectViewer)
- **Custom Roles**: User-defined with specific permissions

### Policy Structure
```json
{
  "bindings": [
    {
      "role": "roles/storage.objectViewer",
      "members": [
        "user:alice@example.com",
        "serviceAccount:my-sa@project.iam.gserviceaccount.com"
      ]
    }
  ]
}
```

### Service Accounts
- **Compute Engine Default**: Default service account for VMs
- **Custom Service Accounts**: Fine-grained permissions for specific workloads
- **Short-lived Credentials**: Generate temporary tokens (IAMCredentials API)

## Key Takeaways
1. Compute Engine offers flexible machine types with custom configurations
2. Cloud Storage provides consistent, tiered object storage with lifecycle management
3. Cloud SQL offers managed MySQL, PostgreSQL, and SQL Server
4. GKE provides managed Kubernetes with Autopilot and Standard modes
5. GCP IAM uses resource-based policies with roles and service accounts
