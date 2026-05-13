# How Java Basics Works

## Compilation Pipeline

```
Source (.java) → Bytecode (.class) → JVM → Machine Code
     │                │              │         │
  javac           bytecode       interpreter/
  compiler                        JIT compiler
```

## Variable Declaration Process

1. **Declaration**: `int x;` - allocates space in stack
2. **Initialization**: `x = 5;` - stores value in that space
3. **Usage**: `System.out.println(x);` - reads value

## Control Flow Execution

### If-Else
```java
if (condition) {
    // evaluated only if true
} else {
    // evaluated only if false
}
```
The condition must evaluate to boolean (unlike C).

### Loops
```java
for (int i = 0; i < 10; i++) { }  // known iterations
while (condition) { }              // unknown iterations
do { } while (condition);          // at least once
```

## Method Execution

1. Call invokes method with arguments
2. Arguments copied to parameters (pass-by-value)
3. Method executes in new stack frame
4. Return value copied back to caller
5. Stack frame popped

## Array Memory Layout

```java
int[] arr = new int[5];
// Creates: array header + 5 * 4 bytes = 20 bytes in heap
// Reference stored in stack
```

## JVM Memory Areas

- **Stack**: Method calls, local variables
- **Heap**: Objects, arrays
- **Method Area**: Class definitions, static variables
- **PC Register**: Current instruction
- **Native Stack**: Native method calls