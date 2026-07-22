# Interview Questions: Class Loading

## Company-Specific Focus

### Google
- Class loading phases: loading, linking (verification, preparation, resolution), initialization
- Delegation model: parent-first class loading hierarchy
- Custom class loaders: findClass vs loadClass overriding

### Microsoft
- Java class loading vs .NET assembly loading
- Parent delegation: why it ensures type safety

### Amazon
- ClassLoader leak: redeployment in Tomcat causes OOM from unreferenced classloaders
- Module path vs classpath: how JPMS changes class loading

### Meta
- Loading: finding the binary representation of a class
- Verification: bytecode verifier checks type safety
- Preparation: allocating memory for static fields
- Resolution: symbolic references to direct references
- Initialization: executing static initializers

### Apple
- Class.forName vs ClassLoader.loadClass: difference in initialization
- Thread context class loader: breaking the delegation model

### Oracle
- JVM Specification Chapter 5: loading, linking, initialization
- Class loader hierarchy: Bootstrap, Platform, Application
- resolveClass: linking a class after loading
- Custom class loader: URLClassLoader, defining class bytes

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — class loading is a JVM mechanism) |

## Real Production Scenarios
- **LinkedIn**: ClassLoader leak from JDBC driver registration — redeployment caused OOM after 20 restarts
- **Airbnb**: Custom class loader for plugin system — isolation prevented plugin conflicts

## Interview Patterns & Tips
- **Parent delegation**: child asks parent before loading
- **Loading**: find the class file and create the binary representation
- **Verification**: ensures bytecode is valid and type-safe
- **Initialization**: static blocks and static field initialization

## Deep Dive Questions
- **Delegation**: How does parent delegation work?
- **Linking**: What are the three steps of linking?
- **Initialization**: When does class initialization occur?
- **ClassLoader leak**: Why does incorrectly removing a classloader cause OOM?
- **Thread context class loader**: Why is it needed?