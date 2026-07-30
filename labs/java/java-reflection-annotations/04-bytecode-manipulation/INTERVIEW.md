# INTERVIEW — Bytecode Manipulation

## Company-Specific Focus

### Google
- ASM vs ByteBuddy — when to use each
- Instrumentation agents and `java.lang.instrument`

### Amazon
- Mockito / EasyMock — how they use ByteBuddy for mock generation
- Class retransformation in production

### Oracle
- `-javaagent` flag and `ClassFileTransformer`
- Bytecode verification and security implications

## Common Questions
1. What does the `ACC_PRIVATE` access flag value mean (0x0002)?
2. How does ByteBuddy implement `MethodDelegation`?
3. Can you modify a class after it has been loaded?
