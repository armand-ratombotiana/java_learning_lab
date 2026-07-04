# Mental Models for Event-Driven Architecture

## The Newspaper Analogy
Events are like newspaper headlines - they report facts about what happened. You don't control who reads the newspaper; subscribers choose what interests them.

## The Chess Game Model
Each move in chess is an event. You can replay the entire game from the sequence of moves (event sourcing). The current board state is derived from all past moves.

## The River Model
Events flow like a river - continuous, unidirectional, and persistent. You can tap into the river at any point (subscribe), and you can go back and examine past water (event replay).

## The Party Invitation
You send invitations (events) to a party. Some people attend, some don't. You don't wait for RSVPs to continue your day. The party happens regardless.

## The Audit Log Mental Model
Every event is like a line in an audit log - timestamped, immutable, and ordered. The current state is just the sum of all past events.

```java
// Events are immutable facts
@Value
public class AccountEvent {
    Instant timestamp;
    String accountId;
    String eventType;  // DEPOSIT, WITHDRAWAL, TRANSFER
    BigDecimal amount;
}
// The balance is derived from replaying all events
```
