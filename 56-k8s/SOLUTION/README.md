# Kubernetes Solution

## Overview
This module covers Kubernetes API, deployments, and services.

## Key Features

### API Client
- Creating Kubernetes API client
- CoreV1Api for core resources
- AppsV1Api for deployment resources

### Namespace
- Creating namespaces

### Deployment
- Creating deployments
- Setting replicas
- Configuring containers
- Label selectors

### Service
- Creating services
- Service types (ClusterIP, NodePort, LoadBalancer)
- Port configuration

### ConfigMap & Secret
- Creating ConfigMaps
- Creating Secrets

## Usage

```java
K8sSolution solution = new K8sSolution();
ApiClient client = solution.createClient();
CoreV1Api coreApi = solution.createCoreV1Api(client);
AppsV1Api appsApi = solution.createAppsV1Api(client);

// Create namespace
coreApi.createNamespace(solution.createNamespace("my-ns"));

// Create deployment
appsApi.createNamespacedDeployment(solution.createDeployment("my-app", "nginx:latest", 3));

// Create service
coreApi.createNamespacedService(solution.createService("my-service", "ClusterIP", 80, 8080));
```

## Dependencies
- Kubernetes Java Client
- JUnit 5
- Mockito