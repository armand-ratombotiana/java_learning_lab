# Exercises: JVM Internals

## Exercise 1: Memory Analysis
Create a simple Java program that allocates objects and monitors memory usage using Runtime and ManagementFactory APIs.

```java
Runtime rt = Runtime.getRuntime();
System.out.println("Free: " + rt.freeMemory() + " bytes");
```

## Exercise 2: GC Algorithm Comparison
Experiment with different GC algorithms using JVM flags:
- `-XX:+UseSerialGC`
- `-XX:+UseG1GC`
- `-XX:+UseZGC`

Observe heap usage patterns with `-XX:+PrintGCDetails -Xlog:gc`.

## Exercise 3: ClassLoader Analysis
Inspect the class loader hierarchy in your application:
- Bootstrap ClassLoader
- Platform ClassLoader
- Application ClassLoader

Print which class loader loaded specific classes.

## Exercise 4: Thread Inspection
Use ThreadMXBean to list all active threads and their states in your application.

## Exercise 5: Heap Dump Analysis
Generate a heap dump on OutOfMemoryError and analyze with jvisualvm:
```
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/tmp/dump.hprof
```

## Solutions
See src/main/java/com/learning/jvm/JVMInternalsLab.java for basic implementations.