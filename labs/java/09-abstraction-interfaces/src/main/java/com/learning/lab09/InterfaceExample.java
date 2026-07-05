package com.learning.lab09;

/**
 * Demonstrates interfaces with default, static, and private methods (Java 9+).
 */
public class InterfaceExample {

    public static void showInterfaces() {
        System.out.println("=== Interfaces ===");

        PaymentProcessor credit = new CreditCardProcessor();
        PaymentProcessor paypal = new PayPalProcessor();

        credit.process(100.0);
        paypal.process(200.0);

        System.out.println(credit.getTransactionSummary());
        System.out.println(paypal.getTransactionSummary());

        boolean supported = PaymentProcessor.validateCurrency("USD");
        System.out.println("USD supported: " + supported);
    }
}

interface PaymentProcessor {
    void process(double amount);

    default String getTransactionSummary() {
        return "Payment processed: " + logTransaction();
    }

    private String logTransaction() {
        return "Transaction logged at " + System.currentTimeMillis();
    }

    static boolean validateCurrency(String currency) {
        return currency.equals("USD") || currency.equals("EUR");
    }
}

class CreditCardProcessor implements PaymentProcessor {
    @Override
    public void process(double amount) {
        System.out.println("Credit card charged: $" + amount);
    }
}

class PayPalProcessor implements PaymentProcessor {
    @Override
    public void process(double amount) {
        System.out.println("PayPal payment: $" + amount);
    }
}
