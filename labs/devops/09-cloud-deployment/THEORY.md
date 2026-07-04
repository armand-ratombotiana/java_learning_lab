# Cloud Deployment Theory

## Cloud Providers Overview
- **AWS**: EKS (K8s), ECS (containers), EC2 (VMs), RDS (databases), S3 (storage).
- **GCP**: GKE (K8s), Cloud Run (serverless), Cloud SQL, Cloud Storage.
- **Azure**: AKS (K8s), Azure Container Instances, Azure SQL, Blob Storage.

## Managed Kubernetes Services
- **EKS**: AWS-managed control plane; worker nodes in auto-scaling groups; IRSA for IAM.
- **GKE**: Google-managed control plane; node auto-repair; auto-scaling; Workload Identity.
- **AKS**: Azure-managed control plane; Virtual Nodes (ACI); managed AAD integration.

## Cloud-Native Patterns
- **12-Factor App**: Methodology for building cloud-native applications.
- **Stateless design**: Scale horizontally; externalize state to cloud services.
- **Service mesh**: Istio, App Mesh (AWS) for traffic management.
- **Serverless**: Fargate, Cloud Run, Azure Container Instances (no node management).
- **Immutable infrastructure**: AMIs, container images, infrastructure as code.

## Deployment Strategies
- **Blue/Green**: Two full environments; switch DNS/load balancer.
- **Canary**: Gradual traffic shifting; monitor metrics.
- **Rolling update**: Incremental pod replacement (default K8s).
