package com.db.springdata;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

/**
 * Demonstrates Spring Data JPA Specifications for dynamic query building.
 *
 * Specifications allow type-safe, composable WHERE clauses
 * without writing raw queries or multiple derived methods.
 */
public class UserSpecification {

    /**
     * Finds users whose lastName matches (case-insensitive).
     */
    public static Specification<User> hasLastName(String lastName) {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (lastName == null || lastName.isBlank()) return null;
            return cb.like(cb.lower(root.get("lastName")),
                           "%" + lastName.toLowerCase() + "%");
        };
    }

    /**
     * Finds users whose age is >= minAge.
     */
    public static Specification<User> ageGreaterOrEqual(Integer minAge) {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (minAge == null) return null;
            return cb.greaterThanOrEqualTo(root.get("age"), minAge);
        };
    }

    /**
     * Finds users whose age is <= maxAge.
     */
    public static Specification<User> ageLessOrEqual(Integer maxAge) {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (maxAge == null) return null;
            return cb.lessThanOrEqualTo(root.get("age"), maxAge);
        };
    }

    /**
     * Finds only active users.
     */
    public static Specification<User> isActive() {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
            cb.isTrue(root.get("active"));
    }

    /**
     * Finds users with email containing a given domain.
     */
    public static Specification<User> emailContains(String domain) {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (domain == null || domain.isBlank()) return null;
            return cb.like(cb.lower(root.get("email")),
                           "%" + domain.toLowerCase() + "%");
        };
    }

    /**
     * Combines multiple specifications using AND.
     *
     * Usage:
     *   Specification<User> spec = Specification
     *       .where(UserSpecification.hasLastName("Smith"))
     *       .and(UserSpecification.ageGreaterOrEqual(18))
     *       .and(UserSpecification.isActive());
     *   List<User> users = repo.findAll(spec);
     */
    public static void showUsage() {
        System.out.println("=== JPA Specifications ===");
        System.out.println();
        System.out.println("Specifications are composable predicates:");
        System.out.println();
        System.out.println("  Specification<User> spec = Specification");
        System.out.println("      .where(UserSpecification.hasLastName(\"Smith\"))");
        System.out.println("      .and(UserSpecification.ageGreaterOrEqual(18))");
        System.out.println("      .and(UserSpecification.isActive());");
        System.out.println();
        System.out.println("  List<User> users = repository.findAll(spec);");
        System.out.println();
        System.out.println("Available specifications:");
        System.out.println("  - hasLastName(String)     — LIKE %value%");
        System.out.println("  - ageGreaterOrEqual(int)  — age >= value");
        System.out.println("  - ageLessOrEqual(int)     — age <= value");
        System.out.println("  - isActive()              — active = true");
        System.out.println("  - emailContains(String)   — LIKE %value%");
    }

    public static void main(String[] args) {
        showUsage();
    }
}
