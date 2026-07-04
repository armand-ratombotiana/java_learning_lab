# Step-by-Step Kubernetes Guide

## 1. Verify Cluster
```powershell
kubectl cluster-info
kubectl get nodes
```

## 2. Create a Namespace
```powershell
kubectl create ns lab02
kubectl config set-context --current --namespace=lab02
```

## 3. Deploy an Application
```powershell
kubectl create deployment nginx --image=nginx:alpine --replicas=3
kubectl get deployments
kubectl get pods -w
```

## 4. Expose as Service
```powershell
kubectl expose deployment nginx --port=80 --type=ClusterIP --name=nginx-svc
kubectl get svc
kubectl run test --rm -it --image=busybox -- wget -qO- http://nginx-svc
```

## 5. Apply from YAML
```powershell
# Create deploy.yaml with Deployment definition
kubectl apply -f deploy.yaml
```

## 6. Rolling Update
```powershell
kubectl set image deployment/nginx nginx=nginx:1.26-alpine
kubectl rollout status deployment/nginx
kubectl rollout history deployment/nginx
```

## 7. ConfigMap and Secrets
```powershell
kubectl create configmap app-config --from-literal=env=prod
kubectl create secret generic db-creds --from-literal=password=secret123
kubectl get secrets db-creds -o yaml
```

## 8. Cleanup
```powershell
kubectl delete ns lab02
```
