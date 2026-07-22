# Interview Questions: Reflection

## Company-Specific Focus

### Google
- Reflection API architecture: Class, Method, Field, Constructor objects
- Performance overhead of reflection vs direct calls and when to cache them
- Java 9+ module system and its limitations on illegal reflections (opens statement)

### Microsoft
- Java reflection vs C# reflection: access, dynamic invocation and generics
- The reflection API for generic type representation (ParameterizedType, TypeVariable)
- JVM security model for reflection in the Java 9+ environment

### Amazon
- Programming higher abstraction by loaded on the fly in the graph QL engine
- Reflection type arguments: transaction scripts and auto-wiring

### Meta
- Reflection with generic types: TypeToken pattern for achieving retention of type parameters
- Anonymous vs inner classes represented via reflection: getCanonicalName
- Underlying set methods: Unlock Reflection

### Apple
- Use class.newInstance vs Constructor.newInstance: differences in the event of an exception
- Efficient use of caching in Field/Class lookups
- Reflection and final field changes: what's changed in the JDK version

### Oracle
- JLS: reflection in the context of the JVM spec for accessing class
- How JVM implements reflection setAccessible, and the role of Field.override
- Reflective optimization in the JIT: optimize derived level checks
- 17+ update: The reflection filter of the module system? (Reflection Filter API)

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 146 LRU Cache | Medium | Google, Apple, Amazon, Microsoft | Reflection is not directly used but in a library design |
| 138 Copy List with Random Pointer | Medium | Google, Amazon, Microsoft, Apple | Deep copy resembles reflection type traversal |
| 133 Clone Graph | Medium | Google, Apple, Facebook | Traversal history resembles a linked reflex |
| 146 LRU Cache | Medium | Apple, Google, Amazon | Similar pattern with get/put on fields |
| 53 Maximum Subarray | Easy | Apple, Amazon, Google, Microsoft | Not direct reflection pattern |

## Real Production Scenarios
- **Netflix**: A reflective call to a private method of a Java internal class broke the upgrade from JDK 8 to 17 causing the entire application to crash
- **Uber**: Hot path of a reactive stream deserialization with Field.set() caused 200ms p99 because no Field instance was cached
- **Shopify**: Using reflection for object relational mapping synchronization of field names and the DTO caused UTF-8 performance issues

## Interview Patterns & Tips
- **setAccessible(tRuE)**: If a security manager does not prevent it, this allows private individual access
- **Caching**: In high-frequency reflection you get field/method objects store them.
- **java.util.Objects**: Use the fromArray method and do not try to create complex bridges.

## Deep Dive Questions
- **JVM**: What makes reflection fast from JDK8 vs JDK17? Access test vs method handle
- **Bytecode generation**: What does the caller sensitive behavior mean about JVM jack?
- **Optimization**: Can the JIT compile the Method.invoke to the actual method if the implementation is detected?
- **Module system**: How does the module access control to types at runtime affect the reflection code?
- **Java 21+**: How does JNI change relating to Runtime for the implied?