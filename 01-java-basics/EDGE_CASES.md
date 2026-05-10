# Java Basics - Edge Cases

## Overview

This document covers tricky scenarios, boundary conditions, and common pitfalls in Java basics. Understanding these edge cases is crucial for writing robust code.

---

## 1. Integer Overflow and Underflow

### Problem
Integers have fixed bounds. Operations can exceed these bounds unexpectedly.

```java
public class IntegerOverflow {
    public static void main(String[] args) {
        int max = Integer.MAX_VALUE;
        System.out.println("Max int: " + max);           // 2147483647
        System.out.println("Max + 1: " + (max + 1));    // -2147483648 (overflow!)
        System.out.println("Max + 1L: " + (max + 1L));   // 2147483648 (correct!)
        
        int min = Integer.MIN_VALUE;
        System.out.println("Min int: " + min);           // -2147483648
        System.out.println("Min - 1: " + (min - 1));     // 2147483647 (underflow!)
    }
}
```

### Solutions

```java
// Check before operation
public static int safeAdd(int a, int b) {
    if (a > 0 && b > Integer.MAX_VALUE - a) {
        throw new ArithmeticException("Overflow");
    }
    if (a < 0 && b < Integer.MIN_VALUE - a) {
        throw new ArithmeticException("Underflow");
    }
    return a + b;
}

// Use BigInteger for large calculations
import java.math.BigInteger;
BigInteger bigA = BigInteger.valueOf(a);
BigInteger bigB = BigInteger.valueOf(b);
BigInteger result = bigA.add(bigB);
```

---

## 2. Floating-Point Precision

### Problem
Floating-point arithmetic can produce unexpected results due to binary representation.

```java
public class FloatingPointPrecision {
    public static void main(String[] args) {
        // Common issue
        System.out.println(0.1 + 0.2);  // 0.30000000000000004
        
        // Comparing floats
        double a = 1.0;
        double b = 0.1 * 10;
        System.out.println(a == b);  // true (by coincidence)
        
        // The problematic case
        double x = 1.1;
        double y = 0.1 * 11;
        System.out.println(x == y);  // false!
        
        // Currency calculation issue
        System.out.println(1.03 - 0.42);  // 0.6100000000000001
    }
}
```

### Solutions

```java
// Use BigDecimal for precise calculations
import java.math.BigDecimal;

public static BigDecimal safeCurrencyAdd(double a, double b) {
    BigDecimal ba = BigDecimal.valueOf(a);
    BigDecimal bb = BigDecimal.valueOf(b);
    return ba.add(bb);
}

// Compare with epsilon
public static boolean approximatelyEqual(double a, double b, double epsilon) {
    return Math.abs(a - b) < epsilon;
}

// Using String constructor for exact representation
BigDecimal exact = new BigDecimal("0.1");  // Exact!
```

---

## 3. Null Pointer Exceptions

### Problem
Accessing members on null references causes NPE.

```java
public class NPEExamples {
    public static void main(String[] args) {
        String str = null;
        System.out.println(str.length());  // NullPointerException!
        
        String[] arr = null;
        System.out.println(arr.length);     // NullPointerException!
        
        Integer num = null;
        int i = num;  // NullPointerException (unboxing)!
    }
}
```

### Solutions

```java
// Null check
if (str != null) {
    System.out.println(str.length());
}

// Optional for better handling
import java.util.Optional;
Optional<String> opt = Optional.ofNullable(str);
opt.ifPresent(s -> System.out.println(s.length()));

// Null-safe utilities
import org.apache.commons.lang3.StringUtils;
StringUtils.length(str);  // Returns 0 for null

// Java 14+ pattern matching
if (str instanceof String s) {
    System.out.println(s.length());  // Pattern matched, no NPE
}
```

---

## 4. String Immutability

### Problem
Strings are immutable; modifications create new objects.

```java
public class StringImmutability {
    public static void main(String[] args) {
        String s = "hello";
        s.concat(" world");  // Creates new String, but doesn't modify s!
        System.out.println(s);  // Still prints "hello"
        
        // The pitfall
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append(i);  // This works
        }
        
        // But with String concatenation in loop
        String result = "";
        for (int i = 0; i < 10000; i++) {
            result += i;  // Creates 10000 String objects! Very slow!
        }
    }
}
```

### Solutions

