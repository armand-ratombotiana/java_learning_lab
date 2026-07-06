package com.javaacademy.lab40.bestpractices;

import java.util.Objects;

public final class EqualsHashCodeExample {
    private final String name;
    private final int age;
    private final String email;

    public EqualsHashCodeExample(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EqualsHashCodeExample that)) return false;
        return age == that.age && Objects.equals(name, that.name) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, email);
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + ", email='" + email + "'}";
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getEmail() { return email; }
}
