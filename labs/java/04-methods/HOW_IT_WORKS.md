# Methods — How It Works

## Step 1: Method Declaration

```java
public static int max(int a, int b)
```

The compiler generates a method descriptor: `(II)I` (two ints in, int out). This is stored in the class file's method pool.

## Step 2: Compilation to Bytecode

Method body compiles to bytecode instructions stored in the `Code` attribute of the class file:
```java
public int add(int a, int b) { return a + b; }
// Bytecode:
// iload_1    (load a)
// iload_2    (load b)
// iadd       (add)
// ireturn    (return int)
```

## Step 3: Calling a Method

1. Evaluate arguments left-to-right
2. Push each argument onto the operand stack
3. Invoke instruction: `invokevirtual`, `invokestatic`, `invokespecial`, `invokeinterface`, or `invokedynamic`

## Step 4: Stack Frame Creation

Each method call creates a stack frame with:
- Local variable array (including `this` for instance methods)
- Operand stack
- Reference to constant pool
- Return address

## Step 5: Execution

Method body executes within the frame. Local variables accessed by index:
- index 0: `this` (instance method) or nothing (static)
- index 1: first parameter
- index 2: second parameter
- ...then local variables

## Step 6: Return

1. Push return value (if any) onto caller's stack
2. Restore caller's frame as current
3. Continue execution at instruction after the call

## Varargs Mechanics

```java
void print(String... args) { }
// Called as: print("a", "b")
// Compiled as: print(new String[]{"a", "b"})
```

At each call site, the compiler generates an array creation. In the method, varargs is just a regular array parameter.
