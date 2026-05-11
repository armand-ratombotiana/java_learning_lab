# Java Syntax Cheat Sheet

## Variables & Data Types

```
┌─────────────────────────────────────────────────────────┐
│  PRIMITIVE TYPES                                        │
├─────────────────────────────────────────────────────────┤
│  byte    │ 8-bit  │ -128 to 127                         │
│  short   │ 16-bit │ -32,768 to 32,767                   │
│  int     │ 32-bit │ -2.1B to 2.1B                       │
│  long    │ 64-bit │ -9.2 Quintillion to 9.2 Quintillion │
│  float   │ 32-bit │ ~6-7 digits                         │
│  double  │ 64-bit │ ~15 digits                          │
│  char    │ 16-bit │ Unicode characters                  │
│  boolean │ -      │ true / false                        │
└─────────────────────────────────────────────────────────┘
```

## Operators

```
ARITHMETIC:    +   -   *   /   %   ++   --   +=   -=   *=   /=
COMPARISON:    ==  !=  <   >   <=  >=
LOGICAL:       &&  ||  !
BITWISE:       &   |   ^   ~   <<  >>  >>>
TERNARY:       condition ? value1 : value2
```

## Control Flow

```java
// If-Else
if (condition) {
    // code
} else if (condition2) {
    // code
} else {
    // code
}

// Switch (Java 14+)
switch (value) {
    case 1 -> "one";
    case 2 -> "two";
    default -> "other";
}

// For Loop
for (int i = 0; i < 10; i++) { }
for (int item : array) { }

// While Loop
while (condition) { }
do { } while (condition);
```

## Arrays

```java
// Declaration
int[] numbers = new int[5];
int[] values = {1, 2, 3, 4, 5};
String[] names = new String[]{"A", "B", "C"};

// Access
numbers[0] = 10;
int x = numbers[0];
int len = numbers.length;

// 2D Array
int[][] matrix = new int[3][4];
int[][] grid = {{1,2}, {3,4}};
```

## Strings

```java
String s = "Hello";
s.length();
s.charAt(0);          // 'H'
s.substring(0, 5);    // "Hello"
s.toLowerCase();      // "hello"
s.toUpperCase();      // "HELLO"
s.indexOf("ell");     // 1
s.contains("ell");    // true
s.replace("l", "x");  // "Hexxo"
s.split(",");         // String[]
s.trim();
s.isEmpty();
s.isBlank();          // Java 11+

// StringBuilder
StringBuilder sb = new StringBuilder();
sb.append("Hello");
sb.append(" World");
sb.toString();        // "Hello World"
```

## Methods

```java
// Basic
returnType methodName(paramType paramName) {
    return value;
}

// Overloading (same name, different params)
int add(int a, int b) { return a + b; }
double add(double a, double b) { return a + b; }

// Varargs
void printAll(String... items) {
    for (String item : items) {
        System.out.println(item);
    }
}
```

## Exception Handling

```java
try {
    // code that might throw
    throw new Exception("message");
} catch (SpecificException e) {
    // handle specific
} catch (Exception e) {
    // handle any
} finally {
    // always executes
}

// Try-with-resources (auto-close)
try (FileReader fr = new FileReader("file.txt");
     BufferedReader br = new BufferedReader(fr)) {
    // resources auto-closed
}

// Custom Exception
public class MyException extends Exception {
    public MyException(String message) {
        super(message);
    }
}
```

## Classes & Objects

```java
// Class Definition
public class Person {
    private String name;      // instance variable
    private int age;
    public static int count;  // class variable
    
    // Constructor
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
        count++;
    }
    
    // Method
    public void greet() {
        System.out.println("Hello, I'm " + name);
    }
    
    // Getters/Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

// Object Creation
Person p = new Person("Alice", 30);
p.greet();
```

## Enums

```java
public enum Day {
    MONDAY, TUESDAY, WEDNESDAY, 
    THURSDAY, FRIDAY, SATURDAY, SUNDAY;
    
    public boolean isWeekend() {
        return this == SATURDAY || this == SUNDAY;
    }
}

// Usage
Day day = Day.MONDAY;
day.isWeekend();  // false
```

## Collections Quick Reference

```java
// List
List<String> list = new ArrayList<>();
list.add("a");
list.get(0);
list.size();

// Set
Set<Integer> set = new HashSet<>();
set.add(1);

// Map
Map<String, Integer> map = new HashMap<>();
map.put("key", 1);
map.get("key");
```

## Common Patterns

```java
// Immutable
public final class ImmutableClass {
    private final int value;
    // no setters, final fields
}

// Singleton
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();
    private Singleton() {}
    public static Singleton getInstance() { return INSTANCE; }
}

// Builder Pattern
public class User {
    private String name;
    private int age;
    
    public static class Builder {
        private User user = new User();
        public Builder name(String name) { 
            user.name = name; 
            return this; 
        }
        public User build() { return user; }
    }
}
```
