# History of Lambda Expressions in Java

## Timeline

### Java 8 (March 2014) - Introduction
- Lambda expressions added to the language
- Method references introduced
- Default methods in interfaces
- Stream API added to java.util.stream
- Functional interfaces: java.util.function package

### Java 9 (September 2017)
- Enhanced Lambda parameter inference
- Private methods in interfaces (default methods)

### Java 10 (March 2018)
- Local-Variable Type Inference (var) with lambdas
- var user = (String s) -> s.toLowerCase();

### Java 11 (September 2018)
- String methods now available as method references
- "abc"::toUpperCase becomes cleaner

## Language Comparison

| Language | Lambda Syntax |
|----------|---------------|
| Java 8+ | `x -> x * 2` |
| C# | `x => x * 2` |
| Python | `lambda x: x * 2` |
| JavaScript | `x => x * 2` |
| Scala | `x => x * 2` or `_ * 2` |

## Key JEPs

- JEP 103: Lambda Expressions for the Java Programming Language
- JEP 107: Bulk Data Operations for Collections
- JEP 109: Lambda Expressions for the Java Programming Language