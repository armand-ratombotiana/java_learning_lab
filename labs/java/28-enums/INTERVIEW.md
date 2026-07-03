# Interview Questions: Enums

## Q1: How are enums different from integer constants?
Enums are type-safe (compiler rejects invalid values), have namespace (qualify with enum name), provide toString(), support methods and fields, and can be iterated with values(). Integer constants lack all these properties.

## Q2: Can an enum have abstract methods?
Yes. Each enum constant must implement the abstract method. This enables behavior-driven enums where each constant has different behavior.

## Q3: How do EnumMap and EnumSet work internally?
EnumMap uses an array sized to the number of enum constants. Operations use ordinal() as array index. EnumSet uses a bit vector (a single long or an array of longs for large enums). Both are more compact and faster than general-purpose alternatives.

## Q4: Why are enums the best singleton implementation?
Enums provide: serialization safety (JVM guarantees singleness), reflection protection (cannot create additional instances via reflection), and a concise syntax. This was Bloch's recommendation in Effective Java, Item 3.

## Q5: How does enum serialization work?
The JVM serializes the enum constant's name, not its field values. On deserialization, `valueOf()` is called to return the existing singleton. This prevents multiple instances and ensures the singleton guarantee.

## Q6: Can you extend an enum?
No. Enums implicitly extend `java.lang.Enum`, and Java doesn't support multiple inheritance of state. All enums are implicitly final.

## Q7: How do you add behavior to enums?
Add an abstract method and implement it in each constant. For small variations, use a concrete method that switches on a field. For complex behavior per constant, use the abstract method approach.

## Q8: What's the difference between name() and toString()?
`name()` returns the exact string used in the enum declaration. `toString()` is the same by default but can be overridden for user-friendly names.
