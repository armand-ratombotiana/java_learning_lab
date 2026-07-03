# Mental Models — Lambdas

## Lambda as Anonymous Function
Think of a lambda as a function literal — like an `int` literal or `String` literal, but for behaviour.

```
x -> x * 2   ≡   function(x) { return x * 2; }
```

## Lambda as Behaviour Parameterisation
Treat methods that accept lambdas as "behaviour holes":
```java
processItems(list, item -> item.isValid()); // plug in behaviour
```

## Closure as Backpack
A lambda captures variables from its enclosing scope — imagine the lambda carrying a backpack with copies of those variables.

## Method Reference as Shortcut
```java
String::toUpperCase   ≡   s -> s.toUpperCase()
System.out::println   ≡   s -> System.out.println(s)
```
