# Mini Project: The Animal Shelter (PECS in Action)

## Objective
Build a type-safe data transfer utility for an Animal Shelter. You will define a class hierarchy and use wildcard bounds (`? extends` and `? super`) to move animals between different enclosures safely, proving the PECS rule in code.

## Prerequisites
*   Java 17+

## Step 1: The Class Hierarchy
Create a simple hierarchy: `Animal` -> `Dog` / `Cat` -> `Puppy`.

```java
public abstract class Animal {
    private final String name;
    public Animal(String name) { this.name = name; }
    public String getName() { return name; }
    @Override public String toString() { return this.getClass().getSimpleName() + "[" + name + "]"; }
}

public class Dog extends Animal {
    public Dog(String name) { super(name); }
}

public class Puppy extends Dog {
    public Puppy(String name) { super(name); }
}

public class Cat extends Animal {
    public Cat(String name) { super(name); }
}
```

## Step 2: The Data Transfer Utility (The PECS Rule)
Create a utility class with methods that accept wildcards.

```java
import java.util.List;

public class ShelterUtils {

    // 1. PRODUCER EXTENDS
    // We only READ from this list. It produces Animals.
    // It can accept List<Animal>, List<Dog>, List<Cat>, List<Puppy>
    public static void printAnimals(List<? extends Animal> animals) {
        System.out.println("--- Shelter Inventory ---");
        for (Animal a : animals) {
            System.out.println("Found: " + a.getName());
        }
        // animals.add(new Dog("Buddy")); // COMPILE ERROR! Cannot write to an 'extends' wildcard.
    }

    // 2. CONSUMER SUPER
    // We only WRITE to this list. It consumes Dogs.
    // It can accept List<Dog>, List<Animal>, List<Object>
    public static void addDog(List<? super Dog> dogs, String name) {
        dogs.add(new Dog(name));
        System.out.println("Added Dog: " + name);
        
        // Dog d = dogs.get(0); // COMPILE ERROR! Cannot safely read as Dog.
    }

    // 3. PECS COMBINED (Transfer from Producer to Consumer)
    // Source produces Dogs (or Puppies). Destination consumes Dogs (or Animals).
    public static void transferDogs(List<? extends Dog> source, List<? super Dog> destination) {
        System.out.println("--- Transferring Dogs ---");
        for (Dog d : source) {
            destination.add(d);
            System.out.println("Transferred: " + d.getName());
        }
    }
}
```

## Step 3: Test the API Flexibility
Create collections of different types and see how the wildcards allow them to interact safely.

```java
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Create specific lists
        List<Puppy> puppies = new ArrayList<>();
        puppies.add(new Puppy("Spot"));
        puppies.add(new Puppy("Rover"));

        List<Cat> cats = new ArrayList<>();
        cats.add(new Cat("Whiskers"));

        // 1. Test Producer Extends (Reading)
        // Both lists are accepted because Puppy and Cat extend Animal
        ShelterUtils.printAnimals(puppies);
        ShelterUtils.printAnimals(cats);

        // 2. Test Consumer Super (Writing)
        List<Animal> generalShelter = new ArrayList<>();
        
        // We can pass a List<Animal> into a method expecting List<? super Dog>
        // because Animal is a superclass of Dog.
        ShelterUtils.addDog(generalShelter, "Fido");
        ShelterUtils.addDog(generalShelter, "Rex");

        // 3. Test PECS Transfer
        // Source is List<Puppy> (? extends Dog)
        // Dest is List<Animal> (? super Dog)
        ShelterUtils.transferDogs(puppies, generalShelter);

        // 4. Final Inventory Check
        ShelterUtils.printAnimals(generalShelter);
        
        // --- COMPILER ERROR EXAMPLES (Uncomment to see them fail) ---
        // ShelterUtils.addDog(puppies, "Fido"); // ERROR: Cannot put a Dog into a List<Puppy>
        // ShelterUtils.transferDogs(cats, generalShelter); // ERROR: List<Cat> does not extend Dog
    }
}
```

## Expected Output
```text
--- Shelter Inventory ---
Found: Spot
Found: Rover
--- Shelter Inventory ---
Found: Whiskers
Added Dog: Fido
Added Dog: Rex
--- Transferring Dogs ---
Transferred: Spot
Transferred: Rover
--- Shelter Inventory ---
Found: Fido
Found: Rex
Found: Spot
Found: Rover
```