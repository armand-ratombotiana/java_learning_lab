# Pattern Matching Theory & Intuition

## 💡 The Problem with Traditional Object-Oriented Java
For decades, Java has relied on traditional Object-Oriented Programming (OOP) patterns, heavily utilizing inheritance and polymorphism. 

When you received an object of an unknown type (e.g., `Object obj`), you had to perform a clunky, repetitive three-step process:
1. **Test**: `if (obj instanceof String)`
2. **Declare**: `String s;`
3. **Cast**: `s = (String) obj;`

This boilerplate code is verbose, error-prone, and hides the actual business logic. Furthermore, when dealing with complex data structures (like ASTs or JSON), traditional OOP forces you to spread behavior across multiple classes using the Visitor Pattern, which is notoriously difficult to maintain.

## 🔄 The Shift: Data-Oriented Programming
Modern Java (versions 14 through 21) has introduced features that support **Data-Oriented Programming (DOP)**. DOP treats data as immutable and separates behavior from data.

### 1. Records
Introduced in Java 14, `record` is a transparent carrier for immutable data. It automatically generates constructors, getters, `equals()`, `hashCode()`, and `toString()`. It eliminates the need for Lombok `@Data` in many cases.

### 2. Sealed Classes
Introduced in Java 15, `sealed class` restricts which other classes may extend it. This allows the compiler to know *exhaustively* all possible subclasses. If a sealed interface `Shape` only permits `Circle` and `Square`, the compiler knows there are no other shapes in the universe.

### 3. Pattern Matching
Pattern matching allows you to test an object against a specific pattern and, if it matches, extract its components into variables in a single, concise step.

- **Pattern Matching for `instanceof` (Java 16)**: Combines the test, declaration, and cast into one step: `if (obj instanceof String s) { ... }`
- **Pattern Matching for `switch` (Java 21)**: Allows `switch` statements to test types, not just values.
- **Record Patterns (Java 21)**: Allows you to deconstruct a record directly inside a switch case or `instanceof` check. e.g., `case Point(int x, int y) -> ...`

By combining Records, Sealed Classes, and Pattern Matching for switch, Java now supports highly expressive, functional-style data processing that is checked for exhaustiveness at compile time.