# Module 38: Event Sourcing & CQRS - Mini Project

**Project Name**: Shopping Cart Event Sourcing Simulator  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Implement a basic Event Sourcing system from scratch to manage a Shopping Cart. Demonstrate how current state is derived by replaying an append-only log of events, and implement a rudimentary CQRS projection.

## 📝 Requirements

### Core Features

1. **The Events**:
   - Create a base interface `DomainEvent`.
   - Create specific event records:
     - `CartCreatedEvent(String cartId)`
     - `ItemAddedEvent(String cartId, String productId, int quantity)`
     - `ItemRemovedEvent(String cartId, String productId, int quantity)`
     - `CartCheckedOutEvent(String cartId)`

2. **The Event Store**:
   - Create a class `EventStore`.
   - Internally use a `List<DomainEvent>` to represent the append-only database.
   - Implement `void saveEvent(DomainEvent event)` and `List<DomainEvent> getEventsForCart(String cartId)`.

3. **The Aggregate (Command Side)**:
   - Create a `ShoppingCartAggregate` class.
   - It should have state: `String id`, `Map<String, Integer> items`, `boolean checkedOut`.
   - Implement a method `void apply(DomainEvent event)`. This method modifies the aggregate's internal state based on the event type (e.g., if `ItemAddedEvent`, increment the product quantity in the map).
   - Implement a static method `ShoppingCartAggregate loadFromHistory(List<DomainEvent> history)`. It instantiates a new empty cart and loops through the history, calling `apply()` for each event to rebuild the current state.

4. **The Projection (Query Side / CQRS)**:
   - Create a `CartSummaryProjection` class. It acts as the read model.
   - It listens to the `EventStore` (you can simulate this by having the EventStore call `projection.handle(event)` whenever a new event is saved).
   - The projection should simply maintain a counter: `int totalCheckouts`. Every time a `CartCheckedOutEvent` is emitted, increment the counter. This avoids having to query the entire Event Store to calculate metrics.

---

## 💡 Solution Blueprint

1. **Events & Store**:
   ```java
   public record ItemAddedEvent(String cartId, String product, int qty) implements DomainEvent {}

   public class EventStore {
       private final List<DomainEvent> log = new ArrayList<>();
       public void save(DomainEvent e) { log.add(e); }
       public List<DomainEvent> getForCart(String id) {
           return log.stream().filter(e -> e.cartId().equals(id)).toList();
       }
   }
   ```

2. **The Aggregate Replay**:
   ```java
   public class ShoppingCartAggregate {
       private Map<String, Integer> items = new HashMap<>();
       
       public void apply(DomainEvent event) {
           if (event instanceof ItemAddedEvent e) {
               items.put(e.product(), items.getOrDefault(e.product(), 0) + e.qty());
           } else if (event instanceof ItemRemovedEvent e) {
               int newQty = items.getOrDefault(e.product(), 0) - e.qty();
               if (newQty <= 0) items.remove(e.product());
               else items.put(e.product(), newQty);
           }
           // handle other events
       }

       public static ShoppingCartAggregate replay(List<DomainEvent> history) {
           ShoppingCartAggregate cart = new ShoppingCartAggregate();
           for (DomainEvent e : history) cart.apply(e);
           return cart;
       }
   }
   ```