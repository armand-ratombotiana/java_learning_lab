# Flashcards: Pattern Matching

## Card 1
**Q**: What is pattern matching for instanceof?
**A**: A feature allowing type check, cast, and variable binding in one step: `if (obj instanceof String s) { s.length(); }`.

## Card 2
**Q**: What is a type pattern in switch?
**A**: A case label that matches based on type: `case Integer i -> process(i);`. No explicit cast needed.

## Card 3
**Q**: What is a record pattern?
**A**: A pattern that deconstructs a record into its components: `case Point(int x, int y) -> ...`.

## Card 4
**Q**: What is a guarded pattern?
**A**: A pattern with a condition: `case String s when s.length() > 5 -> ...`. The guard is evaluated after the pattern matches.

## Card 5
**Q**: What does exhaustive mean in pattern matching switch?
**A**: All possible subtypes of a sealed type are covered by cases. The compiler verifies this.

## Card 6
**Q**: What happens if null is passed to a pattern-matching switch without a null case?
**A**: NullPointerException is thrown.

## Card 7
**Q**: What is pattern dominance?
**A**: When a pattern's match set is a superset of another's. A dominated pattern is unreachable.

## Card 8
**Q**: Can record patterns be nested?
**A**: Yes: `case Line(Point(int x1, int y1), Point(int x2, int y2)) -> ...`.

## Card 9
**Q**: Which Java version finalized pattern matching for switch?
**A**: Java 21 (JEP 441).

## Card 10
**Q**: Can you use var in record patterns?
**A**: Yes: `case Point(var x, var y) -> ...` infers component types.

## Card 11
**Q**: What is the scope of a pattern variable in instanceof?
**A**: The if block (and the else block if the instanceof is negated).

## Card 12
**Q**: What does the `default` case cover in a pattern-matching switch?
**A**: Any value not matching earlier cases. It does NOT catch null unless explicitly included.

## Card 13
**Q**: Can you pattern match on arrays?
**A**: Not directly. But you can match on `int[] arr` as a type pattern and access elements manually.

## Card 14
**Q**: What is MatchException?
**A**: Thrown if a switch expression has no default and no case matches (rare — typically from bytecode manipulation).

## Card 15
**Q**: Which pattern types exist in Java 21?
**A**: Type patterns, record patterns, guarded patterns, var patterns.

## Card 16
**Q**: How does the JIT optimize sealed type switches?
**A**: Uses tableswitch/lookupswitch with type IDs for O(1) dispatch instead of O(n) instanceof chain.

## Card 17
**Q**: Can pattern variables shadow existing variables?
**A**: Yes, but the compiler may warn about overshadowing.

## Card 18
**Q**: What is the relationship between sealed classes and pattern matching?
**A**: Sealed classes enable exhaustive switch without default, because the compiler knows all subtypes.

## Card 19
**Q**: Can you use pattern matching in a traditional switch statement (colon syntax)?
**A**: No, pattern matching requires the arrow (->) syntax in switch.

## Card 20
**Q**: What are unnamed patterns (future feature)?
**A**: Pattern variables using `_` to ignore components: `case Point(int x, _) -> ...`. Not yet finalized in Java 21.
