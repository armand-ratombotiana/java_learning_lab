# DDD Exercises

## Beginner Exercises

### Exercise 1: Value Object Creation
Create Money, Email, and PhoneNumber value objects with validation.

### Exercise 2: Entity vs Value Object
Identify entities vs value objects in a library domain (Book, Patron, Loan, ISBN).

## Intermediate Exercises

### Exercise 3: Implement Aggregate
Build a BankAccount aggregate:
- Account, Transaction (entities)
- Money (value object)
- Business rules: no overdraft, daily withdrawal limit

### Exercise 4: Domain Events
Add domain events to the BankAccount aggregate:
```java
public class BankAccount {
    public void withdraw(Money amount) {
        // Validate
        // Deduct
        registerEvent(new MoneyWithdrawn(id, amount, balance));
    }
}
```

### Exercise 5: Bounded Contexts
Model an e-commerce system with 3 bounded contexts:
- Catalog (products, categories)
- Sales (orders, customers)
- Shipping (fulfillment, tracking)

## Advanced Exercises

### Exercise 6: Anticorruption Layer
Implement an ACL between Sales and a legacy Inventory system:
```java
@Component
public class InventoryTranslator {
    public InventoryItem fromLegacy(LegacyInventoryItem legacy) {
        // Translate from legacy format to domain format
    }
}
```

### Exercise 7: Event Storming
Run an Event Storming workshop for an ATM domain
