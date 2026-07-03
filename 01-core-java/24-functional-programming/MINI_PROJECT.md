# Module 24: Functional Programming - Mini Project

**Project Name**: Declarative Data Pipeline Engine  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Apply deep Functional Programming (FP) paradigms in Java by building a robust data pipeline. The project must strictly enforce Immutability, use Higher-Order Functions, apply Currying/Partial Application, and utilize `Optional` to eliminate null checks.

## 📝 Requirements

### Core Features

1. **Immutable Domain Models**:
   - Create a Java `record` named `Transaction(String id, double amount, String currency, String status)`.
   - Records are inherently immutable. You must not use any mutable POJOs.

2. **Pure Functions & High-Order Functions**:
   - Create a class `TransactionRules`.
   - Define several pure functions as `static final` `Predicate<Transaction>` fields:
     - `isCompleted`: returns true if status is "COMPLETED".
     - `isLargeAmount`: returns true if amount > 10,000.
   - Define a higher-order function: `public static Predicate<Transaction> hasCurrency(String currency)` that returns a `Predicate`.

3. **Currying and Partial Application**:
   - Create a currency converter function. Since standard Java `Function` only takes one argument, use nested functions: 
     `Function<Double, Function<Double, Double>> convert = rate -> amount -> amount * rate;`
   - Use partial application to create specific converter functions:
     - `Function<Double, Double> eurToUsd = convert.apply(1.10);`
     - `Function<Double, Double> gbpToUsd = convert.apply(1.25);`

4. **The Pipeline (Monadic Operations)**:
   - Create a method `public Optional<Double> processTransactions(List<Transaction> transactions)`.
   - Use the Streams API to process the list declaratively:
     - Filter out incomplete transactions.
     - Filter out transactions that aren't in "EUR".
     - Map the amounts using the `eurToUsd` partial function.
     - Reduce the stream to calculate the total sum.
     - Return the result wrapped in an `Optional<Double>`. If the initial list is null or the sum is 0, return `Optional.empty()`.

---

## 💡 Solution Blueprint

1. **Domain Model**:
   ```java
   public record Transaction(String id, double amount, String currency, String status) {}
   ```

2. **Functional Rules**:
   ```java
   public class TransactionRules {
       public static final Predicate<Transaction> IS_COMPLETED = 
           t -> "COMPLETED".equalsIgnoreCase(t.status());
           
       public static Predicate<Transaction> hasCurrency(String currency) {
           return t -> currency.equalsIgnoreCase(t.currency());
       }
   }
   ```

3. **Curried Converter**:
   ```java
   public class CurrencyConverter {
       public static final Function<Double, Function<Double, Double>> CONVERT = 
           rate -> amount -> amount * rate;
           
       public static final Function<Double, Double> EUR_TO_USD = CONVERT.apply(1.10);
   }
   ```

4. **Pipeline Execution**:
   ```java
   public Optional<Double> processTransactions(List<Transaction> transactions) {
       if (transactions == null || transactions.isEmpty()) {
           return Optional.empty();
       }
       
       double total = transactions.stream()
           .filter(TransactionRules.IS_COMPLETED)
           .filter(TransactionRules.hasCurrency("EUR"))
           .map(Transaction::amount)
           .map(CurrencyConverter.EUR_TO_USD)
           .reduce(0.0, Double::sum);
           
       return total > 0 ? Optional.of(total) : Optional.empty();
   }
   ```