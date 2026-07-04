# Cloud Deployment Refactoring

## Before (On-Premises Style, Monolithic)
```yaml
# Single deployment with everything in one container
apiVersion: apps/v1
kind: Deployment
metadata:
  name: monolith
spec:
  template:
    spec:
      containers:
      - name: app
        image: myapp:latest
        env:
        - name: DB_HOST
          value: "10.0.1.50"  # Hardcoded IP of self-managed DB
        ports:
        - containerPort: 3000
```

## After (Cloud-Native)
```yaml
apiVersion: apps/v1
kind: Deployment
spec:
  template:
    spec:
      containers:
      - name: web
        image: myapp:1.2.3
        env:
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: rds-credentials
              key: url
---
apiVersion: v1
kind: Service
metadata:
  annotations:
    service.beta.kubernetes.io/aws-load-balancer-type: "alb"
    service.beta.kubernetes.io/aws-load-balancer-ssl-cert: "arn:aws:acm:..."
spec:
  type: LoadBalancer
---
# External: RDS managed database (created by Terraform)
# External: S3 for static assets
# External: ElastiCache for Redis
# External: SQS for async processing
```

## Gains
- Single cloud provider managed services replace self-managed infrastructure
- Auto-scaling and high availability built-in
- Secrets management via cloud-native tools
- Horizontal scaling with read replicas
- Cost optimization via managed service pricing
