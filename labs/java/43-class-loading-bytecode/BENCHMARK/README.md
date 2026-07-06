# Benchmarks: Class Loading & Bytecode

## Benchmark 1: ClassLoader Loading Speed
Compare loading 1000 classes through different delegation strategies:
- System ClassLoader (normal delegation)
- Child-first ClassLoader
- Custom EncryptionClassLoader (with decryption overhead)

## Benchmark 2: ASM Transformation Overhead
Measure class load-time overhead with ASM transformation:
- Baseline (no transformer)
- Add simple annotation (AsmTransformer style)
- Add method timer (method entry/exit code)
- Full method profiling (all methods instrumented)

## Benchmark 3: Reflection vs MethodHandle vs invokedynamic
Compare invocation performance of:
- `Method.invoke()` via reflection
- `MethodHandle.invoke()` via `java.lang.invoke`
- Direct interface invocation
- Lambda metafactory (invokedynamic)

## Running Benchmarks
```bash
java -jar benchmarks.jar -wi 5 -i 10 -f 3
```

## Key Metrics
- Class load time (ms per class)
- Bytecode size increase (original vs transformed)
- Method invocation throughput (ops/μs)
- Memory overhead (ClassLoader + metadata)
