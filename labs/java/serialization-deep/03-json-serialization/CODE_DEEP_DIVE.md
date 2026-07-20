# JSON Serialization -- Code Deep Dive

## Main Implementation

### Class Structure
The main class demonstrates JSON serialization with Jackson.

**Package**: com.javalab.03

### Core Components
1. **ObjectMapper** - Jackson main serialization facade
2. **Annotations** - @JsonProperty, @JsonIgnore, @JsonFormat
3. **Custom Serializer** - JsonSerializer<T> implementation
4. **Custom Deserializer** - JsonDeserializer<T> implementation

### Basic ObjectMapper Usage
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(person);
Person person = mapper.readValue(json, Person.class);

### Key Annotations
@JsonProperty("full_name") - custom property name
@JsonIgnore - exclude field from serialization
@JsonFormat(pattern = "yyyy-MM-dd") - date format
@JsonProperty(access = Access.READ_ONLY) - read-only property

### Custom Serializer
public class PersonSerializer extends JsonSerializer<Person> {
    public void serialize(Person value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", value.getName());
        gen.writeNumberField("age", value.getAge());
        gen.writeEndObject();
    }
}

### Custom Deserializer
public class PersonDeserializer extends JsonDeserializer<Person> {
    public Person deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        return new Person(node.get("name").asText(), node.get("age").asInt());
    }
}

### Jackson vs Gson vs JSON-B
- Jackson: Most features, best performance, extensive annotations
- Gson: Simpler API, good for basic use cases
- JSON-B: Jakarta EE standard, annotation-driven, portable

### Performance Tuning
1. ObjectMapper is thread-safe - reuse it
2. Use readerFor/writerFor for type-safe operations
3. Register modules for Joda-Time, Java 8 Date/Time
4. Enable INDENT_OUTPUT for debugging only
