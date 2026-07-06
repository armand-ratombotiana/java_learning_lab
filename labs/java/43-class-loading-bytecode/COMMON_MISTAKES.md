# Common Mistakes in Class Loading

## Mistake 1: Assuming Class.forName() Uses the Same Loader
`Class.forName(String)` uses the caller's ClassLoader. `Class.forName(String, boolean, ClassLoader)` allows specifying a loader. Forgetting this causes ClassNotFoundException when loading classes from a different ClassLoader namespace.

## Mistake 2: ClassLoader Memory Leak
Each loaded class holds a reference to its ClassLoader. If the ClassLoader references application objects (e.g., cached instances), the ClassLoader and all its classes can't be GC'd. This causes Metaspace OOM in applications that repeatedly deploy/undeploy (app servers).

## Mistake 3: Breaking the Delegation Model
A custom ClassLoader that always loads classes itself (never delegating to parent) can replace core Java classes. This breaks security assumptions and causes bizarre errors. Always follow delegation: check parent first unless there's a specific reason (like class isolation).

## Mistake 4: Ignoring ClassLoader Casting Issues
A class loaded by ClassLoader A cannot be cast to the same class loaded by ClassLoader B:
```java
// Throws ClassCastException even if the class name is identical
Object o = loaderA.loadClass("Foo").newInstance();
Foo f = (Foo) o; // ClassCastException if Foo loaded by different loader
```

## Mistake 5: ASM Transformer Not Handling All Methods
When using ASM to transform methods, ensure you handle constructors (`<init>`), static initializers (`<clinit>`), and synthetic methods. Modifying these incorrectly can break class initialization.

## Mistake 6: invokedynamic Bootstrap Throwing
If the bootstrap method throws an exception, the class loading fails with a BootstrapMethodError. The bootstrap method must be robust and never assume the arguments are valid.

## Mistake 7: Wrong Class File Version
Using an ASM ClassWriter with the wrong version constant (e.g., V1_8 for a Java 21 class) may generate bytecode that the JVM rejects. Match the version to the target JDK.

## Mistake 8: Modifying the Same Class Bytecode Twice
If two ClassFileTransformers modify the same class, the order matters. The first transformer sees the original bytecode; the second sees the transformed bytecode. Chained transformations must be idempotent.
