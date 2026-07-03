# Deep Dive: Advanced Serialization

## 1. The Basics of Java Serialization
Serialization is the process of converting an object's state into a byte stream so it can be saved to a file, sent over a network, or stored in a database. Deserialization is the reverse process.
In Java, this is achieved by making a class implement the marker interface `java.io.Serializable`.

```java
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private transient String password; // Will not be serialized
}
```
*   **`serialVersionUID`**: A version control number. If you serialize an object, change the class structure (e.g., add a field), and try to deserialize the old byte stream, the JVM will throw an `InvalidClassException` unless the `serialVersionUID` matches.
*   **`transient`**: A keyword that tells the JVM to ignore the field during serialization. Upon deserialization, the field will be initialized to its default value (e.g., `null` for objects, `0` for ints).

## 2. Custom Serialization (`writeObject` and `readObject`)
Sometimes, default serialization isn't enough. You might need to encrypt data before writing it, or initialize `transient` fields upon reading.
You can override the default behavior by implementing two private methods with exact signatures:

```java
private void writeObject(ObjectOutputStream oos) throws IOException {
    oos.defaultWriteObject(); // Do the normal serialization first
    // Add custom logic (e.g., encrypt the password and write it manually)
    oos.writeObject(encrypt(this.password));
}

private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
    ois.defaultReadObject(); // Do the normal deserialization first
    // Add custom logic (e.g., read the encrypted password and decrypt it)
    this.password = decrypt((String) ois.readObject());
}
```

## 3. The Serialization Proxy Pattern
Default deserialization is dangerous because it **bypasses the constructor**. It uses reflection to create the object and populate its fields directly from the byte stream. This means an attacker can craft a malicious byte stream that violates your class's invariants (e.g., creating a `Period` object where the end date is before the start date).

The **Serialization Proxy Pattern** solves this by forcing deserialization to route through your constructors.

```java
public final class Period implements Serializable {
    private final Date start;
    private final Date end;

    public Period(Date start, Date end) {
        if (start.after(end)) throw new IllegalArgumentException();
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
    }

    // 1. Tell the JVM to serialize the Proxy instead of this object
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    // 2. Prevent attackers from deserializing this class directly
    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

    // 3. The Proxy class (must be static and Serializable)
    private static class SerializationProxy implements Serializable {
        private final Date start;
        private final Date end;

        SerializationProxy(Period p) {
            this.start = p.start;
            this.end = p.end;
        }

        // 4. Tell the JVM to return the real object upon deserialization
        private Object readResolve() {
            // Routes through the canonical constructor, enforcing invariants!
            return new Period(start, end); 
        }
    }
}
```

## 4. The Security Nightmare: Deserialization Gadgets
Java serialization is widely considered a massive security flaw. 
When you call `ois.readObject()`, the JVM reads the class name from the byte stream, loads that class, and creates an instance of it. 
If an attacker sends you a byte stream containing a class that exists on your classpath (e.g., a class from a library like Apache Commons Collections), the JVM will instantiate it.

If that class happens to execute code during its `readObject` method or `finalize` method (a "Gadget"), the attacker can achieve **Remote Code Execution (RCE)** on your server.

### Mitigation: ObjectInputFilter (Java 9+)
To defend against this, Java 9 introduced `ObjectInputFilter`. It allows you to strictly whitelist which classes are allowed to be deserialized.

```java
ObjectInputStream ois = new ObjectInputStream(inputStream);

// Only allow deserialization of the User class and strings. Reject everything else.
ObjectInputFilter filter = ObjectInputFilter.Config.createFilter("com.example.User;java.lang.String;!*");
ois.setObjectInputFilter(filter);

Object obj = ois.readObject(); // If the stream contains a malicious gadget, it will be rejected.
```