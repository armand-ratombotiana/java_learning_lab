package com.learning;

/**
 * Concrete implementation of Flyable interface.
 * Represents a bird that can fly.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class Bird implements Flyable {
    private String species;
    private int wingSpan; // in cm
    
    public Bird(String species) {
        this.species = species;
        this.wingSpan = 100; // Default
    }
    
    public Bird(String species, int wingSpan) {
        this.species = species;
        this.wingSpan = wingSpan;
    }
    
    public String getSpecies() {
        return species;
    }
    
    public int getWingSpan() {
        return wingSpan;
    }
    
    @Override
    public void fly() {
        System.out.println(species + " is flying with " + wingSpan + "cm wingspan");
    }
    
    @Override
    public void land() {
        System.out.println(species + " is landing on a tree branch");
    }
    
    @Override
    public int getMaxAltitude() {
        return 3000; // meters
    }
    
    public void chirp() {
        System.out.println(species + " is chirping");
    }
    
    @Override
    public String toString() {
        return "Bird{species='" + species + "', wingSpan=" + wingSpan + "cm}";
    }
}