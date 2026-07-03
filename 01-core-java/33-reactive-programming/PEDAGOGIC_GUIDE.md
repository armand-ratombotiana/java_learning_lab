# Pedagogic Guide: Reactive Programming & Flow API

## 1. Module Overview
This module represents a fundamental paradigm shift for Java developers. It moves them from the comfortable, synchronous, imperative world of "wait for the result" to the complex, asynchronous, event-driven world of "react to the result." It is the necessary foundation for learning modern frameworks like Spring WebFlux or Quarkus Mutiny.

## 2. Learning Paths

### Path A: The Spring Boot Developer (Focus: Concepts & Traps)
**Target Audience**: Developers who will soon be moving to Spring WebFlux or Project Reactor.
*   **Focus**: `DEEP_DIVE.md` (Backpressure) and `EDGE_CASES.md` (Blocking the Event Loop).
*   **Key Takeaway**: Understanding that reactive code is *lazy* (nothing happens until you subscribe) and that blocking a reactive thread is a fatal error that will crash the application.

### Path B: The Systems Architect (Focus: Mechanics & Interoperability)
**Target Audience**: Senior developers designing high-throughput data pipelines or integrating disparate asynchronous systems.
*   **Focus**: `MINI_PROJECT.md` (Raw Flow API) and `INTERVIEW_PREP.md`.
*   **Key Takeaway**: Mastering the exact lifecycle of the `Flow` interfaces and understanding how the `Subscription` contract acts as the universal translator between different reactive libraries.

## 3. Teaching Strategies

### The "Water Hose" Metaphor for Backpressure
To explain Backpressure, use a water hose and a bucket.
*   **No Backpressure**: The hose (Publisher) is turned on full blast. You have a small bucket (Subscriber). The bucket fills up instantly, overflows, and floods the room (OutOfMemoryError).
*   **With Backpressure**: The hose has a smart nozzle. You tell the nozzle: "Give me exactly 1 gallon of water." The nozzle dispenses 1 gallon and stops. You process the water (empty the bucket). Then you tell the nozzle: "Give me 1 more gallon." The consumer is in complete control of the flow rate.

### The "Restaurant Waiter" Metaphor for Event Loops
To explain why blocking `onNext()` is fatal:
*   **Imperative (Thread-per-Request)**: Every customer gets their own dedicated waiter. If the kitchen takes 30 minutes to cook the food, the waiter stands at the table doing nothing for 30 minutes. (Inefficient, but doesn't affect other tables).
*   **Reactive (Event Loop)**: There is only ONE waiter for the entire restaurant. The waiter takes an order, hands it to the kitchen, and immediately goes to the next table. When the kitchen pings a bell (Event), the waiter delivers the food. 
*   **The Bug**: If the waiter takes an order and decides to *wait in the kitchen* until the food is done (a blocking call), NO OTHER TABLE IN THE RESTAURANT can place an order or get their food. The entire system freezes.

## 4. Common Mental Blocks & Clarifications

### Block 1: "Why doesn't my reactive code do anything?"
*   **Clarification**: Show the code: `Mono.just("Hello").map(String::toUpperCase);`. Ask what prints. The answer is nothing. Explain that reactive programming builds a *blueprint* or a *pipeline*. It is completely inert. Data only flows when a `Subscriber` attaches to the end of the pipeline and calls `request()`. Emphasize the mantra: "Nothing happens until you subscribe."

### Block 2: "If I can't block, how do I talk to a relational database?"
*   **Clarification**: This is a major architectural hurdle. Explain that standard JDBC drivers are inherently blocking. If you must use JDBC in a reactive app, you must wrap the call in a specific operator (like `subscribeOn(Schedulers.boundedElastic())`) that offloads the blocking work to a separate, dedicated thread pool, keeping the main reactive event loop free. Point them towards R2DBC for true reactive database drivers.

### Block 3: "Why did Java 9 add `Flow` if we already had `Stream`?"
*   **Clarification**: Contrast the two. `java.util.stream` is for *Pull-based* processing of data you already have in memory (like a List). `java.util.concurrent.Flow` is for *Push-based* processing of data that is arriving unpredictably over time (like a WebSocket feed or a message queue). They solve different problems.

## 5. Assessment Strategy
*   **Formative**: Ask the learner to write the exact sequence of method calls that occur when a Subscriber subscribes to a Publisher and requests 2 items. (Answer: `subscribe()` -> `onSubscribe()` -> `request(2)` -> `onNext()` -> `onNext()` -> `onComplete()`).
*   **Summative**: The `MINI_PROJECT.md` requires the learner to implement the raw `Flow` interfaces. By forcing them to manually manage the `AtomicInteger` for the index and handle the `request(n)` logic asynchronously, they prove they understand the complex mechanics of backpressure.