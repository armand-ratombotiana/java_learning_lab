package com.learning.backend05;

public class Backend05Application {
    public static void main(String[] args) {
        AccountRepository repository = new AccountRepository();
        repository.save(new Account("1001", "Alice", 500.0));
        repository.save(new Account("1002", "Bob", 300.0));

        TransferService transferService = new TransferService(repository);

        TransferService.TransferResult result = transferService.transfer("1001", "1002", 150.0);
        System.out.println("Transfer status: " + result.status());

        System.out.println("Alice balance: " + transferService.getAccountInfo("1001").getBalance());
        System.out.println("Bob balance: " + transferService.getAccountInfo("1002").getBalance());

        transferService.auditLog("1001", "1002", 150.0);

        System.out.println("=== Transaction Management Lab is running ===");
    }
}