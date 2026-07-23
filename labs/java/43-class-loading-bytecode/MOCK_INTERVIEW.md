# Mock Interview Transcript: Class Loading & Bytecode

## Interviewer: Staff Engineer, Oracle
## Candidate: Senior Java developer
## Time: 50 minutes
## Focus: Class file format, constant pool, bytecode instructions

---

**Q1: Parse this bytecode for me. What method does it represent?**

```
0: iconst_5
1: istore_1
2: iload_1
3: iconst_3
4: if_icmple 13
7: getstatic #7
10: iload_1
11: invokevirtual #13
14: return
```

**Candidate**: This is a method that: (1) Stores 5 in local variable 1. (2) If local 1 > 3, prints local 1. The `if_icmple` jumps to return if `iload_1 <= 3`. So the body is: `int x = 5; if (x > 3) System.out.println(x);`. The `#7` is `System.out` and `#13` is `PrintStream.println(int)` from the constant pool.

**Interviewer**: Explain the constant pool entries for a method call.

**Candidate**: For `System.out.println("hello")`: (1) `CONSTANT_Fieldref` for `System.out` (class `java/lang/System`, name "out", type `Ljava/io/PrintStream;`). (2) `CONSTANT_String` for "hello". (3) `CONSTANT_Methodref` for `PrintStream.println` (class, name, descriptor `(Ljava/lang/String;)V`). Each references `CONSTANT_Class` and `CONSTANT_NameAndType` entries.

**Interviewer**: How does the verifier check class files?

**Candidate**: The verifier (Java 7+ has type-checking verifier for pre-verified code, Java 8+ uses stack map frames): (1) Checks magic number 0xCAFEBABE. (2) Checks version numbers (major, minor). (3) Validates constant pool entries (correct tag, valid UTF-8). (4) Checks that final methods aren't overridden. (5) Validates that `invokespecial` targets a constructor or superclass method. (6) Stack map frames: verifies that the operand stack types are consistent at every branch target. (7) No falling off the end of a method. The new type-checking verifier is faster (one pass) than the old data-flow verifier.

**Interviewer**: Write a custom ClassLoader that decrypts classes on load.

**Candidate**: 
```java
public class DecryptingClassLoader extends ClassLoader {
    private final String classPath;
    private final SecretKey key;
    
    public DecryptingClassLoader(String classPath, SecretKey key, ClassLoader parent) {
        super(parent);
        this.classPath = classPath;
        this.key = key;
    }
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            String path = classPath + "/" + name.replace('.', '/') + ".encrypted";
            byte[] encrypted = Files.readAllBytes(Path.of(path));
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] classBytes = cipher.doFinal(encrypted);
            return defineClass(name, classBytes, 0, classBytes.length);
        } catch (Exception e) {
            throw new ClassNotFoundException(name, e);
        }
    }
}
```

**Interviewer**: Explain the difference between `loadClass()` and `findClass()`.

**Candidate**: `loadClass()` implements the delegation model — it checks if the class is already loaded, delegates to parent, then calls `findClass()`. Override `loadClass()` only if you want to change the delegation behavior (dangerous — can break JVM assumptions). `findClass()` is the hook for custom class loading — the default implementation throws `ClassNotFoundException`. Always override `findClass()`, never `loadClass()`.

**Interviewer**: How does `javac` compile a lambda? Show the bytecode.

**Candidate**: `list.stream().filter(x -> x > 5)` compiles to:
```
invokedynamic #123, 0  // LambdaMetafactory bootstrap
// Bootstrap arguments:
//   - MethodHandle for lambda$main$0 (private static boolean lambda$main$0(int))
//   - MethodType (Predicate)

// The synthetic method:
private static boolean lambda$main$0(int x) {
    return x > 5;
}
```
No `invokedynamic` for non-capturing lambdas after warmup — the bootstrap creates a constant CallSite pointing to the synthetic method.

**Interviewer**: How does ASM manipulate bytecode?

**Candidate**: ASM reads/writes `.class` files using a visitor pattern:
```java
ClassReader cr = new ClassReader("com.example.MyClass");
ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
cr.accept(new ClassVisitor(Opcodes.ASM9, cw) {
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
            String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("calculate")) {
            return new MethodVisitor(Opcodes.ASM9, mv) {
                @Override
                public void visitCode() {
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", 
                        "out", "Ljava/io/PrintStream;");
                    mv.visitLdcInsn("Entering calculate");
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", 
                        "println", "(Ljava/lang/String;)V", false);
                    super.visitCode();
                }
            };
        }
        return mv;
    }
});
byte[] modified = cw.toByteArray();
```

**Interviewer**: Final: What happens at the class file level when a sealed class is used?

**Candidate**: The compiler adds a `PermittedSubclasses` attribute to the class file. The attribute list the classes permitted to extend it. The verifier checks that any class claiming to extend a sealed class is in the permitted list. This is enforced at the class file level, not just at compile time. `javap -verbose` shows the attribute.

---

## Feedback

**Strengths**:
- Correct bytecode parsing and interpretation
- Constant pool structure knowledge
- Custom ClassLoader with encryption
- ASM bytecode modification
- Sealed class class-file enforcement

**Areas for Improvement**:
- Could discuss stack map frames in detail
- Mention `nest`-based access (Java 11+) for private access between nested classes

**Score**: 5/5 — Expert bytecode and class loading
