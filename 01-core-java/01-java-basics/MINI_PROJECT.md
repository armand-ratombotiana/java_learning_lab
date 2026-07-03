# Module 01: Java Basics - Mini Project

**Project Name**: Command-Line ATM Simulator  
**Difficulty Level**: Beginner  
**Estimated Time**: 2-3 hours

---

## 🎯 Objective
Apply fundamental Java concepts including primitive data types, control flow (if/else, switch), loops (while, for), and basic console I/O using the `Scanner` class to build an interactive application.

## 📝 Requirements

### Core Features
1. **Authentication**: 
   - Start with a hardcoded PIN (e.g., `1234`).
   - Prompt the user to enter the PIN. Allow a maximum of 3 attempts before locking the system and exiting.

2. **Main Menu**:
   - Once authenticated, display a menu using a `do-while` loop and `switch` statement:
     1. Check Balance
     2. Deposit
     3. Withdraw
     4. Exit

3. **Operations**:
   - **Check Balance**: Display the current account balance (starting at a default value like $1000.00).
   - **Deposit**: Prompt for an amount. Validate that the amount is strictly positive. Add to the balance.
   - **Withdraw**: Prompt for an amount. Validate that the amount is positive, a multiple of 10, and that the account has sufficient funds. Subtract from the balance.
   - **Exit**: Print a goodbye message and terminate the application cleanly.

### Technical Constraints
- Use appropriate primitive types (e.g., `double` for balance, `int` for menu options).
- Handle invalid input gracefully (e.g., if a user enters a letter instead of a number in the menu).

---

## 💡 Solution Blueprint

1. Create a `main` method.
2. Initialize `Scanner scanner = new Scanner(System.in);`.
3. Set initial state: `double balance = 1000.0;`, `int pin = 1234;`, `int attempts = 0;`.
4. Use a `while` loop for the PIN check.
5. If successful, enter a `do-while` loop for the main menu.
6. Use `switch (choice)` for operations.
7. Remember to close the `Scanner` before exiting.