# Mini Project: Robust Payment Processor

## Objective
Build a payment processing system using Algebraic Data Types (ADTs). You will define a closed hierarchy of payment methods using `sealed` interfaces and `record`s. Then, you will build a processor that uses exhaustive pattern matching to handle every payment type safely.

## Prerequisites
*   Java 21+

## Step 1: Define the Domain (Algebraic Data Types)
We define a strict hierarchy. A `PaymentMethod` can only be a `CreditCard`, a `PayPal` account, or a `CryptoWallet`.

```java
// The root of the hierarchy is sealed
public sealed interface PaymentMethod permits CreditCard, PayPal, CryptoWallet {
    String getOwnerName();
}

// Records are implicitly final, satisfying the subclass modifier rule
public record CreditCard(String ownerName, String cardNumber, String cvv) implements PaymentMethod {
    @Override public String getOwnerName() { return ownerName; }
}

public record PayPal(String ownerName, String email) implements PaymentMethod {
    @Override public String getOwnerName() { return ownerName; }
}

// We can also have a sealed sub-hierarchy!
public sealed interface CryptoWallet extends PaymentMethod permits BitcoinWallet, EthereumWallet {}

public record BitcoinWallet(String ownerName, String address) implements CryptoWallet {
    @Override public String getOwnerName() { return ownerName; }
}

public record EthereumWallet(String ownerName, String address) implements CryptoWallet {
    @Override public String getOwnerName() { return ownerName; }
}
```

## Step 2: The Exhaustive Processor
Now we process the payment. Because the hierarchy is sealed, the compiler guarantees we haven't missed any payment types.

```java
public class PaymentProcessor {

    public void processPayment(PaymentMethod method, double amount) {
        System.out.println("Processing $" + amount + " for " + method.getOwnerName());

        // The compiler checks this for exhaustiveness. No 'default' branch needed!
        // If someone adds 'BankTransfer implements PaymentMethod' later, this code will FAIL TO COMPILE,
        // alerting us immediately that we need to add logic for Bank Transfers.
        String result = switch (method) {
            case CreditCard cc -> processCreditCard(cc, amount);
            case PayPal pp -> processPayPal(pp, amount);
            
            // We can match on the sub-hierarchy directly!
            case BitcoinWallet btc -> processCrypto(btc.address(), "BTC", amount);
            case EthereumWallet eth -> processCrypto(eth.address(), "ETH", amount);
        };

        System.out.println("Result: " + result + "\n");
    }

    private String processCreditCard(CreditCard cc, double amount) {
        // Mask the card number for security
        String masked = "****-****-****-" + cc.cardNumber().substring(cc.cardNumber().length() - 4);
        return "Charged " + masked;
    }

    private String processPayPal(PayPal pp, double amount) {
        return "Invoiced PayPal account: " + pp.email();
    }

    private String processCrypto(String address, String coin, double amount) {
        return "Initiated transfer of " + amount + " " + coin + " to address " + address;
    }
}
```

## Step 3: Test the System
```java
public class Main {
    public static void main(String[] args) {
        PaymentProcessor processor = new PaymentProcessor();

        PaymentMethod card = new CreditCard("Alice Smith", "1234567890123456", "123");
        PaymentMethod paypal = new PayPal("Bob Jones", "bob@example.com");
        PaymentMethod btc = new BitcoinWallet("Charlie Nakamoto", "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa");

        processor.processPayment(card, 150.00);
        processor.processPayment(paypal, 45.50);
        processor.processPayment(btc, 0.05);
    }
}
```

## Expected Output
```text
Processing $150.0 for Alice Smith
Result: Charged ****-****-****-3456

Processing $45.5 for Bob Jones
Result: Invoiced PayPal account: bob@example.com

Processing $0.05 for Charlie Nakamoto
Result: Initiated transfer of 0.05 BTC to address 1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa
```