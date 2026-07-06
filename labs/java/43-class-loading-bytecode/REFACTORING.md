# Refactoring Class Loading Code

## From Reflection to MethodHandles
**Before:**
```java
Method m = Foo.class.getMethod("bar", int.class);
m.invoke(obj, 42);
```
**After:**
```java
MethodHandle mh = MethodHandles.lookup().findVirtual(Foo.class, "bar",
    MethodType.methodType(void.class, int.class));
mh.invoke(obj, 42);
```
Benefits: faster (MethodHandle is inlined by JIT), checked exception handling.

## From URLClassLoader to custom findClass
**Before:**
```java
URLClassLoader loader = new URLClassLoader(new URL[]{jarUrl});
```
**After:**
```java
class CustomLoader extends URLClassLoader {
    @Override
    public Class<?> findClass(String name) {
        // custom logic before delegation
        return super.findClass(name);
    }
}
```
Benefits: custom loading logic, encryption, transformation.

## From Runtime class generation to ASM
**Before:**
```java
// Generating byte array manually (error-prone)
byte[] bytes = { ... };
```
**After:**
```java
ClassWriter cw = new ClassWriter(0);
cw.visit(V21, ACC_PUBLIC, "Proxy", null, "java/lang/Object", null);
// ... structured bytecode generation
byte[] bytes = cw.toByteArray();
```
Benefits: type-safe bytecode generation, existing library support.

## From anonymous inner classes to lambda (invokedynamic)
**Before:**
```java
myList.forEach(new Consumer<String>() {
    public void accept(String s) { System.out.println(s); }
});
```
**After:**
```java
myList.forEach(s -> System.out.println(s));
```
Benefits: invokedynamic is faster than anonymous class creation, captures variables more efficiently.

## From manual class scanning to ClassGraph
**Before:**
```java
// Recursive file scanning for classes
Files.walk(root).filter(p -> p.endsWith(".class"))...
```
**After:**
```java
// Use ClassGraph library or Spring's ClassPathScanning
ClassPathScanningCandidateComponentProvider scanner = ...
```
Benefits: handles JAR files, module path, ClassLoader isolation correctly.
