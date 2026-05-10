# 🎯 Mini-Project: Banking System with OOP Principles

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Inheritance, Polymorphism, Abstraction, Encapsulation, Interfaces, Abstract Classes

---

## Project Structure

```
02-oop/src/main/java/com/learning/project/
├── Main.java
├── model/
│   ├── Account.java (abstract)
│   ├── SavingsAccount.java
│   ├── CheckingAccount.java
│   └── CreditAccount.java
├── service/
│   ├── AccountService.java
│   └── TransactionService.java
├── exception/
│   ├── InsufficientFundsException.java
│   └── InvalidAmountException.java
└── ui/
    └── BankMenu.java
```

---

## Step 1: Define Custom Exceptions

```java
// exception/InsufficientFundsException.java
package com.learning.project.exception;

public class InsufficientFundsException extends Exception {
    private double available;
    private double requested;

    public InsufficientFundsException(double available, double requested) {
        super(String.format("Insufficient funds: available $%.2f, requested $%.2f", 
            available, requested));
        this.available = available;
        this.requested = requested;
    }

    public double getAvailable() { return available; }
    public double getRequested() { return requested; }
}

// exception/InvalidAmountException.java
package com.learning.project.exception;

public class InvalidAmountException extends Exception {
    public InvalidAmountException(String message) {
        super(message);
    }

    public InvalidAmountException(double amount) {
        super(String.format("Invalid amount: $%.2f", amount));
    }
}
```

---

## Step 2: Abstract Account Class (Abstraction + Encapsulation)

```java
// model/Account.java
package com.learning.project.model;

import com.learning.project.exception.InsufficientFundsException;
import com.learning.project.exception.InvalidAmountException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    protected String accountNumber;
    protected String holderName;
    protected double balance;
    protected double interestRate;
    protected LocalDateTime createdAt;
    protected List<Transaction> transactions;

    public Account(String accountNumber, String holderName, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = initialDeposit;
        this.createdAt = LocalDateTime.now();
        this.transactions = new ArrayList<>();
        
        if (initialDeposit > 0) {
            transactions.add(new Transaction("DEPOSIT", initialDeposit, "Initial deposit"));
        }
    }

    // Abstract methods - subclasses must implement
    public abstract void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException;
    public abstract void deposit(double amount) throws InvalidAmountException;
    public abstract double calculateInterest();
    public abstract String getAccountType();

    // Common methods
    public void transfer(Account target, double amount) 
            throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        if (amount > balance) {
            throw new InsufficientFundsException(balance, amount);
        }
        
        this.withdraw(amount);
        target.deposit(amount);
        
        this.transactions.add(new Transaction("TRANSFER_OUT", amount, 
            "Transfer to " + target.accountNumber));
        target.transactions.add(new Transaction("TRANSFER_IN", amount, 
            "Transfer from " + this.accountNumber));
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getHolderName() { return holderName; }
    public double getBalance() { return balance; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    protected void addTransaction(String type, double amount, String description) {
        transactions.add(new Transaction(type, amount, description));
    }

    @Override
    public String toString() {
        return String.format("%s[%s] - %s: $%.2f", 
            getAccountType(), accountNumber, holderName, balance);
    }
}

// Transaction inner class
class Transaction {
    private String type;
    private double amount;
    private String description;
    private LocalDateTime timestamp;

    public Transaction(String type, double amount, String description) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] $%.2f - %s at %s", 
            type, amount, description, timestamp);
    }
}
```

---

## Step 3: Savings Account (Inheritance)

```java
// model/SavingsAccount.java
package com.learning.project.model;

import com.learning.project.exception.InsufficientFundsException;
import com.learning.project.exception.InvalidAmountException;

public class SavingsAccount extends Account {
    private static final double MINIMUM_BALANCE = 100.0;
    private static final double WITHDRAWAL_LIMIT = 10000.0;

    public SavingsAccount(String accountNumber, String holderName, double initialDeposit) {
        super(accountNumber, holderName, initialDeposit);
        this.interestRate = 0.045; // 4.5% APY
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        validateAmount(amount);
        
        if (amount > WITHDRAWAL_LIMIT) {
            throw new InvalidAmountException("Withdrawal limit exceeded: $" + WITHDRAWAL_LIMIT);
        }
        
        double potentialBalance = balance - amount;
        if (potentialBalance < MINIMUM_BALANCE) {
            throw new InsufficientFundsException(balance, amount);
        }
        
        balance -= amount;
        addTransaction("WITHDRAWAL", amount, "Savings withdrawal");
    }

    @Override
    public void deposit(double amount) throws InvalidAmountException {
        validateAmount(amount);
        balance += amount;
        addTransaction("DEPOSIT", amount, "Savings deposit");
    }

    @Override
    public double calculateInterest() {
        // Compound interest: A = P(1 + r/n)^(nt)
        // Simplified annual interest
        return balance * interestRate;
    }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }

    private void validateAmount(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
    }

    public double getMinimumBalance() {
        return MINIMUM_BALANCE;
    }
}
```

---

## Step 4: Checking Account (Polymorphism)

