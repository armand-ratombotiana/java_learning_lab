package com.learning.optional;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates advanced Optional patterns and best practices.
 * Shows practical patterns for working with null-prone code.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class OptionalPatternsDemo {
    
    /**
     * Demonstrates the classic nullable check vs Optional pattern.
     */
    public void demonstrateNullableVsOptional() {
        System.out.println("--- NULLABLE VS OPTIONAL PATTERN ---");
        
        Customer customer = getCustomer(1);
        
        // Traditional null checking
        System.out.println("Traditional null check:");
        if (customer != null) {
            String email = customer.getEmail();
            if (email != null) {
                System.out.println("  Email: " + email);
            }
        }
        
        // Optional pattern
        System.out.println("\nOptional pattern:");
        getCustomerOptional(1)
            .map(Customer::getEmail)
            .ifPresent(email -> System.out.println("  Email: " + email));
    }
    
    /**
     * Demonstrates chaining Optional operations.
     */
    public void demonstrateOptionalChaining() {
        System.out.println("\n--- OPTIONAL CHAINING ---");
        
        // Flat map for chaining Optional-returning methods
        var result = getCustomerOptional(1)
            .flatMap(cust -> getPreferenceOptional(cust.getId()))
            .map(Preference::getTheme)
            .orElse("Default Theme");
        
        System.out.println("Customer theme: " + result);
        
        // Multiple flatMaps
        var address = getCustomerOptional(1)
            .flatMap(c -> getAddressOptional(c.getId()))
            .map(Address::getCity)
            .orElse("Unknown City");
        
        System.out.println("Customer city: " + address);
    }
    
    /**
     * Demonstrates filtering with Optional.
     */
    public void demonstrateOptionalFiltering() {
        System.out.println("\n--- OPTIONAL FILTERING ---");
        
        List<Integer> ages = Arrays.asList(15, 25, 35, 10, 30);
        
        ages.stream()
            .map(this::findPersonOptional)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(p -> p.getAge() >= 18)
            .forEach(p -> System.out.println("  Adult: " + p.getName()));
        
        // Using flatMap to handle Optional
        System.out.println("\nUsing flatMap:");
        ages.stream()
            .flatMap(age -> findPersonOptional(age).stream())
            .filter(p -> p.getAge() >= 18)
            .forEach(p -> System.out.println("  Adult: " + p.getName()));
    }
    
    /**
     * Demonstrates Optional with streams - findAny and findFirst.
     */
    public void demonstrateStreamOptionalOperations() {
        System.out.println("\n--- STREAM OPTIONAL OPERATIONS ---");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // findFirst returns first element in stream
        var firstEven = numbers.stream()
            .filter(n -> n % 2 == 0)
            .findFirst();
        System.out.println("First even: " + firstEven.orElse(-1));
        
        // findAny returns any element (useful for parallel streams)
        var anyGreaterThanFive = numbers.stream()
            .filter(n -> n > 5)
            .findAny();
        System.out.println("Any greater than 5: " + anyGreaterThanFive.orElse(-1));
        
        // Matching operations
        boolean allPositive = numbers.stream().allMatch(n -> n > 0);
        boolean anyNegative = numbers.stream().anyMatch(n -> n < 0);
        boolean noneZero = numbers.stream().noneMatch(n -> n == 0);
        
        System.out.println("All positive: " + allPositive);
        System.out.println("Has negative: " + anyNegative);
        System.out.println("No zeros: " + noneZero);
    }
    
    /**
     * Demonstrates Optional with default value patterns.
     */
    public void demonstrateOptionalDefaults() {
        System.out.println("\n--- OPTIONAL DEFAULT PATTERNS ---");
        
        Optional<String> empty = Optional.empty();
        Optional<String> present = Optional.of("Value");
        
        // orElse - returns default value (always evaluated)
        String val1 = empty.orElse(getDefaultValue());
        System.out.println("orElse with computation: " + val1);
        
        // orElseGet - lazy evaluation of default
        String val2 = empty.orElseGet(this::getDefaultValue);
        System.out.println("orElseGet with lazy computation: " + val2);
        
        // orElseThrow - throws exception if empty
        try {
            String val3 = empty.orElseThrow(() -> 
                new IllegalArgumentException("Value is required"));
        } catch (IllegalArgumentException e) {
            System.out.println("Caught exception: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrates conditional logic with Optional.
     */
    public void demonstrateOptionalConditionalLogic() {
        System.out.println("\n--- OPTIONAL CONDITIONAL LOGIC ---");
        
        // ifPresentOrElse
        getCustomerOptional(1).ifPresentOrElse(
            cust -> System.out.println("Customer found: " + cust.getName()),
            () -> System.out.println("Customer not found")
        );
        
        // or() method (Java 9+) - provide alternative Optional
        var customerResult = getCustomerOptional(999)
            .or(() -> getCustomerOptional(1));
        
        customerResult.ifPresent(c -> 
            System.out.println("Found alternative customer: " + c.getName())
        );
    }
    
    /**
     * Demonstrates Optional with transform operations.
     */
    public void demonstrateOptionalTransformations() {
        System.out.println("\n--- OPTIONAL TRANSFORMATIONS ---");
        
        // map - transform value if present
        var uppercaseName = getCustomerOptional(1)
            .map(Customer::getName)
            .map(String::toUpperCase);
        System.out.println("Uppercase name: " + uppercaseName.orElse("N/A"));
        
        // flatMap - chain Optional-returning operations
        var emailDomain = getCustomerOptional(1)
            .flatMap(c -> getPreferenceOptional(c.getId()))
            .map(Preference::getNotificationEmail)
            .map(email -> email.substring(email.indexOf("@") + 1));
        System.out.println("Email domain: " + emailDomain.orElse("N/A"));
    }
    
    /**
     * Demonstrates Optional stream integration.
     */
    public void demonstrateOptionalStreams() {
        System.out.println("\n--- OPTIONAL IN STREAMS ---");
        
        List<Integer> customerIds = Arrays.asList(1, 999, 2, 888, 3);
        
        // Collect all found customers
        List<String> customerNames = customerIds.stream()
            .flatMap(id -> getCustomerOptional(id).stream())
            .map(Customer::getName)
            .collect(Collectors.toList());
        
        System.out.println("Found customers: " + customerNames);
        
        // Count found vs not found
        long foundCount = customerIds.stream()
            .flatMap(id -> getCustomerOptional(id).stream())
            .count();
        System.out.println("Found: " + foundCount + ", Not found: " + 
                         (customerIds.size() - foundCount));
    }
    
    /**
     * Demonstrates combining multiple Optional values.
     */
    public void demonstrateOptionalCombination() {
        System.out.println("\n--- OPTIONAL COMBINATION ---");
        
        Optional<String> firstName = Optional.of("John");
        Optional<String> lastName = Optional.of("Doe");
        
        // Combine if all present using flatMap
        String fullName = firstName
            .flatMap(first -> lastName
                .map(last -> first + " " + last))
            .orElse("Unknown");
        System.out.println("Full name: " + fullName);
        
        // Alternative approach with ifPresent
        if (firstName.isPresent() && lastName.isPresent()) {
            System.out.println("Alt - Full name: " + firstName.get() + " " + lastName.get());
        }
    }
    
    /**
     * Demonstrates avoiding Optional pitfalls.
     */
    public void demonstrateOptionalPitfalls() {
        System.out.println("\n--- OPTIONAL PITFALLS TO AVOID ---");
        
        Optional<String> value = Optional.of("Test");
        
        // WRONG: using get() without checking (can throw NoSuchElementException)
        // String wrong = Optional.empty().get(); // Don't do this!
        
        // RIGHT: use orElse, orElseGet, or ifPresent
        String right1 = Optional.<String>empty().orElse("default");
        Optional.<String>empty().ifPresent(System.out::println);
        
        // WRONG: Optional<Optional<T>> (double wrapping)
        // Optional<Optional<String>> wrong2 = Optional.of(Optional.of("test"));
        
        // RIGHT: use flatMap to unwrap
        var correct = getCustomerOptional(1)
            .flatMap(c -> getPreferenceOptional(c.getId()));
        
        System.out.println("Proper patterns applied");
    }
    
    // Helper methods
    
    private Optional<Customer> getCustomerOptional(int id) {
        if (id == 1) {
            return Optional.of(new Customer(1, "Alice", "alice@example.com"));
        }
        return Optional.empty();
    }
    
    private Customer getCustomer(int id) {
        if (id == 1) {
            return new Customer(1, "Alice", "alice@example.com");
        }
        return null;
    }
    
    private Optional<Preference> getPreferenceOptional(int customerId) {
        if (customerId == 1) {
            return Optional.of(new Preference(1, "Dark", "alice.pref@example.com"));
        }
        return Optional.empty();
    }
    
    private Optional<Address> getAddressOptional(int customerId) {
        if (customerId == 1) {
            return Optional.of(new Address(1, "123 Main St", "New York", "10001"));
        }
        return Optional.empty();
    }
    
    private Optional<Person> findPersonOptional(int age) {
        if (age >= 18) {
            return Optional.of(new Person("Person" + age, age));
        }
        return Optional.empty();
    }
    
    private String getDefaultValue() {
        System.out.println("    [Computing default value]");
        return "DEFAULT";
    }
    
    // Helper classes
    
    public static class Customer {
        private int id;
        private String name;
        private String email;
        
        public Customer(int id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
        
        public int getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
    }
    
    public static class Preference {
        private int customerId;
        private String theme;
        private String notificationEmail;
        
        public Preference(int customerId, String theme, String notificationEmail) {
            this.customerId = customerId;
            this.theme = theme;
            this.notificationEmail = notificationEmail;
        }
        
        public int getCustomerId() { return customerId; }
        public String getTheme() { return theme; }
        public String getNotificationEmail() { return notificationEmail; }
    }
    
    public static class Address {
        private int customerId;
        private String street;
        private String city;
        private String zip;
        
        public Address(int customerId, String street, String city, String zip) {
            this.customerId = customerId;
            this.street = street;
            this.city = city;
            this.zip = zip;
        }
        
        public int getCustomerId() { return customerId; }
        public String getStreet() { return street; }
        public String getCity() { return city; }
        public String getZip() { return zip; }
    }
    
    public static class Person {
        private String name;
        private int age;
        
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        public String getName() { return name; }
        public int getAge() { return age; }
    }
}
