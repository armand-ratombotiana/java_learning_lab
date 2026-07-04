# Step-by-Step Advanced Orchestration Guide

## 1. Configure HPA
```powershell
# Create deployment with resource requests
kubectl apply -f deploy.yaml
# Create HPA
kubectl autoscale deployment myapp --cpu-percent=70 --min=2 --max=10
```

## 2. Generate Load (test HPA)
```powershell
kubectl run -it --rm load-generator --image=busybox -- sh
# In pod:
while true; do wget -q -O- http://myapp-svc; done
# Observe HPA in another terminal
kubectl get hpa -w
```

## 3. Rolling Update
```powershell
kubectl set image deployment/myapp app=myapp:v2
kubectl rollout status deployment/myapp
# Rollback if needed
kubectl rollout undo deployment/myapp
```

## 4. Add Probes
```powershell
# Edit deployment to add probes
kubectl edit deployment myapp
# Test probe failure
kubectl exec -it pod/myapp -- kill 1
kubectl get pods -w  # Watch restart
```

## 5. Create PDB
```powershell
kubectl create pdb app-pdb --selector=app=myapp --min-available=2
# Drain a node (test PDB enforcement)
kubectl drain <node> --ignore-daemonsets
```

## 6. Set Resource Quota
```powershell
kubectl create quota team-quota --hard=pods=10,cpu=4,memory=8Gi
```

## 7. Canary Deploy
```powershell
# Deploy v2 with 1 replica alongside v1
kubectl create deployment myapp-canary --image=myapp:v2 --replicas=1
# Traffic split handled by Service mesh or ingress
```