```java
// model/CheckingAccount.java
package com.learning.project.model;

import com.learning.project.exception.InsufficientFundsException;
import com.learning.project.exception.InvalidAmountException;

public class CheckingAccount extends Account {
    private double overdraftLimit;
    private double overdraftUsed;

    public CheckingAccount(String accountNumber, String holderName, 
                           double initialDeposit, double overdraftLimit) {
        super(accountNumber, holderName, initialDeposit);
        this.interestRate = 0.001; // 0.1% APY
        this.overdraftLimit = overdraftLimit;
        this.overdraftUsed = 0;
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        validateAmount(amount);
        
        double available = balance + (overdraftLimit - overdraftUsed);
        if (amount > available) {
            throw new InsufficientFundsException(available, amount);
        }
        
        if (amount <= balance) {
            balance -= amount;
        } else {
            double overdraftNeeded = amount - balance;
            balance = 0;
            overdraftUsed += overdraftNeeded;
        }
        
        addTransaction("WITHDRAWAL", amount, "Check withdrawal");
    }

    @Override
    public void deposit(double amount) throws InvalidAmountException {
        validateAmount(amount);
        
        // First cover overdraft
        if (overdraftUsed > 0) {
            if (amount >= overdraftUsed) {
                amount -= overdraftUsed;
                overdraftUsed = 0;
            } else {
                overdraftUsed -= amount;
                amount = 0;
            }
        }
        
        balance += amount;
        addTransaction("DEPOSIT", amount, "Check deposit");
    }

    @Override
    public double calculateInterest() {
        double interest = balance * interestRate;
        double overdraftInterest = overdraftUsed * 0.015; // 1.5% on overdraft
        return interest - overdraftInterest;
    }

    @Override
    public String getAccountType() {
        return "CHECKING";
    }

    private void validateAmount(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
    }

    public double getAvailableBalance() {
        return balance + (overdraftLimit - overdraftUsed);
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }
}
```

---

## Step 5: Credit Account (Interface Implementation)

```java
// model/CreditAccount.java
package com.learning.project.model;

import com.learning.project.exception.InsufficientFundsException;
import com.learning.project.exception.InvalidAmountException;

public class CreditAccount extends Account implements Rewardsable {
    private double creditLimit;
    private double creditUsed;
    private int rewardPoints;

    public CreditAccount(String accountNumber, String holderName, double creditLimit) {
        super(accountNumber, holderName, 0); // Credit cards start with 0 balance
        this.creditLimit = creditLimit;
        this.creditUsed = 0;
        this.rewardPoints = 0;
        this.interestRate = 0.199; // 19.9% APR
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        validateAmount(amount);
        
        if (amount > (creditLimit - creditUsed)) {
            throw new InsufficientFundsException(creditLimit - creditUsed, amount);
        }
        
        creditUsed += amount;
        addTransaction("CHARGE", amount, "Credit card charge");
    }

    @Override
    public void deposit(double amount) throws InvalidAmountException {
        validateAmount(amount);
        
        // Payment reduces credit used
        creditUsed = Math.max(0, creditUsed - amount);
        
        // Earn rewards on payments
        addRewardPoints((int)(amount / 10));
        
        addTransaction("PAYMENT", amount, "Credit card payment");
    }

    @Override
    public double calculateInterest() {
        // Interest on carried balance
        return creditUsed * (interestRate / 12);
    }

    @Override
    public String getAccountType() {
        return "CREDIT";
    }

    // Interface implementation
    @Override
    public void addRewardPoints(int points) {
        this.rewardPoints += points;
    }

    @Override
    public int getRewardPoints() {
        return rewardPoints;
    }

    @Override
    public String getRewardsDescription() {
        return rewardPoints + " points available";
    }

    private void validateAmount(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
    }

    public double getAvailableCredit() {
        return creditLimit - creditUsed;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        validateAmount(amount);
        
        if (amount > (creditLimit - creditUsed)) {
            throw new InsufficientFundsException(creditLimit - creditUsed, amount);
        }
        
        creditUsed += amount;
        addTransaction("CHARGE", amount, "Credit card charge");
    }
}

// Interface
interface Rewardsable {
    void addRewardPoints(int points);
    int getRewardPoints();
    String getRewardsDescription();
}
```

---

## Step 6: Account Service (Polymorphic Operations)

```java
// service/AccountService.java
package com.learning.project.service;

import com.learning.project.model.*;
import com.learning.project.exception.*;
import java.util.*;

public class AccountService {
    private Map<String, Account> accounts;
    private int accountCounter;

    public AccountService() {
        this.accounts = new HashMap<>();
        this.accountCounter = 1000;
    }

    public String createSavingsAccount(String holderName, double initialDeposit) 
            throws InvalidAmountException {
        String accountNumber = "SAV-" + (accountCounter++);
        SavingsAccount account = new SavingsAccount(accountNumber, holderName, initialDeposit);
        accounts.put(accountNumber, account);
        return accountNumber;
    }

    public String createCheckingAccount(String holderName, double initialDeposit, 
                                        double overdraftLimit) throws InvalidAmountException {
        String accountNumber = "CHK-" + (accountCounter++);
        CheckingAccount account = new CheckingAccount(accountNumber, holderName, 
            initialDeposit, overdraftLimit);
        accounts.put(accountNumber, account);
        return accountNumber;
    }

    public String createCreditAccount(String holderName, double creditLimit) {
        String accountNumber = "CRD-" + (accountCounter++);
        CreditAccount account = new CreditAccount(accountNumber, holderName, creditLimit);
        accounts.put(accountNumber, account);
        return accountNumber;
    }

    public Account findAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public void transfer(String from, String to, double amount) 
            throws InvalidAmountException, InsufficientFundsException {
        Account source = findAccount(from);
        Account target = findAccount(to);
        
        if (source == null || target == null) {
            throw new InvalidAmountException("Account not found");
        }
        
        source.transfer(target, amount);
    }

    public void applyInterestToAll() {
        for (Account account : accounts.values()) {
            double interest = account.calculateInterest();
            try {
                account.deposit(interest);
            } catch (InvalidAmountException e) {
                // Should not happen for positive interest
            }
        }
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public List<Account> getAccountsByType(String type) {
        List<Account> result = new ArrayList<>();
        for (Account account : accounts.values()) {
            if (account.getAccountType().equalsIgnoreCase(type)) {
                result.add(account);
            }
        }
        return result;
    }

    public double getTotalDeposits() {
        return accounts.values().stream()
            .mapToDouble(Account::getBalance)
            .sum();
    }

    public Map<String, Double> getSummaryByType() {
        Map<String, Double> summary = new LinkedHashMap<>();
        
        for (Account account : accounts.values()) {
            String type = account.getAccountType();
            summary.put(type, summary.getOrDefault(type, 0.0) + account.getBalance());
        }
        
        return summary;
    }
}
```

