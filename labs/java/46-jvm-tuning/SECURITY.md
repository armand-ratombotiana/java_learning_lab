# Security Considerations for JVM Tuning

## Flag Injection
If an attacker can control JVM arguments (via scripts, config files, or injection), they can:
- Disable security features (`-Djava.security.manager=disable`)
- Enable remote debugging (`-agentlib:jdwp=transport=dt_socket,server=y,address=5005`)
- Override classpath with malicious classes (`-Xbootclasspath/a:`)

Protect JVM argument configuration with proper file permissions and input validation.

## Heap Dump Exposure
`-XX:+HeapDumpOnOutOfMemoryError` creates a heap dump file containing all application data. If the dump lands in a world-readable directory, sensitive data is exposed. Configure `-XX:HeapDumpPath` to a secure location with restricted permissions.

## Diagnostic Flag Exposure
Flags like `-XX:+PrintAssembly`, `-XX:+UnlockDiagnosticVMOptions`, and `-XX:+LogCompilation` can reveal internal JVM state. These flags should not be enabled in production environments where an attacker could read the output.

## JMX and Remote Management
`-Dcom.sun.management.jmxremote` exposes MBeans for JVM management. Without proper authentication and SSL:
- An attacker can read heap usage, thread dumps, GC stats (information disclosure)
- An attacker can invoke GC (denial of service via GC thrashing)
- An attacker can modify MBeans

Always configure JMX with `-Dcom.sun.management.jmxremote.authenticate=true` and `-Dcom.sun.management.jmxremote.ssl=true`.

## Shared Memory Attacks (CDS)
CDS archives map JSA files into shared memory. If an attacker can modify the JSA file before the JVM starts, they can inject code into the shared archive. Protect CDS archive files with file system permissions.

## Large Page Security
Huge pages are pinned in memory (not swappable). An attacker who can request large page allocation can cause the JVM to consume excessive physical memory, starving other processes. Set limits on huge page pool size at the OS level.

## Metaspace Exhaustion as DoS
An attacker who can trigger class loading (e.g., via parameterized types or dynamic code generation) can fill Metaspace, causing OutOfMemoryError. Set `-XX:MaxMetaspaceSize` to limit the impact.
