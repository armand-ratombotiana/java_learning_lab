# Mini Project: Advanced Polymorphic Data Processor

## Objective
Build a data processing pipeline that demonstrates covariant return types, method hiding (static polymorphism), default method resolution (MRO), and the dangers of constructor polymorphism.

## Prerequisites
*   Java 17+

## Step 1: Covariant Return Types (The Builder Pattern)
Create a base builder and a specialized builder. Use covariant return types so the client doesn't need to cast.

```java
public class ProcessorBuilder {
    protected String name = "Default";

    public ProcessorBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public DataProcessor build() {
        return new DataProcessor(name);
    }
}

public class SecureProcessorBuilder extends ProcessorBuilder {
    private boolean encrypt = false;

    // Covariant Return Type: Returns SecureProcessorBuilder instead of ProcessorBuilder
    @Override
    public SecureProcessorBuilder withName(String name) {
        super.withName(name);
        return this;
    }

    public SecureProcessorBuilder withEncryption() {
        this.encrypt = true;
        return this;
    }

    // Covariant Return Type: Returns SecureDataProcessor instead of DataProcessor
    @Override
    public SecureDataProcessor build() {
        return new SecureDataProcessor(name, encrypt);
    }
}
```

## Step 2: Static Polymorphism (Method Hiding)
Implement the processors and demonstrate method hiding.

```java
public class DataProcessor {
    protected final String name;

    public DataProcessor(String name) {
        this.name = name;
    }

    // Instance method (Dynamic binding)
    public void process(String data) {
        System.out.println("Processing: " + data);
    }

    // Static method (Static binding / Method Hiding)
    public static void logType() {
        System.out.println("Type: Standard Processor");
    }
}

public class SecureDataProcessor extends DataProcessor {
    private final boolean encrypt;

    public SecureDataProcessor(String name, boolean encrypt) {
        super(name);
        this.encrypt = encrypt;
    }

    @Override
    public void process(String data) {
        String output = encrypt ? "***ENCRYPTED***" : data;
        System.out.println("Securely Processing: " + output);
    }

    // Hides the superclass method
    public static void logType() {
        System.out.println("Type: Secure Processor");
    }
}
```

## Step 3: Default Method Resolution (MRO)
Create interfaces with conflicting default methods.

```java
public interface Auditable {
    default void audit() {
        System.out.println("Audit: Standard Logging");
    }
}

public interface Compliant {
    default void audit() {
        System.out.println("Audit: Strict Compliance Logging");
    }
}

// The class must resolve the conflict!
public class FinancialProcessor extends SecureDataProcessor implements Auditable, Compliant {
    
    public FinancialProcessor(String name, boolean encrypt) {
        super(name, encrypt);
    }

    @Override
    public void audit() {
        // Resolving the diamond problem explicitly
        System.out.print("Financial Audit -> ");
        Compliant.super.audit(); // Choosing the Compliant implementation
    }
}
```

## Step 4: Test the Pipeline
Demonstrate the different types of polymorphism in action.

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 1. Covariant Return Types ---");
        // No casting needed because of covariant returns!
        SecureDataProcessor p1 = new SecureProcessorBuilder()
                .withName("BankVault")
                .withEncryption()
                .build();
        p1.process("Secret Data");

        System.out.println("\n--- 2. Static vs Dynamic Binding ---");
        DataProcessor p2 = new SecureDataProcessor("Proxy", true);
        
        // Dynamic Binding: Calls SecureDataProcessor's process()
        p2.process("Secret Data"); 
        
        // Static Binding: Calls DataProcessor's logType() because p2 is a DataProcessor reference!
        p2.logType(); 
        
        // Proper way to call static methods
        SecureDataProcessor.logType();

        System.out.println("\n--- 3. Default Method Resolution ---");
        FinancialProcessor p3 = new FinancialProcessor("WallStreet", true);
        p3.audit();
    }
}
```

## Expected Output
```text
--- 1. Covariant Return Types ---
Securely Processing: ***ENCRYPTED***

--- 2. Static vs Dynamic Binding ---
Securely Processing: ***ENCRYPTED***
Type: Standard Processor
Type: Secure Processor

--- 3. Default Method Resolution ---
Financial Audit -> Audit: Strict Compliance Logging
```