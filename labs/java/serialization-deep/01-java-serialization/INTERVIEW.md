# Java Serialization -- Interview Questions

## Common Interview Questions

### Beginner Level

Q1: What is serialization and why is it used in Java?
A: Serialization converts Java objects into a byte stream for storage or transmission. Deserialization reconstructs objects from the byte stream. It is used for persistence, RMI, caching, and inter-service communication.

Q2: What interface must a class implement to be serializable?
A: java.io.Serializable. It is a marker interface with no methods.

Q3: What is the purpose of the transient keyword?
A: Transient fields are not serialized. They are skipped during serialization and initialized to default values on deserialization.

Q4: What is serialVersionUID and why is it important?
A: serialVersionUID is a version identifier for serializable classes. It ensures that the same class version was used during serialization and deserialization. Mismatched UIDs cause InvalidClassException.

### Intermediate Level

Q5: How does Java serialization handle circular references?
A: Java serialization uses a handle table. Each object is assigned a handle on first encounter. Subsequent references write the handle instead of the full object, preventing infinite recursion.

Q6: What is the difference between Serializable and Externalizable?
A: Serializable provides automatic serialization with optional customization via writeObject/readObject. Externalizable gives complete control with mandatory writeExternal/readExternal methods.

Q7: Explain the Serialization Proxy pattern.
A: The main class implements writeReplace to substitute a proxy object during serialization. The proxy handles serialization and implements readResolve to return a properly constructed main class instance.

Q8: How can you customize serialization without implementing Externalizable?
A: By implementing writeObject and readObject private methods. The serialization framework invokes these via reflection if they exist.

### Advanced Level

Q9: How does deserialization create objects without calling constructors?
A: It uses Unsafe.allocateInstance() to allocate memory for the object without invoking any constructor. Fields are then populated via reflection.

Q10: What is a deserialization gadget chain and how do you prevent it?
A: A gadget chain uses available classes in the classpath to execute arbitrary code during deserialization. Prevention includes using ObjectInputFilter, avoiding libraries with known gadgets, and preferring safe serialization formats.

Q11: How does serialization work with inheritance?
A: If a parent class is Serializable, all subclasses are automatically serializable. Non-serializable parent classes must have a no-arg constructor, which is invoked during deserialization.

Q12: Compare Java serialization with JSON, Protobuf, and Kryo in terms of performance, size, and security.
A: Kryo is fastest (5-10x Java), Protobuf produces smallest output (3-6x smaller), JSON is human-readable and cross-platform, Java serialization is most convenient but slowest and least secure.

### Coding Questions

Q13: Write a class that implements Serializable with custom writeObject/readObject that encrypts a sensitive field.

Q14: Implement the readResolve method for a singleton class.

Q15: Write an ObjectInputFilter that only allows deserialization of classes in the com.javalab package.
