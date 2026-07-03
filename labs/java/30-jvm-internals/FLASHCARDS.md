# Flashcards: JVM Internals

## Card 1: JVM
Java Virtual Machine — runtime engine that executes Java bytecode. Provides platform independence and automatic memory management.

## Card 2: Heap
Runtime data area where all objects and arrays are stored. Shared among all threads. Divided into Young (Eden, S0, S1) and Old generations.

## Card 3: Stack
Thread-private memory storing frames (local variables, operand stack, frame data). Each method call pushes a new frame.

## Card 4: Class Loading
Loading → Verification → Preparation → Resolution → Initialization. Loads .class files, verifies bytecode, prepares static fields, resolves references.

## Card 5: G1 GC
Garbage First collector. Divides heap into regions. Prioritizes collecting regions with most garbage. Default GC since Java 9.

## Card 6: ZGC
Low-latency GC (sub-millisecond pauses). Uses colored pointers and load barriers. Scales to multi-TB heaps. Production since Java 15.

## Card 7: JIT Compilation
Just-In-Time compilation. Hot methods (frequently executed) are compiled from bytecode to native code. C1 (fast) and C2 (aggressive) compilers.

## Card 8: Bytecode
Platform-independent intermediate representation (.class file). Interpreted or JIT-compiled to native code. Verified before execution.

## Card 9: TLAB
Thread Local Allocation Buffer. Each thread gets a small Eden region for allocation without synchronization. Speeds up object allocation.

## Card 10: Compressed OOPs
Ordinary Object Pointers. 32-bit references for heaps < 32GB, enabling smaller object headers and less memory usage.
