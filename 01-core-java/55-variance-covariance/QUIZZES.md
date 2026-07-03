# Quizzes: Variance & Covariance

Test your knowledge of Generics, Arrays, and Wildcards.

## Quiz 1: Invariance vs Covariance

**Q1: Why does `List<String> strings = new ArrayList<>(); List<Object> objects = strings;` result in a compile-time error?**
- A) Because `String` does not extend `Object`.
- B) Because Java Generics are **invariant**. The compiler prevents this assignment because if it were allowed, you could add an `Integer` to the `objects` list, which would corrupt the underlying `String` list and cause a `ClassCastException` later.
- C) Because `ArrayList` is not a `List`.
- D) Because you must use the `var` keyword.
*Answer: B*

**Q2: What happens if you do the exact same thing with Java Arrays: `String[] strings = new String[10]; Object[] objects = strings; objects[0] = 5;`?**
- A) Compile-time error.
- B) It compiles and runs perfectly.
- C) It compiles, but throws an `ArrayStoreException` at runtime when you try to insert the Integer 5 into the String array.
- D) The array automatically resizes.
*Answer: C (Arrays are covariant, which sacrifices compile-time safety for runtime checks).*

## Quiz 2: Wildcards

**Q1: You have a method `public void process(List<? super Integer> list)`. Which of the following can you safely pass into this method?**
- A) `List<Double>`
- B) `List<String>`
- C) `List<Number>` or `List<Object>` or `List<Integer>`
- D) Only `List<Integer>`
*Answer: C (Contravariance: The list must be of type Integer or a superclass of Integer).*

**Q2: In the method from Q1 (`List<? super Integer>`), which of the following operations is valid inside the method body?**
- A) `list.add(new Integer(5));`
- B) `list.add(new Object());`
- C) `Integer x = list.get(0);`
- D) `list.add(new Double(5.0));`
*Answer: A (You can write an Integer into the list because it is guaranteed to hold at least Integers. You cannot write an Object, because the list might actually be a `List<Integer>`. You cannot read an Integer, because the list might be a `List<Object>`).*

## Quiz 3: Edge Cases

**Q1: Why can't you overload these two methods: `void print(List<String> list)` and `void print(List<Integer> list)`?**
- A) Because `String` and `Integer` are final classes.
- B) Because of Type Erasure. At runtime, both signatures erase to `void print(List list)`. The compiler forbids this because it cannot distinguish between them.
- C) Because `print` is a reserved keyword.
- D) Because the lists are not bounded.
*Answer: B*