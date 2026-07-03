# Mini Project: Secure Domain Model using Advanced Encapsulation

## Objective
Build a small, highly encapsulated domain model for a Banking system. You will use Java Records for Data Transfer Objects (DTOs), Sealed Classes to strictly define transaction types, and defensive copying to ensure true immutability.

## Prerequisites
*   Java 17+

## Step 1: Define a Secure Immutable Class
Create an immutable `Money` class. We use `BigDecimal` to avoid floating-point errors, but we must ensure the class itself cannot be subverted.

```java
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public final class Money {
    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        // Validation (Invariant)
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = Objects.requireNonNull(amount);
        this.currency = Objects.requireNonNull(currency);
    }

    public BigDecimal getAmount() {
        return amount; // BigDecimal is immutable, so returning directly is safe
    }

    public Currency getCurrency() {
        return currency; // Currency is immutable
    }

    // Behavior, not just data
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
}
```

## Step 2: Use Sealed Classes for Domain Logic
Define a strict hierarchy for Bank Transactions. A transaction can ONLY be a Deposit, Withdrawal, or Transfer.

```java
public sealed interface Transaction permits Deposit, Withdrawal, Transfer {
    String transactionId();
    Money amount();
}

public record Deposit(String transactionId, Money amount, String targetAccountId) implements Transaction {}

public record Withdrawal(String transactionId, Money amount, String sourceAccountId) implements Transaction {}

public record Transfer(String transactionId, Money amount, String sourceAccountId, String targetAccountId) implements Transaction {}
```

## Step 3: Implement Pattern Matching (Java 21 Feature)
Create a processor that handles the transactions. Because `Transaction` is sealed, the compiler knows exactly what types exist, allowing for exhaustive `switch` statements without a `default` case.

```java
public class TransactionProcessor {
    
    public void process(Transaction tx) {
        // Java 21 Pattern Matching for switch
        switch (tx) {
            case Deposit d -> {
                System.out.println("Processing Deposit to " + d.targetAccountId() + " for " + d.amount().getAmount());
                // Logic to add funds
            }
            case Withdrawal w -> {
                System.out.println("Processing Withdrawal from " + w.sourceAccountId() + " for " + w.amount().getAmount());
                // Logic to deduct funds
            }
            case Transfer t -> {
                System.out.println("Processing Transfer from " + t.sourceAccountId() + " to " + t.targetAccountId());
                // Logic to move funds
            }
            // NO default case needed! The compiler knows these are the ONLY 3 possibilities.
        }
    }
}
```

## Step 4: The "Leaky" Record Challenge
Create a `CustomerProfile` record that holds a list of roles. Implement a compact constructor to ensure the list is defensively copied, preventing external mutation.

```java
import java.util.List;
import java.util.ArrayList;

public record CustomerProfile(String customerId, List<String> roles) {
    
    // Compact Constructor for validation and defensive copying
    public CustomerProfile {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("ID cannot be blank");
        }
        // Defensive copy: Ensure the internal list cannot be modified by the caller
        roles = List.copyOf(roles); // Java 10+ unmodifiable list
    }
    
    // Test the encapsulation
    public static void main(String[] args) {
        List<String> mutableRoles = new ArrayList<>();
        mutableRoles.add("USER");
        
        CustomerProfile profile = new CustomerProfile("CUST-123", mutableRoles);
        
        // Attempt to hack the record by modifying the original list
        mutableRoles.add("ADMIN");
        
        System.out.println("Original List: " + mutableRoles);
        System.out.println("Record's List: " + profile.roles());
        // If encapsulated correctly, the Record's list will NOT contain "ADMIN".
    }
}
```