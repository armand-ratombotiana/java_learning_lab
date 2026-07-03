# Flashcards: Optional

## Card 1
**Q**: What is Optional<T>?
**A**: A container that may or may not contain a non-null value, providing a type-safe alternative to null.

## Card 2
**Q**: What does Optional.of(x) do?
**A**: Creates an Optional containing x. Throws NullPointerException if x is null.

## Card 3
**Q**: What does Optional.ofNullable(x) do?
**A**: Creates an Optional containing x if x is non-null, or an empty Optional if x is null.

## Card 4
**Q**: What does Optional.empty() do?
**A**: Returns a singleton empty Optional with no value.

## Card 5
**Q**: What is the difference between orElse and orElseGet?
**A**: orElse always evaluates the default argument; orElseGet only evaluates the supplier if the Optional is empty.

## Card 6
**Q**: What does map() do on Optional?
**A**: Applies a function to the value if present, returning Optional.of(result). Returns empty if the Optional is empty.

## Card 7
**Q**: What does flatMap() do?
**A**: Similar to map but the function returns an Optional, and the result is not nested (flat).

## Card 8
**Q**: What does filter() do?
**A**: Returns the Optional if present and the predicate matches, otherwise returns empty.

## Card 9
**Q**: What does ifPresent() do?
**A**: Executes a consumer action if the value is present, does nothing if empty.

## Card 10
**Q**: Is Optional serializable?
**A**: No. Optional is intentionally not Serializable.

## Card 11
**Q**: What is the purpose of Optional?
**A**: To provide a type-safe return type for methods that might not have a result, reducing NullPointerException risk.

## Card 12
**Q**: Should Optional be used for fields?
**A**: No. Use nullable fields directly. Optional is for return types.

## Card 13
**Q**: Should Optional be used for method parameters?
**A**: No. Use method overloading instead.

## Card 14
**Q**: What does orElseThrow() do (Java 10+)?
**A**: Returns the value if present, or throws NoSuchElementException if empty.

## Card 15
**Q**: What does or() do (Java 9+)?
**A**: Returns this Optional if present, otherwise returns the Optional from the supplier.

## Card 16
**Q**: What does Optional.stream() do (Java 9+)?
**A**: Returns a Stream of zero or one elements (empty or value).

## Card 17
**Q**: What is OptionalInt?
**A**: A primitive specialization of Optional for int values, avoiding boxing.

## Card 18
**Q**: What is the memory cost of a present Optional?
**A**: ~16-24 bytes (object header + reference). Empty Optional uses a cached singleton.

## Card 19
**Q**: What is the monad nature of Optional?
**A**: Optional satisfies the three monad laws: left identity, right identity, and associativity.

## Card 20
**Q**: When should you NOT use Optional?
**A**: Fields, method parameters, collections, serialized data, performance-critical hot loops.
