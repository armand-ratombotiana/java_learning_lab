# Quiz: Garbage Collection

1. What is the generational hypothesis?
   a) Objects live in generations of the same age
   b) Most objects die young
   c) Old objects are always garbage
   d) Young objects are always live

2. Which GC collector has the shortest pause times?
   a) Serial GC
   b) Parallel GC
   c) G1 GC
   d) ZGC

3. What does G1 stand for?
   a) Generational One
   b) Garbage-First
   c) General-1
   d) Global-1

4. What is a G1 remembered set used for?
   a) Remembering which regions have been collected
   b) Tracking cross-region references
   c) Storing object ages
   d) Caching GC roots

5. What does SATB stand for in G1?
   a) Stop-After-That-Bit
   b) Snapshot-At-The-Beginning
   c) Standard-Allocation-Thread-Buffer
   d) Serial-Age-Tracking-Bit

6. How does ZGC achieve low pause times?
   a) By not collecting the heap
   b) Colored pointers and load barriers for concurrent operations
   c) Parallel mark-sweep in a single thread
   d) By never stopping the application

7. What is a TLAB?
   a) Thread-Local Allocation Buffer
   b) Total Live-Object Bitmap
   c) Two-Level Allocation Bucket
   d) Tenured-Live Allocation Block

8. Which GC collector became the default in Java 9?
   a) Parallel GC
   b) G1 GC
   c) ZGC
   d) CMS

## Answer Key
1-b, 2-d, 3-b, 4-b, 5-b, 6-b, 7-a, 8-b
