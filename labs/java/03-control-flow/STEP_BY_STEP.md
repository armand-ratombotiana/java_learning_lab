# Control Flow — Step-by-Step Tutorial

## Step 1: Basic if-else

```java
import java.util.Scanner;

public class Step1IfElse {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a number: ");
        int number = scanner.nextInt();
        
        if (number > 0) {
            System.out.println("Positive");
        } else if (number < 0) {
            System.out.println("Negative");
        } else {
            System.out.println("Zero");
        }
        scanner.close();
    }
}
```

## Step 2: Switch Statement

```java
public class Step2Switch {
    public static void main(String[] args) {
        int dayOfWeek = 3;  // Wednesday
        
        String dayName;
        switch (dayOfWeek) {
            case 1:  dayName = "Monday"; break;
            case 2:  dayName = "Tuesday"; break;
            case 3:  dayName = "Wednesday"; break;
            case 4:  dayName = "Thursday"; break;
            case 5:  dayName = "Friday"; break;
            case 6:  dayName = "Saturday"; break;
            case 7:  dayName = "Sunday"; break;
            default: dayName = "Invalid";
        }
        System.out.println("Day " + dayOfWeek + " is " + dayName);
    }
}
```

## Step 3: Switch Expression (Java 14+)

```java
public class Step3SwitchExpr {
    public static void main(String[] args) {
        int month = 2;
        int year = 2024;
        
        int days = switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 4, 6, 9, 11 -> 30;
            case 2 -> (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) ? 29 : 28;
            default -> throw new IllegalArgumentException("Invalid month: " + month);
        };
        System.out.println("Month " + month + " has " + days + " days");
    }
}
```

## Step 4: For Loop

```java
public class Step4ForLoop {
    public static void main(String[] args) {
        // Count from 1 to 10
        for (int i = 1; i <= 10; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
        
        // Count down
        for (int i = 10; i >= 1; i--) {
            System.out.print(i + " ");
        }
        System.out.println();
        
        // Step by 2
        for (int i = 0; i <= 10; i += 2) {
            System.out.print(i + " ");
        }
        System.out.println();
    }
}
```

## Step 5: While and Do-While

```java
import java.util.Random;

public class Step5Loops {
    public static void main(String[] args) {
        // While — check first
        Random random = new Random();
        int target = 7;
        int guess;
        int attempts = 0;
        
        System.out.println("Guess the number (1-10):");
        do {
            guess = random.nextInt(10) + 1;
            attempts++;
            System.out.println("Attempt " + attempts + ": " + guess);
        } while (guess != target);
        
        System.out.println("Found " + target + " in " + attempts + " attempts");
    }
}
```

## Step 6: Break and Continue

```java
public class Step6BreakContinue {
    public static void main(String[] args) {
        // Continue — skip even numbers
        System.out.println("Odd numbers from 1 to 10:");
        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0) continue;
            System.out.print(i + " ");
        }
        System.out.println();
        
        // Break — stop at first prime > 20
        System.out.println("First prime after 20:");
        for (int i = 21; ; i++) {
            boolean isPrime = true;
            for (int j = 2; j * j <= i; j++) {
                if (i % j == 0) {
                    isPrime = false;
                    break;  // inner break
                }
            }
            if (isPrime) {
                System.out.println(i);
                break;  // outer break
            }
        }
    }
}
```

## Step 7: Nested Loops

```java
public class Step7Nested {
    public static void main(String[] args) {
        // Multiplication table
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                System.out.printf("%3d", i * j);
            }
            System.out.println();
        }
    }
}
```
