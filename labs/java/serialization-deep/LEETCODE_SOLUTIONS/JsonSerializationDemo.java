package serialization;

import java.util.*;

/**
 * JSON Serialization demonstration.
 * 
 * Shows: Jackson (most popular) and alternative approaches.
 * 
 * Maven dependency:
 *   com.fasterxml.jackson.core:jackson-databind:2.16.0
 *   com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0
 * 
 * This class simulates Jackson's API for demonstration.
 */
public class JsonSerializationDemo {

    // Data classes
    record Address(String street, String city, String zip) {}
    record Person(String name, int age, Address address, List<String> hobbies) {}

    // Simple JSON serializer (demonstration — not production quality)
    static class JsonSerializer {
        static String toJson(Object obj) {
            if (obj == null) return "null";
            if (obj instanceof String s) return "\"" + escape(s) + "\"";
            if (obj instanceof Number || obj instanceof Boolean) return obj.toString();
            if (obj instanceof Collection<?> c) {
                var items = c.stream().map(JsonSerializer::toJson).collect(java.util.stream.Collectors.joining(","));
                return "[" + items + "]";
            }
            if (obj instanceof Map<?, ?> m) {
                var entries = m.entrySet().stream()
                    .map(e -> "\"" + e.getKey() + "\":" + toJson(e.getValue()))
                    .collect(java.util.stream.Collectors.joining(","));
                return "{" + entries + "}";
            }
            // Record support via reflection
            var clazz = obj.getClass();
            if (clazz.isRecord()) {
                var components = clazz.getRecordComponents();
                var sb = new StringBuilder("{");
                for (int i = 0; i < components.length; i++) {
                    if (i > 0) sb.append(",");
                    try {
                        var val = components[i].getAccessor().invoke(obj);
                        sb.append("\"").append(components[i].getName()).append("\":").append(toJson(val));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                sb.append("}");
                return sb.toString();
            }
            return obj.toString();
        }

        private static String escape(String s) {
            return s.replace("\\", "\\\\").replace("\"", "\\\"")
                    .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
        }
    }

    public static void main(String[] args) {
        var person = new Person("Alice", 30,
            new Address("123 Main St", "Springfield", "12345"),
            List.of("reading", "coding", "hiking"));

        String json = JsonSerializer.toJson(person);
        System.out.println("JSON output: " + json);

        assert json.contains("\"name\":\"Alice\"");
        assert json.contains("\"age\":30");
        assert json.contains("\"city\":\"Springfield\"");
        assert json.contains("\"hobbies\":[\"reading\",\"coding\",\"hiking\"]");

        // Jackson real-world usage:
        System.out.println("\nReal Jackson usage:");
        System.out.println("  ObjectMapper mapper = new ObjectMapper();");
        System.out.println("  String json = mapper.writeValueAsString(person);");
        System.out.println("  Person p = mapper.readValue(json, Person.class);");
        System.out.println("  (Register JavaTimeModule for java.time support)");

        System.out.println("All JsonSerializationDemo tests passed.");
    }
}