# Istio Service Mesh

## Overview
Istio is a service mesh that provides traffic management, security, and observability for microservices with zero code changes.

## Key Features
- Traffic splitting and routing
- Circuit breaker
- mTLS security
- Observability (metrics, tracing)
- Service discovery

## Project Structure
```
57-service-mesh/
  istio-mesh/
    src/main/java/com/learning/istio/mesh/IstioMeshLab.java
```

## Running
```bash
cd 57-service-mesh/istio-mesh
mvn compile exec:java
```

## Concepts Covered
- VirtualService for traffic routing
- DestinationRule for traffic policies
- Gateway configuration
- mTLS

## Dependencies
- Istio client library