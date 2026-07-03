# Data Types — Evolution Across Java Versions

## Java 1.0 (1996)

The original 8 primitives: `byte`, `short`, `int`, `long`, `float`, `double`, `char`, `boolean`. Wrapper classes: `Byte`, `Short`, `Integer`, `Long`, `Float`, `Double`, `Character`, `Boolean`. No autoboxing — manual conversion required: `Integer.valueOf(5)`.

## Java 5 (2004) — Major Change

- **Autoboxing/unboxing**: Automatic conversion between primitives and wrappers
- **Enhanced for-loop**: Works with arrays and Iterables
- **Generics**: `List<Integer>` — wrappers required since generics don't work with primitives
- **Static imports**: `import static java.lang.Integer.MAX_VALUE;`
- **Varargs**: `int...` becomes array

## Java 7 (2011)

- **Binary literals**: `int x = 0b1010;`
- **Underscores in literals**: `int million = 1_000_000;`
- **Diamond operator**: `List<Integer> list = new ArrayList<>();`

## Java 8 (2014)

- **Optional**: `OptionalInt`, `OptionalLong`, `OptionalDouble` for primitive streams
- **Stream APIs**: `IntStream`, `LongStream`, `DoubleStream` avoid boxing overhead
- **Enhanced type inference**: Compiler infers types in more contexts

## Java 10 (2018)

- **Local variable type inference**: `var count = 5;` compiles to `int count = 5;`

## Java 14 (2020)

- **Records** (preview): `record Point(int x, int y)` — compiler generates constructor, accessors, equals, hashCode, toString
- **Pattern matching for instanceof**: Avoids manual casting after type check

## Java 16 (2021)

- **Records standard**: First-class data carriers with value semantics
- **Pattern matching standard**: `if (obj instanceof Integer i)`

## Java 21 (2023)

- **Record patterns**: `if (obj instanceof Point(int x, int y))`
- **Pattern matching for switch** (standard): `case Integer i -> ...`
- **Unnamed patterns**: `case _ ->`

## Future Direction

- **Value types (Project Valhalla)**: Primitive-like performance for user-defined types. In development — will transform how data types work in Java by allowing inline classes that have no identity.
