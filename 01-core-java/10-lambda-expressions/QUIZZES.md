# Module 15: Lambda Expressions - Quizzes

**Total Questions**: 24  
**Difficulty Levels**: Beginner (6), Intermediate (8), Advanced (6), Expert (4)  
**Time Estimate**: 90-120 minutes

---

## 🟢 Beginner Level (6 Questions)

### Q1: What Are Lambda Expressions?
**Question**: Which statement best describes lambda expressions?

A) A type of exception  
B) Anonymous functions that implement functional interfaces  
C) A way to create new classes  
D) A type of loop  

**Answer**: B  
**Explanation**: Lambda expressions are anonymous functions that provide a concise way to implement functional interfaces.

---

### Q2: Lambda Syntax
**Question**: What is the correct syntax for a simple lambda?

A) `x => x * 2`  
B) `x -> x * 2`  
C) `x: x * 2`  
D) `x | x * 2`  

**Answer**: B  
**Explanation**: Lambda expressions use the arrow operator `->` to separate parameters from the body.

---

### Q3: Functional Interface
**Question**: What is a functional interface?

A) An interface with multiple abstract methods  
B) An interface with exactly one abstract method  
C) An interface that cannot be implemented  
D) An interface with only default methods  

**Answer**: B  
**Explanation**: A functional interface has exactly one abstract method and can be implemented with a lambda expression.

---

### Q4: Lambda Parameters
**Question**: How do you write a lambda with multiple parameters?

A) `x, y -> x + y`  
B) `(x, y) -> x + y`  
C) `x y -> x + y`  
D) `[x, y] -> x + y`  

**Answer**: B  
**Explanation**: Multiple parameters must be enclosed in parentheses: `(x, y) -> x + y`.

---

### Q5: Lambda with No Parameters
**Question**: How do you write a lambda with no parameters?

A) `-> System.out.println("Hello")`  
B) `() -> System.out.println("Hello")`  
C) `(void) -> System.out.println("Hello")`  
D) `null -> System.out.println("Hello")`  

**Answer**: B  
**Explanation**: No parameters are represented by empty parentheses: `() -> ...`.

---

### Q6: Predicate Functional Interface
**Question**: What does Predicate<T> do?

A) Transforms input to output  
B) Accepts input and returns nothing  
C) Returns a value without input  
D) Tests input and returns boolean  

**Answer**: D  
**Explanation**: Predicate<T> tests a condition and returns a boolean value.

---

## 🟡 Intermediate Level (8 Questions)

### Q7: Function Functional Interface
**Question**: What does Function<T, R> do?

A) Tests input and returns boolean  
B) Accepts input and returns nothing  
C) Transforms input of type T to output of type R  
D) Returns a value without input  

**Answer**: C  
**Explanation**: Function<T, R> transforms input of type T to output of type R.

---

### Q8: Consumer Functional Interface
**Question**: What does Consumer<T> do?

A) Transforms input to output  
B) Accepts input and returns nothing  
C) Returns a value without input  
D) Tests input and returns boolean  

**Answer**: B  
**Explanation**: Consumer<T> accepts input and performs an action without returning a value.

---

### Q9: Supplier Functional Interface
**Question**: What does Supplier<T> do?

A) Transforms input to output  
B) Accepts input and returns nothing  
C) Returns a value without accepting input  
D) Tests input and returns boolean  

**Answer**: C  
**Explanation**: Supplier<T> returns a value without accepting any input.

---

### Q10: Method References
**Question**: What is a method reference?

A) A reference to a method in documentation  
B) A way to call a method  
C) A shorthand for lambda expressions  
D) A type of annotation  

**Answer**: C  
**Explanation**: Method references provide a shorthand syntax for lambda expressions that call existing methods.

---

### Q11: Static Method Reference
**Question**: How do you reference a static method?

A) `ClassName.methodName`  
B) `ClassName::methodName`  
C) `ClassName->methodName`  
D) `ClassName#methodName`  

**Answer**: B  
**Explanation**: Static method references use the `::` operator: `ClassName::methodName`.

---

### Q12: Constructor Reference
**Question**: How do you reference a constructor?

A) `ClassName.new`  
B) `ClassName::new`  
C) `new ClassName`  
D) `ClassName->new`  

**Answer**: B  
**Explanation**: Constructor references use the `::` operator: `ClassName::new`.

---

### Q13: Variable Capture
**Question**: What is variable capture in lambdas?

