# Module 10: Lambda Expressions - Mini Project

**Project Name**: Functional Event Processing Engine  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Design a functional event processing system that heavily utilizes lambda expressions, custom functional interfaces, method references, and built-in Java functional interfaces (`Predicate`, `Function`, `Consumer`) to filter, transform, and route data streams.

## 📝 Requirements

### Core Features
1. **Event Model**:
   - Create a record `Event(String id, String type, String payload, long timestamp)`.

2. **Custom Functional Interfaces**:
   - Create `@FunctionalInterface EventFilter` with method `boolean allow(Event event)`.
   - Create `@FunctionalInterface EventTransformer` with method `Event transform(Event event)`.
   - Create `@FunctionalInterface EventHandler` with method `void handle(Event event)`.

3. **The Pipeline Engine**:
   - Create an `EventPipeline` class.
   - It should maintain a `List<EventFilter>`, a `List<EventTransformer>`, and a `List<EventHandler>`.
   - Implement methods to register these functions: `addFilter`, `addTransformer`, `addHandler`.
   - Implement a `process(List<Event> events)` method.

4. **Processing Logic**:
   - The `process` method should iterate over the events.
   - For each event, it must pass through ALL registered filters. If any filter returns `false`, the event is dropped. (Hint: Use `Predicate.and()` or a loop).
   - If it passes, it must be transformed sequentially by ALL registered transformers.
   - Finally, the transformed event must be passed to ALL registered handlers.

5. **Implementation via Lambdas**:
   - In your `main` method, instantiate the pipeline.
   - Register filters using lambdas (e.g., filter out events older than a certain timestamp).
   - Register transformers using method references (e.g., a method that encrypts the payload).
   - Register handlers using lambdas (e.g., print to console, write to a mock database).

---

## 💡 Solution Blueprint

1. **Pipeline Class**:
   ```java
   public class EventPipeline {
       private final List<EventFilter> filters = new ArrayList<>();
       private final List<EventTransformer> transformers = new ArrayList<>();
       private final List<EventHandler> handlers = new ArrayList<>();
       
       public EventPipeline addFilter(EventFilter f) { filters.add(f); return this; }
       // ... same for others
       
       public void process(List<Event> events) {
           for (Event event : events) {
               boolean keep = true;
               for (EventFilter filter : filters) {
                   if (!filter.allow(event)) { keep = false; break; }
               }
               if (!keep) continue;
               
               Event current = event;
               for (EventTransformer transformer : transformers) {
                   current = transformer.transform(current);
               }
               
               for (EventHandler handler : handlers) {
                   handler.handle(current);
               }
           }
       }
   }
   ```
2. **Main Method Usage**:
   ```java
   EventPipeline pipeline = new EventPipeline()
       .addFilter(e -> e.type().equals("PAYMENT"))
       .addTransformer(e -> new Event(e.id(), e.type(), e.payload().toUpperCase(), e.timestamp()))
       .addHandler(System.out::println);
   ```