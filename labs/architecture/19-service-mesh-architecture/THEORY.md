# Service Mesh Architecture — Theory

## 1. Introduction
Service Mesh is a dedicated infrastructure layer for handling service-to-service communication in microservices. It provides traffic management, security, observability, and policy enforcement without modifying application code.

## 2. Architecture Components

### 2.1 Control Plane
The control plane manages and configures the data plane. It provides service discovery integration, certificate management for mTLS, traffic routing rules, policy enforcement, and telemetry aggregation.

### 2.2 Data Plane
The data plane consists of sidecar proxies intercepting all network traffic. Responsibilities include load balancing, health checking, circuit breaking, TLS termination, authentication, metrics generation, rate limiting, and access control.

## 3. Traffic Management

### 3.1 Request Routing
Route traffic based on source and destination service, HTTP headers and path, traffic percentage for canary, and source labels for blue/green.

### 3.2 Traffic Splitting
Distribute traffic across multiple versions: canary deployments with gradual shifting, A/B testing with header-based routing, and blue/green with instant switchover.

### 3.3 Ingress and Egress
Manage inbound traffic to the mesh and outbound traffic to external services.

## 4. Security

### 4.1 Mutual TLS (mTLS)
Automatic encryption and authentication between services with automatic certificate generation and rotation, strong service identity verification, and encrypted communication without code changes.

### 4.2 Access Control
Fine-grained authorization policies including service-to-service RBAC, HTTP method and path restrictions, JWT validation at proxy level, and OAuth2 integration.

## 5. Observability

### 5.1 Metrics
HTTP request count, duration, size, TCP connection metrics, gRPC stream metrics, and custom metrics.

### 5.2 Distributed Tracing
Trace propagation through proxy-generated spans with OpenTelemetry integration, trace sampling strategies, and span tagging.

### 5.3 Access Logs
Structured logging of all requests flowing through the mesh.

## 6. Istio Service Mesh
Istio provides Envoy proxy for data plane, Istiod for control plane, Gateway API for ingress/egress, multi-cluster federation, and VM mesh expansion.

## 7. Linkerd Service Mesh
Linkerd offers a simpler alternative written in Rust with minimal configuration overhead, automatic mTLS, tap capability for live traffic inspection, and native Kubernetes integration.

## 8. Adoption Strategy
Start with observability, add mTLS security, introduce traffic management gradually, implement policies iteratively, and monitor adoption and performance impact.
