# Cloud Deployment Code Deep Dive

## Terraform for EKS (AWS)
```hcl
module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 19.0"

  cluster_name    = "myapp-eks"
  cluster_version = "1.28"

  vpc_id     = module.vpc.vpc_id
  subnet_ids = module.vpc.private_subnets

  eks_managed_node_groups = {
    main = {
      desired_size = 3
      min_size     = 3
      max_size     = 10

      instance_types = ["t3.medium"]
      capacity_type  = "ON_DEMAND"
    }

    spot = {
      desired_size = 0
      min_size     = 0
      max_size     = 20

      instance_types = ["t3.large", "t3a.large"]
      capacity_type  = "SPOT"
    }
  }
}
```

## Helm Values for Cloud (Environment-Specific)
```yaml
# values-prod.yaml
replicaCount: 6

ingress:
  enabled: true
  className: alb
  annotations:
    alb.ingress.kubernetes.io/scheme: internet-facing
    alb.ingress.kubernetes.io/certificate-arn: arn:aws:acm:us-east-1:xxxx
    alb.ingress.kubernetes.io/ssl-redirect: "443"

resources:
  requests:
    cpu: 500m
    memory: 1Gi
  limits:
    cpu: 2
    memory: 4Gi

autoscaling:
  enabled: true
  minReplicas: 3
  maxReplicas: 20
  targetCPUUtilizationPercentage: 70
```

## Cloud-Native Application (12-Factor)
```yaml
# deployment.yaml (12-factor compliant)
apiVersion: apps/v1
kind: Deployment
spec:
  template:
    spec:
      containers:
      - name: app
        env:
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: url
        - name: STORAGE_BUCKET
          value: myapp-prod-assets
        readinessProbe:
          httpGet:
            path: /health
            port: 8080
        resources:
          requests:
            cpu: 200m
            memory: 256Mi
```
