# Interview Questions: Class Loading & Bytecode

## Beginner
1. What is the purpose of a ClassLoader?
2. Describe the ClassLoader delegation model.
3. What is the difference between ClassNotFoundException and NoClassDefFoundError?
4. How do you load a class at runtime in Java?

## Intermediate
5. Explain the structure of a .class file (magic number, version, constant pool, etc.).
6. How does ASM's visitor pattern work for reading and writing bytecode?
7. What is invokedynamic and when would you use it?
8. How do lambda expressions compile to bytecode?

## Advanced
9. How does the JVM's class verification process work? What does it check?
10. Explain how Metaspace (Java 8+) differs from PermGen (Java 7 and earlier).
11. How would you implement a hot-reload plugin system using custom ClassLoaders?
12. What is the relationship between MethodHandle, invokedynamic, and reflection?
13. How does the JIT treat invokedynamic call sites differently from regular virtual calls?

## Expert
14. Design a ClassLoader that supports class versioning (multiple versions of the same class).
15. How does AppCDS work and what are its limitations?
16. Explain the constant pool resolution process. When does resolution fail and why?
17. How would you use ASM to add a method to an existing class at runtime?
18. What are the thread safety guarantees of class loading (concurrent loading of the same class)?
19. How does the JVM's class unloading mechanism interact with the garbage collector?

## Answers
Available in the SOLUTION directory.
