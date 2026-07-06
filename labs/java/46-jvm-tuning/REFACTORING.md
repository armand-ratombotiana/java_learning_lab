# Refactoring for JVM Tuning

## From Large Heap to Right-Sized Heap
**Before:** Default heap (may be too large or too small):
```bash
java -jar application.jar
```
**After:** Measured right-sized heap:
```bash
java -Xms2g -Xmx2g -Xmn512m -jar application.jar
```

## From Unbounded Metaspace to Bounded
**Before:**
```bash
java -jar application.jar  # Metaspace unlimited
```
**After:**
```bash
java -XX:MaxMetaspaceSize=256m -jar application.jar
```

## From Manual String Concatenation to StringBuilder (JIT-friendly)
**Before:**
```java
String result = a + b + c; // multiple allocations
```
**After:**
```java
String result = a + b + c; // Java 9+ uses invokedynamic
```
String concatenation in Java 9+ is optimized via invokedynamic with `StringConcatFactory`, which adapts to the call site.

## From Reflection to MethodHandles (Code Cache Friendly)
**Before:**
```java
Method m = clazz.getMethod("foo"); m.invoke(obj);
```
**After:**
```java
MethodHandle mh = lookup.findVirtual(clazz, "foo", MT);
mh.invoke(obj);
```
Method handles compile to direct calls after JIT warmup, reducing code cache pressure from reflection stubs.

## From Wildcard Classpath to Module Path
**Before:**
```bash
java -cp "lib/*:classes" com.example.Main
```
**After:**
```bash
java --module-path lib:classes -m com.example/com.example.Main
```
Module path provides faster class loading and better startup time.

## From Payload Merging to String Dedup
**Before:**
```java
String json = new String("{\"key\":\"value\"}");
```
**After:**
```java
String json = "{\"key\":\"value\"}"; // string literal (interned)
```
Or enable `-XX:+UseStringDeduplication` for automatic dedup.
