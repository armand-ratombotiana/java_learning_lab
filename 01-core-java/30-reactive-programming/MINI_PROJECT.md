# Module 30: Reactive Programming (Project Reactor) - Mini Project

**Project Name**: Reactive Stock Ticker Streaming API  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Build a fully reactive, non-blocking Spring WebFlux application that simulates a real-time stock ticker, utilizing `Mono`, `Flux`, Backpressure concepts, and Server-Sent Events (SSE).

## 📝 Requirements

### Core Features

1. **Domain Model**:
   - Create a record `StockQuote(String symbol, double price, Instant timestamp)`.

2. **Reactive Service Layer**:
   - Create a `@Service` named `StockService`.
   - Implement a method `Flux<StockQuote> streamStockPrices(String symbol)`.
   - Use `Flux.interval(Duration.ofSeconds(1))` to generate an infinite stream of ticks, emitting a new `StockQuote` every second.
   - Use `Math.random()` to generate varying prices.

3. **Reactive Web Controller**:
   - Create a `@RestController` mapped to `/api/stocks`.
   - Implement an endpoint `@GetMapping(value = "/{symbol}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)`.
   - It should call the service layer and return the `Flux<StockQuote>`. Because the media type is `TEXT_EVENT_STREAM_VALUE`, Spring WebFlux will automatically keep the connection open and push events to the client as they arrive (Server-Sent Events).

4. **Handling Blocking Legacy Code**:
   - Introduce a dummy legacy repository `LegacyAuditRepository` with a method `void saveAuditLog(String symbol)` that has a `Thread.sleep(1500)` to simulate a blocking database call.
   - Update your `StockService` stream. Every time a stock quote is generated, it should trigger the `saveAuditLog` method.
   - **Crucial**: You must offload this blocking call using `Schedulers.boundedElastic()` so it does not block the Netty event loop processing the reactive stream.

---

## 💡 Solution Blueprint

1. **The Stream & Blocking Offload**:
   ```java
   @Service
   public class StockService {
       private final LegacyAuditRepository auditRepo;

       public StockService(LegacyAuditRepository auditRepo) {
           this.auditRepo = auditRepo;
       }

       public Flux<StockQuote> streamStockPrices(String symbol) {
           return Flux.interval(Duration.ofSeconds(1))
               .map(tick -> new StockQuote(symbol, 100.0 + (Math.random() * 10), Instant.now()))
               // Execute side-effect
               .flatMap(quote -> 
                   Mono.fromRunnable(() -> auditRepo.saveAuditLog(symbol))
                       .subscribeOn(Schedulers.boundedElastic()) // Offload blocking call!
                       .thenReturn(quote) // Continue the stream with original quote
               );
       }
   }
   ```

2. **The WebFlux Controller**:
   ```java
   @RestController
   @RequestMapping("/api/stocks")
   public class StockController {
       private final StockService stockService;

       public StockController(StockService stockService) { this.stockService = stockService; }

       @GetMapping(value = "/{symbol}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
       public Flux<StockQuote> streamQuotes(@PathVariable String symbol) {
           return stockService.streamStockPrices(symbol);
       }
   }
   ```