```java
// Use StringBuilder for concatenation
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 10000; i++) {
    sb.append(i);
}
String result = sb.toString();

// Or StringWriter
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
for (int i = 0; i < 10000; i++) {
    pw.println(i);
}
String output = sw.toString();
```

---

## 5. Array Covariance (Type Safety)

### Problem
Arrays in Java are covariant, which can cause runtime errors.

```java
public class ArrayCovariance {
    public static void main(String[] args) {
        // Integer extends Number
        Integer[] ints = new Integer[5];
        Number[] numbers = ints;  // Allowed! (arrays are covariant)
        
        numbers[0] = 3.14;  // Compiles! But throws ArrayStoreException at runtime!
        
        // This compiles but fails at runtime
        Object[] objects = new String[5];
        objects[0] = 123;  // ArrayStoreException!
    }
}
```

### Solutions

```java
// Be aware of array types when assigning
Number[] numbers = new Number[5];  // Safe
numbers[0] = 3.14;  // OK
numbers[1] = 42;    // OK

// Use generics (List) for type safety
List<Number> list = new ArrayList<>();  // Generic list
list.add(3.14);  // OK
list.add(42);    // OK
// list.add("text");  // Compile error!
```

---

## 6. Enhanced For Loop Modification

### Problem
You cannot modify a collection while iterating with enhanced for-loop.

```java
import java.util.ArrayList;
import java.util.List;

public class ConcurrentModification {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        
        // This will throw ConcurrentModificationException!
        for (String s : list) {
            if (s.equals("b")) {
                list.remove(s);  // Exception!
            }
        }
    }
}
```

### Solutions

```java
// Use Iterator explicitly
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    String s = it.next();
    if (s.equals("b")) {
        it.remove();  // Safe!
    }
}

// Use removeIf (Java 8+)
list.removeIf(s -> s.equals("b"));

// Collect elements to remove, then remove
List<String> toRemove = new ArrayList<>();
for (String s : list) {
    if (s.equals("b")) toRemove.add(s);
}
list.removeAll(toRemove);

// Iterate backwards
for (int i = list.size() - 1; i >= 0; i--) {
    if (list.get(i).equals("b")) {
        list.remove(i);
    }
}
```

---

## 7. Autoboxing Pitfalls

### Problem
Autoboxing creates unnecessary objects and can cause subtle bugs.

```java
public class AutoboxingPitfalls {
    public static void main(String[] args) {
        // Performance issue
        Long sum = 0L;  // Creates wrapper object
        for (long i = 0; i < 1000000; i++) {
            sum += i;  // Creates new Long each iteration!
        }
        
        // Comparison pitfall
        Integer a = 127;
        Integer b = 127;
        System.out.println(a == b);  // true (cached)
        
        Integer c = 128;
        Integer d = 128;
        System.out.println(c == d);  // false (not cached!)
        
        // Unexpected behavior
        Integer x = 1;
        Integer y = new Integer(1);
        System.out.println(x == y);  // false!
    }
}
```

### Solutions

```java
// Use primitive types for performance
long sum = 0L;
for (long i = 0; i < 1000000; i++) {
    sum += i;  // No autoboxing!
}

// Use equals() for value comparison
System.out.println(c.equals(d));  // true

// Be explicit
Integer c = Integer.valueOf(128);
Integer d = Integer.valueOf(128);
```

---

## 8. Floating-Point Division

### Problem
Integer division truncates, causing unexpected results.

```java
public class IntegerDivision {
    public static void main(String[] args) {
        // What most people expect
        System.out.println(5 / 2);  // 2 (not 2.5!)
        
        // The common mistake
        double average = 85 / 3;  // 28.0 (not 28.33...)
        
        // In loops
        for (int i = 0; i < 10; i++) {
            double progress = i / 3;  // Always 0, 0, 1, 1, 2, 2...
            System.out.println(progress);
        }
    }
}
```

### Solutions

```java
// Cast to double
double result = (double) 5 / 2;  // 2.5

// Or make one operand double
double average = 85.0 / 3;  // 28.333...

// In loops
double progress = i / 3.0;  // Proper decimal division

// For formatting percentages
int correct = 7;
int total = 10;
double percentage = (double) correct / total * 100;
System.out.printf("%.2f%%", percentage);  // 70.00%
```

---

## 9. Variable Shadowing

### Problem
Local variables can shadow class fields.

