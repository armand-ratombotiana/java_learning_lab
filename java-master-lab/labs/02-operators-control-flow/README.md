# Lab 02: Operators & Control Flow

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Beginner |
| **Estimated Time** | 4 hours |
| **Real-World Context** | Building a number guessing game with decision logic |
| **Prerequisites** | Lab 01: Java Basics |
| **Learning Type** | Hands-on, Project-based |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Master all Java operators** (arithmetic, logical, comparison, bitwise, assignment)
2. **Understand operator precedence and associativity** in complex expressions
3. **Implement conditional logic** using if/else, switch statements
4. **Use loops effectively** (for, while, do-while, enhanced for)
5. **Build a complete game** with user interaction and decision logic

## 📚 Prerequisites

- Lab 01: Java Basics completed
- Understanding of variables and data types
- Basic input/output operations
- Familiarity with Java syntax

## 🧠 Concept Theory

### 1. Arithmetic Operators

Arithmetic operators perform mathematical calculations:

```java
int a = 10;
int b = 3;

int addition = a + b;        // 13
int subtraction = a - b;     // 7
int multiplication = a * b;  // 30
int division = a / b;        // 3 (integer division)
int modulo = a % b;          // 1 (remainder)
double trueDivision = (double) a / b;  // 3.333...

// Increment and Decrement
int x = 5;
int preIncrement = ++x;      // x becomes 6, returns 6
int postIncrement = x++;     // returns 6, then x becomes 7

int y = 5;
int preDecrement = --y;      // y becomes 4, returns 4
int postDecrement = y--;     // returns 4, then y becomes 3
```

