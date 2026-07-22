# Interview Questions: JIT Compilation Deep Dive

## Company-Specific Focus

### Google
- JIT compilation: interpreting bytecode -> compiling to native code for hot methods
- Tiered compilation: 5 levels (0=interpreter, 1-3=C1 with/without profiling, 4=C2)
- Compilation thresholds: -XX:TieredCompilation, -XX:CompileThreshold

### Microsoft
- JIT vs .NET JIT (RyuJIT): tiered compilation in both ecosystems
- Server vs client compiler: C2 (server) vs C1 (client)

### Amazon
- Warm-up: JIT compilation causes latency on first requests
- AOT compilation: GraalVM native image as alternative
- Profile-guided optimization: collecting profiling data for better compilation

### Meta
- Inlining: the most important JIT optimization
- Escape analysis: allocating on stack instead of heap
- Lock coarsening: merging adjacent synchronized blocks

### Apple
- JIT on ARM64: differences from x86_64
- Code cache size: -XX:ReservedCodeCacheSize

### Oracle
- HotSpot JIT compilers: C1 (client), C2 (server)
- Tiered compilation: 5 levels of compilation
- PrintCompilation: diagnostic output for compiled methods
- Compiler threads: -XX:CICompilerCount

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — JIT is a JVM optimization engine) |

## Real Production Scenarios
- **Netflix**: Warm-up strategy — invoking key methods during application startup to trigger JIT before traffic arrives
- **LinkedIn**: Code cache full — -XX:ReservedCodeCacheSize=512M resolved performance degradation

## Interview Patterns & Tips
- **Hot methods**: methods invoked frequently are compiled
- **Inlining**: replaces the method call with the method body
- **Escape analysis**: stack allocation, scalar replacement, lock elimination
- **Code cache**: stores compiled native code

## Deep Dive Questions
- **Tiered compilation**: How do the 5 compilation levels work?
- **Inlining threshold**: What is -XX:MaxInlineSize and -XX:FreqInlineSize?
- **OSR**: How does On-Stack Replacement compile loops during execution?
- **Deoptimization**: What causes deoptimization?
- **Code cache**: What happens when code cache fills?