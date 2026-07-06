# Security Considerations for JIT Compilation

## JIT Spraying
JIT spraying is an attack where an attacker controls the input to a JIT compiler to generate executable code with specific bit patterns. The attacker arranges for the JIT to produce native code that, when interpreted at different offsets, executes malicious instructions. Modern JITs mitigate this by randomizing code layout and using W^X (write XOR execute) memory protection.

## Information Leakage from JIT Code
Compiled native code may embed constants that leak sensitive information (e.g., cryptographic keys used in JIT-compiled methods). The JIT code is in readable memory (before execution), so a process with memory access could extract these constants. Use `-XX:+NumberOfLoopInvocationsToCompile` to limit exposure.

## Deoptimization-Based Side Channels
Timing variations from deoptimization can leak information. If an attacker can observe execution timing, they can detect when deoptimization occurs and infer type information or branch outcomes. Constant-time crypto implementations must avoid type profiles that vary with secret data.

## Code Cache Exhaustion
An attacker who can trigger compilation of many unique methods can fill the code cache. Once full, the JIT stops compiling, and performance degrades to interpreter levels. Monitor code cache usage with `-XX:+PrintCodeCache` and set appropriate `-XX:ReservedCodeCacheSize`.

## Compilation Queue Overflow
Submitting many compilation requests (via JMX, JFR, or agent interfaces) can overwhelm the compiler threads. This can delay compilation of critical hot methods, causing performance degradation. Protect JMX and diagnostic interfaces.

## JIT Compiler Bugs
JIT compiler bugs can produce incorrect native code. While rare, these can cause security-sensitive operations to behave incorrectly. Use `-XX:-UseC2` or `-XX:TieredStopAtLevel=3` as a workaround for specific C2 bugs. Stay updated on JIT bug fixes.

## Profiling Data Exposure
The MDO (MethodData Object) records type profiles of actual method arguments. If an attacker can access profiling data (via heap dump or JVM diagnostic commands), they can learn which types flow through specific code paths, potentially revealing implementation details.

## Write-XOR-Execute (W^X)
The JIT must write code to memory and then execute it. W^X protection ensures memory is either writable or executable, never both. The JIT creates the code as writable, then changes to executable. This prevents code injection via the JIT buffer.
