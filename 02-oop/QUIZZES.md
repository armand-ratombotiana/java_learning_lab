# OOP - Quizzes

## Quiz 1: Encapsulation

### Question 1.1
What is the purpose of encapsulation?

A) To make all fields public  
B) To hide implementation details and expose only necessary interface  
C) To create multiple instances  
D) To speed up code execution  

**Answer: B**

---

### Question 1.2
Which access modifier makes a member visible only within its own class?

A) public  
B) protected  
C) private  
D) default  

**Answer: C**

---

### Question 1.3
What is a defensive copy?

A) Copying code for backup  
B) Creating copies of mutable objects to prevent external modification  
C) Encrypting data  
D) Making objects immutable  

**Answer: B**

---

### Question 1.4
What is true about immutable objects?

A) They can be modified after creation  
B) Their fields can only be set via constructor  
C) They don't have getters  
D) They extend the Object class  

**Answer: B**

---

### Question 1.5
What is the purpose of getters and setters?

A) To bypass encapsulation  
B) To provide controlled access to private fields  
C) To make code run faster  
D) To create new instances  

**Answer: B**

---

## Quiz 2: Inheritance

### Question 2.1
What keyword is used to create a subclass?

A) this  
B) super  
C) extends  
D) implements  

**Answer: C**

---

### Question 2.2
Which is true about the super keyword?

A) It refers to the current object  
B) It calls the parent class constructor or methods  
C) It's only used in static methods  
D) It's optional in all cases  

**Answer: B**

---

### Question 2.3
What happens if a subclass constructor doesn't explicitly call super()?

A) Compilation error  
B) Parent constructor with no args is called automatically  
C) Object is not created  
D) null is assigned to parent  

**Answer: B**

---

### Question 2.4
Can a class extend multiple classes in Java?

A) Yes, always  
B) Yes, with interfaces  
C) No, only single inheritance  
D) Only in special cases  

**Answer: C**

---

### Question 2.5
What is the Object class?

A) A user-defined class  
B) The root class of all Java classes  
C) An interface  
D) A primitive type  

**Answer: B**

---

## Quiz 3: Polymorphism

### Question 3.1
What type of polymorphism is method overriding?

A) Compile-time  
B) Runtime  
C) Both  
D) Neither  

**Answer: B**

---

### Question 3.2
What is required for runtime polymorphism?

A) Static methods  
B) Inheritance and method overriding  
C) Multiple constructors  
D) Final classes  

**Answer: B**

---

### Question 3.3
What does `instanceof` check?

A) If a class implements an interface  
B) If an object is an instance of a class or its subclass  
C) If a method is overridden  
D) If a variable is initialized  

**Answer: B**

---

### Question 3.4
What is covariant return type?

A) Returning multiple values  
B) Overriding a method to return a more specific type  
C) Returning the same type  
D) Returning null  

**Answer: B**

---

### Question 3.5
Which is true about method overloading?

A) Same name, different parameters  
B) Same name, same parameters  
C) Different name, same parameters  
D) Different name, different parameters  

**Answer: A**

---

## Quiz 4: Abstraction

### Question 4.1
What is the difference between abstract class and interface?

A) Abstract class can have concrete methods, interface cannot  
B) Interface can have state, abstract class cannot  
C) No difference  
D) Abstract class can implement multiple interfaces  

**Answer: A**

---

### Question 4.2
Can an abstract class have constructors?

A) No, never  
B) Yes, but they can't be called directly  
C) Only default constructor  
D) Only private constructors  

**Answer: B**

---

### Question 4.3
What is a functional interface?

A) An interface with all static methods  
B) An interface with exactly one abstract method  
C) An interface used by functions  
D) An abstract interface  

**Answer: B**

---

### Question 4.4
Can interfaces have default methods?

A) No, only abstract methods  
B) Yes, from Java 8  
C) Yes, but only one per interface  
D) Only in abstract classes  

**Answer: B**

---

### Question 4.5
What happens if you don't implement all abstract methods of an interface?

