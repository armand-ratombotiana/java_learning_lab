# Quiz: JIT Compilation

1. What are the five tiers of JIT compilation in Java?
   a) Interpreter, Simple C1, Limited C1, Full C1, C2
   b) Basic, Advanced, Expert, Master, Legendary
   c) Level 1-5
   d) Interpreted, Basic, Optimized, Aggressive, Final

2. After approximately how many invocations does C2 compilation typically begin?
   a) 100
   b) 1,000
   c) 10,000
   d) 100,000

3. What is the default MaxInlineSize in bytes?
   a) 10
   b) 25
   c) 35
   d) 100

4. Which of the following methods is NOT a JVM intrinsic?
   a) System.arraycopy
   b) Math.sqrt
   c) String.length
   d) Collections.sort

5. What does escape analysis allow the JIT to do?
   a) Inline methods across class boundaries
   b) Allocate non-escaping objects on the stack or as scalars
   c) Eliminate unused class files from the class path
   d) Optimize garbage collection cycles

6. What triggers deoptimization?
   a) A method exceeds the code cache size
   b) A type assumption changes (new class appears at a call site)
   c) The JVM runs out of memory
   d) A thread is interrupted

7. What does the `%` symbol mean in PrintCompilation output?
   a) The method is compiled with exceptions
   b) OSR (On-Stack Replacement) compilation
   c) The method is synchronized
   d) The method is native

8. How many types cause a call site to become megamorphic?
   a) 1
   b) 2
   c) 3 or more
   d) More than 5

## Answer Key
1-a, 2-c, 3-c, 4-d, 5-b, 6-b, 7-b, 8-c
