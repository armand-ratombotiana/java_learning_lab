# Mini Project: Data Processor with Type Inference

## Objective
Build a data processing utility that demonstrates the clean, readable code enabled by Local Variable Type Inference (`var`). You will also demonstrate the specific edge cases where `var` fails (lambdas, diamond operators) and how to resolve them.

## Prerequisites
*   Java 11+ (Java 17+ recommended)

## Step 1: The Domain Model
Create a simple record representing a data point.

```java
public record DataPoint(String id, double value, String category) {}
```

## Step 2: The Data Processor (Using `var` effectively)
Implement a method that groups data and calculates averages. Notice how `var` removes the massive, unreadable generic type signatures from the local variables.

```java
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataProcessor {

    public void processAndPrint(List<DataPoint> data) {
        System.out.println("--- Processing Data ---");

        // Good use of var: The type is obvious from the method name.
        // Without var, this would be: 
        // Map<String, List<DataPoint>> groupedData = data.stream()...
        var groupedData = data.stream()
                .collect(Collectors.groupingBy(DataPoint::category));

        // Good use of var in a loop
        for (var entry : groupedData.entrySet()) {
            var category = entry.getKey();
            var points = entry.getValue();

            // Calculate average
            var sum = 0.0;
            for (var point : points) {
                sum += point.value();
            }
            var average = points.isEmpty() ? 0 : sum / points.size();

            System.out.printf("Category: %-10s | Count: %d | Avg: %.2f%n", category, points.size(), average);
        }
    }
}
```

## Step 3: Demonstrating the Edge Cases
Create a method that explicitly shows where `var` goes wrong and how to fix it.

```java
import java.util.ArrayList;
import java.util.function.Function;

public class EdgeCaseDemo {

    public void demonstrateTraps() {
        System.out.println("\n--- Edge Case Demonstrations ---");

        // 1. The Diamond Operator Trap
        // BAD: var list1 = new ArrayList<>(); // Infers ArrayList<Object>
        var list1 = new ArrayList<String>(); // FIX: Explicit type on the right
        list1.add("Hello");
        System.out.println("List 1 type is safe: " + list1.get(0).toUpperCase());

        // 2. The Poly Expression Trap (Lambdas)
        // BAD: var formatter = (String s) -> "[" + s + "]"; // Compile Error!
        // FIX: Cast to explicitly provide the Target Type
        var formatter = (Function<String, String>) (s -> "[" + s + "]");
        System.out.println("Formatter result: " + formatter.apply("Data"));

        // 3. The Anonymous Class Trap (Non-denotable types)
        var wrapper = new Object() {
            String name = "Secret Identity";
            void print() { System.out.println("My name is: " + name); }
        };
        // This works locally!
        wrapper.print(); 
        
        // But if you try to return 'wrapper' from this method, it will fail, 
        // because the return type of the method would have to be 'Object', 
        // and the caller wouldn't be able to call .print() on it.
    }
}
```

## Step 4: Execute
```java
import java.util.List;

public class Main {
    public static void main(String[] args) {
        var data = List.of(
            new DataPoint("1", 100.5, "FINANCE"),
            new DataPoint("2", 200.0, "FINANCE"),
            new DataPoint("3", 50.0, "HR"),
            new DataPoint("4", 75.5, "HR"),
            new DataPoint("5", 300.0, "IT")
        );

        var processor = new DataProcessor();
        processor.processAndPrint(data);

        var edgeCases = new EdgeCaseDemo();
        edgeCases.demonstrateTraps();
    }
}
```

## Expected Output
```text
--- Processing Data ---
Category: FINANCE    | Count: 2 | Avg: 150.25
Category: HR         | Count: 2 | Avg: 62.75
Category: IT         | Count: 1 | Avg: 300.00

--- Edge Case Demonstrations ---
List 1 type is safe: HELLO
Formatter result: [Data]
My name is: Secret Identity
```