**Key Points**:
- Integer division truncates (doesn't round)
- Modulo (%) returns remainder
- Pre-increment/decrement changes value before use
- Post-increment/decrement changes value after use

### 2. Comparison Operators

Comparison operators return boolean values:

```java
int a = 10;
int b = 5;

boolean equal = a == b;           // false
boolean notEqual = a != b;        // true
boolean greaterThan = a > b;      // true
boolean lessThan = a < b;         // false
boolean greaterOrEqual = a >= b;  // true
boolean lessOrEqual = a <= b;     // false

// String comparison (use .equals(), not ==)
String str1 = "hello";
String str2 = "hello";
boolean stringEqual = str1.equals(str2);        // true
boolean stringNotEqual = !str1.equals(str2);    // false
boolean caseInsensitive = str1.equalsIgnoreCase(str2);  // true
```

**Key Points**:
- Use `==` for primitives, `.equals()` for objects
- `==` compares reference for objects, not content
- Always use `.equals()` for string comparison

### 3. Logical Operators

Logical operators combine boolean conditions:

```java
boolean a = true;
boolean b = false;

// AND operator (&&) - both must be true
boolean andResult = a && b;      // false
boolean andResult2 = a && true;  // true

// OR operator (||) - at least one must be true
boolean orResult = a || b;       // true
boolean orResult2 = b || false;  // false

// NOT operator (!) - inverts boolean
boolean notResult = !a;          // false
boolean notResult2 = !b;         // true

// Short-circuit evaluation
int x = 5;
if (x > 0 && x < 10) {
    // Both conditions checked
}

if (x < 0 || x > 100) {
    // First condition false, second checked
}

// Practical example
int age = 25;
boolean isAdult = age >= 18;
boolean hasLicense = true;
boolean canDrive = isAdult && hasLicense;  // true
```

**Key Points**:
- `&&` (AND): Both conditions must be true
- `||` (OR): At least one condition must be true
- `!` (NOT): Inverts the boolean value
- Short-circuit: `&&` stops if first is false, `||` stops if first is true

### 4. Assignment Operators

Assignment operators assign values to variables:

```java
int x = 10;

// Basic assignment
x = 5;  // x is now 5

// Compound assignment operators
x += 3;   // x = x + 3;  (x is now 8)
x -= 2;   // x = x - 2;  (x is now 6)
x *= 2;   // x = x * 2;  (x is now 12)
x /= 3;   // x = x / 3;  (x is now 4)
x %= 3;   // x = x % 3;  (x is now 1)

// String concatenation assignment
String message = "Hello";
message += " World";  // "Hello World"
```

### 5. Ternary Operator

The ternary operator is a shorthand for if-else:

```java
int age = 25;

// Traditional if-else
String status;
if (age >= 18) {
    status = "Adult";
} else {
    status = "Minor";
}

// Ternary operator
String status = age >= 18 ? "Adult" : "Minor";

// Nested ternary (use sparingly)
String category = age < 13 ? "Child" : age < 18 ? "Teen" : "Adult";

// In method calls
System.out.println(age >= 18 ? "Can vote" : "Cannot vote");
```

### 6. Operator Precedence

Operators are evaluated in a specific order:

```
1. Parentheses ()
2. Increment/Decrement (++, --)
3. Multiplication, Division, Modulo (*, /, %)
4. Addition, Subtraction (+, -)
5. Comparison (<, >, <=, >=)
6. Equality (==, !=)
7. Logical AND (&&)
8. Logical OR (||)
9. Ternary (?:)
10. Assignment (=, +=, -=, etc.)
```

**Examples**:
```java
int result = 2 + 3 * 4;        // 14 (not 20)
int result2 = (2 + 3) * 4;     // 20
boolean logic = true || false && false;  // true (AND before OR)
boolean logic2 = (true || false) && false;  // false
```

### 7. Control Flow: if-else Statements

Conditional statements execute code based on conditions:

```java
int score = 85;

// Simple if
if (score >= 90) {
    System.out.println("Grade A");
}

// if-else
if (score >= 90) {
    System.out.println("Grade A");
} else {
    System.out.println("Not Grade A");
}

// if-else if-else
if (score >= 90) {
    System.out.println("Grade A");
} else if (score >= 80) {
    System.out.println("Grade B");
} else if (score >= 70) {
    System.out.println("Grade C");
} else {
    System.out.println("Grade F");
}

// Nested if
if (score >= 70) {
    if (score >= 90) {
        System.out.println("Excellent");
    } else {
        System.out.println("Good");
    }
}
```

### 8. Control Flow: switch Statements

Switch statements are cleaner for multiple conditions:

```java
int day = 3;
String dayName;

switch (day) {
    case 1:
        dayName = "Monday";
        break;
    case 2:
        dayName = "Tuesday";
        break;
    case 3:
        dayName = "Wednesday";
        break;
    case 4:
        dayName = "Thursday";
        break;
    case 5:
        dayName = "Friday";
        break;
    case 6:
        dayName = "Saturday";
        break;
    case 7:
        dayName = "Sunday";
        break;
    default:
        dayName = "Invalid day";
}

// Java 14+: Switch expressions
String dayName = switch (day) {
    case 1 -> "Monday";
    case 2 -> "Tuesday";
    case 3 -> "Wednesday";
    case 4 -> "Thursday";
    case 5 -> "Friday";
    case 6 -> "Saturday";
    case 7 -> "Sunday";
    default -> "Invalid day";
};

// Switch with multiple cases
char grade = 'B';
switch (grade) {
    case 'A':
    case 'B':
        System.out.println("Good");
        break;
    case 'C':
        System.out.println("Average");
        break;
    default:
        System.out.println("Poor");
}
```

**Key Points**:
- Use `break` to exit the switch
- Without `break`, execution falls through to next case
- `default` is optional but recommended
- Java 14+ supports switch expressions with `->`

### 9. Loops: for Loop

The for loop repeats code a specific number of times:

```java
// Traditional for loop
for (int i = 0; i < 5; i++) {
    System.out.println("Iteration " + i);
}
// Output: 0, 1, 2, 3, 4

// Nested loops
for (int i = 1; i <= 3; i++) {
    for (int j = 1; j <= 3; j++) {
        System.out.print(i * j + " ");
    }
    System.out.println();
}
// Output:
// 1 2 3
// 2 4 6
// 3 6 9

// Loop with different increments
for (int i = 0; i < 10; i += 2) {
    System.out.println(i);  // 0, 2, 4, 6, 8
}

// Infinite loop (use with caution)
for (;;) {
    // Code here
    if (condition) break;
}
```

### 10. Loops: while and do-while

While loops repeat while a condition is true:

```java
// while loop
int count = 0;
while (count < 5) {
    System.out.println("Count: " + count);
    count++;
}

// do-while loop (executes at least once)
int num = 0;
do {
    System.out.println("Number: " + num);
    num++;
} while (num < 5);

// Practical example: input validation
Scanner scanner = new Scanner(System.in);
int age = -1;
while (age < 0 || age > 150) {
    System.out.print("Enter valid age: ");
    age = scanner.nextInt();
}
```

### 11. Enhanced for Loop (for-each)

The enhanced for loop iterates over collections:

```java
int[] numbers = {1, 2, 3, 4, 5};

// Enhanced for loop
for (int num : numbers) {
    System.out.println(num);
}

// Equivalent traditional for loop
for (int i = 0; i < numbers.length; i++) {
    System.out.println(numbers[i]);
}

// With ArrayList
List<String> fruits = Arrays.asList("Apple", "Banana", "Orange");
for (String fruit : fruits) {
    System.out.println(fruit);
}
```

### 12. Loop Control: break and continue

Control loop execution:

```java
// break: exits the loop
for (int i = 0; i < 10; i++) {
    if (i == 5) {
        break;  // Exits loop when i equals 5
    }
    System.out.println(i);  // 0, 1, 2, 3, 4
}

// continue: skips to next iteration
for (int i = 0; i < 10; i++) {
    if (i % 2 == 0) {
        continue;  // Skips even numbers
    }
    System.out.println(i);  // 1, 3, 5, 7, 9
}

// Labeled break (break out of nested loops)
outerLoop:
for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        if (i == 1 && j == 1) {
            break outerLoop;  // Breaks out of both loops
        }
        System.out.println("i=" + i + ", j=" + j);
    }
}
```

---

## 💻 Step-by-Step Coding Tasks

### Task 1: Arithmetic Operations

**Objective**: Practice arithmetic operators and operator precedence

**Acceptance Criteria**:
- [ ] All arithmetic operations work correctly
- [ ] Operator precedence is respected
- [ ] Results are displayed with proper formatting

**Instructions**:
1. Create a class `ArithmeticOperations`
2. Declare variables for two numbers
3. Perform all arithmetic operations
4. Display results with labels
5. Demonstrate operator precedence

**Code Template**:
```java
package com.learning;

public class ArithmeticOperations {
    public static void main(String[] args) {
        int num1 = 20;
        int num2 = 6;
        
        // TODO: Perform arithmetic operations
        // TODO: Display results
    }
}
```

**Expected Output**:
```
Addition: 26
Subtraction: 14
Multiplication: 120
Division: 3
Modulo: 2
Operator Precedence: 26
```

### Task 2: Conditional Logic

**Objective**: Implement if-else and switch statements

**Acceptance Criteria**:
- [ ] if-else statements work correctly
- [ ] switch statement handles all cases
- [ ] Logic is clear and efficient

**Instructions**:
1. Create a class `GradeCalculator`
2. Accept a score as input
3. Use if-else to determine grade
4. Use switch for grade description
5. Display results

**Code Template**:
```java
package com.learning;

import java.util.Scanner;

public class GradeCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // TODO: Accept score input
        // TODO: Calculate grade using if-else
        // TODO: Get description using switch
        // TODO: Display results
    }
}
```

**Expected Output**:
```
Enter score: 85
Grade: B
Description: Good performance
```

### Task 3: Loop Implementation

**Objective**: Use different types of loops effectively

**Acceptance Criteria**:
- [ ] for loop works correctly
- [ ] while loop works correctly
- [ ] Enhanced for loop works correctly
- [ ] Loop control (break/continue) works

**Instructions**:
1. Create a class `LoopExamples`
2. Implement a for loop (1 to 10)
3. Implement a while loop (countdown)
4. Implement an enhanced for loop (array)
5. Use break and continue appropriately

**Code Template**:
```java
package com.learning;

public class LoopExamples {
    public static void main(String[] args) {
        // TODO: for loop example
        // TODO: while loop example
        // TODO: enhanced for loop example
        // TODO: break and continue examples
    }
}
```

---

## 🎨 Mini-Project: Number Guessing Game

### Project Overview

**Description**: Create an interactive number guessing game where the user tries to guess a random number between 1 and 100. The game provides hints and counts attempts.

**Real-World Application**: Foundation for game development, user interaction, and decision logic.

**Learning Value**: Master operators, control flow, loops, and user interaction.

### Project Requirements

#### Functional Requirements
- [ ] Generate a random number between 1 and 100
- [ ] Accept user guesses
- [ ] Provide hints (too high/too low)
- [ ] Count number of attempts
- [ ] Determine difficulty level based on attempts
- [ ] Allow multiple games
- [ ] Display game statistics

#### Non-Functional Requirements
- [ ] Clean, readable code
- [ ] Proper error handling
- [ ] User-friendly prompts
- [ ] Efficient logic

### Project Structure

```
number-guessing-game/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           ├── Game.java
│   │           ├── GameManager.java
│   │           └── Main.java
│   └── test/
│       └── java/
│           └── com/learning/
│               └── GameTest.java
├── pom.xml
└── README.md
```

### Implementation Guide

#### Step 1: Create Game Class

```java
package com.learning;

import java.util.Random;
import java.util.Scanner;

/**
 * Represents a single game of number guessing.
 */
public class Game {
    private int secretNumber;
    private int attempts;
    private int maxAttempts;
    private boolean gameWon;
    
    /**
     * Initializes a new game with a random secret number.
     */
    public Game() {
        Random random = new Random();
        this.secretNumber = random.nextInt(100) + 1;  // 1-100
        this.attempts = 0;
        this.maxAttempts = 10;
        this.gameWon = false;
    }
    
    /**
     * Processes a guess and returns feedback.
     *
     * @param guess the user's guess
     * @return feedback message
     */
    public String processGuess(int guess) {
        attempts++;
        
        if (guess == secretNumber) {
            gameWon = true;
            return "Correct! You won!";
        } else if (guess < secretNumber) {
            return "Too low! Try again.";
        } else {
            return "Too high! Try again.";
        }
    }
    
    /**
     * Checks if the game is won.
     */
    public boolean isWon() {
        return gameWon;
    }
    
    /**
     * Checks if max attempts reached.
     */
    public boolean isGameOver() {
        return attempts >= maxAttempts || gameWon;
    }
    
    /**
     * Gets the number of attempts.
     */
    public int getAttempts() {
        return attempts;
    }
    
    /**
     * Gets the difficulty level based on attempts.
     */
    public String getDifficultyLevel() {
        if (attempts <= 3) {
            return "Genius!";
        } else if (attempts <= 6) {
            return "Excellent!";
        } else if (attempts <= 8) {
            return "Good!";
        } else {
            return "Lucky!";
        }
    }
    
    /**
     * Gets the secret number (for testing).
     */
    public int getSecretNumber() {
        return secretNumber;
    }
}
```

#### Step 2: Create GameManager Class

```java
package com.learning;

import java.util.Scanner;

/**
 * Manages the game flow and user interaction.
 */
public class GameManager {
    private Scanner scanner;
    private int totalGames;
    private int gamesWon;
    
    public GameManager() {
        this.scanner = new Scanner(System.in);
        this.totalGames = 0;
        this.gamesWon = 0;
    }
    
    /**
     * Plays a single game.
     */
    public void playGame() {
        Game game = new Game();
        totalGames++;
        
        System.out.println("\n=== Number Guessing Game ===");
        System.out.println("Guess a number between 1 and 100");
        System.out.println("You have 10 attempts\n");
        
        while (!game.isGameOver()) {
            System.out.print("Enter your guess: ");
            
            try {
                int guess = scanner.nextInt();
                
                // Validate input
                if (guess < 1 || guess > 100) {
                    System.out.println("Please enter a number between 1 and 100");
                    continue;
                }
                
                String feedback = game.processGuess(guess);
                System.out.println(feedback);
                System.out.println("Attempts: " + game.getAttempts() + "/10\n");
                
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();  // Clear invalid input
            }
        }
        
        // Display results
        displayGameResults(game);
    }
    
    /**
     * Displays game results.
     */
    private void displayGameResults(Game game) {
        System.out.println("\n=== Game Over ===");
        
        if (game.isWon()) {
            System.out.println("Congratulations! You won!");
            System.out.println("Attempts: " + game.getAttempts());
            System.out.println("Difficulty: " + game.getDifficultyLevel());
            gamesWon++;
        } else {
            System.out.println("Game Over! You lost.");
            System.out.println("The secret number was: " + game.getSecretNumber());
        }
    }
    
    /**
     * Displays overall statistics.
     */
    public void displayStatistics() {
        System.out.println("\n=== Statistics ===");
        System.out.println("Total games: " + totalGames);
        System.out.println("Games won: " + gamesWon);
        
        if (totalGames > 0) {
            double winRate = (double) gamesWon / totalGames * 100;
            System.out.printf("Win rate: %.1f%%\n", winRate);
        }
    }
    
    /**
     * Asks if user wants to play again.
     */
    public boolean playAgain() {
        System.out.print("\nPlay again? (yes/no): ");
        String response = scanner.next().toLowerCase();
        return response.equals("yes") || response.equals("y");
    }
    
    /**
     * Closes the scanner.
     */
    public void close() {
        scanner.close();
    }
}
```

#### Step 3: Create Main Class

```java
package com.learning;

/**
 * Main entry point for the Number Guessing Game.
 */
public class Main {
    public static void main(String[] args) {
        GameManager manager = new GameManager();
        
        System.out.println("Welcome to the Number Guessing Game!");
        
        // Play games until user quits
        do {
            manager.playGame();
        } while (manager.playAgain());
        
        // Display final statistics
        manager.displayStatistics();
        
        System.out.println("\nThanks for playing!");
        manager.close();
    }
}
```

#### Step 4: Create Unit Tests

```java
package com.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Game class.
 */
public class GameTest {
    
    private Game game;
    
    @BeforeEach
    void setUp() {
        game = new Game();
    }
    
    @Test
    void testGameInitialization() {
        assertFalse(game.isWon());
        assertFalse(game.isGameOver());
        assertEquals(0, game.getAttempts());
    }
    
    @Test
    void testCorrectGuess() {
        int secretNumber = game.getSecretNumber();
        String result = game.processGuess(secretNumber);
        
        assertTrue(game.isWon());
        assertEquals("Correct! You won!", result);
    }
    
    @Test
    void testTooLowGuess() {
        int secretNumber = game.getSecretNumber();
        String result = game.processGuess(secretNumber - 10);
        
        assertEquals("Too low! Try again.", result);
        assertFalse(game.isWon());
    }
    
    @Test
    void testTooHighGuess() {
        int secretNumber = game.getSecretNumber();
        String result = game.processGuess(secretNumber + 10);
        
        assertEquals("Too high! Try again.", result);
        assertFalse(game.isWon());
    }
    
    @Test
    void testAttemptCounting() {
        game.processGuess(50);
        assertEquals(1, game.getAttempts());
        
        game.processGuess(75);
        assertEquals(2, game.getAttempts());
    }
    
    @Test
    void testDifficultyLevel() {
        int secretNumber = game.getSecretNumber();
        
        // Win in 2 attempts
        game.processGuess(secretNumber - 1);
        game.processGuess(secretNumber);
        
        assertEquals("Genius!", game.getDifficultyLevel());
    }
}
```

### Running the Project

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Run the game
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

### Expected Output

```
Welcome to the Number Guessing Game!

=== Number Guessing Game ===
Guess a number between 1 and 100
You have 10 attempts

Enter your guess: 50
Too high! Try again.
Attempts: 1/10

Enter your guess: 25
Too low! Try again.
Attempts: 2/10

Enter your guess: 37
Too high! Try again.
Attempts: 3/10

Enter your guess: 31
Correct! You won!
Attempts: 4/10

=== Game Over ===
Congratulations! You won!
Attempts: 4
Difficulty: Excellent!

Play again? (yes/no): no

=== Statistics ===
Total games: 1
Games won: 1
Win rate: 100.0%

Thanks for playing!
```

---

## 📝 Exercises

### Exercise 1: Temperature Converter

**Objective**: Practice operators and conditional logic

**Task Description**:
Create a program that converts temperature between Celsius and Fahrenheit with validation.

**Acceptance Criteria**:
- [ ] Accepts temperature input
- [ ] Validates input (reasonable range)
- [ ] Converts correctly
- [ ] Displays results with proper formatting

**Starter Code**:
```java
public class TemperatureConverter {
    public static void main(String[] args) {
        // TODO: Accept temperature and unit
        // TODO: Validate input
        // TODO: Convert temperature
        // TODO: Display result
    }
}
```

**Reflection Prompt**:
- What validation is necessary?
- How would you handle invalid input?
- What edge cases should you consider?

### Exercise 2: Multiplication Table Generator

**Objective**: Practice nested loops

**Task Description**:
Create a program that generates multiplication tables using nested loops.

**Acceptance Criteria**:
- [ ] Generates correct multiplication table
- [ ] Properly formatted output
- [ ] Handles user input for table size

**Starter Code**:
```java
public class MultiplicationTable {
    public static void main(String[] args) {
        // TODO: Accept table size
        // TODO: Generate table using nested loops
        // TODO: Display formatted output
    }
}
```

**Reflection Prompt**:
- How would you optimize the output?
- What patterns do you notice?
- How would you extend this to 3D tables?

### Exercise 3: Number Pattern Generator

**Objective**: Practice loops and control flow

**Task Description**:
Create a program that generates various number patterns using loops.

**Acceptance Criteria**:
- [ ] Generates pyramid pattern
- [ ] Generates diamond pattern
- [ ] Generates other patterns
- [ ] Properly formatted

**Starter Code**:
```java
public class PatternGenerator {
    public static void main(String[] args) {
        // TODO: Generate pyramid
        // TODO: Generate diamond
        // TODO: Generate other patterns
    }
}
```

**Reflection Prompt**:
- What's the logic behind each pattern?
- How would you generalize pattern generation?
- What other patterns can you create?

---

## 🧪 Quiz

### Question 1: What is the result of 10 / 3 in Java?

A) 3.333...  
B) 3  
C) 3.0  
D) 4  

