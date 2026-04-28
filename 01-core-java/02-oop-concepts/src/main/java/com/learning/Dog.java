package com.learning;

/**
 * Demonstrates inheritance by extending Animal class.
 * Shows method overriding and additional functionality.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class Dog extends Animal {
    // Additional field specific to Dog
    private String breed;
    
    // Constructor
    public Dog(String name, int age, String breed) {
        super(name, age); // Call parent constructor
        this.breed = breed;
    }
    
    // Getter and Setter for breed
    public String getBreed() {
        return breed;
    }
    
    public void setBreed(String breed) {
        this.breed = breed;
    }
    
    // Override parent method
    @Override
    public void makeSound() {
        System.out.println(name + " barks: Woof! Woof!");
    }
    
    // Override parent method
    @Override
    public void displayInfo() {
        super.displayInfo(); // Call parent method
        System.out.println("Breed: " + breed);
    }
    
    // Dog-specific methods
    public void fetch() {
        System.out.println(name + " is fetching the ball");
    }
    
    public void wagTail() {
        System.out.println(name + " is wagging its tail");
    }
    
    @Override
    public String toString() {
        return "Dog{name='" + name + "', age=" + age + ", breed='" + breed + "'}";
    }
}