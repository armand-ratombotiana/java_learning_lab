# ⚠️ Java Basics - Common Pitfalls & Edge Cases

## Table of Contents
1. [Variable & Type Pitfalls](#variable--type-pitfalls)
2. [String Gotchas](#string-gotchas)
3. [Operator Traps](#operator-traps)
4. [Loop and Array Gotchas](#loop-and-array-gotchas)
5. [Type Conversion Dangers](#type-conversion-dangers)

---

## Variable & Type Pitfalls

### Pitfall 1: Uninitialized Local Variables
```java
public void buggyCode() {
    int count;
    System.out.println(count);  // ❌ COMPILE ERROR: Variable count might not have been initialized
}

// FACT: Instance variables are initialized, local variables are NOT
class MyClass {
    int instanceVar;            // ✅ Defaults to 0
    
    void method() {
        int localVar;           // ❌ Must initialize before use
        System.out.println(instanceVar);  // ✅ Prints 0
        // System.out.println(localVar);  // ❌ Compile error
    }
}
```

**Prevention:** Always initialize local variables before use, or use compiler warning flags

---

### Pitfall 2: Integer Overflow Goes Silent
```java
// This code compiles and runs but gives wrong answer!
int total = Integer.MAX_VALUE;  // 2147483647
int result = total + 1;         // Wraps to -2147483648!

System.out.println(total + " + 1 = " + result);  
// Output: 2147483647 + 1 = -2147483648  ❌ WRONG!

// Real-world bug location:
long totalCost = 0;
for (Order order : orders) {
    totalCost += order.getPrice();  // If totalCost becomes int, overflow!
}

// FIX:
long totalCost = 0L;  // Use long for safety
```

**When It Happens:** Addition, multiplication, or bit shifts
**Cost:** Money calculations, counters, indices - production bugs worth millions

---

### Pitfall 3: Floating-Point Precision Trap
```java
double a = 0.1;
double b = 0.2;
double sum = a + b;

System.out.println(sum);           // 0.30000000000000004 (wrong!)
System.out.println(sum == 0.3);    // false (wrong!)

if (sum == 0.3) {
    System.out.println("Equal");   // ❌ This NEVER prints
}

// FINANCIAL DISASTER SCENARIO:
public void payroll(double hourlyRate, double hours) {
    double pay = hourlyRate * hours;
    if (pay == 1000.00) {  // ❌ Will almost never be true!
        giveBonus();
    }
}

// FIX:
// For exact arithmetic: Use BigDecimal
BigDecimal bd1 = new BigDecimal("0.1");
BigDecimal bd2 = new BigDecimal("0.2");
System.out.println(bd1.add(bd2));  // 0.3 ✅ Exact

// For approximate comparison:
double epsilon = 1e-9;
if (Math.abs(sum - 0.3) < epsilon) {
    System.out.println("Pretty close");
}
```

**Impact:** Financial systems, scientific calculations, comparisons all broken

---

### Pitfall 4: Boolean is NOT int
```java
// This is VALID in C but INVALID in Java:
int flag = 1;
// if (flag) { }  // ❌ COMPILE ERROR in Java

// VALID Java:
if (flag != 0) { }      // ✅ Explicit comparison
if (flag > 0) { }       // ✅ Explicit comparison

boolean isSafe = true;
// int x = isSafe;  // ❌ COMPILE ERROR

// This Java-specific design prevents common C bugs:
int x = 0;
if (x = 5) { }  // ❌ COMPILE ERROR (C would assign then check)
if (x == 5) { } // ✅ Forced to use comparison operator
```

**Why This Helps:** Prevents accidental assignment in conditionals that plague C code

---

## String Gotchas

### Gotcha 1: String Interning Unpredictable
```java
String s1 = "hello";              // Literal, in pool
String s2 = "hello";              // Literal, from pool
String s3 = new String("hello");  // Heap object, not pooled

System.out.println(s1 == s2);     // true (same pool entry)
System.out.println(s1 == s3);     // false (different objects)
System.out.println(s1.equals(s3)); // true (same content)

// WHAT ABOUT THIS?
String a = "hel" + "lo";          // Compiler combines literals
String b = "hello";
System.out.println(a == b);       // true (compiler optimization)

// BUT THIS?
String x = "hel";
String y = "lo";
String z = x + y;                 // Runtime concatenation
String w = "hello";
System.out.println(z == w);       // false (z not pooled)
System.out.println(z.equals(w));  // true

// INTERN METHOD:
String intern_z = z.intern();     // Add to pool
System.out.println(intern_z == w); // true (now pooled)
```

**The Rule:** String literals are pooled, but constructed strings are not (unless interned)

---

### Gotcha 2: String "Replacement" Creates New Objects
```java
String text = "Java Java Java";
text.replace("Java", "Python");
System.out.println(text);         // "Java Java Java" (unchanged!)

// FIX:
text = text.replace("Java", "Python");
System.out.println(text);         // "Python Python Python" ✅

// JUNIOR DEVELOPER ERROR:
String email = "user@example.com";
email.toLowerCase();
sendEmail(email);                 // Sends uppercase! ❌

// CORRECT:
email = email.toLowerCase();
sendEmail(email);                 // ✅
```

**Mental Model:** String methods NEVER modify the original, they return new objects

---

### Gotcha 3: Null String Operations
```java
String s = null;
// System.out.println(s.length());        // ❌ NullPointerException
// System.out.println(s.equals("hi"));    // ❌ NullPointerException

// BUT THESE ARE SAFE:
System.out.println(s == null);             // ✅ true
System.out.println(null == null);          // ✅ true
System.out.println(Objects.equals(s, "")); // ✅ false (safe null check)

// SAFE PATTERN (Java 11+):
if (s != null && !s.isBlank()) {
    processString(s);
}

// OR (All Java versions):
if (s != null && !s.isEmpty()) {
    processString(s);
}
```

---

### Gotcha 4: StringBuilder vs String Performance
```java
// ❌ PERFORMANCE DISASTER
String result = "";
for (int i = 0; i < 10000; i++) {
    result = result + i;  // Creates 10000 string objects!
}

// ✅ CORRECT
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 10000; i++) {
    sb.append(i);
}
String result = sb.toString();

// Real impact:
// - String version: O(n²) - each concat copies entire previous string
// - StringBuilder version: O(n) - buffer reused
// - With n=100,000: String version slow, StringBuilder instant
```

---

## Operator Traps

### Trap 1: Integer Division Truncates
```java
// WRONG calculation:
int percentage = 5 / 100;         // 0 (truncated!)
System.out.println(percentage);   // 0, not 0.05

// CORRECT calculations:
double percentage = 5.0 / 100;    // 0.05 ✅
double percentage = (double) 5 / 100;  // 0.05 ✅
double percentage = 5 / 100.0;    // 0.05 ✅

// COMMON BUG in user score calculation:
int userScore = 150;
int maxScore = 200;
int percentage = (userScore / maxScore) * 100;  // Wrong! Always 0
int percentage = (userScore * 100) / maxScore;  // Correct

// Or better:
double percentage = (double) userScore / maxScore * 100;  // Clear intent
```

---

### Trap 2: Modulus with Negative Numbers
```java
System.out.println(7 % 3);        // 1
System.out.println(-7 % 3);       // -1 (not 2!)
System.out.println(7 % -3);       // 1
System.out.println(-7 % -3);      // -1

// The rule: Result has SAME SIGN as dividend (left operand)
// NOT the same sign as divisor

// To always get positive remainder:
public static int positiveMod(int a, int b) {
    return ((a % b) + b) % b;
}

System.out.println(positiveMod(-7, 3));   // 2 ✅
System.out.println(positiveMod(7, 3));    // 1 ✅

// Used in: Array wrapping, circular buffers
int nextIndex = (currentIndex + 1) % array.length;  // Safe wrap-around
```

---

### Trap 3: Increment Operators Are Confusing
```java
int x = 5;
int y = ++x;     // Pre-increment: increment THEN use
System.out.println(x + " " + y);  // 6 6

int a = 5;
int b = a++;     // Post-increment: use THEN increment
System.out.println(a + " " + b);  // 6 5

// IN EXPRESSIONS - Can be disasters:
int[] arr = {10, 20, 30};
int i = 0;
int sum = arr[i++] + arr[i++];  // Is this arr[0] + arr[1] or arr[1] + arr[0]?
System.out.println(sum);         // 30 (arr[0] + arr[1])
System.out.println(i);           // 2

// BEST PRACTICE: Avoid increment in compound expressions
int[] arr = {10, 20, 30};
int i = 0;
int value1 = arr[i];   // Explicit steps
i++;
int value2 = arr[i];
i++;
int sum = value1 + value2;  // Clear what's happening
```

---

### Trap 4: Bitwise Operators Are Different From Logical
```java
int x = 5;    // 0101
int y = 3;    // 0011

// BITWISE operators (on bits):
System.out.println(x & y);       // 0001 = 1
System.out.println(x | y);       // 0111 = 7
System.out.println(x ^ y);       // 0110 = 6

// LOGICAL operators (on booleans):
boolean a = true;
boolean b = false;
System.out.println(a && b);      // false (AND)
System.out.println(a || b);      // true (OR)
System.out.println(a ^ b);       // true (XOR) - yes, ^ works on booleans too!

// MIXING THEM UP IS A COMPILE ERROR:
// System.out.println(x && y);    // ❌ Cannot apply && to int

// SHORT-CIRCUIT IMPORTANT:
int result = 0;
if (result != 0 && 10 / result > 2) {  // ✅ Won't divide by zero
    // ...
}

if (result != 0 & 10 / result > 2) {   // ❌ Single & doesn't short-circuit, will divide by zero!
    // ...
}
```

---

## Loop and Array Gotchas

### Gotcha 1: Off-by-One Errors
```java
String[] names = {"Alice", "Bob", "Charlie"};

// ❌ WRONG
for (int i = 0; i <= names.length; i++) {  // Uses <=
    System.out.println(names[i]);  // ArrayIndexOutOfBoundsException on last iteration
}

// ✅ CORRECT
for (int i = 0; i < names.length; i++) {
    System.out.println(names[i]);
}

// SAME MISTAKE WITH WHILE:
int i = 0;
while (i <= names.length) {  // ❌ Should be i < names.length
    System.out.println(names[i]);
    i++;
}
```

---

### Gotcha 2: ConcurrentModificationException
```java
List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C"));

// ❌ WRONG - throws ConcurrentModificationException
for (String item : list) {
    if (item.equals("B")) {
        list.remove(item);
    }
}

// ✅ CORRECT - use Iterator
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (it.next().equals("B")) {
        it.remove();  // Safe removal
    }
}

// ✅ ALSO CORRECT - use index loop
for (int i = list.size() - 1; i >= 0; i--) {
    if (list.get(i).equals("B")) {
        list.remove(i);  // Removing forward causes skips, backward is safe
    }
}
```

---

### Gotcha 3: Empty Arrays vs Null
```java
int[] emptyArray = new int[0];
int[] nullArray = null;

System.out.println(emptyArray.length);   // 0
// System.out.println(nullArray.length);  // ❌ NullPointerException

// CHECK PROPERLY:
if (emptyArray != null && emptyArray.length > 0) {
    int first = emptyArray[0];
}

// DANGEROUS - doesn't check empty:
if (emptyArray != null) {
    int first = emptyArray[0];  // Could crash if empty
}

// Using var or var-args:
boolean hasValues(int... values) {
    return values != null && values.length > 0;  // values never null, but can be length 0
}
```

---

## Type Conversion Dangers

### Danger 1: Implicit Widening Hides Bugs
```java
int x = 1000000;
int y = 1000000;
int z = x * y;  // ❌ Integer overflow! Should be long

long result = x * y;  // ❌ STILL OVERFLOW! Multiplication happens as int first
long result = (long) x * y;  // ✅ Cast before multiplication

// ALWAYS CAST FIRST:
long safeResult = x * y;  // false sense of security
long safeResult = 1L * x * y;  // ✅ True safety
```

---

### Danger 2: Narrowing Silently Loses Data
```java
double d = 300.7;
byte b = (byte) d;  // ❌ Silently truncates to 44, losing 300 and .7
System.out.println(b);  // 44 (not 300!)

// Double to int:
double height = 5.9;
int intHeight = (int) height;  // ❌ 5 (not 6, no rounding!)

// PROPER CONVERSION:
int roundedHeight = Math.round(height);  // ✅ 6
int truncatedHeight = (int) height;      // ✅ 5 (if intentional)

// MONEY CALCULATION DANGER:
double price = 19.99;
int cents = (int) (price * 100);  // Might not be exactly 1999!
// Use BigDecimal instead
```

---

### Danger 3: Character to int Conversion
```java
char c = '5';
int n = c;  // ❌ 53 (ASCII code), not 5!
System.out.println(n);  // 53

// CORRECT:
int n = c - '0';  // ✅ 5
int n = Character.getNumericValue(c);  // ✅ 5
int n = Integer.parseInt(String.valueOf(c));  // ✅ 5

// THE HARD WAY:
char c = 'A';
int asciiValue = (int) c;  // ✅ 65 (intentional ASCII conversion)
```

---

## Pitfall Prevention Checklist

| Issue | Prevention |
|-------|-----------|
| **Integer overflow** | Use long for large calculations, check boundaries |
| **Floating-point precision** | Use BigDecimal for financial, epsilon comparison for approximate |
| **String comparison** | Always use .equals() or .equalsIgnoreCase(), never == |
| **Null references** | Check with == null, use Objects.equals() for safe comparison |
| **String immutability** | Reassign: str = str.replace(...), not just str.replace(...) |
| **Uninitialized locals** | Initialize at declaration: int x = 0; |
| **Off-by-one array loops** | Use i < length not i <= length |
| **ConcurrentModification** | Use Iterator.remove(), not list.remove() in loops |
| **Division truncation** | Use double/float or multiply before dividing |
| **Modulus with negatives** | Understand sign follows dividend, not divisor |
| **Boolean as int** | Java enforces type safety - if it compiles, it's probably right |

