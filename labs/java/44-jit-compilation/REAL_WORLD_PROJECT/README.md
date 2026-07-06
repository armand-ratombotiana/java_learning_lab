# Real-World Project: JIT-Optimized Expression Evaluator

## Overview
Build a high-performance expression evaluator that leverages JIT compilation patterns to achieve near-native speed for mathematical expression evaluation.

## Architecture
```
ExpressionParser (ANTLR4 grammar)
  → AST nodes (AddNode, MulNode, ConstNode, VarNode)
  → Interpreter (baseline, interpreted mode)
  → CompiledExecutor (JIT-triggering hot loop)
```

## Key Design Decisions
1. **Small hot methods** — keep `evaluate()` methods under the inlining threshold (35 bytes)
2. **Monomorphic dispatch** — use `final` classes or `sealed` interfaces to avoid megamorphic call sites
3. **Escape analysis friendly** — allocate AST nodes once, reuse in hot loop
4. **Intrinsic-aware** — use `Math.fma()` (fused multiply-add) where applicable
5. **Branch prediction** — minimize unpredictable branches in `evaluate()`

## Performance Targets
- Throughput: > 10 million evaluations/second for simple expressions
- Warmup: < 10,000 iterations to reach peak performance
- Memory: zero allocation during evaluation after warmup

## Evaluation Techniques
- Use `-XX:+PrintCompilation` to verify methods are compiled
- Use `-XX:+PrintInlining` to verify inlining decisions
- Use `-XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly` to inspect generated code
- Use JMH with `-prof perfasm` for assembly-level profiling

## Deliverables
- `ExprNode.java` — AST node hierarchy
- `ExprParser.java` — expression parser
- `ExprEvaluator.java` — evaluation engine
- `ExprBenchmark.java` — JMH benchmark suite
- Performance analysis report
