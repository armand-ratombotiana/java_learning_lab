# Data Types — How It Works

## Step 1: Type Declaration

The compiler processes type declarations and assigns storage:
```java
int age = 25;    // Compiler allocates 4 bytes for 'age'
double pi = 3.14; // Compiler allocates 8 bytes for 'pi'
```

## Step 2: Type Checking

At compile time, the compiler verifies type compatibility:
- Widening conversions allowed implicitly
- Narrowing conversions require explicit cast
- Type mismatch → compilation error

## Step 3: Assignment

Primitives: value is copied. References: address is copied.
```java
int a = 5;
int b = a;  // b gets a COPY of 5
b = 10;     // a is still 5

int[] arr1 = {1, 2, 3};
int[] arr2 = arr1;  // arr2 gets a COPY of the REFERENCE
arr2[0] = 99;       // arr1[0] is also 99 — same array
```

## Step 4: Expression Evaluation

Binary numeric promotion applied automatically:
```java
byte b = 10;
int i = 5;
double d = 3.0;
// b + i: byte promoted to int → int result
// i + d: int promoted to double → double result
```

## Step 5: Autoboxing/Unboxing

Compiler inserts wrapper conversions:
```java
Integer x = 42;  // Compiled as: Integer x = Integer.valueOf(42);
int y = x;       // Compiled as: int y = x.intValue();
```

## Step 6: Memory Allocation

- Primitives in local vars → stack (or register)
- Primitives in objects → heap (inline in object)
- References → heap (the object) + stack (the reference)
- Wrappers → heap (object with primitive field)
