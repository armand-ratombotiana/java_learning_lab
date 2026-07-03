# Security: JVM Internals

## Bytecode Verification
Before execution, the JVM verifies bytecode to ensure:
- No stack overflow/underflow
- No illegal type conversions
- No access to private fields/methods from outside class
- No forged control flow (no jumping to middle of instruction)
- All operand stack types are correct

## Security Manager (Deprecated)
Java's Security Manager (deprecated for removal in Java 17) provided:
- File permissions (read/write/delete)
- Network permissions (connect/accept/listen)
- Runtime permissions (exit, setFactory)
- Reflection permissions
The module system's strong encapsulation now provides better security.

## Class Loader Security
- **Namespace isolation**: Classes in different class loaders cannot access each other's packages
- **Parent delegation**: System classes are loaded by bootstrap class loader (protected)
- **Package sealing**: JAR packages can be sealed to a single class loader
- **Code signing**: Verifies JAR authenticity

## JVM Sandbox
The JVM provides a security sandbox:
- Untrusted code runs in a restricted environment
- System calls go through native method interface
- JNI is the only way to bypass Java security (and JNI is checked by SecurityManager)

## Side-Channel Attacks
- Timing attacks: JVM does not guarantee constant-time operations
- Memory attacks: GC does not zero memory (sensitive data may remain in freed memory)
- JIT speculation: Potential for Spectre-like attacks (JIT generates branch-predicting code)

## Security Best Practices
- Use the latest JDK version with security patches
- Enable strong encapsulation (Java 17+ default: --illegal-access=deny)
- Monitor with JFR for suspicious activity
- Use jlink to exclude unnecessary modules from your runtime
- Avoid JNI unless absolutely necessary
- Use sealed classes and records for type safety
