# JIT Compiler Theory & Intuition

## 💡 The "Two-Phase" Execution
Java is neither purely interpreted nor purely compiled. It uses a hybrid approach to achieve the "Write Once, Run Anywhere" promise without sacrificing performance.

1. **Phase 1: `javac` (Static)**: Your `.java` code is compiled into platform-independent Bytecode (`.class` files). This happens before the program runs.
2. **Phase 2: JVM Execution (Dynamic)**: When the program runs, the JVM starts by **Interpreting** the bytecode. This is slow. However, the JVM identifies "Hot Spots"—methods that are called thousands of times.

## 🚀 The Just-In-Time (JIT) Magic
When a method becomes "Hot", the JIT compiler kicks in. It translates the bytecode of that specific method into **Native Machine Code** (optimized for your specific CPU, e.g., x86_64 or ARM64).

### Tiered Compilation
Modern JVMs use multiple levels of compilation:
- **Level 0**: Interpreter (No compilation overhead, slow execution).
- **Level 1-3 (C1 Compiler)**: Quick compilation with basic optimizations. Used to get the app running fast.
- **Level 4 (C2 Compiler)**: The "Server" compiler. It takes longer to compile but applies aggressive, high-level optimizations (Inlining, Escape Analysis). This is where Java matches or beats C++ performance.

## 📉 Why Interpreter First?
Why doesn't Java just compile everything to machine code at the start (AOT)?
1. **Startup Time**: Compiling millions of lines of code into machine code takes a long time.
2. **Profiling Data**: The JIT has a "superpower" that static compilers (like C++) don't: **Dynamic Profiling**. The JIT watches how your code actually runs. If it sees that an `if` branch is never taken, it can optimize the machine code to assume it's never taken, making it much faster. If the assumption fails later, it simply "Deoptimizes" and tries again.