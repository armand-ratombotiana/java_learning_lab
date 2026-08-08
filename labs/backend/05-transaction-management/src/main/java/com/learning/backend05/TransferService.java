package com.learning.backend05;

public class TransferService {

    private final AccountRepository accountRepository;

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public TransferResult transfer(String fromAccount, String toAccount, double amount) {
        synchronized (accountRepository) {
            System.out.printf("Transferring $%.2f from %s to %s%n", amount, fromAccount, toAccount);

            Account source = accountRepository.findByAccountNumber(fromAccount)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found: " + fromAccount));
            Account destination = accountRepository.findByAccountNumber(toAccount)
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found: " + toAccount));

            source.debit(amount);
            accountRepository.save(source);

            destination.credit(amount);
            accountRepository.save(destination);

            System.out.printf("Transfer completed: $%.2f from %s to %s%n", amount, fromAccount, toAccount);

            if (source.getBalance() < 100) {
                throw new LowBalanceWarningException("Low balance warning for account " + fromAccount);
            }

            return new TransferResult(fromAccount, toAccount, amount, "SUCCESS");
        }
    }

    public void auditLog(String fromAccount, String toAccount, double amount) {
        System.out.printf("Audit log: $%.2f transferred from %s to %s%n", amount, fromAccount, toAccount);
    }

    public Account getAccountInfo(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
    }

    public record TransferResult(String fromAccount, String toAccount, double amount, String status) {}
}