# Flashcards: Class Loading & Bytecode

**Q: What are the three phases of class loading?**
A: Loading (find bytes + create Class), Linking (verify + prepare + resolve), Initialization (run static initializers).

**Q: What is the delegation model in ClassLoaders?**
A: When loading a class, first check if already loaded, then ask parent loader, then try to load from own sources.

**Q: What is the constant pool in a .class file?**
A: A table of constants and symbolic references (strings, class names, field/method references) used by bytecode instructions.

**Q: What does the bytecode verifier check?**
A: Type safety, operand stack depth, local variable initialization, access control, and final class/method restrictions.

**Q: How does ASM's visitor pattern work for bytecode?**
A: ClassReader parses bytecode and fires events to ClassVisitor. ClassVisitor transforms events. ClassWriter collects events to produce new bytecode.

**Q: What is invokedynamic used for?**
A: Lambda expressions, string concatenation (Java 9+), pattern matching, and dynamic language implementations on the JVM.

**Q: How does the LambdaMetafactory bootstrap work?**
A: At runtime, it creates a CallSite that generates the lambda implementation as a MethodHandle or inner class.

**Q: What is a BootstrapMethodError?**
A: An error thrown when a bootstrap method (e.g., LambdaMetafactory) fails during invokedynamic linking.
