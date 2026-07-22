# Interview Questions: JIT Compilation Deep Dive

## Company-Specific Focus

### Google
- Just-in-Time compilation: interpreting vs compiling to native code
- C1 (client) vs C2 (server) compiler: optimization levels and trade-offs
- Tiered compilation: interpretation through C1 (profiled) to C2

### Microsoft
- JIT compilation: Java vs .NET RyuJIT
- Code cache management and the effect of code cache filling

### Amazon
- Warm-up latency: how JIT compilation causes latency in the first requests
- Profile-guided optimization (PGO): tiered compilation and profiling
- Inlining: the most important optimization

### Meta
- Compilation thresholds: -XX:CompileThreshold and tiered compilation
- Deoptimization: when the JIT revokes compiled code
- OSR (On Stack Replacement): compiling loops while running

### Apple
- JIT on ARM64: differences from x86_64
- Code cache size tuning for small footprint applications

### Oracle
- HotSpot JIT compilers: C1 (client) and C2 (server)
- Tiered compilation phases: level 0 to level 4
- Compilation tasks: queued, compiled, installed
- JVM flags: -XX:+PrintCompilation, -XX:+UnlockDiagnosticVMOptions

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — JIT is an internal optimization mechanism) |

## Real Production Scenarios
- **Netflix**: First request latency due to JIT warm-up — mitigated with application warm-up scripts
- **LinkedIn**: Code cache full warning — the JIT stopped compiling hot methods, performance degraded by 30%
- **Uber**: Deoptimization caused a 500ms latency spike on a previously optimized method

## Interview Patterns & Tips
- **Warm-up**: JVM needs warm-up for the JIT to optimize hot paths
- **Inlining**: The most important optimization performed by the JIT compiler
- **Deoptimization**: JIT can revert to interpreted mode if assumptions are invalidated
- **PrintCompilation**: Use -XX:+PrintCompilation to see compilation activity

## Deep Dive Questions
- **Tiered compilation**: What are the 5 compilation levels?
- **Inlining**: What is the -XX:MaxInlineSize default? When does the JIT inline?
- **OSR**: How does On-Stack Replacement allow compiling a loop that's already running?
- **Code cache**: What happens when the code cache fills? (Stops compiling)
- **Deoptimization**: What triggers deoptimization? (Class loading, profiling data change)