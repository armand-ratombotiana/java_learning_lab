# Deep Dive: JIT Compilation

## 1. C1 vs C2 Compilation Tiers

HotSpot's tiered compilation (default since Java 8) uses two compilers with different trade-offs:

### C1 (Client Compiler)

C1 compiles quickly but produces less optimized code:

```java
// C1 characteristics:
// - Single-pass compilation
// - No expensive optimizations (no loop unrolling, no vectorization)
// - No on-stack replacement (OSR) queue
// - High-level IR (HIR) → Low-level IR (LIR) → Register allocation → Code generation
// - Simple graph coloring for register allocation (linear scan)
// - 10-20x faster compilation than C2
// - Produces code that runs 3-5x slower than C2 code
```

**C1 compilation pipeline:**
1. Bytecode → HIR (high-level intermediate representation)
2. HIR optimizations: constant folding, copy propagation, dead code elimination
3. HIR → LIR (low-level IR, machine-level)
4. LIR optimizations: resolution of moves, simple peephole
5. Linear scan register allocation
6. Code generation (template-based)

### C2 (Server Compiler)

C2 is a heavyweight optimizing compiler derived from the ANSI C++ Columbia HPF compiler:

```java
// C2 characteristics:
// - Multi-pass Sea-of-Nodes IR
// - Global Value Numbering (GVN)
// - Loop optimizations: unrolling, vectorization, invariant code motion
// - Inlining (aggressive, up to a limit)
// - Escape analysis and scalar replacement
// - Intrinsic expansion
// - 100-500x slower compilation than C1
// - Produces highly optimized native code
```

**C2 compilation pipeline:**
1. Bytecode → Ideal Graph (Sea-of-Nodes IR)
2. Parsing with inlining decisions
3. Iterative global optimization (Ideal Phase):
   - Constant propagation
   - Global Value Numbering
   - Loop transformations (peeling, unrolling, vectorization)
   - Escape analysis
   - Conditional constant propagation
4. Matching (Ideal → MachNode)
5. Global code motion and scheduling
6. Graph coloring register allocation (Chaitin-Briggs style)
7. Peephole optimization
8. Code generation

```java
// -XX:+PrintIdealPhase -XX:+PrintOptoAssembly shows C2 IR
// The Sea-of-Nodes IR is a program dependence graph:
// - Nodes represent operations
// - Edges represent data and control dependencies
// - No basic block boundaries (global scheduling later)
public class C2OptimizationExample {
    // This method may be vectorized by C2:
    public static float sum(float[] a) {
        float sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i]; // C2 can auto-vectorize with SIMD
        }
        return sum;
    }
    // Without C2: loop with 4x unrolling + SSE packed single
    // With -XX:-UseSuperWord: scalar operations only
}
```

## 2. Tiered Compilation Levels

Tiered compilation uses 5 execution levels:

```
Level 0:  Interpreter
Level 1:  C1 with full optimization (no profiling)
Level 2:  C1 with invocation counters only
Level 3:  C1 with full profiling (all counters)
Level 4:  C2 (maximum optimization)
```

### Compilation Flow

**Normal (hot method):**
```
Level 0 → Level 3 → Level 4
```
The method is interpreted (L0), then compiled by C1 with profiling (L3) when it becomes hot enough, then recompiled by C2 (L4).

**Trivial method (inlined everywhere):**
```
Level 0 → Level 1
```
Skipped profiling and C2 compilation because the method is so simple it will be inlined into all callers.

**Warm method (moderately hot):**
```
Level 0 → Level 2 → Level 3 → Level 4
```
Interpreter → C1 (invocation only) → C1 (full profiling) → C2.

### Compilation Thresholds

```java
// -XX:CompileThreshold=10000 (default for tiered: varies by level)
// -XX:Tier0InvokeNotifyFreq=2
// -XX:Tier2InvokeNotifyFreq=15
// -XX:Tier3InvokeNotifyFreq=55
// -XX:Tier4InvokeThreshold=8500
// -XX:Tier3MinInvocationThreshold=400
// -XX:Tier3CompileThreshold=11000

// Each method has two counters:
// - invocation_counter: incremented at method entry
// - backedge_counter: incremented at loop backward branches
//
// Method is hot enough for compilation when:
// invocation_counter + backedge_counter > CompileThreshold
public class CompilationThresholds {
    // For this method, each recursive call is an invocation
    // but the loop back-edge also increments backedge_counter
    public static int hotMethod(int n) {
        int sum = 0;
        for (int i = 0; i < n; i++) { // back-edge increments counter
            sum += i * i; 
        }
        return sum;
    }
    
    // -XX:+PrintCompilation shows:
    // 187   34       3    CompilationThresholds::hotMethod (22 bytes)
    // 187   35       4    CompilationThresholds::hotMethod (22 bytes)
}
```

