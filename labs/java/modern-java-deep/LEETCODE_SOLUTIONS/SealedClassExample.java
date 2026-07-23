package modernjava;

/**
 * Sealed classes and interfaces (Java 17+).
 * 
 * Sealed classes restrict which classes can extend/implement them.
 * This enables exhaustive pattern matching and better domain modeling.
 * 
 * Benefits:
 * - Compiler-enforced exhaustive switch
 * - Clear domain boundaries
 * - No unexpected subclasses
 * 
 * Time: O(1)
 * Space: O(1)
 */
public class SealedClassExample {

    // Sealed hierarchy for JSON values
    sealed interface JsonValue permits JsonNull, JsonNumber, JsonString, JsonArray, JsonObject {}

    record JsonNull() implements JsonValue {
        static final JsonNull INSTANCE = new JsonNull();
    }

    record JsonNumber(double value) implements JsonValue {}
    record JsonString(String value) implements JsonValue {}

    static class JsonArray implements JsonValue {
        private final java.util.List<JsonValue> values;
        JsonArray(java.util.List<JsonValue> values) { this.values = java.util.List.copyOf(values); }
        java.util.List<JsonValue> values() { return values; }
    }

    static class JsonObject implements JsonValue {
        private final java.util.Map<String, JsonValue> map;
        JsonObject(java.util.Map<String, JsonValue> map) { this.map = java.util.Map.copyOf(map); }
        java.util.Map<String, JsonValue> map() { return map; }
    }

    // Exhaustive serialization (compiler verifies all cases covered)
    static String serialize(JsonValue val) {
        return switch (val) {
            case JsonNull n -> "null";
            case JsonNumber n -> String.valueOf(n.value());
            case JsonString s -> "\"" + s.value() + "\"";
            case JsonArray a -> {
                var items = a.values().stream()
                    .map(SealedClassExample::serialize)
                    .collect(java.util.stream.Collectors.joining(","));
                yield "[" + items + "]";
            }
            case JsonObject o -> {
                var entries = o.map().entrySet().stream()
                    .map(e -> "\"" + e.getKey() + "\":" + serialize(e.getValue()))
                    .collect(java.util.stream.Collectors.joining(","));
                yield "{" + entries + "}";
            }
        };
    }

    public static void main(String[] args) {
        JsonValue json = new JsonObject(java.util.Map.of(
            "name", new JsonString("Java"),
            "version", new JsonNumber(21),
            "features", new JsonArray(java.util.List.of(
                new JsonString("Records"),
                new JsonString("Sealed Classes"),
                new JsonString("Pattern Matching")
            )),
            "nullTest", JsonNull.INSTANCE
        ));

        String result = serialize(json);
        System.out.println("Serialized: " + result);
        assert result.contains("\"name\":\"Java\"");
        assert result.contains("\"version\":21.0");
        assert result.contains("\"nullTest\":null");

        // Verify null
        assert serialize(JsonNull.INSTANCE).equals("null");
        assert serialize(new JsonNumber(42)).equals("42.0");
        assert serialize(new JsonString("hello")).equals("\"hello\"");

        System.out.println("All SealedClassExample tests passed.");
    }
}