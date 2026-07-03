# Module 12: Java 21 Features - Mini Project

**Project Name**: Modern Java Data Processor  
**Difficulty Level**: Intermediate  
**Estimated Time**: 2-3 hours

---

## 🎯 Objective
Upgrade legacy Java code by applying Java 21 LTS features, including Pattern Matching for `switch`, Record Patterns, Virtual Threads, and Sequenced Collections.

## 📝 Requirements

### Core Features
1. **Domain Modeling with Records**:
   - Create a sealed interface `Event` permitting `ClickEvent`, `PurchaseEvent`, and `ErrorEvent`.
   - Implement these as `record` classes.
   - `PurchaseEvent` should contain a nested record `Amount(double value, String currency)`.

2. **Pattern Matching & Switch Expressions**:
   - Create a method `processEvent(Event e)` that uses a Java 21 `switch` expression.
   - Use **Record Patterns** to destructure the `PurchaseEvent` directly in the case label: `case PurchaseEvent(Amount(double v, String c), String user)`.
   - Use **Guarded Patterns** (`when`) to process high-value purchases differently than low-value ones.

3. **Virtual Threads**:
   - Simulate an external API call by making the `processEvent` method sleep for 1 second.
   - Create a main method that generates 10,000 random events.
   - Process all 10,000 events concurrently using an `ExecutorService` backed by Virtual Threads (`Executors.newVirtualThreadPerTaskExecutor()`).

4. **Sequenced Collections**:
   - Collect the results of the processing into a `LinkedHashSet`.
   - Use Java 21's new `SequencedCollection` methods to retrieve the `.getFirst()` and `.getLast()` elements processed.

5. **Unnamed Variables**:
   - Use the unnamed variable `_` in a `catch` block or a loop where the variable isn't needed.

---

## 💡 Solution Blueprint

1. **Records & Sealed Interfaces**:
   ```java
   sealed interface Event permits ClickEvent, PurchaseEvent, ErrorEvent {}
   record Amount(double value, String currency) {}
   record PurchaseEvent(Amount amount, String user) implements Event {}
   ```

2. **Pattern Matching**:
   ```java
   String processEvent(Event e) {
       return switch(e) {
           case PurchaseEvent(Amount(double v, String c), String u) when v > 1000 -> 
               "High value purchase of " + v + " by " + u;
           case PurchaseEvent(Amount a, String u) -> "Standard purchase";
           case ClickEvent c -> "Clicked";
           case ErrorEvent err -> "Error";
       };
   }
   ```

3. **Virtual Threads**:
   ```java
   try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
       for (int i = 0; i < 10000; i++) {
           executor.submit(() -> {
               // process event and sleep
           });
       }
   }
   ```