# Service Mesh — Step-by-Step Guide

## 1. Istio Architecture
- **Control Plane**: Istiod — manages config, certs, and discovery.
- **Data Plane**: Envoy sidecars — handle all traffic between services.

## 2. mTLS
- Istiod distributes certificates to all Envoy sidecars.
- Traffic between sidecars is automatically encrypted and authenticated.

## 3. Traffic Splitting
- `VirtualService` + `DestinationRule` to route % of traffic to different versions.
- Example: 90% v1, 10% v2 for canary.

## 4. Fault Injection
- Inject delays (latency) or aborts (HTTP errors) into requests for testing.

## 5. Circuit Breaking
- Envoy circuit breaker: max connections, max pending requests, max retries.
- When tripped, subsequent requests fail fast instead of overwhelming the service.

## Build & Run
```bash
javac --enable-preview -source 21 -d out src/com/devops/deep/lab02/*.java
java --enable-preview -cp out com.devops.deep.lab02.ServiceMeshLab
```
