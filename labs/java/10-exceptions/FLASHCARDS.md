# Exceptions — Flashcards

1. **Q: Root exception class?** A: Throwable
2. **Q: Checked exception requires?** A: Catch or declare (throws)
3. **Q: Unchecked exception?** A: RuntimeException — no handling required
4. **Q: Error vs Exception?** A: Error = JVM problem (unrecoverable)
5. **Q: throw keyword?** A: Throws an exception
6. **Q: throws keyword?** A: Declares exception in method signature
7. **Q: finally block?** A: Always executes (unless System.exit)
8. **Q: Try-with-resources?** A: Auto-closes AutoCloseable resources
9. **Q: Suppressed exception?** A: Secondary exception when close fails
10. **Q: Multi-catch syntax?** A: catch (IOE | SQLE e)
11. **Q: Exception propagation?** A: Moves up call stack
12. **Q: Stack trace?** A: Method call list at exception point
13. **Q: Custom exception extends?** A: Exception (checked) or RuntimeException
14. **Q: Catch order?** A: Most specific to most general
15. **Q: Helpful NPE?** A: Shows exact null variable (Java 14+)
16. **Q: Chained exception?** A: Exception with cause
17. **Q: finally overriding return?** A: finally return overrides try return
18. **Q: finally throwing?** A: Overrides original exception
19. **Q: InterruptedException must?** A: Be handled or declared
20. **Q: AssertionError?** A: Thrown when assert fails
