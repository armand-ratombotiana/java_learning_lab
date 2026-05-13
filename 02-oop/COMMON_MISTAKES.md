# Common OOP Mistakes

## 1. Violating Encapsulation

```java
// BAD - exposing internal state
public class BankAccount {
    public double balance;  // Anyone can modify!
}

// GOOD - private with controlled access
public class BankAccount {
    private double balance;
    public void deposit(double amount) {
        if (amount > 0) balance += amount;
    }
}
```

## 2. Using Inheritance When Composition Is Better

```java
// BAD - inheritance for code reuse
class Dog extends Animal {
    void bark() { }
}

// GOOD - composition
class Dog {
    private Animal animal = new Animal();
    private Barkable barker = new BarkService();
}
```

## 3. Not Using Interface

```java
// BAD - concrete dependency
class EmailService {
    private GmailClient client = new GmailClient();
}

// GOOD - depend on interface
class EmailService {
    private MailClient client;  // interface
}
```

## 4. Breaking Liskov Substitution

```java
// BAD - override breaks parent contract
class Square extends Rectangle {
    @Override
    public void setWidth(int w) {
        this.width = w;
        this.height = w;  // Breaks: should only set width!
    }
}
```

## 5. God Class

```java
// BAD - class does too much
class OrderProcessor {
    void processOrder() { }
    void sendEmail() { }
    void generateInvoice() { }
    void updateInventory() { }  // Too many responsibilities!
}

// GOOD - split responsibilities
class OrderService { }
class EmailService { }
class InvoiceService { }
class InventoryService { }
```