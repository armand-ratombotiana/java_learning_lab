# Java Serialization -- Code Deep Dive

## Main Implementation

### Class Structure
The main class demonstrates Java serialization fundamentals:

**Package**: com.javalab.01

### Core Components
1. **Serializable POJO** - A class implementing java.io.Serializable
2. **Custom Serialization** - writeObject/readObject overrides
3. **Externalizable** - Full control over serialization
4. **Serialization Proxy** - Proxy pattern for safe serialization

### Key Methods

#### writeObject
Called during serialization to write custom data:
private void writeObject(ObjectOutputStream oos) throws IOException {
    oos.defaultWriteObject();
    oos.writeUTF(this.derivedField);
}

#### readObject
Called during deserialization to read custom data:
private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
    ois.defaultReadObject();
    this.derivedField = ois.readUTF();
}

#### readResolve
Replaces the deserialized object (used for singletons):
private Object readResolve() throws ObjectStreamException {
    return INSTANCE;
}

#### writeReplace
Replaces the object being serialized (used for proxy pattern):
private Object writeReplace() throws ObjectStreamException {
    return new SerializationProxy(this);
}

### Externalizable Implementation
Externalizable provides complete control:
public void writeExternal(ObjectOutput out) throws IOException {
    out.writeInt(id);
    out.writeUTF(name);
}
public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    this.id = in.readInt();
    this.name = in.readUTF();
}

## Code Walkthrough

### Serialization Flow
1. Create FileOutputStream and ObjectOutputStream
2. Call writeObject() on the root object
3. Stream traverses object graph depth-first
4. Each new object gets a handle and is serialized
5. Existing handles are written as references

### Deserialization Flow
1. Create FileInputStream and ObjectInputStream
2. Call readObject() to reconstruct the root
3. Stream reads class descriptors and field data
4. Objects are allocated without constructors
5. Fields are populated from stream data

### Handling Inheritance
- Subclasses of a serializable parent inherit serializability
- Non-serializable parents require a no-arg constructor
- The first non-serializable class in the hierarchy must have a no-arg constructor

### Transient Fields
Fields marked transient are not serialized:
private transient String cachedValue;
On deserialization, transient fields are initialized to their default values.

## Best Practices
1. Always declare serialVersionUID
2. Make the field private static final long
3. Use transient for derived or sensitive fields
4. Implement custom serialization for complex objects
5. Consider Serialization Proxy pattern for security
6. Validate data in readObject
7. Use readResolve for singletons and enums
8. Avoid serializing inner classes
9. Be careful with serialization and class evolution
10. Test serialization compatibility

## Performance Considerations
- Default serialization uses reflection, which is slow
- Custom writeObject/readObject can improve performance
- Externalizable provides the best performance
- writeReplace/readResolve add overhead
- ObjectOutputStream buffering affects throughput
