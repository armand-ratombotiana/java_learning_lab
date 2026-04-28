# 📌 Java Basics - Quick Reference Sheet

Use this sheet for quick lookups while coding or studying.

---

## 🧠 Memory Model Quick Chart

```
┌─────────────────────────────────────────────────┐
│             JVM MEMORY REGIONS                  │
├─────────────────────────────────────────────────┤
│ STACK (per thread)                              │
│ • Local variables (primitives & references)    │
│ • Method call info                             │
│ • LIFO (Last-In-First-Out)                     │
│ • Automatic cleanup                            │
│                                                 │
│ HEAP (shared)                                   │
│ • Objects                                       │
│ • Arrays (are objects!)                        │
│ • Strings                                       │
│ • Garbage collected                            │
│                                                 │
│ METHOD AREA / METASPACE                         │
│ • Class definitions                            │
│ • Method bytecode                              │
│ • Constant pool                                │
└─────────────────────────────────────────────────┘
```

---

## 8️⃣ Primitive Types Cheat Sheet

```java
// Integers
byte:   8-bit   [-128, 127]
short:  16-bit  [-32K, 32K]
int:    32-bit  [-2B, 2B]          ← Most common
long:   64-bit  [-9×10^18, 9×10^18] ← Add 'L' suffix: 100L

// Decimals
float:  32-bit  Single precision  ← Add 'f' suffix: 3.14f
double: 64-bit  Double precision  ← Default for decimals

// Logical
boolean: true/false  (NOT int!)

// Character
char: 16-bit  Unicode  (0-65535)    ← Single quotes: 'A'

// DEFAULT VALUES
int x;              // Instance: 0, Local: ERROR
long y;             // Instance: 0L, Local: ERROR
double z;           // Instance: 0.0, Local: ERROR
boolean flag;       // Instance: false, Local: ERROR
String s;           // Instance: null, Local: ERROR
```

---

## 🔄 Type Conversion Pyramid

```
        byte
        short ← can also convert from char
        int
        long
        float
        double

           WIDENING: Automatic ↓
           NARROWING: Requires cast (↑)

byte b = 5;
int i = b;              // ✅ Automatic widening

double d = 10.5;
int x = (int) d;        // ✅ Required cast (loses .5)
```

---

## ⚙️ Operator Precedence (High to Low)

```
1.  ++ -- (postfix)
2.  ++ -- ! ~ (prefix)
3.  * / %
4.  + -
5.  << >> >>>
6.  < > <= >= instanceof
7.  == !=
8.  &
9.  ^
10. |
11. &&  (short-circuit)
12. ||  (short-circuit)
13. ? :
14. = += -= *= /= %=

TIP: Use parentheses: (a + b) * c
```

---

## 📝 String Comparison Guide

```java
String s1 = "hello";
String s2 = "hello";
String s3 = new String("hello");

// NEVER USE FOR CONTENT
s1 == s2            // true  (same pool entry - luck!)
s1 == s3            // false (different objects - danger!)

// ALWAYS USE FOR CONTENT
s1.equals(s2)       // true
s1.equals(s3)       // true
s1.equalsIgnoreCase("HELLO")  // true

// FOR NULL-SAFE COMPARISON
Objects.equals(s1, s2)      // true
Objects.equals(s1, null)    // false

// FOR PREFIX/SUFFIX
s1.startsWith("he")         // true
s1.endsWith("lo")           // true
s1.contains("ll")           // true
```

---

## 🎯 Variable Scope at a Glance

```java
class Example {
    // Class scope - accessible everywhere in class
    private int classVar = 0;
    public static int staticVar = 1;
    
    public void method() {
        // Method scope - accessible only in method
        int methodVar = 2;
        
        {
            // Block scope - accessible only in block
            int blockVar = 3;
            // System.out.println(blockVar); after this block? ❌
        }
        // System.out.println(blockVar);  ❌ ERROR
        System.out.println(methodVar);   ✅
    }
}

// SHADOWING (Legal but confusing)
{
    int x = 1;
    {
        int x = 2;  // Shadows outer x
        System.out.println(x);  // 2
    }
    System.out.println(x);      // 1 (outer x unchanged)
}
```

---

## 🔴 Common Errors Quick Fix

| Error | Cause | Fix |
|-------|-------|-----|
| "Variable might not be initialized" | Local var not given value | `int x = 0;` |
| ArrayIndexOutOfBoundsException | Index >= array.length | Use `i < length` not `i <= length` |
| NullPointerException | Calling method on null | Check `if (x != null)` |
| "double cannot be converted to int" | Narrowing without cast | `int x = (int) d;` |
| ConcurrentModificationException | Modify list during iteration | Use `Iterator.remove()` |
| "boolean cannot be converted to int" | Using int as boolean | Use `if (x != 0)` not `if (x)` |

---

## ⚠️ Top 10 Gotchas

