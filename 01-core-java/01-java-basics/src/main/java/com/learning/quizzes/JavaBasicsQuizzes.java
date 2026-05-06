package com.learning.quizzes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Java Basics - Code-Based Quizzes
 * 
 * Run this file to see interactive quiz demonstrations with explanations
 * Each quiz method is self-contained and explains the concept
 * 
 * Navigate to each quiz method to understand Java behaviors firsthand
 */
public class JavaBasicsQuizzes {

    // ==================== QUIZ 1: Type Widening ====================
    private static void quiz1_typeWidening() {
        System.out.println("\n=== QUIZ 1: Type Widening ===");
        System.out.println("Which conversions are automatic (widening)?");
        System.out.println();
        
        byte b = 100;
        short s = b;        // ✅ Automatic widening
        int i = s;          // ✅ Automatic widening
        long l = i;         // ✅ Automatic widening
        float f = l;        // ✅ Automatic widening
        double d = f;       // ✅ Automatic widening
        
        System.out.println("byte → short → int → long → float → double");
        System.out.println("All automatic, no data loss guaranteed");
        System.out.println("(except long to float may lose precision due to float's 24-bit mantissa)");
        
        // Narrowing requires explicit cast
        double orig = 42.7;
        // int narrow = orig;  // ❌ Compile error
        int narrow = (int) orig;  // ✅ Require explicit cast
        System.out.println("\nNarrowing REQUIRES explicit cast:");
        System.out.println("double 42.7 casted to int: " + narrow + " (decimal lost!)");
    }

    // ==================== QUIZ 2: String Pooling ====================
    private static void quiz2_stringPooling() {
        System.out.println("\n=== QUIZ 2: String Pooling and Interning ===");
        
        String s1 = "hello";              // Literal → goes to pool
        String s2 = "hello";              // Literal → found in pool
        String s3 = new String("hello");  // Constructor → new heap object
        
        System.out.println("s1 = \"hello\"");
        System.out.println("s2 = \"hello\"");
        System.out.println("s3 = new String(\"hello\")");
        System.out.println();
        
        System.out.println("s1 == s2: " + (s1 == s2) + " ✅ (same pool entry)");
        System.out.println("s1 == s3: " + (s1 == s3) + " ❌ (different objects)");
        System.out.println("s1.equals(s3): " + s1.equals(s3) + " ✅ (same content)");
        System.out.println();
        System.out.println("KEY LESSON: Use .equals() for String comparison, NEVER ==");
        System.out.println("Unless you specifically need reference equality (rare!)");
    }

    // ==================== QUIZ 3: Integer Overflow ====================
    private static void quiz3_integerOverflow() {
        System.out.println("\n=== QUIZ 3: Integer Overflow ===");
        
        int max = Integer.MAX_VALUE;
        System.out.println("Integer.MAX_VALUE = " + max);
        System.out.println("Adding 1...");
        
        int overflow = max + 1;
        System.out.println("Result: " + overflow);
        System.out.println("This is: Integer.MIN_VALUE = " + Integer.MIN_VALUE);
        System.out.println();
        System.out.println("⚠️  OVERFLOW IS SILENT - No exception, just wraps around!");
        System.out.println("This is why proper type sizing is crucial:");
        
        long safeLong = (long) max + 1;
        System.out.println("\nUsing long instead:");
        System.out.println("(long)Integer.MAX_VALUE + 1 = " + safeLong + " ✅ Correct");
    }

    // ==================== QUIZ 4: Floating-Point Precision ====================
    private static void quiz4_floatingPoint() {
        System.out.println("\n=== QUIZ 4: Floating-Point Precision Trap ===");
        
        double x = 0.1;
        double y = 0.2;
        double sum = x + y;
        
        System.out.println("0.1 + 0.2 = " + sum);
        System.out.println("Expected: 0.3");
        System.out.println("Actual: " + String.format("%.17f", sum));
        System.out.println();
        System.out.println("Why? 0.1 and 0.2 can't be exactly represented in binary");
        System.out.println("(IEEE 754 standard limitation)");
        System.out.println();
        System.out.println("Comparison using ==:");
        System.out.println("(0.1 + 0.2) == 0.3: " + (sum == 0.3) + " ❌ DANGEROUS!");
        System.out.println();
        
        System.out.println("Solution 1: Epsilon comparison");
        double epsilon = 1e-9;
        boolean closeEnough = Math.abs(sum - 0.3) < epsilon;
        System.out.println("Math.abs(sum - 0.3) < 1e-9: " + closeEnough + " ✅");
        
        System.out.println("\nSolution 2: Use BigDecimal for exact arithmetic");
        BigDecimal bd1 = new BigDecimal("0.1");
        BigDecimal bd2 = new BigDecimal("0.2");
        BigDecimal bdSum = bd1.add(bd2);
        System.out.println("BigDecimal(\"0.1\").add(\"0.2\"): " + bdSum + " ✅ Exact");
    }

