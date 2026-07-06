package com.javaacademy.lab40.bestpractices;

import java.util.Objects;

public class BuilderExample {
    private final String firstName;
    private final String lastName;
    private final int age;
    private final String email;
    private final String phone;
    private final String address;

    private BuilderExample(Builder b) {
        this.firstName = b.firstName;
        this.lastName = b.lastName;
        this.age = b.age;
        this.email = b.email;
        this.phone = b.phone;
        this.address = b.address;
    }

    public static class Builder {
        private String firstName;
        private String lastName;
        private int age;
        private String email;
        private String phone;
        private String address;

        public Builder firstName(String val) { this.firstName = val; return this; }
        public Builder lastName(String val) { this.lastName = val; return this; }
        public Builder age(int val) { this.age = val; return this; }
        public Builder email(String val) { this.email = val; return this; }
        public Builder phone(String val) { this.phone = val; return this; }
        public Builder address(String val) { this.address = val; return this; }

        public BuilderExample build() {
            Objects.requireNonNull(firstName, "firstName must not be null");
            Objects.requireNonNull(lastName, "lastName must not be null");
            return new BuilderExample(this);
        }
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getAge() { return age; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }

    public static Builder builder() { return new Builder(); }
}
