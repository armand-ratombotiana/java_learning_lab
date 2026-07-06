# Quiz: Class Loading & Bytecode

1. What is the magic number that starts every .class file?
   a) 0xDEADBEEF
   b) 0xCAFEBABE
   c) 0xFEEDFACE
   d) 0xCOFFEECA

2. Which ClassLoader returns null from getParent()?
   a) Application ClassLoader
   b) Platform ClassLoader
   c) Bootstrap ClassLoader
   d) Custom ClassLoader

3. In the delegation model, what happens when a ClassLoader cannot find a class?
   a) It throws ClassNotFoundException immediately
   b) It delegates to its parent
   c) It calls findClass() first
   d) It returns null

4. What does the ASM ClassWriter do?
   a) Reads bytecode
   b) Transforms bytecode
   c) Generates new bytecode
   d) Verifies bytecode

5. What is the purpose of invokedynamic?
   a) To call methods dynamically using reflection
   b) To defer method dispatch to a bootstrap method at runtime
   c) To invoke native methods
   d) To call methods in a different JVM

6. What is the LambdaMetafactory used for?
   a) To create lambda instances from compiled bytecode
   b) To compile lambda expressions at runtime
   c) To verify lambda type safety
   d) To serialize lambda instances

7. Which bytecode instruction is used for toString() call on an object?
   a) invokestatic
   b) invokevirtual
   c) invokespecial
   d) invokeinterface

8. What happens to a class when its ClassLoader is garbage collected?
   a) The class is immediately unloaded
   b) The class becomes eligible for unloading
   c) The class persists forever
   d) The JVM crashes

## Answer Key
1-b, 2-c, 3-b, 4-c, 5-b, 6-a, 7-b, 8-b
