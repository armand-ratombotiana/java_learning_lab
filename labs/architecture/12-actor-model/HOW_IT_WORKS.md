# How Actor Model Works

## Architecture Flow
```
[Message] -> Actor's Mailbox -> [Process sequentially] -> [Send messages / Create actors / Change behavior]
```

## Complete Example

### Message Definitions
```java
// Messages for a bank account actor
public sealed interface AccountCommand {}
public record Deposit(BigDecimal amount) implements AccountCommand {}
public record Withdraw(BigDecimal amount) implements AccountCommand {}
public record GetBalance(ActorRef<BalanceResponse> replyTo) implements AccountCommand {}
public record BalanceResponse(BigDecimal balance) {}
```

### Account Actor
```java
public class AccountActor {

    // Message protocol
    public sealed interface Command {}
    public record Deposit(BigDecimal amount, String from) implements Command {}
    public record Withdraw(BigDecimal amount) implements Command {}
    public record GetBalance(ActorRef<Balance> replyTo) implements Command {}
    public record Balance(BigDecimal amount) {}

    // Actor behavior
    public static Behavior<Command> create(String accountId, BigDecimal initialBalance) {
        return Behaviors.setup(ctx -> active(accountId, initialBalance));
    }

    private static Behavior<Command> active(String accountId, BigDecimal balance) {
        return Behaviors.receive(Command.class)
            .onMessage(Deposit.class, msg -> {
                BigDecimal newBalance = balance.add(msg.amount());
                System.out.printf("Deposited %s to %s. New balance: %s%n",
                    msg.amount(), accountId, newBalance);
                return active(accountId, newBalance);
            })
            .onMessage(Withdraw.class, msg -> {
                if (balance.compareTo(msg.amount()) < 0) {
                    System.out.printf("Insufficient funds in %s%n", accountId);
                    return Behaviors.same();
                }
                BigDecimal newBalance = balance.subtract(msg.amount());
                System.out.printf("Withdrew %s from %s. New balance: %s%n",
                    msg.amount(), accountId, newBalance);
                return active(accountId, newBalance);
            })
            .onMessage(GetBalance.class, (msg, ctx) -> {
                msg.replyTo().tell(new Balance(balance));
                return Behaviors.same();
            })
            .build();
    }
}
```

### Usage
```java
ActorSystem<AccountActor.Command> system = ActorSystem.create(
    AccountActor.create("ACC-001", BigDecimal.valueOf(1000)),
    "bank-system");

ActorRef<AccountActor.Command> account = system;

// Send messages
account.tell(new AccountActor.Deposit(BigDecimal.valueOf(500), "Alice"));
account.tell(new AccountActor.Withdraw(BigDecimal.valueOf(200)));
```

### Ask Pattern (Request-Response)
```java
// Ask pattern for request-response
Duration timeout = Duration.ofSeconds(3);
CompletionStage<AccountActor.Balance> future =
    AskPattern.ask(system,
        (ActorRef<AccountActor.Balance> ref) ->
            new AccountActor.GetBalance(ref),
        timeout,
        system.scheduler());

future.thenAccept(balance ->
    System.out.println("Current balance: " + balance.amount()));
```
