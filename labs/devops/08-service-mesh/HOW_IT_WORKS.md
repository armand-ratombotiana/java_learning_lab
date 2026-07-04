# How Service Mesh Works

## Istio Data Flow
```
Service A ──▶ Sidecar Envoy A ──▶ Sidecar Envoy B ──▶ Service B
                  │                       │
                  └─────── Istiod ────────┘
                          (control plane)

1. Service A sends request to Service B via localhost (Envoy).
2. Envoy A looks up routing rules from Istiod (Pilot).
3. Envoy A encrypts with mTLS (certificates from Citadel).
4. Envoy A forwards to Envoy B (load balanced).
5. Envoy B applies authorization policies.
6. Envoy B forwards to Service B via localhost.
7. Both Envoys report telemetry (metrics, traces, logs) to Istiod.
```

## Sidecar Injection
- **Automatic**: Istio injector (mutating webhook) adds Envoy container to Pod at creation.
- **Manual**: `istioctl kube-inject -f deploy.yaml` adds sidecar to YAML.

## mTLS Flow
```
1. Envoy A presents certificate to Envoy B.
2. Envoy B validates A's certificate against CA (Istiod/Citadel).
3. Envoy B presents certificate to Envoy A.
4. Envoy A validates B's certificate.
5. mTLS connection established; traffic encrypted with session keys.
```

## Traffic Routing
```
VirtualService → matches conditions → routes to Service subsets (versions)
DestinationRule → defines how to handle traffic for each subset
```
