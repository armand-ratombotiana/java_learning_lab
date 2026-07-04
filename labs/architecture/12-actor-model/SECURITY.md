# Actor Model Security

## Security Considerations

### Actor Authentication
```java
public class SecureActor {

    public static Behavior<Command> create() {
        return Behaviors.receive(Command.class)
            .onMessage(AuthenticatedMessage.class, (msg, ctx) -> {
                if (!validateToken(msg.token())) {
                    ctx.getLog().warn("Invalid token from {}", ctx.getSelf());
                    return Behaviors.same();
                }
                return processMessage(msg.payload());
            })
            .build();
    }
}
```

### Message Encryption
```java
public class EncryptedActor {

    public static Behavior<EncryptedMessage> create(SecretKey key) {
        return Behaviors.receive(EncryptedMessage.class)
            .onMessage(EncryptedMessage.class, msg -> {
                // Decrypt
                String plaintext = decrypt(msg.data(), key);
                // Process
                return Behaviors.same();
            })
            .build();
    }
}
```

### Actor-Level Authorization
```java
public class AuthorizedActor {

    private final Set<String> authorizedRoles = Set.of("ADMIN", "MANAGER");

    public Behavior<Command> create() {
        return Behaviors.receive(Command.class)
            .onMessage(SensitiveOperation.class, (msg, ctx) -> {
                if (!authorizedRoles.contains(msg.requesterRole())) {
                    ctx.getLog().warn("Unauthorized access by {}", msg.requesterId());
                    return Behaviors.same();
                }
                return performOperation(msg);
            })
            .build();
    }
}
```

## Security Best Practices
- Validate and sanitize all incoming messages
- Use encrypted message channels for sensitive data
- Implement actor-level authorization
- Log security events at actor boundaries
- Avoid passing sensitive data in messages
- Use dead letters for rejected messages
- Rate-limit actor message processing
