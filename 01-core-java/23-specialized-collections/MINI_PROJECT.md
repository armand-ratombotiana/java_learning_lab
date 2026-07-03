# Mini Project: Object Graph Serializer

## Objective
Build a simple object serializer that converts Java objects to a JSON-like string. You will use `IdentityHashMap` to detect circular references (preventing infinite loops) and `WeakHashMap` to cache class metadata without causing memory leaks.

## Prerequisites
*   Java 17+

## Step 1: The Metadata Cache (WeakHashMap)
Reflection is slow. We want to cache the fields of a class so we don't have to look them up every time. We use `WeakHashMap` so that if a class is unloaded (e.g., in a dynamic application server), its metadata is garbage collected.

```java
import java.lang.reflect.Field;
import java.util.Map;
import java.util.WeakHashMap;

public class ClassMetadataCache {
    // Weak keys: If the Class object is unloaded, the cache entry is removed
    private final Map<Class<?>, Field[]> cache = new WeakHashMap<>();

    public Field[] getFields(Class<?> clazz) {
        return cache.computeIfFromAbsent(clazz, c -> {
            Field[] fields = c.getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true); // Bypass encapsulation for serialization
            }
            return fields;
        });
    }
}
```

## Step 2: The Serializer (IdentityHashMap)
To serialize an object graph, we must keep track of objects we have already serialized. If Object A points to Object B, and Object B points back to Object A, a naive serializer will infinite loop.
We use `IdentityHashMap` because we want to know if we have seen the *exact same object in memory*, regardless of what its `equals()` method says.

```java
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Map;

public class GraphSerializer {
    private final ClassMetadataCache cache = new ClassMetadataCache();

    public String serialize(Object root) {
        // IdentityHashMap to track visited objects by reference (==)
        // We use Boolean.TRUE as a dummy value, acting like a Set.
        Map<Object, Boolean> visited = new IdentityHashMap<>();
        return serializeRecursive(root, visited);
    }

    private String serializeRecursive(Object obj, Map<Object, Boolean> visited) {
        if (obj == null) return "null";

        // If it's a primitive or String, just return it
        if (obj.getClass().isPrimitive() || obj instanceof String || obj instanceof Number) {
            return "\"" + obj.toString() + "\"";
        }

        // CIRCULAR REFERENCE CHECK
        if (visited.containsKey(obj)) {
            return "\"[CIRCULAR REFERENCE]\"";
        }
        
        // Mark this exact object as visited
        visited.put(obj, Boolean.TRUE);

        StringBuilder sb = new StringBuilder("{");
        Field[] fields = cache.getFields(obj.getClass());

        try {
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                sb.append("\"").append(f.getName()).append("\":");
                
                Object value = f.get(obj);
                sb.append(serializeRecursive(value, visited)); // Recurse
                
                if (i < fields.length - 1) sb.append(",");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        sb.append("}");
        return sb.toString();
    }
}
```

## Step 3: Test with Circular Data
Create objects that point to each other and test the serializer.

```java
public class Main {
    
    static class Person {
        String name;
        Person friend;
        public Person(String name) { this.name = name; }
    }

    public static void main(String[] args) {
        Person alice = new Person("Alice");
        Person bob = new Person("Bob");

        // Create a circular reference!
        alice.friend = bob;
        bob.friend = alice;

        GraphSerializer serializer = new GraphSerializer();
        
        System.out.println("Serializing Alice...");
        String json = serializer.serialize(alice);
        
        System.out.println(json);
    }
}
```

## Expected Output
Because of the `IdentityHashMap`, the serializer safely stops when Bob points back to Alice.
```text
Serializing Alice...
{"name":"Alice","friend":{"name":"Bob","friend":"[CIRCULAR REFERENCE]"}}
```

## Step 4 (Optional): EnumSet Demonstration
Show how `EnumSet` is used for high-performance bit flags.

```java
import java.util.EnumSet;
import java.util.Set;

public class EnumDemo {
    enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }

    public static void main(String[] args) {
        // Creates a bit vector: 0000011 (binary)
        Set<Day> weekend = EnumSet.of(Day.SAT, Day.SUN);
        
        // Very fast bitwise AND check
        System.out.println("Is Monday a weekend? " + weekend.contains(Day.MON));
        System.out.println("Is Sunday a weekend? " + weekend.contains(Day.SUN));
    }
}
```