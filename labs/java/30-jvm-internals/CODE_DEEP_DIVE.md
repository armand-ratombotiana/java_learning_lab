# Code Deep Dive: JVM Internals

## Bytecode Example

### Java Source
```java
public int add(int a, int b) {
    return a + b;
}
```

### Corresponding Bytecode
```java
public int add(int, int);
  Code:
   0: iload_1          // Push local variable 1 (a) onto stack
   1: iload_2          // Push local variable 2 (b) onto stack
   2: iadd             // Pop two ints, add, push result
   3: ireturn          // Return top of stack (int)
```

### Java Source with Objects
```java
public String concat(String a, String b) {
    return a + b;
}
```

### Bytecode
```java
public java.lang.String concat(java.lang.String, java.lang.String);
  Code:
   0: aload_1
   1: aload_2
   2: invokedynamic #7,   // makeConcatWithConstants (java 9+)
   7: areturn
```

## String Concatenation Bytecode
Before Java 9: StringBuilder append/toString
After Java 9: invokedynamic with StringConcatFactory

## Synchronized Block Bytecode
```java
// Source
public synchronized void syncMethod() { ... }

// Bytecode
public synchronized void syncMethod();
  Code:
   0: aload_0
   1: monitorenter     // Enter monitor (this)
   2: ...
   N: monitorexit      // Exit monitor (normal)
   N+1: ...
   Exception table (catch any → monitorexit)
```

## Virtual Method Dispatch
```java
// Source
list.add("hello");  // List interface

// Bytecode (before resolution)
invokeinterface #12,  2  // InterfaceMethod java/util/List.add:(Ljava/lang/Object;)Z
// #12 is a symbolic reference, resolved during class linking
```
