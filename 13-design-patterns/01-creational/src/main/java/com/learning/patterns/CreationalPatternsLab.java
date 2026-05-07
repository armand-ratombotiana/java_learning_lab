package com.learning.patterns;

public class CreationalPatternsLab {

    public static void main(String[] args) {
        System.out.println("=== Creational Design Patterns ===\n");

        demonstrateSingleton();
        demonstrateFactory();
        demonstrateBuilder();
    }

    private static void demonstrateSingleton() {
        System.out.println("--- Singleton Pattern ---");
        DatabaseConnection conn1 = DatabaseConnection.getInstance();
        DatabaseConnection conn2 = DatabaseConnection.getInstance();
        System.out.println("Same instance: " + (conn1 == conn2));
    }

    private static void demonstrateFactory() {
        System.out.println("\n--- Factory Pattern ---");
        Notification email = NotificationFactory.create("EMAIL");
        Notification sms = NotificationFactory.create("SMS");
        email.send("Hello");
        sms.send("World");
    }

    private static void demonstrateBuilder() {
        System.out.println("\n--- Builder Pattern ---");
        User user = new User.UserBuilder()
            .name("John")
            .email("john@test.com")
            .age(30)
            .build();
        System.out.println("User: " + user.getName() + ", " + user.getEmail());
    }
}

class DatabaseConnection {
    private static DatabaseConnection instance;
    private DatabaseConnection() {}

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) instance = new DatabaseConnection();
        return instance;
    }
}

class NotificationFactory {
    public static Notification create(String type) {
        return switch (type) {
            case "EMAIL" -> new EmailNotification();
            case "SMS" -> new SMSNotification();
            default -> throw new IllegalArgumentException("Unknown type");
        };
    }
}

interface Notification {
    void send(String message);
}

class EmailNotification implements Notification {
    public void send(String message) {
        System.out.println("Email sent: " + message);
    }
}

class SMSNotification implements Notification {
    public void send(String message) {
        System.out.println("SMS sent: " + message);
    }
}

class User {
    private String name, email;
    private int age;

    private User(UserBuilder builder) {
        this.name = builder.name;
        this.email = builder.email;
        this.age = builder.age;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getAge() { return age; }

    static class UserBuilder {
        private String name, email;
        private int age;
        public UserBuilder name(String name) { this.name = name; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder age(int age) { this.age = age; return this; }
        public User build() { return new User(this); }
    }
}