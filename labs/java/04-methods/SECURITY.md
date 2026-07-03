# Security — Methods

## Validate Parameters

Always validate method parameters for security-critical methods:
```java
public void transferMoney(Account from, Account to, double amount) {
    Objects.requireNonNull(from, "from account");
    Objects.requireNonNull(to, "to account");
    if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
    if (amount > from.getBalance()) throw new InsufficientFundsException(...);
}
```

## Return Mutable Internal State

Returning a reference to internal mutable state allows callers to bypass encapsulation:
```java
public List<String> getItems() {
    return new ArrayList<>(items);  // Defensive copy
}
```

## Sensitive Parameters in Logs

Method parameters containing passwords, tokens, or PII should be masked before logging.

## varargs with Generics

`@SafeVarargs` suppresses heap pollution warnings — use only when method is safe.

## Override Security-Critical Methods

Classes like `ClassLoader`, `URLConnection`, `Authenticator` have security-sensitive methods. Override with caution.
