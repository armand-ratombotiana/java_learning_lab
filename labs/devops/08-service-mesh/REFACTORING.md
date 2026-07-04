# Service Mesh Refactoring

## Before (No Service Mesh)
```yaml
kind: Service
metadata:
  name: orders
spec:
  selector:
    app: orders
---
kind: Deployment
spec:
  template:
    spec:
      containers:
      - name: orders
        image: orders:1.0
        env:
        - name: PAYMENTS_URL
          value: "http://payments:8080"
```

## After (With Istio)
```yaml
kind: Service
metadata:
  name: orders
spec:
  selector:
    app: orders
---
kind: Deployment
spec:
  template:
    metadata:
      labels:
        app: orders
        version: v1
    spec:
      containers:
      - name: orders
        image: orders:1.0
---
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: orders
spec:
  hosts:
  - orders
  http:
  - route:
    - destination:
        host: orders
        subset: v1
      weight: 90
    - destination:
        host: orders
        subset: v2
      weight: 10
---
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: orders
spec:
  host: orders
  trafficPolicy:
    tls:
      mode: ISTIO_MUTUAL
  subsets:
  - name: v1
    labels:
      version: v1
  - name: v2
    labels:
      version: v2
```

## Gains
- No hardcoded URLs — service discovery by DNS
- Canary deployments via VirtualService weights
- mTLS between all services
- Distributed tracing automatically
- Circuit breaking and retries
