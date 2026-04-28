package com.learning;

/**
 * Elite interview exercises for Java Basics module.
 * Contains 35+ interview questions with detailed explanations and code solutions.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class EliteExercises {
    
    /**
     * Main entry point for elite exercises.
     */
    public static void demonstrateEliteExercises() {
        System.out.println("\n--- ELITE INTERVIEW EXERCISES ---");
        System.out.println("Topics: Variables, Types, Operators, Control Flow, Methods, Exceptions");
        
        // Easy Level Questions
        printEasyLevelQuestions();
        
        // Medium Level Questions
        printMediumLevelQuestions();
        
        // Hard Level Questions
        printHardLevelQuestions();
        
        // Review and Best Practices
        printReviewSummary();
    }
    
    /**
     * Easy Level Questions (Beginner Interview Candidates).
     */
    private static void printEasyLevelQuestions() {
        System.out.println("\n========== EASY LEVEL ==========");
        
        printQuestion(1, "What are the 8 primitive data types in Java?",
            "Answer: byte, short, int, long, float, double, char, boolean\n" +
            "Explanation: These are the fundamental data types. Everything else is a reference type.\n" +
            "Example:\n" +
            "  byte b = 127;           // 8-bit\n" +
            "  short s = 32767;        // 16-bit\n" +
            "  int i = 2147483647;     // 32-bit (default for integers)\n" +
            "  long l = 9223372036854775807L;  // 64-bit\n" +
            "  float f = 3.14f;        // 32-bit floating point\n" +
            "  double d = 3.14159;     // 64-bit floating point (default)\n" +
            "  char c = 'A';           // 16-bit Unicode character\n" +
            "  boolean b = true;       // True or false");
        
        printQuestion(2, "What is the difference between == and equals()?",
            "Answer: == compares references, equals() compares values.\n" +
            "Explanation: == checks if two objects point to the same memory location.\n" +
            "             equals() checks if the content is the same (overridable).\n" +
            "Example:\n" +
            "  String s1 = new String(\"hello\");\n" +
            "  String s2 = new String(\"hello\");\n" +
            "  s1 == s2        // false (different objects)\n" +
            "  s1.equals(s2)   // true (same content)");
        
        printQuestion(3, "What is the String pool in Java?",
            "Answer: String pool is a special memory region where Java stores string literals.\n" +
            "Explanation: When you create a string using double quotes, Java checks if an\n" +
            "             identical string exists in the pool. If yes, it returns the reference;\n" +
            "             if no, it creates a new string in the pool.\n" +
            "Code Example:\n" +
            "  String s1 = \"hello\";      // Added to pool\n" +
            "  String s2 = \"hello\";      // Points to s1 (same reference)\n" +
            "  String s3 = new String(\"hello\");  // New object, not in pool\n" +
            "  s1 == s2        // true (same reference)\n" +
            "  s1 == s3        // false (different objects)\n" +
            "  s1.equals(s3)   // true (same value)");
        
        printQuestion(4, "What is autoboxing and unboxing?",
            "Answer: Autoboxing = automatic conversion from primitive to wrapper class.\n" +
            "        Unboxing = automatic conversion from wrapper class to primitive.\n" +
            "Example:\n" +
            "  int x = 10;              // primitive\n" +
            "  Integer obj = x;         // autoboxing: int -> Integer\n" +
            "  int y = obj;             // unboxing: Integer -> int");
        
        printQuestion(5, "Explain variable scope in Java",
            "Answer: Four levels of scope: method, block, class, and package.\n" +
            "  1. Method scope: Variable accessible only within the method\n" +
            "  2. Block scope: Variable accessible only within {} block\n" +
            "  3. Class scope: Instance/static variables accessible throughout class\n" +
            "  4. Package scope: Package-level access (default visibility)\n" +
            "Example:\n" +
            "  class Example {\n" +
            "      int classVar = 10;    // class scope\n" +
            "      void method() {\n" +
            "          int methodVar = 20;  // method scope\n" +
            "          { int blockVar = 30; }  // block scope\n" +
            "      }\n" +
            "  }");
    }
    
    /**
     * Medium Level Questions (Intermediate Interview Candidates).
     */
    private static void printMediumLevelQuestions() {
        System.out.println("\n========== MEDIUM LEVEL ==========");
        
        printQuestion(6, "What is the difference between String, StringBuffer, and StringBuilder?",
            "Answer: All three represent strings but differ in mutability and thread-safety.\n" +
            "  String:        Immutable, thread-safe, slow for modifications\n" +
            "  StringBuffer:  Mutable, thread-safe (synchronized), slightly slower\n" +
            "  StringBuilder: Mutable, NOT thread-safe, fastest\n" +
            "Performance: StringBuilder > StringBuffer > String (for concatenation)\n" +
            "Use Case:\n" +
            "  String: When content is constant\n" +
            "  StringBuffer: Multi-threaded environment with string modifications\n" +
            "  StringBuilder: Single-threaded string modifications (most common)");
        
        printQuestion(7, "What is type casting and when should it be used?",
            "Answer: Type casting converts one data type to another.\n" +
            "  1. Implicit (automatic): smaller to larger type (safe)\n" +
            "     int x = 10;\n" +
            "     double d = x;  // int -> double (automatic)\n" +
            "  2. Explicit (manual): larger to smaller type (may lose data)\n" +
            "     double d = 10.5;\n" +
            "     int x = (int) d;  // double -> int (lose decimal part)\n" +
            "Widening:  byte -> short -> int -> long -> float -> double\n" +
            "Narrowing: double -> float -> long -> int -> short -> byte");
        
        printQuestion(8, "Explain the difference between pass-by-value and pass-by-reference",
            "Answer: Java is always pass-by-value, but values can be object references.\n" +
            "  Pass-by-value: Method receives a COPY of the value\n" +
            "                 Changes to primitive parameters don't affect original\n" +
            "  Pass-by-reference (for objects): Method receives a COPY of the reference\n" +
            "                 Changes to object properties affect original\n" +
            "                 But reassigning the reference doesn't affect original\n" +
            "Example:\n" +
            "  void modifyPrimitive(int x) { x = 10; }  // Doesn't affect caller\n" +
            "  void modifyArray(int[] arr) { arr[0] = 10; }  // Affects caller\n" +
            "  void reassignArray(int[] arr) { arr = new int[10]; }  // Doesn't affect caller");
        
        printQuestion(9, "What are the access modifiers in Java?",
            "Answer: Four access modifiers control visibility.\n" +
            "  public:     Accessible everywhere\n" +
            "  protected:  Accessible in same package and subclasses\n" +
            "  default:    Accessible only in same package (no modifier)\n" +
            "  private:    Accessible only in the same class\n" +
            "Visibility Table:\n" +
            "  Modifier    | Same Class | Package | Subclass | Outside\n" +
            "  public      |     Y      |    Y    |    Y     |    Y\n" +
            "  protected   |     Y      |    Y    |    Y     |    N\n" +
            "  default     |     Y      |    Y    |    N     |    N\n" +
            "  private     |     Y      |    N    |    N     |    N");
        
        printQuestion(10, "What is method overloading?",
            "Answer: Creating multiple methods with the same name but different parameters.\n" +
            "Rules:\n" +
            "  1. Different number of parameters\n" +
            "  2. Different parameter types\n" +
            "  3. Different parameter order\n" +
            "  4. Return type alone is NOT enough (causes compilation error)\n" +
            "Example:\n" +
            "  void add(int a, int b) { }\n" +
            "  void add(double a, double b) { }       // Different types\n" +
            "  void add(int a, int b, int c) { }      // Different count\n" +
            "  void add(String a, int b) { }\n" +
            "  void add(int a, String b) { }          // Different order");
        
        printQuestion(11, "Explain the finally block in exception handling",
            "Answer: finally block ALWAYS executes, whether exception occurs or not.\n" +
            "Use Cases:\n" +
            "  1. Close resources (files, connections, streams)\n" +
            "  2. Clean up state regardless of outcome\n" +
            "  3. Log information\n" +
            "Example:\n" +
            "  try {\n" +
            "      // Code that might throw exception\n" +
            "  } catch (Exception e) {\n" +
            "      // Handle exception\n" +
            "  } finally {\n" +
            "      // ALWAYS executes\n" +
            "      // Close resources here\n" +
            "  }\n" +
            "Only scenario finally doesn't execute: System.exit(0) in try/catch");
        
        printQuestion(12, "What are checked vs unchecked exceptions?",
            "Answer: Difference in compile-time enforcement.\n" +
            "Checked Exceptions:\n" +
            "  - Must be caught or declared with 'throws'\n" +
            "  - Subclass of Exception (but not RuntimeException)\n" +
            "  - Examples: IOException, SQLException, FileNotFoundException\n" +
            "Unchecked Exceptions:\n" +
            "  - No requirement to handle\n" +
            "  - Subclass of RuntimeException\n" +
            "  - Examples: NullPointerException, ArrayIndexOutOfBoundsException\n" +
            "Best Practice: Use unchecked for programming errors, checked for recoverable conditions");
    }
    
    /**
     * Hard Level Questions (Senior Interview Candidates).
     */
    private static void printHardLevelQuestions() {
        System.out.println("\n========== HARD LEVEL ==========");
        
        printQuestion(13, "Design a custom exception for handling invalid user input",
            "Solution:\n" +
            "// Create custom exception extending Exception (checked)\n" +
            "class InvalidUserInputException extends Exception {\n" +
            "    private String invalidField;\n" +
            "    private String receivedValue;\n" +
            "    \n" +
            "    public InvalidUserInputException(String message) {\n" +
            "        super(message);\n" +
            "    }\n" +
            "    \n" +
            "    public InvalidUserInputException(String field, String value) {\n" +
            "        super(\"Invalid \" + field + \": \" + value);\n" +
            "        this.invalidField = field;\n" +
            "        this.receivedValue = value;\n" +
            "    }\n" +
            "}\n" +
            "// Usage:\n" +
            "try {\n" +
            "    validateEmail(userEmail);\n" +
            "} catch (InvalidUserInputException e) {\n" +
            "    System.out.println(e.getMessage());\n" +
            "}");
        
        printQuestion(14, "Write a method that safely parses a string to integer",
            "Solution:\n" +
            "public static Integer safeParseInt(String value) {\n" +
            "    if (value == null || value.trim().isEmpty()) {\n" +
            "        System.out.println(\"Error: null or empty string\");\n" +
            "        return null;  // or use Optional.empty()\n" +
            "    }\n" +
            "    try {\n" +
            "        return Integer.parseInt(value);\n" +
            "    } catch (NumberFormatException e) {\n" +
            "        System.out.println(\"Error: \" + value + \" is not a valid integer\");\n" +
            "        return null;\n" +
            "    }\n" +
            "}\n" +
            "Better approach using Optional:\n" +
            "public static Optional<Integer> parseIntSafely(String value) {\n" +
            "    try {\n" +
            "        return value != null && !value.isEmpty() \n" +
            "            ? Optional.of(Integer.parseInt(value))\n" +
            "            : Optional.empty();\n" +
            "    } catch (NumberFormatException e) {\n" +
            "        return Optional.empty();\n" +
            "    }\n" +
            "}");
        
        printQuestion(15, "What are immutable objects and how do you create one?",
            "Answer: Object whose state cannot be changed after creation.\n" +
            "Characteristics:\n" +
            "  1. Declare class final to prevent inheritance\n" +
            "  2. Make all fields private and final\n" +
            "  3. No setters (only getters)\n" +
            "  4. Initialize all fields in constructor\n" +
            "  5. For mutable fields, return defensive copies\n" +
            "Example - Immutable Person class:\n" +
            "public final class Person {\n" +
            "    private final String name;\n" +
            "    private final int age;\n" +
            "    \n" +
            "    public Person(String name, int age) {\n" +
            "        this.name = name;\n" +
            "        this.age = age;\n" +
            "    }\n" +
            "    \n" +
            "    public String getName() { return name; }\n" +
            "    public int getAge() { return age; }\n" +
            "    // No setters!\n" +
            "}\n" +
            "Benefits: Thread-safe, hashable, cache-friendly");
        
        printQuestion(16, "Analyze this code and identify the issue: String concatenation in loop",
            "Problem Code:\n" +
            "String result = \"\";\n" +
            "for (int i = 0; i < 1000; i++) {\n" +
            "    result += \"data\" + i;  // INEFFICIENT!\n" +
            "}\n" +
            "Issue: String is immutable. Each concatenation creates a new String object.\n" +
            "       This results in 1000 intermediate String objects.\n" +
            "       Time Complexity: O(n²) because StringBuilder grows exponentially.\n" +
            "Solution 1 - Use StringBuilder:\n" +
            "StringBuilder sb = new StringBuilder();\n" +
            "for (int i = 0; i < 1000; i++) {\n" +
            "    sb.append(\"data\").append(i);\n" +
            "}\n" +
            "String result = sb.toString();\n" +
            "Performance: ~1000x faster\n" +
            "Solution 2 - Use array and join (for bulk data):\n" +
            "List<String> items = new ArrayList<>();\n" +
            "for (int i = 0; i < 1000; i++) {\n" +
            "    items.add(\"data\" + i);\n" +
            "}\n" +
            "String result = String.join(\"\", items);");
        
        printQuestion(17, "What is the difference between null and default values?",
            "Answer: Default values are assigned automatically; null must be explicit.\n" +
            "Primitive Types - Always have default values:\n" +
            "  byte, short, int, long: 0\n" +
            "  float, double: 0.0\n" +
            "  boolean: false\n" +
            "  char: '\\u0000' (null character)\n" +
            "Reference Types - Default value is null:\n" +
            "  String, objects, arrays\n" +
            "Code Example:\n" +
            "  class MyClass {\n" +
            "      int x;           // Default: 0\n" +
            "      String s;        // Default: null\n" +
            "      MyClass obj;     // Default: null\n" +
            "      boolean flag;    // Default: false\n" +
            "  }\n" +
            "Note: Local variables have NO default values and must be initialized");
        
        printQuestion(18, "Code Review: Find the memory leak",
            "Code:\n" +
            "class ResourceHolder {\n" +
            "    private byte[] largeData = new byte[1024 * 1024 * 10];  // 10MB\n" +
            "    \n" +
            "    public void processData() {\n" +
            "        // Use largeData\n" +
            "    }\n" +
            "}\n" +
            "Problem: If an exception occurs during processing, the object remains in memory.\n" +
            "         Stack frames holding references prevent garbage collection.\n" +
            "Solution 1 - Use try-finally:\n" +
            "try {\n" +
            "    holder.processData();\n" +
            "} finally {\n" +
            "    holder = null;  // Release reference\n" +
            "}\n" +
            "Solution 2 - Use try-with-resources (if class implements AutoCloseable)\n" +
            "Solution 3 - Scope management: Create object in smallest possible scope");
    }
    
    /**
     * Review and best practices.
     */
    private static void printReviewSummary() {
        System.out.println("\n========== BEST PRACTICES SUMMARY ==========");
        System.out.println("1. String Handling:");
        System.out.println("   • Use StringBuilder for string modification in loops");
        System.out.println("   • Remember String is immutable; == vs equals() matter");
        System.out.println("   • Use String methods instead of manual character manipulation");
        System.out.println("");
        System.out.println("2. Exception Handling:");
        System.out.println("   • Catch specific exceptions before general ones");
        System.out.println("   • Always close resources in finally or use try-with-resources");
        System.out.println("   • Create meaningful exception messages with context");
        System.out.println("");
        System.out.println("3. Type Safety:");
        System.out.println("   • Understand implicit and explicit type casting");
        System.out.println("   • Be aware of potential data loss in narrowing conversions");
        System.out.println("   • Use wrapper classes carefully (boxing/unboxing overhead)");
        System.out.println("");
        System.out.println("4. Variable Scope:");
        System.out.println("   • Declare variables in the smallest necessary scope");
        System.out.println("   • Use meaningful names for different scope levels");
        System.out.println("   • Avoid shadowing (same variable name in nested scopes)");
        System.out.println("");
        System.out.println("5. Performance Optimization:");
        System.out.println("   • Avoid repeated concatenation (use StringBuilder)");
        System.out.println("   • Release resources promptly (set = null only if necessary)");
        System.out.println("   • Understand BigO complexity of your operations");
    }
    
    // ===== HELPER METHODS =====
    
    /**
     * Helper method to print a formatted question and answer.
     */
    private static void printQuestion(int number, String question, String answer) {
        System.out.println("\n[Question " + number + "] " + question);
        System.out.println(answer);
        System.out.println("-".repeat(70));
    }
}
