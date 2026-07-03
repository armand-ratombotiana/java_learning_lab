# Java Syntax — Step-by-Step Tutorial

## Step 1: Set Up Your Environment

1. Verify JDK installation:
   ```bash
   java -version
   javac -version
   ```

2. Create a project directory:
   ```bash
   mkdir JavaSyntaxLab
   cd JavaSyntaxLab
   ```

## Step 2: Write Your First Java File

Create `HelloSyntax.java`:

```java
public class HelloSyntax {
    public static void main(String[] args) {
        System.out.println("Hello, Java Syntax!");
    }
}
```

**Key observations:**
- The filename matches the class name: `HelloSyntax.java`
- The `main` method has the exact signature `public static void main(String[])`
- Every statement ends with a semicolon `;`
- The method body is enclosed in braces `{}`

## Step 3: Compile and Run

```bash
javac HelloSyntax.java
java HelloSyntax
```

Expected output: `Hello, Java Syntax!`

## Step 4: Add Variables

```java
public class Variables {
    public static void main(String[] args) {
        // Variable declaration: type name = value;
        int age = 25;
        double price = 19.99;
        String name = "Alice";
        boolean isStudent = true;

        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Price: $" + price);
        System.out.println("Student: " + isStudent);
    }
}
```

## Step 5: Add Operators

```java
public class Operators {
    public static void main(String[] args) {
        int a = 10;
        int b = 3;

        System.out.println("a + b = " + (a + b));  // 13
        System.out.println("a - b = " + (a - b));  // 7
        System.out.println("a * b = " + (a * b));  // 30
        System.out.println("a / b = " + (a / b));  // 3 (integer division)
        System.out.println("a % b = " + (a % b));  // 1 (remainder)

        // Compound assignments
        int x = 5;
        x += 3;  // x = 8
        x *= 2;  // x = 16
        System.out.println("x = " + x);
    }
}
```

## Step 6: Add Control Flow

```java
public class ControlFlow {
    public static void main(String[] args) {
        int score = 85;

        // if-else
        if (score >= 90) {
            System.out.println("Grade: A");
        } else if (score >= 80) {
            System.out.println("Grade: B");
        } else {
            System.out.println("Grade: C or lower");
        }

        // for loop
        for (int i = 0; i < 5; i++) {
            System.out.println("Count: " + i);
        }

        // while loop
        int count = 0;
        while (count < 3) {
            System.out.println("While: " + count);
            count++;
        }
    }
}
```

## Step 7: Add Methods

```java
public class Methods {
    public static void main(String[] args) {
        int result = add(5, 3);
        System.out.println("5 + 3 = " + result);
        greet("Alice");
    }

    // Method with parameters and return value
    public static int add(int a, int b) {
        return a + b;
    }

    // Method with parameter, no return value
    public static void greet(String name) {
        System.out.println("Hello, " + name + "!");
    }
}
```

## Step 8: Use All Three Comment Types

```java
/**
 * This class demonstrates all Java comment types.
 * Created as part of the Java Syntax lab.
 */
public class Comments {
    public static void main(String[] args) {
        // This is a single-line comment
        System.out.println("Line 1");  // Inline comment

        /*
         * This is a multi-line comment.
         * It can span several lines.
         */
        System.out.println("Line 2");
    }
}
```

## Step 9: Understand Naming Conventions

```java
public class NamingDemo {                    // Class: PascalCase
    private static final int MAX_VALUE = 100; // Constant: UPPER_SNAKE_CASE
    private String firstName;                 // Field: camelCase

    public NamingDemo(String firstName) {     // Parameter: camelCase
        this.firstName = firstName;
    }

    public String getFirstName() {            // Method: camelCase
        return firstName;
    }

    public static void main(String[] args) {  // args is conventional
        NamingDemo demo = new NamingDemo("Bob");
        System.out.println(demo.getFirstName());
    }
}
```

## Step 10: Practice — Build a Calculator

Combine everything: variables, operators, control flow, methods.

```java
public class MiniCalc {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java MiniCalc <num1> <op> <num2>");
            return;
        }
        double a = Double.parseDouble(args[0]);
        String op = args[1];
        double b = Double.parseDouble(args[2]);
        double result = switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> b != 0 ? a / b : Double.NaN;
            default -> Double.NaN;
        };
        System.out.println(a + " " + op + " " + b + " = " + result);
    }
}
```

Compile and run:
```bash
javac MiniCalc.java
java MiniCalc 10 + 5
java MiniCalc 100 / 0
```
