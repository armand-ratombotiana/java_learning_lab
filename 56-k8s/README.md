# Kubernetes Operators

## Overview
Kubernetes Operators extend the API to automate complex applications using the controller pattern and Custom Resource Definitions (CRDs).

## Key Features
- Custom Resource Definitions (CRDs)
- Controller reconciliation loop
- Operator SDK
- Lifecycle management
- GitOps integration

## Project Structure
```
56-k8s/
  k8s-operators/
    src/main/java/com/learning/k8s/operators/K8sOperatorsLab.java
```

## Running
```bash
cd 56-k8s/k8s-operators
mvn compile exec:java
```

## Concepts Covered
- Operator pattern
- CRD creation
- Controller implementation
- Reconciliation logic

## Dependencies
- Java Operator SDK