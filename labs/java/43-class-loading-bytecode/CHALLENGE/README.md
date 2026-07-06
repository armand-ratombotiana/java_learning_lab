# Challenge: Bytecode Method Profiler with ASM

## Problem
Write a Java agent using the ASM library that instruments all methods in a package to record execution time. The agent should add `long start = System.nanoTime()` at method entry and `System.out.println(...)` at each exit point, preserving the original method's control flow.

## Specifications
- Agent must transform classes at load time via `ClassFileTransformer`
- Use `ClassReader` → `ClassWriter` → instrumented bytecode
- Add `try-finally` blocks around method bodies for timing (challenge: handle multiple return points)
- Skip constructors, static initializers, and synthetic methods
- Output format: `[PROFILE] com.example.Foo.bar() : 1234 ns`

## Advanced Challenges
1. Handle methods with multiple return statements — inject timing before each return
2. Handle methods that throw exceptions — inject timing in catch blocks
3. Aggregate timing per method and output summary at JVM shutdown via `Runtime.addShutdownHook`
4. Support configuration via `-Dprofile.include` and `-Dprofile.exclude` patterns
5. Handle `native` methods (what should the agent do?)

## Edge Cases
- Methods with no return statement (void, infinite loops, System.exit)
- Methods with try-catch-finally (must not interfere with exception tables)
- Recursive methods (should timing be per-call or cumulative?)
- Interface default methods
- Lambdas and invokedynamic bootstrap methods

## Hints
- Use `AdviceAdapter` from ASM's `org.objectweb.asm.commons` package
- The `onMethodEnter()` and `onMethodExit()` hooks handle try-finally generation
- `MethodNode` analysis can find all return opcodes (ARETURN, IRETURN, RETURN, etc.)
