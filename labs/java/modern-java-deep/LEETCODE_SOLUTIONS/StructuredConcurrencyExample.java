package modernjava;

import java.time.Duration;
import java.util.concurrent.*;

/**
 * Structured Concurrency (Java 21+ — preview).
 * 
 * Structured concurrency treats groups of related tasks as a single unit.
 * If a subtask fails, other related subtasks are cancelled.
 * 
 * Key classes:
 * - StructuredTaskScope: base class for task scopes
 * - ShutdownOnFailure: cancel all if any fails
 * - ShutdownOnSuccess: cancel all if any succeeds
 * 
 * Benefits:
 * - Automatic cancellation on failure
 * - Clear task lifecycle
 * - Error propagation
 * 
 * Time: O(max(task duration))
 * Space: O(tasks)
 */
public class StructuredConcurrencyExample {

    record User(int id, String name) {}
    record Account(int userId, double balance) {}

    // Simulated remote calls
    static User fetchUser(int id) {
        try { Thread.sleep(100); } catch (InterruptedException e) { }
        if (id == 0) throw new RuntimeException("User not found");
        return new User(id, "User" + id);
    }

    static Account fetchAccount(int userId) {
        try { Thread.sleep(150); } catch (InterruptedException e) { }
        return new Account(userId, 1000.0);
    }

    public static void main(String[] args) throws Exception {
        // Structured concurrency using StructuredTaskScope
        // Note: StructuredTaskScope is preview in Java 21, may need --enable-preview

        // Simulate the structured approach with CompletableFuture
        // (StructuredTaskScope requires preview feature flags)

        // Structured pattern using try-with-resources
        // try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        //     Future<User> userFuture = scope.fork(() -> fetchUser(1));
        //     Future<Account> accountFuture = scope.fork(() -> fetchAccount(1));
        //
        //     scope.join();             // Wait for all
        //     scope.throwIfFailed();    // Propagate error
        //
        //     User user = userFuture.resultNow();
        //     Account account = accountFuture.resultNow();
        //     return new Response(user, account);
        // }

        // Demonstrate the concept using CompletableFuture (production-ready alternative)
        CompletableFuture<User> userF = CompletableFuture.supplyAsync(() -> fetchUser(1));
        CompletableFuture<Account> acctF = CompletableFuture.supplyAsync(() -> fetchAccount(1));

        // Wait for all
        CompletableFuture.allOf(userF, acctF).join();

        User user = userF.get();
        Account account = acctF.get();
        assert user.id() == 1;
        assert account.userId() == 1;
        assert account.balance() == 1000.0;

        // Error handling: if one fails, cancel others
        CompletableFuture<User> failing = CompletableFuture.supplyAsync(() -> fetchUser(0));
        CompletableFuture<Account> acct2 = CompletableFuture.supplyAsync(() -> fetchAccount(1));

        try {
            CompletableFuture.allOf(failing, acct2).join();
            assert false : "Should throw";
        } catch (CompletionException e) {
            System.out.println("Error correctly propagated: " + e.getCause().getMessage());
        }

        System.out.println("All StructuredConcurrencyExample tests passed.");
    }
}