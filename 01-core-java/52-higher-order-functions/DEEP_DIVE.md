# Deep Dive: Higher-Order Functions

## 1. What is a Higher-Order Function?
In mathematics and computer science, a higher-order function is a function that does at least one of the following:
1.  **Takes one or more functions as arguments.**
2.  **Returns a function as its result.**

In Java, since functions are represented by Functional Interfaces (like `Function`, `Predicate`, `Consumer`), a higher-order function is simply a method that accepts or returns an instance of a Functional Interface.

### Why are they important?
Higher-order functions allow you to abstract over *actions*, not just data. Instead of writing a method that loops through a list and prints items, you write a method that loops through a list and *applies a provided action* to each item. This is the foundation of the Strategy Pattern, the Template Method Pattern, and the entire Java Streams API.

## 2. Functions as Parameters (Passing Behavior)
Passing a function as a parameter allows the caller to dictate a specific piece of logic within a larger algorithm.

```java
// A Higher-Order Function
public static <T> List<T> filterList(List<T> list, Predicate<T> condition) {
    List<T> result = new ArrayList<>();
    for (T item : list) {
        if (condition.test(item)) { // The behavior is injected here!
            result.add(item);
        }
    }
    return result;
}

// Usage
List<Integer> numbers = List.of(1, 2, 3, 4, 5);
List<Integer> evens = filterList(numbers, n -> n % 2 == 0);
```

## 3. Functions as Return Values (Function Factories)
Returning a function allows you to generate customized behavior dynamically at runtime. This is incredibly useful for creating configurable validators, formatters, or routing logic.

```java
// A Higher-Order Function returning a Function
public static Function<String, String> createFormatter(String prefix, String suffix) {
    // The returned lambda captures the variables 'prefix' and 'suffix' (Closure)
    return str -> prefix + str + suffix;
}

// Usage
Function<String, String> htmlFormatter = createFormatter("<b>", "</b>");
System.out.println(htmlFormatter.apply("Hello")); // Output: <b>Hello</b>
```

## 4. Currying
Currying is the technique of translating the evaluation of a function that takes multiple arguments into evaluating a sequence of functions, each with a single argument.

For example, converting `f(x, y, z)` into `f(x)(y)(z)`.

In Java, this means a `BiFunction<A, B, R>` becomes a `Function<A, Function<B, R>>`.

```java
// Standard BiFunction (Uncurried)
BiFunction<Integer, Integer, Integer> add = (x, y) -> x + y;
System.out.println(add.apply(2, 3)); // 5

// Curried Version
Function<Integer, Function<Integer, Integer>> curriedAdd = x -> y -> x + y;
System.out.println(curriedAdd.apply(2).apply(3)); // 5
```
*   **Why Curry?** It allows you to elegantly "bake in" configuration arguments one at a time, passing the partially configured function around your application until all arguments are collected.

## 5. Partial Application
Partial application is related to currying. It is the process of fixing a number of arguments to a function, producing another function of smaller arity (fewer arguments).

If you have a function that takes 3 arguments, and you provide 1 argument, you get back a new function that takes 2 arguments.

```java
// Original 3-argument function
@FunctionalInterface
interface TriFunction<A, B, C, R> {
    R apply(A a, B b, C c);
}

TriFunction<Double, Double, Double, Double> calculateTax = 
    (price, stateRate, localRate) -> price * (1 + stateRate + localRate);

// Partial Application: Fix the stateRate and localRate for a specific region
Function<Double, Double> nyTaxCalculator = 
    price -> calculateTax.apply(price, 0.04, 0.045);

System.out.println(nyTaxCalculator.apply(100.0)); // Output: 108.5
```