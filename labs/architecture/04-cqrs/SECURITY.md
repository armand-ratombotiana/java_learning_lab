# CQRS Security

## Security Separation

### Command Authorization
```java
@Component
public class SecureCommandBus {
    public <R> CompletableFuture<R> dispatch(Command<R> command, Principal principal) {
        if (!authzService.canExecute(principal, command.getClass())) {
            return CompletableFuture.failedFuture(
                new AuthorizationException("Access denied"));
        }
        return commandBus.dispatch(command);
    }
}
```

### Query Authorization
```java
@Component
public class SecureQueryBus {
    public <R> CompletableFuture<R> query(Query<R> query, Principal principal) {
        // Different authorization rules for reads
        if (!authzService.canRead(principal, query.getClass())) {
            return CompletableFuture.failedFuture(
                new AuthorizationException("Access denied"));
        }
        return queryBus.query(query, R.class);
    }
}
```

### Field-Level Security
```java
@EventHandler
public void on(AccountCreatedEvent event) {
    AccountView view = new AccountView(event);
    if (!currentUser().isAdmin()) {
        view.maskSensitiveData(); // Hide balance for non-admin
    }
    repository.save(view);
}
```

## Audit Logging
- Log all commands for audit trail
- Log all queries for access monitoring
- Never log sensitive data in queries
- Separate read/write audit logs
