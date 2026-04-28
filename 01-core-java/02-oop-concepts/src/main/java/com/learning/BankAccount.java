package com.learning;

/**
 * Demonstrates encapsulation by hiding internal state
 * and providing controlled access through public methods.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class BankAccount {
    // Private fields (encapsulated data)
    private String accountNumber;
    private double balance;
    private String accountHolder;
    
    // Constructor
    public BankAccount(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance >= 0 ? initialBalance : 0;
    }
    
    public BankAccount(String accountNumber, double initialBalance, String accountHolder) {
        this(accountNumber, initialBalance);
        this.accountHolder = accountHolder;
    }
    
    // Public methods to access private data (controlled access)
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public String getAccountHolder() {
        return accountHolder;
    }
    
    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }
    
    // Business logic methods with validation
    public boolean deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited: $" + amount);
            System.out.println("New Balance: $" + balance);
            return true;
        } else {
            System.out.println("Invalid deposit amount");
            return false;
        }
    }
    
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawn: $" + amount);
            System.out.println("New Balance: $" + balance);
            return true;
        } else if (amount > balance) {
            System.out.println("Insufficient funds");
            return false;
        } else {
            System.out.println("Invalid withdrawal amount");
            return false;
        }
    }
    
    public boolean transfer(BankAccount targetAccount, double amount) {
        if (this.withdraw(amount)) {
            targetAccount.deposit(amount);
            System.out.println("Transferred $" + amount + " to account " + targetAccount.getAccountNumber());
            return true;
        }
        return false;
    }
    
    public void displayAccountInfo() {
        System.out.println("Account Number: " + accountNumber);
        if (accountHolder != null) {
            System.out.println("Account Holder: " + accountHolder);
        }
        System.out.println("Balance: $" + balance);
    }
    
    @Override
    public String toString() {
        return "BankAccount{accountNumber='" + accountNumber + "', balance=" + balance + "}";
    }
}