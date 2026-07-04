# Debugging Actor Model

## Common Issues

### 1. Dead Letters
```java
// Messages sent to terminated actors go to Dead Letters
system.eventStream().subscribe(deadLetterActorRef, DeadLetter.class);

// Listen for dead letters
system.eventStream().subscribe(
    ActorRef.noSender(),
    DeadLetter.class
);
// Log: "Message [type] from [sender] to [recipient] was not delivered"
```

### 2. Actor Logging
```java
public class LoggingActor {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public Behavior<Command> create() {
        return Behaviors.setup(ctx -> {
            ctx.getLog().info("Actor started"); // Akka logging
            return Behaviors.receive(Command.class)
                .onMessage(AnyCommand.class, (msg, c) -> {
                    c.getLog().debug("Processing: {}", msg);
                    return Behaviors.same();
                })
                .build();
        });
    }
}
```

### 3. Supervision Debug
```java
public class DebugSupervisorStrategy extends SupervisorStrategy {

    @Override
    public Directive handle(Throwable cause) {
        log.warn("Actor failed: {}", cause.getMessage());
        Directive directive = super.handle(cause);
        log.info("Supervisor decided: {}", directive);
        return directive;
    }
}
```

### 4. Actor State Inspection
```java
// Use Akka Actor TestKit for state inspection
TestKit<Response> probe = TestKit.create(system);
actor.tell(new GetState(probe.getRef()));
Response state = probe.expectMessageClass(Response.class);
assertThat(state).isNotNull();
```

### 5. Configuration Debug
```yaml
akka:
  loglevel: DEBUG
  actor:
    debug:
      receive: on   # Log all messages
      lifecycle: on # Log actor lifecycle events
      unhandled: on # Log unhandled messages
```