---

## Step 7: Bank Menu UI

```java
// ui/BankMenu.java
package com.learning.project.ui;

import com.learning.project.model.*;
import com.learning.project.service.*;
import com.learning.project.exception.*;
import java.util.Scanner;

public class BankMenu {
    private Scanner scanner;
    private AccountService service;
    private boolean running;

    public BankMenu() {
        this.scanner = new Scanner(System.in);
        this.service = new AccountService();
        this.running = true;
    }

    public void start() {
        System.out.println("\n🏦 WELCOME TO OOP BANK");
        System.out.println("======================");
        
        // Create sample accounts
        createSampleAccounts();
        
        while (running) {
            displayMenu();
            handleChoice(getChoice());
        }
    }

    private void createSampleAccounts() {
        try {
            service.createSavingsAccount("John Doe", 5000);
            service.createCheckingAccount("Jane Smith", 2000, 500);
            service.createCreditAccount("Bob Wilson", 10000);
            System.out.println("Sample accounts created!\n");
        } catch (InvalidAmountException e) {
            System.out.println("Error creating sample: " + e.getMessage());
        }
    }

    private void displayMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Create Savings Account");
        System.out.println("2. Create Checking Account");
        System.out.println("3. Create Credit Account");
        System.out.println("4. Deposit");
        System.out.println("5. Withdraw");
        System.out.println("6. Transfer");
        System.out.println("7. View Account");
        System.out.println("8. View All Accounts");
        System.out.println("9. Apply Interest");
        System.out.println("10. Bank Summary");
        System.out.println("11. Exit");
        System.out.print("\nChoice: ");
    }

    private int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void handleChoice(int choice) {
        try {
            switch (choice) {
                case 1 -> createSavings();
                case 2 -> createChecking();
                case 3 -> createCredit();
                case 4 -> doDeposit();
                case 5 -> doWithdraw();
                case 6 -> doTransfer();
                case 7 -> viewAccount();
                case 8 -> viewAllAccounts();
                case 9 -> applyInterest();
                case 10 -> showSummary();
                case 11 -> { System.out.println("Goodbye!"); running = false; }
                default -> System.out.println("Invalid choice!");
            }
        } catch (InvalidAmountException | InsufficientFundsException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void createSavings() {
        System.out.print("Holder Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Initial Deposit: ");
        double amount = getDouble();
        
        try {
            String accNum = service.createSavingsAccount(name, amount);
            System.out.println("✓ Savings account created: " + accNum);
        } catch (InvalidAmountException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void createChecking() {
        System.out.print("Holder Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Initial Deposit: ");
        double amount = getDouble();
        System.out.print("Overdraft Limit: ");
        double overdraft = getDouble();
        
        try {
            String accNum = service.createCheckingAccount(name, amount, overdraft);
            System.out.println("✓ Checking account created: " + accNum);
        } catch (InvalidAmountException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void createCredit() {
        System.out.print("Holder Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Credit Limit: ");
        double limit = getDouble();
        
        String accNum = service.createCreditAccount(name, limit);
        System.out.println("✓ Credit account created: " + accNum);
    }

    private void doDeposit() throws InvalidAmountException {
        String accNum = promptAccount();
        Account acc = service.findAccount(accNum);
        if (acc == null) {
            System.out.println("Account not found!");
            return;
        }
        
        System.out.print("Amount: ");
        double amount = getDouble();
        acc.deposit(amount);
        System.out.println("✓ Deposited $" + amount);
    }

    private void doWithdraw() throws InsufficientFundsException, InvalidAmountException {
        String accNum = promptAccount();
        Account acc = service.findAccount(accNum);
        if (acc == null) {
            System.out.println("Account not found!");
            return;
        }
        
        System.out.print("Amount: ");
        double amount = getDouble();
        acc.withdraw(amount);
        System.out.println("✓ Withdrawn $" + amount);
    }

    private void doTransfer() throws InsufficientFundsException, InvalidAmountException {
        System.out.print("From Account: ");
        String from = scanner.nextLine().trim();
        System.out.print("To Account: ");
        String to = scanner.nextLine().trim();
        System.out.print("Amount: ");
        double amount = getDouble();
        
        service.transfer(from, to, amount);
        System.out.println("✓ Transferred $" + amount);
    }

    private void viewAccount() {
        String accNum = promptAccount();
        Account acc = service.findAccount(accNum);
        if (acc == null) {
            System.out.println("Account not found!");
            return;
        }
        
        System.out.println("\n" + "=".repeat(40));
        System.out.println("ACCOUNT DETAILS");
        System.out.println("=".repeat(40));
        System.out.println("Type: " + acc.getAccountType());
        System.out.println("Number: " + acc.getAccountNumber());
        System.out.println("Holder: " + acc.getHolderName());
        System.out.printf("Balance: $%.2f%n", acc.getBalance());
        
        if (acc instanceof CheckingAccount) {
            CheckingAccount ca = (CheckingAccount) acc;
            System.out.printf("Available: $%.2f%n", ca.getAvailableBalance());
        } else if (acc instanceof CreditAccount) {
            CreditAccount ca = (CreditAccount) acc;
            System.out.printf("Credit Limit: $%.2f%n", ca.getCreditLimit());
            System.out.printf("Available Credit: $%.2f%n", ca.getAvailableCredit());
            System.out.println("Rewards: " + ca.getRewardsDescription());
        }
        
        System.out.println("\n--- Recent Transactions ---");
        for (Transaction t : acc.getTransactions().subList(
                Math.max(0, acc.getTransactions().size() - 5), 
                acc.getTransactions().size())) {
            System.out.println(t);
        }
    }

    private void viewAllAccounts() {
        System.out.println("\n--- ALL ACCOUNTS ---");
        for (Account acc : service.getAllAccounts()) {
            System.out.println(acc);
        }
    }

    private void applyInterest() {
        service.applyInterestToAll();
        System.out.println("✓ Interest applied to all accounts");
    }

    private void showSummary() {
        System.out.println("\n=== BANK SUMMARY ===");
        System.out.println("Total Deposits: $" + service.getTotalDeposits());
        System.out.println("\nBy Account Type:");
        for (var entry : service.getSummaryByType().entrySet()) {
            System.out.printf("  %s: $%.2f%n", entry.getKey(), entry.getValue());
        }
    }

    private String promptAccount() {
        System.out.print("Account Number: ");
        return scanner.nextLine().trim();
    }

    private double getDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static void main(String[] args) {
        new BankMenu().start();
    }
}

// Simple Transaction class for the UI
class Transaction {
    private String type;
    private double amount;
    private String description;
    
    public Transaction(String type, double amount, String description) {
        this.type = type;
        this.amount = amount;
        this.description = description;
    }
    
    @Override
    public String toString() {
        return String.format("[%s] $%.2f - %s", type, amount, description);
    }
}
```

