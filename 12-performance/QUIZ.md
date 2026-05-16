# Performance Quiz

## Section 1: JVM Tuning

**Question 1:** What does -Xms256m do?
- A) Maximum heap size
- B) Initial heap size
- C) Stack size
- D) Metaspace size

**Answer:** B) Initial heap size (minimum)

---

**Question 2:** Which garbage collector is the default in Java 11+?
- A) Serial
- B) Parallel
- C) CMS
- D) G1

**Answer:** D) G1

---

**Question 3:** What is the purpose of -XX:MaxGCPauseMillis?
- A) Sets maximum heap size
- B) Sets target for maximum GC pause time
- C) Sets minimum pause time
- D) Sets GC timeout

**Answer:** B) Sets target for maximum GC pause time (G1 tries to achieve this)

---

**Question 4:** What does Metaspace store?
- A) Objects
- B) Class metadata
- C) Stack frames
- D) Native memory

**Answer:** B) Class metadata (replaced PermGen in Java 8)

---

**Question 5:** Which GC algorithm is best for low-latency applications?
- A) Serial
- B) Parallel
- C) G1
- D) All are equal

**Answer:** C) G1 or ZGC for very low latency

---

## Section 2: Profiling

**Question 6:** What is CPU profiling?
- A) Finding memory leaks
- B) Measuring CPU time per method
- C) Analyzing network usage
- D) Database query analysis

**Answer:** B) Measuring CPU time per method

---

**Question 7:** What is a heap dump?
- A) CPU profiling output
- B) Snapshot of heap memory
- C) GC log
- D) Thread dump

**Answer:** B) Snapshot of heap memory

---

**Question 8:** Which tool is used for Java Flight Recorder?
- A) jmap
- B) jstat
- C) jcmd or jfr
- D) jstack

**Answer:** C) jcmd or jfr

---

**Question 9:** What does jstack print?
- A) Heap usage
- B) CPU usage
- C) Thread dump
- D) GC information

**Answer:** C) Thread dump

---

**Question 10:** What is VisualVM used for?
- A) GC logging
- B) CPU and memory profiling
- C) Thread management
- D) Class loading

**Answer:** B) CPU and memory profiling

---

## Section 3: Benchmarking

**Question 11:** What is JMH?
- A) Java Memory Heap
- B) Java Microbenchmark Harness
- C) Java Method Helper
- D) Java Monitoring Hub

**Answer:** B) Java Microbenchmark Harness

---

**Question 12:** What does @Benchmark annotation do?
- A) Defines benchmark class
- B) Defines benchmark method
- C) Sets warmup iterations
- D) Sets fork count

**Answer:** B) Defines benchmark method

---

**Question 13:** What is Mode.Throughput in JMH?
- A) Operations per second
- B) Average time per operation
- C) Single operation time
- D) Sample time

**Answer:** A) Operations per second

---

**Question 14:** Why is warmup important in benchmarks?
- A) JIT compilation needs to happen
- B) Memory needs to be allocated
- C) Tests need to be repeated
- D)GC needs to run

**Answer:** A) JIT compilation needs to happen (cold vs warm)

---

**Question 15:** What does @Fork(2) do?
- A) Runs benchmark 2 times
- B) Creates 2 JVM instances
- C) Uses 2 threads
- D) Iterates 2 times

**Answer:** B) Creates 2 JVM instances (fresh JVM for each fork)

---

## Section 4: Memory Management

**Question 16:** What is the Young Generation?
- A) Long-lived objects
- B) Short-lived objects
- C) Metaspace
- D) Native memory

**Answer:** B) Short-lived objects (newly allocated)

---

**Question 17:** What happens during a Minor GC?
- A) Full heap collection
- B) Young generation collection only
- C) Metaspace collection
- D) Only old generation collection

**Answer:** B) Young generation collection only

---

**Question 18:** What is the purpose of -XX:+UseG1GC?
- A) Use Serial GC
- B) Use G1 garbage collector
- C) Use Parallel GC
- D) Use ZGC

**Answer:** B) Use G1 garbage collector

---

**Question 19:** What is a memory leak?
- A) Out of memory error
- B) Objects not garbage collected when should be
- C) GC running too often
- D) Low memory

**Answer:** B) Objects not garbage collected when should be (memory grows)

---

**Question 20:** What is the solution for OutOfMemoryError: Java heap space?
- A) Increase stack size
- B) Increase heap size or fix memory leak
- C) Increase metaspace
- D) Decrease threads

**Answer:** B) Increase heap size or fix memory leak

---

## Score Interpretation

| Score | Level |
|-------|-------|
| 18-20 | Expert |
| 14-17 | Advanced |
| 10-13 | Intermediate |
| 5-9 | Beginner |
| < 5 | Foundation needed |