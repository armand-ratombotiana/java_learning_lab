# Why Inheritance Matters

## Code Reuse at Scale

Inheritance eliminates duplication across related classes. A single `save()` method in a base `Repository` class is inherited by 50 entity-specific subclasses. Changes to the base class automatically propagate — no need to update 50 copies.

## Framework Design

Inheritance is central to framework design. Extending `HttpServlet` (override `doGet`/`doPost`) or `JPanel` (override `paintComponent`) is how frameworks provide extension points. Template Method pattern uses inheritance to define algorithm skeletons.

## The Object Class: Universal Methods

Every class inherits from `Object`, providing:
- `toString()` — debugging output
- `equals()` / `hashCode()` — logical equality and hashing
- `getClass()` — runtime type information

Properly overriding these methods is essential for correct behavior in collections, logging, and debugging.

## Real-World Impact of Bad Inheritance

Deep inheritance hierarchies (+5 levels) become fragile — changes to a base class can break subclasses unexpectedly (fragile base class problem). The `@Override` annotation prevents accidental method signature mismatches. Understanding when to use inheritance vs composition directly affects code quality.

## Financial Systems Example

A banking system might have:
```
Account (base) → SavingsAccount, CheckingAccount, CreditAccount → StudentSavingsAccount
```

Rules for interest calculation, overdraft protection, and fees are defined at appropriate levels. Changes to regulatory requirements affect only the relevant level.
