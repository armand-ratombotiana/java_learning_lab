# Quiz: Pattern Matching

## Question 1
Which Java version introduced pattern matching for instanceof as a finalized feature?

A) Java 14
B) Java 15
C) Java 16
D) Java 17

## Question 2
What keyword is used to add a condition to a pattern in a switch expression?

A) `if`
B) `when`
C) `where`
D) `guard`

## Question 3
What happens if a switch expression with patterns receives null and there's no case null?

A) The switch falls through to default
B) A NullPointerException is thrown
C) The switch returns null
D) A MatchException is thrown

## Question 4
Which of the following is a valid record pattern?

A) `case Point(x, y) ->`
B) `case Point(int x, int y) ->`
C) `case Point(x: int, y: int) ->`
D) `case Point->(x, y) ->`

## Question 5
What is pattern dominance?

A) When a pattern matches faster than another pattern
B) When one pattern's match set is a superset of another's
C) When a pattern has a guard condition
D) When a pattern is used in a sealed hierarchy

## Question 6
Can a pattern variable `s` in `obj instanceof String s` be used in the else branch?

A) Yes, always
B) Yes, if the else branch is paired with a negated expression
C) No, never
D) Only if s is declared volatile

## Question 7
What is required for an exhaustive switch without a default case?

A) The selector must be an enum
B) The selector must be a sealed type with all subtypes covered
C) The selector must be a String
D) The switch must have exactly 3 cases

## Question 8
Which is NOT a valid pattern type in Java 21?

A) Type pattern: `case String s`
B) Record pattern: `case Point(int x, int y)`
C) Collection pattern: `case List.of(a, b)`
D) Guarded pattern: `case Integer i when i > 0`

## Question 9
What does the `_` (unnamed pattern) do in Java pattern matching?

A) It matches any value without binding
B) It is not yet a standard feature (planned for future releases)
C) It ignores the previous pattern
D) It creates a wildcard type

## Question 10
How does the JIT compiler handle record pattern accessor calls?

A) It cannot optimize them
B) It inlines them to direct field reads
C) It caches the results
D) It converts them to reflection

## Answer Key
1. C, 2. B, 3. B, 4. B, 5. B, 6. B, 7. B, 8. C, 9. B, 10. B
