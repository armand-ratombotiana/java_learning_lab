# Refactoring Java Syntax

## Improving Code Through Better Syntax Usage

### 1. Replace Raw Type with Diamond Operator

**Before (Java 6 style):**
```java
List<String> list = new ArrayList<String>();
Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
```

**After (Java 7+):**
```java
List<String> list = new ArrayList<>();
Map<String, List<Integer>> map = new HashMap<>();
```

### 2. Replace Traditional For-Loop with Enhanced For-Loop

**Before:**
```java
int[] numbers = {1, 2, 3, 4, 5};
for (int i = 0; i < numbers.length; i++) {
    System.out.println(numbers[i]);
}
```

**After:**
```java
int[] numbers = {1, 2, 3, 4, 5};
for (int number : numbers) {
    System.out.println(number);
}
```

### 3. Use Switch Expressions Instead of Switch Statements

**Before (Java 13 and earlier):**
```java
String result;
switch (day) {
    case MONDAY:
    case FRIDAY:
        result = "Work day";
        break;
    case SATURDAY:
    case SUNDAY:
        result = "Weekend";
        break;
    default:
        result = "Midweek";
}
```

**After (Java 14+):**
```java
String result = switch (day) {
    case MONDAY, FRIDAY -> "Work day";
    case SATURDAY, SUNDAY -> "Weekend";
    default -> "Midweek";
};
```

### 4. Use Local Variable Type Inference

**Before:**
```java
List<String> items = new ArrayList<>();
Map<String, List<Integer>> complex = new HashMap<>();
```

**After (when type is obvious):**
```java
var items = new ArrayList<String>();
var complex = new HashMap<String, List<Integer>>();
```

**Note:** Use `var` only when the type is clear from context. Avoid when it reduces readability.

### 5. Replace Anonymous Inner Classes with Lambdas

**Before:**
```java
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Clicked!");
    }
});
```

**After:**
```java
button.addActionListener(e -> System.out.println("Clicked!"));
```

### 6. Use Text Blocks for Multi-Line Strings

**Before:**
```java
String json = "{\n" +
              "  \"name\": \"Alice\",\n" +
              "  \"age\": 30\n" +
              "}";
```

**After:**
```java
String json = """
        {
          "name": "Alice",
          "age": 30
        }
        """;
```

### 7. Simplify with Multi-Catch

**Before:**
```java
try {
    method();
} catch (IOException e) {
    logger.error(e);
} catch (SQLException e) {
    logger.error(e);
}
```

**After:**
```java
try {
    method();
} catch (IOException | SQLException e) {
    logger.error(e);
}
```

### 8. Use Try-With-Resources

**Before:**
```java
BufferedReader reader = null;
try {
    reader = new BufferedReader(new FileReader("file.txt"));
    String line = reader.readLine();
} finally {
    if (reader != null) reader.close();
}
```

**After:**
```java
try (BufferedReader reader = new BufferedReader(new FileReader("file.txt"))) {
    String line = reader.readLine();
}
```

### 9. Use Underscores for Readable Numeric Literals

**Before:**
```java
long phoneNumber = 1234567890L;
int maxInt = 2147483647;
```

**After:**
```java
long phoneNumber = 123_456_7890L;
int maxInt = 2_147_483_647;
```

### 10. Static Import for Frequently Used Constants

**Before:**
```java
double circumference = 2 * Math.PI * radius;
int maxVal = Math.max(a, b);
```

**After:**
```java
import static java.lang.Math.PI;
import static java.lang.Math.max;

double circumference = 2 * PI * radius;
int maxVal = max(a, b);
```

**Note:** Use sparingly — overuse can hurt readability.
