# Interview Questions: Java Basics

## Entry Level

### Q1: Explain the difference between primitive and reference types
**A:** Primitive types store actual values (int, double, etc.) directly in memory. Reference types store references (pointers) to objects in heap memory. Primitives have default values (0, false), references have null.

### Q2: What is the difference between `==` and `.equals()`?
**A:** `==` compares object references (memory addresses). `.equals()` compares object contents. For Strings, always use `.equals()`. `==` is fine for primitives.

### Q3: Explain the Java execution flow (compilation to execution)
**A:** Source code → javac compiles to bytecode (.class) → JVM loads bytecode → either interprets or JIT compiles to machine code → executes

### Q4: What is the purpose of the `main` method?
**A:** It's the entry point of any Java application. The JVM looks for this method to start execution.

### Q5: Describe the different loops in Java
**A:** 
- for: used when number of iterations is known
- while: checks condition before each iteration
- do-while: executes at least once, checks condition after
- enhanced for: iterates over arrays/collections

### Q6: What is type casting? When do you need it?
**A:** Converting one type to another. Needed when assigning larger type to smaller (explicit cast) or converting between related types.

### Q7: What are the different access modifiers?
**A:** 
- private: class only
- default (package-private): class + package
- protected: class + package + subclasses
- public: everywhere

### Q8: What is the difference between String and StringBuilder?
**A:** String is immutable (cannot be changed). StringBuilder is mutable, better for frequent modifications.

### Q9: Explain pass-by-value in Java
**A:** Java always passes by value. For primitives, the actual value is copied. For objects, the reference value is copied (so you can modify the object's contents but not the reference itself).

### Q10: What is the output of: System.out.println('a' + 1)?
**A:** 98 (char 'a' is ASCII 97, plus 1 = 98)