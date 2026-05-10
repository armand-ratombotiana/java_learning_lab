# Java Basics - Quizzes

## Quiz 1: Variables and Data Types

### Question 1.1
What is the default value of an `int` in Java?

A) null  
B) 0  
C) undefined  
D) -1  

**Answer: B**

---

### Question 1.2
Which of the following is NOT a primitive type in Java?

A) byte  
B) int  
C) String  
D) boolean  

**Answer: C** (String is a reference type)

---

### Question 1.3
What is the output of the following code?

```java
int x = 5;
int y = 2;
System.out.println(x / y);
```

A) 2.5  
B) 2  
C) 2.0  
D) 3  

**Answer: B** (Integer division truncates)

---

### Question 1.4
Which type conversion requires an explicit cast?

A) int to long  
B) double to int  
C) char to int  
D) byte to short  

**Answer: B** (Narrowing conversion requires cast)

---

### Question 1.5
What is the size of a `double` in Java?

A) 4 bytes  
B) 8 bytes  
C) 16 bytes  
D) 2 bytes  

**Answer: B**

---

## Quiz 2: Operators

### Question 2.1
What is the result of `5 % 2`?

A) 2  
B) 2.5  
C) 1  
D) 0  

**Answer: C** (Modulo returns remainder)

---

### Question 2.2
What is the output?

```java
int x = 3;
System.out.println(x++ + ++x);
```

A) 7  
B) 8  
C) 9  
D) 6  

**Answer: C**
- x++ returns 3, x becomes 4
- ++x returns 5, x becomes 5
- 3 + 5 = 8... Wait, let me recalculate
- Initial: x = 3
- x++: returns 3, x = 4
- ++x: x = 5, returns 5
- 3 + 5 = 8

**Answer: B**

---

### Question 2.3
Which operator has the highest precedence?

A) +  
B) *  
C) =  
D) ==  

**Answer: B** (* has higher precedence than +)

---

### Question 2.4
What is the result of `true && false || true`?

A) true  
B) false  
C) Compilation error  
D) undefined  

**Answer: A** (&& has higher precedence: true && false = false, then false || true = true)

---

### Question 2.5
What does the XOR operator (^) return for `true ^ false`?

A) true  
B) false  
C) Compilation error  
D) null  

**Answer: A** (XOR returns true when operands differ)

---

## Quiz 3: Control Flow

### Question 3.1
How many times does this loop execute?

```java
for (int i = 0; i < 5; i++) {
    System.out.println(i);
}
```

A) 4  
B) 5  
C) 6  
D) Infinite  

**Answer: B** (0, 1, 2, 3, 4 = 5 iterations)

---

### Question 3.2
What is the output?

```java
int i = 0;
while (i < 3) {
    if (i == 1) {
        i++;
        continue;
    }
    System.out.println(i);
    i++;
}
```

A) 0, 1, 2  
B) 0, 2  
C) 0  
D) 1, 2  

**Answer: B** (0 is printed, 1 is skipped, 2 is printed)

---

### Question 3.3
Which statement correctly exits both nested loops?

```java
outer:
for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        if (i == 1 && j == 1) {
            // exit both loops
        }
    }
}
```

A) break;  
B) break outer;  
C) exit;  
D) return;  

**Answer: B**

---

### Question 3.4
What is the output of this switch?

```java
int x = 2;
switch (x) {
    case 1: System.out.print("One ");
    case 2: System.out.print("Two ");
    case 3: System.out.print("Three ");
    default: System.out.print("Default ");
}
```

A) Two  
B) Two Three Default  
C) Two Default  
D) One Two Three Default  

**Answer: B** (Fall-through without break)

---

### Question 3.5
Which loop guarantees at least one execution?

A) for loop  
B) while loop  
C) do-while loop  
D) Enhanced for loop  

**Answer: C**

---

## Quiz 4: Methods

### Question 4.1
Which of the following is a valid method signature?

A) public void method() {}  
B) public void method(int) {}  
C) void method() {}  
D) All of the above  

**Answer: D** (All are valid)

---

### Question 4.2
What is varargs syntax?

A) int... args  
B) int[] args  
C) int args...  
D) var args  

**Answer: A**

---

### Question 4.3
How many times is the recursive method called for factorial(5)?

```java
int factorial(int n) {
    if (n <= 1) return 1;
    return n * factorial(n - 1);
}
```

A) 4  
B) 5  
C) 6  
D) 7  

**Answer: C** (5, 4, 3, 2, 1, base case = 6 calls)

---

### Question 4.4
What is method overloading?

A) Calling same method from different classes  
B) Having multiple methods with same name but different parameters  
C) Overriding a parent class method  
D) Calling a method recursively  

**Answer: B**

---

### Question 4.5
Can methods have the same name but different return types?

A) Yes, always  
B) No  
C) Yes, if parameters are different  
D) Only in static methods  

