# 12 — Azure Fundamentals — Theory

## Overview
Microsoft Azure is a comprehensive cloud platform offering infrastructure, platform, and software as a service. This lab covers Azure VMs, Blob Storage, Azure SQL, AKS, and managed identities.

## 1. Azure Virtual Machines

### VM Types
- **General Purpose**: Balanced CPU/memory for dev/test (B-series, D-series)
- **Compute Optimized**: High CPU-to-memory ratio (F-series)
- **Memory Optimized**: High memory-to-CPU ratio (E-series, M-series)
- **Storage Optimized**: High disk throughput and IOPS (L-series)
- **GPU**: Machine learning and graphics workloads (N-series)

### Availability Options
- **Availability Set**: Logical grouping of VMs across fault/update domains
- **Availability Zone**: Physical separation across data centers
- **VMSS (Scale Sets)**: Auto-scaling group of identical VMs

### Disk Types
- **Ultra Disk**: Sub-millisecond latency, up to 300,000 IOPS
- **Premium SSD**: High-performance for production workloads
- **Standard SSD**: Cost-effective for dev/test
- **Standard HDD**: Lowest cost for backup/archival

### VM Extensions
Post-deployment configuration: Custom Script Extension, Docker extension, Monitoring agent, Security agent

## 2. Azure Blob Storage

### Storage Account Types
- **Standard GPv2**: General-purpose, supports all storage types
- **Premium Block Blobs**: Low-latency for high-throughput workloads
- **Premium Page Blobs**: VHDs for VMs
- **Premium File Shares**: SMB shares with high performance

### Blob Types
- **Block Blobs**: Text and binary data, up to 4.75 TB
- **Append Blobs**: Optimized for append operations (logging)
- **Page Blobs**: Random access up to 8 TB (VHDs)

### Access Tiers
- **Hot**: Frequent access, lowest storage cost
- **Cool**: Infrequent access (>30 days), lower availability cost
- **Cold**: Rare access (>90 days)
- **Archive**: Offline backup (>180 days), hours to retrieve

### Redundancy Options
- **LRS**: Locally redundant (3 copies in single data center)
- **ZRS**: Zone-redundant (3 copies across availability zones)
- **GRS**: Geo-redundant (LRS replicated to paired region)
- **RA-GRS**: Read-access geo-redundant (read from secondary region)

## 3. Azure SQL Database

### Deployment Models
- **Single Database**: Isolated database with dedicated resources
- **Elastic Pool**: Shared resources across multiple databases
- **Managed Instance**: Instance-scoped with near 100% SQL Server compatibility

### Service Tiers
- **General Purpose**: Balanced compute/storage, 5ms latency
- **Business Critical**: Low-latency, high-availability with in-memory OLTP
- **Hyperscale**: Up to 100 TB, fast scaling, instant backup

### Security Features
- **Azure AD Authentication**: Managed identity support
- **Always Encrypted**: Client-side encryption with column-level granularity
- **Advanced Threat Protection**: Anomaly detection and alerting
- **Auditing**: Track database events to Log Analytics

## 4. AKS (Azure Kubernetes Service)

### Core Components
- **Control Plane**: Managed Kubernetes API server, etcd (Azure-managed)
- **Node Pools**: Groups of identical VMs running your containers
- **Pod Networking**: Azure CNI or Kubenet for pod connectivity
- **Storage**: Azure Disk, Azure Files, Blob CSI drivers

### Features
- **Cluster Autoscaler**: Automatically resize node pools
- **Azure AD Integration**: Kubernetes RBAC with Azure AD identities
- **Pod Identity**: Assign Azure AD identities to pods
- **Azure Policy**: Enforce policies on AKS clusters
- **Container Insights**: Monitoring through Azure Monitor

### Networking Models
- **Kubenet**: Pods get IPs from a private address space (NAT for outbound)
- **Azure CNI**: Pods get VNet IPs (direct connectivity, more IPs needed)

## 5. Managed Identities

### Identity Types
- **System-assigned**: Tied to resource lifecycle, created with the resource
- **User-assigned**: Independent identity assigned to multiple resources

### Authentication Flow
```
Application -> Azure Instance Metadata Service -> Azure AD -> Access Token
                |
          Resource-specific endpoint (e.g., Key Vault, Storage, SQL)
```

### Use Cases
- Access Azure Key Vault from a VM without storing secrets
- Connect to Azure SQL Database from App Service
- Authenticate to Azure Storage from Azure Functions
- Pull images from ACR to AKS without credentials

## Key Takeaways
1. Azure VMs offer diverse VM types with availability sets and zones
2. Blob Storage provides tiered object storage with multiple redundancy options
3. Azure SQL offers managed relational databases with Hyperscale option
4. AKS provides managed Kubernetes with Azure AD integration
5. Managed Identities eliminate credential management for Azure resources