### Compilation Logs with PrintCompilation

```java
// -XX:+PrintCompilation output format:
// timestamp  compile_id  flags  tier  class::method (size)
// 
// Flags:
//   s = synchronized method
//   ! = has exception handlers
//   b = blocking (OSR compilation)
//   n = native method wrapper
//   % = OSR compilation
// 
// Example:
//  548  1       3    java.lang.String::hashCode (55 bytes)
//  548  2       3    java.lang.Object::<init> (1 bytes)
//  549  3       3    java.lang.Math::sin (12 bytes)   (intrinsic)
//  550  1       4    java.lang.String::hashCode (55 bytes)   made not entrant
//  551  4  %    3    CompilationThresholds::hotMethod @ 7 (22 bytes)
//
// "made not entrant" = C2 version replaced earlier C1 version
// "made zombie" = the code has been reclaimed
```

### Analyzing Compilation Logs Programmatically

```java
import java.io.*;
import java.util.regex.*;

public class CompilationLogAnalyzer {
    // Pattern for -XX:+PrintCompilation output
    private static final Pattern LOG_PATTERN = 
        Pattern.compile("(\\d+)\\s+(\\d+)\\s+([%s!bn ])\\s+(\\d)\\s+(\\S+)::(\\S+)\\s+\\((\\d+)\\s+bytes\\)");
    
    public static void analyze(InputStream logStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(logStream));
        String line;
        int totalCompilations = 0;
        int osrCount = 0;
        int intrinsicCount = 0;
        
        while ((line = reader.readLine()) != null) {
            Matcher m = LOG_PATTERN.matcher(line);
            if (m.find()) {
                totalCompilations++;
                long timestamp = Long.parseLong(m.group(1));
                int compileId = Integer.parseInt(m.group(2));
                String flags = m.group(3);
                int tier = Integer.parseInt(m.group(4));
                String className = m.group(5);
                String methodName = m.group(6);
                int byteSize = Integer.parseInt(m.group(7));
                
                if (flags.contains("%")) osrCount++;
                if (line.contains("(intrinsic)")) intrinsicCount++;
                
                System.out.printf("T%d %s::%s (%d bytes)%n", 
                    tier, className, methodName, byteSize);
            }
        }
        
        System.out.printf("%nSummary: %d compilations, %d OSR, %d intrinsics%n",
            totalCompilations, osrCount, intrinsicCount);
    }
    
    public static void main(String[] args) throws IOException {
        // Run: java -XX:+PrintCompilation -jar app.jar > compilation.log
        // Then: java CompilationLogAnalyzer < compilation.log
    }
}
```

## 3. On-Stack Replacement (OSR)

OSR allows the JVM to replace the version of a method currently executing on the stack.

### When OSR Happens

When a long-running loop is detected as hot, the method is compiled for OSR and the executing frame is patched:

```java
public class OSRExample {
    // Run with -XX:+PrintCompilation to see OSR events
    public static void longRunningLoop() {
        // The JIT detects this loop as hot
        // It triggers an OSR compilation at the loop back-edge
        // The loop entry point is compiled, not the entire method
        double sum = 0;
        for (int i = 0; i < 1_000_000; i++) {
            sum += Math.sin(i) * Math.cos(i);
            if (i % 100_000 == 0) {
                // Safepoint poll here
                System.out.println("Progress: " + i);
            }
        }
        System.out.println("Sum: " + sum);
    }
    // Output:
    //  125  189  %    3    OSRExample::longRunningLoop @ 7 (45 bytes)
    //  126  190       4    OSRExample::longRunningLoop (45 bytes)
    //  The % indicates OSR compilation at bytecode index 7
}
```

### OSR Mechanics

