# Refactoring for JIT Compilation

## From Megamorphic to Monomorphic
**Before:** Interface with multiple implementations:
```java
List<Task> tasks = List.of(new FastTask(), new SlowTask(), new OtherTask());
for (Task t : tasks) t.execute(); // megamorphic
```
**After:** Sealed interface or final classes:
```java
sealed interface Task permits FastTask, SlowTask {}
// Or use value-based dispatch
```

## From Object Allocation to Primitives
**Before:** Point object in hot loop:
```java
for (...) { Point p = new Point(x, y); sum += p.x + p.y; }
```
**After (or trust EA):** Let escape analysis handle it:
```java
for (...) { sum += x + y; } // No allocation at all
```

## From Manual Array Copy to System.arraycopy
**Before:**
```java
for (int i = 0; i < src.length; i++) dst[i] = src[i];
```
**After:**
```java
System.arraycopy(src, 0, dst, 0, src.length);
```

## From Deep Call Chain to Flat Code
**Before:** Deep method calls:
```java
result = parse(validate(normalize(input)));
```
**After (if possible):** Inline or flatten:
```java
String n = input.trim().toLowerCase();
validate(n);
result = parse(n);
```

## From Virtual to Static Dispatch
**Before:** Instance method (virtual):
```java
calculator.compute(value);
```
**After (if single implementation):** Static method or final class:
```java
Calculator.compute(value); // invokestatic, not invokevirtual
```

## From Conditional to Branchless Code
**Before:**
```java
result = condition ? a : b; // requires branch
```
**After:**
```java
// Use Math.max/min or conditional move via bit operations
result = b ^ (a ^ b) & (-(condition ? 1 : 0));
```
The JIT may already optimize ternaries to conditional moves (CMOV) on x86.
