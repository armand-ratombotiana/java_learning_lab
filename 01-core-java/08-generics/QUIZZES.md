# Module 13: Generics - Quizzes

**Total Questions**: 24  
**Difficulty Levels**: Beginner (6), Intermediate (8), Advanced (6), Expert (4)  
**Time Estimate**: 90-120 minutes

---

## 🟢 Beginner Level (6 Questions)

### Q1: What Are Generics?
**Question**: Which statement best describes generics?

A) A way to create multiple classes  
B) A mechanism for type-safe collections and methods  
C) A type of exception  
D) A way to inherit from multiple classes  

**Answer**: B  
**Explanation**: Generics enable type-safe collections and methods by allowing you to specify the type of objects a collection can hold at compile time.

---

### Q2: Generic Class Syntax
**Question**: What is the correct syntax for a generic class?

A) `public class Box { }`  
B) `public class Box<T> { }`  
C) `public class Box[T] { }`  
D) `public class Box(T) { }`  

**Answer**: B  
**Explanation**: Generic classes use angle brackets `<T>` to declare type parameters. `T` is a placeholder for any type.

---

### Q3: Using Generic Classes
**Question**: How do you create an instance of a generic class?

A) `Box box = new Box();`  
B) `Box<String> box = new Box<String>();`  
C) `Box<String> box = new Box<>();`  
D) Both B and C  

**Answer**: D  
**Explanation**: Both B and C are correct. C uses the diamond operator `<>` which infers the type from the left side.

---

### Q4: Type Safety Benefit
**Question**: What is a key benefit of using generics?

A) Faster code execution  
B) Compile-time type checking  
C) Smaller code size  
D) Automatic memory management  

**Answer**: B  
**Explanation**: Generics provide compile-time type checking, catching type errors before runtime and eliminating the need for casting.

---

### Q5: Generic Methods
**Question**: What is the correct syntax for a generic method?

A) `public void method<T>(T param) { }`  
B) `public <T> void method(T param) { }`  
C) `public void <T> method(T param) { }`  
D) `public T method(T param) { }`  

**Answer**: B  
**Explanation**: Generic methods declare the type parameter before the return type: `public <T> returnType methodName(T param)`.

---

### Q6: Bounded Type Parameters
**Question**: What does `<T extends Number>` mean?

A) T can be any type  
B) T must be Number or a subclass of Number  
C) T must be a superclass of Number  
D) T must be exactly Number  

**Answer**: B  
**Explanation**: `<T extends Number>` is an upper bound that restricts T to Number and its subclasses.

---

## 🟡 Intermediate Level (8 Questions)

### Q7: Wildcard Usage
**Question**: What does `List<?>` represent?

A) A list of any type  
B) A list of Objects  
C) A list of unknown type  
D) Both A and C  

**Answer**: D  
**Explanation**: `List<?>` is an unbounded wildcard representing a list of unknown type. It's more flexible than `List<Object>`.

---

### Q8: Upper Bounded Wildcard
**Question**: What does `List<? extends Number>` allow?

A) Reading Number objects  
B) Writing Number objects  
C) Both reading and writing  
D) Neither reading nor writing  

**Answer**: A  
**Explanation**: `List<? extends Number>` is an upper bounded wildcard that allows reading but not writing (except null).

---

### Q9: Lower Bounded Wildcard
**Question**: What does `List<? super Integer>` allow?

A) Reading Integer objects  
B) Writing Integer objects  
C) Both reading and writing  
D) Neither reading nor writing  

**Answer**: B  
**Explanation**: `List<? super Integer>` is a lower bounded wildcard that allows writing Integer objects but reading as Object.

---

### Q10: Type Erasure
**Question**: What happens to generic type information at runtime?

A) It's preserved in the bytecode  
B) It's erased and replaced with Object  
C) It's converted to raw types  
D) It's stored in metadata  

**Answer**: B  
**Explanation**: Type erasure removes generic type information at runtime and replaces it with Object or the upper bound.

---

### Q11: Generic Inheritance
**Question**: Can you extend a generic class?

A) No, generics cannot be extended  
B) Yes, with or without specifying the type parameter  
C) Yes, but only with the same type parameter  
D) No, you must use composition  

**Answer**: B  
**Explanation**: You can extend a generic class either as a generic class `class Child<T> extends Parent<T>` or as a concrete class `class Child extends Parent<String>`.

---

### Q12: Multiple Type Parameters
**Question**: How many type parameters can a generic class have?

A) Only one  
B) At most two  
C) At most three  
D) Unlimited  

