# Module 14: Reflection & Introspection - Mini Project

**Project Name**: Custom JSON Serializer  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Use the Java Reflection API to inspect object fields at runtime and dynamically serialize any given POJO into a JSON string.

## 📝 Requirements

### Core Features
1. **Serialization Method**:
   - Create a class `JsonSerializer`.
   - Implement `public String serialize(Object obj) throws IllegalAccessException`.

2. **Field Inspection**:
   - Inside the method, use `obj.getClass().getDeclaredFields()` to get all fields.
   - For each field, call `setAccessible(true)` to ensure private fields can be read.
   - Extract the field's name and its value.

3. **String Formatting**:
   - Format the extracted names and values into a valid JSON string structure: `{"key": "value", "key2": 123}`.
   - If a value is a String, wrap it in double quotes. If it's a Number, leave it as is.

4. **Custom Annotation (Bonus)**:
   - Create an annotation `@JsonExclude`.
   - If a field is annotated with `@JsonExclude`, the serializer should ignore it and not include it in the JSON string (useful for passwords).

---

## 💡 Solution Blueprint

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonExclude {}

public class JsonSerializer {
    public static String serialize(Object obj) throws IllegalAccessException {
        Class<?> clazz = obj.getClass();
        StringBuilder json = new StringBuilder("{");
        Field[] fields = clazz.getDeclaredFields();
        
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.isAnnotationPresent(JsonExclude.class)) continue;
            
            field.setAccessible(true);
            json.append("\"").append(field.getName()).append("\": ");
            
            Object value = field.get(obj);
            if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else {
                json.append(value);
            }
            if (i < fields.length - 1) json.append(", ");
        }
        json.append("}");
        return json.toString();
    }
}
```