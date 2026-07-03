# Performance in Core Java

## Key Performance Areas

### 1. Object Creation
- Object allocation is cheap but not free
- Object pooling for expensive objects
- Use primitive types when possible
- Avoid unnecessary autoboxing

### 2. String Operations
- Use StringBuilder for concatenation in loops
- Avoid string concatenation in performance-critical code
- Consider String.intern() for repeated string values
- Use appropriate collection initial capacity

### 3. Collection Performance
- Choose right collection type:
  - ArrayList for indexed access
  - HashSet for uniqueness
  - HashMap for key-value lookup
  - LinkedList for frequent insertion/deletion
- Set initial capacity to reduce rehashing
- Use iterator instead of index-based loops when possible

### 4. Memory Management
- Prefer shallow objects over deep hierarchies
- Clear references to allow GC
- Use primitive arrays for large numeric data
- Consider memory-efficient libraries

## Profiling Tools

- VisualVM for general profiling
- JConsole for JMX-based monitoring
- Flight Recorder for production diagnostics