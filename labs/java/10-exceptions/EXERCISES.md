# Exceptions — Exercises

## Exercise 1: Basic Try-Catch
Write a program that asks for two numbers and divides them. Handle ArithmeticException.

## Exercise 2: Multiple Catch
Read a file, parse numbers, sum them. Handle FileNotFoundException, IOException, NumberFormatException.

## Exercise 3: Custom Exception
Create `InvalidAgeException` (checked). Throw it for age < 0 or age > 150.

## Exercise 4: Try-With-Resources
Read a file using `BufferedReader` with try-with-resources. Count lines and words.

## Exercise 5: Exception Propagation
Create methodA → methodB → methodC. Let methodC throw, methodA catch. Show propagation.

## Exercise 6: Multi-Catch
Merge two catch blocks for `IOException` and `SQLException` into a multi-catch.

## Exercise 7: Finally vs Return
Write a method with try-finally that tries to return a value. Show that finally runs before return.

## Exercise 8: Custom Unchecked Exception
Create `InvalidTransactionException extends RuntimeException`. Use it in a banking app.

## Exercise 9: Log and Rethrow
Write a method that catches an exception, logs it, and rethrows it. Preserve the original exception.

## Exercise 10: Suppressed Exceptions
Create a try-with-resources where both body and close() throw. Show suppressed exceptions.

## Exercise 11: Exception Chaining
Throw a custom exception that wraps the original `SQLException` as its cause.

## Exercise 12: Assertions
Use `assert` for internal invariants. Enable with `-ea`. Show assertion error behavior.
