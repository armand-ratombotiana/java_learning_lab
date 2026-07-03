# Reflection: Pattern Matching

## Self-Assessment

1. **Before pattern matching**: How many instanceof-check patterns did you write per day? How many ClassCastExceptions have you debugged in production?

2. **Current codebase**: What percentage of your conditional logic could be expressed more clearly with pattern matching? How would that change readability?

3. **Exhaustiveness culture**: Does your team currently use default cases in switch statements? How many bugs have occurred because a new enum value or type wasn't handled?

4. **Learning curve**: Which aspect of pattern matching took the most effort to understand? The scope rules? Pattern dominance? The interaction with sealed types?

## Design Decisions

### Arrow Syntax vs. Colon Syntax
Pattern matching requires the arrow (`->`) syntax in switch. Do you find this more readable than colon syntax with fall-through? How does it change your mental model of switch?

### Explicit null Handling
Java requires explicit `case null` for null handling in pattern switches. Is this good design (making null handling visible) or friction (forcing you to think about null even when it shouldn't occur)?

### Scope of Pattern Variables
The flow-sensitive scoping of pattern variables (they're in scope where definitely matched) is powerful but has subtle rules. Do you find these rules intuitive or confusing?

## Learning Progress

Rate your understanding from 1 (novice) to 5 (expert) on each:
- [ ] instanceof pattern matching and scope
- [ ] Switch type patterns and ordering
- [ ] Guarded patterns with when
- [ ] Record patterns and nesting
- [ ] Pattern dominance rules
- [ ] Exhaustiveness with sealed types
- [ ] Null handling in pattern matching
- [ ] Combined use with records and sealed classes
- [ ] Performance implications

## Open Questions

1. **Future evolution**: Should Java support collection patterns (matching on list contents)? Should it support custom extractors?

2. **Tooling**: How do IDEs handle pattern matching? Do they add missing cases automatically? Show dominance violations?

3. **Team adoption**: What training material would you create to teach pattern matching to your team? What are the most important warning signs to watch for?

4. **Production impact**: Have you seen reductions in ClassCastException or NullPointerException after adopting pattern matching?
