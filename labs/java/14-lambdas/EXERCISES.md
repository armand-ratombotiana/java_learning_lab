# Exercises — Lambdas

## Beginner
1. Convert the following anonymous class to a lambda:
   ```java
   Runnable r = new Runnable() {
       public void run() { System.out.println("Hi"); }
   };
   ```

2. Write a lambda that implements `Comparator<String>` to sort by length.

3. Use `Consumer<String>` to print each element of a list.

## Intermediate
4. Implement a custom `@FunctionalInterface` called `Validator<T>` and use it with a lambda.

5. Compose three `Function` instances (andThen/compose) to create a pipeline: `String → Integer → String`.

6. Use `Supplier<LocalDate>` to lazily supply the current date.

7. Write a method that takes `Predicate<Integer>` and tests a range of numbers.

## Advanced
8. Create a fluent builder pattern using lambdas:
   - `Mailer.send(mailer -> mailer.to("a@b.com").subject("Hi").body("Hello"))`

9. Implement a simple event bus using lambdas as listeners.

10. Use method references (`ClassName::staticMethod`, `instance::method`, `Class::new`) and explain each variant.

## Reflection
11. Decompile a lambda class file — examine the `invokedynamic` instruction and bootstrap method.