1. The JVM detects the loop back-edge counter exceeding the OSR threshold
2. C1 or C2 compiles the loop body starting from the OSR entry point (bytecode index)
3. The compiled code includes the stack frame layout expected at the OSR point
4. At the next safepoint, the interpreter frame is replaced with the compiled frame
5. Execution continues in compiled code

```java
// -XX:BackEdgeThreshold=100000 (default ~100k)
// -XX:OnStackReplacePercentage=140
// OSR threshold = CompileThreshold * (OnStackReplacePercentage / 100)
//
// For tiered compilation:
// - Tier3BackEdgeThreshold = 61000
// - Tier4BackEdgeThreshold = 22000
// 
// OSR is critical for:
// - Long-running loops in otherwise cold methods
// - Infinite loops (server event loops)
// - Computation-heavy inner loops

public class OSROptimization {
    // Without OSR, this loop would stay in the interpreter forever
    // because the method only runs once
    public static void main(String[] args) {
        double result = 0;
        for (int i = 0; i < 10_000_000; i++) {
            result += Math.sqrt(i) * Math.sin(i); // OSR at ~10k iterations
        }
        System.out.println(result);
    }
}
```

## 4. Uncommon Traps and Deoptimization

### Uncommon Traps

An **uncommon trap** is a point in compiled code that handles rare or unexpected situations by falling back to the interpreter:

```java
public class UncommonTrapExample {
    public static String classify(Object obj) {
        // The JIT profiles types at this instanceof check
        if (obj instanceof String s) {
            // Initially, C2 assumes obj is always String (monomorphic)
            // and inlines String.valueOf() directly
            if (s.length() > 100) {
                return "Long: " + s.length();
            }
            return s;
        }
        // This path is compiled with an uncommon trap
        // If control reaches here, deoptimization happens
        return "Unknown: " + obj.getClass().getName();
    }
    
    public static void main(String[] args) {
        // Phase 1: All calls pass Strings → C2 compiles for String-only
        for (int i = 0; i < 20000; i++) {
            classify("item-" + i);
        }
        // Phase 2: Now we pass an Integer → UNCOMMON TRAP!
        // The compiled code can't handle it → deoptimize → recompile
        classify(42);
        
        // Phase 3: C2 recompiles with bimorphic/megamorphic dispatch
    }
}
```

### Deoptimization Reasons

Deoptimization reasons (visible in `-XX:+UnlockDiagnosticVMOptions -XX:+PrintDeoptimizationDetails`):

```java
public class DeoptimizationReasons {
    // Common deopt reasons:
    // - "class_check" — type profile assumption violated (new subclass seen)
    // - "null_check" — null seen where non-null was assumed
    // - "range_check" — array index out of expected range
    // - "unreached" — control reached a path compiled as unreachable
    // - "uninitialized" — class not yet initialized
    // - "age" — code is old, recompiled by higher tier
    // - "bimorphic" → "megamorphic" — type profile changed
    // - "intrinsic" — intrinsic not applicable
    // - "not_compiled" — method wasn't compiled yet (OSR)
    
    // -XX:+PrintDeoptimizationStatistics shows frequency
    // -XX:+TraceDeoptimization shows individual events
    
    // A deoptimization has a cost:
    // 1. Current frame is "unpacked" back to interpreter format
    // 2. Execution resumes from the deopt point
    // 3. The recompilation trigger is set
    // 4. Method recompiled with broader assumptions
}
```

### Deoptimization Sequence

```java
public class DeoptSequence {
    // The deoptimization process:
    // 1. Trap instruction fires (e.g., uncommon trap in compiled code)
    // 2. VM calls Deoptimization::fetch_unroll_info()
    // 3. Stack is walked, interpreter frames reconstructed from oop maps
    // 4. The "unpack" phase: compiled frames are replaced
    // 5. Execution resumes in the interpreter
    // 6. The method's compile state is invalidated
    // 7. When re-profiled, the method is recompiled with new assumptions
    //
    // Cost: ~10-100 microseconds per deoptimization
}
```

## 5. Intrinsic Methods

Intrinsics are JVM-internal replacements for standard library methods. The JIT recognizes specific method signatures and replaces them with hand-optimized native code.

