# Module 14: Annotations - Quizzes

**Total Questions**: 24  
**Difficulty Levels**: Beginner (6), Intermediate (8), Advanced (6), Expert (4)  
**Time Estimate**: 90-120 minutes

---

## 🟢 Beginner Level (6 Questions)

### Q1: What Are Annotations?
**Question**: Which statement best describes annotations?

A) A way to create new classes  
B) Metadata that provides information about code  
C) A type of exception  
D) A way to inherit from multiple classes  

**Answer**: B  
**Explanation**: Annotations are a form of metadata that provide information about the code but are not part of the code itself.

---

### Q2: @Override Annotation
**Question**: What is the purpose of @Override?

A) To override a method's behavior  
B) To indicate that a method overrides a superclass method  
C) To prevent method overriding  
D) To create a new method  

**Answer**: B  
**Explanation**: @Override indicates that a method overrides a superclass method and helps catch errors like typos in method names.

---

### Q3: @Deprecated Annotation
**Question**: What does @Deprecated indicate?

A) The code is broken  
B) The code should not be used  
C) The code is optimized  
D) The code is new  

**Answer**: B  
**Explanation**: @Deprecated marks code as outdated and indicates that it should not be used in new code.

---

### Q4: Annotation Syntax
**Question**: What is the correct syntax for using an annotation?

A) `#Override`  
B) `@Override`  
C) `<Override>`  
D) `[Override]`  

**Answer**: B  
**Explanation**: Annotations use the @ symbol prefix, e.g., `@Override`, `@Deprecated`.

---

### Q5: @SuppressWarnings
**Question**: What does @SuppressWarnings do?

A) Removes all warnings  
B) Tells the compiler to suppress specific warnings  
C) Prevents code from running  
D) Creates new warnings  

**Answer**: B  
**Explanation**: @SuppressWarnings tells the compiler to suppress specific warnings, like unchecked casts.

---

### Q6: @FunctionalInterface
**Question**: What does @FunctionalInterface indicate?

A) An interface with multiple methods  
B) An interface with a single abstract method  
C) An interface that cannot be implemented  
D) An interface with default methods  

**Answer**: B  
**Explanation**: @FunctionalInterface marks an interface as a functional interface with exactly one abstract method.

---

## 🟡 Intermediate Level (8 Questions)

### Q7: @Retention Meta-Annotation
**Question**: What does @Retention control?

A) Where an annotation can be applied  
B) How long an annotation is available  
C) The name of an annotation  
D) The parameters of an annotation  

**Answer**: B  
**Explanation**: @Retention controls when an annotation is available: SOURCE, CLASS, or RUNTIME.

---

### Q8: RetentionPolicy.RUNTIME
**Question**: What does RetentionPolicy.RUNTIME mean?

A) Annotation is available only during compilation  
B) Annotation is available in bytecode only  
C) Annotation is available at runtime via reflection  
D) Annotation is removed after compilation  

**Answer**: C  
**Explanation**: RetentionPolicy.RUNTIME means the annotation is available at runtime and can be accessed via reflection.

---

### Q9: @Target Meta-Annotation
**Question**: What does @Target control?

A) The retention policy of an annotation  
B) Where an annotation can be applied  
C) The parameters of an annotation  
D) The inheritance of an annotation  

**Answer**: B  
**Explanation**: @Target specifies where an annotation can be applied: TYPE, METHOD, FIELD, PARAMETER, etc.

---

### Q10: Custom Annotations
**Question**: How do you create a custom annotation?

A) `public class MyAnnotation { }`  
B) `public interface MyAnnotation { }`  
C) `public @interface MyAnnotation { }`  
D) `public annotation MyAnnotation { }`  

**Answer**: C  
**Explanation**: Custom annotations are created using the `@interface` keyword.

---

### Q11: Annotation Elements
**Question**: What are annotation elements?

A) Classes that use annotations  
B) Methods that process annotations  
C) Parameters of an annotation  
D) Types of annotations  

**Answer**: C  
**Explanation**: Annotation elements are parameters that can be specified when using an annotation.

---

### Q12: Default Values in Annotations
**Question**: Can annotation elements have default values?

A) No, all elements are required  
B) Yes, using the default keyword  
C) Yes, but only for String elements  
D) No, defaults are not allowed  

**Answer**: B  
**Explanation**: Annotation elements can have default values using the `default` keyword.

---

