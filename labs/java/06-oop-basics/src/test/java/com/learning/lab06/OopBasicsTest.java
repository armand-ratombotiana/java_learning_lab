package com.learning.lab06;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class OopBasicsTest {

    @Test
    @DisplayName("Class instantiation and field access")
    void classInstantiation() {
        Person p = new Person("Alice", 30);
        assertEquals("Alice", p.getName());
        assertEquals(30, p.getAge());
    }

    @Test
    @DisplayName("Encapsulation with getters/setters")
    void encapsulation() {
        Person p = new Person("Bob", 25);
        p.setName("Robert");
        p.setAge(26);
        assertEquals("Robert", p.getName());
        assertEquals(26, p.getAge());
    }

    @Test
    @DisplayName("Constructors initialize state")
    void constructors() {
        Person defaultPerson = new Person();
        assertEquals("Unknown", defaultPerson.getName());
        assertEquals(0, defaultPerson.getAge());
    }

    @Test
    @DisplayName("Static fields are shared across instances")
    void staticFields() {
        int before = Person.getCount();
        new Person("A", 1);
        new Person("B", 2);
        assertEquals(before + 2, Person.getCount());
    }

    @Test
    @DisplayName("Static methods called without instance")
    void staticMethods() {
        assertTrue(Person.isValidAge(25));
        assertFalse(Person.isValidAge(-1));
        assertFalse(Person.isValidAge(200));
    }

    @Test
    @DisplayName("Object initialization order")
    void objectInitialization() {
        var obj = new InitializationDemo();
        assertEquals(42, obj.getValue());
        assertEquals("initialized", obj.getText());
    }

    @Test
    @DisplayName("This keyword refers to current instance")
    void thisKeyword() {
        Person p = new Person("Self", 20);
        assertEquals("Self", p.getName());
    }

    @Test
    @DisplayName("Method overloading in same class")
    void methodOverloading() {
        Person p = new Person("Test", 10);
        assertEquals("Test", p.getName());
    }

    @Test
    @DisplayName("toString override")
    void toStringOverride() {
        Person p = new Person("Alice", 30);
        assertTrue(p.toString().contains("Alice"));
    }

    @Test
    @DisplayName("Equals and hashCode contract")
    void equalsHashCode() {
        Person p1 = new Person("Alice", 30);
        Person p2 = new Person("Alice", 30);
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}

class Person {
    private static int count = 0;
    private String name;
    private int age;

    public Person() { this("Unknown", 0); }
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
        count++;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public static int getCount() { return count; }
    public static boolean isValidAge(int age) { return age >= 0 && age <= 150; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person p)) return false;
        return age == p.age && name.equals(p.name);
    }

    @Override
    public int hashCode() { return 31 * name.hashCode() + age; }

    @Override
    public String toString() { return "Person{name='" + name + "', age=" + age + "}"; }
}

class InitializationDemo {
    private int value;
    private String text;

    { value = 42; text = "initialized"; }

    public int getValue() { return value; }
    public String getText() { return text; }
}
