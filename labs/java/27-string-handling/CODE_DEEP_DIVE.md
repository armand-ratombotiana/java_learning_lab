# Code Deep Dive: String Handling

## JSON Builder with StringBuilder
```java
public class JsonBuilder {
    private final StringBuilder sb = new StringBuilder();
    private boolean first = true;
    
    public JsonBuilder startObject() { sb.append('{'); first = true; return this; }
    public JsonBuilder endObject() { sb.append('}'); return this; }
    
    public JsonBuilder property(String key, String value) {
        appendComma();
        sb.append('"').append(escape(key)).append("\":\"")
          .append(escape(value)).append('"');
        return this;
    }
    
    private void appendComma() {
        if (!first) sb.append(',');
        first = false;
    }
    
    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    
    public String build() { return sb.toString(); }
}
```

## Text Block for SQL
```java
public List<User> findActiveUsers(String department) {
    String sql = """
        SELECT id, name, email, department
        FROM users
        WHERE active = TRUE
          AND department = ?
        ORDER BY name ASC
        """;
    return jdbcTemplate.query(sql, userRowMapper, department);
}
```

## String Formatting Examples
```java
// printf-style formatting
String formatted = String.format("Hello %s, you are %d years old", name, age);

// MessageFormat for i18n
String pattern = "At {1,time} on {1,date}, we detected {0} errors.";
String result = MessageFormat.format(pattern, count, timestamp);

// Formatter class
StringBuilder sb = new StringBuilder();
Formatter fmt = new Formatter(sb);
fmt.format("%-10s %5d %8.2f%n", name, quantity, price);
```

## Pattern Matching with Regex
```java
public class EmailParser {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("(?<local>[^@]+)@(?<domain>[^@]+\\.[^@]+)");
    
    public record Email(String local, String domain) {}
    
    public Optional<Email> parse(String email) {
        Matcher m = EMAIL_PATTERN.matcher(email);
        if (m.matches()) {
            return Optional.of(new Email(m.group("local"), m.group("domain")));
        }
        return Optional.empty();
    }
}
```

## String Performance Comparison
```java
// BAD: O(n²) performance in loops
String result = "";
for (int i = 0; i < 10000; i++) {
    result += item;  // Creates new String each iteration
}

// GOOD: O(n) performance
StringBuilder sb = new StringBuilder(100000);
for (int i = 0; i < 10000; i++) {
    sb.append(item);
}
String result = sb.toString();
```
