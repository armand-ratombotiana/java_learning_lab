package com.learning;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive test suite for Person class.
 * Tests constructors, getters/setters, and methods.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
@DisplayName("Person Tests")
class PersonTest {
    
    private Person person;
    
    @BeforeEach
    void setUp() {
        person = new Person("John Doe", 30);
    }
    
    @Test
    @DisplayName("Should create person with default constructor")
    void testDefaultConstructor() {
        Person p = new Person();
        assertThat(p.getName()).isEqualTo("Unknown");
        assertThat(p.getAge()).isEqualTo(0);
    }
    
    @Test
    @DisplayName("Should create person with name and age")
    void testParameterizedConstructor() {
        assertThat(person.getName()).isEqualTo("John Doe");
        assertThat(person.getAge()).isEqualTo(30);
    }
    
    @Test
    @DisplayName("Should create person with full constructor")
    void testFullConstructor() {
        Person p = new Person("Jane Doe", 25, "jane@example.com");
        assertThat(p.getName()).isEqualTo("Jane Doe");
        assertThat(p.getAge()).isEqualTo(25);
        assertThat(p.getEmail()).isEqualTo("jane@example.com");
    }
    
    @Test
    @DisplayName("Should set and get name")
    void testSetGetName() {
        person.setName("Jane Smith");
        assertThat(person.getName()).isEqualTo("Jane Smith");
    }
    
    @Test
    @DisplayName("Should set and get age")
    void testSetGetAge() {
        person.setAge(35);
        assertThat(person.getAge()).isEqualTo(35);
    }
    
    @Test
    @DisplayName("Should not set negative age")
    void testSetNegativeAge() {
        person.setAge(-5);
        assertThat(person.getAge()).isEqualTo(30); // Should remain unchanged
    }
    
    @Test
    @DisplayName("Should set and get email")
    void testSetGetEmail() {
        person.setEmail("john@example.com");
        assertThat(person.getEmail()).isEqualTo("john@example.com");
    }
    
    @Test
    @DisplayName("Should increment person count")
    void testPersonCount() {
        int initialCount = Person.getPersonCount();
        new Person("Test", 20);
        assertThat(Person.getPersonCount()).isEqualTo(initialCount + 1);
    }
    
    @Test
    @DisplayName("Should celebrate birthday")
    void testCelebrateBirthday() {
        int initialAge = person.getAge();
        person.celebrateBirthday();
        assertThat(person.getAge()).isEqualTo(initialAge + 1);
    }
    
    @Test
    @DisplayName("Should check if person is adult")
    void testIsAdult() {
        Person adult = new Person("Adult", 18);
        Person minor = new Person("Minor", 17);
        
        assertThat(adult.isAdult()).isTrue();
        assertThat(minor.isAdult()).isFalse();
    }
    
    @Test
    @DisplayName("Should display info without errors")
    void testDisplayInfo() {
        assertThatCode(() -> person.displayInfo()).doesNotThrowAnyException();
    }
    
    @Test
    @DisplayName("Should return correct toString")
    void testToString() {
        String result = person.toString();
        assertThat(result).contains("John Doe");
        assertThat(result).contains("30");
    }
    
    @Test
    @DisplayName("Should check equality correctly")
    void testEquals() {
        Person p1 = new Person("John", 30);
        Person p2 = new Person("John", 30);
        Person p3 = new Person("Jane", 30);
        
        assertThat(p1.equals(p2)).isTrue();
        assertThat(p1.equals(p3)).isFalse();
        assertThat(p1.equals(null)).isFalse();
    }
    
    @Test
    @DisplayName("Should generate consistent hashCode")
    void testHashCode() {
        Person p1 = new Person("John", 30);
        Person p2 = new Person("John", 30);
        
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }
    
    @Test
    @DisplayName("Should handle zero age")
    void testZeroAge() {
        Person baby = new Person("Baby", 0);
        assertThat(baby.getAge()).isEqualTo(0);
        assertThat(baby.isAdult()).isFalse();
    }
    
    @Test
    @DisplayName("Should handle very old age")
    void testVeryOldAge() {
        Person elder = new Person("Elder", 120);
        assertThat(elder.getAge()).isEqualTo(120);
        assertThat(elder.isAdult()).isTrue();
    }
}