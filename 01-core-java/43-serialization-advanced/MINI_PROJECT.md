# Mini Project: Secure Serialization Pipeline

## Objective
Build a system that serializes and deserializes a sensitive `Employee` object. You will implement custom `writeObject`/`readObject` methods to encrypt a social security number, and use `ObjectInputFilter` to protect against deserialization gadgets.

## Prerequisites
*   Java 9+

## Step 1: The Encryptor Utility
Create a simple utility to simulate encryption/decryption.

```java
public class Encryptor {
    // A very simple "encryption" for demonstration purposes (Caesar cipher)
    public static String encrypt(String data) {
        if (data == null) return null;
        return new String(data.getBytes()).replace('0', 'X'); 
    }

    public static String decrypt(String data) {
        if (data == null) return null;
        return new String(data.getBytes()).replace('X', '0');
    }
}
```

## Step 2: The Secure Domain Object
Create the `Employee` class. We use `transient` for the SSN so it isn't serialized in plain text. We override `writeObject` and `readObject` to handle the encryption manually.

```java
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int id;
    
    // transient means "ignore this field during default serialization"
    private transient String ssn; 

    public Employee(String name, int id, String ssn) {
        this.name = name;
        this.id = id;
        this.ssn = ssn;
    }

    public String getSsn() { return ssn; }

    // --- Custom Serialization Logic ---

    private void writeObject(ObjectOutputStream oos) throws IOException {
        // 1. Perform default serialization for non-transient fields (name, id)
        oos.defaultWriteObject();
        
        // 2. Encrypt the SSN and write it to the stream manually
        String encryptedSsn = Encryptor.encrypt(ssn);
        oos.writeObject(encryptedSsn);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        // 1. Perform default deserialization for non-transient fields
        ois.defaultReadObject();
        
        // 2. Read the encrypted SSN from the stream and decrypt it
        String encryptedSsn = (String) ois.readObject();
        this.ssn = Encryptor.decrypt(encryptedSsn);
        
        // 3. Optional: Perform invariant validation here (since constructors are bypassed)
        if (this.id <= 0) {
            throw new java.io.InvalidObjectException("ID must be positive");
        }
    }

    @Override
    public String toString() {
        return "Employee{name='" + name + "', id=" + id + ", ssn='" + ssn + "'}";
    }
}
```

## Step 3: The Malicious Gadget
Create a class that simulates a security vulnerability. If an attacker manages to get the JVM to deserialize this class, it executes harmful code.

```java
import java.io.Serializable;

public class MaliciousGadget implements Serializable {
    private static final long serialVersionUID = 1L;

    // This method is called by the Garbage Collector before destroying the object
    @Override
    protected void finalize() throws Throwable {
        System.err.println(">>> MALICIOUS CODE EXECUTED! Data exfiltrated! <<<");
        super.finalize();
    }
}
```

## Step 4: The Serialization Pipeline & Filter
Write the code to serialize to a byte array, and deserialize back. We will apply an `ObjectInputFilter` to block the malicious gadget.

```java
import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Employee emp = new Employee("Alice", 101, "000-11-0000");
        MaliciousGadget gadget = new MaliciousGadget();

        System.out.println("--- 1. Serializing Employee ---");
        byte[] empBytes = serialize(emp);
        System.out.println("Serialized size: " + empBytes.length + " bytes");

        System.out.println("\n--- 2. Serializing Malicious Gadget ---");
        byte[] gadgetBytes = serialize(gadget);

        System.out.println("\n--- 3. Deserializing Employee (With Filter) ---");
        Employee restoredEmp = (Employee) deserializeWithFilter(empBytes);
        System.out.println("Restored: " + restoredEmp);

        System.out.println("\n--- 4. Deserializing Gadget (With Filter) ---");
        try {
            deserializeWithFilter(gadgetBytes);
        } catch (Exception e) {
            System.out.println("BLOCKED by filter: " + e.getMessage());
        }
        
        // Force GC to show the gadget wasn't instantiated
        System.gc();
        Thread.sleep(100);
    }

    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
        }
        return baos.toByteArray();
    }

    private static Object deserializeWithFilter(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            
            // SECURITY: Create a filter that ONLY allows Employee and Strings.
            // Reject everything else (!*).
            ObjectInputFilter filter = ObjectInputFilter.Config.createFilter(
                "Employee;java.lang.String;!*"
            );
            ois.setObjectInputFilter(filter);

            return ois.readObject();
        }
    }
}
```

## Expected Output
Notice that the SSN is correctly decrypted, and the Malicious Gadget is caught by the filter before it can do any harm.
```text
--- 1. Serializing Employee ---
Serialized size: 104 bytes

--- 2. Serializing Malicious Gadget ---

--- 3. Deserializing Employee (With Filter) ---
Restored: Employee{name='Alice', id=101, ssn='000-11-0000'}

--- 4. Deserializing Gadget (With Filter) ---
BLOCKED by filter: filter status: REJECTED
```