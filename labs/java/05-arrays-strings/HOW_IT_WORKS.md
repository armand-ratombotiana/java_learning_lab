# Arrays & Strings — How It Works

## Arrays: Step by Step

### Step 1: Declaration and Creation
```java
int[] numbers = new int[5];
```
1. Allocates contiguous heap memory: 5 × 4 bytes + object header (12-16 bytes) = 32-36 bytes
2. Initializes all elements to default value (0)
3. Stores array length (5) in object header
4. Returns reference to the array object

### Step 2: Access
```java
int x = numbers[2];
```
1. Verify index: `2 >= 0 && 2 < length` (bounds check — throws IndexOutOfBoundsException if invalid)
2. Calculate offset: `array_base + 2 * element_size`
3. Load value at offset

### Step 3: Multi-dimensional Arrays
```java
int[][] matrix = new int[3][4];
```
1. Create array of 3 references (each pointing to an int[4] array)
2. Create 3 int[4] arrays
3. Total: 1 + 3 = 4 array objects

## Strings: Step by Step

### Step 1: String Literal
```java
String s = "hello";
```
1. JVM checks String pool for "hello"
2. If found: return existing reference
3. If not found: create String object, add to pool, return reference

### Step 2: String Concatenation
```java
String result = "Hello, " + name;
```
Java 9+: Uses `invokedynamic` with `StringConcatFactory`. Bootstrap makes policy decisions (use StringBuilder, direct array copy, etc.). Java 8 and earlier: compiled to `new StringBuilder().append("Hello, ").append(name).toString()`.

### Step 3: Immutability Implementation
```java
s = s + "!";
```
1. New String object created containing "hello!"
2. Original "hello" remains unchanged (still in pool, referenced by any other variables)
