# Common Mistakes in Java Basics

## 1. Using = Instead of == 

```java
// WRONG - assigns value
if (x = 5) { }

// CORRECT - compares values
if (x == 5) { }
```

## 2. Comparing Strings with ==

```java
// WRONG - compares references
if (str1 == str2) { }

// CORRECT - compares content
if (str1.equals(str2)) { }
```

## 3. Off-by-One Errors in Arrays

```java
int[] arr = new int[5];  // indices 0-4

// WRONG - ArrayIndexOutOfBoundsException
arr[5] = 10;

// CORRECT
arr[4] = 10;
```

## 4. Infinite Loops

```java
// WRONG -忘记增量
for (int i = 0; i < 5; ) {
    System.out.println(i);
}

// CORRECT
for (int i = 0; i < 5; i++) {
    System.out.println(i);
}
```

## 5. Integer Division Confusion

```java
// WRONG - expects 1.5 but gets 1
double result = 3 / 2;  // result = 1.0

// CORRECT - cast or use double
double result = 3.0 / 2;  // result = 1.5
```

## 6. Not Initializing Variables

```java
// WRONG - may cause error
int x;
System.out.println(x);  // compile error if not initialized

// CORRECT - always initialize
int x = 0;
```

## 7. Wrong Switch Break

```java
// WRONG - fall-through without break
switch (x) {
    case 1: System.out.println("One");
    case 2: System.out.println("Two"); break;
}
// Input 1 prints: "One" "Two"

// CORRECT
switch (x) {
    case 1: System.out.println("One"); break;
    case 2: System.out.println("Two"); break;
}
```

## 8. Using Floating Point for Money

```java
// WRONG - precision issues
double price = 19.99;
System.out.println(price * 100);  // 1998.9999999999998

// CORRECT - use BigDecimal
BigDecimal price = new BigDecimal("19.99");
```