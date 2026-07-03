# How Pattern Matching Works

## instanceof Pattern Matching

### Syntax
```java
if (obj instanceof Type variable) {
    // variable is of type Type
}
```

### Semantics
1. Evaluate `obj`
2. If `obj` is `null`, the pattern does NOT match
3. If `obj instanceof Type` is true, cast `obj` to `Type` and assign to `variable`
4. `variable` is in scope in the `if` block (and the block's associated `else` if the pattern is negated)

### Flow Analysis
The compiler performs flow analysis to determine where pattern variables are in scope:

```java
if (obj instanceof String s) {
    System.out.println(s.length());  // OK
}
// System.out.println(s.length());  // COMPILER ERROR (out of scope)

// With negated instanceof:
if (!(obj instanceof String s)) {
    // s NOT in scope here
} else {
    System.out.println(s.length());  // OK — s is definitely matched
}
```

## Pattern Matching in Switch

### Syntax
```java
switch (obj) {
    case Type variable -> expression;
    case Type variable when guard -> expression;
    case null -> expression;
    default -> expression;
}
```

### Semantics
1. Evaluate the switch selector (`obj`)
2. If `obj` is `null`:
   - If a `case null` exists, use that case
   - Otherwise, throw `NullPointerException`
3. Check patterns in declaration order:
   - If the pattern matches and the guard (if any) is true, execute that case
   - Continue to next case if pattern doesn't match or guard is false
4. If no case matches, use the `default` case (if present) or throw `MatchException`

### Pattern Dominance
A pattern `p` dominates `q` if:
- `p` is a type pattern with a supertype of `q`'s type
- `p` is a record pattern and `q` matches a subset of `p`'s match set

The compiler rejects a pattern that is dominated by a previous pattern:

```java
switch (obj) {
    case Object o -> ...  // Matches everything
    case String s -> ...  // COMPILER ERROR: dominated by Object
}

// Correct ordering:
switch (obj) {
    case String s -> ...
    case Object o -> ...  // Catch-all for non-String
}
```

## Record Patterns

### Syntax
```java
if (obj instanceof RecordType(component1, component2, ...)) {
    // components are in scope
}
```

### Semantics
1. Check `obj instanceof RecordType`
2. If matched, call the accessor methods for each component
3. Recursively match nested patterns for each component
4. Bind component values to pattern variables

### Nesting Example
```java
record Phone(String countryCode, String number) {}
record Contact(String name, Phone phone) {}

if (obj instanceof Contact(var name, Phone(var cc, var num))) {
    // name, cc, num are all in scope
}
```

Step-by-step:
1. `obj instanceof Contact` → true
2. `name = ((Contact) obj).name()`
3. `((Contact) obj).phone()` → is it a `Phone`? → yes
4. `cc = phone.countryCode()`
5. `num = phone.number()`

## Guarded Patterns

### Syntax
```java
case Type variable when condition -> expression
```

### Semantics
1. Check if the value matches the pattern (type check + record deconstruction)
2. If the pattern matches, evaluate the guard (`condition`)
3. If the guard is `true`, execute the case body
4. If the guard is `false`, continue to the next case

### Execution Flow
```java
switch (obj) {
    case Integer i when i > 0 -> "positive";   // 1
    case Integer i -> "non-positive";           // 2
    case String s when s.isEmpty() -> "empty";  // 3
    case String s -> "non-empty";               // 4
}
```

For `obj = Integer(-5)`:
- Case 1: pattern matches (Integer), but guard `-5 > 0` is false → fall through
- Case 2: pattern matches → executed

For `obj = Integer(10)`:
- Case 1: pattern matches AND guard `10 > 0` is true → executed

## Exhaustiveness

### For Sealed Types
The compiler collects all leaf subtypes (final or non-sealed that aren't further restricted) and checks each leaf against the cases.

### For Non-Sealed Types
A `default` case is required unless the patterns collectively cover all possibilities (e.g., `Object o` as a catch-all).
