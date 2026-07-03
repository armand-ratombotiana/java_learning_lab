# Reflection: Sealed Classes

## Self-Assessment

1. **Before sealed classes**: How did you previously control class hierarchies in your projects? Did you use package-private visibility, documentation conventions, or something else?

2. **Exhaustiveness experience**: Think about switch statements in your current codebase. How many have a `default` case that silently swallows unhandled types? Could sealed classes eliminate these?

3. **Visitor pattern pain points**: Have you implemented the Visitor pattern? How many files were involved? Would sealed classes + pattern matching simplify your design?

4. **API design**: If you maintain a library, which classes or interfaces would benefit from being sealed? Which would lose important extensibility?

## Design Decisions

### sealed vs. final
When would you choose `final` over `sealed`? When would you choose `sealed` over `non-sealed`? Is there a general principle you can articulate?

### Permits Granularity
The `permits` clause lists every subtype explicitly. Is this too verbose for large hierarchies? Would an alternative like "permit all classes in this package" be better?

### non-sealed as Escape Hatch
The `non-sealed` modifier is a pragmatic escape hatch for extensibility. But does it undermine the exhaustiveness guarantee? How do you design hierarchies where `non-sealed` is necessary but doesn't compromise safety?

## Learning Progress

Rate your understanding from 1 (novice) to 5 (expert) on each:
- [ ] Declaring sealed classes/interfaces
- [ ] Understanding the three subtype modifiers
- [ ] Exhaustive pattern matching with sealed types
- [ ] Sealed + records for algebraic data types
- [ ] Module-level sealed hierarchies
- [ ] Migration from open to sealed hierarchies
- [ ] Runtime verification and security
- [ ] Replacing Visitor pattern with sealed + pattern matching

## Open Questions

1. **Future evolution**: Should Java add sealed methods (not just classes)? Would that enable new patterns?
2. **Performance**: Does the JIT really optimize sealed hierarchy dispatch better than open hierarchies? Have you seen evidence?
3. **Adoption**: Why might a team resist using sealed classes even when they're clearly beneficial?
4. **Composition over inheritance**: Sealed classes are about controlled inheritance. How does this fit with the modern preference for composition over inheritance?