| # | Gotcha | Wrong | Right |
|---|--------|-------|-------|
| 1 | String == | `if (s == "hi")` | `if (s.equals("hi"))` |
| 2 | Integer overflow | `int big = 3000000000` | `long big = 3000000000L` |
| 3 | Float precision | `if (0.1 + 0.2 == 0.3)` | `if (Math.abs(sum - 0.3) < 1e-9)` |
| 4 | Int division | `int p = 5 / 100 * 100` | `int p = (5 * 100) / 100` |
| 5 | String immutability | `s.replace("a", "b");` | `s = s.replace("a", "b");` |
| 6 | Array bounds | `for (int i = 0; i <= arr.length; i++)` | `for (int i = 0; i < arr.length; i++)` |
| 7 | Modifying list in loop | `for (String s : list) list.remove(s);` | Use `Iterator.remove()` |
| 8 | Uninitialized local | `int x; System.out.println(x);` | `int x = 0;` |
| 9 | Boolean from int | `if (flag) {}` where flag=int | `if (flag != 0) {}` |
| 10 | Off-by-one | `new int[5]` has indices 0-4 | Remember: `arr[0]` to `arr[n-1]` |

---

## 🎯 Switch vs Switch Expression

```java
// OLD: Switch Statement
switch (day) {
    case 1:
        System.out.println("Monday");
        break;
    default:
        System.out.println("Other");
}

// NEW: Switch Expression (Java 17+)
String dayName = switch (day) {
    case 1 -> "Monday";
    case 2 -> "Tuesday";
    default -> "Other";
};  // Automatically returns value
```

---

## 📊 Operator Behavior Summary

```java
// INTEGER DIVISION - TRUNCATES
7 / 2       = 3      (not 3.5!)
-7 / 3      = -2
7 % 3       = 1
-7 % 3      = -1     (sign of dividend, not divisor!)

// INCREMENT OPERATORS
int x = 5;
y = ++x;    // y=6, x=6 (pre: increment THEN use)
z = x++;    // z=6, x=7 (post: use THEN increment)

// BITWISE Operations
5 & 3       = 1      (0101 & 0011 = 0001)
5 | 3       = 7      (0101 | 0011 = 0111)
5 ^ 3       = 6      (0101 ^ 0011 = 0110)
~5          = -6     (inverts all bits)
5 << 1      = 10     (multiply by 2)
5 >> 1      = 2      (divide by 2)

// SHORT CIRCUIT
false && anything        // Anything NOT evaluated
true || anything         // Anything NOT evaluated
```

---

## 🧪 Type Casting Rules

```
WIDENING (Safe - Automatic)
byte → short → int → long → float → double

NARROWING (Unsafe - Requires Cast)
double → float → long → int → short → byte

// Safest way to cast:
double d = 42.7;
int rounded = Math.round(d);      // ✅ Smart rounding
int truncated = (int) d;          // ✅ Intentional truncation
```

---

## 🎓 Method Behavior Quick Reference

```java
// VALUE passed (primitives)
public void change(int x) {
    x = 100;      // Changes local copy only
}
int y = 5;
change(y);        // y is still 5

// REFERENCE passed (objects)
public void change(List list) {
    list.add("item");  // Modifies actual list
}
List myList = new ArrayList();
change(myList);   // myList now has "item"
```

---

## 🔍 String Methods Cheat Sheet

```java
String s = "Hello World";

// Search
s.length()              = 11
s.indexOf("o")          = 4
s.lastIndexOf("o")      = 7
s.contains("World")     = true
s.startsWith("Hello")   = true
s.endsWith("ld")        = true

// Modify (return NEW string)
s.toUpperCase()         = "HELLO WORLD"
s.toLowerCase()         = "hello world"
s.concat(" 123")        = "Hello World 123"
s.replace("World", "Java") = "Hello Java"
s.substring(0, 5)       = "Hello"
s.trim()                = removes leading/trailing spaces
s.split(" ")            = ["Hello", "World"]

// Check
s.isEmpty()             = false
s.isBlank()             = false (Java 11+)
```

---

## 🚀 Performance Tips

| Operation | Good | Bad | Why |
|-----------|------|-----|-----|
| String concat | `StringBuilder` | Loop + string `+` | O(n) vs O(n²) |
| Loop size | Cache: `size = list.size()` | `list.size()` in loop | Avoid method calls |
| Array access | Sequential | Random jumps | Cache misses |
| List iteration | Enhanced for | Index if remove | ConcurrentModification |
| Null equal | `Objects.equals()` | `==` | Null-safe |

---

## 📚 References in This Guide

This quick reference complements:
- **DEEP_DIVE.md** - Deep understanding of each concept
- **QUIZZES.md** - Test your knowledge
- **EDGE_CASES.md** - Real-world pitfalls
- **JavaBasicsQuizzes.java** - Executable demonstrations

---

## ✋ Stop and Review Checklist

Before moving to OOP Concepts, make sure you can:

- [ ] Draw a stack and heap diagram
- [ ] List all 8 primitive types and their sizes
- [ ] Explain widening vs narrowing
- [ ] Explain why `String` needs `.equals()`
- [ ] Identify operator precedence by sight
- [ ] Explain variable scope and shadowing
- [ ] Write safe iteration (no ConcurrentModification)
- [ ] Describe integer division behavior
- [ ] Explain immutability consequence
- [ ] Handle all edge cases from EDGE_CASES.md

If you can do all of these, **you've mastered Java Basics!** ✅

