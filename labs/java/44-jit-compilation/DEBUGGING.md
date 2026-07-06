# Debugging JIT Compilation Issues

## PrintCompilation
`-XX:+PrintCompilation` shows compilation events:
```
timestamp compile_id tier [%] method size
  119       31     3          String::hashCode (55 bytes)
  120       32     4          Math::max (5 bytes)
  337       34 %   4          HotMethod::loop @ 12 (42 bytes)
```
- `%` = OSR compilation (loop)
- `!` = with exceptions
- `s` = synchronized method
- `n` = native method
- `made not entrant` = deoptimization pending
- `made zombie` = space reclaimed

## PrintInlining
`-XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining` shows inlining decisions:
```
@ 12   com.example.Foo::bar (5 bytes)   inline (hot)
@ 15   com.example.Baz::qux (40 bytes)   too big
@ 20   com.example.Quux::method (10 bytes)  already compiled into a big method
```

## PrintAssembly
`-XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly` shows compiled native code (requires hsdis plugin). Use to verify intrinsic replacement and instruction selection.

## JFR Events for JIT
Java Flight Recorder captures:
- `jdk.Compilation` — method compilation events
- `jdk.Inlining` — inlining decisions
- `jdk.Deoptimization` — deoptimization events
- `jdk.CompilerPhase` — compilation phase timing

## Hot Method Identification
Use `-XX:+PrintCompilation` combined with `jstack` or `async-profiler`:
1. Run application with `-XX:+PrintCompilation`
2. Capture thread dumps during performance issue
3. Cross-reference hot methods in thread dumps with compilation log
4. Identify methods that are not being compiled (always interpreted)

## Deoptimization Diagnosis
High deoptimization rates indicate unstable type profiles:
- Add `-XX:+UnlockDiagnosticVMOptions -XX:+PrintDeoptimizationDetails`
- Check which assumptions are violated
- Look for new class loading in hot paths
- Consider sealed classes or type profiling hints
