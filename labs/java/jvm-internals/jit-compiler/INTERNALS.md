# JIT Compiler Internals: C2 Optimizations

## 🔬 The C2 (Server) Compiler
While the C1 compiler focuses on quick startup, the C2 compiler is responsible for the "Peak Performance" that Java is famous for. It performs aggressive, high-level optimizations based on profiling data collected during the interpretation and C1 phases.

### 1. Inlining (The King of Optimizations)
Inlining is the process of replacing a method call with the actual body of the called method.
- **Why?**: Method calls in Java have overhead (stack frame creation, argument passing).
- **Impact**: Eliminates the overhead and, more importantly, enables other optimizations (like constant folding) to work across method boundaries.

### 2. Escape Analysis
The JIT determines the "scope" of an object. If an object is created inside a method and never "escapes" that method (e.g., it's not returned or passed to another thread):
- **Scalar Replacement**: The JVM can decompose the object into its individual fields and store them in CPU registers instead of allocating the object on the Heap.
- **Lock Elision**: If an object is only visible to one thread, the JVM can completely remove any `synchronized` blocks associated with it.

### 3. Loop Unrolling
The JIT modifies loops to reduce the number of iterations and branch instructions.
- **Original**: `for (int i=0; i<100; i++) { sum += a[i]; }`
- **Unrolled**: `for (int i=0; i<100; i+=4) { sum += a[i]; sum += a[i+1]; sum += a[i+2]; sum += a[i+3]; }`
- **Impact**: Reduces loop control overhead and allows the CPU to leverage instruction-level parallelism.

### 4. Intrinsics
The JVM replaces certain standard Java methods (like `System.arraycopy()` or `Math.sin()`) with hand-written, highly optimized assembly code specific to the target CPU.

## 🔙 Deoptimization (The Uncommon Trap)
If the JIT makes an optimistic optimization (e.g., assuming a class only has one subclass) and that assumption is later violated (e.g., a new JAR is loaded with a second subclass), the JVM must "Deoptimize".
1. It discards the optimized machine code.
2. It falls back to the Interpreter.
3. It restarts the profiling and compilation process.
This is why loading new classes at runtime can cause temporary performance dips.