```java
// -XX:+PrintIntrinsics -XX:+UnlockDiagnosticVMOptions shows intrinsic usage
public class IntrinsicMethods {
    // Complete list of key intrinsics:
    
    // java.lang.Math:
    //   sin, cos, tan, sqrt, abs, floor, ceil, rint, exp, log, log10, pow
    //   → replaced with x87 SSE/AVX instructions (fsin, fcos, vsqrtpd, etc.)
    
    // java.lang.System:
    //   arraycopy(Object, int, Object, int, int)
    //   → replaced with hand-unrolled memmove or rep movsb
    
    // java.lang.String:
    //   String.hashCode() — C2 generates specialized code
    //   String.indexOf() — vectorized search with SSE4.2 PCMPEQB
    
    // java.lang.Thread:
    //   Thread.currentThread() — load from %fs:0x48 (or %gs:0x48 on Linux)
    
    // sun.misc.Unsafe:
    //   compareAndSwapObject, compareAndSwapInt, compareAndSwapLong
    //   → replaced with CMPXCHG instruction
    
    // java.lang.Object:
    //   Object.hashCode() — get from mark word (or call get_next_hash())
    
    // java.lang.ref.Reference:
    //   Reference.get() — special handling for GC
    
    // Integers:
    //   Integer.bitCount, Integer.numberOfLeadingZeros, Integer.reverse
    //   → replaced with POPCNT, LZCNT, BSWAP instructions
    
    // java.util.Arrays:
    //   Arrays.equals(...), Arrays.compare(...) — vectorized comparisons
    
    // java.lang.invoke.MethodHandleNatives:
    //   linkMethodHandleConstant, linkToVirtual, linkToStatic, linkToSpecial
    //   → replaced with direct method calls
}

// Benchmarking intrinsic performance
public class IntrinsicBenchmark {
    private static final int SIZE = 10_000_000;
    private static final int[] array = new int[SIZE];
    
    static {
        for (int i = 0; i < SIZE; i++) array[i] = i;
    }
    
    public static void main(String[] args) {
        // Bit operations: intrinsic vs manual
        long t0 = System.nanoTime();
        int sum = 0;
        for (int i = 0; i < SIZE; i++) {
            sum += Integer.bitCount(i); // intrinsic → POPCNT
        }
        long intrinsicTime = System.nanoTime() - t0;
        
        t0 = System.nanoTime();
        sum = 0;
        for (int i = 0; i < SIZE; i++) {
            sum += bitCountManual(i); // Java implementation
        }
        long manualTime = System.nanoTime() - t0;
        
        System.out.printf("Intrinsic: %dms, Manual: %dms%n",
            intrinsicTime / 1_000_000, manualTime / 1_000_000);
    }
    
    // The intrinsic replaces this with a single POPCNT instruction
    public static int bitCountManual(int i) {
        i = i - ((i >>> 1) & 0x55555555);
        i = (i & 0x33333333) + ((i >>> 2) & 0x33333333);
        i = (i + (i >>> 4)) & 0x0f0f0f0f;
        i = i + (i >>> 8);
        i = i + (i >>> 16);
        return i & 0x3f;
    }
}
```

## 6. Method Inlining

Method inlining is the single most important optimization in C2:

```java
public class InliningDeepDive {
    // -XX:+PrintInlining shows inlining decisions
    // -XX:MaxInlineLevel=9 (default)
    // -XX:MaxInlineSize=325 (default bytecode size for inlining)
    // -XX:FreqInlineSize=250 (for hot methods)
    // -XX:InlineSmallCode=4500 (don't inline into methods >4.5KB)
    
    public int topMethod(int a, int b) {
        // If both methods are hot and small enough:
        return mediumMethod(a, b); // Inlined: topMethod → mediumMethod → smallMethod
    }
    
    public int mediumMethod(int a, int b) {
        return smallMethod(a) + smallMethod(b); // Both inlined
    }
    
    public int smallMethod(int x) {
        return x * x + x * 2 + 1;
    }
    
    // After inlining, topMethod is effectively:
    // return (a * a + a * 2 + 1) + (b * b + b * 2 + 1);
    // Then constant-folded and simplified further
}

// Inlining decisions are logged with -XX:+PrintInlining:
// @ 16   App::smallMethod (14 bytes)   inline (hot)
// @ 22   App::mediumMethod (12 bytes)   inline (hot)
// @ 5    App::topMethod (8 bytes)   already compiled into a big blob
```

