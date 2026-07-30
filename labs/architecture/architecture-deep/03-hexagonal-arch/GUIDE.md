# Hexagonal Architecture — Step-by-Step Guide

## 1. Define the Domain
- Create `Account` entity with business logic.
- Define `AccountRepository` (outbound port) and `AccountService` (inbound port interface).

## 2. Application Core
- `AccountService` implements use cases: create, deposit, withdraw, transfer.
- Core depends only on ports (Java interfaces), never on adapters.

## 3. Inbound Adapters
- `ConsoleAdapter` reads CLI input, calls `AccountService`.
- `RestAdapter` would map HTTP requests to the same service interface.

## 4. Outbound Adapters
- `InMemoryAccountRepository` implements `AccountRepository`.
- `DatabaseAccountRepository` would be a swap-in replacement.

## 5. Dependency Injection
- Wire adapters to ports at composition root (main method).

## Build & Run
```bash
javac --enable-preview -source 21 -d out src/com/architecture/deep/lab03/*.java
java --enable-preview -cp out com.architecture.deep.lab03.HexagonalLab
```
