# Methods — Step-by-Step Tutorial

## Step 1: Create a Method

```java
public class Step1Method {
    public static void main(String[] args) {
        greet();
        greet();
    }
    
    // Simple method — no params, no return
    public static void greet() {
        System.out.println("Hello, World!");
    }
}
```

## Step 2: Add Parameters

```java
public class Step2Params {
    public static void main(String[] args) {
        greet("Alice");
        greet("Bob");
    }
    
    public static void greet(String name) {
        System.out.println("Hello, " + name + "!");
    }
}
```

## Step 3: Add Return Value

```java
public class Step3Return {
    public static void main(String[] args) {
        int result = add(5, 3);
        System.out.println("5 + 3 = " + result);
        
        int square = multiply(result, result);
        System.out.println(result + " squared = " + square);
    }
    
    public static int add(int a, int b) {
        return a + b;
    }
    
    public static int multiply(int a, int b) {
        return a * b;
    }
}
```

## Step 4: Method Overloading

```java
public class Step4Overload {
    public static void main(String[] args) {
        System.out.println("Sum 2 ints: " + sum(5, 3));
        System.out.println("Sum 3 ints: " + sum(5, 3, 2));
        System.out.println("Sum 2 doubles: " + sum(5.5, 3.3));
    }
    
    public static int sum(int a, int b) {
        return a + b;
    }
    
    public static int sum(int a, int b, int c) {
        return a + b + c;
    }
    
    public static double sum(double a, double b) {
        return a + b;
    }
}
```

## Step 5: Varargs

```java
public class Step5Varargs {
    public static void main(String[] args) {
        System.out.println("Max of 3, 7, 1: " + max(3, 7, 1));
        System.out.println("Max of 10, 20: " + max(10, 20));
        System.out.println("Max of single: " + max(42));
    }
    
    public static int max(int... numbers) {
        if (numbers.length == 0) {
            throw new IllegalArgumentException("At least one number required");
        }
        int max = numbers[0];
        for (int n : numbers) {
            if (n > max) max = n;
        }
        return max;
    }
}
```

## Step 6: Recursion

```java
public class Step6Recursion {
    public static void main(String[] args) {
        System.out.println("Factorial of 5: " + factorial(5));
        System.out.println("Factorial of 0: " + factorial(0));
        System.out.println("Factorial of 10: " + factorial(10));
    }
    
    public static int factorial(int n) {
        // Base case
        if (n <= 1) return 1;
        // Recursive case
        return n * factorial(n - 1);
    }
}
```

## Step 7: Build a Calculator

```java
public class Step7Calculator {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java Step7Calculator <num> <op> <num>");
            System.out.println("Operations: +, -, *, /, %");
            return;
        }
        
        double a = Double.parseDouble(args[0]);
        String op = args[1];
        double b = Double.parseDouble(args[2]);
        
        double result = calculate(a, op, b);
        System.out.println(a + " " + op + " " + b + " = " + result);
    }
    
    public static double calculate(double a, String op, double b) {
        return switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> b != 0 ? a / b : Double.NaN;
            case "%" -> b != 0 ? a % b : Double.NaN;
            default -> throw new IllegalArgumentException("Unknown operator: " + op);
        };
    }
}
```
