# Serverless Architecture — Step-by-Step Guide

## 1. Function Composition
- Chain: `OrderPlaced -> ValidateOrder -> ProcessPayment -> SendConfirmation`
- Fan-out: single event triggers multiple functions (e.g., notification + analytics).

## 2. Cold Start Mitigation
- Use snap-start / tiered compilation (CRaC, GraalVM).
- Keep-warm strategies: scheduled pings, provisioned concurrency.

## 3. Event-Driven Pipeline
- Event sources (SQS, Kinesis, EventBridge) invoke functions.
- Functions produce events to downstream sources.

## 4. State Management
- Functions are stateless; use external stores (DynamoDB, Redis).
- Example: `OrderState` stored in `StateStore` interface.

## 5. FaaS Internals
- Cold start: download code → init runtime → invoke handler.
- Warm start: re-use existing execution environment.
- Lifecycle: `Init` → `Invoke` → `Shutdown` (after idle timeout).

## Build & Run
```bash
javac --enable-preview -source 21 -d out src/com/architecture/deep/lab08/*.java
java --enable-preview -cp out com.architecture.deep.lab08.ServerlessLab
```
