# Refactoring Exceptions

## Replace Return Codes with Exceptions

Before: `int result = process(); if (result == -1) { /* error */ }`
After: `process();` — throws `ProcessingException` on failure.

## Replace Throwing Generic Exception with Specific Ones

Before: `throw new Exception("Invalid input");`
After: `throw new IllegalArgumentException("Invalid input");`

## Replace If-Else Error Handling with try-catch

Before: Deeply nested if-else checking error returns.
After: `try { process(); } catch (SpecificException e) { handle(); }`

## Extract Try Block to Method

Before: try block with 30+ lines — hard to see what's protected.
After: Extract content to method, try only at appropriate level.

## Use Try-With-Resources

Before: `try { r = open(); ... } finally { if (r != null) r.close(); }`
After: `try (Resource r = open()) { ... }`

## Use Multi-Catch

Before: `catch (IOException e) { log(e); } catch (SQLException e) { log(e); }`
After: `catch (IOException | SQLException e) { log(e); }`

## Preserve Original Exception

Before: `throw new MyException();` — loses root cause.
After: `throw new MyException("message", originalException);`

## Replace Custom Exception Hierarchy with Enum

Before: Multiple exception subclasses for similar errors.
After: Single exception class with error enum field.