---

## Running the Project

```bash
cd 02-oop
javac -d out src/com/learning/project/**/*.java src/com/learning/project/*.java
java -cp out com.learning.project.ui.BankMenu
```

---

## OOP Concepts Demonstrated

| Concept | Implementation |
|---------|----------------|
| **Abstraction** | `Account` abstract class with abstract methods |
| **Encapsulation** | Private fields with public getters |
| **Inheritance** | `SavingsAccount`, `CheckingAccount`, `CreditAccount` extend `Account` |
| **Polymorphism** | `AccountService` works with any `Account` subclass |
| **Interface** | `Rewardsable` implemented by `CreditAccount` |
| **Exception Handling** | Custom exceptions for banking errors |

---

---

# 🚀 Real-World Project: Full-Featured Banking System

## Project Overview

**Duration**: 15-20 hours  
**Difficulty**: Advanced  
**Concepts Used**: Design Patterns, Transaction Management, Interest Calculation, Account Types, Reporting

This project implements a production-ready banking system with multiple account types, transaction history, interest calculations, and reporting capabilities.

---

## Project Structure

```
02-oop/src/main/java/com/learning/project/
├── Main.java
├── model/
│   ├── Account.java (abstract)
│   ├── SavingsAccount.java
│   ├── CheckingAccount.java
│   ├── CreditAccount.java
│   └── Transaction.java
├── service/
│   ├── AccountService.java
│   ├── TransactionService.java
│   ├── InterestCalculator.java
│   └── ReportService.java
├── repository/
│   └── AccountRepository.java
├── exception/
│   ├── InsufficientFundsException.java
│   ├── InvalidAmountException.java
│   └── AccountNotFoundException.java
├── factory/
│   └── AccountFactory.java
├── observer/
│   ├── AccountObserver.java
│   └── NotificationService.java
├── ui/
│   └── BankMenu.java
└── util/
    └── DateUtils.java
```

---

## Step 1: Account Not Found Exception

```java
// exception/AccountNotFoundException.java
package com.learning.project.exception;

public class AccountNotFoundException extends Exception {
    private String accountNumber;
    
    public AccountNotFoundException(String accountNumber) {
        super("Account not found: " + accountNumber);
        this.accountNumber = accountNumber;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
}
```

---

## Step 2: Transaction Model with Full Details

```java
// model/Transaction.java
package com.learning.project.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String id;
    private String accountNumber;
    private TransactionType type;
    private double amount;
    private String description;
    private LocalDateTime timestamp;
    private double balanceAfter;
    
    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT, 
        INTEREST, FEE, PAYMENT, CHARGE
    }
    
    public Transaction(String accountNumber, TransactionType type, 
                       double amount, String description, double balanceAfter) {
        this.id = generateId();
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.balanceAfter = balanceAfter;
    }
    
    private String generateId() {
        return "TXN-" + System.currentTimeMillis();
    }
    
    public String getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getBalanceAfter() { return balanceAfter; }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %s: $%.2f - %s | Balance: $%.2f",
            id, type, amount, description, balanceAfter);
    }
}
```

---

## Step 3: Enhanced Abstract Account

```java
// model/Account.java
package com.learning.project.model;

import com.learning.project.exception.InsufficientFundsException;
import com.learning.project.exception.InvalidAmountException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    protected String accountNumber;
    protected String holderName;
    protected double balance;
    protected double interestRate;
    protected LocalDateTime createdAt;
    protected List<Transaction> transactions;
    protected boolean active;
    
    public Account(String accountNumber, String holderName, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = initialDeposit;
        this.createdAt = LocalDateTime.now();
        this.transactions = new ArrayList<>();
        this.active = true;
        
        if (initialDeposit > 0) {
            addTransaction(Transaction.TransactionType.DEPOSIT, 
                initialDeposit, "Initial deposit");
        }
    }
    
    public abstract void withdraw(double amount) 
            throws InsufficientFundsException, InvalidAmountException;
    public abstract void deposit(double amount) 
            throws InvalidAmountException;
    public abstract double calculateInterest();
    public abstract String getAccountType();
    public abstract double getAvailableBalance();
    
    public void transfer(Account target, double amount) 
            throws InsufficientFundsException, InvalidAmountException {
        validateAmount(amount);
        
        if (amount > getAvailableBalance()) {
            throw new InsufficientFundsException(getAvailableBalance(), amount);
        }
        
        this.withdraw(amount);
        target.deposit(amount);
        
        addTransaction(Transaction.TransactionType.TRANSFER_OUT, amount,
            "Transfer to " + target.accountNumber);
        target.addTransaction(Transaction.TransactionType.TRANSFER_IN, amount,
            "Transfer from " + this.accountNumber);
    }
    
    protected void addTransaction(Transaction.TransactionType type, 
                                  double amount, String description) {
        transactions.add(new Transaction(accountNumber, type, amount, 
            description, balance));
    }
    
    protected void validateAmount(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
    }
    
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
    
    public List<Transaction> getTransactions(int limit) {
        int size = transactions.size();
        int from = Math.max(0, size - limit);
        return new ArrayList<>(transactions.subList(from, size));
    }
    
    public String getAccountNumber() { return accountNumber; }
    public String getHolderName() { return holderName; }
    public double getBalance() { return balance; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isActive() { return active; }
    
    public void deactivate() { this.active = false; }
    public void activate() { this.active = true; }
    
    @Override
    public String toString() {
        return String.format("%s[%s] - %s: $%.2f (Active: %s)", 
            getAccountType(), accountNumber, holderName, balance, active);
    }
}
```

