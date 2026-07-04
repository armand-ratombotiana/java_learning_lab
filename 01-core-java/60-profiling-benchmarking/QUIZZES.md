# Quizzes: Profiling & Benchmarking

Test your knowledge of JMH, JFR, and JIT compiler optimizations.

## Quiz 1: Benchmarking Pitfalls

**Q1: Why is using `System.currentTimeMillis()` in a simple `for` loop a terrible way to benchmark a Java method?**
- A) It is not accurate enough; you should use `System.nanoTime()`.
- B) It includes the JVM "warm-up" time (where the code is interpreted slowly before being JIT compiled), and it is highly susceptible to Dead Code Elimination if you don't use the result of the method.
- C) It causes a memory leak.
- D) It throws an exception in Java 17.
*Answer: B*

**Q2: In JMH, what is the purpose of the `Blackhole` object?**
- A) To delete files from the hard drive.
- B) To consume the results of a computation, forcing the JIT compiler to actually execute the code rather than deleting it as an optimization (Dead Code Elimination).
- C) To hide compiler warnings.
- D) To measure memory usage.
*Answer: B*

## Quiz 2: JFR and Profiling

**Q1: What makes JDK Flight Recorder (JFR) safe to use continuously in a production environment?**
- A) It only records data when an exception is thrown.
- B) It runs on a separate physical server.
- C) It is deeply integrated into the JVM and uses asynchronous, lock-free data structures to record events, keeping the performance overhead extremely low (typically under 1%).
- D) It only monitors the Garbage Collector, not CPU usage.
*Answer: C*

**Q2: You look at a JFR recording in JDK Mission Control. You see a massive allocation rate (e.g., 2 GB/sec) of `java.lang.String` objects. What does this likely indicate?**
- A) The JVM is working perfectly.
- B) A memory leak; the application will soon crash with an OutOfMemoryError.
- C) High memory churn. The application is creating and discarding Strings very rapidly. While the GC might keep up, the constant GC cycles will severely degrade CPU throughput.
- D) The hard drive is full.
*Answer: C*

## Quiz 3: Methodology

**Q1: What is the first step in the performance optimization methodology?**
- A) Rewrite the code using C++.
- B) Increase the JVM Heap size.
- C) Establish a baseline measurement using a profiler or benchmark tool under realistic load conditions.
- D) Replace all `ArrayList`s with arrays.
*Answer: C (You cannot optimize what you cannot measure).*