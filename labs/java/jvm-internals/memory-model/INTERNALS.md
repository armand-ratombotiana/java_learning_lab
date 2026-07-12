# JVM Memory Internals

## 🔬 Object Memory Layout
When you create a new object on the Heap using `new Object()`, how much memory does it actually take?

In the HotSpot JVM, an object consists of:
1. **Mark Word (8 bytes)**: Contains identity hashcode, GC age (tenuring), and lock state (biased locking, lightweight locking).
2. **Klass Pointer (4 bytes with Compressed Oops, 8 bytes without)**: A pointer to the class metadata in the Metaspace. This is how the JVM knows what type of object it is.
3. **Instance Data**: The actual fields of the object (e.g., an `int` takes 4 bytes, a `boolean` takes 1 byte).
4. **Padding**: The JVM requires objects to be aligned on 8-byte boundaries for CPU cache efficiency. If the total size is 17 bytes, the JVM will add 7 bytes of padding to make it 24 bytes.

Therefore, an absolutely empty object `new Object()` takes **16 bytes** of memory on a 64-bit JVM with compressed pointers enabled.

## 🗑️ Garbage Collection Roots (GC Roots)
How does the Garbage Collector know which objects on the Heap are safe to delete?
It uses a process called **Reachability Analysis**.

The GC starts at specific memory locations known as **GC Roots**. If an object on the Heap can be reached by following a chain of references starting from a GC Root, it is "alive". If it cannot be reached, it is "dead" and will be collected.

What constitutes a GC Root?
1. **Local Variables on the Stack**: Any object currently being referenced by a method executing in any thread.
2. **Static Variables**: Objects referenced by static fields of loaded classes in the Metaspace.
3. **JNI References**: Objects held by native code.
4. **Active Threads**: The Thread objects themselves.

## 🛑 Memory Leaks in Java
Because Java has a Garbage Collector, developers often assume memory leaks are impossible. This is false.
A memory leak in Java occurs when an object is no longer needed by the application logic, but a **GC Root still holds a strong reference to it**, preventing the GC from cleaning it up.

Common causes:
- Adding objects to a static `HashMap` or `List` and forgetting to remove them.
- Unclosed resources (Database connections, ThreadLocals).
- Inner classes holding implicit references to their outer classes.