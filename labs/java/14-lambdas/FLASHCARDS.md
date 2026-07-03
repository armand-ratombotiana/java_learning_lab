# Flashcards — Lambdas

**Q:** What is a functional interface?  
**A:** An interface with exactly one abstract method.

**Q:** What is the syntax for a lambda with one parameter?  
**A:** `param -> expression`

**Q:** What is a method reference?  
**A:** A shorthand for a lambda calling an existing method, e.g. `String::length`.

**Q:** Can lambdas capture mutable local variables?  
**A:** No, they must be effectively final.

**Q:** What is `java.util.function.Predicate<T>`?  
**A:** A functional interface: `T → boolean`.

**Q:** What bytecode instruction enables lambda compilation?  
**A:** `invokedynamic`.

**Q:** Does a lambda create an anonymous class at compile time?  
**A:** No — it uses `invokedynamic` and `LambdaMetafactory`.

**Q:** What does `andThen` do for `Function`?  
**A:** Chains functions: `f.andThen(g)` applies `f` then `g`.

**Q:** How do you write a lambda for `Runnable`?  
**A:** `() -> System.out.println("run")`

**Q:** What is a closure?  
**A:** A function that captures variables from its enclosing scope.
