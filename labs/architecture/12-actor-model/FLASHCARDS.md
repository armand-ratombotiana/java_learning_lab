# Actor Model Flashcards

## Q: What is an actor?
**A:** A computational entity that encapsulates state and behavior, communicates via messages, and processes messages asynchronously.

## Q: What is a mailbox?
**A:** A queue that stores incoming messages for an actor, processed sequentially.

## Q: What is supervision?
**A:** A hierarchy where parent actors monitor and handle failures of child actors.

## Q: What is location transparency?
**A:** The ability to interact with actors using the same API regardless of whether they are local or remote.

## Q: What is an ActorRef?
**A:** A reference to an actor that encapsulates its address, used to send messages.

## Q: What is a dispatcher?
**A:** The component that manages how actors are executed (thread allocation, throughput).

## Q: What is the ask pattern?
**A:** A request-response interaction where a message expects a reply.

## Q: What is a dead letter?
**A:** A message sent to an actor that no longer exists or cannot be delivered.

## Q: What is Akka?
**A:** A Java/Scala toolkit for building actor-based concurrent and distributed systems.

## Q: What is the difference between actors and threads?
**A:** Actors are lightweight, user-level entities with no shared state; threads are OS-level with shared memory and locking.
