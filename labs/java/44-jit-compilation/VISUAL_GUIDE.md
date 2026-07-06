# Visual Guide to JIT Compilation

## Tier Progression
```
Invocation Count:   0     1,500    5,000    10,000    15,000+
                    ↓       ↓        ↓         ↓         ↓
Tier:              [0]    [2]      [3]       [4]       [4]
                  Int    C1 limited C1 full  C2 full   C2 full
```

## JIT Compilation Pipeline
```
Bytecode → Interpreter → Profiler → C1 Compiler → C2 Compiler
                                      ↓              ↓
                                  Basic opt     Aggressive opt
                                       ↓              ↓
                                  nmethod        nmethod
                                       ↓              ↓
                                      Install at safepoint
```

## Inlining Decision Tree
```
Method called from hot loop?
├── Yes: check size
│   ├── < 35 bytes: Inline (MaxInlineSize)
│   ├── < 325 bytes: Inline if frequent (FreqInlineSize)
│   └── > 325 bytes: Don't inline
└── No: don't inline
```

## Escape Analysis Outcomes
```
Object allocation in method
├── No escape:
│   └── Scalar replacement (fields → locals)
├── Arg escape:
│   └── Stack allocation (if possible)
└── Global escape:
    └── Heap allocation (no optimization)
```

## Deoptimization Flow
```
Optimistic C2 compilation → Type assumption changes
    → nmethod marked "not entrant"
    → Next call enters interpreter
    → Re-profile → Recompile (or stay interpreted)
```

## Inline Cache States
```
Call site:
├── Monomorphic (1 type) → Direct call
├── Bimorphic (2 types) → Type check + branch
└── Megamorphic (3+ types) → vtable lookup (slow)
```

## Compilation Log (-XX:+PrintCompilation)
```
    119   31       3       java.lang.String::hashCode (55 bytes)
    120   32       4       java.lang.Math::max (5 bytes)
    121   33       4       java.util.HashMap::get (117 bytes)
    337   34 %     4       com.example.HotMethod::loop @ 12 (42 bytes)
```
Columns: timestamp, compile ID, tier (3=C1, 4=C2), class::method, % = OSR
