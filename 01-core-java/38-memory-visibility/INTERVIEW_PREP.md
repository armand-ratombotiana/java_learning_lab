# Interview Preparation: Memory Visibility & Ordering

This document covers advanced questions related to the Java Memory Model, Happens-Before rules, and the `volatile` keyword.

## Q1: What are the two primary guarantees provided by the `volatile` keyword in Java?
**Answer:**
1.  **Memory Visibility**: It guarantees that when a thread writes to a volatile variable, the CPU flushes its local cache and writes the value directly to main memory. When a thread reads the variable, it fetches it directly from main memory, bypassing the local cache. This ensures all threads see the most up-to-date value.
2.  **Instruction Ordering (Memory Barriers)**: It prevents the compiler and the CPU from reordering instructions across the volatile read/write boundary. Writes that occurred in the code *before* the volatile write cannot be reordered to happen *after* it.

## Q2: Explain the "Double-Checked Locking" (DCL) Singleton pattern. Why was it broken before Java 5, and how do you fix it?
**Answer:**
DCL attempts to initialize a Singleton lazily without paying the performance penalty of a `synchronized` block on every `getInstance()` call.
```java
if (instance == null) {
    synchronized (Singleton.class) {
        if (instance == null) { instance = new Singleton(); }
    }
}
```
**Why it was broken**: `instance = new Singleton()` is not an atomic operation. It involves: 1) Allocating memory, 2) Calling the constructor, 3) Assigning the reference. The CPU is allowed to reorder steps 2 and 3. If it does, Thread A might assign the reference while the constructor is still running. Thread B checks `if (instance == null)`, sees it's not null, and returns a partially constructed object, causing a crash.
**The Fix**: In Java 5, the JMM was updated. Declaring `private static volatile Singleton instance;` fixes the issue because the `volatile` write acts as a memory barrier, preventing the reference assignment from being reordered before the constructor finishes.

## Q3: What is the "Initialization-on-Demand Holder" idiom, and why is it preferred over DCL?
**Answer:**
It is a thread-safe, lazy-loaded Singleton pattern that does not require the `volatile` keyword or explicit `synchronized` blocks.
```java
public class Singleton {
    private static class Holder {
        static final Singleton INSTANCE = new Singleton();
    }
    public static Singleton getInstance() { return Holder.INSTANCE; }
}
```
**Why it's preferred**: It relies on the JVM's class-loading mechanism. The inner `Holder` class is not loaded into memory until `getInstance()` is called for the first time. The JVM guarantees that class initialization is strictly thread-safe and happens-before any thread can access the class. It is simpler, cleaner, and faster than DCL.

## Q4: Does `volatile` guarantee thread safety for operations like `count++`? Why or why not?
**Answer:**
**No.** `volatile` guarantees *visibility*, but it does not guarantee *atomicity*.
The operation `count++` is actually three separate steps:
1.  Read the current value from main memory.
2.  Add 1 to the value.
3.  Write the new value back to main memory.
Even if `count` is volatile, Thread A and Thread B can both execute step 1 simultaneously, reading the same value (e.g., 5). They both increment to 6, and both write 6 back to memory. An increment is lost. To make `count++` thread-safe, you must use `AtomicInteger` or a `synchronized` block.

## Q5: Explain the "Happens-Before" relationship in the Java Memory Model. Give two examples.
**Answer:**
"Happens-Before" is a formal set of rules that defines when memory writes by one specific operation are guaranteed to be visible to another specific operation. If Operation A happens-before Operation B, B is guaranteed to see A's changes.
**Examples:**
1.  **Monitor Lock Rule**: An unlock on a monitor (exiting a `synchronized` block) happens-before every subsequent lock on that *same* monitor. This guarantees that a thread entering a synchronized block sees all changes made by the previous thread that held that lock.
2.  **Thread Start Rule**: A call to `Thread.start()` happens-before any code executed inside that thread's `run()` method. This guarantees the new thread sees all memory changes made by the parent thread prior to calling `start()`.