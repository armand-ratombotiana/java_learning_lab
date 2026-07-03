# Exercises: Pattern Matching

## Exercise 1: instanceof to Pattern Matching

Convert the following code to use pattern matching:
```java
public static String describe(Object obj) {
    if (obj == null) return "null";
    if (obj instanceof String) {
        String s = (String) obj;
        return "String of length " + s.length();
    } else if (obj instanceof Integer) {
        Integer i = (Integer) obj;
        return "Integer: " + (i > 0 ? "positive" : i < 0 ? "negative" : "zero");
    } else if (obj instanceof List) {
        List<?> list = (List<?>) obj;
        return "List with " + list.size() + " elements";
    }
    return "Unknown: " + obj.getClass().getName();
}
```

## Exercise 2: Record Pattern Deconstruction

Create records `Name(String first, String last)`, `Person(Name name, int age)`, and `Employee(Person person, String department, double salary)`. Write a method that takes an `Object` and, using nested record patterns, prints the employee's full name if the object is an Employee, or the person's name if it's a Person, or just the object's toString() otherwise.

## Exercise 3: Exhaustive Switch with Sealed Types

Define a sealed interface `HttpResponse` with permitted subtypes `SuccessResponse`, `RedirectResponse`, `ClientError`, `ServerError`. Each subtype carries appropriate data (e.g., `SuccessResponse(int statusCode, String body)`). Write an exhaustive switch that processes each response type differently.

## Exercise 4: Guarded Patterns for Validation

Create a record `Payment(double amount, String currency, String method)`. Write a method `validate(Payment)` that returns a `List<String>` of validation errors using a switch expression with guarded patterns. Rules: amount > 0, currency must be one of USD/EUR/GBP, method must be credit_card, paypal, or bank_transfer.

## Exercise 5: Nested Pattern Matching for AST

Build an expression evaluator for boolean logic using sealed types and records. Include: `Value(boolean v)`, `Not(Expr expr)`, `And(Expr left, Expr right)`, `Or(Expr left, Expr right)`. Implement `evaluate`, `simplify` (double negation elimination, etc.), and `toPrettyString` using pattern matching.

## Exercise 6: Pattern Matching with null Safety

Write a method `safeToString(Object obj)` that uses pattern matching to handle: null, String, Number, arrays, collections, and other objects. Use explicit null handling and return appropriate string representations.

## Exercise 7: HTTP Request Router

Design a sealed hierarchy for HTTP requests and implement a router using pattern matching (as shown in the Code Deep Dive). Add support for PATCH and HEAD methods. Ensure the switch is exhaustive.

## Exercise 8: JSON Query Engine

Using the JSON sealed hierarchy from the lab, implement a `query` method that takes a `Json` value and a JSONPath-like query string (e.g., "$.store.book[0].title") and returns the matched value. Use pattern matching to navigate the nested structure.

## Exercise 9: Pattern Matching in Exceptions

Write a method `handleAll` that catches Exception and uses a switch expression on the caught exception to handle: `IOException` (log and retry), `IllegalArgumentException` (return default), `NullPointerException` (log warning), `RuntimeException` (log error and throw), and other exceptions (wrap and rethrow). Use guarded patterns where appropriate.

## Exercise 10: Complete Refactoring

Take a real piece of code from your project (or an open-source project) that uses instanceof + cast and/or the Visitor pattern. Refactor it to use pattern matching. Document:
1. What the original code looked like (lines of code, number of files)
2. What the pattern matching version looks like
3. The reduction in lines of code
4. Any safety benefits (exhaustiveness, null safety)
5. Any performance observations
