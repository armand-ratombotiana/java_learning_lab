# History of Pattern Matching in Java

## Origins: Project Amber

Pattern matching was developed as part of Project Amber, alongside records, sealed classes, and switch expressions.

### Early Discussions (2016-2017)
Brian Goetz began discussing pattern matching for Java around 2016. The initial proposal was modest: extend `instanceof` to include a binding variable. This was seen as a low-risk introduction to the pattern matching concept.

### Design Influences
Scala's pattern matching (on case classes), Haskell's algebraic data types, and C#'s pattern matching all influenced Java's design. Java's version is more conservative, focusing on the most common use cases and ensuring backward compatibility.

## Preview Phases

### Java 14 (March 2020) — instanceof Pattern Matching Preview (JEP 305)
The first pattern matching feature: `if (obj instanceof String s)`. This was deliberately minimal to test the community's reaction to pattern variable binding.

### Java 16 (March 2021) — instanceof Pattern Matching Finalized (JEP 394)
Pattern matching for `instanceof` became a permanent feature with no changes from the preview. This was one of the shortest preview periods, reflecting the feature's simplicity and community support.

### Java 16 — Records Finalized (JEP 395)
Records were finalized, providing the foundation for record patterns.

### Java 17 — Sealed Classes Finalized (JEP 409)
Sealed classes were finalized, providing the foundation for exhaustiveness in pattern matching.

### Java 17 — Switch Pattern Matching Preview (JEP 406)
The first preview of pattern matching in switch expressions. Introduced:
- Type patterns in switch cases
- Guarded patterns with `when`
- Null case handling

### Java 18 — Second Preview (JEP 420)
Refinements based on feedback:
- Improved exhaustiveness checking
- Pattern dominance rules clarified

### Java 19 — Third Preview (JEP 427)
Added record patterns to switch:
- `case Point(int x, int y) -> ...`
- Nested record patterns

### Java 20 — Fourth Preview (JEP 433)
Final refinements:
- Pattern dominance checking improved
- Interaction with sealed types clarified

### Java 21 — Finalized (JEP 440, JEP 441)
Two JEPs finalized pattern matching:
- **JEP 440: Record Patterns** — Deconstruction of records
- **JEP 441: Pattern Matching for switch** — Type patterns, guards, exhaustiveness

## Timeline Summary

| Version | Date | Feature | JEP | Status |
|---------|------|---------|-----|--------|
| Java 14 | Mar 2020 | instanceof pattern matching | 305 | Preview |
| Java 16 | Mar 2021 | instanceof pattern matching | 394 | Finalized |
| Java 16 | Mar 2021 | Records | 395 | Finalized |
| Java 17 | Sep 2021 | Sealed Classes | 409 | Finalized |
| Java 17 | Sep 2021 | Switch pattern matching | 406 | Preview |
| Java 18 | Mar 2022 | Switch pattern matching | 420 | 2nd Preview |
| Java 19 | Sep 2022 | Switch + Record patterns | 427 | 3rd Preview |
| Java 20 | Mar 2023 | Switch + Record patterns | 433 | 4th Preview |
| Java 21 | Sep 2023 | Record Patterns | 440 | Finalized |
| Java 21 | Sep 2023 | Switch pattern matching | 441 | Finalized |

## Future Directions

Ongoing work and planned features include:
- **Unnamed patterns** (`_`): To ignore matched components
- **Collection patterns**: Matching on collection contents
- **Primitive type patterns**: For specialized type hierarchies
- **Universal `match` expression**: Possibly a dedicated `match` keyword
