# Step-by-Step — Deploy Spring Boot App to EKS

## Step 1: Create EKS Cluster
```bash
# Install eksctl
# Create cluster (takes ~15 min)
eksctl create cluster --name java-cluster --region us-east-1 \
  --nodegroup-name standard-workers --node-type m5.large \
  --nodes 3 --nodes-min 2 --nodes-max 6 --with-oidc

# Verify
kubectl get nodes
kubectl cluster-info
```

## Step 2: Containerize and Push Spring Boot App
```bash
# Build Docker image
docker build -t my-app:1.0.0 .

# Tag for ECR
aws ecr create-repository --repository-name my-app
docker tag my-app:1.0.0 123456789.dkr.ecr.us-east-1.amazonaws.com/my-app:1.0.0

# Push to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 123456789.dkr.ecr.us-east-1.amazonaws.com
docker push 123456789.dkr.ecr.us-east-1.amazonaws.com/my-app:1.0.0
```

## Step 3: Create Deployment
```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: spring-app
  template:
    metadata:
      labels:
        app: spring-app
    spec:
      containers:
      - name: app
        image: 123456789.dkr.ecr.us-east-1.amazonaws.com/my-app:1.0.0
        ports:
        - containerPort: 8080
        livenessProbe:
          httpGet: {path: /actuator/health/liveness, port: 8080}
          initialDelaySeconds: 60
        readinessProbe:
          httpGet: {path: /actuator/health/readiness, port: 8080}
          initialDelaySeconds: 30
        resources:
          requests: {memory: "256Mi", cpu: "250m"}
          limits: {memory: "512Mi", cpu: "500m"}
```

```bash
kubectl apply -f deployment.yaml
kubectl get pods -w
```

## Step 4: Create Service
```yaml
# service.yaml
apiVersion: v1
kind: Service
metadata:
  name: spring-app-svc
spec:
  selector:
    app: spring-app
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

```bash
kubectl apply -f service.yaml
kubectl get svc spring-app-svc -w  # Get EXTERNAL-IP
```

## Step 5: Create ConfigMap and Secret
```bash
# ConfigMap for non-sensitive config
kubectl create configmap app-config \
  --from-literal=APP_ENV=production \
  --from-literal=LOG_LEVEL=INFO

# Secret for sensitive data
kubectl create secret generic app-secrets \
  --from-literal=DB_PASSWORD=ChangeMe123! \
  --from-literal=API_KEY=abc123xyz
```

## Step 6: Mount Config in Deployment
```yaml
# Add to container spec in deployment.yaml
envFrom:
- configMapRef:
    name: app-config
- secretRef:
    name: app-secrets
```

## Step 7: Create Ingress
```yaml
# ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: spring-app-ingress
  annotations:
    kubernetes.io/ingress.class: alb
    alb.ingress.kubernetes.io/scheme: internet-facing
spec:
  rules:
  - host: app.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: spring-app-svc
            port:
              number: 80
```

## Step 8: Deploy Update
```bash
# Update image tag
kubectl set image deployment/spring-app app=my-app:1.0.1

# Watch rolling update
kubectl rollout status deployment/spring-app

# Rollback if needed
kubectl rollout undo deployment/spring-app
```

## Step 9: Clean Up
```bash
eksctl delete cluster --name java-cluster --region us-east-1
```