```java
public class VariableShadowing {
    private String name = "Class field";
    
    public void print(String name) {  // Parameter shadows field!
        System.out.println(name);       // Prints parameter
        System.out.println(this.name); // Prints field
    }
    
    public void example() {
        int count = 10;
        if (true) {
            int count = 20;  // Compile error: variable already defined!
        }
    }
}
```

### Solutions

```java
public class Solution {
    private int value = 100;
    
    public void setValue(int value) {
        this.value = value;  // Use 'this' to access field
    }
    
    public void method() {
        int temp = value;  // Rename local variable
        // ... use temp ...
    }
}
```

---

## 10. Switch Statement Fall-Through

### Problem
Switch cases fall through by default without break.

```java
public class SwitchFallThrough {
    public static void main(String[] args) {
        int num = 2;
        switch (num) {
            case 1:
                System.out.println("One");
            case 2:
                System.out.println("Two");  // Prints
            case 3:
                System.out.println("Three");  // Also prints!
            default:
                System.out.println("Default");  // Also prints!
        }
        // Output: Two, Three, Default
    }
}
```

### Solutions

```java
// Always use break
switch (num) {
    case 1:
        System.out.println("One");
        break;
    case 2:
        System.out.println("Two");
        break;
    // ...
}

// Java 14+ use arrow syntax (no fall-through)
switch (num) {
    case 1 -> System.out.println("One");
    case 2 -> System.out.println("Two");
    case 3 -> System.out.println("Three");
    default -> System.out.println("Default");
}

// Group cases with fall-through (intentional)
switch (num) {
    case 1, 2, 3 -> System.out.println("Small");
    case 4, 5, 6 -> System.out.println("Medium");
}
```

---

## 11. Comparing Strings

### Problem
Using == for String comparison often fails.

```java
public class StringComparison {
    public static void main(String[] args) {
        String s1 = "hello";
        String s2 = "hello";
        String s3 = new String("hello");
        
        System.out.println(s1 == s2);  // true (string pool)
        System.out.println(s1 == s3);  // false (different objects)
        System.out.println(s1.equals(s3));  // true (same content)
        
        // The tricky case
        String a = "hello";
        String b = "HELLO".toLowerCase();
        System.out.println(a == b);  // true (compiler optimizes)
        
        String c = new String("hello");
        String d = new String("hello");
        System.out.println(c == d);  // false
    }
}
```

### Solutions

```java
// Always use equals() for content comparison
if (s1.equals(s2)) { ... }

// For null-safe comparison
if ("hello".equals(s)) { ... }  // Never NPE

// Java 7+ Objects.equals()
import java.util.Objects;
Objects.equals(s1, s2);  // Null-safe

// Case-insensitive comparison
if (s1.equalsIgnoreCase(s2)) { ... }

// Use intern() for pooled reference
String pooled = s3.intern();
System.out.println(a == pooled);  // true
```

---

## 12. Short-Circuit Evaluation

### Problem
Reliance on side effects when second condition isn't evaluated.

```java
public class ShortCircuitDemo {
    public static void main(String[] args) {
        // The dangerous pattern
        if (denominator != 0 && x / denominator > 5) {
            // Safe because denominator != 0 is checked first
        }
        
        // But this fails:
        if (str != null & str.isEmpty()) {  // | instead of ||
            // str.isEmpty() is ALWAYS called, even if str is null!
        }
        
        // The tricky one
        boolean result = methodA() || methodB();  // methodB() may never run!
    }
}
```

### Solutions

```java
// Always use && and || for short-circuit evaluation
if (str != null && !str.isEmpty()) { ... }

// If you need to ensure both sides run, use & or |
boolean x = methodA() | methodB();  // Both always run

// Be careful with side effects
// Don't do this:
if (list != null && list.add(item)) { ... }
// list.add() only runs if list != null, but that's OK here
```

---

## Summary Checklist

| Edge Case | Prevention |
|-----------|------------|
| Integer overflow | Use BigInteger or check bounds |
| Floating-point precision | Use BigDecimal for money |
| NullPointerException | Null checks, Optional, Objects.requireNonNull |
| String immutability | Use StringBuilder |
| Array covariance | Use generics (List) |
| ConcurrentModification | Use Iterator.remove() or removeIf() |
| Autoboxing | Use primitives when possible, equals() for comparison |
| Integer division | Cast to double |
| Variable shadowing | Use this keyword, rename variables |
| Switch fall-through | Use break or arrow syntax |
| String comparison | Use equals(), not == |
| Short-circuit | Use correct operator (&& vs &) |
