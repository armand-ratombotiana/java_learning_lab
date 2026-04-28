package com.learning.sets;

import java.util.*;

/**
 * Demonstrates Set interface - unordered, unique elements collection.
 * Sets maintain uniqueness contract through equals() and hashCode().
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class SetInterfaceDemo {
    
    public void demonstrateSets() {
        System.out.println("--- SET INTERFACE DEMONSTRATION ---");
        
        Set<String> animals = new HashSet<>();
        animals.add("Dog");
        animals.add("Cat");
        animals.add("Bird");
        animals.add("Dog"); // Duplicate - not added
        
        System.out.println("Set: " + animals);
        System.out.println("Size: " + animals.size());
        System.out.println("Contains Dog: " + animals.contains("Dog"));
        
        // Add and remove
        animals.add("Fish");
        System.out.println("After add Fish: " + animals);
        
        animals.remove("Bird");
        System.out.println("After remove Bird: " + animals);
        
        // Set operations
        Set<String> otherAnimals = new HashSet<>(Arrays.asList("Dog", "Wolf", "Eagle"));
        System.out.println("Other animals: " + otherAnimals);
        
        // Union
        Set<String> union = new HashSet<>(animals);
        union.addAll(otherAnimals);
        System.out.println("Union: " + union);
        
        // Intersection
        Set<String> intersection = new HashSet<>(animals);
        intersection.retainAll(otherAnimals);
        System.out.println("Intersection: " + intersection);
    }
}
