# Quizzes: Metaprogramming in Java

Test your knowledge of bytecode manipulation, Java agents, and runtime generation.

## Quiz 1: Core Concepts

**Q1: What is the primary difference between ASM and Javassist?**
- A) ASM only works on Java 8, while Javassist works on Java 21.
- B) ASM operates directly on raw bytecode instructions, while Javassist allows you to write Java source code as strings that it compiles into bytecode.
- C) Javassist is faster than ASM.
- D) ASM can only read classes, not write them.
*Answer: B*

**Q2: Which Java feature allows a tool (like an APM monitor) to modify the bytecode of a class *before* it is fully loaded into the JVM?**
- A) Annotation Processors
- B) Java Agents (`javaagent`) using `ClassFileTransformer`
- C) Dynamic Proxies
- D) The `SecurityManager`
*Answer: B*

## Quiz 2: Edge Cases and Errors

**Q1: You generate a class dynamically using ASM, but when you try to instantiate it, the JVM throws a `java.lang.VerifyError`. What is the most likely cause?**
- A) You forgot to add the class to the classpath.
- B) You generated invalid bytecode (e.g., trying to pop an integer from the stack when a string is present).
- C) The Metaspace is full.
- D) You are using an outdated version of Java.
*Answer: B*

**Q2: Why is it dangerous to dynamically generate a brand new class for every incoming HTTP request?**
- A) It will quickly cause a `java.lang.OutOfMemoryError: Metaspace` because generated classes are loaded into memory and are difficult to garbage collect.
- B) It will corrupt the hard drive.
- C) It will cause a `StackOverflowError`.
- D) It violates the HTTP protocol.
*Answer: A*

## Quiz 3: Annotation Processing

**Q1: According to the standard Java Annotation Processing API (JSR 269), what is an annotation processor explicitly forbidden from doing?**
- A) Reading annotations on a method.
- B) Generating new `.java` files.
- C) Modifying existing `.java` or `.class` files.
- D) Throwing compilation errors.
*Answer: C (Processors like Lombok bypass this by using internal compiler APIs, not the standard public API)*