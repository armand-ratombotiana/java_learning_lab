# MOCK_INTERVIEW — Optional Deep

## Scenario
Given a nested `User -> Address -> City` structure where each getter returns `Optional`, extract the city name safely, or return "Unknown".

## Interviewer Notes
- Candidate should use `flatMap` chain, not nested `isPresent`/`get`
- Discuss `orElse("Unknown")` vs `orElseGet(() -> computeExpensive())`
- Mention `Optional.map()` for transforming

## Expected Solution Sketch
```java
String city = user.flatMap(User::getAddress)
    .flatMap(Address::getCity)
    .map(City::name)
    .orElse("Unknown");
```

## Follow‑Up
- Convert a `List<Optional<String>>` to `List<String>` skipping empty
- How would you handle 10 levels of nesting?
