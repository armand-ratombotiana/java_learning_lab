# Domain-Driven Design — Step-by-Step Guide

## 1. Ubiquitous Language
- Use domain terms consistently in code, tests, and conversations.
- Example: "Quote", "Coverage", "Premium", "Policy" in insurance.

## 2. Bounded Context
- Insurance: Underwriting, Sales, Claims are separate contexts.
- Each has its own model of "Policy".

## 3. Aggregate
- `Policy` aggregate roots `PolicyHolder` and `Coverage`.
- Consistency boundary: all changes to the aggregate go through the root.

## 4. Entity vs Value Object
- Entity: `PolicyHolder` (has identity — `holderId`).
- Value Object: `Money`, `Address`, `DateRange` (immutable, compared by attributes).

## 5. Domain Event
- `PolicyIssued`, `PremiumPaid` — represent something that happened.
- Handled within or across bounded contexts.

## 6. Repository
- `PolicyRepository` provides collection-like access to aggregates.
- One repository per aggregate root.

## Build & Run
```bash
javac --enable-preview -source 21 -d out src/com/architecture/deep/lab05/*.java
java --enable-preview -cp out com.architecture.deep.lab05.DddLab
```
