package com.learning.lab23;

/**
 * Demonstrates sealed interfaces with record implementations.
 */
public class SealedWithRecordsExample {

    public static void showSealedWithRecords() {
        System.out.println("=== Sealed Interface with Records ===");

        Payment payment1 = new CreditCard("4111-1111-1111-1111", "Alice");
        Payment payment2 = new PayPal("alice@email.com");
        Payment payment3 = new Crypto("0xABC123");

        process(payment1);
        process(payment2);
        process(payment3);
    }

    static void process(Payment p) {
        String result = switch (p) {
            case CreditCard cc -> "CC ending in " + cc.cardNumber().substring(cc.cardNumber().length() - 4);
            case PayPal pp -> "PayPal account " + pp.email();
            case Crypto c -> "Crypto wallet " + c.walletAddress().substring(0, 6) + "...";
        };
        System.out.println("  Processed: " + result);
    }
}

sealed interface Payment permits CreditCard, PayPal, Crypto {}

record CreditCard(String cardNumber, String cardHolder) implements Payment {}
record PayPal(String email) implements Payment {}
record Crypto(String walletAddress) implements Payment {}
