# How Sealed Classes Work

## Declaration Syntax

### Sealed Interface
```java
public sealed interface Shape permits Circle, Rectangle, Triangle {}
```

### Sealed Abstract Class
```java
public sealed abstract class PaymentMethod 
    permits CreditCard, PayPal, CryptoWallet, BankTransfer {}
```

## Permitted Subtype Rules

### Same Module Requirement
All permitted subtypes must be in the same module as the sealed type. In the unnamed module (the default), they must be in the same package.

### Direct Extension
A permitted subtype must directly extend the sealed type:

```java
sealed interface A permits B {}
sealed interface B extends A permits C {}  // OK
// interface C extends A {}  // ERROR: not in permits clause
```

### Subtype Modifiers
Each permitted subtype must be declared with one of:

```java
// 1. final — no further extension
final class Circle extends Shape {}

// 2. sealed — continues the sealed hierarchy
sealed class Polygon extends Shape permits Triangle, Rectangle {}
final class Triangle extends Polygon {}
final class Rectangle extends Polygon {}

// 3. non-sealed — opens the hierarchy
non-sealed class FreeForm extends Shape {}
class CustomShape extends FreeForm {}  // OK
```

## Compiler Enforcement

The compiler enforces:

1. **Completeness**: Every type in the `permits` clause must extend the sealed type
2. **Exhaustiveness**: No other type can extend the sealed type
3. **Classification**: Each permitted subtype is `final`, `sealed`, or `non-sealed`
4. **No circularity**: A type cannot permit itself

## Exhaustive Switch Example

```java
sealed interface Vehicle permits Car, Bike, Truck {}
record Car(int seats) implements Vehicle {}
record Bike(boolean hasSidecar) implements Vehicle {}
record Truck(int payloadKg) implements Vehicle {}

String describe(Vehicle v) {
    return switch (v) {
        case Car s -> s.seats() + " seats";
        case Bike b -> b.hasSidecar() ? "With sidecar" : "No sidecar";
        case Truck t -> t.payloadKg() + " kg payload";
    };  // No default needed
}

// If we add: record Bus(int capacity) implements Vehicle {}
// The switch above would fail to compile — missing Bus case!
```

## Multiple Permitted Subtype Classification

A permitted subtype can itself be a sealed hierarchy:

```java
sealed interface Node permits Leaf, Branch {}
record Leaf(int value) implements Node {}
sealed interface Branch extends Node permits BinaryNode, TernaryNode {}
record BinaryNode(Node left, Node right) implements Branch {}
record TernaryNode(Node first, Node second, Node third) implements Branch {}
```

With deep hierarchies, pattern matching can nest:

```java
String describe(Node n) {
    return switch (n) {
        case Leaf(var v) -> "Leaf(" + v + ")";
        case BinaryNode(var left, var right) -> "Binary(" + describe(left) + ", " + describe(right) + ")";
        case TernaryNode(var f, var s, var t) -> "Ternary(" + describe(f) + ", " + describe(s) + ", " + describe(t) + ")";
    };
}
```

## Sealed Classes and Records

Records are inherently `final` and work naturally with sealed hierarchies:

```java
sealed interface Expr 
    permits Constant, Negate, Add, Multiply {}

record Constant(int value) implements Expr {}
record Negate(Expr expr) implements Expr {}
record Add(Expr left, Expr right) implements Expr {}
record Multiply(Expr left, Expr right) implements Expr {}
```

Each record is a `final` implementation of `Expr`.

## Sealed Classes and Enums

Enums can implement sealed interfaces:

```java
sealed interface Status permits Active, Inactive, Pending {
    String description();
}

enum Active implements Status {
    INSTANCE;
    public String description() { return "Active"; }
}

enum Inactive implements Status {
    INSTANCE;
    public String description() { return "Inactive"; }
}

enum Pending implements Status {
    INSTANCE;
    public String description() { return "Pending"; }
}
```

## Reflection

Sealed classes support reflection:

```java
if (cls.isSealed()) {
    Class<?>[] permitted = cls.getPermittedSubclasses();
    System.out.println("Permitted subclasses: " + Arrays.toString(permitted));
}
```
