# Service Mesh Solution

## Overview
This module covers Istio, sidecar, and mesh configuration.

## Key Features

### Virtual Service
- Creating virtual services
- Routing rules
- Destination subsets

### Destination Rule
- Creating destination rules
- Subset definitions
- Load balancing policies

### Gateway
- Creating gateways
- Port configuration
- Selector-based routing

### Sidecar
- Creating sidecar resources
- Namespace configuration

### Authorization Policy
- Creating authorization policies
- ALLOW/DENY actions

### Peer Authentication
- Creating peer authentication
- mTLS modes (STRICT, PERMISSIVE, DISABLE)

## Usage

```java
ServiceMeshSolution solution = new ServiceMeshSolution();

// Create virtual service
VirtualService vs = solution.createVirtualService("my-vs", "my-service", "my-svc", "v1");

// Create destination rule
DestinationRule dr = solution.createDestinationRule("my-dr", "my-svc", "v1", "v1");

// Create gateway
Gateway gw = solution.createGateway("my-gw", "ingressgateway", 80);

// Create authorization policy
AuthorizationPolicy ap = solution.createAuthorizationPolicy("my-authz", "default", "ALLOW");
```

## Dependencies
- Istio Java Client
- JUnit 5