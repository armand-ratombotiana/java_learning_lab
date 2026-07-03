# Common Mistakes in Java Syntax

## 1. Missing Semicolons

```java
int x = 5
// Error: ';' expected
```

**Fix:** Every statement must end with `;`.

## 2. Using `=` Instead of `==` in Conditions

```java
if (x = 5) { }  // Compilation error: int cannot be converted to boolean
```

Java prevents this common C/C++ mistake by requiring boolean expressions in `if` conditions.

## 3. Case-Sensitivity Confusion

```java
String name = "Alice";
System.out.printlN(Name);  // Error: cannot find symbol 'Name' and 'printlN'
```

**Fix:** Java is case-sensitive. `name` and `Name` are different identifiers.

## 4. Mismatched Braces

```java
public class Test {
    public static void main(String[] args) {
        if (true) {
            System.out.println("Hello");
    }   // Error: expected '}' to close if block
}
// But only one '}' for class
```

**Fix:** Use consistent indentation and IDE brace matching.

## 5. Wrong Bracket Types

```java
List<String> list = new ArrayList<>();  // Correct: angle brackets
int[] array = new int[5];               // Correct: square brackets
// Method call: parentheses
list.add("hello");

// Common confusion:
// () — parentheses (method calls, grouping)
// {} — braces (blocks, arrays, class bodies)
// [] — brackets (array types, array indexing)
// <> — angle brackets (generics)
```

## 6. Using Reserved Keywords as Identifiers

```java
int class = 5;     // Error: 'class' is a keyword
int null = 0;      // Error: 'null' is a keyword
String true = "yes"; // Error: 'true' is a keyword
```

**Fix:** Never use Java keywords as variable names.

## 7. Forgetting to Import

```java
public class Test {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        // Error: Cannot find symbol 'List' and 'ArrayList'
    }
}
```

**Fix:** Add `import java.util.List;` and `import java.util.ArrayList;`.

## 8. Method Called Before Declaration (Order Doesn't Matter)

```java
public class Test {
    public static void main(String[] args) {
        greet();  // This works even though greet is defined after
    }

    public static void greet() {
        System.out.println("Hi!");
    }
}
```

This is NOT an error, but many beginners think forward references are invalid. Method order in a class does not matter.

## 9. Confusing `println` vs `print` vs `printf`

```java
System.out.println("Line 1");  // Prints and moves to next line
System.out.print("Line 2");    // Prints, stays on same line
System.out.printf("%d items", 5); // Formatted output
```

## 10. Uninitialized Local Variables

```java
public class Test {
    public static void main(String[] args) {
        int x;
        // System.out.println(x);  // Error: variable x might not have been initialized
        x = 5;  // Must initialize before use
        System.out.println(x);  // OK
    }
}
```

Fields get default values (0, null, false). Local variables do NOT.

## 11. Integer Division Instead of Floating-Point

```java
double result = 5 / 2;   // 2.0, not 2.5!
double correct = 5.0 / 2; // 2.5
```

**Fix:** Use at least one floating-point operand.

## 12. Confusing `String` with Primitive

```java
String s = "hello";
if (s == "hello") { }  // Compares references, not values!
if (s.equals("hello")) { }  // Correct: compares content
```

## 13. Array Declaration Syntax Confusion

```java
int[] arr1 = new int[5];  // Preferred syntax
int arr2[] = new int[5];  // C-style, works but discouraged
int[] arr3 = {1, 2, 3};   // Anonymous array initializer (works)
// int[5] arr4;            // Error: size not allowed here
```

## 14. Breaking the `switch` Without `break` (Pre-Java 14)

```java
int x = 2;
switch (x) {
    case 1: System.out.print("One");
    case 2: System.out.print("Two");  // Falls through!
    case 3: System.out.print("Three");
}
// Prints: "TwoThree"
```

**Fix:** Use `break` or use switch expressions (Java 14+): `case 2 -> "Two";`

## 15. Misplaced `else`

```java
if (x > 0)
    System.out.println("Positive");
    System.out.println("Still positive"); // Always runs — not part of if!
else  // Error: 'else' without 'if'
    System.out.println("Negative");
```

**Fix:** Always use braces `{}` for if/else bodies.
