# Bytecode and JIT Internals

## 🔍 The Structure of a .class File
If you open a compiled `.class` file in a hex editor, you will see it always starts with the magic number `CA FE BA BE`. 
The file contains the Constant Pool (strings, class names), Field info, Method info, and the actual Bytecode instructions.

Bytecode instructions are 1-byte opcodes. For example:
- `aload_0`: Load a reference from local variable 0 onto the stack.
- `iadd`: Add two integers on the stack.
- `invokevirtual`: Invoke an instance method.

## 🚀 Tiered Compilation
Modern JVMs (Java 8+) use **Tiered Compilation**. They don't just have one JIT compiler; they have two, which work together.

1. **Tier 0**: Interpreter. (Slowest execution, zero compilation time).
2. **Tier 1, 2, 3 (C1 Compiler)**: The "Client" compiler. It compiles Bytecode into machine code very quickly, but it only applies basic optimizations. It also injects "profiling" code to gather statistics about how the method is being used (e.g., "Is this `if` statement always true?").
3. **Tier 4 (C2 Compiler)**: The "Server" compiler. Once the C1 compiler has gathered enough profiling data, the C2 compiler takes over. It spends a lot of CPU time performing massive, aggressive optimizations based on the profiling data, producing the absolute fastest possible machine code.

## ✂️ JIT Optimizations: Inlining
The most powerful optimization the C2 compiler performs is **Method Inlining**.
Every time you call a method in Java, there is overhead (pushing a new frame onto the stack). If you have a simple getter method `public int getX() { return x; }`, the overhead of calling the method is more expensive than the actual code inside it.

When the JIT compiles the code, it takes the body of `getX()` and literally copies and pastes it directly into the caller's machine code, completely eliminating the method call overhead.

## 🔙 Deoptimization
Because the C2 compiler optimizes based on profiling data, it makes assumptions. 
For example, if it notices an `if (obj instanceof String)` has been true 10,000 times in a row, the C2 compiler will assume it is *always* true, completely remove the `if` check from the machine code, and compile it as if `obj` is guaranteed to be a String.

What happens if, on the 10,001st call, `obj` is an Integer?
The JVM detects that its assumption was violated. It immediately throws away the highly optimized C2 machine code, drops back down to the Interpreter (Tier 0), and starts profiling again. This is called **Deoptimization**.