package com.learning.functional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Demonstrations of core functional paradigms: Immutability, Pure Functions, and Optionals.
 */
public class CoreFunctionalConcepts {

    // =========================================================================
    // 1. Immutability (Using Java Records)
    // =========================================================================
    // Records are immutable by default. All fields are final.
    public record ImmutableCustomer(int id, String name, String email) {
        
        // "With" methods return a NEW instance instead of mutating the current one
        public ImmutableCustomer withEmail(String newEmail) {
            return new ImmutableCustomer(this.id, this.name, newEmail);
        }
    }

    // =========================================================================
    // 2. Pure Functions
    // =========================================================================
    // IMPURE: Mutates external state
    private static int globalDiscount = 10;
    public static double applyImpureDiscount(double price) {
        return price - (price * (globalDiscount / 100.0));
    }

    // PURE: Output depends ONLY on input, no side effects
    public static double applyPureDiscount(double price, int discountPercentage) {
        return price - (price * (discountPercentage / 100.0));
    }

    // PURE: Does not mutate the input list, returns a new list
    public static List<String> toUpperCasePure(List<String> input) {
        return input.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // 3. Optional Monad Handling
    // =========================================================================
    public record Address(String city, String zipCode) {}
    public record Employee(String name, Optional<Address> address) {}

    // Safe extraction without NullPointerException
    public static String getEmployeeCity(Employee employee) {
        return Optional.ofNullable(employee)
                .flatMap(Employee::address)
                .map(Address::city)
                .orElse("Unknown City"); // Fallback value
    }

    // Using orElseGet for lazy evaluation of the fallback
    public static String getEmployeeCityLazyFallback(Employee employee) {
        return Optional.ofNullable(employee)
                .flatMap(Employee::address)
                .map(Address::city)
                .orElseGet(() -> computeDefaultCity()); 
    }

    private static String computeDefaultCity() {
        // Expensive computation simulated here
        return "Default HQ City";
    }
}