---

## Step 4: Savings Account with Tiered Interest

```java
// model/SavingsAccount.java
package com.learning.project.model;

import com.learning.project.exception.InsufficientFundsException;
import com.learning.project.exception.InvalidAmountException;

public class SavingsAccount extends Account {
    private static final double MINIMUM_BALANCE = 100.0;
    private static final double WITHDRAWAL_LIMIT = 10000.0;
    private static final double HIGH_BALANCE_THRESHOLD = 50000.0;
    
    public SavingsAccount(String accountNumber, String holderName, double initialDeposit) {
        super(accountNumber, holderName, initialDeposit);
        updateInterestRate();
    }
    
    private void updateInterestRate() {
        if (balance >= HIGH_BALANCE_THRESHOLD) {
            this.interestRate = 0.055; // 5.5% for high balance
        } else {
            this.interestRate = 0.045; // 4.5% standard
        }
    }
    
    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        validateAmount(amount);
        
        if (amount > WITHDRAWAL_LIMIT) {
            throw new InvalidAmountException("Withdrawal limit: $" + WITHDRAWAL_LIMIT);
        }
        
        double potentialBalance = balance - amount;
        if (potentialBalance < MINIMUM_BALANCE) {
            throw new InsufficientFundsException(balance, amount);
        }
        
        balance -= amount;
        updateInterestRate();
        addTransaction(Transaction.TransactionType.WITHDRAWAL, amount, "Savings withdrawal");
    }
    
    @Override
    public void deposit(double amount) throws InvalidAmountException {
        validateAmount(amount);
        balance += amount;
        updateInterestRate();
        addTransaction(Transaction.TransactionType.DEPOSIT, amount, "Savings deposit");
    }
    
    @Override
    public double calculateInterest() {
        double monthlyRate = interestRate / 12;
        return balance * monthlyRate;
    }
    
    @Override
    public String getAccountType() {
        return "SAVINGS";
    }
    
    @Override
    public double getAvailableBalance() {
        return Math.max(0, balance - MINIMUM_BALANCE);
    }
    
    public double getMinimumBalance() {
        return MINIMUM_BALANCE;
    }
}
```

---

## Step 5: Checking Account with Overdraft Protection

```java
// model/CheckingAccount.java
package com.learning.project.model;

import com.learning.project.exception.InsufficientFundsException;
import com.learning.project.exception.InvalidAmountException;

public class CheckingAccount extends Account {
    private double overdraftLimit;
    private double overdraftUsed;
    private double monthlyFee;
    private boolean hasOverdraftProtection;
    
    public CheckingAccount(String accountNumber, String holderName, 
                           double initialDeposit, double overdraftLimit) {
        super(accountNumber, holderName, initialDeposit);
        this.overdraftLimit = overdraftLimit;
        this.overdraftUsed = 0;
        this.interestRate = 0.001;
        this.monthlyFee = 10.0;
        this.hasOverdraftProtection = overdraftLimit > 0;
    }
    
    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        validateAmount(amount);
        
        double available = getAvailableBalance();
        if (amount > available) {
            throw new InsufficientFundsException(available, amount);
        }
        
        if (amount <= balance) {
            balance -= amount;
        } else {
            double overdraftNeeded = amount - balance;
            balance = 0;
            overdraftUsed += overdraftNeeded;
        }
        
        addTransaction(Transaction.TransactionType.WITHDRAWAL, amount, "Check withdrawal");
    }
    
    @Override
    public void deposit(double amount) throws InvalidAmountException {
        validateAmount(amount);
        
        if (overdraftUsed > 0) {
            if (amount >= overdraftUsed) {
                amount -= overdraftUsed;
                overdraftUsed = 0;
            } else {
                overdraftUsed -= amount;
                amount = 0;
            }
        }
        
        balance += amount;
        addTransaction(Transaction.TransactionType.DEPOSIT, amount, "Check deposit");
    }
    
    @Override
    public double calculateInterest() {
        double creditInterest = balance * interestRate;
        double debitInterest = overdraftUsed * 0.015;
        return creditInterest - debitInterest;
    }
    
    @Override
    public String getAccountType() {
        return "CHECKING";
    }
    
    @Override
    public double getAvailableBalance() {
        return balance + (overdraftLimit - overdraftUsed);
    }
    
    public double getOverdraftLimit() {
        return overdraftLimit;
    }
    
    public double getOverdraftUsed() {
        return overdraftUsed;
    }
    
    public double getMonthlyFee() {
        return monthlyFee;
    }
}
```

---

## Step 6: Credit Account with Rewards

