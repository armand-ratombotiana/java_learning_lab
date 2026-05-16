# Lambda Expressions Flashcards

## Q1: What is a functional interface?
**A**: An interface with exactly one abstract method. May have default methods. Marked with @FunctionalInterface.

---

## Q2: What does the -> arrow mean in lambda?
**A**: Separates parameters from body. Left side is parameters, right side is lambda body.

---

## Q3: What is method reference?
**A**: Shorthand syntax for lambdas that call a single method. Example: String::toUpperCase equals s -> s.toUpperCase()

---

## Q4: What is "effectively final"?
**A**: A variable not explicitly declared final but never modified after initialization. Required for lambda capture.

---

## Q5: What's the difference between Function and Consumer?
**A**: Function<T,R> takes input, returns output. Consumer<T> takes input, returns nothing (void).

---

## Q6: What does Predicate return?
**A**: boolean - used for filtering based on a condition.

---

## Q7: What does Supplier return?
**A**: T - takes no input, returns an object. Used for lazy creation.

---

## Q8: What is Function composition?
**A**: Combining two functions using andThen() or compose(). Example: f.andThen(g) applies f then g.

---

## Q9: What's the difference between map and flatMap?
**A**: map transforms each element to one result. flatMap transforms to stream of results and flattens.

---

## Q10: What is terminal operation?
**A**: Operation that ends the stream pipeline (collect, reduce, forEach, findFirst, etc.)

---

## Q11: When is lambda execution lazy?
**A**: Intermediate operations (filter, map) are lazy - they don't execute until terminal operation.

---

## Q12: What is the difference between reduce and collect?
**A**: reduce combines elements into single value. collect groups elements into a collection.

---

## Q13: What is the :: syntax in method references?
**A**: Method reference operator. String::length means x -> x.length()

---

## Q14: What does the @FunctionalInterface annotation do?
**A**: Compile-time checking that interface has exactly one abstract method. Optional but recommended.

---

## Q15: Can lambdas access local variables?
**A**: Yes, but only effectively final local variables. Can always access instance and static variables.