# Spring Data JPA Code Deep Dive

This lab demonstrates how to optimize Spring Data JPA queries using Entity Graphs to solve the N+1 problem, and how to implement custom repository fragments.

## 💻 Pure Java Implementation

```java file="labs/spring-boot/02-data-jpa-internals/SOLUTION/JpaOptimizationDemo.java"
package springboot.jpa.internals;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * A demonstration of JPA query optimization.
 */
public class JpaOptimizationDemo {

    @Entity
    static class User {
        @Id @GeneratedValue long id;
        String username;

        // Lazy loading by default
        @OneToMany(fetch = FetchType.LAZY)
        List<Post> posts;
    }

    @Entity
    static class Post {
        @Id @GeneratedValue long id;
        String content;
    }

    @Repository
    public interface UserRepository extends JpaRepository<User, Long> {

        // ❌ THE SLOW WAY: Triggers N+1 queries if you access posts
        List<User> findByUsername(String username);

        // ✅ THE FAST WAY: Forces a JOIN FETCH in a single query
        @EntityGraph(attributePaths = {"posts"})
        List<User> findAllWithPostsByUsername(String username);
        
        // 💡 CUSTOM FRAGMENT: When method names aren't enough
        // Spring will look for an implementation named UserRepositoryCustomImpl
        // and merge it into the proxy!
    }

    /**
     * Interface for the custom fragment
     */
    public interface UserRepositoryCustom {
        void performComplexBusinessLogic(long userId);
    }

    /**
     * Implementation of the custom fragment.
     * Spring Data JPA automatically discovers this by the 'Impl' suffix.
     */
    public static class UserRepositoryCustomImpl implements UserRepositoryCustom {
        
        @PersistenceContext
        private EntityManager entityManager;

        @Override
        public void performComplexBusinessLogic(long userId) {
            // Here you can use raw EntityManager, Criteria API, or even 
            // native SQL for complex reporting queries.
            System.out.println("[CUSTOM] Executing complex logic for user: " + userId);
        }
    }
}
```

## 🔍 Key Takeaways
1. **The 'Impl' Suffix**: Notice the `UserRepositoryCustomImpl` class. Spring Data JPA uses a naming convention (configurable via `repository-implementation-postfix`) to find your custom code and wire it into the repository proxy. This is how you escape the "method name" cage for complex logic.
2. **Entity Graphs**: The `@EntityGraph` annotation is a declarative way to control fetching. It is safer than `JOIN FETCH` inside a `@Query` string because it is validated against your entity model at startup.
3. **The Proxy in Debugger**: If you set a breakpoint on a repository call and look at the `userRepository` variable, you will see its class name is something like `com.sun.proxy.$Proxy88`. This confirms that Spring generated a dynamic proxy at runtime.