# Architecture of JIT Compilation

## JIT Compiler Components
```
JVM Runtime
├── Interpreter (tier 0)
├── Method Profiler (MDO)
├── Compiler Queue
├── C1 Compiler (tiers 1-3)
├── C2 Compiler (tier 4)
├── Code Cache (nmethod storage)
└── Deoptimization Manager
```

## Compilation Pipeline
```
Bytecode → C1 IR → HIR (High IR) → LIR (Low IR) → Register Allocation → Code Generation
Bytecode → C2 IR → Ideal Graph → Global Optim → Matcher → Code Generation
```

## C2 Compilation Phases
1. **Parsing**: Build Ideal Graph from bytecode
2. **Canonicalization**: Simplify graph (common subexpressions, constants)
3. **Inlining**: Inline call sites based on frequency
4. **Escape Analysis**: Identify non-escaping objects
5. **Loop Optimizations**: Unrolling, vectorization, peeling
6. **Global Value Numbering**: Remove redundant computations
7. **Conditional Constant Propagation**: Fold constants through branches
8. **Dead Code Elimination**: Remove unused computations
9. **Matcher**: Map Ideal nodes to machine instructions
10. **Register Allocation**: Assign registers to values
11. **Code Generation**: Emit native instructions

## Code Cache Organization
```
CodeHeap (non-nmethod)
├── Code Blob 1 (method entry)
├── Code Blob 2 (method entry)
└── ...
CodeHeap (profiled nmethod)
├── nmethod (C1 compiled)
└── ...
CodeHeap (non-profiled nmethod)
├── nmethod (C2 compiled)
└── ...
```

## Compilation Threads
```
VM Thread → CompileBroker → C1 CompilerThread(s)
                           → C2 CompilerThread(s)
```
Compilation is non-blocking. The application continues running (interpreted or previously compiled code) while compilation proceeds.
