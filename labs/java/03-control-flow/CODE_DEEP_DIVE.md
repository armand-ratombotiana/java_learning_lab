# Control Flow — Code Deep Dive

## Example 1: All if-else Forms

```java
public class IfElseDemo {
    public static void main(String[] args) {
        int score = 85;
        
        // Simple if
        if (score >= 60) {
            System.out.println("Passed");
        }
        
        // if-else
        if (score >= 90) {
            System.out.println("Grade: A");
        } else {
            System.out.println("Grade: B or lower");
        }
        
        // if-else-if ladder
        if (score >= 90) {
            System.out.println("A");
        } else if (score >= 80) {
            System.out.println("B");
        } else if (score >= 70) {
            System.out.println("C");
        } else if (score >= 60) {
            System.out.println("D");
        } else {
            System.out.println("F");
        }
        
        // Nested if
        if (score >= 60) {
            if (score >= 90) {
                System.out.println("Excellent pass");
            } else {
                System.out.println("Pass");
            }
        }
        
        // Ternary as compact if-else
        String result = (score >= 60) ? "Pass" : "Fail";
        System.out.println("Result: " + result);
    }
}
```

## Example 2: Switch Deep Dive

```java
public class SwitchDemo {
    public static void main(String[] args) {
        // Traditional switch (falls through)
        int day = 3;
        String dayName;
        switch (day) {
            case 1:
                dayName = "Monday";
                break;
            case 2:
                dayName = "Tuesday";
                break;
            case 3:
                dayName = "Wednesday";
                break;
            case 4:
                dayName = "Thursday";
                break;
            case 5:
                dayName = "Friday";
                break;
            default:
                dayName = "Weekend";
        }
        System.out.println("Day " + day + " is " + dayName);
        
        // Switch expression (Java 14+)
        String type = switch (day) {
            case 1, 2, 3, 4, 5 -> "Weekday";
            case 6, 7 -> "Weekend";
            default -> throw new IllegalArgumentException("Invalid day: " + day);
        };
        System.out.println("Type: " + type);
        
        // Switch with yield (for complex expressions)
        dayName = switch (day) {
            case 1: yield "Monday";
            case 2: yield "Tuesday";
            case 3: yield "Wednesday";
            case 4: yield "Thursday";
            case 5: yield "Friday";
            default: yield "Weekend";
        };
    }
}
```

## Example 3: Loop Patterns

```java
public class LoopDemo {
    public static void main(String[] args) {
        // Standard for loop
        for (int i = 0; i < 5; i++) {
            System.out.print(i + " ");  // 0 1 2 3 4
        }
        System.out.println();
        
        // While loop
        int count = 0;
        while (count < 5) {
            System.out.print(count + " ");
            count++;
        }
        System.out.println();
        
        // Do-while (always executes at least once)
        int x = 10;
        do {
            System.out.print(x + " ");
            x++;
        } while (x < 5);  // Only prints 10
        System.out.println();
        
        // Nested loops
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= i; j++) {
                System.out.print("*");
            }
            System.out.println();
        }
        // Output:
        // *
        // **
        // ***
        
        // Break and continue
        for (int i = 0; i < 10; i++) {
            if (i % 3 == 0) continue;  // Skip multiples of 3
            if (i > 7) break;           // Stop at 8
            System.out.print(i + " ");  // 1 2 4 5 7
        }
        System.out.println();
        
        // Labeled break
        outer:
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == 1 && j == 1) break outer;
                System.out.println(i + "," + j);
            }
        }
        // Prints: (0,0) (0,1) (0,2) (1,0) — then exits both loops
    }
}
```

## Example 4: Enhanced For-Loop

```java
import java.util.*;

public class ForEachDemo {
    public static void main(String[] args) {
        // Array iteration
        int[] numbers = {10, 20, 30, 40, 50};
        for (int n : numbers) {
            System.out.print(n + " ");  // 10 20 30 40 50
        }
        System.out.println();
        
        // List iteration
        List<String> names = Arrays.asList("Alice", "Bob", "Carol");
        for (String name : names) {
            System.out.println("Hello, " + name);
        }
        
        // Map iteration (via entrySet)
        Map<String, Integer> ages = new HashMap<>();
        ages.put("Alice", 30);
        ages.put("Bob", 25);
        for (Map.Entry<String, Integer> entry : ages.entrySet()) {
            System.out.println(entry.getKey() + " is " + entry.getValue());
        }
        
        // Cannot modify while iterating:
        // for (String name : names) {
        //     names.remove(name);  // ConcurrentModificationException!
        // }
    }
}
```
