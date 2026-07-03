# Exceptions — Quiz

1. What is the root class of all exceptions? **Throwable**
2. What is a checked exception? **Must be caught or declared**
3. What is an unchecked exception? **Runtime exception — not required to handle**
4. Does `Error` extend `Exception`? **No (extends Throwable)**
5. What keyword throws an exception? **throw**
6. What keyword declares exceptions? **throws**
7. What is finally used for? **Cleanup code that always executes**
8. What is try-with-resources? **Auto-closes AutoCloseable resources**
9. What is a suppressed exception? **Exception from close() when another exception is active**
10. When was multi-catch added? **Java 7**
11. Can a finally block execute without try? **No**
12. What is exception propagation? **Exception moves up call stack until caught**
13. What is a custom exception? **User-defined exception class**
14. Can you have multiple catch blocks? **Yes**
15. What is the order of catch blocks? **Most specific to most general**
16. What happens if an exception is not caught? **Thread terminates with stack trace**
17. What is the cause chain? **Chain of exceptions using initCause()**
18. Can a finally block throw an exception? **Yes (overrides original)**
19. What is a stack trace? **List of method calls that led to the exception**
20. When were helpful NullPointerExceptions added? **Java 14 (JEP 358)**