**Answer**: B) 3

**Explanation**: Integer division truncates the decimal part. To get 3.333..., you need `(double) 10 / 3` or `10.0 / 3`.

### Question 2: What does the ++ operator do when placed after a variable?

A) Increments before returning value  
B) Increments after returning value  
C) Decrements the value  
D) Returns the incremented value  

**Answer**: B) Increments after returning value

**Explanation**: Post-increment (x++) returns the current value, then increments. Pre-increment (++x) increments first, then returns.

### Question 3: Which statement is true about switch?

A) break is optional  
B) default is required  
C) Without break, execution falls through  
D) Only works with integers  

**Answer**: C) Without break, execution falls through

**Explanation**: Without `break`, the switch continues executing the next case (fall-through). `break` and `default` are optional.

### Question 4: What is the output of this code?

```java
for (int i = 0; i < 3; i++) {
    if (i == 1) continue;
    System.out.print(i);
}
```

A) 012  
B) 02  
C) 01  
D) 0  

**Answer**: B) 02

**Explanation**: When i == 1, `continue` skips to the next iteration, so 1 is not printed.

### Question 5: Which operator has the highest precedence?

A) +  
B) *  
C) ()  
D) &&  

**Answer**: C) ()

**Explanation**: Parentheses have the highest precedence, followed by increment/decrement, then multiplication/division.

