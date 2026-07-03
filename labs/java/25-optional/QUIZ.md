# Quiz: Optional

## Question 1
Which method creates an Optional that may be empty if the provided value is null?

A) `Optional.of()`
B) `Optional.empty()`
C) `Optional.ofNullable()`
D) `Optional.fromNullable()`

## Question 2
What happens when you call `get()` on an empty Optional?

A) Returns null
B) Throws NoSuchElementException
C) Throws NullPointerException
D) Returns Optional.empty()

## Question 3
What is the difference between `orElse()` and `orElseGet()`?

A) There is no difference
B) `orElse` always evaluates the argument; `orElseGet` only evaluates when the Optional is empty
C) `orElseGet` throws an exception; `orElse` returns a default
D) `orElse` works with primitives; `orElseGet` works with objects

## Question 4
Which method should you use instead of `map()` when the mapping function returns an Optional?

A) `then()`
B) `chain()`
C) `flatMap()`
D) `andThen()`

## Question 5
Which Java version added `Optional.isEmpty()`?

A) Java 8
B) Java 9
C) Java 10
D) Java 11

## Question 6
What does `Optional.stream()` do (Java 9+)?

A) Converts the Optional to a Stream of its class methods
B) Returns a Stream with zero or one element
C) Returns a Stream of Optional operations
D) Converts the value to a byte stream

## Question 7
Is Optional serializable?

A) Yes
B) No
C) Only in Java 8+
D) Only when the value is serializable

## Question 8
Which of these is a valid use case for Optional?

A) A field in an entity class
B) A method parameter
C) A method return type
D) An element in a collection

## Question 9
What does `Optional.or()` (Java 9+) do?

A) Provides an alternative value if empty
B) Provides an alternative Optional if empty
C) Combines two Optionals into one
D) Returns the Optional or throws

## Question 10
What is the correct way to execute an action only if the Optional is present?

A) `opt.ifPresent(action)`
B) `if (opt.isPresent()) action.run()`
C) `opt.orElseThrow().run()`
D) Both A and B are correct

## Answer Key
1. C, 2. B, 3. B, 4. C, 5. D, 6. B, 7. B, 8. C, 9. B, 10. D
