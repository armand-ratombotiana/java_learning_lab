# Common Mistakes — Lambdas

## 1. Modifying Captured Local Variables
```java
int count = 0;
list.forEach(x -> count++); // Compile error — count must be effectively final
```
**Fix:** Use an atomic integer or a mutable container.

## 2. Returning a Value in a Void Lambda
```java
Consumer<String> c = s -> s.length(); // Compile error — Consumer returns void
```

## 3. Confusing `(a, b)` with `(a, b) ->`
Missing arrow or incorrect syntax causes cryptic errors.

## 4. Type Ambiguity with Overloaded Methods
```java
void process(Function<String, String> f) { }
void process(Predicate<String> p) { }

process(s -> s.isEmpty()); // Ambiguous — cast to resolve
```

## 5. Using `this` in a Lambda vs Anonymous Class
In anonymous classes, `this` refers to the inner class instance; in lambdas, `this` refers to the enclosing class. This is a common source of confusion.

## 6. Forgetting `@FunctionalInterface`
The annotation is not required but prevents accidental addition of abstract methods.

## 7. Lambda Too Large for Readability
If the lambda body exceeds 3-5 lines, extract it into a method and use a method reference.
