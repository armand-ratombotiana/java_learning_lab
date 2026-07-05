package com.learning.lab07;

/**
 * Demonstrates abstract class inheritance hierarchy with constructor chaining.
 */
public class AbstractInheritanceExample {

    public static void showAbstractInheritance() {
        System.out.println("=== Abstract Class Inheritance ===");

        Manager mgr = new Manager("Alice", "Engineering", 5);
        System.out.println(mgr.getDetails());
        mgr.work();
    }
}

abstract class EmployeeRecord {
    protected String name;
    protected String department;

    public EmployeeRecord(String name, String department) {
        this.name = name;
        this.department = department;
    }

    public abstract String getDetails();
}

class Manager extends EmployeeRecord {
    private int teamSize;

    public Manager(String name, String department, int teamSize) {
        super(name, department);
        this.teamSize = teamSize;
    }

    @Override
    public String getDetails() {
        return "Manager " + name + " in " + department + " leads " + teamSize + " people";
    }

    public void work() {
        System.out.println(name + " is managing the team");
    }
}
