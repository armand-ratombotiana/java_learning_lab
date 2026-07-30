# INTERVIEW — Optional Deep

## Company-Specific Focus

### Google
- `Optional` as a return type — never as a field or parameter
- `OptionalInt` vs `Optional<Integer>` — boxing overhead

### Amazon
- Using `Optional` in DTOs and API responses
- `Optional.orElseThrow()` vs manual null check

### Oracle
- `Optional` API additions: `stream()`, `or()`, `ifPresentOrElse()` (Java 9+)
- `Optional.isEmpty()` (Java 11+)

## Common Questions
1. Why shouldn't `Optional` be used as a method parameter?
2. What is the difference between `orElse` and `orElseGet`?
3. When would `Optional.stream()` be useful?
