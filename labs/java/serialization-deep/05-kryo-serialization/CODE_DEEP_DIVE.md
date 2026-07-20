# Kryo Serialization -- Code Deep Dive

## Main Implementation

### Class Structure
The main class demonstrates Kryo serialization principles.

**Package**: com.javalab.05

### Core Components
1. **Kryo Instance** - Main serialization engine
2. **Serializer Registration** - Registering classes with Kryo
3. **KryoPool** - Thread-safe pool of Kryo instances
4. **Custom Serializer** - Implementing Serializer<T>

### Basic Kryo Usage
Kryo kryo = new Kryo();
kryo.register(Person.class);
Output output = new Output(new FileOutputStream("person.bin"));
kryo.writeObject(output, person);
output.close();
Input input = new Input(new FileInputStream("person.bin"));
Person person = kryo.readObject(input, Person.class);
input.close();

### Registration
kryo.register(Person.class, 1);
kryo.register(Address.class, 2);

### Thread-Safe Pool
KryoPool pool = new KryoPool.Builder(() -> {
    Kryo kryo = new Kryo();
    kryo.register(Person.class);
    kryo.setReferences(true);
    return kryo;
}).softReferences().build();

### Custom Serializer
public class PersonSerializer extends Serializer<Person> {
    public void write(Kryo kryo, Output output, Person person) {
        output.writeString(person.getName());
        output.writeInt(person.getAge());
    }
    public Person read(Kryo kryo, Input input, Class<? extends Person> type) {
        return new Person(input.readString(), input.readInt());
    }
}

### FieldSerializer vs FieldAnnotationSerializer
- FieldSerializer: serializes all non-transient fields
- FieldAnnotationSerializer: only annotated fields

### Performance
- 5-10x faster than Java serialization
- 3-6x smaller output than Java serialization
- Registration required for best performance
- Thread-safe via pooling

### Best Practices
1. Register all classes that will be serialized
2. Maintain consistent registration order
3. Use KryoPool for multi-threaded scenarios
4. Set registrationRequired=true in production
5. Use FieldSerializer for simple POJOs
6. Implement custom serializers for performance-critical classes
