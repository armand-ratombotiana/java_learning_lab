# Data Types — Step-by-Step Tutorial

## Step 1: Declare Variables of Each Primitive Type

```java
public class Step1Primitives {
    public static void main(String[] args) {
        byte myByte = 100;
        short myShort = 10000;
        int myInt = 1000000;
        long myLong = 10000000000L;    // Note: L suffix
        float myFloat = 3.14f;         // Note: f suffix
        double myDouble = 3.14159265;
        char myChar = 'A';
        boolean myBoolean = true;
        
        System.out.println("byte: " + myByte);
        System.out.println("short: " + myShort);
        System.out.println("int: " + myInt);
        System.out.println("long: " + myLong);
        System.out.println("float: " + myFloat);
        System.out.println("double: " + myDouble);
        System.out.println("char: " + myChar);
        System.out.println("boolean: " + myBoolean);
    }
}
```

## Step 2: Create and Run

```bash
javac Step1Primitives.java
java Step1Primitives
```

## Step 3: Practice Type Conversion

```java
public class Step3Conversion {
    public static void main(String[] args) {
        int intVal = 42;
        long longVal = intVal;           // implicit widening: int → long
        System.out.println("int to long: " + longVal);
        
        double doubleVal = intVal;       // implicit: int → double
        System.out.println("int to double: " + doubleVal);
        
        double pi = 3.14159;
        int truncated = (int) pi;        // explicit narrowing (truncates)
        System.out.println("double to int: " + truncated);
        
        long big = 1_000_000_000_000L;
        int lost = (int) big;            // Data loss!
        System.out.println("long to int (overflow): " + lost);
    }
}
```

## Step 4: Autoboxing Practice

```java
import java.util.ArrayList;
import java.util.List;

public class Step4Boxing {
    public static void main(String[] args) {
        // Autoboxing: int → Integer
        List<Integer> numbers = new ArrayList<>();
        numbers.add(10);                 // autoboxing
        numbers.add(20);
        numbers.add(30);
        
        // Unboxing: Integer → int
        int sum = 0;
        for (Integer n : numbers) {
            sum += n;                    // unboxing
        }
        System.out.println("Sum: " + sum);
    }
}
```

## Step 5: Experiment with Wrappers

```java
public class Step5Wrappers {
    public static void main(String[] args) {
        System.out.println("Integer MAX_VALUE: " + Integer.MAX_VALUE);
        System.out.println("Integer MIN_VALUE: " + Integer.MIN_VALUE);
        System.out.println("Double MAX_VALUE: " + Double.MAX_VALUE);
        
        // Parsing
        int parsed = Integer.parseInt("123");
        double d = Double.parseDouble("3.14");
        System.out.println("Parsed int: " + parsed);
        System.out.println("Parsed double: " + d);
        
        // Comparison
        Integer a = Integer.valueOf(100);  // cached
        Integer b = Integer.valueOf(100);
        System.out.println("a == b: " + (a == b));     // true (cached)
        
        Integer c = Integer.valueOf(200);  // not cached
        Integer d2 = Integer.valueOf(200);
        System.out.println("c == d2: " + (c == d2));   // false
        System.out.println("c.equals(d2): " + c.equals(d2)); // true
    }
}
```
