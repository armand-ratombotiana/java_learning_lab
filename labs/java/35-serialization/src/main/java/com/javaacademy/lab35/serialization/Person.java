package com.javaacademy.lab35.serialization;

import java.io.*;
import java.util.Objects;

public class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final int age;
    private transient final String password;
    private final String email;
    private static int instanceCount = 0;

    public Person(String name, int age, String password, String email) {
        this.name = name;
        this.age = age;
        this.password = password;
        this.email = email;
        instanceCount++;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public static int getInstanceCount() { return instanceCount; }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(password != null ? password : "");
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        String pwd = in.readUTF();
        try {
            java.lang.reflect.Field pwdField = Person.class.getDeclaredField("password");
            pwdField.setAccessible(true);
            pwdField.set(this, pwd.isEmpty() ? null : pwd);
        } catch (Exception e) {
            throw new IOException("Failed to restore transient field", e);
        }
    }

    private Object readResolve() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        return age == person.age && Objects.equals(name, person.name) && Objects.equals(email, person.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, email);
    }
}
