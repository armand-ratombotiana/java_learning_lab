# Arrays & Strings — Code Deep Dive

## Example 1: Array Operations

```java
import java.util.Arrays;

public class ArrayDemo {
    public static void main(String[] args) {
        // Declaration and initialization
        int[] numbers = new int[5];            // All zeros
        int[] literal = {1, 2, 3, 4, 5};      // Literal initializer
        int[] anonymous = new int[]{10, 20};   // Anonymous array
        
        // Fill array
        Arrays.fill(numbers, 42);
        System.out.println("Filled: " + Arrays.toString(numbers)); // [42, 42, 42, 42, 42]
        
        // Copy
        int[] copy = Arrays.copyOf(literal, literal.length);
        int[] partial = Arrays.copyOfRange(literal, 1, 4); // [2, 3, 4]
        
        // Sort and search
        int[] unsorted = {5, 3, 1, 4, 2};
        Arrays.sort(unsorted);
        int index = Arrays.binarySearch(unsorted, 3);
        System.out.println("Sorted: " + Arrays.toString(unsorted)); // [1, 2, 3, 4, 5]
        System.out.println("Index of 3: " + index); // 2
        
        // Compare
        int[] a1 = {1, 2, 3};
        int[] a2 = {1, 2, 3};
        System.out.println("Equals: " + Arrays.equals(a1, a2)); // true
        
        // Multi-dimensional
        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        System.out.println("Element [1][2]: " + matrix[1][2]); // 6
        
        // Jagged array
        int[][] jagged = new int[3][];
        jagged[0] = new int[]{1};
        jagged[1] = new int[]{2, 3};
        jagged[2] = new int[]{4, 5, 6};
        
        // Print 2D array
        System.out.println("Matrix:");
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }
}
```

## Example 2: String Manipulation

```java
public class StringDemo {
    public static void main(String[] args) {
        // String pool demonstration
        String s1 = "hello";
        String s2 = "hello";
        String s3 = new String("hello");
        
        System.out.println("s1 == s2: " + (s1 == s2));       // true (pool)
        System.out.println("s1 == s3: " + (s1 == s3));       // false (new object)
        System.out.println("s1.equals(s3): " + s1.equals(s3)); // true (content)
        
        // Immutability
        String original = "Hello";
        String modified = original.toUpperCase();
        System.out.println("Original: " + original);  // Hello (unchanged)
        System.out.println("Modified: " + modified);  // HELLO
        
        // Common operations
        String text = "  Java Programming  ";
        System.out.println("Length: " + text.length());
        System.out.println("Trimmed: '" + text.trim() + "'");
        System.out.println("Char at 0: " + text.charAt(0));
        System.out.println("Substring: " + text.substring(2, 6));
        System.out.println("Contains 'Prog': " + text.contains("Prog"));
        System.out.println("Starts with '  Ja': " + text.startsWith("  Ja"));
        System.out.println("Ends with '  ': " + text.endsWith("  "));
        System.out.println("Replace: " + text.replace("Java", "Python"));
        
        // Split and join
        String csv = "apple,banana,cherry";
        String[] fruits = csv.split(",");
        System.out.println("Joined: " + String.join(" | ", fruits));
        
        // Format
        String formatted = String.format("Name: %s, Age: %d, Score: %.1f",
            "Alice", 30, 95.5);
        System.out.println(formatted);
    }
}
```

## Example 3: StringBuilder vs String

```java
public class BuilderDemo {
    public static void main(String[] args) {
        // Inefficient string concatenation
        long start = System.nanoTime();
        String result = "";
        for (int i = 0; i < 10000; i++) {
            result += i;  // O(n²) — creates new String each time
        }
        long end = System.nanoTime();
        System.out.println("String '+': " + (end - start) / 1_000_000 + "ms");
        
        // Efficient StringBuilder
        start = System.nanoTime();
        StringBuilder sb = new StringBuilder(50000);
        for (int i = 0; i < 10000; i++) {
            sb.append(i);
        }
        result = sb.toString();
        end = System.nanoTime();
        System.out.println("StringBuilder: " + (end - start) / 1_000_000 + "ms");
        
        // StringBuilder operations
        StringBuilder builder = new StringBuilder("Hello");
        builder.append(" World");
        builder.insert(6, "Beautiful ");
        builder.replace(6, 15, "Amazing");
        builder.delete(6, 13);
        builder.reverse();
        System.out.println("Builder result: " + builder.toString());
        
        // StringBuffer (thread-safe)
        StringBuffer buffer = new StringBuffer("Thread");
        buffer.append(" safe");
        System.out.println("Buffer: " + buffer.toString());
    }
}
```

## Example 4: Text Blocks (Java 15+)

```java
public class TextBlockDemo {
    public static void main(String[] args) {
        // Text block for multi-line strings
        String json = """
                {
                    "name": "Alice",
                    "age": 30,
                    "hobbies": ["reading", "hiking"]
                }
                """;
        System.out.println(json);
        
        // SQL query
        String query = """
                SELECT u.name, u.email, COUNT(o.id) as order_count
                FROM users u
                LEFT JOIN orders o ON u.id = o.user_id
                WHERE u.active = true
                GROUP BY u.id
                HAVING COUNT(o.id) > 5
                ORDER BY order_count DESC
                """;
        System.out.println(query);
        
        // HTML template
        String html = """
                <html>
                    <body>
                        <h1>Welcome, %s!</h1>
                        <p>You have %d new messages.</p>
                    </body>
                </html>
                """.formatted("Alice", 3);
        System.out.println(html);
    }
}
```