## 7. Escape Analysis and Scalar Replacement

Escape analysis determines object allocation lifetime:

```java
public class EscapeAnalysisDeepDive {
    // -XX:+PrintEscapeAnalysis shows EA results
    // -XX:+EliminateAllocations (default: true)
    // -XX:+EliminateLocks (default: true)
    // -XX:+EliminateNestedLocks (default: true)
    
    // NoEscape: Object is thread-local and doesn't escape
    public static long noEscape() {
        Point p = new Point(3, 4); // Allocated on stack or scalar replaced
        return p.x * p.x + p.y * p.y;
    }
    // After scalar replacement:
    // long x = 3, y = 4;  // No allocation!
    // return x * x + y * y;
    
    // ArgEscape: Object is passed as argument but not stored
    public static long argEscape(Calculator calc) {
        Point p = new Point(3, 4);
        return calc.compute(p); // p escapes via method call
    }
    
    // GlobalEscape: Object is stored in a field or returned
    public static Point globalEscape() {
        return new Point(3, 4); // Must allocate, returned to caller
    }
}

class Point {
    long x, y;
    Point(long x, long y) { this.x = x; this.y = y; }
}

// EA works through C2's Sea-of-Nodes IR:
// 1. Each Allocate node is analyzed for escape state
// 2. If NoEscape → replaced with individual field allocations (scalar replacement)
// 3. Lock elimination: synchronized(this) on non-escaping lock → removed
// 4. Array elimination: non-escaping new int[n] → replaced with local variables
```

## 8. Compilation Queues and Background Compilation

```java
// The compilation system:
// - CompileBroker manages the compilation queue
// - C1Queue: C1 compilations (FIFO priority)
// - C2Queue: C2 compilations (FIFO priority)  
// - C1 and C2 have separate compiler threads
// - -XX:CICompilerCount=8 (total compiler threads)
// - -XX:C1CompilerThreadQueueSize=100
// - -XX:C2CompilerThreadQueueSize=100

public class CompilationQueues {
    // Compilation request lifecycle:
    // 1. Method becomes hot → compilation task enqueued
    // 2. CompileBroker assigns to compiler thread
    // 3. Compiler thread compiles (may dequeue stale requests)
    // 4. Compiled code installed in CodeCache
    // 5. Method entry updated to point to compiled code
    // 
    // -XX:+PrintCompilationQueue shows queue state
    // -XX:+TraceCompilerThreads shows compiler activity
}
```

## 9. Profiled Method Data

C1 with profiling collects type profiles:

```java
// Profiled data stored in method's MDO (MethodDataObject):
// - Receiver type profiles (used for inlining decisions)
// - Branch probabilities
// - Type checks (instanceof, checkcast)
// - Argument type profiles at call sites
//
// -XX:TypeProfileWidth=2 (default: 2 types profiled per site)
// -XX:TypeProfileLevel=111 (profiling for all types)
//
// Type profiling is used by C2 to make optimistic inlining decisions:
// - If 99% of calls go to String.substring, inline that path
// - Add an uncommon trap for the 1% other case
```

## 10. Practical JIT Observation

```java
// Comprehensive JIT diagnostic flags:
// java -XX:+UnlockDiagnosticVMOptions \
//      -XX:+PrintCompilation \
//      -XX:+PrintInlining \
//      -XX:+PrintCodeCache \
//      -XX:+PrintIdealPhase \
//      -XX:+PrintOptoAssembly \
//      -XX:+PrintIntrinsics \
//      -XX:+TraceDeoptimization \
//      -XX:+PrintEscapeAnalysis \
//      -XX:+LogCompilation \
//      -XX:+PrintSafepointStatistics \
//      -XX:PrintSafepointStatisticsCount=1 \
//      -XX:+PrintGC \
//      -jar MyApp.jar

// For flame graphs of JIT activity:
// -XX:+UnlockDiagnosticVMOptions -XX:+LogCompilation -XX:+TraceClassLoading
// Then use JITWatch (https://github.com/AdoptOpenJDK/jitwatch) to visualize
```

This level of JIT understanding enables:
- Predicting which methods will be JIT compiled
- Designing methods that are good candidates for inlining
- Understanding warm-up time and how to measure it
- Diagnosing performance regressions from deoptimization storms