A) Compilation error  
B) Runtime error  
C) The class becomes abstract  
D) Nothing, it's optional  

**Answer: A** (or the class must be abstract)

---

## Quiz 5: Design Patterns

### Question 5.1
Which pattern ensures only one instance of a class?

A) Factory  
B) Strategy  
C) Singleton  
D) Observer  

**Answer: C**

---

### Question 5.2
Which pattern defines an interface for creating objects?

A) Singleton  
B) Factory  
C) Observer  
D) Strategy  

**Answer: B**

---

### Question 5.3
Which pattern defines a one-to-many dependency?

A) Factory  
B) Observer  
C) Singleton  
D) Adapter  

**Answer: B**

---

### Question 5.4
Which pattern lets you vary behavior without changing the class?

A) Singleton  
B) Observer  
C) Strategy  
D) Decorator  

**Answer: C**

---

### Question 5.5
Which pattern builds complex objects step by step?

A) Builder  
B) Factory  
C) Prototype  
D) Composite  

**Answer: A**

---

## Quiz 6: SOLID Principles

### Question 6.1
Which SOLID principle states that classes should be open for extension but closed for modification?

A) SRP  
B) OCP  
C) LSP  
D) DIP  

**Answer: B**

---

### Question 6.2
Which principle states that a subclass should be substitutable for its parent class?

A) SRP  
B) OCP  
C) LSP  
D) ISP  

**Answer: C**

---

### Question 6.3
Which principle suggests depending on abstractions, not concretions?

A) OCP  
B) LSP  
C) DIP  
D) SRP  

**Answer: C**

---

### Question 6.4
Which principle states that a class should have only one reason to change?

A) SRP  
B) OCP  
C) ISP  
D) LSP  

**Answer: A**

---

### Question 6.5
Which principle suggests many client-specific interfaces are better than one general-purpose interface?

A) ISP  
B) DIP  
C) SRP  
D) OCP  

**Answer: A**

---

## Coding Challenges

### Challenge 1: Predict the Output
```java
class Parent {
    int x = 10;
}

class Child extends Parent {
    int x = 20;
}

public class Test {
    public static void main(String[] args) {
        Parent p = new Child();
        System.out.println(p.x);
    }
}
```

**Answer: 10** (Field hiding, not overriding)

---

### Challenge 2: Constructor Chain
```java
class A {
    A() { System.out.println("A"); }
}

class B extends A {
    B() { System.out.println("B"); }
}

class C extends B {
    C() { System.out.println("C"); }
}

public class Test {
    public static void main(String[] args) {
        new C();
    }
}
```

**Answer: A, B, C** (Constructor chain)

---

### Challenge 3: Abstract Method
```java
abstract class Shape {
    abstract double area();
}

class Circle extends Shape {
    double radius = 5;
    double area() { return Math.PI * radius * radius; }
}

public class Test {
    public static void main(String[] args) {
        Shape s = new Circle();
        System.out.println(s.area());
    }
}
```

**Answer: ~78.54**

---

### Challenge 4: Interface Default Method
```java
interface A {
    default void method() { System.out.println("A"); }
}

interface B extends A {
    default void method() { System.out.println("B"); }
}

class C implements A, B {
    public static void main(String[] args) {
        new C().method();
    }
}
```

**Answer: B**

---

### Challenge 5: Singleton Check
```java
class Singleton {
    private static Singleton instance;
    private Singleton() {}
    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}

public class Test {
    public static void main(String[] args) {
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();
        System.out.println(s1 == s2);
    }
}
```

**Answer: true**

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
| Quiz 2 | 1. C, 2. B, 3. B, 4. C, 5. B |
| Quiz 3 | 1. B, 2. B, 3. B, 4. B, 5. A |
| Quiz 4 | 1. A, 2. B, 3. B, 4. B, 5. A |
| Quiz 5 | 1. C, 2. B, 3. B, 4. C, 5. A |
| Quiz 6 | 1. B, 2. C, 3. C, 4. A, 5. A |
