# Mini Project: Secure DTO & Data Normalization

## Objective
Build a robust Data Transfer Object (DTO) using a Java Record. You will use a Compact Constructor to normalize input data and perform defensive copying of a mutable array. You will also demonstrate how Records safely survive the deserialization process compared to standard classes.

## Prerequisites
*   Java 17+

## Step 1: The Vulnerable Standard Class
Create a standard Java class that tries to enforce validation, but fails during deserialization.

```java
import java.io.Serializable;

public class BadUserDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String username;

    public BadUserDTO(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        this.username = username.trim().toLowerCase(); // Normalization
    }

    public String getUsername() { return username; }

    @Override
    public String toString() { return "BadUserDTO{username='" + username + "'}"; }
}
```

## Step 2: The Secure Record
Create the equivalent Record. Use a compact constructor for validation and normalization, and defensively copy a mutable byte array.

```java
import java.io.Serializable;

public record SecureUserDTO(String username, byte[] profileImage) implements Serializable {
    
    // Compact Constructor
    public SecureUserDTO {
        // 1. Validation
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        
        // 2. Normalization (Reassigning the parameter before it is assigned to the field)
        username = username.trim().toLowerCase();
        
        // 3. Defensive Copy (In)
        if (profileImage != null) {
            profileImage = profileImage.clone();
        }
    }

    // Defensive Copy (Out) - Overriding the generated accessor
    @Override
    public byte[] profileImage() {
        return profileImage == null ? null : profileImage.clone();
    }
}
```

## Step 3: The Serialization Test
Write a utility to simulate an attacker modifying a byte stream to bypass the constructor.

```java
import java.io.*;

public class SerializationHacker {

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
        }
        return baos.toByteArray();
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        }
    }

    // Simulates an attacker modifying the byte stream to inject a null username
    public static byte[] hackByteStream(byte[] original, String targetString) {
        // Very rudimentary hack: find the string in the byte array and replace it with null bytes
        // This simulates a crafted malicious payload that bypasses the constructor.
        byte[] hacked = original.clone();
        byte[] target = targetString.getBytes();
        
        for (int i = 0; i < hacked.length - target.length; i++) {
            boolean match = true;
            for (int j = 0; j < target.length; j++) {
                if (hacked[i + j] != target[j]) { match = false; break; }
            }
            if (match) {
                // Wipe out the string
                for (int j = 0; j < target.length; j++) hacked[i + j] = 0;
                break;
            }
        }
        return hacked;
    }
}
```

## Step 4: Execute
```java
public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("--- 1. Testing Defensive Copying ---");
        byte[] imgData = {1, 2, 3};
        SecureUserDTO secureUser = new SecureUserDTO("  Alice  ", imgData);
        
        System.out.println("Normalized Username: '" + secureUser.username() + "'");
        
        // Try to hack the internal array
        imgData[0] = 99; 
        System.out.println("Internal array hacked? (Should be 1): " + secureUser.profileImage()[0]);

        System.out.println("\n--- 2. Testing Deserialization Vulnerability ---");
        
        BadUserDTO badUser = new BadUserDTO("Bob");
        byte[] badBytes = SerializationHacker.serialize(badUser);
        
        // Attack!
        byte[] hackedBadBytes = SerializationHacker.hackByteStream(badBytes, "bob");
        BadUserDTO restoredBadUser = (BadUserDTO) SerializationHacker.deserialize(hackedBadBytes);
        
        // The attacker successfully created a BadUserDTO with a corrupted/null username, bypassing the constructor!
        System.out.println("Restored BadUser: " + restoredBadUser);


        System.out.println("\n--- 3. Testing Record Serialization Safety ---");
        
        byte[] secureBytes = SerializationHacker.serialize(secureUser);
        byte[] hackedSecureBytes = SerializationHacker.hackByteStream(secureBytes, "alice");
        
        try {
            // Attack!
            SerializationHacker.deserialize(hackedSecureBytes);
        } catch (Exception e) {
            // The JVM routed the deserialization through the canonical constructor, 
            // which caught the null/blank username and threw the exception!
            System.out.println("Attack FAILED! Record constructor caught it: " + e.getMessage());
        }
    }
}
```

## Expected Output
```text
--- 1. Testing Defensive Copying ---
Normalized Username: 'alice'
Internal array hacked? (Should be 1): 1

--- 2. Testing Deserialization Vulnerability ---
Restored BadUser: BadUserDTO{username='   '}

--- 3. Testing Record Serialization Safety ---
Attack FAILED! Record constructor caught it: Username cannot be blank
```