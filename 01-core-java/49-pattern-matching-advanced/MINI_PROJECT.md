# Mini Project: Abstract Syntax Tree (AST) Evaluator

## Objective
Build a mathematical expression evaluator. You will use **Sealed Interfaces** to define the nodes of an Abstract Syntax Tree (AST), and a pattern-matching `switch` expression with **Record Patterns** to recursively evaluate the tree.

## Prerequisites
*   Java 21+

## Step 1: Define the AST using Sealed Classes and Records
We define an `Expr` interface. It can only be a `Value` (a number), an `Add` operation, a `Multiply` operation, or a `Negate` operation.

```java
public sealed interface Expr permits Value, Add, Multiply, Negate {}

// Leaf node
public record Value(double number) implements Expr {}

// Binary nodes
public record Add(Expr left, Expr right) implements Expr {}
public record Multiply(Expr left, Expr right) implements Expr {}

// Unary node
public record Negate(Expr expr) implements Expr {}
```

## Step 2: Build the Evaluator
We use a `switch` expression to evaluate the tree. Notice how concise this is compared to the traditional Visitor Pattern.

```java
public class Evaluator {

    public static double evaluate(Expr expr) {
        // The compiler knows this switch is exhaustive because Expr is sealed!
        // No default branch is needed.
        return switch (expr) {
            
            // Base case: Extract the number directly from the Value record
            case Value(double n) -> n;
            
            // Recursive cases: Deconstruct the records to get the left/right expressions
            case Add(Expr left, Expr right) -> evaluate(left) + evaluate(right);
            case Multiply(Expr left, Expr right) -> evaluate(left) * evaluate(right);
            case Negate(Expr e) -> -evaluate(e);
            
            // Handle null explicitly to prevent an unexpected NPE
            case null -> throw new IllegalArgumentException("Expression cannot be null");
        };
    }
}
```

## Step 3: Implement an Optimizer with Guard Clauses
Let's build an optimizer that simplifies the tree before evaluating it. We will use `when` clauses to catch specific patterns (like multiplying by zero or adding zero).

```java
public class Optimizer {

    public static Expr optimize(Expr expr) {
        return switch (expr) {
            
            // --- Addition Optimizations ---
            
            // 0 + X = X
            case Add(Value(double n), Expr right) when n == 0.0 -> optimize(right);
            // X + 0 = X
            case Add(Expr left, Value(double n)) when n == 0.0 -> optimize(left);
            
            // --- Multiplication Optimizations ---
            
            // 0 * X = 0
            case Multiply(Value(double n), Expr right) when n == 0.0 -> new Value(0.0);
            // X * 0 = 0
            case Multiply(Expr left, Value(double n)) when n == 0.0 -> new Value(0.0);
            
            // 1 * X = X
            case Multiply(Value(double n), Expr right) when n == 1.0 -> optimize(right);
            // X * 1 = X
            case Multiply(Expr left, Value(double n)) when n == 1.0 -> optimize(left);
            
            // --- Double Negation ---
            // -(-X) = X
            case Negate(Negate(Expr inner)) -> optimize(inner);
            
            // --- Recursive Fallback ---
            // If no specific optimizations apply, optimize the children
            case Add(Expr left, Expr right) -> new Add(optimize(left), optimize(right));
            case Multiply(Expr left, Expr right) -> new Multiply(optimize(left), optimize(right));
            case Negate(Expr e) -> new Negate(optimize(e));
            
            // Base cases
            case Value v -> v;
            case null -> null;
        };
    }
}
```

## Step 4: Test the System
```java
public class Main {
    public static void main(String[] args) {
        // Equation: (5 + 0) * -( - (2 * 1) )
        // Should optimize to: 5 * 2 = 10
        
        Expr rawTree = new Multiply(
            new Add(new Value(5), new Value(0)),
            new Negate(new Negate(new Multiply(new Value(2), new Value(1))))
        );

        System.out.println("--- 1. Raw Evaluation ---");
        System.out.println("Result: " + Evaluator.evaluate(rawTree));

        System.out.println("\n--- 2. Optimization ---");
        Expr optimizedTree = Optimizer.optimize(rawTree);
        System.out.println("Optimized Tree: " + optimizedTree);
        
        System.out.println("\n--- 3. Optimized Evaluation ---");
        System.out.println("Result: " + Evaluator.evaluate(optimizedTree));
    }
}
```

## Expected Output
Notice how the optimizer stripped away the redundant `Add`, `Negate`, and `Multiply` nodes, resulting in a much simpler tree.
```text
--- 1. Raw Evaluation ---
Result: 10.0

--- 2. Optimization ---
Optimized Tree: Multiply[left=Value[number=5.0], right=Value[number=2.0]]

--- 3. Optimized Evaluation ---
Result: 10.0
```