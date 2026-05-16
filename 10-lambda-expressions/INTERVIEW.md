# Interview Questions: Lambda Expressions

## Basic Questions

### Q1: What is a functional interface?
**A**: An interface with exactly one abstract method. Java 8 introduced @FunctionalInterface annotation to enforce this constraint.

### Q2: Explain lambda expression syntax
**A**: `(parameters) -> { body }`. Parameters can be typed or inferred. Body can be expression (implicit return) or block (explicit return).

### Q3: What are method references?
**A**: Compact lambda expressions that call a single method. Four types: static (Integer::parseInt), instance (String::length), instance on parameter (String::compareTo), constructor (ArrayList::new).

### Q4: What is the difference between Function and Predicate?
**A**: Function<T,R> maps T to R (transformation). Predicate<T> maps T to boolean (test/filter).

## Intermediate Questions

### Q5: How does lambda capture work?
**A**: Lambdas can capture instance and static variables freely, but local variables must be effectively final.

### Q6: What is the difference between map and flatMap?
**A**: map transforms each element to one result element. flatMap transforms to stream and flattens all resulting streams into one.

### Q7: Explain stream lazy evaluation
**A**: Intermediate operations (filter, map, etc.) are lazy - they build a pipeline but don't execute until a terminal operation triggers execution.

### Q8: What is method reference and when would you use it?
**A**: Shorthand for lambdas calling a single method. Use when the lambda simply calls a method - it's more concise and readable.

## Advanced Questions

### Q9: How does parallelStream work?
**A**: Splits data into chunks, processes each in separate thread, then combines results. Use for CPU-intensive or large datasets, not I/O-bound operations.

### Q10: What is the performance impact of lambdas?
**A**: Lambdas are created via invokedynamic (Java 8+) which uses caching. Performance is usually negligible for typical use. Use primitive streams (IntStream) to avoid boxing overhead.

### Q11: How would you debug a lambda?
**A**: Add peek() to inspect elements, use IDE debug with breakpoints (lambdas show as synthetic methods), or add logging in lambda body.

### Q12: What is side-effect free programming?
**A**: Lambdas should not modify external state. Operations should transform data through parameters and return values, making code more predictable and parallelizable.

### Q13: Compare reduce vs collect
**A**: reduce() combines elements into one value - good for sum, max, etc. collect() groups elements into a container - good for toList(), groupingBy(), etc.

### Q14: What are the four types of method references?
**A**: 
1. Static: ClassName::staticMethod (x -> ClassName.staticMethod(x))
2. Instance on object: obj::instanceMethod (x -> obj.instanceMethod(x))
3. Instance on type: ClassName::instanceMethod ((a,b) -> a.instanceMethod(b))
4. Constructor: ClassName::new () -> new ClassName()