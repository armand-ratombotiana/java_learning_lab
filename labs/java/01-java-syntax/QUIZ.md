# Java Syntax — Quiz

## Question 1
What is the correct signature for the main method in Java?
- A) `public void main(String[] args)`
- B) `public static void main(String[] args)`
- C) `static void main(String[] args)`
- D) `public static int main(String[] args)`

**Answer: B**

## Question 2
Which of the following is NOT a valid Java identifier?
- A) `$value`
- B) `_count`
- C) `2ndPlace`
- D) `myVariable`

**Answer: C** (cannot start with digit)

## Question 3
What is the result of `System.out.println(10 + 5 * 2);`?
- A) 30
- B) 20
- C) 15
- D) 25

**Answer: B** (20 — multiplication before addition)

## Question 4
Which comment type is used for generating API documentation?
- A) `// comment`
- B) `/* comment */`
- C) `/** comment */`
- D) All of the above

**Answer: C** (Javadoc)

## Question 5
Which naming convention is correct for a constant in Java?
- A) `MAX_VALUE`
- B) `maxValue`
- C) `MaxValue`
- D) `max_value`

**Answer: A** (UPPER_SNAKE_CASE)

## Question 6
What happens when you compile `public class MyClass { }` in a file named `MyClass.java`?
- A) Compilation error — no main method
- B) Compilation succeeds, generates MyClass.class
- C) Compilation error — file name mismatch
- D) Compilation succeeds, generates MyClass.java.class

**Answer: B**

## Question 7
Which of the following is a valid array declaration?
- A) `int[5] arr;`
- B) `int arr[5];`
- C) `int[] arr;`
- D) `int arr = new int[5];`

**Answer: C**

## Question 8
What does `int x = 0b1100;` represent?
- A) Compilation error
- B) 12
- C) 1100
- D) Binary literal for decimal 1100

**Answer: B** (0b1100 is binary for decimal 12)

## Question 9
Which statement about Java identifiers is TRUE?
- A) Identifiers are case-insensitive
- B) `$` and `_` are allowed anywhere in an identifier
- C) Keywords can be used as identifiers
- D) Identifiers can contain spaces

**Answer: B**

## Question 10
What is the output of `System.out.println("5" + 3 + 2);`?
- A) 10
- B) 532
- C) 53
- D) 8

**Answer: B** ("5" + 3 = "53", then "53" + 2 = "532")

## Question 11
Which is NOT a Java keyword?
- A) `goto`
- B) `const`
- C) `include`
- D) `strictfp`

**Answer: C** (`include` is C/C++, not Java)

## Question 12
What is the result of `System.out.println(10 % 3);`?
- A) 3
- B) 0
- C) 1
- D) 3.33

**Answer: C** (10 / 3 = 3 with remainder 1)

## Question 13
Which of these is NOT a valid way to write an integer literal?
- A) `0xFF`
- B) `052`
- C) `0b1010`
- D) `0xG`

**Answer: D** (G is not a valid hex digit)

## Question 14
What is the correct way to declare a method that takes a variable number of int arguments?
- A) `void method(int[] args)`
- B) `void method(int... args)`
- C) `void method(int args...)`
- D) `void method(int args)`

**Answer: B** (varargs with `...`)

## Question 15
Which statement about `var` in Java is FALSE?
- A) `var` can be used for local variables
- B) `var` can be used for method parameters
- C) `var` can be used in for-each loops
- D) The type is inferred at compile time

**Answer: B** (`var` cannot be used for method parameters)

## Question 16
What does `>>>` operator do?
- A) Left shift
- B) Right shift (signed)
- C) Unsigned right shift
- D) Bitwise XOR

**Answer: C**

## Question 17
What is the correct package declaration?
- A) `package com.example;`
- B) `Package com.example;`
- C) `import com.example;`
- D) `package com/example;`

**Answer: A**

## Question 18
Which is the correct way to declare a char with Unicode value?
- A) `char c = \u0041;`
- B) `char c = '\u0041';`
- C) `char c = "\u0041";`
- D) `char c = u0041;`

**Answer: B**

## Question 19
What is the output of `System.out.println(3 + 4 + "5");`?
- A) 345
- B) 12
- C) 75
- D) 345

**Answer: C** (3 + 4 = 7, then 7 + "5" = "75")

## Question 20
Which feature was introduced in Java 14?
- A) Lambdas
- B) Switch expressions (standard)
- C) Generics
- D) Annotations

**Answer: B**
