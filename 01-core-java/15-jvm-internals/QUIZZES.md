# Module 15: JVM Internals - Quizzes

---

## Q1: Runtime Data Areas
Which of the following is shared among all threads in the JVM?

A) Stack
B) Program Counter (PC) Register
C) Heap
D) Native Method Stack

**Answer**: C
**Explanation**: The Heap and Method Area are shared among all threads. Stacks, PC registers, and Native Method Stacks are thread-local.

---

## Q2: Class Loading
Which ClassLoader is responsible for loading Java's core API classes (e.g., `java.lang.String`)?

A) Application ClassLoader
B) Extension ClassLoader
C) Bootstrap ClassLoader
D) Custom ClassLoader

**Answer**: C
**Explanation**: The Bootstrap ClassLoader loads the core Java API libraries.

---

## Q3: JIT Compilation
What is the primary role of the JIT Compiler in the JVM?

A) To compile Java source code into bytecode
B) To compile frequently executed bytecode into native machine code
C) To garbage collect unused objects
D) To verify bytecode for security

**Answer**: B
**Explanation**: The Just-In-Time (JIT) compiler improves runtime performance by compiling hot (frequently executed) bytecode into native machine code.