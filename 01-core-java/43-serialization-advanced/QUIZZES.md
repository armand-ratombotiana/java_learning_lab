# Quizzes: Advanced Serialization

Test your knowledge of custom serialization, security filters, and proxy patterns.

## Quiz 1: Core Mechanics

**Q1: What happens if you serialize an object, change the class by adding a new field, and then try to deserialize the old byte stream without having defined a `serialVersionUID`?**
- A) The JVM automatically sets the new field to `null` and succeeds.
- B) The JVM throws an `InvalidClassException` because the automatically generated UID of the new class structure does not match the UID embedded in the old byte stream.
- C) The JVM throws a `NullPointerException`.
- D) The JVM ignores the new field.
*Answer: B*

**Q2: During standard Java deserialization, which of the following is true?**
- A) The JVM calls the no-argument constructor of the class.
- B) The JVM calls the constructor that matches the serialized fields.
- C) The JVM bypasses the class's constructors entirely and uses reflection to populate the fields directly from the byte stream.
- D) The JVM calls the `init()` method.
*Answer: C (This is why deserialization is dangerous; it bypasses invariant checks in the constructor).*

## Quiz 2: Security and Proxies

**Q1: How does the Serialization Proxy Pattern solve the security risks of default deserialization?**
- A) It encrypts the byte stream using AES-256.
- B) It forces the deserialization process to return a Proxy object. The Proxy object then uses its `readResolve()` method to call the *actual class's constructor*, ensuring that all invariant checks and validation logic are executed before the final object is created.
- C) It uses a digital signature.
- D) It prevents the use of the `transient` keyword.
*Answer: B*

**Q2: What is a "Deserialization Gadget" in the context of Java security?**
- A) A tool used by developers to debug serialized data.
- B) A class existing on the classpath that, if instantiated by an attacker via a malicious serialized byte stream, executes harmful code during its `readObject` or `finalize` methods, potentially leading to Remote Code Execution (RCE).
- C) A specific type of `ObjectInputStream`.
- D) A Java 9 feature for filtering streams.
*Answer: B*

## Quiz 3: Edge Cases

**Q1: You implement the Singleton pattern with a private constructor and a static `getInstance()` method. You also implement `Serializable`. What must you do to ensure the Singleton contract is not broken during deserialization?**
- A) Make the `getInstance` method `synchronized`.
- B) Implement the `writeReplace()` method.
- C) Implement the `readResolve()` method to discard the newly deserialized instance and return the existing true Singleton instance.
- D) Use the `transient` keyword on the instance variable.
*Answer: C*