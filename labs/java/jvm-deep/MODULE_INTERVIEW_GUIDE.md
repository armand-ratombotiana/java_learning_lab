# JVM Deep Dive — Module Interview Guide

## Company-Specific Questions

### Oracle
- "Walk through the entire class file format. What does each section of the .class file contain?"
- "Explain the invokedynamic instruction. How does it bootstrap a lambda? Walk through the full mechanism."
- "Compare the three GC algorithms implemented in HotSpot: G1, ZGC, Shenandoah. How does each handle concurrent marking?"
- "How does tiered compilation work? What triggers a method to move from C1 to C2?"

### Google
- "Design a custom class loader for a plugin system. How do you prevent class conflicts?"
- "How would you use ASM to add profiling code to every method in a package at load time?"
- "Explain method handles. How do they differ from reflection in performance and type safety?"

### Amazon
- "You have a microservice that crashes with 'Code cache full' every 2 days. How do you diagnose and fix?"
- "Your Java service startup takes 30 seconds. How would you use CDS to reduce it to under 5 seconds?"
- "A production JVM is paused for 5 seconds every hour. Walk through the diagnostic process."

### Meta
- "How does the JVM's escape analysis work? Show me examples of scalar replacement and stack allocation."
- "What triggers JIT compilation? How do -XX:CompileThreshold, tiered compilation, and on-stack replacement interact?"

### Apple
- "How does the JVM object layout differ on ARM64 vs x64? What is the impact of different page sizes?"
- "How does the JVM handle transparent huge pages on Linux vs macOS?"

## LeetCode Problems

| Problem | JVM Topic |
|---------|-----------|
| 2949 Count Beautiful Substrings II | (None direct — but tests understanding of polynomial hashing, which mirrors hash table internals) |
| Implement custom ClassLoader | Class loading, delegation, defineClass |
| Parse .class file | Bytecode, constant pool interpretation |
| Build simple bytecode optimizer | ASM, InstructionAdapter, bytecode transformation |
| Interpret GC log | GC algorithm understanding, pause time analysis |
| Read hs_err file | Crash dump analysis, register state, stack frames |

## FAANG Interview Stories

**Story 1: Oracle — Class File Parsing**
> *"The interviewer handed me a hex dump of a .class file. They asked me to manually parse the magic number, major/minor version, constant pool count, and identify a CONSTANT_Utf8 entry. Then they asked: 'If I change the constant pool count, what happens at verification?'"* — JVM Engineer, Oracle

**Story 2: Google — JIT Inlining**
> *"I was asked why a method that's called 50K times per second isn't being inlined. I walked through the inlining limits: MaxInlineSize (35 bytes), FreqInlineSize (325 bytes), InlineSmallCode (1000 bytes). We loaded the method with -XX:+PrintInlining to find it was 38 bytes — one byte over the limit. We then discussed -XX:MaxInlineSize=50 as a tunable."* — Staff SWE, Google

**Story 3: Netflix — G1 GC Tuning**
> *"Our streaming service had a 3-second pause every 10 minutes. GC logs showed concurrent mark cycle followed by mixed GC. We tuned G1 by reducing -XX:G1MixedGCCountTarget, increasing -XX:G1HeapRegionSize, and setting -XX:G1NewSizePercent. The pauses dropped to 150ms. The lesson: always check GC logs before tuning."* — Senior Performance Engineer, Netflix

## Senior vs Staff Deep Dive

### Senior-Level
- "Explain the complete class linking process: verification, preparation, resolution. What is lazy resolution?"
- "Compare C1 and C2 compilers. What optimizations does each perform?"
- "What is a safepoint? When do they occur? How does -XX:+UseBiasedLocking affect safepoint frequency?"

### Staff-Level
- "How would you add a new bytecode instruction to the JVM? Walk through HotSpot source changes needed."
- "Design a custom garbage collector for a low-latency trading system. What data structures would you use?"
- "How does the JVM implement Thread.yield()? Thread.sleep()? Object.wait()? At the OS level."
- "Explain how JFR works with zero overhead recording. How are events written without blocking application threads?"

## System Design Connections

| System | JVM Technology |
|--------|---------------|
| Plugin system | Custom ClassLoader, module isolation |
| APM tool | Bytecode instrumentation (ASM/ByteBuddy) |
| AOT compilation | GraalVM native-image, substrate VM |
| Polyglot runtime | GraalVM Truffle, invokedynamic |
| Hot-reload server | Custom ClassLoader, class unloading |
| Performance testing | JMH, async-profiler, JFR |

## Code Review Scenarios

**Scenario 1**: ThreadLocal usage in virtual threads.
```java
// Issue: ThreadLocal has high overhead with virtual threads (pinning)
ThreadLocal<User> currentUser = new ThreadLocal<>();
// Fix: Use ScopedValue<User> in Java 21+
```

**Scenario 2**: Using `System.gc()` to trigger GC.
- Issue: `System.gc()` only suggests GC; `-XX:+DisableExplicitGC` often set
- Fix: Use `jcmd <pid> GC.run` or tune GC instead

**Scenario 3**: Synchronized block inside virtual thread.
```java
// Issue: Causes virtual thread to pin to carrier thread
synchronized (lock) {
    Thread.sleep(1000);  // Pins for 1 second — blocks carrier
}
// Fix: Use ReentrantLock (which does not pin)
```

## Debugging Scenarios

**Scenario 1**: JVM crash with `SIGSEGV` in `hs_err_pid.log`.
- Check: Top frame in stack, register values, instruction pointer
- Common: Native code bug (JNI), corrupted heap, HotSpot bug
- Action: Copy crash log, file JBS bug, try `-XX:-UseCompressedOops`

**Scenario 2**: "Code cache full" warning.
- Monitor: `-XX:+PrintCodeCache` — see code cache usage
- Fix: Increase `-XX:ReservedCodeCacheSize`, check for code bloating (e.g., too many JSP compilations)

**Scenario 3**: Metaspace grows indefinitely.
- Monitor: `jstat -gcmetacapacity` or JFR `jdk.MetaspaceSummary`
- Root cause: ClassLoader leak (each deployment creates new class loaders without cleanup)
- Fix: Ensure class loaders are GC'd, or set `-XX:MaxMetaspaceSize`