    // ==================== QUIZ 5: Variable Scope ====================
    private static void quiz5_variableScope() {
        System.out.println("\n=== QUIZ 5: Variable Scope and Shadowing ===");
        
        int x = 1;
        System.out.println("Outer scope: x = " + x);
        
        {
            int xInner = 2;  // Shadows outer x (renamed to avoid redeclaration in same scope)
            System.out.println("Middle scope: x = " + xInner);
            
            {
                int xInnermost = 3;  // Shadows both
                System.out.println("Inner scope: x = " + xInnermost);
            }
            
            System.out.println("Back to middle scope: x = " + xInner);
        }
        
        System.out.println("Back to outer scope: x = " + x);
        System.out.println();
        System.out.println("Note: Shadowing is LEGAL but DANGEROUS");
        System.out.println("Each scope gets its own variable on the stack");
        System.out.println("Outer variables are untouched");
    }

    // ==================== QUIZ 6: Operator Precedence ====================
    private static void quiz6_operatorPrecedence() {
        System.out.println("\n=== QUIZ 6: Operator Precedence ===");
        
        int result = 10 + 5 * 2 - 3 / 2;
        
        System.out.println("Expression: 10 + 5 * 2 - 3 / 2");
        System.out.println();
        System.out.println("Step 1: 5 * 2 = 10 (multiplication first)");
        System.out.println("Step 2: 3 / 2 = 1 (division, integer truncates)");
        System.out.println("Step 3: 10 + 10 - 1 = 19 (left-to-right)");
        System.out.println();
        System.out.println("Result: " + result);
        System.out.println();
        System.out.println("TIP: Use parentheses for clarity:");
        int clarified = 10 + (5 * 2) - (3 / 2);
        System.out.println("10 + (5 * 2) - (3 / 2) = " + clarified);
    }

    // ==================== QUIZ 7: String Immutability ====================
    private static void quiz7_stringImmutability() {
        System.out.println("\n=== QUIZ 7: String Immutability ===");
        
        String original = "Java";
        System.out.println("Original string: " + original);
        System.out.println();
        
        String modified = original.toUpperCase();
        System.out.println("After toUpperCase():");
        System.out.println("  original: " + original + " (unchanged!)");
        System.out.println("  modified: " + modified);
        System.out.println();
        
        String concatenated = original.concat(" World");
        System.out.println("After concat(\" World\"):");
        System.out.println("  original: " + original + " (unchanged!)");
        System.out.println("  concatenated: " + concatenated);
        System.out.println();
        System.out.println("⚠️  All String methods return NEW strings");
        System.out.println("The original is NEVER modified");
        System.out.println("This is why String is thread-safe!");
    }

    // ==================== QUIZ 8: Modulus with Negatives ====================
    private static void quiz8_modulusNegatives() {
        System.out.println("\n=== QUIZ 8: Modulus with Negative Numbers ===");
        
        System.out.println("7 % 3 = " + (7 % 3));
        System.out.println("-7 % 3 = " + (-7 % 3) + " ⚠️  Sign follows dividend!");
        System.out.println("7 % -3 = " + (7 % -3));
        System.out.println("-7 % -3 = " + (-7 % -3));
        System.out.println();
        System.out.println("THE RULE: Result has same sign as dividend (left operand)");
        System.out.println("NOT the divisor (right operand)");
        System.out.println();
        System.out.println("To always get positive remainder:");
        int mod = (((-7) % 3) + 3) % 3;
        System.out.println("((-7 % 3) + 3) % 3 = " + mod);
        System.out.println("This is used in: circular buffers, array wrapping");
    }

    // ==================== QUIZ 9: Switch Fall-Through ====================
    private static void quiz9_switchFallThrough() {
        System.out.println("\n=== QUIZ 9: Switch Statement Fall-Through ===");
        
        int day = 2;
        System.out.println("day = " + day);
        System.out.println("switch (day) {");
        System.out.println();
        
        switch (day) {
            case 1:
                System.out.println("  case 1: prints \"Monday\"");
                System.out.println("  (execution continues - NO BREAK!)");
            case 2:
                System.out.println("  case 2: prints \"Tuesday\" ← match!");
                System.out.println("  (execution continues - NO BREAK!)");
            case 3:
                System.out.println("  case 3: prints \"Wednesday\"");
                System.out.println("  (execution continues - NO BREAK!)");
            default:
                System.out.println("  default: prints \"Other\"");
                System.out.println("  (end of switch)");
        }
        
        System.out.println();
        System.out.println("This is called FALL-THROUGH - Usually a BUG!");
        System.out.println();
        System.out.println("FIX: Add break statements");
        System.out.println("switch (day) {");
        System.out.println("    case 2:");
        System.out.println("        System.out.println(\"Tuesday\");");
        System.out.println("        break;  ← Prevents fall-through");
        System.out.println("}");
        
        System.out.println();
        System.out.println("Modern Java (17+): Use switch expressions instead");
        System.out.println("String dayName = switch (day) {");
        System.out.println("    case 1 -> \"Monday\";  ← No fall-through possible");
        System.out.println("    case 2 -> \"Tuesday\";");
        System.out.println("};");
    }

