# Java Syntax — Interview Questions

## Question 1: Entry Point
**Q:** Why is `main` declared `public static void`?
**A:** `public` so the JVM can access it, `static` so it can be called without creating an instance, `void` because the JVM exits via `System.exit()` — the return value would serve no purpose. The `String[] args` parameter passes command-line arguments.

## Question 2: Primitive vs Reference
**Q:** What is the difference between `int` and `Integer` in terms of syntax?
**A:** `int` is a primitive type — it's a value, not an object. `Integer` is a wrapper class. Syntax differences: `int x = 5;` vs `Integer x = Integer.valueOf(5);`. Autoboxing (Java 5+) blurs this: `Integer x = 5;` is syntactic sugar for `Integer.valueOf(5)`.

## Question 3: Final Keyword
**Q:** What does `final` mean when applied to a variable, method, and class?
**A:** Variable: cannot be reassigned (but object contents can change). Method: cannot be overridden. Class: cannot be subclassed.

## Question 4: Static Keyword
**Q:** What does `static` mean in `public static void main`?
**A:** `static` means the method belongs to the class, not instances. It can be called without creating an object. Static methods cannot access instance variables directly — they have no `this` reference.

## Question 5: String Switch Internals
**Q:** How does a `switch` on Strings work internally?
**A:** The compiler compiles it into: (1) Get the string's hash code, (2) Use a `tableswitch`/`lookupswitch` on the hash, (3) In each case, verify the match with `.equals()`. This handles hash collisions.

## Question 6: Checked vs Unchecked Exceptions
**Q:** What syntax difference is there between checked and unchecked exceptions?
**A:** Checked exceptions (subclasses of `Exception` but not `RuntimeException`) must be either caught with `try-catch` or declared with `throws` in the method signature. Unchecked exceptions (`RuntimeException` and subclasses) do not require either.

## Question 7: Varargs Rules
**Q:** What are the rules for using varargs (`...`)?
**A:** (1) Varargs must be the last parameter. (2) There can be at most one varargs parameter. (3) The method can be called with zero or more arguments for that parameter. (4) Inside the method, the varargs parameter is treated as an array.

## Question 8: Anonymous Classes
**Q:** What is the syntax for creating an anonymous inner class?
**A:** `new SuperType() { body }`. Example:
```java
Runnable r = new Runnable() {
    @Override
    public void run() {
        System.out.println("Running");
    }
};
```

## Question 9: Static Imports
**Q:** What is a static import and when would you use it?
**A:** `import static com.example.Constants.MAX_VALUE;` imports a static member so you can use it without the class name. Use sparingly for constants or utility methods that are used frequently within a class.

## Question 10: Diamond Operator
**Q:** What is the diamond operator `<>` and why was it introduced?
**A:** `<>` allows the compiler to infer generic type arguments from the context. Before Java 7, you had to repeat the type: `List<String> list = new ArrayList<String>();`. With diamond: `List<String> list = new ArrayList<>();`. Reduces verbosity without sacrificing type safety.

## Question 11: Java 8+ Lambda Syntax
**Q:** What are the two forms of lambda expression syntax?
**A:** (1) Expression form: `(params) -> expression` (e.g., `(x, y) -> x + y`). (2) Block form: `(params) -> { statements; }` (e.g., `(x, y) -> { return x + y; }`). If there's a single parameter, parentheses can be omitted: `x -> x * 2`.

## Question 12: Switch Expressions vs Switch Statements
**Q:** What is the syntax difference between a switch statement and a switch expression?
**A:** Switch statements use `:` and `break`: `switch(x) { case 1: doSomething(); break; }`. Switch expressions use `->` and can be assigned: `int result = switch(x) { case 1 -> 10; case 2 -> 20; default -> 0; };`. Switch expressions must be exhaustive and don't fall through.
