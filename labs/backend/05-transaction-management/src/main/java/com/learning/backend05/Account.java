package com.learning.backend05;

import jakarta.persistence.*;

/**
 * Bank account entity used in transaction management examples.
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false)
    private String owner;

    @Column(nullable = false)
    private double balance;

    public Account() {}

    public Account(String accountNumber, String owner, double balance) {
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.balance = balance;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public void debit(double amount) {
        if (balance < amount) {
            throw new InsufficientFundsException("Insufficient funds in account " + accountNumber);
        }
        this.balance -= amount;
    }

    public void credit(double amount) {
        this.balance += amount;
    }

    @Override
    public String toString() {
        return "Account{id=" + id + ", accountNumber='" + accountNumber +
               "', owner='" + owner + "', balance=" + balance + "}";
    }
}
