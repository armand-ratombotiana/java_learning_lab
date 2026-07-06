# Tests for Class Loading & Bytecode

## Test Categories

### Unit Tests (ClassLoadingBytecodeTest.java)
- `testClassLoaderHierarchy()` — verifies the class loader chain is non-null
- `testCustomClassLoaderDelegation()` — verifies parent delegation is set correctly
- `testBytecodeAnalyzer()` — validates parsing of .class file structure without exceptions
- `testAsmTransformation()` — verifies ASM adds annotation bytes to transformed class
- `testInvokeDynamic()` — verifies lambda metafactory invocation

### Running Tests
```bash
mvn test -pl labs/java/43-class-loading-bytecode
```

### Test Dependencies
- ASM 9.7 (org.ow2.asm:asm) is required for AsmTransformer
- Java Compiler API (tools.jar) for custom class generation

### Additional Test Scenarios
- Load a class from custom ClassLoader and verify its behavior
- Parse a known .class file and verify magic number, version, constant pool count
- Transform a class with ASM and verify the annotation exists
- Verify invokedynamic bootstrap method chain works correctly

### Coverage Targets
- ClassLoader delegation: 3-level hierarchy verified
- Bytecode analysis: magic number, version, flags, method count
- ASM transform: annotation injection bytes differ from original
- invokedynamic: lambda creation and method handle invocation
