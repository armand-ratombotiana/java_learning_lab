# Performance — Exceptions

## Exception Creation Cost

Creating an exception is expensive because `fillInStackTrace()` walks the call stack. A typical exception costs 100-1000x more than a normal return.

## Throwing Cost

- Stack unwinding: searching exception table for matching handler
- Performance proportional to stack depth and handler count
- Keep try blocks focused (small, specific) for faster matching

## try Block Overhead with No Exception

Modern JVMs make try blocks essentially free when no exception is thrown — the exception table is metadata, not executed code.

## Try-With-Resources

Creates a finally block and suppresses exceptions — minimal overhead when no exception occurs.

## Performance Best Practices

- Don't use exceptions for control flow
- Lazy stack traces: override `fillInStackTrace()` if creating exceptions for known paths
- `-XX:-OmitStackTraceInFastThrow`: JIT elides stack traces for repeated exceptions in hot loops — disable for debugging
- Use `java.util.logging` or SLF4J parameterized logging instead of `if (log.isDebugEnabled()) log.debug(...)`
- Benchmark before optimizing exception handling — it's rarely the bottleneck
