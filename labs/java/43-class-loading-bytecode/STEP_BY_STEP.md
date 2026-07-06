# Step-by-Step: Class Loading & Bytecode

## Step 1: Run ClassLoaderHierarchy.java
The output shows the three-level hierarchy. Note the Bootstrap ClassLoader returns null from getParent() — it's the primordial loader implemented natively. The Platform ClassLoader's definedPackages() lists JDK module packages.

## Step 2: Run CustomClassLoader.java
The custom loader is created with a Platform ClassLoader parent. When `loadClass("CustomClassLoader")` is called, the parent (Platform) delegates to Bootstrap, which can't find it, then Platform fails, then findClass() looks in the custom directory. The class is eventually found by the Application ClassLoader (parent delegation chain).

## Step 3: Run BytecodeAnalyzer.java
The analyzer reads its own .class file. Output shows:
- Magic: 0xCAFEBABE
- Version: JDK 21 format
- Constant pool entries (skipped in parsing)
- Access flags, this/super class, interfaces, fields, methods

Compare with `javap -v BytecodeAnalyzer.class` for the full disassembly.

## Step 4: Run AsmTransformer.java
The ASM transformation adds a class-level annotation. Compare the byte array sizes before and after transformation. The transformed class now has an additional `RuntimeVisibleAnnotations` attribute. The annotation count (from `ClassReader.getAnnotationCount()`) should be 1.

## Step 5: Run InvokeDynamicExample.java
The lambda expression `String::toUpperCase` demonstrates invokedynamic. The manual `LambdaMetafactory.metafactory()` call shows what the compiler generates automatically. Both produce Function instances that call toUpperCase.

## Step 6: Run ClassLoadingBytecodeTest.java
Run the JUnit tests to verify all class loading and bytecode transformation functionality.
