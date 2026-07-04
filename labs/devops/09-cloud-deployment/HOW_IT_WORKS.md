# How Cloud Deployment Works

## EKS Cluster Creation (AWS)
```
1. Create VPC with public/private subnets
2. Create EKS control plane (managed by AWS)
3. Create node group (EC2 auto-scaling group)
4. Configure kubectl to connect to EKS
5. Deploy workloads
6. Configure IAM roles for service accounts
```

## GKE Cluster Creation (GCP)
```
1. Enable GKE API
2. Create cluster (regional or zonal)
3. Configure Workload Identity
4. Connect with gcloud + kubectl
5. Deploy workloads
```

## AKS Cluster Creation (Azure)
```
1. Create resource group
2. Create AKS cluster (with managed AAD)
3. Get credentials (az aks get-credentials)
4. Deploy workloads
```

## Cloud CI/CD Flow
```
Code Commit → Build (CloudBuild/CodeBuild/GitHub Actions)
  → Push image to registry (ECR/GCR/ACR)
  → Deploy to K8s (EKS/GKE/AKS)
  → Health check
  → Route traffic (CloudFront/Cloud CDN/Traffic Manager)
```
