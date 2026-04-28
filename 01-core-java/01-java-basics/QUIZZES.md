# 📝 Java Basics - Quizzes & Practice Questions

## Table of Contents
1. [Beginner Quizzes](#beginner-quizzes)
2. [Intermediate Quizzes](#intermediate-quizzes)
3. [Advanced Quizzes](#advanced-quizzes)
4. [Interview Tricky Questions](#interview-tricky-questions)

---

## Beginner Quizzes

### Q1: Variable Declaration and Initialization
Which of the following is VALID Java code?

```java
A) int x = 10; double y = x;      // Widening - valid
B) double x = 10.5; int y = x;    // Narrowing - needs cast
C) String s = "hello" + 5;        // Valid - concatenation
D) boolean b = 1;                 // Invalid - boolean is not int

E) char c = 65;                   // Valid - 65 is ASCII 'A'
```

**Correct Answer:** A, C, E
- A: Widening conversion (int → double) is automatic
- B: INVALID - narrowing requires explicit cast: `int y = (int) x;`
- C: Valid - operator overloading for String concatenation
- D: INVALID - `boolean` is its own type NOT an integer
- E: Valid - char can be initialized with int (ASCII code)

**Why This Matters:** Java is strongly typed. Understanding widening vs narrowing prevents compilation errors.

---

### Q2: Understanding Primitive Defaults
What is printed?

```java
class DefaultValues {
    int x;
    double y;
    boolean z;
    String s;
    
    public static void main(String[] args) {
        DefaultValues obj = new DefaultValues();
        System.out.println(x + " " + y + " " + z + " " + s);
    }
}
```

**Answer:**
```
0 0.0 false null
```

**Explanation:**
- Instance variables get default values
- `int`: 0
- `double`: 0.0
- `boolean`: false
- `String` (reference): null
- **Local variables** have NO default - you MUST initialize them or get compiler error

---

### Q3: String Concatenation
What is printed?

```java
String s1 = "Hello";
String s2 = "Hello";
String s3 = new String("Hello");

System.out.println(s1 == s2);      // ?
System.out.println(s1 == s3);      // ?
System.out.println(s1.equals(s3)); // ?
```

**Answer:**
```
true
false
true
```

**Explanation:**
- `s1 == s2`: true - both are literals from pool, same reference
- `s1 == s3`: false - s3 is new heap object, different reference
- `s1.equals(s3)`: true - content comparison

**Key Lesson:** Always use `.equals()` for String comparison, NOT `==`

---

### Q4: Operator Precedence
What is the value of result?

```java
int result = 10 + 5 * 2 - 3 / 2;
```

**Answer:** `19`

**Calculation Order:**
1. `5 * 2` = 10 (multiplication first)
2. `3 / 2` = 1 (integer division)
3. `10 + 10 - 1` = 19 (left-to-right)

**Why It Matters:** Operator precedence can hide bugs. Use parentheses for clarity:
```java
int result = 10 + (5 * 2) - (3 / 2);  // Same result, clearer intent
```

---

### Q5: Type Casting
Which statement correctly prevents data loss?

```java
double d = 42.7;
byte b = d;              // A - Compile error

byte b = (byte) 42.7;    // B - Compiles, but loses .7
byte b = (byte) (int) 42.7; // C - Same, narrows twice
byte b = Math.round(d);  // D - Best option for rounding
```

**Answer:** D is best

**Why:**
- A: Compile error - narrowing requires cast
- B, C: Compiles but silently loses decimal
- D: Properly rounds to nearest integer, then casts to byte

---

## Intermediate Quizzes

### Q6: Variable Scope and Shadowing
What is printed?

```java
public void test() {
    int count = 1;
    System.out.println(count);  // ?
    
    {
        int count = 2;  // Shadows outer scope
        System.out.println(count);  // ?
    }
    
    System.out.println(count);  // ?
}
```

**Output:**
```
1
2
1
```

**Explanation:**
- Shadowing is allowed but BAD practice
- Inner scope's `count` hides outer scope's `count`
- When inner scope ends, outer `count` is still 1 (unchanged)
- Inner variable is deallocated from stack

**Code Smell:** Shadowing makes code hard to follow - avoid it

---

### Q7: Floating-Point Precision
What is printed?

```java
double x = 0.1 + 0.2;
System.out.println(x);              // ?
System.out.println(x == 0.3);       // ?

BigDecimal bd1 = new BigDecimal("0.1");
BigDecimal bd2 = new BigDecimal("0.2");
System.out.println(bd1.add(bd2));   // ?
System.out.println(bd1.add(bd2).equals(new BigDecimal("0.3"))); // ?
```

**Output:**
```
0.30000000000000004
false
0.3
true
```

**Explanation:**
- 0.1 and 0.2 cannot be represented exactly in binary (IEEE 754)
- Results in rounding errors: 0.30000000000000004
- `== ` on doubles is DANGEROUS
- For financial calculations: use `BigDecimal` (base 10), NOT double

**Key Lesson:** NEVER use `==` for double comparison
```java
// Correct way:
double epsilon = 1e-9;
if (Math.abs(x - 0.3) < epsilon) { }  // Safe comparison

// Or better for financial: use BigDecimal
```

---

### Q8: Integer Overflow
What is printed?

```java
int x = Integer.MAX_VALUE;
System.out.println(x);              // ?
System.out.println(x + 1);          // ?

long y = Integer.MAX_VALUE;
System.out.println(y);              // ?
System.out.println(y + 1);          // ?
```

**Output:**
```
2147483647
-2147483648
2147483647
2147483648
```

**Explanation:**
- `int` has fixed size (32 bits)
- Adding 1 to MAX_VALUE wraps to MIN_VALUE (overflow)
- Java does NOT throw exception - silent wrap-around
- `long` is 64-bit, so y+1 fits correctly

**This is a MAJOR source of bugs in production code!**

---

### Q9: Switch Statement Fall-Through
What is printed?

```java
int day = 2;
switch (day) {
    case 1:
        System.out.println("Monday");
    case 2:
        System.out.println("Tuesday");
    case 3:
        System.out.println("Wednesday");
    default:
        System.out.println("Other");
}
```

**Output:**
```
Tuesday
Wednesday
Other
```

**Explanation:**
- Execution starts at `case 2`
- Without `break`, it "falls through" to next cases
- This is usually a BUG (why default prints too)
- Always include `break;` unless fall-through is intentional

**Fix:**
```java
case 2:
    System.out.println("Tuesday");
    break;  // Prevents fall-through
```

---

### Q10: Enhanced For-Loop and Arrays
Can you remove elements during iteration?

```java
List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C"));

// WRONG - ConcurrentModificationException
for (String item : list) {
    if (item.equals("B")) {
        list.remove(item);  // ❌ ERROR
    }
}

// CORRECT - Use Iterator
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (it.next().equals("B")) {
        it.remove();  // ✅ Safe removal
    }
}
```

**Why?** Enhanced for-loop uses Iterator internally, and modifying during iteration corrupts it.

---

## Advanced Quizzes

### Q11: Autoboxing and Unboxing Quirks
What is printed?

```java
Integer i1 = 100;           // Autoboxing
Integer i2 = 100;
System.out.println(i1 == i2);  // ?

Integer i3 = 1000;
Integer i4 = 1000;
System.out.println(i3 == i4);  // ?

Integer i5 = Integer.valueOf(100);
Integer i6 = Integer.valueOf(100);
System.out.println(i5 == i6);  // ?
```

**Output:**
```
true
false
true
```

**Explanation:**
- Integer.valueOf() caches values -128 to 127
- i1 and i2 both use cached value: same reference → true
- i3 and i4 are outside cache range: different objects → false
- i5 and i6 both use cached value: same reference → true

**Key Lesson:** NEVER use `==` with autoboxed types, use `.equals()`

---

### Q12: Method Overloading and Type Widening
What is printed?

```java
class Test {
    void print(int x) { System.out.println("int"); }
    void print(long x) { System.out.println("long"); }
    void print(double x) { System.out.println("double"); }
}

Test t = new Test();
t.print(5);           // ?
t.print(5L);          // ?
t.print(5.0);         // ?
t.print(5.0f);        // ?
```

**Output:**
```
int
long
double
double
```

**Explanation:**
- `5`: literal int → matches int method
- `5L`: explicit long suffix → matches long method
- `5.0`: double literal (default) → matches double method
- `5.0f`: float literal → widened to double

**JVM Matching Order:**
1. Exact match (100% priority)
2. Wrapper (auto/unboxing)
3. Widening (byte → short → int → long → float → double)

---

### Q13: String Builder vs String Concatenation
Which is more efficient?

```java
// A - String concatenation
String result = "";
for (int i = 0; i < 100000; i++) {
    result = result + i;  // Creates 100000 string objects!
}

// B - StringBuilder
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 100000; i++) {
    sb.append(i);  // Reuses buffer
}
String result = sb.toString();

// C - String.join()
List<Integer> list = new ArrayList<>();
for (int i = 0; i < 100000; i++) {
    list.add(i);
}
String result = String.join("", list.stream().map(String::valueOf).collect(Collectors.toList()));
```

**Answer:** B is most efficient

**Performance:**
- A: O(n²) - each concatenation copies entire string
- B: O(n) - amortized constant append
- C: O(n) - but with more overhead than B

**Key Lesson:** Never use string concatenation in loops, use StringBuilder

---

### Q14: Ternary Operator Edge Cases
What is printed?

```java
int x = 5;
int y = 10;
int result = x > y ? x : y;
System.out.println(result);  // ?

// What about this?
int a = 1;
System.out.println(a > 2 ? "big" : a > 1 ? "medium" : "small");  // ?

// Or this?
int b = 10;
Object response = b > 5 ? "yes" : 0;
System.out.println(response);  // ?
System.out.println(response.getClass());  // ?
```

**Output:**
```
10
small
yes
class java.lang.String
```

**Explanation:**
- First: Simple ternary, returns max value
- Second: Nested ternary (right-associative), evaluates right-to-left
- Third: Type unification - String wins over Integer, result is String("yes")

---

### Q15: Bitwise Operations - Real World Example
What is printed?

```java
int flags = 0;
int READ = 1;      // 0001
int WRITE = 2;     // 0010
int EXECUTE = 4;   // 0100

// Set permissions
flags = flags | READ | EXECUTE;     // Set bits

System.out.println((flags & READ) != 0);     // ? - Check if can READ
System.out.println((flags & WRITE) != 0);    // ? - Check if can WRITE
System.out.println((flags & EXECUTE) != 0);  // ? - Check if can EXECUTE

// Remove EXECUTE permission
flags = flags & ~EXECUTE;           // Clear bit
System.out.println((flags & EXECUTE) != 0);  // ?
```

**Output:**
```
true
false
true
false
```

**Explanation:**
- Bitwise AND/OR used for permission flags
- `| ` sets bits
- `& ` checks bits
- `^ ` toggles bits
- `~` inverts bits

**Used in:** File permissions, graphics (RGB colors), networking flags

---

## Interview Tricky Questions

### Q16: What's Wrong With This Code?
```java
public class Test {
    public static void main(String[] args) {
        String[] names = {"Alice", "Bob", "Charlie"};
        for (int i = 0; i <= names.length; i++) {
            System.out.println(names[i]);
        }
    }
}
```

**Answer:** Off-by-one error! Uses `i <= names.length` instead of `i < names.length`

**Result:** ArrayIndexOutOfBoundsException on last iteration
- names.length = 3
- Loop tries to access index 3, but valid indices are 0, 1, 2

**Fix:** `for (int i = 0; i < names.length; i++)`

---

### Q17: Null Pointer Questions
```java
String s = null;
System.out.println(s.length());     // What happens?
System.out.println(s == null);      // ?
System.out.println(null == null);   // ?
```

**Answers:**
```
Line 2: NullPointerException
Line 3: true
Line 4: true
```

**Key Points:**
- Any method call on null throws NPE
- Null comparison is safe
- null == null is true

---

### Q18: What's the Value?
```java
char c = 'A';
System.out.println(c + 1);     // ? 
System.out.println((char)(c + 1)); // ?

String str = "ABC";
System.out.println(str + 1);   // ?
```

**Answers:**
```
66
B
ABC1
```

**Explanation:**
- 'A' is char (ASCII 65), + 1 converts to int: 65 + 1 = 66
- Cast back to char: (char) 66 = 'B'
- String + int: concatenation returns "ABC1"

---

### Q19: Increment & Array Index
```java
int[] arr = {10, 20, 30};
int i = 0;
System.out.println(arr[i++] + arr[i++] + arr[i++]);  // ?
System.out.println(i);  // ?

int j = 0;
System.out.println(arr[++j] + arr[++j] + arr[++j]);  // ?
System.out.println(j);  // ?
```

**Answers:**
```
60
3
60
3
```

**Explanation:**
- `arr[i++]`: Use value (0), then increment, so accesses arr[0], arr[1], arr[2]
- `arr[++j]`: Increment (1), then use, so accesses arr[1], arr[2], arr[3]
    - Wait! arr[3] is OUT OF BOUNDS! This throws ArrayIndexOutOfBoundsException

**Corrected Output:**
```
60
3
Exception in thread "main": java.lang.ArrayIndexOutOfBoundsException: Index 3 out of bounds for length 3
```

---

### Q20: String Immutability Consequence
```java
String original = "Hello";
String temp = original;
temp = temp.concat(" World");

System.out.println(temp);       // ?
System.out.println(original);   // ?
System.out.println(temp == original);  // ?
```

**Output:**
```
Hello World
Hello
false
```

**Why This Matters:**
- String methods return NEW strings
- Original is never modified
- `temp = ` reassigns the variable, doesn't modify objects
- This is why String is safe in multithreaded code

---

## Challenge Questions (Expert Level)

### Q21: Complex Type Inference
```java
int x = 5;
double y = 10;
float f = 2.5f;
long l = 3L;

// What's the type of each expression?
var result1 = x + y;           // ?
var result2 = x + (int)f;      // ?
var result3 = l * x;           // ?
var result4 = f / x;           // ?
```

**Answers:**
```
result1: double (int + double = double)
result2: int (int + int = int)
result3: long (long * int = long)
result4: float (float / int = float)
```

---

### Q22: Operator Overloading with String
```java
System.out.println(1 + 2 + "3");        // ?
System.out.println("1" + 2 + 3);        // ?
System.out.println("Result: " + 10 + 20); // ?
System.out.println(10 + 20 + " Result");   // ?
```

**Output:**
```
33
123
Result: 1020
30 Result
```

**Explanation:**
- `+` is left-associative: evaluated left-to-right
- First expression: 1 + 2 = 3 (int), then 3 + "3" = "33" (concat)
- Second: "1" + 2 = "12" (concat), then "12" + 3 = "123"
- Third: "Result: " + 10 = "Result: 10", then + 20 = "Result: 1020"
- Fourth: 10 + 20 = 30 (arithmetic), then 30 + " Result" = "30 Result"

**Key:** Order matters! String conversion happens left-to-right

---

## Answer Summary

| Q# | Answer | Key Concept |
|----|--------|-------------|
| 1 | A,C,E | Widening vs Narrowing |
| 2 | 0 0.0 false null | Default values |
| 3 | true, false, true | String pooling |
| 4 | 19 | Operator precedence |
| 5 | D | Smart casting |
| 6 | 1,2,1 | Variable scope |
| 7 | 0.3..., false, 0.3, true | Floating-point precision |
| 8 | 2147483647, -2147483648, (...), 2147483648 | Integer overflow |
| 9 | Tuesday, Wednesday, Other | Switch fall-through |
| 10 | Use Iterator | Safe removal |
| 11 | true, false, true | Integer caching |
| 12 | int, long, double, double | Method overloading |
| 13 | B | Performance |
| 14 | 10, small, yes, String | Ternary operator |
| 15 | true, false, true, false | Bitwise operations |
| 16 | ArrayIndexOutOfBoundsException | Off-by-one error |
| 17 | NPE, true, true | Null handling |
| 18 | 66, B, ABC1 | Type conversion |
| 19 | 60,3 / Exception | Pre/post increment |
| 20 | Hello World, Hello, false | Immutability |
| 21 | double, int, long, float | Type inference |
| 22 | 33, 123, Result: 1020, 30 Result | String concatenation |