**Answer**: D  
**Explanation**: Generic classes can have multiple type parameters: `class Map<K, V>`, `class Triple<A, B, C>`, etc.

---

### Q13: Raw Types
**Question**: What is a raw type?

A) A type without generics  
B) A generic type used without type parameters  
C) A primitive type  
D) Both A and B  

**Answer**: B  
**Explanation**: A raw type is a generic type used without type parameters, like `List` instead of `List<String>`. It's allowed for backward compatibility but not recommended.

---

### Q14: Generic Interfaces
**Question**: Can interfaces be generic?

A) No, only classes can be generic  
B) Yes, interfaces can have type parameters  
C) Yes, but with restrictions  
D) No, interfaces don't support generics  

**Answer**: B  
**Explanation**: Interfaces can be generic just like classes: `interface Container<T> { void add(T item); }`.

---

## 🔴 Advanced Level (6 Questions)

### Q15: PECS Rule
**Question**: What does PECS stand for?

A) Producer Extends, Consumer Super  
B) Parameter Extends, Class Super  
C) Public Extends, Class Super  
D) Producer Extends, Class Subclass  

**Answer**: A  
**Explanation**: PECS (Producer Extends, Consumer Super) is a guideline for using wildcards: use `? extends` for producers (reading) and `? super` for consumers (writing).

---

### Q16: Recursive Type Bounds
**Question**: What is the purpose of `<T extends Comparable<T>>`?

A) T must be comparable to itself  
B) T must be comparable to any type  
C) T must extend Comparable  
D) T must be a Comparable class  

**Answer**: A  
**Explanation**: `<T extends Comparable<T>>` ensures that T is comparable to itself, enabling proper comparison operations.

---

### Q17: Bridge Methods
**Question**: Why does the compiler generate bridge methods?

A) To improve performance  
B) To maintain type safety with type erasure  
C) To support multiple inheritance  
D) To enable reflection  

**Answer**: B  
**Explanation**: Bridge methods are generated to maintain type safety when a generic class is extended with a concrete type parameter.

---

### Q18: Generic Array Creation
**Question**: Why can't you create a generic array like `new List<String>[10]`?

A) It's not allowed by the language  
B) Type erasure makes it unsafe  
C) Arrays need to know the exact type  
D) All of the above  

**Answer**: D  
**Explanation**: Generic arrays are not allowed because type erasure makes them unsafe. You can use `List<?>[]` instead.

---

### Q19: Covariance
**Question**: What is covariance in generics?

A) A type can be substituted for its supertype  
B) A type can be substituted for its subtype  
C) A type can be substituted for any type  
D) A type cannot be substituted  

**Answer**: B  
**Explanation**: Covariance allows a type to be substituted for its subtype. In generics, `List<? extends Number>` is covariant.

---

### Q20: Contravariance
**Question**: What is contravariance in generics?

A) A type can be substituted for its supertype  
B) A type can be substituted for its subtype  
C) A type can be substituted for any type  
D) A type cannot be substituted  

**Answer**: A  
**Explanation**: Contravariance allows a type to be substituted for its supertype. In generics, `List<? super Integer>` is contravariant.

---

## 🟣 Expert Level (4 Questions)

### Q21: Type Inference
**Question**: How does Java infer generic types in method calls?

A) From the method signature  
B) From the assignment context  
C) From the arguments passed  
D) All of the above  

**Answer**: D  
**Explanation**: Java uses multiple sources to infer generic types: method signature, assignment context, and arguments passed.

---

### Q22: Wildcard Capture
**Question**: What is wildcard capture?

A) Converting a wildcard to a type parameter  
B) Capturing the actual type of a wildcard  
C) Using a wildcard in a method  
D) Creating a new wildcard  

**Answer**: B  
**Explanation**: Wildcard capture is a technique to convert a wildcard to a type parameter, allowing more operations on the captured type.

---

### Q23: Generic Method Overloading
**Question**: Can you overload generic methods with different type parameters?

A) No, it's not allowed  
B) Yes, if the signatures are different after erasure  
C) Yes, always  
D) Only with bounded types  

**Answer**: B  
**Explanation**: You can overload generic methods if their signatures are different after type erasure.

---

### Q24: Variance in Collections
**Question**: Why is `List<String>` not a subtype of `List<Object>`?

A) Because of type erasure  
B) Because of invariance in generics  
C) Because String is not a subtype of Object  
D) Because lists are mutable  

**Answer**: B  
**Explanation**: Generics are invariant, meaning `List<String>` is not a subtype of `List<Object>` even though String is a subtype of Object. This prevents type errors.

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

**Module 13 - Generics Quizzes**  
*Test your understanding of type-safe programming*