    // ==================== QUIZ 10: Uninitialized Variables ====================
    private static void quiz10_uninitializedVariables() {
        System.out.println("\n=== QUIZ 10: Uninitialized Variables ===");
        System.out.println();
        
        System.out.println("Instance variables (fields) are initialized to defaults:");
        
        class TestClass {
            int intVar;
            double doubleVar;
            boolean booleanVar;
            String stringVar;
            
            void show() {
                System.out.println("  int: " + intVar + " (default 0)");
                System.out.println("  double: " + doubleVar + " (default 0.0)");
                System.out.println("  boolean: " + booleanVar + " (default false)");
                System.out.println("  String: " + stringVar + " (default null)");
            }
        }
        
        new TestClass().show();
        
        System.out.println();
        System.out.println("Local variables are NOT initialized:");
        System.out.println("  int localInt;");
        System.out.println("  System.out.println(localInt); // ❌ COMPILE ERROR");
        System.out.println("  // Variable 'localInt' might not have been initialized");
        System.out.println();
        System.out.println("You MUST initialize local variables before use");
        int localInt = 0;  // Must initialize
        System.out.println("  int localInt = 0; ✅");
    }

    // ==================== QUIZ 11: Prefix vs Postfix Increment ====================
    private static void quiz11_incrementOperators() {
        System.out.println("\n=== QUIZ 11: Prefix vs Postfix Increment ===");
        
        int x = 5;
        System.out.println("x = 5");
        System.out.println();
        
        System.out.println("int y = ++x;  // Prefix: increment THEN use");
        int y = ++x;
        System.out.println("  x = " + x + ", y = " + y);
        System.out.println();
        
        x = 5;
        System.out.println("int z = x++;  // Postfix: use THEN increment");
        int z = x++;
        System.out.println("  x = " + x + ", z = " + z);
        System.out.println();
        
        System.out.println("⚠️  BE CAREFUL in complex expressions!");
        int[] arr = {10, 20, 30};
        int i = 0;
        System.out.println();
        System.out.println("int[] arr = {10, 20, 30};");
        System.out.println("int i = 0;");
        System.out.println("int sum = arr[i++] + arr[i++];");
        int sum = arr[i++] + arr[i++];
        System.out.println();
        System.out.println("Evaluation: arr[0] + arr[1] = " + arr[0] + " + " + arr[1] + " = " + sum);
        System.out.println("Then i becomes " + i);
    }

    // ==================== QUIZ 12: Autoboxing and Object Equality ====================
    private static void quiz12_autoboxing() {
        System.out.println("\n=== QUIZ 12: Autoboxing and == vs .equals() ===");
        
        Integer i1 = 100;
        Integer i2 = 100;
        System.out.println("Integer i1 = 100;");
        System.out.println("Integer i2 = 100;");
        System.out.println("i1 == i2: " + (i1 == i2) + " ✅ (cached -128 to 127)");
        System.out.println();
        
        Integer i3 = 1000;
        Integer i4 = 1000;
        System.out.println("Integer i3 = 1000;");
        System.out.println("Integer i4 = 1000;");
        System.out.println("i3 == i4: " + (i3 == i4) + " ❌ (outside cache range)");
        System.out.println("i3.equals(i4): " + i3.equals(i4) + " ✅ (correct way)");
        System.out.println();
        System.out.println("⚠️  Integer.valueOf() caches values -128 to 127");
        System.out.println("Different instance outside this range");
        System.out.println("NEVER use == with autoboxed types, use .equals()");
    }