### Q13: @Inherited Meta-Annotation
**Question**: What does @Inherited do?

A) Allows annotations to be inherited by subclasses  
B) Requires annotations to be inherited  
C) Prevents annotation inheritance  
D) Marks an annotation as abstract  

**Answer**: A  
**Explanation**: @Inherited allows annotations to be inherited by subclasses of an annotated class.

---

### Q14: Annotation Processing
**Question**: How do you access annotations at runtime?

A) Using the @Retention annotation  
B) Using reflection and getAnnotation()  
C) Using the @Target annotation  
D) Using the @Documented annotation  

**Answer**: B  
**Explanation**: Annotations are accessed at runtime using reflection methods like getAnnotation() and isAnnotationPresent().

---

## 🔴 Advanced Level (6 Questions)

### Q15: @Repeatable Meta-Annotation
**Question**: What does @Repeatable allow?

A) Using the same annotation multiple times  
B) Repeating method calls  
C) Repeating class definitions  
D) Repeating variable declarations  

**Answer**: A  
**Explanation**: @Repeatable allows the same annotation to be applied multiple times to the same element.

---

### Q16: ElementType.TYPE
**Question**: What does ElementType.TYPE allow?

A) Annotation on methods only  
B) Annotation on fields only  
C) Annotation on classes, interfaces, enums, and annotations  
D) Annotation on parameters only  

**Answer**: C  
**Explanation**: ElementType.TYPE allows annotations on classes, interfaces, enums, and annotation types.

---

### Q17: Annotation Composition
**Question**: Can you apply multiple annotations to the same element?

A) No, only one annotation per element  
B) Yes, multiple annotations can be applied  
C) Yes, but only if they have the same retention policy  
D) No, annotations cannot be combined  

**Answer**: B  
**Explanation**: Multiple annotations can be applied to the same element.

---

### Q18: @Documented Meta-Annotation
**Question**: What does @Documented do?

A) Requires documentation for the annotation  
B) Includes the annotation in Javadoc  
C) Prevents documentation of the annotation  
D) Marks the annotation as documented  

**Answer**: B  
**Explanation**: @Documented indicates that the annotation should be included in Javadoc documentation.

---

### Q19: Reflection with Annotations
**Question**: Which method checks if an annotation is present?

A) hasAnnotation()  
B) getAnnotation()  
C) isAnnotationPresent()  
D) checkAnnotation()  

**Answer**: C  
**Explanation**: isAnnotationPresent() checks if a specific annotation is present on a class, method, or field.

---

### Q20: Annotation Element Types
**Question**: What types can annotation elements have?

A) Only String and int  
B) Primitives, String, Class, enums, and arrays  
C) Any type  
D) Only primitives  

**Answer**: B  
**Explanation**: Annotation elements can be primitives, String, Class, enums, and arrays of these types.

---

## 🟣 Expert Level (4 Questions)

### Q21: Type Annotations
**Question**: What are type annotations?

A) Annotations on type declarations  
B) Annotations on method return types  
C) Annotations on any use of a type  
D) Annotations on class definitions  

**Answer**: C  
**Explanation**: Type annotations (ElementType.TYPE_USE) can be applied to any use of a type, including generics.

---

### Q22: Annotation Inheritance Behavior
**Question**: If a parent class has @Inherited annotation, what happens to child classes?

A) Child classes automatically have the annotation  
B) Child classes must explicitly apply the annotation  
C) Child classes cannot have the annotation  
D) The annotation is removed from child classes  

**Answer**: A  
**Explanation**: If an annotation is marked with @Inherited, child classes automatically inherit it from the parent class.

---

### Q23: Retention Policy and Reflection
**Question**: Can you access an annotation at runtime if it has RetentionPolicy.CLASS?

A) Yes, always  
B) No, it's only in bytecode  
C) Yes, but only with special tools  
D) No, it's removed after compilation  

**Answer**: B  
**Explanation**: RetentionPolicy.CLASS means the annotation is in bytecode but not available at runtime via reflection.

---

### Q24: Custom Annotation Processing
**Question**: What is the primary use of custom annotations?

A) To replace method calls  
B) To provide metadata for frameworks and tools  
C) To improve code performance  
D) To replace comments  

**Answer**: B  
**Explanation**: Custom annotations are primarily used to provide metadata that frameworks and tools can process.

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

**Module 14 - Annotations Quizzes**  
*Test your understanding of metadata programming*