```java
// model/CreditAccount.java
package com.learning.project.model;

import com.learning.project.exception.InsufficientFundsException;
import com.learning.project.exception.InvalidAmountException;

public class CreditAccount extends Account implements Rewardsable {
    private double creditLimit;
    private double creditUsed;
    private int rewardPoints;
    private double annualFee;
    private int gracePeriodDays = 25;
    
    public CreditAccount(String accountNumber, String holderName, double creditLimit) {
        super(accountNumber, holderName, 0);
        this.creditLimit = creditLimit;
        this.creditUsed = 0;
        this.rewardPoints = 0;
        this.interestRate = 0.199;
        this.annualFee = 50.0;
    }
    
    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        validateAmount(amount);
        
        if (amount > (creditLimit - creditUsed)) {
            throw new InsufficientFundsException(creditLimit - creditUsed, amount);
        }
        
        creditUsed += amount;
        addTransaction(Transaction.TransactionType.CHARGE, amount, "Credit card charge");
    }
    
    @Override
    public void deposit(double amount) throws InvalidAmountException {
        validateAmount(amount);
        
        creditUsed = Math.max(0, creditUsed - amount);
        addRewardPoints((int)(amount / 10));
        
        addTransaction(Transaction.TransactionType.PAYMENT, amount, "Credit card payment");
    }
    
    @Override
    public double calculateInterest() {
        return creditUsed * (interestRate / 12);
    }
    
    @Override
    public String getAccountType() {
        return "CREDIT";
    }
    
    @Override
    public double getAvailableBalance() {
        return creditLimit - creditUsed;
    }
    
    @Override
    public double getBalance() {
        return creditUsed;
    }
    
    @Override
    public void addRewardPoints(int points) {
        this.rewardPoints += points;
    }
    
    @Override
    public int getRewardPoints() {
        return rewardPoints;
    }
    
    @Override
    public String getRewardsDescription() {
        return String.format("%d points (%.2f credit)", 
            rewardPoints, rewardPoints / 100.0);
    }
    
    public double getCreditLimit() {
        return creditLimit;
    }
    
    public double getCreditUsed() {
        return creditUsed;
    }
    
    public double getAnnualFee() {
        return annualFee;
    }
    
    public boolean isWithinGracePeriod() {
        return true; // Simplified
    }
}
```

---

## Step 7: Factory Pattern for Account Creation

```java
// factory/AccountFactory.java
package com.learning.project.factory;

import com.learning.project.model.*;
import com.learning.project.exception.InvalidAmountException;

public class AccountFactory {
    private static int counter = 1000;
    
    public static Account createSavingsAccount(String holderName, double initialDeposit) 
            throws InvalidAmountException {
        if (initialDeposit < 100) {
            throw new InvalidAmountException("Minimum deposit for savings: $100");
        }
        return new SavingsAccount("SAV-" + (counter++), holderName, initialDeposit);
    }
    
    public static Account createCheckingAccount(String holderName, double initialDeposit,
                                                 double overdraftLimit) 
            throws InvalidAmountException {
        if (overdraftLimit < 0 || overdraftLimit > 10000) {
            throw new InvalidAmountException("Overdraft limit must be $0-$10,000");
        }
        return new CheckingAccount("CHK-" + (counter++), holderName, initialDeposit, overdraftLimit);
    }
    
    public static Account createCreditAccount(String holderName, double creditLimit) {
        if (creditLimit < 1000 || creditLimit > 100000) {
            creditLimit = Math.max(1000, Math.min(100000, creditLimit));
        }
        return new CreditAccount("CRD-" + (counter++), holderName, creditLimit);
    }
    
    public static Account createFromType(String type, String holderName, 
                                         double... params) throws InvalidAmountException {
        return switch (type.toUpperCase()) {
            case "SAVINGS", "SAV" -> createSavingsAccount(holderName, 
                params.length > 0 ? params[0] : 100);
            case "CHECKING", "CHK" -> createCheckingAccount(holderName,
                params.length > 0 ? params[0] : 0,
                params.length > 1 ? params[1] : 0);
            case "CREDIT", "CRD" -> createCreditAccount(holderName,
                params.length > 0 ? params[0] : 5000);
            default -> throw new InvalidAmountException("Unknown account type: " + type);
        };
    }
}
```

---

## Step 8: Observer Pattern for Notifications

```java
// observer/AccountObserver.java
package com.learning.project.observer;

import com.learning.project.model.Account;
import com.learning.project.model.Transaction;

public interface AccountObserver {
    void onTransaction(Account account, Transaction transaction);
    void onLowBalance(Account account, double threshold);
    void onLargeWithdrawal(Account account, double amount);
}

// observer/NotificationService.java
package com.learning.project.observer;

import com.learning.project.model.Account;
import com.learning.project.model.Transaction;
import java.util.ArrayList;
import java.util.List;

public class NotificationService implements AccountObserver {
    private List<String> notifications = new ArrayList<>();
    
    @Override
    public void onTransaction(Account account, Transaction transaction) {
        String msg = String.format("[%s] %s: $%.2f - %s",
            account.getAccountNumber(),
            transaction.getType(),
            transaction.getAmount(),
            transaction.getDescription());
        notifications.add(msg);
    }
    
    @Override
    public void onLowBalance(Account account, double threshold) {
        if (account.getBalance() < threshold) {
            String msg = String.format("LOW BALANCE ALERT: %s has $%.2f (threshold: $%.2f)",
                account.getAccountNumber(), account.getBalance(), threshold);
            notifications.add(msg);
        }
    }
    
    @Override
    public void onLargeWithdrawal(Account account, double amount) {
        if (amount > 5000) {
            String msg = String.format("LARGE WITHDRAWAL: %s withdrew $%.2f",
                account.getAccountNumber(), amount);
            notifications.add(msg);
        }
    }
    
    public List<String> getNotifications() {
        return new ArrayList<>(notifications);
    }
    
    public void clear() {
        notifications.clear();
    }
}
```

