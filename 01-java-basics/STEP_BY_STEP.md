# Step-by-Step Java Basics

## Step 1: Your First Program

1. Create file `HelloWorld.java`
2. Write class declaration: `public class HelloWorld {`
3. Add main method: `public static void main(String[] args) {`
4. Add print statement: `System.out.println("Hello!");`
5. Close braces
6. Compile: `javac HelloWorld.java`
7. Run: `java HelloWorld`

## Step 2: Variables and Types

```java
// Declare and initialize
int age = 25;              // Integer
double price = 19.99;      // Decimal
boolean isActive = true;   // True/False
char grade = 'A';          // Single character
String name = "John";      // Text (reference type)
```

## Step 3: Operators

```java
int a = 10, b = 3;
int sum = a + b;           // 13
int diff = a - b;          // 7
int prod = a * b;          // 30
int quot = a / b;          // 3 (integer division)
int rem = a % b;           // 1 (remainder)
```

## Step 4: Control Flow

```java
// If-else
if (age >= 18) {
    System.out.println("Adult");
} else {
    System.out.println("Minor");
}

// Switch
switch (day) {
    case 1: System.out.println("Mon"); break;
    case 2: System.out.println("Tue"); break;
    default: System.out.println("Other");
}
```

## Step 5: Loops

```java
// For loop
for (int i = 0; i < 5; i++) {
    System.out.println(i);
}

// While loop
while (condition) {
    // repeat while true
}

// For-each (arrays/collections)
for (String item : collection) {
    System.out.println(item);
}
```

## Step 6: Methods

```java
// Method definition
public static int add(int a, int b) {
    return a + b;
}

// Method call
int result = add(5, 3);  // result = 8
```

## Step 7: Arrays

```java
// Create array
int[] numbers = new int[5];
int[] values = {1, 2, 3, 4, 5};

// Access elements
numbers[0] = 10;
int first = numbers[0];

// Array length
int len = numbers.length;
```