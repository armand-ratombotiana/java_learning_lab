# Exceptions — Step-by-Step Tutorial

## Step 1: Basic try-catch

```java
import java.util.Scanner;

public class Step1TryCatch {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter numerator: ");
        int a = scanner.nextInt();
        System.out.print("Enter denominator: ");
        int b = scanner.nextInt();
        
        try {
            int result = a / b;
            System.out.println("Result: " + result);
        } catch (ArithmeticException e) {
            System.out.println("Cannot divide by zero!");
        }
        
        scanner.close();
        System.out.println("Program continues...");
    }
}
```

## Step 2: Multiple Catch Blocks

```java
import java.util.Scanner;

public class Step2MultiCatch {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            System.out.print("Enter array size: ");
            int size = scanner.nextInt();
            int[] array = new int[size];
            
            System.out.print("Enter index: ");
            int index = scanner.nextInt();
            
            System.out.print("Enter value: ");
            int value = scanner.nextInt();
            
            array[index] = value;
            System.out.println("Array[" + index + "] = " + array[index]);
            
        } catch (NegativeArraySizeException e) {
            System.out.println("Array size cannot be negative");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Index out of bounds");
        } catch (Exception e) {
            System.out.println("Some other error: " + e.getMessage());
        }
        
        scanner.close();
    }
}
```

## Step 3: Try-With-Resources

```java
import java.io.*;
import java.util.Scanner;

public class Step3TryWithResources {
    public static void main(String[] args) {
        // Try-with-resources — resources are auto-closed
        try (Scanner scanner = new Scanner(System.in);
             FileWriter writer = new FileWriter("output.txt")) {
            
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            writer.write("Hello, " + name + "!");
            System.out.println("Written to file.");
            
        } catch (IOException e) {
            System.out.println("IO error: " + e.getMessage());
        }
        // scanner and writer are closed automatically
    }
}
```

## Step 4: Custom Exception

```java
public class Step4CustomException {
    public static void main(String[] args) {
        try {
            registerUser("al", 15);
        } catch (InvalidUserException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }
    
    public static void registerUser(String username, int age) 
            throws InvalidUserException {
        if (username.length() < 3) {
            throw new InvalidUserException("Username too short (min 3 chars)");
        }
        if (age < 18) {
            throw new InvalidUserException("Must be 18 or older (got " + age + ")");
        }
        System.out.println("User " + username + " registered successfully");
    }
}

class InvalidUserException extends Exception {
    public InvalidUserException(String message) {
        super(message);
    }
}
```

## Step 5: Checked vs Unchecked

```java
import java.io.*;

public class Step5CheckedVsUnchecked {
    public static void main(String[] args) {
        // Unchecked — no handling required
        int[] arr = new int[5];
        try {
            arr[10] = 42;  // ArrayIndexOutOfBoundsException
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Unchecked exception caught");
        }
        
        // Checked — MUST handle or declare
        try {
            readFile("test.txt");
        } catch (IOException e) {
            System.out.println("Checked exception caught: " + e.getMessage());
        }
        
        System.out.println("Program continues safely");
    }
    
    // Declare checked exception
    public static void readFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        System.out.println("First line: " + reader.readLine());
        reader.close();
    }
}
```

## Step 6: Finally Block

```java
import java.io.*;

public class Step6Finally {
    private static BufferedReader reader = null;
    
    public static void main(String[] args) {
        try {
            reader = new BufferedReader(new FileReader("config.txt"));
            String line = reader.readLine();
            if (line == null) return;
            System.out.println("Config: " + line);
        } catch (FileNotFoundException e) {
            System.out.println("Config file not found, using defaults");
        } catch (IOException e) {
            System.out.println("Error reading config");
        } finally {
            // Always executes, even with return or exception
            try {
                if (reader != null) reader.close();
                System.out.println("Reader closed");
            } catch (IOException e) {
                System.out.println("Error closing reader");
            }
        }
    }
}
```