---

## 🚀 Advanced Challenge

### Challenge: Advanced Game Features

**Difficulty**: Intermediate

**Objective**: Extend the number guessing game with advanced features

**Description**:
Enhance the game with:
- Difficulty levels (easy: 1-50, medium: 1-100, hard: 1-1000)
- Score calculation based on attempts
- Leaderboard system
- Game history
- Hints system

**Requirements**:
- [ ] Multiple difficulty levels
- [ ] Score calculation
- [ ] Leaderboard (top 5 scores)
- [ ] Game history tracking
- [ ] Hint system (reveal range)

**Hints**:
1. Create a `Difficulty` enum for levels
2. Use a `Score` class to track player performance
3. Use a `Leaderboard` class to manage top scores
4. Implement hint logic with limited hints

**Stretch Goals**:
- [ ] Save leaderboard to file
- [ ] Load leaderboard on startup
- [ ] Player profiles
- [ ] Achievement system

---

## 🏆 Best Practices

### Operator Usage

1. **Use Appropriate Operators**
   - Use `==` for primitives
   - Use `.equals()` for strings
   - Use `&&` and `||` for logical operations

2. **Operator Precedence**
   - Use parentheses for clarity
   - Don't rely on precedence rules
   - Make code self-documenting

3. **Compound Operators**
   - Use `+=`, `-=` for clarity
   - Avoid complex expressions
   - Keep expressions simple

