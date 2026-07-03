# Arrays & Strings — Step-by-Step Tutorial

## Step 1: Create and Use Arrays

```java
import java.util.Arrays;

public class Step1Array {
    public static void main(String[] args) {
        // Declare and initialize
        int[] numbers = new int[5];
        numbers[0] = 10;
        numbers[1] = 20;
        numbers[2] = 30;
        numbers[3] = 40;
        numbers[4] = 50;
        
        // Access and print
        System.out.println("First: " + numbers[0]);
        System.out.println("Last: " + numbers[numbers.length - 1]);
        
        // Shorthand initialization
        int[] primes = {2, 3, 5, 7, 11, 13};
        System.out.println("Primes: " + Arrays.toString(primes));
        
        // Iterate
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        System.out.println("Sum: " + sum);
    }
}
```

## Step 2: Multi-dimensional Arrays

```java
public class Step2MultiDim {
    public static void main(String[] args) {
        // 3x3 matrix
        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        
        // Access element
        System.out.println("Center: " + matrix[1][1]); // 5
        
        // Print matrix
        for (int[] row : matrix) {
            for (int val : row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
        
        // Jagged array
        int[][] jagged = new int[4][];
        jagged[0] = new int[]{1};
        jagged[1] = new int[]{2, 3};
        jagged[2] = new int[]{4, 5, 6};
        jagged[3] = new int[]{7, 8, 9, 10};
    }
}
```

## Step 3: String Operations

```java
public class Step3Strings {
    public static void main(String[] args) {
        String text = "  Hello, Java Programming!  ";
        
        System.out.println("Original: '" + text + "'");
        System.out.println("Length: " + text.length());
        System.out.println("Trimmed: '" + text.trim() + "'");
        System.out.println("Uppercase: " + text.toUpperCase());
        System.out.println("Contains 'Java': " + text.contains("Java"));
        System.out.println("Starts with 'Hello': " + text.trim().startsWith("Hello"));
        
        String[] words = text.trim().split(" ");
        System.out.println("Words: " + String.join(", ", words));
        
        // Substring
        String sub = text.trim().substring(7, 11);
        System.out.println("Substring (7,11): " + sub); // "Java"
    }
}
```

## Step 4: StringBuilder

```java
public class Step4Builder {
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Hello");
        sb.append(" ");
        sb.append("World");
        sb.append("!");
        
        System.out.println("Built: " + sb.toString());
        
        // Insert
        sb.insert(6, "Beautiful ");
        System.out.println("After insert: " + sb.toString());
        
        // Replace
        sb.replace(6, 15, "Amazing");
        System.out.println("After replace: " + sb.toString());
        
        // Delete
        sb.delete(6, 13);
        System.out.println("After delete: " + sb.toString());
        
        // Reverse
        sb.reverse();
        System.out.println("Reversed: " + sb.toString());
    }
}
```

## Step 5: Array Algorithms

```java
import java.util.Arrays;

public class Step5Algorithms {
    public static void main(String[] args) {
        int[] data = {5, 2, 8, 1, 9, 3};
        
        // Sort
        Arrays.sort(data);
        System.out.println("Sorted: " + Arrays.toString(data));
        
        // Binary search (requires sorted array)
        int index = Arrays.binarySearch(data, 5);
        System.out.println("Index of 5: " + index);
        
        // Fill
        Arrays.fill(data, 0);
        System.out.println("Filled: " + Arrays.toString(data));
        
        // Copy
        int[] source = {1, 2, 3, 4, 5};
        int[] dest = Arrays.copyOf(source, source.length);
        int[] partial = Arrays.copyOfRange(source, 1, 4);
        System.out.println("Copy: " + Arrays.toString(dest));
        System.out.println("Partial(1,4): " + Arrays.toString(partial));
    }
}
```

## Step 6: Text Blocks

```java
public class Step6TextBlocks {
    public static void main(String[] args) {
        String html = """
                <html>
                    <body>
                        <h1>Java Academy</h1>
                        <p>Learning Arrays & Strings</p>
                    </body>
                </html>
                """;
        System.out.println(html);
        
        // Formatted text block
        String name = "Alice";
        int score = 95;
        String report = """
                Report for: %s
                Score: %d/100
                Grade: %s
                """.formatted(name, score, score >= 90 ? "A" : "B");
        System.out.println(report);
    }
}
```
