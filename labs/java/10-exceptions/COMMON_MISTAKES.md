# Common Mistakes — Exceptions

1. **Swallowing exceptions**: Empty catch block `catch (Exception e) {}` hides bugs.
2. **Catching generic Exception**: `catch (Exception e)` catches everything, including RuntimeExceptions.
3. **Forgetting finally for cleanup**: Resources not closed on exception. Use try-with-resources.
4. **Throwing in finally**: Overrides exception from try block.
5. **Return in finally**: Overrides return value from try/catch block.
6. **Using exceptions for control flow**: `try { parse(s); } catch (Exception e) { return false; }` — expensive. Use validation methods.
7. **Not preserving cause**: `throw new MyException();` loses original exception. Use `new MyException(cause)`.
8. **Checked exception overuse**: Too many checked exceptions clutter API. Consider Runtime for programming errors.
9. **Log and throw**: Same exception logged and thrown — duplicate log entries.
10. **Catching Throwable**: Catches Errors like OutOfMemoryError — can't recover from those.
11. **Not closing resources**: Pre-Java 7: forgetting to close in finally block causes resource leaks.
12. **Ignoring InterruptedException**: Swallowing `InterruptedException` breaks thread cancellation protocol.
