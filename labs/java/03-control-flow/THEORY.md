# Control Flow — Theoretical Foundation

## Structured Programming

Control flow in Java follows the structured programming paradigm: sequence, selection, and iteration. Any algorithm can be expressed using these three constructs. Java adds `break` and `continue` for early exit within loops.

## Selection Statements

### if-else

```java
if (condition) {
    // executed when condition is true
} else if (anotherCondition) {
    // executed when first condition is false and anotherCondition is true
} else {
    // executed when all conditions are false
}
```

The condition must be a `boolean` expression — not an integer or object. This is a deliberate safety decision.

### switch Statement

Traditional switch:
```java
switch (variable) {
    case VALUE1:
        // code
        break;
    case VALUE2:
    case VALUE3:
        // shared code for VALUE2 and VALUE3
        break;
    default:
        // code for unmatched values
}
```

Supported types: `byte`, `short`, `char`, `int`, `String` (Java 7+), enums.

### switch Expression (Java 14+)

```java
int result = switch (day) {
    case MONDAY, FRIDAY -> 0;
    case TUESDAY -> 1;
    case WEDNESDAY, THURSDAY -> 2;
    case SATURDAY, SUNDAY -> 3;
};
```

Switch expressions:
- Use `->` instead of `:` and `break`
- Must be exhaustive (cover all possible values)
- Can be used as expressions (assigned to variables)
- Don't fall through

## Iteration Statements

### for Loop

```java
for (initialization; condition; update) {
    // loop body
}
```

Evaluation order: (1) initialization, (2) condition check, (3) body, (4) update, (5) repeat from (2).

### Enhanced for-loop (for-each)

```java
for (Type element : iterable) {
    // loop body
}
```

Works with arrays and any `Iterable`. Cannot modify the underlying collection during iteration (throws `ConcurrentModificationException`).

### while Loop

```java
while (condition) {
    // loop body
}
```

Condition is checked before the body. Zero iterations if condition is initially false.

### do-while Loop

```java
do {
    // loop body
} while (condition);
```

Condition is checked after the body. Always executes at least once.

## Transfer Statements

### break
Exits the innermost enclosing loop or switch. With label, exits the labeled block.

### continue
Skips the rest of the current iteration and proceeds to the update/condition check.

### Labeled Statements

```java
outer:
for (int i = 0; i < 3; i++) {
    inner:
    for (int j = 0; j < 3; j++) {
        if (i == j) continue outer;  // Skips to outer loop's update
        if (i + j == 4) break outer; // Exits outer loop entirely
    }
}
```

## Short-Circuit Evaluation

`&&` and `||` short-circuit: the right operand is evaluated only when necessary.
```java
if (obj != null && obj.isValid()) { }  // Safe: obj.isValid() not called if null
```

## Flow Analysis

The compiler performs definite assignment analysis:
- Variables must be definitely assigned before use
- The compiler can determine that an `if (true)` always executes its body
- The compiler can detect unreachable code after `break`, `continue`, `return`, or infinite loops
