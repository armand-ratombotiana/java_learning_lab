# Mini Project: Custom Rule Engine

## Objective
Build a flexible Rule Engine that uses standard Java functional interfaces (`Predicate`, `Function`, `Consumer`) to evaluate data, apply transformations, and execute actions based on the results.

## Prerequisites
*   Java 17+

## Step 1: Define the Domain Model
Create a simple `Transaction` record.

```java
public record Transaction(String id, double amount, String type, String status) {}
```

## Step 2: Create the Rule Class
A Rule consists of a condition (Predicate), an optional transformation (Function), and an action (Consumer).

```java
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Rule<T> {
    private final Predicate<T> condition;
    private final Function<T, T> transformation;
    private final Consumer<T> action;

    private Rule(Builder<T> builder) {
        this.condition = builder.condition;
        this.transformation = builder.transformation;
        this.action = builder.action;
    }

    // Evaluates the rule against the input
    public void evaluate(T input) {
        if (condition.test(input)) {
            T transformedInput = (transformation != null) ? transformation.apply(input) : input;
            if (action != null) {
                action.accept(transformedInput);
            }
        }
    }

    // Builder pattern for fluent rule creation
    public static class Builder<T> {
        private Predicate<T> condition = x -> true; // Default: always true
        private Function<T, T> transformation = null;
        private Consumer<T> action = null;

        public Builder<T> when(Predicate<T> condition) {
            this.condition = condition;
            return this;
        }

        public Builder<T> map(Function<T, T> transformation) {
            this.transformation = transformation;
            return this;
        }

        public Builder<T> then(Consumer<T> action) {
            this.action = action;
            return this;
        }

        public Rule<T> build() {
            return new Rule<>(this);
        }
    }
}
```

## Step 3: Implement the Rule Engine
The engine holds a list of rules and applies them to incoming transactions.

```java
import java.util.ArrayList;
import java.util.List;

public class RuleEngine<T> {
    private final List<Rule<T>> rules = new ArrayList<>();

    public void addRule(Rule<T> rule) {
        rules.add(rule);
    }

    public void process(T item) {
        // Use method reference to evaluate each rule
        rules.forEach(rule -> rule.evaluate(item));
    }
}
```

## Step 4: Define Rules using Lambdas and Method References
Create specific rules using the `java.util.function` interfaces.

```java
public class Main {
    public static void main(String[] args) {
        RuleEngine<Transaction> engine = new RuleEngine<>();

        // Rule 1: High-value alert (Using pure lambdas)
        engine.addRule(new Rule.Builder<Transaction>()
            .when(tx -> tx.amount() > 10000)
            .then(tx -> System.out.println("ALERT: High value transaction detected: " + tx.id()))
            .build());

        // Rule 2: Fraud suspicion transformation
        // Transforms a PENDING transaction to a FRAUD_REVIEW transaction if it's a specific type
        engine.addRule(new Rule.Builder<Transaction>()
            .when(tx -> tx.status().equals("PENDING") && tx.type().equals("CRYPTO"))
            .map(tx -> new Transaction(tx.id(), tx.amount(), tx.type(), "FRAUD_REVIEW"))
            .then(tx -> System.out.println("Status updated to: " + tx.status() + " for " + tx.id()))
            .build());

        // Rule 3: Logging all completed transactions (Using method references)
        engine.addRule(new Rule.Builder<Transaction>()
            .when(tx -> tx.status().equals("COMPLETED"))
            .then(System.out::println) // Method reference to System.out.println
            .build());

        // Test Data
        Transaction tx1 = new Transaction("TX-001", 15000, "WIRE", "COMPLETED");
        Transaction tx2 = new Transaction("TX-002", 500, "CRYPTO", "PENDING");
        Transaction tx3 = new Transaction("TX-003", 50, "DEBIT", "COMPLETED");

        System.out.println("Processing TX-001...");
        engine.process(tx1);
        
        System.out.println("\nProcessing TX-002...");
        engine.process(tx2);
        
        System.out.println("\nProcessing TX-003...");
        engine.process(tx3);
    }
}
```

## Expected Output
```text
Processing TX-001...
ALERT: High value transaction detected: TX-001
Transaction[id=TX-001, amount=15000.0, type=WIRE, status=COMPLETED]

Processing TX-002...
Status updated to: FRAUD_REVIEW for TX-002

Processing TX-003...
Transaction[id=TX-003, amount=50.0, type=DEBIT, status=COMPLETED]
```