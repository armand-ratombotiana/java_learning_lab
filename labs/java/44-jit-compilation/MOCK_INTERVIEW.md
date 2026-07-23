# Mock Interview Transcript: JIT Compilation

## Interviewer: Staff Engineer, Oracle
## Candidate: Senior Java developer
## Time: 45 minutes
## Focus: C1/C2, inlining, escape analysis, deoptimization

---

**Q1: How does the JVM determine when to compile a method?**

**Candidate**: The JVM uses invocation counters and back-edge counters. Each method call increments the invocation counter. Loop back-edges increment the back-edge counter. When the sum exceeds a threshold, the method is queued for compilation. In tiered mode: (1) Level 0 (interpreter) → at 0 threshold. (2) Level 3 (C1 with profiling) → at `-XX:Tier3InvocationThreshold` (default ~200). (3) Level 4 (C2) → at `-XX:Tier4InvocationThreshold` (default ~5000). Counters are half-life decay — methods that cool down aren't compiled.

**Interviewer**: Explain on-stack replacement (OSR).

**Candidate**: OSR compiles a loop that's running long enough to be compiled mid-execution. When the JVM detects a hot loop (high back-edge count), it compiles the method at the loop entry point. The next iteration jumps to the compiled version. This is why methods with long-running loops eventually speed up. OSR compilation is specific to the loop — it doesn't compile the entire method.

**Interviewer**: What methods are good candidates for inlining?

**Candidate**: (1) Small methods (< MaxInlineSize = 35 bytes). (2) Hot methods (< FreqInlineSize = 325 bytes, called frequently). (3) Methods called from a single call site (monomorphic). (4) Trivial methods (getters, simple delegation). (5) Methods with primitive argument/return types (no boxing). (6) Methods in the same class (easier analysis). The JIT stops inlining after reaching `-XX:MaxInlineLevel=9` default.

**Interviewer**: How does escape analysis determine if an object can be stack-allocated?

**Candidate**: The C2 compiler traces object references: (1) No Escape — object is local to method, never read by other threads. (2) ArgEscape — object is passed as argument but not stored in heap. (3) GlobalEscape — object is stored in heap or passed to unknown code. For NoEscape: stack allocation or scalar replacement (fields stored in registers). For ArgEscape: can't stack-allocate but can remove synchronization.

**Interviewer**: Show an example of scalar replacement via escape analysis.

**Candidate**: 
```java
int sum(int a, int b) {
    Point p = new Point(a, b);  // Point has int x, y
    return p.x + p.y;
}
```
C2 Escape Analysis: Point doesn't escape. Scalar replacement: allocate `x` and `y` in registers instead of creating a Point object. The method becomes `return a + b`. No heap allocation, no GC pressure.

**Interviewer**: What triggers deoptimization?

**Candidate**: Deoptimization reverts compiled code back to interpreted execution. Triggers: (1) Class loading that changes CHA (Class Hierarchy Analysis) assumptions. (2) Biased lock revocation (changing lock state). (3) Making a previously monomorphic call site megamorphic. (4) `-XX:+UnlockDiagnosticVMOptions -XX:+DeoptimizeAll` (for debugging). The deoptimization handler builds interpreter frames from the compiled code's state, then the method continues in the interpreter.

**Interviewer**: How does the JVM print compilation information?

**Candidate**: 
```bash
-XX:+PrintCompilation           # Summary: method, size, time
-XX:+PrintInlining              # Inlining decisions
-XX:+UnlockDiagnosticVMOptions -XX:+LogCompilation  # XML log for JITWatch
-XX:+PrintAssembly              # Actual native code (requires hsdis)
-XX:+CITime                     # Compiler time statistics
```
`PrintCompilation` output: `java.lang.String::hashCode (57 bytes)` — method, size, time. `PrintInlining` shows: `@ 15 java.lang.String::charAt (7 bytes) inline (hot)`.

**Interviewer**: What is `-XX:CompileThreshold` in non-tiered vs tiered mode?

**Candidate**: In non-tiered mode (Java 7-): `-XX:CompileThreshold=10000` (C2). In tiered mode (Java 8+ default): `CompileThreshold` is ignored. Instead, tiered thresholds apply: `Tier3InvocationThreshold` (~200), `Tier3MinInvocationThreshold` (~100), `Tier4InvocationThreshold` (~5000). Setting `-XX:-TieredCompilation` restores the old behavior.

**Interviewer**: Final: How does the JVM handle a virtual call site optimization?

**Candidate**: For `invokevirtual`, the JIT uses: (1) Monomorphic inline cache — assumes same class as last call, checks with a guard. (2) CHA (Class Hierarchy Analysis) — if only one implementation exists, devirtualize completely. (3) Megamorphic call — if > 2 different types at the call site, use a complete vtable lookup (no optimization). `-XX:TypeProfileWidth=2` (default) controls when a call is considered megamorphic.

---

## Feedback

**Strengths**:
- Complete compilation tier breakdown
- OSR mechanism explanation
- Inlining candidates and limits
- Escape analysis and scalar replacement
- Deoptimization triggers

**Areas for Improvement**:
- Could discuss speculative optimizations (loop unrolling, branch prediction)
- Mention `-XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI` for Graal JIT

**Score**: 5/5 — Expert JIT knowledge