---

## Step 9: Repository with In-Memory Storage

```java
// repository/AccountRepository.java
package com.learning.project.repository;

import com.learning.project.model.Account;
import com.learning.project.observer.NotificationService;
import java.util.*;
import java.util.stream.Collectors;

public class AccountRepository {
    private Map<String, Account> accounts;
    private NotificationService notifier;
    private int accountCounter;
    
    public AccountRepository() {
        this.accounts = new HashMap<>();
        this.notifier = new NotificationService();
        this.accountCounter = 1000;
    }
    
    public String createAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
        return account.getAccountNumber();
    }
    
    public Account findAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }
    
    public List<Account> findAll() {
        return new ArrayList<>(accounts.values());
    }
    
    public List<Account> findByType(String type) {
        return accounts.values().stream()
            .filter(a -> a.getAccountType().equalsIgnoreCase(type))
            .collect(Collectors.toList());
    }
    
    public List<Account> findByHolder(String holderName) {
        return accounts.values().stream()
            .filter(a -> a.getHolderName().toLowerCase()
                .contains(holderName.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    public boolean deleteAccount(String accountNumber) {
        return accounts.remove(accountNumber) != null;
    }
    
    public double getTotalBalance() {
        return accounts.values().stream()
            .mapToDouble(Account::getBalance)
            .sum();
    }
    
    public Map<String, Double> getBalanceByType() {
        return accounts.values().stream()
            .collect(Collectors.groupingBy(
                Account::getAccountType,
                Collectors.summingDouble(Account::getBalance)
            ));
    }
    
    public void registerObserver(NotificationService observer) {
        this.notifier = observer;
    }
    
    public NotificationService getNotifier() {
        return notifier;
    }
}
```

---

## Step 10: Report Service

```java
// service/ReportService.java
package com.learning.project.service;

import com.learning.project.model.*;
import com.learning.project.repository.AccountRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {
    private AccountRepository repository;
    
    public ReportService(AccountRepository repository) {
        this.repository = repository;
    }
    
    public String generateAccountStatement(String accountNumber) {
        Account account = repository.findAccount(accountNumber);
        if (account == null) return "Account not found";
        
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(50)).append("\n");
        sb.append("ACCOUNT STATEMENT\n");
        sb.append("=".repeat(50)).append("\n");
        sb.append(String.format("Account: %s\n", accountNumber));
        sb.append(String.format("Holder: %s\n", account.getHolderName()));
        sb.append(String.format("Type: %s\n", account.getAccountType()));
        sb.append(String.format("Balance: $%.2f\n", account.getBalance()));
        sb.append(String.format("Available: $%.2f\n", account.getAvailableBalance()));
        
        sb.append("\n--- Recent Transactions ---\n");
        for (Transaction tx : account.getTransactions(10)) {
            sb.append(tx).append("\n");
        }
        
        return sb.toString();
    }
    
    public String generateBankSummary() {
        List<Account> accounts = repository.findAll();
        
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(50)).append("\n");
        sb.append("BANK SUMMARY REPORT\n");
        sb.append("=".repeat(50)).append("\n");
        
        double totalBalance = repository.getTotalBalance();
        sb.append(String.format("Total Deposits: $%.2f\n", totalBalance));
        sb.append(String.format("Total Accounts: %d\n", accounts.size()));
        
        sb.append("\n--- By Account Type ---\n");
        for (var entry : repository.getBalanceByType().entrySet()) {
            long count = accounts.stream()
                .filter(a -> a.getAccountType().equals(entry.getKey()))
                .count();
            sb.append(String.format("%s: $%.2f (%d accounts)\n", 
                entry.getKey(), entry.getValue(), count));
        }
        
        return sb.toString();
    }
    
    public String generateInterestReport() {
        List<Account> accounts = repository.findAll();
        
        StringBuilder sb = new StringBuilder();
        sb.append("INTEREST CALCULATION REPORT\n");
        sb.append("=".repeat(50)).append("\n");
        
        double totalInterest = 0;
        for (Account account : accounts) {
            double interest = account.calculateInterest();
            totalInterest += interest;
            sb.append(String.format("%s: $%.2f/month\n", 
                account.getAccountNumber(), interest));
        }
        
        sb.append(String.format("\nTotal Monthly Interest: $%.2f\n", totalInterest));
        
        return sb.toString();
    }
}
```

---

## Step 11: Enhanced Menu

