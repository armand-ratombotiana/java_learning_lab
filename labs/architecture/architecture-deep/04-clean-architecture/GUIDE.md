# Clean Architecture — Step-by-Step Guide

## 1. Entity Layer (inner)
- `Order`, `Product` — enterprise-wide business rules.
- No dependencies on any outer layer.

## 2. Use Case Layer
- `CreateOrderUseCase`, `ProcessPaymentUseCase` — application-specific rules.
- Depends only on Entities and boundary interfaces.

## 3. Interface Adapters
- `OrderController` (presenter/controller) converts DTOs to use case requests.
- `OrderRepositoryGateway` implements repository port.

## 4. Frameworks & Drivers (outermost)
- Main method wires dependencies.
- Database, web framework, UI live here.

## 5. Boundary Crossing
- Use case `OutputBoundary` interface in inner layer; presenter implements it.
- `CreateOrderInteractor` calls presenter with response model.

## Comparison: Clean vs Hexagonal
- Both enforce dependency inversion. Clean adds concentric layers with specific responsibilities; Hexagonal uses ports/adapters with symmetric boundaries.

## Build & Run
```bash
javac --enable-preview -source 21 -d out src/com/architecture/deep/lab04/*.java
java --enable-preview -cp out com.architecture.deep.lab04.CleanArchLab
```
