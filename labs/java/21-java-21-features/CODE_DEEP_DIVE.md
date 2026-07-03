# Code Deep Dive: Java 21 Features

## Virtual Threads: Building a High-Concurrency Web Server

```java
import java.util.concurrent.*;
import java.net.*;
import java.io.*;

public class VirtualThreadServer {
    private static final int PORT = 8080;
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);
            while (true) {
                Socket client = server.accept();
                executor.submit(() -> handle(client));
            }
        }
    }

    private static void handle(Socket client) {
        try (client; BufferedReader reader = new BufferedReader(
                new InputStreamReader(client.getInputStream()));
             PrintWriter writer = new PrintWriter(client.getOutputStream(), true)) {

            String request = reader.readLine();
            System.out.println(Thread.currentThread() + " processing: " + request);

            // Simulate blocking I/O (database call, external API, etc.)
            String response = processRequest(request);

            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: text/plain");
            writer.println();
            writer.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String processRequest(String request) {
        try {
            // Simulate blocking microservice call
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Processed by virtual thread: " + Thread.currentThread().threadId();
    }
}
```

**Key observations:**
- Each connection gets a virtual thread (not a platform thread)
- The server can handle millions of concurrent connections
- Code is synchronous and simple — no reactive chaining
- `ExecutorService` shuts down cleanly (no orphaned threads)

## Pattern Matching: Expression Evaluator

```java
sealed interface Expr permits Constant, Add, Multiply, Negate {}
record Constant(int value) implements Expr {}
record Add(Expr left, Expr right) implements Expr {}
record Multiply(Expr left, Expr right) implements Expr {}
record Negate(Expr expr) implements Expr {}

public int evaluate(Expr expr) {
    return switch (expr) {
        case Constant(int v)                 -> v;
        case Add(Expr l, Expr r)             -> evaluate(l) + evaluate(r);
        case Multiply(Expr l, Expr r)        -> evaluate(l) * evaluate(r);
        case Negate(Expr e)                  -> -evaluate(e);
    };
    // No default needed — sealed hierarchy makes this exhaustive
}
```

**Nested pattern matching in action:**
```java
// Match pattern: -(a + b) → -a - b
public Expr simplify(Expr expr) {
    return switch (expr) {
        case Negate(Add(Expr a, Expr b)) -> new Add(simplify(new Negate(a)), simplify(new Negate(b)));
        case Negate(Multiply(Expr a, Expr b)) -> new Multiply(simplify(new Negate(a)), b);
        default -> expr;
    };
}
```

## Sequenced Collections: Reversible Pipeline

```java
record Transaction(String id, double amount, long timestamp) {}

public List<Transaction> processTransactions(Collection<Transaction> txns) {
    // If the collection is ordered, preserve and reverse order
    if (txns instanceof SequencedCollection<Transaction> sc) {
        return sc.reversed()
                 .stream()
                 .filter(t -> t.amount() > 0)
                 .sorted(Comparator.comparingLong(Transaction::timestamp))
                 .toList();
    }
    // Fallback for unordered collections
    return txns.stream()
               .filter(t -> t.amount() > 0)
               .sorted(Comparator.comparingLong(Transaction::timestamp))
               .toList();
}
```

## String Templates: Safe SQL Builder

```java
import static java.util.FormatProcessor.FMT;

// Custom SQL template processor
StringQueryProcessor SQL = StringTemplate.Processor.of(
    (StringTemplate st) -> {
        StringBuilder query = new StringBuilder();
        List<String> fragments = st.fragments();
        List<Object> values = st.values();

        // Build parameterized query
        for (int i = 0; i < fragments.size(); i++) {
            query.append(fragments.get(i));
            if (i < values.size()) {
                query.append("?");  // parameter placeholder
            }
        }

        // Return a custom query object with both SQL and params
        return new StringQueryProcessor(query.toString(), values);
    }
);

// Usage
StringQueryProcessor query = SQL."SELECT * FROM users WHERE name = \{userName} AND age > \{minAge}";
// query.sql() = "SELECT * FROM users WHERE name = ? AND age > ?"
// query.params() = [userName, minAge]
```

## Structured Concurrency: Resilient Microservice Caller

```java
record UserData(String name, String email, Address address) {}
record Address(String street, String city, String zip) {}

public UserData fetchUserData(int userId) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        Future<String> name  = scope.fork(() -> fetchUserName(userId));
        Future<String> email = scope.fork(() -> fetchUserEmail(userId));
        Future<Address> addr = scope.fork(() -> fetchUserAddress(userId));

        scope.join();
        scope.throwIfFailed();

        return new UserData(name.resultNow(), email.resultNow(), addr.resultNow());
    }
}

private String fetchUserName(int id) {
    // Simulate network call
    try { Thread.sleep(50); } catch (InterruptedException e) { throw new RuntimeException(e); }
    return "Alice";
}

private String fetchUserEmail(int id) {
    try { Thread.sleep(30); } catch (InterruptedException e) { throw new RuntimeException(e); }
    return "alice@example.com";
}

private Address fetchUserAddress(int id) {
    try { Thread.sleep(80); } catch (InterruptedException e) { throw new RuntimeException(e); }
    return new Address("123 Main St", "Springfield", "12345");
}
```

All three fetches run concurrently. If any fails, the others are cancelled automatically via `ShutdownOnFailure`.