    // ==================== QUIZ 13: ConcurrentModificationException ====================
    private static void quiz13_safeIteration() {
        System.out.println("\n=== QUIZ 13: Safe Iteration (ConcurrentModificationException) ===");
        
        List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C"));
        
        System.out.println("Original list: " + list);
        System.out.println();
        
        System.out.println("❌ WRONG - Using enhanced for-loop to remove:");
        System.out.println("for (String item : list) {");
        System.out.println("    if (item.equals(\"B\")) {");
        System.out.println("        list.remove(item);  // ConcurrentModificationException!");
        System.out.println("    }");
        System.out.println("}");
        System.out.println();
        
        System.out.println("✅ CORRECT - Using Iterator:");
        System.out.println("Iterator<String> it = list.iterator();");
        System.out.println("while (it.hasNext()) {");
        System.out.println("    if (it.next().equals(\"B\")) {");
        System.out.println("        it.remove();  // Safe removal");
        System.out.println("    }");
        System.out.println("}");
        System.out.println();
        
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().equals("B")) {
                it.remove();
            }
        }
        
        System.out.println("Result: " + list);
        System.out.println();
        System.out.println("Why? Enhanced for-loop uses Iterator internally");
        System.out.println("Modifying the list invalidates the iterator");
        System.out.println("Iterator.remove() is designed to be safe");
    }

    // ==================== QUIZ 14: Integer Division ====================
    private static void quiz14_integerDivision() {
        System.out.println("\n=== QUIZ 14: Integer Division Truncates ===");
        
        System.out.println("7 / 2 = " + (7 / 2) + " (not 3.5!)");
        System.out.println("7.0 / 2 = " + (7.0 / 2) + " (double: exact)");
        System.out.println("(double) 7 / 2 = " + ((double) 7 / 2) + " (cast first)");
        System.out.println();
        
        System.out.println("⚠️  REAL WORLD BUG: Calculating percentage");
        int attempts = 5;
        int total = 100;
        System.out.println();
        System.out.println("int percentage = attempts / total * 100;");
        System.out.println("int percentage = " + attempts + " / " + total + " * 100;");
        System.out.println("int percentage = " + (attempts / total) + " * 100;");
        System.out.println("int percentage = " + ((attempts / total) * 100) + " ❌ WRONG!");
        System.out.println();
        
        System.out.println("FIX 1: Multiply first");
        System.out.println("int percentage = (attempts * 100) / total;");
        System.out.println("int percentage = " + ((attempts * 100) / total) + " ✅");
        System.out.println();
        
        System.out.println("FIX 2: Use double");
        System.out.println("double percentage = (double) attempts / total * 100;");
        System.out.println("double percentage = " + ((double) attempts / total * 100) + " ✅");
    }

    // ==================== QUIZ 15: Bitwise Operations ====================
    private static void quiz15_bitwiseOperations() {
        System.out.println("\n=== QUIZ 15: Bitwise Operations ===");
        
        int x = 5;      // 0101
        int y = 3;      // 0011
        
        System.out.println("x = 5  (binary: 0101)");
        System.out.println("y = 3  (binary: 0011)");
        System.out.println();
        
        System.out.println("x & y = " + (x & y) + "  (AND: 0001)");
        System.out.println("x | y = " + (x | y) + "  (OR:  0111)");
        System.out.println("x ^ y = " + (x ^ y) + "  (XOR: 0110)");
        System.out.println("~x = " + (~x) + "  (NOT: inverts all bits)");
        System.out.println();
        
        System.out.println("Shifts:");
        System.out.println("5 << 1 = " + (5 << 1) + "  (multiply by 2)");
        System.out.println("5 >> 1 = " + (5 >> 1) + "  (divide by 2)");
        System.out.println("-5 >> 1 = " + (-5 >> 1) + " (arithmetic shift, sign extends)");
        System.out.println("-5 >>> 1 = " + (-5 >>> 1) + " (logical shift, zeros fill)");
        System.out.println();
        
        System.out.println("Real use case: File permissions");
        int READ = 1;     // 001
        int WRITE = 2;    // 010
        int EXECUTE = 4;  // 100
        
        int perms = READ | EXECUTE;  // User can read and execute
        System.out.println("\nPermissions: READ | EXECUTE = " + perms);
        System.out.println("Can READ? " + ((perms & READ) != 0));
        System.out.println("Can WRITE? " + ((perms & WRITE) != 0));
        System.out.println("Can EXECUTE? " + ((perms & EXECUTE) != 0));
    }

    // ==================== Main Menu ====================
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║  Java Basics - Code-Based Quizzes          ║");
        System.out.println("║  Run individual quiz methods to see        ║");
        System.out.println("║  interactive demonstrations                ║");
        System.out.println("╚════════════════════════════════════════════╝");
        
        // Run all quizzes
        quiz1_typeWidening();
        quiz2_stringPooling();
        quiz3_integerOverflow();
        quiz4_floatingPoint();
        quiz5_variableScope();
        quiz6_operatorPrecedence();
        quiz7_stringImmutability();
        quiz8_modulusNegatives();
        quiz9_switchFallThrough();
        quiz10_uninitializedVariables();
        quiz11_incrementOperators();
        quiz12_autoboxing();
        quiz13_safeIteration();
        quiz14_integerDivision();
        quiz15_bitwiseOperations();
        
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║  All quizzes completed!                    ║");
        System.out.println("║  Review QUIZZES.md and EDGE_CASES.md       ║");
        System.out.println("║  for more detailed explanations            ║");
        System.out.println("╚════════════════════════════════════════════╝");
    }
}
