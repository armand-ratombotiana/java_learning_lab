package com.learning;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

/**
 * Test suite for inheritance hierarchy with Animal and Dog classes.
 * Tests parent-child class relationships and method overriding.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
@DisplayName("Animal and Inheritance Tests")
class AnimalTest {

    private Animal animal;
    private Dog dog;

    @BeforeEach
    void setUp() {
        animal = new Animal("Generic Animal", 5);
        dog = new Dog("Buddy", 3, "Labrador");
    }

    @Test
    @DisplayName("Should create animal with name and age")
    void testAnimalConstructor() {
        assertThat(animal.getName()).isEqualTo("Generic Animal");
        assertThat(animal.getAge()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should set and get name")
    void testSetGetName() {
        animal.setName("Updated Name");
        assertThat(animal.getName()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("Should set and get age")
    void testSetGetAge() {
        animal.setAge(7);
        assertThat(animal.getAge()).isEqualTo(7);
    }

    @Test
    @DisplayName("Should not set negative age")
    void testNegativeAge() {
        animal.setAge(-5);
        assertThat(animal.getAge()).isEqualTo(5); // Should remain unchanged
    }

    @Test
    @DisplayName("Should create dog with breed")
    void testDogConstructor() {
        assertThat(dog.getName()).isEqualTo("Buddy");
        assertThat(dog.getAge()).isEqualTo(3);
        assertThat(dog.getBreed()).isEqualTo("Labrador");
    }

    @Test
    @DisplayName("Dog should override makeSound method")
    void testDogMakeSound() {
        // This test verifies that Dog overrides the parent method
        assertThat(dog).isInstanceOf(Animal.class);
        // The output would be "Buddy barks: Woof! Woof!" instead of generic sound
    }

    @Test
    @DisplayName("Dog should have dog-specific methods")
    void testDogSpecificMethods() {
        assertThat(dog).hasFieldOrProperty("breed");
        assertThat(dog.getBreed()).isNotNull();
    }

    @Test
    @DisplayName("Should set and get dog breed")
    void testSetGetBreed() {
        dog.setBreed("Golden Retriever");
        assertThat(dog.getBreed()).isEqualTo("Golden Retriever");
    }

    @Test
    @DisplayName("Test dog toString representation")
    void testDogToString() {
        String str = dog.toString();
        assertThat(str)
            .contains("Dog")
            .contains("Buddy")
            .contains("Labrador");
    }

    @Test
    @DisplayName("Dog should be instance of Animal")
    void testInheritanceRelationship() {
        assertThat(dog instanceof Animal).isTrue();
        assertThat(dog instanceof Dog).isTrue();
    }
}
