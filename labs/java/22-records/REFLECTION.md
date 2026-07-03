# Reflection: Records

## Self-Assessment

1. **Before records**: Think about how many simple data classes you wrote in previous projects. Estimate the total lines of boilerplate code. How would records have changed those projects?

2. **Identifying value objects**: Look at the codebase you work on most. How many classes are candidates for conversion to records? What percentage are true value objects (defined by state) vs. entities (defined by identity)?

3. **Immutability tradeoffs**: Consider a class that you intentionally left mutable. Would converting it to an immutable record make the code safer? What refactoring in callers would be needed?

4. **Record patterns in Java 21**: How would record patterns change the way you write instance-of checks and type-based dispatch in your current code?

## Design Decisions

### Naming Convention
Records use `point.x()` not `point.getX()`. Is this a better naming convention for accessors in general? Would you change your codebase to use this style even in non-record classes?

### Compact Constructors vs. Lombok
If you've used Lombok's `@Data` or `@Value`, compare the experience with records. What does Lombok do better? What do records do better?

### When NOT to Use Records
Consider scenarios where records would be a poor choice:
- JPA/Hibernate entities
- Objects with mutable state
- Objects with complex initialization
- Objects where identity matters

Are there cases where you would have used records but now think a class is better? Or vice versa?

## Learning Progress

Rate your understanding from 1 (novice) to 5 (expert) on each:
- [ ] Basic record declaration and instantiation
- [ ] Compact constructors with validation
- [ ] Custom methods and non-canonical constructors
- [ ] Records and serialization
- [ ] Local records in streams
- [ ] Record patterns and destructuring
- [ ] Records with sealed types
- [ ] Defensive copying of mutable components
- [ ] Records vs. traditional class tradeoffs

## Open Questions

1. **Future evolution**: Should records eventually support mutable components (like Kotlin's `var`)? Why or why not?
2. **Performance**: Does the automatic generation of equals/hashCode for records with many components introduce performance concerns? Would you ever override these generated methods?
3. **Framework support**: Which frameworks in your stack don't yet support records? Is this blocking adoption?
4. **Team adoption**: What training or coding standards would your team need to adopt records effectively?
