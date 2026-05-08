# 43 - Service Mesh

A service mesh provides infrastructure layer for service-to-service communication, handling load balancing, encryption, authentication, and observability without code changes.

## Overview

- **Topic**: Service Mesh Architecture
- **Prerequisites**: Microservices, Kubernetes
- **Duration**: 2-3 hours

## Key Concepts

- Sidecar proxies (Envoy)
- Traffic management
- mTLS and security
- Observability and tracing

## Getting Started

Run the training code:
```bash
cd 43-service-mesh
mvn compile exec:java -Dexec.mainClass=com.learning.servicemesh.Lab
```

## Module Contents

- Service mesh patterns
- Traffic routing and splitting
- Security policies
- Observability integration