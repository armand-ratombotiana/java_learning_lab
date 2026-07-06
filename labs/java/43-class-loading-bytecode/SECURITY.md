# Security Considerations for Class Loading

## ClassLoader as a Security Boundary
The ClassLoader is part of Java's security architecture. Each ClassLoader has an associated ProtectionDomain that defines the permissions for all classes loaded by it. The SecurityManager (deprecated in Java 17) used this for access control.

## Malicious Class Files
An attacker could:
- Create a .class file with an invalid constant pool, causing the JVM to crash
- Craft bytecode that passes verification but executes illegal operations
- Inject classes into a running JVM via JMX or agent attachment

Protection: The bytecode verifier prevents invalid state transitions and type violations.

## ClassLoader Manipulation
An attacker with reflection access can:
- Load classes via `Class.forName()` with arbitrary ClassLoaders
- Access `protected` ClassLoader methods (defineClass, findClass)
- Modify the Thread context ClassLoader via `setContextClassLoader()`

Protection: Module access controls in Java 9+ limit reflective access. `--add-opens` is needed for deep reflection.

## Encrypted Class Loading
Custom ClassLoaders that decrypt classes prevent static analysis of bytecode. However, the decryption key is in the ClassLoader's bytecode, which can be decompiled. For true protection, use a secure element or native code to manage keys.

## Class Data Hijacking
If an attacker can write to a directory on the classpath, they can replace a .class file with a malicious version. The ClassLoader will load their version instead of the original. Protect classpath directories with file system permissions.

## Deserialization Attacks
Deserialization of malicious objects can trigger class loading of arbitrary classes. The JEP 290 (Filter Incoming Serialization Data) and JEP 415 (Context-Specific Deserialization Filters) provide defense mechanisms.

## Bytecode Injection via ASM
If an attacker controls the input to an ASM transformer, they can inject arbitrary bytecode into the transformed class. Validate all inputs to bytecode transformers and never apply transformations to untrusted bytecode.
