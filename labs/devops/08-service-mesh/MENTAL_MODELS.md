# Mental Models for Service Mesh

## 1. Network as Operating System
Service mesh is like adding a networking layer between services — similar to how an OS handles IPC, process isolation, and resource management, the service mesh handles inter-service communication, security, and observability.

## 2. Sidecar as Network Card
The sidecar proxy is like a dedicated network card for each service. Application code doesn't handle network protocols — the network card (sidecar) handles encryption, routing, and retries transparently.

## 3. Control Plane as Air Traffic Control
- **Pilot** = Air traffic controller (routes traffic, knows destinations)
- **Envoy proxies** = Airplane pilots (follow instructions, report status)
- **VirtualService** = Flight plan (which route to take)
- **DestinationRule** = Landing procedures (how to approach)
- **mTLS** = Encrypted radio communication

## 4. Mesh as Security Perimeter
Think of the mesh as a security checkpoint at every door. Before any two services can communicate, they must authenticate (mTLS handshake) and be authorized (AuthorizationPolicy).
