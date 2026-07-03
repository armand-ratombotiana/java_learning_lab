# Exceptions — Code Deep Dive

## Example 1: Basic Exception Handling

```java
import java.io.*;

public class ExceptionBasics {
    public static void main(String[] args) {
        // Basic try-catch
        try {
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            System.out.println("Cannot divide by zero: " + e.getMessage());
        }
        
        // Multiple catch blocks (order matters — specific to general)
        try {
            String str = null;
            System.out.println(str.length());
        } catch (NullPointerException e) {
            System.out.println("Null pointer: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Runtime: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("General exception: " + e.getMessage());
        }
        
        // try-catch-finally
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("test.txt"));
            System.out.println(reader.readLine());
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("IO error: " + e.getMessage());
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                System.out.println("Error closing reader");
            }
        }
        
        System.out.println("Program continues...");
    }
}
```

## Example 2: Custom Exception

```java
// Custom checked exception
class InsufficientFundsException extends Exception {
    private final double balance;
    private final double amount;
    
    public InsufficientFundsException(double balance, double amount) {
        super(String.format("Insufficient funds: balance=%.2f, required=%.2f, shortfall=%.2f",
            balance, amount, amount - balance));
        this.balance = balance;
        this.amount = amount;
    }
    
    public double getBalance() { return balance; }
    public double getAmount() { return amount; }
    public double getShortfall() { return amount - balance; }
}

// Custom unchecked exception
class InvalidTransactionException extends RuntimeException {
    public InvalidTransactionException(String message) {
        super(message);
    }
    
    public InvalidTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}

class BankAccount {
    private double balance;
    
    public BankAccount(double initialBalance) {
        this.balance = initialBalance;
    }
    
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new InvalidTransactionException("Amount must be positive: " + amount);
        }
        if (amount > balance) {
            throw new InsufficientFundsException(balance, amount);
        }
        balance -= amount;
        System.out.printf("Withdrew $%.2f. New balance: $%.2f%n", amount, balance);
    }
    
    public double getBalance() { return balance; }
}

public class CustomExceptionDemo {
    public static void main(String[] args) {
        BankAccount account = new BankAccount(500);
        
        try {
            account.withdraw(100);   // OK
            account.withdraw(200);   // OK
            account.withdraw(300);   // Throws InsufficientFundsException
        } catch (InsufficientFundsException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Shortfall: $" + e.getShortfall());
        }
        
        try {
            account.withdraw(-50);   // Throws InvalidTransactionException
        } catch (InsufficientFundsException e) {
            System.out.println("Should not reach here");
        }
    }
}
```

## Example 3: Try-With-Resources

```java
import java.io.*;
import java.nio.file.*;

public class TryWithResourcesDemo {
    // Multiple resources
    public static void copyFile(String source, String dest) throws IOException {
        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(dest)) {
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            System.out.println("File copied successfully");
        }
        // Both fis.close() and fos.close() called automatically (reverse order)
    }
    
    // Custom resource
    static class DatabaseConnection implements AutoCloseable {
        public void query(String sql) {
            System.out.println("Executing: " + sql);
        }
        
        @Override
        public void close() {
            System.out.println("Closing database connection");
        }
    }
    
    // Java 9+: effectively-final resource
    public static void processResource(DatabaseConnection conn) throws Exception {
        // conn must be effectively final
        try (conn) {
            conn.query("SELECT * FROM users");
        }
    }
    
    public static void main(String[] args) {
        // Using custom resource
        try (DatabaseConnection db = new DatabaseConnection()) {
            db.query("INSERT INTO logs VALUES ('start')");
        }
        // db.close() called automatically
        
        // Using java.nio.file.Files (simpler I/O)
        try {
            String content = Files.readString(Path.of("test.txt"));
            System.out.println("Content: " + content);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        
        // Suppressed exceptions demo
        try {
            readWithError();
        } catch (Exception e) {
            System.out.println("Primary: " + e.getMessage());
            for (Throwable suppressed : e.getSuppressed()) {
                System.out.println("  Suppressed: " + suppressed.getMessage());
            }
        }
    }
    
    static class FailingReader implements AutoCloseable {
        public void read() throws IOException {
            throw new IOException("Read failed");
        }
        
        @Override
        public void close() throws Exception {
            throw new IOException("Close failed");
        }
    }
    
    static void readWithError() throws Exception {
        try (FailingReader reader = new FailingReader()) {
            reader.read();
        }
    }
}
```

## Example 4: Exception Propagation

```java
public class PropagationDemo {
    public static void main(String[] args) {
        try {
            level1();
        } catch (Exception e) {
            System.out.println("Caught in main: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    static void level1() {
        System.out.println("Level 1 start");
        level2();
        System.out.println("Level 1 end (not reached if exception)");
    }
    
    static void level2() {
        System.out.println("Level 2 start");
        level3();
        System.out.println("Level 2 end (not reached)");
    }
    
    static void level3() {
        System.out.println("Level 3 start");
        throw new RuntimeException("Something went wrong in level 3");
    }
}
// Output:
// Level 1 start
// Level 2 start
// Level 3 start
// Caught in main: Something went wrong in level 3
// java.lang.RuntimeException: Something went wrong in level 3
//     at PropagationDemo.level3(PropagationDemo.java:30)
//     at PropagationDemo.level2(PropagationDemo.java:24)
//     at PropagationDemo.level1(PropagationDemo.java:18)
//     at PropagationDemo.main(PropagationDemo.java:10)
```
