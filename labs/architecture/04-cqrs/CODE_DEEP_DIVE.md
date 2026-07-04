# Code Deep Dive: CQRS

## Complete Axon CQRS Example

### POM Dependencies
```xml
<dependency>
    <groupId>org.axonframework</groupId>
    <artifactId>axon-spring-boot-starter</artifactId>
    <version>4.9.0</version>
</dependency>
```

### Command Model
```java
@Value
public class CreateAccountCommand {
    @TargetAggregateIdentifier
    String accountId;
    String ownerName;
    Money initialDeposit;
}

@Value
public class DepositMoneyCommand {
    @TargetAggregateIdentifier
    String accountId;
    Money amount;
}

@Value
public class WithdrawMoneyCommand {
    @TargetAggregateIdentifier
    String accountId;
    Money amount;
}
```

### Aggregate
```java
@Aggregate
@NoArgsConstructor
public class AccountAggregate {

    @AggregateIdentifier
    private String accountId;
    private Money balance;
    private AccountStatus status;

    @CommandHandler
    public AccountAggregate(CreateAccountCommand cmd) {
        AggregateLifecycle.apply(new AccountCreatedEvent(
            cmd.getAccountId(), cmd.getOwnerName(), cmd.getInitialDeposit()
        ));
    }

    @CommandHandler
    public void handle(DepositMoneyCommand cmd) {
        AggregateLifecycle.apply(new MoneyDepositedEvent(
            cmd.getAccountId(), cmd.getAmount()
        ));
    }

    @CommandHandler
    public void handle(WithdrawMoneyCommand cmd) {
        if (balance.getAmount().compareTo(cmd.getAmount().getAmount()) < 0) {
            throw new InsufficientBalanceException(accountId, balance, cmd.getAmount());
        }
        AggregateLifecycle.apply(new MoneyWithdrawnEvent(
            cmd.getAccountId(), cmd.getAmount()
        ));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.getAccountId();
        this.balance = event.getInitialDeposit();
        this.status = AccountStatus.ACTIVE;
    }

    @EventSourcingHandler
    public void on(MoneyDepositedEvent event) {
        this.balance = this.balance.add(event.getAmount());
    }

    @EventSourcingHandler
    public void on(MoneyWithdrawnEvent event) {
        this.balance = this.balance.subtract(event.getAmount());
    }
}
```

### Query Model
```java
@Value
public class FindAccountQuery {
    String accountId;
}

@Component
@RequiredArgsConstructor
public class AccountProjection {

    private final AccountViewRepository repository;

    @EventHandler
    public void on(AccountCreatedEvent event) {
        repository.save(new AccountView(
            event.getAccountId(),
            event.getOwnerName(),
            event.getInitialDeposit().getAmount(),
            "ACTIVE"
        ));
    }

    @EventHandler
    public void on(MoneyDepositedEvent event) {
        repository.findById(event.getAccountId()).ifPresent(view -> {
            view.setBalance(view.getBalance()
                .add(event.getAmount().getAmount()));
            repository.save(view);
        });
    }

    @EventHandler
    public void on(MoneyWithdrawnEvent event) {
        repository.findById(event.getAccountId()).ifPresent(view -> {
            view.setBalance(view.getBalance()
                .subtract(event.getAmount().getAmount()));
            repository.save(view);
        });
    }
}

@Component
public class AccountQueryHandler {

    private final AccountViewRepository repository;

    @QueryHandler
    public AccountView handle(FindAccountQuery query) {
        return repository.findById(query.getAccountId())
            .orElseThrow(() -> new AccountNotFoundException(query.getAccountId()));
    }
}
```
