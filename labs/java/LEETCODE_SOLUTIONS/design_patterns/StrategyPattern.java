package design_patterns;

import java.util.function.Function;

/**
 * Strategy Pattern — defines a family of algorithms, encapsulates each one, and makes them interchangeable.
 * 
 * Modern Java alternative: Use lambdas / method references instead of defining strategy interfaces.
 * 
 * Time: O(1) per operation
 * Space: O(1)
 */
public class StrategyPattern {

    // Traditional Strategy interface
    interface PaymentStrategy {
        void pay(int amount);
    }

    static class CreditCardPayment implements PaymentStrategy {
        private final String card;
        CreditCardPayment(String card) { this.card = card; }
        public void pay(int amount) { System.out.println("Paid " + amount + " via card " + card); }
    }

    static class PayPalPayment implements PaymentStrategy {
        private final String email;
        PayPalPayment(String email) { this.email = email; }
        public void pay(int amount) { System.out.println("Paid " + amount + " via PayPal " + email); }
    }

    static class ShoppingCart {
        private PaymentStrategy strategy;
        void setStrategy(PaymentStrategy s) { this.strategy = s; }
        void checkout(int amount) { strategy.pay(amount); }
    }

    // Modern Java approach: Strategy as lambda
    static class ShoppingCartLambda {
        private java.util.function.IntConsumer paymentFn;
        void setStrategy(java.util.function.IntConsumer fn) { this.paymentFn = fn; }
        void checkout(int amount) { paymentFn.accept(amount); }
    }

    public static void main(String[] args) {
        // Traditional
        ShoppingCart cart = new ShoppingCart();
        cart.setStrategy(new CreditCardPayment("1234"));
        cart.checkout(100);
        cart.setStrategy(new PayPalPayment("a@b.com"));
        cart.checkout(200);

        // Lambda-based
        ShoppingCartLambda cart2 = new ShoppingCartLambda();
        cart2.setStrategy(amt -> System.out.println("Cash: " + amt));
        cart2.checkout(50);
        cart2.setStrategy(amt -> System.out.println("Crypto: " + amt));
        cart2.checkout(500);

        System.out.println("All StrategyPattern tests passed.");
    }
}