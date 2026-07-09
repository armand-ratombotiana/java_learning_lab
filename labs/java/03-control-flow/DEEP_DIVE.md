# Control Flow — Ultra Deep Dive

## 1. Bytecode-Level Control Flow: The JVM Instruction Set

The JVM uses a **zero-operand** (stack-based) instruction set. Control flow is implemented via branching instructions that compare values on the operand stack and jump to bytecode offsets.

### If-Else Compilation

```java
// Source
if (x > 0) {
    positive();
} else {
    nonPositive();
}
```

```bytecode
// Bytecode (javap -c)
  iload_1           // push x
  ifle 12           // if x <= 0, jump to offset 12
  invokestatic positive
  goto 15           // skip else block
  invokestatic nonPositive
  return
```

The `ifle` instruction pops one `int` from the stack and compares it to 0. If the condition is true (x <= 0), execution continues at offset 12. Otherwise, fall-through executes `positive()`.

**Comparison opcodes table**:

| Opcode | Mnemonic | Condition |
|--------|----------|-----------|
| 0x99 | `ifeq` | value == 0 |
| 0x9a | `ifne` | value != 0 |
| 0x9b | `iflt` | value < 0 |
| 0x9c | `ifge` | value >= 0 |
| 0x9d | `ifgt` | value > 0 |
| 0x9e | `ifle` | value <= 0 |
| 0x9f | `if_icmpeq` | value1 == value2 |
| 0xa0 | `if_icmpne` | value1 != value2 |
| 0xa1 | `if_icmplt` | value1 < value2 |
| 0xa2 | `if_icmpge` | value1 >= value2 |
| 0xa3 | `if_icmpgt` | value1 > value2 |
| 0xa4 | `if_icmple` | value1 <= value2 |
| 0xa5 | `if_acmpeq` | ref1 == ref2 |
| 0xa6 | `if_acmpne` | ref1 != ref2 |

### Switch Statement Compilation

There are TWO compilation strategies for `switch`:

**1. `tableswitch` (dense cases)**:
```java
switch (x) {
    case 0: a(); break;
    case 1: b(); break;
    case 2: c(); break;
}
```

```bytecode
tableswitch 0 2       // low=0, high=2
   0: 20              // offset 20 for case 0
   1: 28              // offset 28 for case 1
   2: 36              // offset 36 for case 2
   default: 44
```

`tableswitch` is O(1) — it uses the switch value as an index into a jump table. For dense cases (no gaps > 20%), this strategy is used.

**2. `lookupswitch` (sparse cases)**:
```java
switch (x) {
    case 1: a(); break;
    case 10: b(); break;
    case 100: c(); break;
}
```

```bytecode
lookupswitch
   1: 20
   10: 28
   100: 36
   default: 44
```

`lookupswitch` is O(log n) — it uses binary search on sorted key-offset pairs.

### Compilation Heuristic

The compiler decides between `tableswitch` and `lookupswitch` based on *density*:
```
density = (max - min + 1) / (number of cases)
If density > 0.67 (approximately) → tableswitch
Else → lookupswitch
```

## 2. Switch Expressions vs Switch Statements

### Switch Expression (Java 14+)

```java
String result = switch (day) {
    case MONDAY, FRIDAY, SUNDAY -> "Weekend edge";
    case TUESDAY -> "Second day";
    default -> {
        int x = computeSpecial();
        yield "Other: " + x;
    }
};
```

Key differences from switch statements:
1. **Exhaustiveness**: Switch expressions must cover ALL possible values (or have a `default`)
2. **No fall-through**: `->` has implicit break, no `break` needed
3. **Return value**: Every branch produces a value (via `->` or `yield`)
4. **Scope**: Variables in `->` branches are scoped to the branch

### Switch Expression Bytecode

The compiler transforms switch expressions into equivalent switch statements with synthetic variables:

```java
// Source:
String s = switch (x) {
    case 1 -> "one";
    case 2 -> "two";
    default -> "other";
};

// Desugared:
String s;
switch (x) {
    case 1: s = "one"; break;
    case 2: s = "two"; break;
    default: s = "other"; break;
}
```

## 3. Pattern Matching in Switch (Java 17+ / 21)

Java 21 (JEP 441) allows patterns in case labels:

```java
Object obj = getObject();
String result = switch (obj) {
    case Integer i -> "int: " + i;
    case String s when s.length() > 0 -> "non-empty string: " + s;
    case String s -> "empty string";
    case null -> "null!";
    default -> "unknown: " + obj;
};
```

### Compilation Strategy

Pattern matching in switch is compiled using a combination of:
1. `instanceof` checks
2. Type casts
3. Guard evaluation (`when` clauses)

For the above:
```java
String result;
if (obj instanceof Integer i) {
    result = "int: " + i;
} else if (obj instanceof String s) {
    if (s.length() > 0) {
        result = "non-empty string: " + s;
    } else {
        result = "empty string";
    }
} else if (obj == null) {
    result = "null!";
} else {
    result = "unknown: " + obj;
}
```

### Dominance Checking

The compiler checks pattern *dominance* at compile time:
```java
switch (obj) {
    case CharSequence cs -> "charseq";
    case String s -> "string";  // ERROR: String is subtype of CharSequence
}
```

The second case is *dominated* by the first because `String` is a subtype of `CharSequence`. The compiler rejects this.

### Total Type Coverage (Exhaustiveness)

