# Cloud Deployment Debugging Guide

## AWS Debugging
```powershell
# Check EKS cluster
eksctl get cluster
aws eks describe-cluster --name myapp

# Check node groups
aws eks describe-nodegroup --cluster-name myapp --nodegroup-name workers

# Check CloudWatch logs
aws logs describe-log-groups --log-group-name-prefix /aws/eks/myapp

# Check IAM
aws sts get-caller-identity
```

## GCP Debugging
```powershell
# Check GKE cluster
gcloud container clusters describe myapp --region us-central1

# Check logs
gcloud logging read "resource.type=k8s_container AND resource.labels.cluster_name=myapp"

# Check IAM
gcloud auth list
```

## Azure Debugging
```powershell
# Check AKS
az aks show --resource-group myapp --name myapp

# Check logs
az aks browse --resource-group myapp --name myapp

# Check identity
az account show
```

## Common Issues
- **Kubectl connection failed**: Update kubeconfig, check IAM permissions.
- **Pod stuck Pending**: Check node group size, resource requests, PVC binding.
- **Load balancer not working**: Check security groups, target group health.
- **Database connection refused**: Check security group inbound rules, subnet routing.
- **CI/CD failure**: Check IAM roles, repository permissions, service account tokens.
