package com.db.springdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository demonstrating various query methods.
 *
 * Covers:
 * - Derived query methods (findByEmail, findByAgeBetween)
 * - Custom @Query with JPQL and native SQL
 * - Pagination support
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Derived query — Spring Data parses method name
    Optional<User> findByEmail(String email);

    List<User> findByLastName(String lastName);

    List<User> findByAgeBetween(int min, int max);

    List<User> findByActiveTrue();

    // JPQL custom query
    @Query("SELECT u FROM User u WHERE u.email LIKE %:domain")
    List<User> findByEmailDomain(@Param("domain") String domain);

    // Native SQL query
    @Query(value = "SELECT * FROM users WHERE age > :minAge ORDER BY age DESC LIMIT :limit",
           nativeQuery = true)
    List<User> findTopOldestUsers(@Param("minAge") int minAge, @Param("limit") int limit);

    // Count queries
    long countByActiveTrue();

    // Exists queries
    boolean existsByEmail(String email);

    // Delete by derived query
    void deleteByEmail(String email);

    /**
     * This is a showcase interface — no main method needed.
     * To use: @Autowired UserRepository repo;
     *
     * Example usage:
     *   repo.save(new User("Alice", "alice@example.com", 30));
     *   repo.findByEmail("alice@example.com");
     *   repo.findByAgeBetween(25, 40);
     *   repo.findByEmailDomain("@example.com");
     */
    static void showExamples() {
        System.out.println("=== Spring Data JPA Repository Methods ===\n");

        System.out.println("Derived query methods:");
        System.out.println("  Optional<User> findByEmail(String email)");
        System.out.println("  List<User> findByLastName(String lastName)");
        System.out.println("  List<User> findByAgeBetween(int min, int max)");
        System.out.println("  List<User> findByActiveTrue()");
        System.out.println("  long countByActiveTrue()");
        System.out.println("  boolean existsByEmail(String email)");
        System.out.println("  void deleteByEmail(String email)");

        System.out.println("\nCustom @Query methods:");
        System.out.println("  @Query JPQL: findByEmailDomain(@Param(\"domain\") String domain)");
        System.out.println("  @Query native: findTopOldestUsers(@Param(\"minAge\") int minAge, @Param(\"limit\") int limit)");
    }
}
