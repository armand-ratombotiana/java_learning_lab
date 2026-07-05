package com.learning.lab06;

/**
 * Demonstrates encapsulation with getters and setters, and JavaBeans pattern.
 */
public class GetterSetterExample {

    public static void showGettersSetters() {
        System.out.println("=== Encapsulation (Getters/Setters) ===");

        Employee emp = new Employee();
        emp.setId(1001);
        emp.setName("Alice Smith");
        emp.setSalary(75000.0);

        System.out.println("Employee Info:");
        System.out.println("  ID: " + emp.getId());
        System.out.println("  Name: " + emp.getName());
        System.out.println("  Salary: $" + emp.getSalary());
        System.out.println("  Annual bonus: $" + emp.calculateBonus());

        emp.setSalary(-1000);
        System.out.println("  After invalid salary: $" + emp.getSalary());
    }
}

class Employee {
    private int id;
    private String name;
    private double salary;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getSalary() { return salary; }

    public void setSalary(double salary) {
        if (salary >= 0) {
            this.salary = salary;
        } else {
            System.out.println("  Invalid salary, setting to 0");
            this.salary = 0;
        }
    }

    public double calculateBonus() {
        return salary * 0.10;
    }
}
