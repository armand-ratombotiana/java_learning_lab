# Core Java Interview Questions

## Beginner Level

### Q1: Explain the JVM architecture.
**A:** The JVM consists of Class Loader, Bytecode Verifier, Interpreter, JIT Compiler, and Garbage Collector. It loads classes, verifies bytecode safety, interprets or compiles bytecode, and manages memory.

### Q2: What is the difference between heap and stack?
**A:** Heap is where objects are allocated and managed by GC. Stack is where method frames, local variables, and references are stored. Stack is LIFO, heap is not.

### Q3: Explain the four pillars of OOP.
**A:** Encapsulation (data hiding), Inheritance (code reuse), Polymorphism (multiple forms), Abstraction (hiding complexity).

### Q4: What is the difference between abstract class and interface?
**A:** Abstract class can have implemented methods and state; can only extend one. Interface can only have abstract methods (pre-Java 8); a class can implement multiple.

### Q5: What is method overloading vs overriding?
**A:** Overloading: same method name, different parameters in same class. Overriding: subclass redefines parent method with same signature.

## Intermediate Level

### Q6: How does GC work?
**A:** GC identifies unreachable objects, marks them, and reclaims memory. Types include: Serial, Parallel, G1, ZGC, Shenandoah.

### Q7: What is the difference between String, StringBuilder, and StringBuffer?
**A:** String is immutable; StringBuilder is mutable and not thread-safe; StringBuffer is mutable and thread-safe but slower.

### Q8: Explain the volatile keyword.
**A:** Ensures visibility of changes across threads; prevents instruction reordering; does not provide atomicity.

### Q9: What is the purpose of the transient keyword?
**A:** Excludes a field from serialization.

### Q10: Explain the Object class methods.
**A:** equals(), hashCode(), toString(), clone(), wait(), notify(), notifyAll(), getClass()