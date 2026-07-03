# Refactoring Control Flow

## Replace Traditional For with Enhanced For

Before: `for (int i = 0; i < list.size(); i++) { process(list.get(i)); }`
After: `for (var item : list) { process(item); }`

## Replace if-else Ladder with Switch

Before: Multiple if-else on same variable
After: `switch (value) { case X -> ...; default -> ...; }`

## Replace Switch Statement with Switch Expression

Before: Statement with break, variable assigned in each case.
After: `int result = switch (val) { case 1 -> 10; case 2 -> 20; default -> 0; };`

## Extract Complex Conditions to Method

Before: `if (user != null && user.isActive() && user.getAge() >= 18)`
After: `if (isEligibleAdult(user))`

## Remove Dead Code After Return

Before: `if (x > 0) { return x; } else { return 0; } return -1;` — last line unreachable
After: Remove unreachable code.

## Flatten Nested Conditions

Before: Deeply nested if blocks.
After: Early returns or extracted methods.

## Replace While with For When Knowing Bounds

Before: `int i = 0; while (i < 10) { ... i++; }`
After: `for (int i = 0; i < 10; i++) { ... }`
