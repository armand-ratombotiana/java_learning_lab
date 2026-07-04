# Step-by-Step Cloud Deployment Guide

## 1. Set Up Cloud CLI
```powershell
# AWS
aws configure
# GCP
gcloud auth login && gcloud config set project <project-id>
# Azure
az login
```

## 2. Create Kubernetes Cluster
```powershell
# EKS
eksctl create cluster --name myapp --region us-east-1 --nodegroup-name workers --node-type t3.medium --nodes 3

# GKE
gcloud container clusters create myapp --num-nodes 3 --region us-central1

# AKS
az aks create --resource-group myapp --name myapp --node-count 3 --enable-managed-identity
```

## 3. Configure kubectl
```powershell
aws eks update-kubeconfig --region us-east-1 --name myapp
gcloud container clusters get-credentials myapp --region us-central1
az aks get-credentials --resource-group myapp --name myapp
```

## 4. Deploy Application
```powershell
kubectl create namespace production
kubectl apply -f k8s/ -n production
```

## 5. Set Up Managed Database
```powershell
# AWS RDS (via Terraform or console)
terraform apply -target=aws_db_instance.main

# Connect app
kubectl create secret generic db-url --from-literal=url=postgresql://user:pass@db-host:5432/mydb
```

## 6. Configure CI/CD
```powershell
# GitHub Actions workflow in .github/workflows/deploy.yml
# Build Docker, push to ECR/GCR/ACR, deploy to K8s
```

## 7. Set Up Monitoring
```powershell
# CloudWatch / Stackdriver / Azure Monitor
helm install aws-cloudwatch-exporter prometheus-community/prometheus-cloudwatch-exporter
```

## 8. Clean Up
```powershell
eksctl delete cluster --name myapp
gcloud container clusters delete myapp --region us-central1
az aks delete --resource-group myapp --name myapp
```
