# Common Mistakes with Java 21 Features

## Virtual Threads

### 1. Using ThreadPoolExecutor
```java
// Old way - not optimal for virtual threads
ExecutorService exec = Executors.newFixedThreadPool(100);

// New way - per-task virtual threads
ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();
```

### 2. ThreadLocal Overuse
```java
// Each virtual thread has its own ThreadLocal
// OK if small, but thousands of threads = thousands of copies
ThreadLocal<byte[]> buffer = ThreadLocal.withInitial(() -> new byte[1024]);
```

### 3. Synchronized Blocks
```java
// Virtual threads can be pinned if using synchronized
synchronized(lock) {  // Can cause performance issues
    // ...
}
// Use java.util.concurrent locks instead
```

## Pattern Matching

### 1. Forgetting Null Checks
```java
// All cases must handle null
switch(obj) {
    case null -> "null";  // Must include!
    case String s -> ...
}
```

### 2. Incorrect Guards
```java
// Guard must come after type pattern
case Integer i && i > 0 -> "positive";  // Correct
case Integer > 0 i -> "positive";       // Syntax error
```

## Sequenced Collections

### 1. Using Wrong Interface
```java
// Regular List doesn't have getFirst()
List<String> list = new ArrayList<>();
// Use:
List<String> list = new ArrayList<>();
list.addFirst("first");  // Works, but
// Better:
SequencedCollection<String> seq = new ArrayList<>();
seq.addFirst("first");
```

## String Templates

### 1. Missing Preview Enablement
```java
// Enable in module-info.java
// requires jdk.incubator.stringtemplate;

// Or via command line
// --add-exports jdk.incubator.stringtemplate/jdk.internal.template=ALL-UNNAMED
```