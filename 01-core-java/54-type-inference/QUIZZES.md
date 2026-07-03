# Quizzes: Type Inference

Test your knowledge of the `var` keyword, target typing, and poly expressions.

## Quiz 1: The `var` Keyword

**Q1: Which of the following is a valid use of the `var` keyword in Java?**
- A) `var name;`
- B) `public var getName() { return "Alice"; }`
- C) `var list = new ArrayList<String>();`
- D) `private var count = 0;`
*Answer: C (`var` can only be used for local variables with an initializer. It cannot be used without an initializer, for return types, or for class fields).*

**Q2: Is Java dynamically typed when you use `var`?**
- A) Yes, `var x = 10; x = "Hello";` is valid in Java 10+.
- B) No. `var` simply tells the compiler to figure out the static type at compile time based on the right-hand side. Once inferred, the type is locked in forever.
- C) Only if you use reflection.
- D) Yes, but only for primitive types.
*Answer: B*

## Quiz 2: Target Typing and Poly Expressions

**Q1: Why does `var myLambda = (String s) -> s.length();` fail to compile?**
- A) Because lambdas cannot be assigned to variables.
- B) Because `var` only works with objects created using the `new` keyword.
- C) Because lambdas are "poly expressions". The compiler needs a target type (like `Function<String, Integer>`) on the left side to know what functional interface the lambda is supposed to implement. `var` provides no target type.
- D) Because the parameter `s` must also be declared with `var`.
*Answer: C*

**Q2: If you write `var map = new HashMap<>();`, what is the inferred type of `map`?**
- A) `HashMap<String, String>`
- B) `Map<Object, Object>`
- C) `HashMap<Object, Object>`
- D) A compile-time error occurs.
*Answer: C (Because the diamond operator `<>` is empty, the compiler has no information to infer the generic types, so it falls back to `Object`. It infers the concrete class `HashMap`, not the interface `Map`).*

## Quiz 3: Best Practices

**Q1: According to Java guidelines, when is the best time to use `var`?**
- A) Everywhere possible to reduce typing.
- B) Only when the type is extremely obvious from the right-hand side, reducing visual clutter without sacrificing readability (e.g., `var stream = new FileInputStream(...)`).
- C) When you don't know what type a method returns.
- D) Only in `for` loops.
*Answer: B*