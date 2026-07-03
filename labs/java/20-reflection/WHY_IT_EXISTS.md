# Why Reflection Exists

## Dynamic Behaviour
Code can adapt at runtime — discover and invoke classes not known at compile time (plugins, JDBC drivers).

## Framework Building
Reflection powers:
- **DI containers** (Spring, Guice) — discover `@Inject` fields and constructors
- **ORM** (Hibernate) — map database columns to fields
- **Serialization** (Jackson, Gson) — read/write fields dynamically
- **Testing** (JUnit, TestNG) — discover `@Test` methods
- **Mocking** (Mockito) — create proxies for test doubles

## Class Browsers & Tools
- IDEs show class hierarchy, methods, fields via reflection
- Debuggers inspect live object state
- Decompilation tools

## Why It's Needed
Without reflection, frameworks would require compile-time code generation or verbose configuration.
