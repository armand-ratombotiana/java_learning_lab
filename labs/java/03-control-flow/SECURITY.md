# Security — Control Flow

## Denial of Service via Infinite Loops

User input should never directly control unbounded loop iterations. Always set maximum bounds.

## SQL Injection

Never concatenate user input into SQL queries: `"SELECT * FROM users WHERE id = " + id`. Always use parameterized queries.

## Path Traversal

When iterating over files: validate user-provided paths. Strip `..` and check against allowed directory.

## Resource Exhaustion

Loops that allocate memory based on user input can cause OutOfMemoryError. Validate input size before processing.

## Timing Attacks

String comparison: `if (userInput.equals(secret))` leaks timing information based on character-by-character comparison length. Use `MessageDigest.isEqual()` for constant-time comparison.
