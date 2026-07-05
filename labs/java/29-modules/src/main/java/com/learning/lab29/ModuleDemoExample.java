package com.learning.lab29;

import com.learning.lab29.service.UserService;

/**
 * Demonstrates module concepts — using exported packages from a service.
 * In a real module setup, module-info.java would declare 'exports' and 'requires'.
 */
public class ModuleDemoExample {

    public static void showModuleConcepts() {
        System.out.println("=== Module System Demo ===");

        UserService service = new UserService();

        String name = service.getUserName(42);
        String greeting = service.formatGreeting(name);

        System.out.println("  " + greeting);
        service.listAllUsers();

        System.out.println("  Module: " + ModuleDemoExample.class.getModule().getName());
        System.out.println("  Package: " + ModuleDemoExample.class.getPackageName());
        System.out.println("  Is named module: " 
            + ModuleDemoExample.class.getModule().isNamed());
    }
}
