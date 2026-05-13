# 57 - Service Mesh Quick Reference

## Key Concepts

| Concept | Description |
|---------|-------------|
| Sidecar | Proxy alongside each pod |
| Control Plane | Manages proxy configuration |
| Data Plane | Handles traffic |
| mTLS | Mutual TLS for service auth |
| Canary | Gradual traffic shifting |
| Circuit Breaker | Failure isolation |

## Istio Architecture

```
┌─────────────────────────────────────────┐
│           Control Plane (Istiod)        │
│  - Configuration                        │
│  - Certificate management               │
│  - Service discovery                   │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│              Data Plane                  │
│  ┌─────────┐    ┌─────────┐             │
│  │ Sidecar │    │ Sidecar │  (Envoy)    │
│  │ Proxy   │    │ Proxy   │             │
│  └─────────┘    └─────────┘             │
└─────────────────────────────────────────┘
```

## Virtual Service

```yaml
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: myapp-vs
spec:
  hosts:
  - myapp
  http:
  - match:
    - headers:
        x-canary:
          exact: "true"
    route:
    - destination:
        host: myapp
        subset: v2
      weight: 100
  - route:
    - destination:
        host: myapp
        subset: v1
      weight: 90
    - destination:
        host: myapp
        subset: v2
      weight: 10
```

## Destination Rule

```yaml
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: myapp-dr
spec:
  host: myapp
  trafficPolicy:
    connectionPool:
      tcp:
        maxConnections: 100
      http:
        h2UpgradePolicy: UPGRADE
        http1MaxPendingRequests: 100
        http2MaxRequests: 1000
    loadBalancer:
      simple: LEAST_REQUEST
    outlierDetection:
      consecutive5xxErrors: 5
      interval: 30s
      baseEjectionTime: 30s
  subsets:
  - name: v1
    labels:
      version: v1
  - name: v2
    labels:
      version: v2
```

## Service Entry

```yaml
apiVersion: networking.istio.io/v1beta1
kind: ServiceEntry
metadata:
  name: external-api
spec:
  hosts:
  - api.external.com
  ports:
  - number: 443
    name: https
    protocol: HTTPS
  location: MESH_EXTERNAL
  resolution: DNS
```

## Authorization Policy

```yaml
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: myapp-auth
spec:
  selector:
    matchLabels:
      app: myapp
  rules:
  - from:
    - source:
        principals: ["cluster.local/ns/default/sa/frontend"]
    to:
    - operations:
      - paths: ["/api/admin"]
        methods: ["GET"]
    when:
    - key: request.headers[x-role]
      values: ["admin"]
```

## Peer Authentication (mTLS)

```yaml
# Namespace-level mTLS
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: default
  namespace: production
spec:
  mtls:
    mode: STRICT

---
# Per-workload mTLS
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: myapp-peer
spec:
  selector:
    matchLabels:
      app: myapp
  mtls:
    mode: PERMISSIVE
```

## Request Authentication (JWT)

```yaml
apiVersion: security.istio.io/v1beta1
kind: RequestAuthentication
metadata:
  name: myapp-jwt
spec:
  selector:
    matchLabels:
      app: myapp
  jwtRules:
  - issuer: "https://auth.example.com"
    audiences:
    - "myapp"
    forwardOriginalToken: true
    # Extract claims for authorization
    claimToHeaders:
    - claim: sub
      header: x-user
```

## Gateway

```yaml
apiVersion: networking.istio.io/v1beta1
kind: Gateway
metadata:
  name: myapp-gateway
spec:
  selector:
    istio: ingressgateway
  servers:
  - port:
      number: 80
      name: http
      protocol: HTTP
    hosts:
    - "myapp.example.com"
    tls:
      httpsRedirect: true
  - port:
      number: 443
      name: https
      protocol: HTTPS
    hosts:
    - "myapp.example.com"
    tls:
      mode: SIMPLE
      credentialName: myapp-cert
```

## Telemetry

```yaml
apiVersion: telemetry.istio.io/v1alpha1
kind: Telemetry
metadata:
  name: myapp-telemetry
spec:
  tracing:
  - providers:
    - name: jaeger
    randomSamplingPercentage: 10.0
  metrics:
  - providers:
    - name: prometheus
  accessLogging:
  - providers:
    - name: envoy
```

## Linkerd Architecture

```
┌─────────────────────────────────────────┐
│        Control Plane (linkerd)          │
│  - destination (service discovery)     │
│  - identity (certificate issuance)      │
│  - identity (PKI)                       │
└─────────────────────────────────────────┘
                    │
┌─────────────────────────────────────────┐
│              Data Plane                  │
│  ┌─────────┐    ┌─────────┐  (Linkerd) │
│  │ Sidecar │    │ Sidecar │  (proxy)    │
│  │ Proxy   │    │ Proxy   │             │
│  └─────────┘    └─────────┘             │
└─────────────────────────────────────────┘
```

## Linkerd Service Profile

```yaml
apiVersion: linkerd.io/v1alpha2
kind: ServiceProfile
metadata:
  name: myapp.default.svc.cluster.local
spec:
  routes:
  - name: GET /api/users
    condition:
      path: /api/users
      method: GET
    metrics:
      name: request_count
    retries:
      budget:
        minRetriesPerSecond: 10
        retryPercent: 20%
    timeout: 300ms
```

## Commands

```bash
# Istio
istioctl get virtualservices
istioctl get destinationrules
istioctl proxy-config route <pod>
istioctl proxy-config clusters <pod>
istioctl analyze

# Linkerd
linkerd get routes
linkerd get pods
linkerd stat deploy
linkerd tap <pod>
linkerd edges deploy
```

## Best Practices

Enable mTLS for all traffic. Use canary deployments for gradual rollouts. Configure circuit breakers to prevent cascading failures. Implement proper observability with tracing. Use authorization policies for zero-trust security. Monitor service metrics and set alerts.