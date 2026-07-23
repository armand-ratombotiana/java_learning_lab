# Mock Interview Transcript: Reflection

## Interviewer: Staff Engineer, Oracle
## Candidate: Senior Java developer
## Time: 40 minutes
## Focus: Reflection API, performance, module access, MethodHandle

---

**Q1: What's the Class object? How do you obtain it?**

**Candidate**: Every Java object has a `Class` object that represents its runtime type. Three ways: (1) `obj.getClass()` — from an instance. (2) `ClassName.class` — class literal. (3) `Class.forName("fully.qualified.ClassName")` — by fully qualified name. The Class object is created by the JVM when the class is loaded.

**Interviewer**: What can you do with reflection that you can't do with normal code?

**Candidate**: (1) Inspect classes at runtime (methods, fields, annotations). (2) Access private fields/methods. (3) Create instances without knowing the class at compile time. (4) Invoke methods dynamically. (5) Load classes dynamically. (6) Get/set field values without compile-time type knowledge.

**Interviewer**: When should you NOT use reflection?

**Candidate**: (1) Performance-critical paths — reflection is slower than direct calls (JIT can inline normal calls but not reflected ones). (2) Type safety is lost — errors become runtime exceptions instead of compile errors. (3) Security — reflection can break encapsulation. (4) Module system (Java 9+) restricts reflective access to exported packages. (5) Code readability — reflective code is harder to understand.

**Interviewer**: How does the module system (Java 9+) restrict reflection?

**Candidate**: In Java 9+, the module system controls reflective access. A module exports packages via `exports` in `module-info.java`. If a package isn't exported, reflection can't access it (throws `InaccessibleObjectException`). You can open packages with `opens` for deep reflection. The `--add-opens` JVM flag provides a runtime workaround.

**Interviewer**: Compare `Method.invoke()` with `MethodHandle.invoke()`.

**Candidate**: `MethodHandle` (Java 7+) is faster and more type-safe: (1) MethodHandle is a typed, direct pointer to the underlying method — JIT can inline it. (2) Method.invoke() does boxing, varargs array creation, access checking each time. (3) MethodHandle has `.invoke()` (with type checking) and `.invokeExact()` (exact type match). (4) MethodHandle supports different call types (invokevirtual, invokespecial, etc.) while Method is always virtual.

**Interviewer**: Write a method that creates a new instance of a class by name, calling a one-arg constructor.

**Candidate**: 
```java
<T> T createInstance(String className, Class<T> type, Object arg) {
    Class<?> clazz = Class.forName(className);
    Constructor<?> ctor = clazz.getConstructor(arg.getClass());
    return type.cast(ctor.newInstance(arg));
}
```

**Interviewer**: How would you handle constructor parameters with different types? What if there are multiple constructors?

**Candidate**: 
```java
<T> T createInstance(String className, Class<T> type, Object... args) {
    Class<?> clazz = Class.forName(className);
    for (Constructor<?> ctor : clazz.getConstructors()) {
        Class<?>[] paramTypes = ctor.getParameterTypes();
        if (paramTypes.length == args.length) {
            boolean match = true;
            for (int i = 0; i < args.length; i++) {
                if (!paramTypes[i].isInstance(args[i]) && args[i] != null) {
                    match = false; break;
                }
            }
            if (match) return type.cast(ctor.newInstance(args));
        }
    }
    throw new NoSuchMethodException("No matching constructor");
}
```

**Interviewer**: How does the JVM implement `Field.setAccessible(true)`?

**Candidate**: `setAccessible(true)` suppresses Java language access checking. Prior to Java 9, this could access private fields in any class. In Java 9+, the module system controls this: for exported packages, `setAccessible` only works for the module's own classes. For opened packages, deep reflection is allowed. The implementation modifies a `FieldAccessor` or `MethodAccessor` object's override flag.

**Interviewer**: How does the JIT optimize `Method.invoke()` after many calls?

**Candidate**: The JVM has an "inflation" threshold. Initially, each `Method.invoke()` call uses native code (slow). After 15 calls (default), the JVM generates bytecode for that specific method, creating a `GeneratedMethodAccessor` that calls the target directly. At 15+ calls, it's comparable to direct calls. You can adjust with `-Dsun.reflect.inflationThreshold=0` to start at generated accessor.

---

## Feedback

**Strengths**:
- Comprehensive reflection API knowledge
- Understands Module system restrictions
- Correctly compares MethodHandle vs Method
- Knows JIT inflation mechanism

**Areas for Improvement**:
- Could mention `VarHandle` (Java 9+) for field access
- Should discuss `ClassLoader.defineClass()` and dynamic class creation

**Score**: 4/5 — Deep reflection understanding
