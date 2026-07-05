package com.learning.lab29.service;

/**
 * A public service class that would be exported via module-info.java.
 * Demonstrates the service provider side of the module system.
 */
public class UserService {

    public String getUserName(int id) {
        return "User_" + id;
    }

    public String formatGreeting(String name) {
        return "Hello, " + name + "! Welcome to Java Modules.";
    }

    public void listAllUsers() {
        System.out.println("  User list: [Alice, Bob, Charlie]");
    }
}
