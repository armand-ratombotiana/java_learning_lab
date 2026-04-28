# 🔬 Java Basics - Deep Dive Guide

## Table of Contents
1. [Variables & Memory](#variables--memory)
2. [Data Types in Detail](#data-types-in-detail)
3. [Type Conversion & Coercion](#type-conversion--coercion)
4. [Operators Deep Analysis](#operators-deep-analysis)
5. [String Immutability & Internals](#string-immutability--internals)
6. [Control Flow Advanced Patterns](#control-flow-advanced-patterns)

---

## Variables & Memory

### Memory Layout in Java

```
┌─────────────────────────────────────────────────────────────┐
│                        JVM Memory                           │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌───────────────┐  ┌────────────────────┐                 │
│  │     Stack     │  │      Heap          │                 │
│  ├───────────────┤  ├────────────────────┤                 │
│  │ Local vars    │  │ Objects            │                 │
│  │ Primitives    │  │ String instances   │                 │
│  │ References    │  │ Arrays (objects)   │                 │
│  │               │  │                    │                 │
│  │ Thread-local  │  │ Shared across      │                 │
│  │ LIFO order    │  │ threads, GC        │                 │
│  └───────────────┘  └────────────────────┘                 │
│                                                             │
│  ┌───────────────────────────────────────┐                 │
│  │  Method Area / Metaspace              │                 │
│  │  - Class definitions                  │                 │
│  │  - Method code                        │                 │
│  │  - Runtime constant pool               │                 │
│  └───────────────────────────────────────┘                 │
└─────────────────────────────────────────────────────────────┘
```

### Variable Scope Rules

**LIFO (Last-In-First-Out) Stack Behavior:**

```java
public void demonstrateScope() {
    int x = 10;        // LEVEL 1 - method scope
    {
        int y = 20;    // LEVEL 2 - block scope
        {
            int z = 30; // LEVEL 3 - inner block scope
            System.out.println(x + y + z); // ✅ All visible
        }
        // z is OUT OF SCOPE here
        System.out.println(x + y);      // ✅ x, y visible
        // System.out.println(z);  // ❌ COMPILE ERROR
    }
    // y is OUT OF SCOPE here
    System.out.println(x);              // ✅ Only x visible
}
```

### Memory Lifecycle Visualization

```
int x = 5;           // Stack: x @ 0x1000 = 5
String s = "hello";  // Stack: s @ 0x2000 → Heap: "hello" @ 0x5000

{
    int y = 10;      // Stack: y @ 0x1004 = 10
}
// y is deallocated, stack pointer resets
// x and s remain
```

### Why Variable Scope Matters?
- **Stack memory is automatic**: Variables are freed when scope ends
- **Heap memory needs GC**: Unreferenced objects are garbage collected
- **Shadowing is legal but dangerous**:

```java
int count = 5;  // Outer scope
{
    int count = 10;  // Inner scope shadows outer
    System.out.println(count);  // Prints 10
}
System.out.println(count);      // Prints 5
```

---

## Data Types in Detail

### Primitive vs Reference Types - The Core Difference

```
┌─────────────────────────────────────────────────────────────┐
│                   PRIMITIVE TYPES                           │
├─────────────────────────────────────────────────────────────┤
│ Variable stores:  THE ACTUAL VALUE                          │
│ Memory:          Allocated on Stack                         │
│ Size:            Fixed                                      │
│ Default:         0, false, or '\u0000'                      │
│ Comparison:      == compares VALUE                          │
│                                                             │
│  int x = 5;     // x contains 5                             │
│  int y = 5;     // y contains 5                             │
│  x == y;        // true (same VALUE)                        │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                   REFERENCE TYPES                           │
├─────────────────────────────────────────────────────────────┤
│ Variable stores:  A MEMORY ADDRESS (reference)              │
│ Memory:          Reference on Stack, Object on Heap         │
│ Size:            Variable                                   │
│ Default:         null                                       │
│ Comparison:      == compares REFERENCE (address)            │
│                                                             │
│  String s1 = new String("5");  // s1 = 0x5000              │
│  String s2 = new String("5");  // s2 = 0x6000              │
│  s1 == s2;                      // false (different refs)    │
│  s1.equals(s2);                 // true (same content)      │
└─────────────────────────────────────────────────────────────┘
```

### The 8 Primitive Types - In-Depth

#### Integer Types (4 types)
```
┌─────────┬────────┬──────────────────────────────────────────┐
│  Type   │ Bits   │ Range                                    │
├─────────┼────────┼──────────────────────────────────────────┤
│ byte    │ 8      │ -128 to 127                              │
│ short   │ 16     │ -32,768 to 32,767                        │
│ int     │ 32     │ -2,147,483,648 to 2,147,483,647          │
│ long    │ 64     │ -9,223,372,036,854,775,808 to ...        │
└─────────┴────────┴──────────────────────────────────────────┘

// Integer overflow behavior (CRUCIAL!)
int max = Integer.MAX_VALUE;      // 2147483647
int overflow = max + 1;           // -2147483648 (wraps around!)

long safeLong = (long) max + 1;   // 2147483648 (correct)
```

#### Floating-Point Types - IEEE 754 Standard
```java
// float: 32-bit, 7 significant digits
float f = 3.14159265f;       // Precision loss: 3.1415927
System.out.println(f);       // 3.1415927

// double: 64-bit, 15-17 significant digits  
double d = 3.14159265358979; // Full precision maintained
System.out.println(d);       // 3.14159265358979

// Special values
float inf = Float.POSITIVE_INFINITY;
float nan = Float.NaN;
double zero = 0.0;
double negZero = -0.0;

System.out.println(zero == negZero);      // true
System.out.println(1.0 / zero);           // Infinity
System.out.println(1.0 / negZero);        // -Infinity
System.out.println(0.0 / 0.0);            // NaN
```

#### boolean - More Than True/False
```java
// boolean is NOT interchangeable with int (unlike C)
boolean flag = true;
// int count = flag;  // ❌ COMPILE ERROR!
// if (1) { }         // ❌ COMPILE ERROR!

// This prevents common bugs from C-style code
if (flag) { }         // ✅ Correct
if (flag == true) { } // ✅ Also correct (but redundant)

// Logical short-circuit evaluation
boolean a = false;
boolean b = someExpensiveCall();  // NOT called because a is false
if (a && b) { }       // && short-circuits on first false

boolean x = true;
boolean y = someExpensiveCall();  // IS called
if (x && y) { }       // && still evaluates y
```

---

## Type Conversion & Coercion

### The Conversion Hierarchy

```
    byte     short    char
       \      |       /
        \     |      /
         \    |     /
              int
              |
            long
            |
         float
            |
         double

Arrow Direction: Implicit (automatic) conversion is safe
Reverse: Explicit (casting) may lose precision
```

### Widening vs Narrowing

**Widening (Safe - Automatic)**
```java
byte b = 100;
short s = b;        // ✅ Auto-widened
int i = s;          // ✅ Auto-widened
long l = i;         // ✅ Auto-widened
float f = l;        // ✅ Auto-widened
double d = f;       // ✅ Auto-widened

// No data loss guaranteed (except float/double precision with long)
```

**Narrowing (Unsafe - Requires Explicit Cast)**
```java
double d = 100.5;
float f = (float) d;          // ✅ Explicit cast required
int i = (int) f;              // ✅ Explicit cast required, decimal lost!
byte b = (byte) i;            // ✅ Explicit cast required

// Data WILL be lost silently
System.out.println(i);        // 100 (0.5 is gone)
System.out.println((byte) 300); // 44 (overflow wraps around)
```

### Assignment Conversion Context

```java
int result = 10 + 5.5;  // 10 (int) + 5.5 (double) = 15.5 (double) 
                        // then narrowed to int 15

// Compiler allows because it's assignment context
int x = 100_000_000;    // ✅ literal fits in int

// int y = 100_000_000_000;  // ❌ ERROR: literal needs L suffix
long z = 100_000_000_000L;   // ✅ Correct
```

---

## Operators Deep Analysis

### Operator Precedence & Associativity

```
PRECEDENCE (High to Low)           ASSOCIATIVITY
1. ++ -- (postfix)                 Left-to-Right
2. ++ -- + - (prefix/unary)        Right-to-Left
3. * / %                           Left-to-Right
4. + -                             Left-to-Right
5. << >> >>>                       Left-to-Right
6. < > <= >= instanceof            Left-to-Right
7. == !=                           Left-to-Right
8. &                               Left-to-Right
9. ^                               Left-to-Right
10. |                              Left-to-Right
11. &&                             Left-to-Right (short-circuit)
12. ||                             Left-to-Right (short-circuit)
13. ? :                            Right-to-Left
14. = += -= *= /= %=               Right-to-Left
```

### Critical Operator Behaviors

**Prefix vs Postfix**
```java
int x = 5;
System.out.println(++x);  // Prints 6 (incremented THEN used)
System.out.println(x);    // 6

int y = 5;
System.out.println(y++);  // Prints 5 (used THEN incremented)
System.out.println(y);    // 6

// In arrays: CRITICAL difference
int[] arr = {1, 2, 3};
int i = 0;
System.out.println(arr[i++]);  // 1, then i becomes 1
System.out.println(arr[++i]);  // 3, then i becomes 2
```

**Integer Division Truncates (Doesn't Round)**
```java
System.out.println(7 / 2);        // 3 (not 3.5!)
System.out.println(7 / 2.0);      // 3.5 (one operand is double)
System.out.println((double) 7 / 2); // 3.5 (explicit cast)

// Common mistake:
int percentage = 5 / 100;        // 0 (truncated to 0)!
double percentage = 5.0 / 100;   // 0.05 (correct)
```

**Modulus with Negative Numbers**
```java
System.out.println(7 % 3);       // 1
System.out.println(-7 % 3);      // -1 (sign follows dividend, not divisor!)
System.out.println(7 % -3);      // 1 (sign follows dividend)
System.out.println(-7 % -3);     // -1

// To always get positive remainder:
int mod = ((a % b) + b) % b;     // Handles negatives correctly
```

**Bitwise Operations**
```java
// AND: Both bits must be 1
int a = 5;        // 0101
int b = 3;        // 0011
System.out.println(a & b);        // 0001 = 1

// OR: At least one bit must be 1
System.out.println(a | b);        // 0111 = 7

// XOR: Bits must be different
System.out.println(a ^ b);        // 0110 = 6

// Bit shift
System.out.println(5 << 1);       // 10 (multiply by 2)
System.out.println(5 >> 1);       // 2 (divide by 2)
System.out.println(-5 >> 1);      // -3 (arithmetic shift, sign extends)
System.out.println(-5 >>> 1);     // Large positive (logical shift, fills zeros)
```

---

## String Immutability & Internals

### String Pool Architecture

```
┌──────────────────────────────────────────┐
│        String Constant Pool              │
│     (Part of Runtime Constant Pool)      │
├──────────────────────────────────────────┤
│                                          │
│  "hello"  ──→ 0x5000                    │
│  "world"  ──→ 0x5100                    │
│  "test"   ──→ 0x5200                    │
│  "hello"  ──→ 0x5000 (same reference!)  │
│                                          │
└──────────────────────────────────────────┘

String pool reduces memory for duplicate literals
But constructed strings go to heap:

String s1 = "hello";           // From pool → 0x5000
String s2 = "hello";           // From pool → 0x5000
String s3 = new String("hello"); // New heap object → 0x6000

s1 == s2    // true  (same reference from pool)
s1 == s3    // false (different objects)
s1.equals(s3) // true (same content)
```

### String Immutability - Why It Matters

```java
String original = "Java";
String modified = original.concat(" Programming");

// What happens:
// 1. original → 0x5000 → "Java" (unchanged!)
// 2. modified → 0x6000 → "Java Programming" (new object)

String result = original.toUpperCase();
// original → "Java" (unchanged!)
// result → "JAVA" (new object, even though all uppercase)

// MENTAL MODEL:
// String methods NEVER modify the original
// They return a NEW string (or the same string if nothing changes)

// This is why String concatenation in loops is expensive:
String s = "";
for (int i = 0; i < 1000; i++) {
    s = s + i;  // Creates 1000 intermediate String objects!
}

// Solution: Use StringBuilder (mutable, efficient)
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append(i);  // Reuses same internal buffer
}
String result = sb.toString();
```

### String Equality - The Most Common Mistake

```java
String input = getUserInput();     // "123" from user

// WRONG
if (input == "123") { }            // May be false even if content matches!

// CORRECT
if (input.equals("123")) { }        // Compares content
if (input.equalsIgnoreCase("123")) { } // Case-insensitive

// WHY it matters:
String s1 = "hello";               // Pool
String s2 = "hel" + "lo";          // Compiler optimizes to pool
String s3 = "hel" + new String("lo");  // One part is from pool, one from heap!
                                    // Result: Heap object, not in pool

s1 == s2;  // true (compiler optimized to same pool entry)
s1 == s3;  // false (result not in pool due to new String())
s1.equals(s3); // true (content comparison always works)
```

---

## Control Flow Advanced Patterns

### Switch Expression vs Statement (Java 17+)

```java
// OLD: Switch Statement (Java 1.0+)
String result;
switch (day) {
    case "Monday":
        result = "Start of week";
        break;        // MUST include or falls through!
    case "Friday":
        result = "Almost weekend";
        break;
    default:
        result = "Mid-week";
}

// NEW: Switch Expression (Java 17+) - More concise and safer
String result = switch (day) {
    case "Monday" -> "Start of week";
    case "Friday" -> "Almost weekend";
    default -> "Mid-week";
    // No break needed, automatically doesn't fall through
};

// Can return from switch:
int priority = switch (severity) {
    case CRITICAL -> 1;
    case HIGH -> 2;
    case MEDIUM -> 3;
    case LOW -> 4;
};

// Pattern matching in switch (Java 21+)
Object obj = "hello";
String message = switch (obj) {
    case String s -> "String: " + s;
    case Integer i -> "Number: " + i;
    case null -> "Null value";
    default -> "Other type";
};
```

### Loop Optimization Patterns

```java
// INEFFICIENT: Method call in condition
for (int i = 0; i < list.size(); i++) {  // size() called each iteration!
    System.out.println(list.get(i));
}

// EFFICIENT: Cache the size
for (int i = 0, size = list.size(); i < size; i++) {
    System.out.println(list.get(i));
}

// BEST: Use enhanced for-loop
for (String item : list) {
    System.out.println(item);
}

// ITERATOR for removal during iteration
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (shouldRemove(it.next())) {
        it.remove();  // Safe removal
    }
}
// vs list.remove() in for-loop: ConcurrentModificationException!
```

### Early Exit Patterns

```java
// Pattern 1: Guard clauses (preferred)
public void process(User user) {
    if (user == null) return;
    if (!user.isActive()) return;
    if (!hasPermission(user)) return;
    
    // Actual logic here - clearly separated
    doRealWork();
}

// Pattern 2: Nested ifs (harder to read)
public void process(User user) {
    if (user != null) {
        if (user.isActive()) {
            if (hasPermission(user)) {
                doRealWork();
            }
        }
    }
}

// Pattern 3: Logical AND (concise but less flexible)
if (user != null && user.isActive() && hasPermission(user)) {
    doRealWork();
}
```

---

## Key Takeaways

| Concept | Key Point |
|---------|-----------|
| **Variables** | Stack-based, LIFO cleanup; scope affects visibility |
| **Primitives** | Value types, 8 total, fixed size, auto-default values |
| **References** | Reference types, stored on heap, null default |
| **Widening** | Automatic, safe conversion (byte → int → long → double) |
| **Narrowing** | Requires explicit cast, data loss possible |
| **String Immutability** | Strings never change, operations create new objects |
| **String Equality** | Use `.equals()` not `==` for content comparison |
| **Operators** | Understand precedence, short-circuits, and overflow |
| **Control Flow** | Use guard clauses, prefer switch expressions, cache loop sizes |

