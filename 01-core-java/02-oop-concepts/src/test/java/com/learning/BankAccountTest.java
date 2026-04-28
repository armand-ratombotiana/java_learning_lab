package com.learning;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive test suite for BankAccount class.
 * Tests encapsulation, deposit, withdraw, and transfer operations.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
@DisplayName("BankAccount Tests - Encapsulation")
class BankAccountTest {
    
    private BankAccount account;
    
    @BeforeEach
    void setUp() {
        account = new BankAccount("ACC001", 1000.0);
    }
    
    @Test
    @DisplayName("Should create account with initial balance")
    void testConstructorWithBalance() {
        assertThat(account.getAccountNumber()).isEqualTo("ACC001");
        assertThat(account.getBalance()).isEqualTo(1000.0);
    }
    
    @Test
    @DisplayName("Should create account with holder name")
    void testConstructorWithHolder() {
        BankAccount acc = new BankAccount("ACC002", 500.0, "John Doe");
        assertThat(acc.getAccountNumber()).isEqualTo("ACC002");
        assertThat(acc.getBalance()).isEqualTo(500.0);
        assertThat(acc.getAccountHolder()).isEqualTo("John Doe");
    }
    
    @Test
    @DisplayName("Should not allow negative initial balance")
    void testNegativeInitialBalance() {
        BankAccount acc = new BankAccount("ACC003", -100.0);
        assertThat(acc.getBalance()).isEqualTo(0.0);
    }
    
    @Test
    @DisplayName("Should deposit valid amount")
    void testDepositValidAmount() {
        boolean result = account.deposit(500.0);
        assertThat(result).isTrue();
        assertThat(account.getBalance()).isEqualTo(1500.0);
    }
    
    @Test
    @DisplayName("Should not deposit zero amount")
    void testDepositZero() {
        boolean result = account.deposit(0.0);
        assertThat(result).isFalse();
        assertThat(account.getBalance()).isEqualTo(1000.0);
    }
    
    @Test
    @DisplayName("Should not deposit negative amount")
    void testDepositNegative() {
        boolean result = account.deposit(-100.0);
        assertThat(result).isFalse();
        assertThat(account.getBalance()).isEqualTo(1000.0);
    }
    
    @Test
    @DisplayName("Should withdraw valid amount")
    void testWithdrawValidAmount() {
        boolean result = account.withdraw(300.0);
        assertThat(result).isTrue();
        assertThat(account.getBalance()).isEqualTo(700.0);
    }
    
    @Test
    @DisplayName("Should not withdraw more than balance")
    void testWithdrawInsufficientFunds() {
        boolean result = account.withdraw(1500.0);
        assertThat(result).isFalse();
        assertThat(account.getBalance()).isEqualTo(1000.0);
    }
    
    @Test
    @DisplayName("Should not withdraw zero amount")
    void testWithdrawZero() {
        boolean result = account.withdraw(0.0);
        assertThat(result).isFalse();
        assertThat(account.getBalance()).isEqualTo(1000.0);
    }
    
    @Test
    @DisplayName("Should not withdraw negative amount")
    void testWithdrawNegative() {
        boolean result = account.withdraw(-100.0);
        assertThat(result).isFalse();
        assertThat(account.getBalance()).isEqualTo(1000.0);
    }
    
    @Test
    @DisplayName("Should withdraw entire balance")
    void testWithdrawEntireBalance() {
        boolean result = account.withdraw(1000.0);
        assertThat(result).isTrue();
        assertThat(account.getBalance()).isEqualTo(0.0);
    }
    
    @Test
    @DisplayName("Should transfer to another account")
    void testTransferSuccess() {
        BankAccount targetAccount = new BankAccount("ACC002", 500.0);
        boolean result = account.transfer(targetAccount, 300.0);
        
        assertThat(result).isTrue();
        assertThat(account.getBalance()).isEqualTo(700.0);
        assertThat(targetAccount.getBalance()).isEqualTo(800.0);
    }
    
    @Test
    @DisplayName("Should not transfer more than balance")
    void testTransferInsufficientFunds() {
        BankAccount targetAccount = new BankAccount("ACC002", 500.0);
        boolean result = account.transfer(targetAccount, 1500.0);
        
        assertThat(result).isFalse();
        assertThat(account.getBalance()).isEqualTo(1000.0);
        assertThat(targetAccount.getBalance()).isEqualTo(500.0);
    }
    
    @Test
    @DisplayName("Should set and get account holder")
    void testSetGetAccountHolder() {
        account.setAccountHolder("Jane Doe");
        assertThat(account.getAccountHolder()).isEqualTo("Jane Doe");
    }
    
    @Test
    @DisplayName("Should display account info without errors")
    void testDisplayAccountInfo() {
        assertThatCode(() -> account.displayAccountInfo()).doesNotThrowAnyException();
    }
    
    @Test
    @DisplayName("Should return correct toString")
    void testToString() {
        String result = account.toString();
        assertThat(result).contains("ACC001");
        assertThat(result).contains("1000");
    }
    
    @Test
    @DisplayName("Should handle multiple deposits")
    void testMultipleDeposits() {
        account.deposit(100.0);
        account.deposit(200.0);
        account.deposit(300.0);
        assertThat(account.getBalance()).isEqualTo(1600.0);
    }
    
    @Test
    @DisplayName("Should handle multiple withdrawals")
    void testMultipleWithdrawals() {
        account.withdraw(100.0);
        account.withdraw(200.0);
        account.withdraw(300.0);
        assertThat(account.getBalance()).isEqualTo(400.0);
    }
    
    @Test
    @DisplayName("Should handle mixed transactions")
    void testMixedTransactions() {
        account.deposit(500.0);  // 1500
        account.withdraw(300.0); // 1200
        account.deposit(200.0);  // 1400
        account.withdraw(400.0); // 1000
        assertThat(account.getBalance()).isEqualTo(1000.0);
    }
    
    @Test
    @DisplayName("Should handle decimal amounts")
    void testDecimalAmounts() {
        account.deposit(123.45);
        account.withdraw(23.45);
        assertThat(account.getBalance()).isEqualTo(1100.0);
    }
    
    @Test
    @DisplayName("Should handle very small amounts")
    void testVerySmallAmounts() {
        account.deposit(0.01);
        assertThat(account.getBalance()).isEqualTo(1000.01);
    }
    
    @Test
    @DisplayName("Should handle large amounts")
    void testLargeAmounts() {
        account.deposit(1000000.0);
        assertThat(account.getBalance()).isEqualTo(1001000.0);
    }
    
    @Test
    @DisplayName("Should maintain balance integrity after failed operations")
    void testBalanceIntegrityAfterFailures() {
        double initialBalance = account.getBalance();
        account.withdraw(2000.0); // Should fail
        account.deposit(-100.0);  // Should fail
        account.withdraw(-50.0);  // Should fail
        assertThat(account.getBalance()).isEqualTo(initialBalance);
    }
}