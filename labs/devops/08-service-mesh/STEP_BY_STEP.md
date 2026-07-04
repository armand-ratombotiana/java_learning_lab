# Step-by-Step Service Mesh Guide

## 1. Download and Install Istio
```powershell
# Download Istio
curl -L https://istio.io/downloadIstio | sh -
cd istio-*
Add-Content $PROFILE "`n`$env:Path += ';$pwd\bin'"
```

## 2. Install Istio on Cluster
```powershell
istioctl install --set profile=demo -y
kubectl label namespace default istio-injection=enabled
```

## 3. Deploy Sample Application
```powershell
kubectl apply -f samples/bookinfo/platform/kube/bookinfo.yaml
kubectl get pods -w
```

## 4. Check Sidecar Injection
```powershell
kubectl describe pod <pod-name>
# Should show 2 containers (app + istio-proxy)
```

## 5. Expose Service with Gateway
```powershell
kubectl apply -f samples/bookinfo/networking/bookinfo-gateway.yaml
# Get ingress gateway URL
kubectl get svc istio-ingressgateway -n istio-system
```

## 6. Traffic Routing (Canary)
```powershell
kubectl apply -f samples/bookinfo/networking/virtual-service-all-v1.yaml
# Route 50% to v3
kubectl apply -f samples/bookinfo/networking/virtual-service-reviews-50-v3.yaml
```

## 7. Enable mTLS
```powershell
kubectl apply -f - <<EOF
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: default
spec:
  mtls:
    mode: STRICT
EOF
```

## 8. View Telemetry
```powershell
# Open Kiali dashboard
istioctl dashboard kiali
# Open Grafana
istioctl dashboard grafana
```
