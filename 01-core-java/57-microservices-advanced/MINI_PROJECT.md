# Module 57: Advanced Microservices Patterns - Mini Project

**Project Name**: Transactional Outbox Implementation  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Implement the Transactional Outbox Pattern to solve the dual-write problem. You will build a Spring Boot application that safely saves an order to a database and guarantees the emission of an `OrderCreatedEvent` to a simulated message broker, even in the event of application crashes.

## 📝 Requirements

### Core Features

1. **Database Setup**:
   - Configure a Spring Boot app with H2 or PostgreSQL.
   - Create two entities: `Order` (`id`, `item`, `price`) and `OutboxEvent` (`id`, `aggregateId`, `eventType`, `payload`, `processed`).

2. **The Dual-Write Transaction**:
   - Create an `OrderService` with a `@Transactional` method `createOrder(Order order)`.
   - Save the `Order` using `orderRepository.save()`.
   - Instead of publishing directly to a message broker, serialize the `Order` to JSON. Create an `OutboxEvent` representing this action (`eventType = "OrderCreated"`, `processed = false`) and save it using `outboxRepository.save()`.

3. **The Outbox Poller (Relay)**:
   - Create a `@Component` called `OutboxRelay`.
   - Use Spring's `@Scheduled(fixedDelay = 5000)` to poll the database every 5 seconds.
   - Query for all `OutboxEvent`s where `processed == false`.
   - For each event, simulate publishing to a message broker (e.g., `System.out.println("PUBLISHING TO KAFKA: " + event.getPayload())`).
   - If the publish is successful, mark the event as `processed = true` and save it back to the database.

4. **Failure Simulation**:
   - Write a test endpoint that triggers `createOrder()`.
   - Stop the application immediately after the order is saved but *before* the Poller has a chance to run.
   - Restart the application. Observe that the Poller wakes up, finds the unprocessed event in the database, and successfully publishes it, guaranteeing At-Least-Once delivery despite the crash.

---

## 💡 Solution Blueprint

1. **Entities**:
   ```java
   @Entity
   public class OutboxEvent {
       @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
       private String aggregateId;
       private String eventType;
       private String payload;
       private boolean processed;
       // getters & setters
   }
   ```

2. **The Transactional Service**:
   ```java
   @Service
   public class OrderService {
       private final OrderRepository orderRepo;
       private final OutboxEventRepository outboxRepo;
       private final ObjectMapper objectMapper;

       @Transactional
       public void createOrder(Order order) throws Exception {
           Order saved = orderRepo.save(order);
           
           OutboxEvent event = new OutboxEvent();
           event.setAggregateId(saved.getId().toString());
           event.setEventType("OrderCreated");
           event.setPayload(objectMapper.writeValueAsString(saved));
           event.setProcessed(false);
           
           outboxRepo.save(event); // Atomically committed with the order
       }
   }
   ```

3. **The Poller**:
   ```java
   @Component
   public class OutboxRelay {
       private final OutboxEventRepository repo;
       // inject MessageBrokerTemplate

       @Scheduled(fixedDelay = 5000)
       public void pollAndPublish() {
           List<OutboxEvent> pending = repo.findByProcessedFalse();
           for (OutboxEvent event : pending) {
               try {
                   // messageBrokerTemplate.send(event.getEventType(), event.getPayload());
                   System.out.println("Published: " + event.getPayload());
                   event.setProcessed(true);
                   repo.save(event);
               } catch (Exception e) {
                   // Retry on next poll
               }
           }
       }
   }
   ```