**Answer: B** (Method overloading is based on parameters, not return type)

---

## Quiz 5: Arrays

### Question 5.1
What is the length of this array?

```java
int[][] arr = new int[3][4];
```

A) 3  
B) 4  
C) 7  
D) 12  

**Answer: A** (First dimension's length)

---

### Question 5.2
What is the output?

```java
int[] arr = {1, 2, 3};
System.out.println(arr[3]);
```

A) 0  
B) null  
C) ArrayIndexOutOfBoundsException  
D) 3  

**Answer: C**

---

### Question 5.3
Which is true about arrays in Java?

A) Arrays can hold primitive and reference types  
B) Arrays have fixed size  
C) Arrays are zero-indexed  
D) All of the above  

**Answer: D**

---

### Question 5.4
How do you create a 2D array with 3 rows and 2 columns?

A) int[][] arr = new int[2][3];  
B) int[][] arr = new int[3][2];  
C) int[][] arr = new int[2,3];  
D) int[][] arr = {3, 2};  

**Answer: B**

---

### Question 5.5
What is the output?

```java
int[] a = {1, 2, 3};
int[] b = a;
b[0] = 10;
System.out.println(a[0]);
```

A) 1  
B) 10  
C) Compilation error  
D) null  

**Answer: B** (Arrays are objects; b is reference to same array)

---

## Quiz 6: Basic OOP

### Question 6.1
Which access modifier makes a member accessible only within the same package?

A) public  
B) private  
C) protected  
D) default (no modifier)  

**Answer: D**

---

### Question 6.2
What is the output?

```java
public class Test {
    static int count = 0;
    
    public Test() {
        count++;
    }
    
    public static void main(String[] args) {
        new Test();
        new Test();
        new Test();
        System.out.println(count);
    }
}
```

A) 1  
B) 2  
C) 3  
D) 0  

**Answer: C**

---

### Question 6.3
What is a constructor?

A) A method that creates objects  
B) A special method called when object is created  
C) A method that destroys objects  
D) A static method  

**Answer: B**

---

### Question 6.4
Can a class have multiple constructors?

A) No  
B) Yes, with different parameter lists  
C) Yes, but only one can be public  
D) Only if they are static  

**Answer: B** (Constructor overloading)

---

### Question 6.5
What is the `this` keyword?

A) Reference to the current object  
B) Reference to the superclass  
C) Reference to static members  
D) A variable name  

**Answer: A**

---

## Coding Challenges

### Challenge 1: Predict the Output
```java
public class Challenge {
    public static void main(String[] args) {
        int x = 5;
        int y = x++;
        int z = ++x;
        System.out.println(y + ", " + z);
    }
}
```

**Answer: 5, 7** (y = 5 (x becomes 6), z = 7 (x becomes 7))

---

### Challenge 2: Fix the Code
```java
public class FixMe {
    public static void main(String[] args) {
        String s = "Hello";
        s.append(" World");
        System.out.println(s);
    }
}
```

**Fix:** Use StringBuilder or concatenation:
```java
String s = "Hello" + " World";
// or
StringBuilder sb = new StringBuilder("Hello");
sb.append(" World");
System.out.println(sb.toString());
```

---

### Challenge 3: Trace the Recursion
```java
public static int mystery(int n) {
    if (n <= 1) return 1;
    return n * mystery(n - 1);
}
// What is mystery(5)?
```

**Answer: 120** (5! = 5 × 4 × 3 × 2 × 1 = 120)

---

### Challenge 4: Array Analysis
```java
int[] arr = {5, 2, 8, 1, 9, 3};
// What is arr[arr.length - 2]?
```

**Answer: 9** (arr[4] = 9)

---

### Challenge 5: Object Behavior
```java
public class Counter {
    int count = 0;
    
    void increment() { count++; }
    int getCount() { return count; }
}

public static void main(String[] args) {
    Counter c1 = new Counter();
    Counter c2 = c1;
    c1.increment();
    c2.increment();
    c2.increment();
    System.out.println(c1.getCount());
}
```

**Answer: 3** (c1 and c2 reference the same object)

---

## Score Calculation

| Score | Rating |
|-------|--------|
| 25-30 | Expert |
| 20-24 | Proficient |
| 15-19 | Competent |
| 10-14 | Developing |
| 0-9 | Needs Improvement |

---

## Answer Key

| Quiz | Answers |
|------|---------|
| Quiz 1 | 1. B, 2. C, 3. B, 4. B, 5. B |
| Quiz 2 | 1. C, 2. B, 3. B, 4. A, 5. A |
| Quiz 3 | 1. B, 2. B, 3. B, 4. B, 5. C |
| Quiz 4 | 1. D, 2. A, 3. C, 4. B, 5. B |
| Quiz 5 | 1. A, 2. C, 3. D, 4. B, 5. B |
| Quiz 6 | 1. D, 2. C, 3. B, 4. B, 5. A |
