# Reflection: Optional

## Self-Assessment

1. **Before Optional**: How did you handle potentially absent values? Did you use null checks, sentinel values, or exceptions?

2. **Null safety culture**: Does your team have a culture of null checking? How many NullPointerExceptions have occurred in production this year?

3. **API design**: Look at the public APIs in your codebase. How many methods return values that could be null? How many callers handle null correctly?

4. **Functional style**: How comfortable are you with the functional style (map/flatMap/filter) vs. imperative null checks?

## Design Decisions

### Optional for Return Types
Brian Goetz has said Optional is "designed to provide a limited mechanism for library method return types where there is a clear need to represent 'no result.'" Do you agree with this limited scope? Would broader use be better?

### orElse vs orElseGet
The subtle difference between `orElse(compute())` (always computes) and `orElseGet(() -> compute())` (lazy) is a common source of bugs. Is this a design flaw or a reasonable tradeoff?

### Not Serializable
Optional is not Serializable. Is this the right decision, or should Optional implement Serializable for use in distributed systems?

## Learning Progress

Rate your understanding from 1 (novice) to 5 (expert) on each:
- [ ] Creating Optionals (of, ofNullable, empty)
- [ ] Retrieving values (orElse, orElseGet, orElseThrow)
- [ ] Transforming with map/filter
- [ ] flatMap for chaining Optional-returning methods
- [ ] or() for fallback chains
- [ ] Optional with streams
- [ ] Primitive Optionals (OptionalInt, etc.)
- [ ] Best practices and anti-patterns
- [ ] Performance characteristics

## Open Questions

1. **Future evolution**: Should Java add pattern matching for Optional (e.g., `switch (opt) { case Optional.of(var x) -> ...; case Optional.empty() -> ... }`)?

2. **Team adoption**: What strategies work best for introducing Optional to a team accustomed to null-heavy code?

3. **Tooling**: How well does your IDE support Optional (inspection warnings, quick fixes)?

4. **Real impact**: Have you measurably reduced NPE-related bugs after adopting Optional?
