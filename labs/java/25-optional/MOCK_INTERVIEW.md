# Mock Interview Transcript: Optional

## Interviewer: Senior SWE, Meta
## Candidate: Junior Java developer
## Time: 25 minutes
## Focus: Optional usage, best practices, common mistakes

---

**Q1: What is Optional? When should you use it?**

**Candidate**: `Optional<T>` is a container that may or may not contain a value. Use it as a return type from methods that might not have a result. It forces the caller to consider the absence case.

**Interviewer**: When should you NOT use Optional?

**Candidate**: (1) As a field in a class — `Optional` is not serializable and adds an extra object. (2) As a method parameter — makes the API awkward; use method overloading instead. (3) In collections — `Map<String, Optional<String>>` is confusing. (4) As a replacement for null checks in all cases — null is fine for local variables. (5) For performance-critical paths — `Optional` adds allocation overhead.

**Interviewer**: What methods does Optional provide for safe access?

**Candidate**: `ifPresent(Consumer)` — do something if value exists. `ifPresentOrElse(Consumer, Runnable)` — handle present and absent cases. `orElse(default)` — return value or default. `orElseGet(Supplier)` — return value or compute default lazily. `orElseThrow()` — return value or throw NoSuchElementException. `orElseThrow(Supplier)` — return value or throw custom exception. `stream()` — returns a stream of zero or one element.

**Interviewer**: What's wrong with this code?

```java
Optional<String> opt = getOptionalResult();
if (opt.isPresent()) {
    System.out.println(opt.get());
}
```

**Candidate**: It defeats the purpose of using Optional. The point is to avoid explicit `isPresent()`/`get()` checks. Better: `opt.ifPresent(System.out::println)`. Using `isPresent()`/`get()` is the Optional equivalent of checking for null — you've gained nothing.

**Interviewer**: Chain Optional operations: get user, get their address, get city, or return "Unknown".

**Candidate**: 
```java
String getCity(Long userId) {
    return findUser(userId)
        .flatMap(User::getAddress)
        .map(Address::city)
        .orElse("Unknown");
}
```

**Interviewer**: What's the difference between `map` and `flatMap` when the function already returns Optional?

**Candidate**: If `User::getAddress` returns `Optional<Address>`, then `map(u -> u.getAddress())` returns `Optional<Optional<Address>>`. `flatMap(u -> u.getAddress())` returns `Optional<Address>`. Always use `flatMap` when the lambda returns Optional.

**Interviewer**: How does `Optional.stream()` help?

**Candidate**: `Optional.stream()` (Java 9+) returns a stream of 0 or 1 elements. This is useful when combining multiple optionals in stream operations:
```java
List<String> cities = users.stream()
    .map(User::getAddress)  // Optional<Address>
    .flatMap(Optional::stream)  // Stream<Address> (filtering out empty)
    .map(Address::city)
    .toList();
```
Without it, you'd need `.filter(Optional::isPresent).map(Optional::get)`.

**Interviewer**: How would you handle a `findUser` that can throw an exception?

**Candidate**: 
```java
Optional<User> findUserSafe(long id) {
    try { return Optional.ofNullable(userRepo.findById(id)); }
    catch (Exception e) {
        log.warn("Error finding user", e);
        return Optional.empty();  // or throw after logging
    }
}
```

---

## Feedback

**Strengths**:
- Correctly identifies Optional use cases and anti-patterns
- Proper chain of map/flatMap/orElse
- Knows Optional.stream() for stream integration

**Areas for Improvement**:
- Could mention `or()` (Java 9+) for alternative Optional
- Should discuss that Optional doesn't solve the null problem for fields

**Score**: 3.5/5 — Solid Optional usage