### Control Flow

1. **if-else vs switch**
   - Use switch for multiple discrete values
   - Use if-else for ranges or complex conditions
   - Keep nesting minimal

2. **Loop Selection**
   - Use for loop for known iterations
   - Use while for unknown iterations
   - Use enhanced for for collections

3. **Loop Control**
   - Use break to exit loops
   - Use continue to skip iterations
   - Avoid labeled breaks (use methods instead)

### Code Clarity

1. **Meaningful Conditions**
   ```java
   // ❌ Bad
   if (age >= 18 && hasLicense && !suspended) {
   
   // ✅ Good
   boolean canDrive = age >= 18 && hasLicense && !suspended;
   if (canDrive) {
   ```

2. **Avoid Deep Nesting**
   ```java
   // ❌ Bad: Deep nesting
   if (condition1) {
       if (condition2) {
           if (condition3) {
               // Code
           }
       }
   }
   
   // ✅ Good: Early return
   if (!condition1) return;
   if (!condition2) return;
   if (!condition3) return;
   // Code
   ```

3. **Use Meaningful Variable Names**
   ```java
   // ❌ Bad
   int x = 5;
   if (x > 18) { }
   
   // ✅ Good
   int userAge = 5;
   if (userAge > 18) { }
   ```

---

## 🔗 Next Steps

### Ready for Lab 03?

You've mastered operators and control flow! Next, learn about methods and scope.

**Next Lab**: [Lab 03: Methods & Scope](../03-methods-scope/README.md)

### Additional Resources

- [Java Operators](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/operators.html)
- [Control Flow Statements](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/flow.html)
- [Switch Expressions](https://docs.oracle.com/en/java/javase/17/language/switch-expressions.html)

---

## ✅ Completion Checklist

- [ ] Completed all step-by-step coding tasks
- [ ] Built and tested the number guessing game
- [ ] Solved all exercises
- [ ] Passed the quiz (80%+)
- [ ] Attempted the advanced challenge
- [ ] Reviewed best practices
- [ ] Added project to portfolio
- [ ] Reflected on learning outcomes

---

**Congratulations on completing Lab 02! 🎉**

You've successfully mastered operators and control flow. You can now write programs with complex decision logic and loops. Ready for the next challenge? Move on to [Lab 03: Methods & Scope](../03-methods-scope/README.md).