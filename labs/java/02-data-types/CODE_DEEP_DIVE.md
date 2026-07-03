# Data Types — Code Deep Dive

## Example 1: All Primitive Types

```java
public class PrimitiveDemo {
    public static void main(String[] args) {
        byte b = 127;                     // 8 bits, max 127
        short s = 32767;                  // 16 bits, max 32767
        int i = 2_147_483_647;            // 32 bits, max value
        long l = 9_223_372_036_854_775_807L; // 64 bits, note L suffix
        
        float f = 3.1415926f;             // 32 bits, note f suffix, ~7 digits precision
        double d = 3.141592653589793;     // 64 bits, ~15 digits precision
        
        char c = 'A';                     // 16 bits, unsigned
        char unicode = '\u00E9';          // é via Unicode escape
        char asInt = 65;                  // 'A' via integer literal
        
        boolean flag = true;              // JVM-dependent size
        
        System.out.println("byte: " + b);
        System.out.println("short: " + s);
        System.out.println("int: " + i);
        System.out.println("long: " + l);
        System.out.println("float: " + f);
        System.out.println("double: " + d);
        System.out.println("char: " + c + " / " + unicode + " / " + asInt);
        System.out.println("boolean: " + flag);
    }
}
```

## Example 2: Type Conversion

```java
public class ConversionDemo {
    public static void main(String[] args) {
        // Widening (implicit) — always safe
        int i = 100;
        long l = i;        // int → long: OK
        float f = i;       // int → float: OK (possible precision loss for large ints)
        double d = i;      // int → double: OK
        
        // Narrowing (explicit cast) — may lose data
        double pi = 3.14159;
        int truncated = (int) pi;        // 3 — fractional part lost
        System.out.println("Truncated: " + truncated); // 3
        
        // Overflow example
        int big = 2_000_000_000;
        int overflow = big * 2;           // -294967296 — overflow!
        long correct = (long) big * 2;    // 4000000000 — cast before multiplication
        
        // char to int
        char letter = 'A';
        int code = letter;               // 65 — implicit widening
        
        // int to char (narrowing)
        char backToChar = (char) 65;     // 'A'
        
        // String to primitive
        int parsed = Integer.parseInt("123");
        double parsedD = Double.parseDouble("3.14");
        boolean parsedB = Boolean.parseBoolean("true");
    }
}
```

## Example 3: Autoboxing Pitfalls

```java
public class BoxingDemo {
    public static void main(String[] args) {
        // Integer cache: -128 to 127
        Integer a = 100;
        Integer b = 100;
        System.out.println(a == b);     // true (cached same object)
        
        Integer c = 200;
        Integer d = 200;
        System.out.println(c == d);     // false (different objects!)
        System.out.println(c.equals(d)); // true (correct comparison)
        
        // Null pointer on unboxing
        Integer nullInt = null;
        // int val = nullInt;           // NullPointerException!
        
        // Performance: autoboxing in loops
        long start = System.nanoTime();
        Integer sum = 0;
        for (int i = 0; i < 10_000_000; i++) {
            sum += i;  // Autoboxing each iteration!
        }
        long end = System.nanoTime();
        System.out.println("With Integer: " + (end - start) / 1_000_000 + "ms");
        
        // Better: use int primitive
        start = System.nanoTime();
        int sumPrim = 0;
        for (int i = 0; i < 10_000_000; i++) {
            sumPrim += i;
        }
        end = System.nanoTime();
        System.out.println("With int: " + (end - start) / 1_000_000 + "ms");
    }
}
```

## Example 4: BigDecimal for Precision

```java
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalDemo {
    public static void main(String[] args) {
        // Don't use double for money:
        double a = 0.1;
        double b = 0.2;
        System.out.println("double: 0.1 + 0.2 = " + (a + b)); // 0.30000000000000004
        
        // Use BigDecimal:
        BigDecimal bd1 = new BigDecimal("0.1");   // Use String constructor!
        BigDecimal bd2 = new BigDecimal("0.2");
        BigDecimal sum = bd1.add(bd2);
        System.out.println("BigDecimal: 0.1 + 0.2 = " + sum); // 0.3
        
        // Rounding
        BigDecimal precise = new BigDecimal("10.56789");
        BigDecimal rounded = precise.setScale(2, RoundingMode.HALF_UP);
        System.out.println("Rounded: " + rounded); // 10.57
    }
}
```
