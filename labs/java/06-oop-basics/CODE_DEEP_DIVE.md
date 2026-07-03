# OOP Basics — Code Deep Dive

## Example 1: Complete Class Design

```java
public class BankAccount {
    // Static (class) members
    private static int nextAccountNumber = 1000;
    private static final String BANK_NAME = "Java National Bank";
    
    // Instance fields
    private final int accountNumber;
    private String ownerName;
    private double balance;
    
    // Constructor
    public BankAccount(String ownerName) {
        this.accountNumber = nextAccountNumber++;
        this.ownerName = ownerName;
        this.balance = 0.0;
    }
    
    // Overloaded constructor
    public BankAccount(String ownerName, double initialDeposit) {
        this(ownerName);  // Calls the first constructor
        if (initialDeposit > 0) {
            this.balance = initialDeposit;
        }
    }
    
    // Instance methods
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit must be positive");
        }
        balance += amount;
        logTransaction("Deposit", amount);
    }
    
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal must be positive");
        }
        if (amount > balance) {
            throw new IllegalStateException("Insufficient funds");
        }
        balance -= amount;
        logTransaction("Withdrawal", amount);
    }
    
    // Getters (read-only access to private fields)
    public double getBalance() { return balance; }
    public int getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    
    // Static method
    public static String getBankName() { return BANK_NAME; }
    
    // Private helper
    private void logTransaction(String type, double amount) {
        System.out.printf("[%s] Account %d: %s $%.2f (Balance: $%.2f)%n",
            BANK_NAME, accountNumber, type, amount, balance);
    }
    
    public static void main(String[] args) {
        System.out.println("Welcome to " + BankAccount.getBankName());
        
        BankAccount alice = new BankAccount("Alice", 1000);
        BankAccount bob = new BankAccount("Bob");
        
        alice.withdraw(200);
        bob.deposit(500);
        
        System.out.println(alice.getOwnerName() + "'s balance: $" + alice.getBalance());
        System.out.println(bob.getOwnerName() + "'s balance: $" + bob.getBalance());
    }
}
```

## Example 2: Static Members in Detail

```java
public class CounterDemo {
    public static void main(String[] args) {
        Counter c1 = new Counter();
        Counter c2 = new Counter();
        Counter c3 = new Counter();
        
        System.out.println("Instance counts:");
        System.out.println("c1.id = " + c1.getId() + ", c1.getInstanceCount() = " + c1.getInstanceCount());
        System.out.println("c2.id = " + c2.getId() + ", c2.getInstanceCount() = " + c2.getInstanceCount());
        System.out.println("c3.id = " + c3.getId() + ", c3.getInstanceCount() = " + c3.getInstanceCount());
        System.out.println("Total count (static): " + Counter.getTotalCount());
        
        // Accessing static via instance (works but bad practice)
        System.out.println("c1.totalCount (via instance): " + c1.getTotalCount());
    }
}

class Counter {
    private static int totalCount = 0;     // Shared across all instances
    private final int instanceCount;       // Per instance
    
    public Counter() {
        totalCount++;
        instanceCount = Counter.getTotalCount();  // OK to call static from instance
    }
    
    public int getId() { return instanceCount; }
    public int getInstanceCount() { return instanceCount; }
    public static int getTotalCount() { return totalCount; }
}
```

## Example 3: this Keyword

```java
public class ThisDemo {
    private String name;
    private int age;
    
    // 1. Disambiguate field from parameter
    public ThisDemo(String name, int age) {
        this.name = name;  // this.name = field, name = parameter
        this.age = age;
    }
    
    // 2. Call another constructor
    public ThisDemo() {
        this("Unknown", 0);  // calls ThisDemo(String, int)
    }
    
    // 3. Pass current object to another method
    public void register() {
        Database.save(this);  // passes current object
    }
    
    // 4. Return current object (fluent interface)
    public ThisDemo setName(String name) {
        this.name = name;
        return this;  // enables chaining: obj.setName("X").setAge(5);
    }
    
    public ThisDemo setAge(int age) {
        this.age = age;
        return this;
    }
    
    @Override
    public String toString() {
        return name + " (" + age + ")";
    }
    
    public static void main(String[] args) {
        // Fluent API using this-return
        ThisDemo person = new ThisDemo()
            .setName("Alice")
            .setAge(30);
        System.out.println(person);
    }
}

class Database {
    static void save(Object obj) {
        System.out.println("Saving: " + obj);
    }
}
```

## Example 4: Instance Initializer Blocks

```java
public class InitDemo {
    private static int staticCount = 0;
    private int instanceCount;
    
    // Static initializer block — runs once when class is loaded
    static {
        System.out.println("Static init block 1");
        staticCount = 100;
    }
    
    // Instance initializer block — runs before constructor body
    {
        System.out.println("Instance init block 1");
        instanceCount = 50;
    }
    
    {
        System.out.println("Instance init block 2");
        instanceCount += 10;
    }
    
    public InitDemo() {
        System.out.println("Constructor body");
        instanceCount += 5;
    }
    
    public static void main(String[] args) {
        System.out.println("Creating first object:");
        InitDemo obj1 = new InitDemo();
        System.out.println("instanceCount = " + obj1.instanceCount);
        
        System.out.println("\nCreating second object:");
        InitDemo obj2 = new InitDemo();
    }
}
```