```java
// ui/BankMenu.java
package com.learning.project.ui;

import com.learning.project.model.*;
import com.learning.project.repository.AccountRepository;
import com.learning.project.service.ReportService;
import com.learning.project.factory.AccountFactory;
import com.learning.project.exception.*;
import java.util.*;

public class BankMenu {
    private Scanner scanner;
    private AccountRepository repository;
    private ReportService reportService;
    private boolean running;
    
    public BankMenu() {
        this.scanner = new Scanner(System.in);
        this.repository = new AccountRepository();
        this.reportService = new ReportService(repository);
        this.running = true;
    }
    
    public void start() {
        System.out.println("\n🏦 WELCOME TO OOP BANK v2.0");
        System.out.println("============================");
        createSampleData();
        
        while (running) {
            displayMenu();
            handleChoice(getChoice());
        }
    }
    
    private void createSampleData() {
        try {
            repository.createAccount(
                AccountFactory.createSavingsAccount("John Doe", 10000));
            repository.createAccount(
                AccountFactory.createCheckingAccount("Jane Smith", 5000, 1000));
            repository.createAccount(
                AccountFactory.createCreditAccount("Bob Wilson", 15000));
            System.out.println("Sample accounts created!\n");
        } catch (InvalidAmountException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void displayMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Create Savings Account");
        System.out.println("2. Create Checking Account");
        System.out.println("3. Create Credit Account");
        System.out.println("4. Deposit");
        System.out.println("5. Withdraw");
        System.out.println("6. Transfer");
        System.out.println("7. View Account Details");
        System.out.println("8. View All Accounts");
        System.out.println("9. Account Statement");
        System.out.println("10. Bank Summary Report");
        System.out.println("11. Interest Report");
        System.out.println("12. Delete Account");
        System.out.println("13. Exit");
        System.out.print("\nChoice: ");
    }
    
    private int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private void handleChoice(int choice) {
        try {
            switch (choice) {
                case 1 -> createSavings();
                case 2 -> createChecking();
                case 3 -> createCredit();
                case 4 -> doDeposit();
                case 5 -> doWithdraw();
                case 6 -> doTransfer();
                case 7 -> viewAccount();
                case 8 -> viewAllAccounts();
                case 9 -> accountStatement();
                case 10 -> bankSummary();
                case 11 -> interestReport();
                case 12 -> deleteAccount();
                case 13 -> { System.out.println("Goodbye!"); running = false; }
                default -> System.out.println("Invalid choice!");
            }
        } catch (InvalidAmountException | InsufficientFundsException | 
                 AccountNotFoundException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void createSavings() throws InvalidAmountException {
        System.out.print("Holder Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Initial Deposit: ");
        double amount = getDouble();
        
        Account acc = AccountFactory.createSavingsAccount(name, amount);
        repository.createAccount(acc);
        System.out.println("✓ Savings account created: " + acc.getAccountNumber());
    }
    
    private void createChecking() throws InvalidAmountException {
        System.out.print("Holder Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Initial Deposit: ");
        double amount = getDouble();
        System.out.print("Overdraft Limit: ");
        double overdraft = getDouble();
        
        Account acc = AccountFactory.createCheckingAccount(name, amount, overdraft);
        repository.createAccount(acc);
        System.out.println("✓ Checking account created: " + acc.getAccountNumber());
    }
    
    private void createCredit() {
        System.out.print("Holder Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Credit Limit: ");
        double limit = getDouble();
        
        Account acc = AccountFactory.createCreditAccount(name, limit);
        repository.createAccount(acc);
        System.out.println("✓ Credit account created: " + acc.getAccountNumber());
    }
    
    private void doDeposit() throws InvalidAmountException, AccountNotFoundException {
        String accNum = promptAccount();
        Account acc = repository.findAccount(accNum);
        if (acc == null) throw new AccountNotFoundException(accNum);
        
        System.out.print("Amount: ");
        double amount = getDouble();
        acc.deposit(amount);
        System.out.println("✓ Deposited $" + amount);
    }
    
    private void doWithdraw() throws InsufficientFundsException, 
                                     InvalidAmountException, AccountNotFoundException {
        String accNum = promptAccount();
        Account acc = repository.findAccount(accNum);
        if (acc == null) throw new AccountNotFoundException(accNum);
        
        System.out.print("Amount: ");
        double amount = getDouble();
        acc.withdraw(amount);
        System.out.println("✓ Withdrawn $" + amount);
    }
    
    private void doTransfer() throws InsufficientFundsException, 
                                    InvalidAmountException, AccountNotFoundException {
        System.out.print("From Account: ");
        String from = scanner.nextLine().trim();
        System.out.print("To Account: ");
        String to = scanner.nextLine().trim();
        System.out.print("Amount: ");
        double amount = getDouble();
        
        Account source = repository.findAccount(from);
        Account target = repository.findAccount(to);
        
        if (source == null) throw new AccountNotFoundException(from);
        if (target == null) throw new AccountNotFoundException(to);
        
        source.transfer(target, amount);
        System.out.println("✓ Transferred $" + amount);
    }
    
    private void viewAccount() throws AccountNotFoundException {
        String accNum = promptAccount();
        Account acc = repository.findAccount(accNum);
        if (acc == null) throw new AccountNotFoundException(accNum);
        
        System.out.println("\n" + acc);
    }
    
    private void viewAllAccounts() {
        System.out.println("\n--- ALL ACCOUNTS ---");
        for (Account acc : repository.findAll()) {
            System.out.println(acc);
        }
    }
    
    private void accountStatement() throws AccountNotFoundException {
        String accNum = promptAccount();
        System.out.println(reportService.generateAccountStatement(accNum));
    }
    
    private void bankSummary() {
        System.out.println(reportService.generateBankSummary());
    }
    
    private void interestReport() {
        System.out.println(reportService.generateInterestReport());
    }
    
    private void deleteAccount() {
        String accNum = promptAccount();
        if (repository.deleteAccount(accNum)) {
            System.out.println("✓ Account deleted!");
        } else {
            System.out.println("Account not found!");
        }
    }
    
    private String promptAccount() {
        System.out.print("Account Number: ");
        return scanner.nextLine().trim();
    }
    
    private double getDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public static void main(String[] args) {
        new BankMenu().start();
    }
}
```

---

## Running the Project

```bash
cd 02-oop
javac -d out -sourcepath src src/com/learning/project/**/*.java src/com/learning/project/*.java
java -cp out com.learning.project.ui.BankMenu
```

---

## Design Patterns Demonstrated

| Pattern | Implementation |
|---------|----------------|
| **Factory** | `AccountFactory` creates different account types |
| **Observer** | `NotificationService` notifies on account events |
| **Template Method** | Abstract `Account` defines withdrawal/deposit structure |
| **Strategy** | Different interest calculation strategies per account type |
| **Repository** | `AccountRepository` manages data access |

---

## Extensions

1. **Add persistence**: Save/load accounts to file
2. **Add scheduled jobs**: Monthly interest calculation
3. **Add security**: User authentication
4. **Add国际化**: Multi-language support
5. **Add REST API**: Spring Boot controller