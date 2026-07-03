# Refactoring Core Java Code

## Common Refactoring Patterns

### Extract Method
Break large methods into smaller, focused methods:
```java
// Before
public void processOrder(Order order) {
    validateOrder(order);
    calculateTotal(order);
    applyDiscount(order);
    saveOrder(order);
    sendConfirmation(order);
}

// After - each method has single responsibility
public void processOrder(Order order) {
    validateOrder(order);
    calculateFinalPrice(order);
    persistOrder(order);
    notifyCustomer(order);
}
```

### Replace Conditional with Polymorphism
Use inheritance instead of switch/if statements:
```java
// Before
if (type.equals("A")) { ... }
else if (type.equals("B")) { ... }

// After - use strategy pattern or inheritance
```

### Introduce Parameter Object
Group related parameters into objects:
```java
// Before
void createUser(String name, String email, String phone, String address) {}

// After
void createUser(UserProfile profile) {}
```

## Code Smells to Address

- Long methods (>30 lines)
- Duplicate code
- Feature envy (using too many other class methods)
- Primitive obsession (using primitives instead of objects)