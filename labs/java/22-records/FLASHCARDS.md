# Flashcards: Records

## Card 1
**Q**: What is a record in Java?
**A**: A special class that serves as a transparent carrier for immutable data. It declares state via components and automatically derives constructor, accessors, equals, hashCode, and toString.

## Card 2
**Q**: How do you declare a record with two int components?
**A**: `record Point(int x, int y) {}`

## Card 3
**Q**: What methods are automatically generated for a record?
**A**: Canonical constructor, accessor methods (one per component), `equals()`, `hashCode()`, `toString()`.

## Card 4
**Q**: Can a record be extended by another class?
**A**: No. All records are implicitly final.

## Card 5
**Q**: What is a compact constructor?
**A**: A constructor that omits the parameter list and is used for validation of normalizing input before the implicit field assignment.

## Card 6
**Q**: How does equals() work for records?
**A**: Two records are equal if they are the same type and all components are equal (recursively).

## Card 7
**Q**: What is the naming convention for record accessor methods?
**A**: Named after the component (e.g., `point.x()`, `person.name()`), not prefixed with `get`.

## Card 8
**Q**: Can records have custom methods?
**A**: Yes. Records can have instance methods, static methods, and non-canonical constructors (which must delegate to the canonical constructor).

## Card 9
**Q**: What is a local record?
**A**: A record declared inside a method or block, scoped to that enclosing context. Useful for intermediate data in stream pipelines.

## Card 10
**Q**: How are records serialized?
**A**: Record serialization always uses the canonical constructor. Custom readObject/writeObject are ignored.

## Card 11
**Q**: What is the superclass of all records?
**A**: `java.lang.Record` (an abstract class).

## Card 12
**Q**: Can records implement interfaces?
**A**: Yes, records can implement any number of interfaces.

## Card 13
**Q**: Are records mutable?
**A**: Records are shallowly immutable. The record fields are final, but mutable component types (like List) can still be modified.

## Card 14
**Q**: How do you add validation to a record?
**A**: Use a compact constructor: `record Range(int start, int end) { Range { if (start > end) throw ... } }`

## Card 15
**Q**: Can records have static fields?
**A**: Yes, but only static fields (not instance fields beyond the components).

## Card 16
**Q**: Can records have non-canonical constructors?
**A**: Yes, but they must delegate to the canonical constructor via `this(...)`.

## Card 17
**Q**: What is the reflection method to check if a class is a record?
**A**: `Class.isRecord()`

## Card 18
**Q**: How do you get a record's components via reflection?
**A**: `Class.getRecordComponents()` returns an array of `RecordComponent` objects.

## Card 19
**Q**: What annotation controls component annotation propagation?
**A**: `@Target(ElementType.RECORD_COMPONENT)` annotations are propagated to constructor params, fields, and accessor methods.

## Card 20
**Q**: Should JPA entities be implemented as records?
**A**: No. JPA requires no-arg constructors and supports lazy loading through proxies, which are incompatible with records' immutability and lack of setters.
