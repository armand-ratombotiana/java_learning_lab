# Methods — Code Deep Dive

## Example 1: Method Declaration Forms

```java
public class MethodDemo {
    // No parameters, no return
    public void sayHello() {
        System.out.println("Hello!");
    }
    
    // Parameters, no return
    public void greet(String name) {
        System.out.println("Hello, " + name + "!");
    }
    
    // Parameters, return value
    public int add(int a, int b) {
        return a + b;
    }
    
    // Return object
    public String buildMessage(String prefix, String suffix) {
        return prefix + " " + suffix;
    }
    
    // Static method
    public static int multiply(int a, int b) {
        return a * b;
    }
    
    // Private helper
    private void log(String message) {
        System.out.println("[LOG] " + message);
    }
    
    public static void main(String[] args) {
        MethodDemo demo = new MethodDemo();
        demo.sayHello();
        demo.greet("Alice");
        System.out.println("3 + 4 = " + demo.add(3, 4));
        System.out.println("5 * 6 = " + multiply(5, 6));
    }
}
```

## Example 2: Method Overloading

```java
public class OverloadDemo {
    // Different parameter count
    public int sum(int a, int b) {
        return a + b;
    }
    public int sum(int a, int b, int c) {
        return a + b + c;
    }
    
    // Different parameter types
    public double sum(double a, double b) {
        return a + b;
    }
    
    // Different type order
    public String format(String prefix, int value) {
        return prefix + value;
    }
    public String format(int value, String prefix) {
        return value + prefix;
    }
    
    // Autoboxing variant
    public void process(int x) {
        System.out.println("primitive int: " + x);
    }
    public void process(Integer x) {
        System.out.println("wrapper Integer: " + x);
    }
    
    public static void main(String[] args) {
        OverloadDemo d = new OverloadDemo();
        System.out.println(d.sum(1, 2));         // calls int,int
        System.out.println(d.sum(1, 2, 3));      // calls int,int,int
        System.out.println(d.sum(1.5, 2.5));     // calls double,double
        System.out.println(d.format("val:", 5)); // calls String,int
        System.out.println(d.format(5, "val:")); // calls int,String
        
        d.process(5);     // calls process(int) — exact match
        // d.process(null);  // ambiguous! Both int and Integer match
    }
}
```

## Example 3: Varargs

```java
public class VarargsDemo {
    // Varargs must be last parameter
    public void printNumbers(String label, int... numbers) {
        System.out.print(label + ": ");
        for (int n : numbers) {
            System.out.print(n + " ");
        }
        System.out.println();
    }
    
    // Varargs with zero arguments
    public void printAll(String... items) {
        System.out.println("Items count: " + items.length);
        for (String item : items) {
            System.out.println("  - " + item);
        }
    }
    
    // Varargs vs array (same signature!)
    // public void process(int[] arr) { }       // Same erasure as below
    // public void process(int... arr) { }      // Compilation error!
    
    public static void main(String[] args) {
        VarargsDemo d = new VarargsDemo();
        d.printNumbers("Count", 1, 2, 3);          // 3 ints
        d.printNumbers("Single", 42);               // 1 int
        d.printNumbers("None");                      // 0 ints
        d.printAll("apple", "banana", "cherry");
        d.printAll();                                // empty
        d.printNumbers("Array", new int[]{10, 20});  // explicit array
    }
}
```

## Example 4: Recursion

```java
public class RecursionDemo {
    // Factorial: n! = n * (n-1)!
    public static int factorial(int n) {
        if (n <= 1) return 1;           // Base case
        return n * factorial(n - 1);    // Recursive case
    }
    
    // Fibonacci: fib(n) = fib(n-1) + fib(n-2)
    public static int fib(int n) {
        if (n <= 1) return n;           // Base case
        return fib(n - 1) + fib(n - 2); // Recursive (inefficient!)
    }
    
    // Optimized Fibonacci with memoization
    public static int fibMemo(int n, int[] memo) {
        if (n <= 1) return n;
        if (memo[n] != 0) return memo[n];  // Already computed
        memo[n] = fibMemo(n - 1, memo) + fibMemo(n - 2, memo);
        return memo[n];
    }
    
    // Palindrome check with recursion
    public static boolean isPalindrome(String s) {
        if (s.length() <= 1) return true;
        if (s.charAt(0) != s.charAt(s.length() - 1)) return false;
        return isPalindrome(s.substring(1, s.length() - 1));
    }
    
    public static void main(String[] args) {
        System.out.println("5! = " + factorial(5));          // 120
        System.out.println("fib(10) = " + fib(10));          // 55
        
        int n = 40;
        int[] memo = new int[n + 1];
        System.out.println("fibMemo(40) = " + fibMemo(40, memo)); // 102334155
        
        System.out.println("racecar: " + isPalindrome("racecar")); // true
        System.out.println("hello: " + isPalindrome("hello"));     // false
    }
}
```
