# Module 01: Java Basics - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: Is Java "Pass-by-Value" or "Pass-by-Reference"?
**Answer**: Java is strictly **Pass-by-Value**. 
- For primitive types (int, double), a copy of the actual value is passed.
- For object references, a copy of the *reference* (memory address pointer) is passed. You can modify the object's state via this reference, but you cannot reassign the original reference to point to a completely different object.

### Q2: What is the difference between `==` and `.equals()`?
**Answer**:
- `==` is an operator that compares memory addresses (for objects) or literal values (for primitives).
- `.equals()` is a method defined in `Object` that compares the logical equivalence of two objects. It must be explicitly overridden by the class (e.g., `String` overrides it to compare character sequences).

### Q3: Explain the difference between `break` and `continue`.
**Answer**:
- `break` completely terminates the nearest enclosing loop or switch statement. Execution jumps to the statement immediately following the loop.
- `continue` skips the rest of the current iteration of a loop and immediately evaluates the loop's condition to begin the next iteration.

---

## 💻 Whiteboarding / Coding Scenarios

### Scenario 1: FizzBuzz
**Problem**: Write a program that prints numbers from 1 to 100. For multiples of 3, print "Fizz". For multiples of 5, print "Buzz". For multiples of both 3 and 5, print "FizzBuzz".

**Solution**:
```java
public void fizzBuzz() {
    for (int i = 1; i <= 100; i++) {
        if (i % 3 == 0 && i % 5 == 0) {
            System.out.println("FizzBuzz");
        } else if (i % 3 == 0) {
            System.out.println("Fizz");
        } else if (i % 5 == 0) {
            System.out.println("Buzz");
        } else {
            System.out.println(i);
        }
    }
}
```

### Scenario 2: Reverse an Array In-Place
**Problem**: Given an array of integers, reverse its elements without using a second array.

**Solution**:
```java
public void reverse(int[] arr) {
    int left = 0;
    int right = arr.length - 1;
    
    while (left < right) {
        int temp = arr[left];
        arr[left] = arr[right];
        arr[right] = temp;
        left++;
        right--;
    }
}
```