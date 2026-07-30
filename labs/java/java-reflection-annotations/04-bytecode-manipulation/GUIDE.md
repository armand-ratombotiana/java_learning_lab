# GUIDE — Bytecode Manipulation

## Step 1: Class File Structure
- Magic number `0xCAFEBABE`, constant pool, access flags, interfaces, fields, methods, attributes

## Step 2: ASM — Reading Classes
```java
ClassReader reader = new ClassReader("com.example.MyClass");
ClassWriter writer = new ClassWriter(reader, 0);
reader.accept(new ClassVisitor(Opcodes.ASM9, writer) {
    // override visitMethod etc.
}, 0);
```

## Step 3: ASM — Writing Classes
- Use `ClassWriter` to generate a new class from scratch
- Emit `visit`, `visitField`, `visitMethod`, `visitEnd`

## Step 4: ByteBuddy
```java
Class<?> dynamicType = new ByteBuddy()
    .subclass(Object.class)
    .method(ElementMatchers.named("toString"))
    .intercept(FixedValue.value("Hello ByteBuddy!"))
    .make()
    .load(getClass().getClassLoader())
    .getLoaded();
```

## Step 5: Exercises
1. Use ASM to add a `@Loggable` method interceptor
2. Generate a POJO with getters/setters via ByteBuddy
3. Create a simple mocking framework
