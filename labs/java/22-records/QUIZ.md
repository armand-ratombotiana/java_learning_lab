# Quiz: Records

## Question 1
Which keyword is used to declare a record in Java?

A) `class`
B) `struct`
C) `record`
D) `data`

## Question 2
What is the superclass of all record types in Java?

A) `java.lang.Object`
B) `java.lang.Record`
C) `java.io.Serializable`
D) `java.lang.Enum`

## Question 3
Which of the following is NOT automatically generated for a record?

A) Canonical constructor
B) Setter methods for components
C) `equals()` method
D) `toString()` method

## Question 4
What is a compact constructor?

A) A constructor with minimal parameters
B) A constructor that omits the parameter list and allows validation before implicit field assignment
C) A constructor that takes varargs
D) A private constructor only used for serialization

## Question 5
Can a record extend another class?

A) Yes, any class
B) Yes, but only abstract classes
C) Yes, but only java.lang.Record
D) No, records cannot extend any class (they implicitly extend Record)

## Question 6
Which annotation is used to preserve a record's serialVersionUID?

A) `@Serial`
B) `@SerialVersionUID`
C) `@SerialField`
D) `@RecordComponent`

## Question 7
What is a local record?

A) A record declared inside a method
B) A record that can only be accessed from the same package
C) A record with private access modifier
D) A record with no components

## Question 8
How does deserialization create a record instance?

A) By using `Unsafe.allocateInstance()`
B) By calling the no-arg constructor
C) By invoking the canonical constructor with component values
D) By using `clone()` on a template instance

## Question 9
What happens if you try to declare an instance field in a record body?

A) It works fine
B) It causes a compilation error
C) It becomes a record component
D) It's allowed but ignored

## Question 10
Can a record implement multiple interfaces?

A) No
B) Yes, like regular classes
C) Yes, but only if the interfaces have default methods
D) Yes, but only marker interfaces

## Answer Key
1. C, 2. B, 3. B, 4. B, 5. D, 6. A, 7. A, 8. C, 9. B, 10. B