Records and sealed classes enable exhaustiveness checking:
```java
sealed interface Shape permits Circle, Square {}
record Circle(double r) implements Shape {}
record Square(double s) implements Shape {}

double area = switch (shape) {
    case Circle c -> Math.PI * c.r() * c.r();
    case Square s -> s.s() * s.s();
};
// No default needed — Shape is sealed with exact permits
```

## 4. Loop Compilation

### Basic for-loop
```java
for (int i = 0; i < 10; i++) {
    body();
}
```

Bytecode:
```
  iconst_0
  istore_1        // i = 0
L1:
  iload_1
  bipush 10
  if_icmpge L2    // if i >= 10, exit
  invokestatic body
  iinc 1, 1       // i++ (increment local var slot 1 by 1)
  goto L1
L2:
  return
```

### Enhanced for-loop (arrays)
```java
for (String s : array) { ... }
```

Desugars to indexed loop:
```
  aload array
  arraylength
  istore length
  iconst_0
  istore i
L1:
  iload i
  iload length
  if_icmpge L2
  aload array
  iload i
  aaload           // Load array element
  astore s
  // body...
  iinc i, 1
  goto L1
L2:
```

### Enhanced for-loop (Iterables)
```java
for (String s : collection) { ... }
```

Desugars to iterator loop:
```
  aload collection
  invokeinterface Iterable.iterator()
  astore it
L1:
  aload it
  invokeinterface Iterator.hasNext()
  ifeq L2
  aload it
  invokeinterface Iterator.next()
  checkcast String
  astore s
  // body...
  goto L1
L2:
```

## 5. JIT Branch Prediction

The C2 JIT compiler (also known as `server` compiler) performs profile-guided optimization:

### Branch Profiling

The interpreter collects branch statistics during warmup:
- **taken count**: How many times branch was taken
- **not taken count**: How many times it fell through
- **ratio**: Used to guide layout decisions

### Branch Probability

C2 annotations in code:
```java
if (likelyCondition) {  // HotSpot intrinsic: @ForceInline
    hotPath();
}
```

Methods like `java.lang.Long.numberOfLeadingZeros` use `@HotSpotIntrinsicCandidate` for architecture-specific optimizations.

### Loop Optimizations

1. **Loop unrolling**: Replicate loop body to reduce branch overhead
   ```java
   for (int i = 0; i < 4; i++) sum += a[i];
   // Unrolled:
   sum += a[0]; sum += a[1]; sum += a[2]; sum += a[3];
   ```

2. **Loop inversion**: Transform `while (cond) { body }` to `if (cond) do { body } while (cond);` — reduces branch count

3. **Loop unswitching**: Move invariant branches outside loops:
   ```java
   for (int i = 0; i < n; i++) {
       if (debug) log(i);  // debug is loop-invariant
       sum += a[i];
   }
   // Unswitched:
   if (debug) {
       for (int i = 0; i < n; i++) { log(i); sum += a[i]; }
   } else {
       for (int i = 0; i < n; i++) { sum += a[i]; }
   }
   ```

4. **Loop peeling**: Remove first few iterations for specialization

5. **Loop vectorization**: SIMD instructions via `SuperWord` optimization (auto-vectorization)

### Benchmark: Branch Prediction Impact

```
Benchmark                          Mode  Cnt   Score  Error  Units
BranchPrediction.randomBranch      avgt   5  45.32 ± 0.5  ns/op  (~90% mispredict)
BranchPrediction.patternBranch     avgt   5   8.12 ± 0.1  ns/op  (~5% mispredict)
BranchPrediction.steadyBranch      avgt   5   3.45 ± 0.1  ns/op  (~0% mispredict)
```

A mispredicted branch costs ~15-20 cycles on modern x86 (Skylake+). Steady branches (always taken or never taken) cost ~0-2 cycles.

## 6. Ternary Operator Compilation

```java
int max = (a > b) ? a : b;
```

Bytecode:
```
  iload_1        // a
  iload_2        // b
  if_icmple else // if a <= b, goto else
  iload_1        // a (the true value)
  goto merge
else:
  iload_2        // b
merge:
  istore_3       // store to max
```

The ternary operator always evaluates to a single value, placed on the operand stack. Both branches must produce compatible types.

## 7. Definite Assignment and Reachability

The JLS (§16) requires the compiler to prove variables are assigned before use:

```java
int x;
if (condition()) {
    x = 1;
} // ERROR: x might not be initialized

int y;
if (condition()) {
    y = 1;
} else {
    y = 2;
}
System.out.println(y); // OK: both branches assign
```

The Flow phase (`com.sun.tools.javac.comp.Flow`) performs this analysis using a boolean per-variable "assigned" flag propagated through the AST.

## 8. Control Flow in the Presence of Exceptions

Exception handling adds implicit control flow edges:

```java
try {
    int x = risky();
    int y = safe();
} catch (IOException e) {
    handle();
}
```

The exception table in bytecode:
```
Exception table:
 from    to  target type
    0     5     8   Class java/io/IOException
```

This creates additional control flow edges that the JIT must consider during compilation.

## 9. `assert` and Control Flow

Assertions add implicit control flow that's eliminated at runtime when disabled:

```java
assert x > 0 : "x must be positive";
```

The JVM's `-ea`/`-da` flags control a global boolean. The JIT sees whether assertions are enabled and eliminates the branch entirely when they're not.
