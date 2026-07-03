# Mini Project: Functional Validation & Processing Pipeline

## Objective
Build a robust data processing pipeline using functional composition. You will create a chain of `Predicate`s for validation, use `Optional` (Monad) for safe data retrieval, and compose `Function`s for data transformation.

## Prerequisites
*   Java 17+

## Step 1: The Domain Model
Create a simple `User` record and a simulated database.

```java
import java.util.Map;
import java.util.Optional;

public record User(String id, String email, int age) {}

public class UserRepository {
    private final Map<String, User> db = Map.of(
        "1", new User("1", "alice@example.com", 28),
        "2", new User("2", "bob@invalid", 15),
        "3", new User("3", "charlie@example.com", 45)
    );

    // Returns Optional (a Monad) because the user might not exist
    public Optional<User> findById(String id) {
        return Optional.ofNullable(db.get(id));
    }
}
```

## Step 2: Composing Predicates (Validation)
Create a set of validation rules and compose them into a single, strict validator.

```java
import java.util.function.Predicate;

public class UserValidator {
    // Individual rules
    private static final Predicate<User> isAdult = user -> user.age() >= 18;
    private static final Predicate<User> hasValidEmail = user -> user.email().contains("@");
    private static final Predicate<User> emailIsDomain = user -> user.email().endsWith("example.com");

    // Composed rule
    public static final Predicate<User> isValidForCampaign = 
        isAdult.and(hasValidEmail).and(emailIsDomain);
}
```

## Step 3: Composing Functions (Transformation)
Create a pipeline to transform a valid user into a formatted marketing string.

```java
import java.util.function.Function;

public class MarketingFormatter {
    // Individual transformations
    private static final Function<User, String> extractEmail = User::email;
    private static final Function<String, String> toUpperCase = String::toUpperCase;
    private static final Function<String, String> addPrefix = email -> "SEND_PROMO_TO: " + email;

    // Composed pipeline (Left to Right execution using andThen)
    public static final Function<User, String> formatPipeline = 
        extractEmail.andThen(toUpperCase).andThen(addPrefix);
}
```

## Step 4: The Monadic Workflow
Combine the repository (Optional), the validator (Predicate), and the formatter (Function) into a single, clean workflow.

```java
import java.util.Optional;

public class CampaignManager {
    private final UserRepository repo = new UserRepository();

    public void processUser(String userId) {
        System.out.println("Processing User ID: " + userId);

        // The Monadic Pipeline
        Optional<String> result = repo.findById(userId)              // 1. Fetch (Returns Optional)
            .filter(UserValidator.isValidForCampaign)                // 2. Validate
            .map(MarketingFormatter.formatPipeline);                 // 3. Transform

        // 4. Terminal Action
        result.ifPresentOrElse(
            promoString -> System.out.println("  SUCCESS -> " + promoString),
            () -> System.out.println("  FAILED  -> User invalid or not found.")
        );
    }
}
```

## Step 5: Execute the Pipeline
```java
public class Main {
    public static void main(String[] args) {
        CampaignManager manager = new CampaignManager();

        // Alice is 28 and has a valid example.com email (Should Succeed)
        manager.processUser("1");

        // Bob is 15 and has an invalid email (Should Fail Validation)
        manager.processUser("2");

        // User 99 does not exist (Should Fail at findById)
        manager.processUser("99");
    }
}
```

## Expected Output
Notice how clean the `processUser` method is. There are no `if-else` blocks, no null checks, and no nested loops. The logic is entirely declarative.
```text
Processing User ID: 1
  SUCCESS -> SEND_PROMO_TO: ALICE@EXAMPLE.COM
Processing User ID: 2
  FAILED  -> User invalid or not found.
Processing User ID: 99
  FAILED  -> User invalid or not found.
```