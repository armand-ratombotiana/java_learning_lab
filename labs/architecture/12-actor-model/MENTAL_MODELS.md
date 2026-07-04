# Mental Models for Actor Model

## The Office Worker
Each actor is like an office worker with their own desk (state), filing system (behavior), and inbox (mailbox). They process one item at a time from their inbox. They can send memos (messages) to other workers.

## The Post Office
Actors have addresses (ActorRef) just like people have mailing addresses. You send a letter (message) to the address, and it's delivered to their mailbox. They process it when they get to it.

## The Human Body
Cells are like actors - each cell has its own state and behavior. Cells communicate through chemical signals (messages). If a cell fails, other cells (supervisor) can detect and handle the failure.

## The Cell Phone Network
Each phone (actor) processes one call at a time. Messages are like calls and texts. The network routes messages to the correct phone regardless of location (location transparency).

## The Game of Chess Server
Each game of chess is managed by an actor. The actor receives moves (messages), updates the game state, and sends responses. Thousands of games run concurrently without interfering.

```java
// Chess game as an actor
public class ChessGameActor {

    public static Behavior<ChessMessage> create(String gameId) {
        return idleGame(gameId);
    }

    private static Behavior<ChessMessage> idleGame(String gameId) {
        return Behaviors.receive(ChessMessage.class)
            .onMessage(JoinGame.class, msg -> activeGame(gameId, msg.player()))
            .build();
    }

    private static Behavior<ChessMessage> activeGame(String gameId, String player) {
        return Behaviors.receive(ChessMessage.class)
            .onMessage(Move.class, msg -> {
                // Process move
                return activeGame(gameId, player);
            })
            .build();
    }
}
```
