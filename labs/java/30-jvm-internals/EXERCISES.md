# Exercises: JVM Internals

## Exercise 1: Read Bytecode
Compile a simple Java class and use javap -c -p to examine the bytecode. Identify: local variable slots, operand stack operations, method invocation types.

## Exercise 2: GC Log Analysis
Run a program that allocates objects in a loop with -Xlog:gc*:file=gc.log. Examine GC logs: young GC frequency, old gen promotion, pause times.

## Exercise 3: Object Header Inspection
Use JOL (Java Object Layout) to print the layout of a simple object. Calculate header size, field offsets, and total object size.

## Exercise 4: JIT Watching
Run a method 100,000 times with -XX:+PrintCompilation. Observe which tier each compilation reaches and when.

## Exercise 5: Thread Dump Analysis
Create a program with thread contention (synchronized blocks, deadlock potential). Take thread dumps with jstack and analyze lock states.

## Exercise 6: Heap Dump Analysis
Create a program with a memory leak (objects referenced but never used). Take a heap dump and analyze with Eclipse MAT or VisualVM.

## Exercise 7: JFR Profiling
Use JFR to record a 60-second profile of an application. Examine hot methods, allocation sites, GC events.

## Exercise 8: Custom ClassLoader
Write a custom ClassLoader that loads classes from a network source. Demonstrate class isolation.

## Exercise 9: Escape Analysis Test
Write a method that allocates objects without escaping. With -XX:+PrintEscapeAnalysis, verify objects are stack-allocated or scalar-replaced.

## Exercise 10: GC Algorithm Comparison
Run the same workload with G1 (-XX:+UseG1GC), ZGC (-XX:+UseZGC), and Serial (-XX:+UseSerialGC). Compare throughput, pause times, and memory usage.