A) Storing variables in memory  
B) Lambdas accessing variables from enclosing scope  
C) Creating new variables in lambdas  
D) Deleting variables after use  

**Answer**: B  
**Explanation**: Variable capture is when a lambda accesses variables from its enclosing scope.

---

### Q14: Effectively Final
**Question**: What does "effectively final" mean?

A) A variable declared with final keyword  
B) A variable that is not modified after initialization  
C) A variable that cannot be accessed  
D) A variable that is immutable  

**Answer**: B  
**Explanation**: Effectively final means a variable is not modified after initialization, even without the final keyword.

---

## 🔴 Advanced Level (6 Questions)

### Q15: Function Composition
**Question**: What does `andThen()` do in Function?

A) Applies the first function, then the second  
B) Applies the second function, then the first  
C) Combines two functions into one  
D) Executes functions in parallel  

**Answer**: A  
**Explanation**: `andThen()` applies the first function, then applies the second function to the result.

---

### Q16: Predicate Composition
**Question**: What does `and()` do in Predicate?

A) Combines two predicates with OR logic  
B) Combines two predicates with AND logic  
C) Negates a predicate  
D) Applies predicates in sequence  

**Answer**: B  
**Explanation**: `and()` combines two predicates with AND logic, both must be true.

---

### Q17: Stream Filter
**Question**: What does `filter()` do in streams?

A) Transforms elements  
B) Keeps elements that match a predicate  
C) Removes duplicate elements  
D) Sorts elements  

**Answer**: B  
**Explanation**: `filter()` keeps only elements that match the given predicate.

---

### Q18: Stream Map
**Question**: What does `map()` do in streams?

A) Filters elements  
B) Transforms each element using a function  
C) Combines elements  
D) Sorts elements  

**Answer**: B  
**Explanation**: `map()` transforms each element using the provided function.

---

### Q19: Stream FlatMap
**Question**: What does `flatMap()` do in streams?

A) Filters elements  
B) Transforms and flattens nested structures  
C) Sorts elements  
D) Removes duplicates  

**Answer**: B  
**Explanation**: `flatMap()` transforms elements and flattens the result, useful for nested collections.

---

### Q20: Stream Reduce
**Question**: What does `reduce()` do in streams?

A) Filters elements  
B) Transforms elements  
C) Combines elements into a single value  
D) Sorts elements  

**Answer**: C  
**Explanation**: `reduce()` combines all elements into a single value using an accumulator function.

---

## 🟣 Expert Level (4 Questions)

### Q21: Currying
**Question**: What is currying in functional programming?

A) Converting a multi-parameter function to nested single-parameter functions  
B) Combining multiple functions  
C) Applying functions in sequence  
D) Creating anonymous functions  

**Answer**: A  
**Explanation**: Currying converts a function with multiple parameters into a series of functions with single parameters.

---

### Q22: Side Effects in Lambdas
**Question**: Why should lambdas avoid side effects?

A) Side effects are not allowed  
B) Side effects make code harder to reason about and test  
C) Side effects improve performance  
D) Side effects are required for lambdas  

**Answer**: B  
**Explanation**: Side effects make code harder to understand, test, and parallelize. Pure functions are preferred.

---

### Q23: Lazy Evaluation
**Question**: What is lazy evaluation?

A) Evaluating expressions immediately  
B) Deferring computation until the result is needed  
C) Evaluating expressions in parallel  
D) Evaluating expressions multiple times  

**Answer**: B  
**Explanation**: Lazy evaluation defers computation until the result is actually needed, improving efficiency.

---

### Q24: Functional Interface Compatibility
**Question**: Can a lambda be assigned to different functional interfaces?

A) No, each lambda is tied to one interface  
B) Yes, if the lambda signature matches the interface  
C) Yes, always  
D) No, lambdas cannot be assigned to interfaces  

**Answer**: B  
**Explanation**: A lambda can be assigned to any functional interface with a matching signature.

---

## 📊 Quiz Statistics

| Difficulty | Count | Percentage |
|-----------|-------|-----------|
| Beginner | 6 | 25% |
| Intermediate | 8 | 33% |
| Advanced | 6 | 25% |
| Expert | 4 | 17% |

---

## 🎯 Scoring Guide

- **22-24 correct**: Expert level mastery
- **18-21 correct**: Advanced understanding
- **14-17 correct**: Intermediate proficiency
- **10-13 correct**: Beginner foundation
- **Below 10**: Review recommended

---

**Module 15 - Lambda Expressions Quizzes**  
*Test your understanding of functional programming*