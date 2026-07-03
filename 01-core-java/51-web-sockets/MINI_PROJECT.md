# Module 51: WebSockets & Real-Time Communication - Mini Project

**Project Name**: Real-Time Live Bidding Server  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Build a scalable, real-time bidding server using Spring Boot, WebSockets, and STOMP. Clients should be able to connect, subscribe to an item's bid stream, and submit new bids that are instantly broadcast to all other connected clients.

## 📝 Requirements

### Core Features

1. **WebSocket Configuration**:
   - Create a `WebSocketConfig` class implementing `WebSocketMessageBrokerConfigurer`.
   - Register a STOMP endpoint `/ws-auction` and enable SockJS fallback (e.g., `.withSockJS()`).
   - Configure a simple in-memory message broker. Set the application destination prefix to `/app` and enable a broker for the `/topic` prefix.

2. **The Domain Model**:
   - Create a `BidMessage` class containing `itemId` (String), `bidderName` (String), and `amount` (double).
   - Create a `BidUpdate` class to broadcast back to clients containing the new highest bid and the winning bidder's name.

3. **State Management**:
   - Create a `BiddingService` that holds a thread-safe map: `ConcurrentHashMap<String, Double> currentHighestBids`.
   - When a new bid arrives, atomically check if the new `amount` is higher than the current highest bid for that `itemId`. If so, update it. If not, reject the bid (throw an exception or return an error).

4. **The WebSocket Controller**:
   - Create an `AuctionController`.
   - Implement `@MessageMapping("/bid")`. When a client sends a `BidMessage` here, call the `BiddingService`.
   - If the bid is accepted, use `SimpMessagingTemplate` to broadcast the new `BidUpdate` to `/topic/item/{itemId}` so all users watching that specific item see the price jump instantly.

---

## 💡 Solution Blueprint

1. **Configuration**:
   ```java
   @Configuration
   @EnableWebSocketMessageBroker
   public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
       @Override
       public void registerStompEndpoints(StompEndpointRegistry registry) {
           // The URL clients will connect to
           registry.addEndpoint("/ws-auction").setAllowedOriginPatterns("*").withSockJS();
       }

       @Override
       public void configureMessageBroker(MessageBrokerRegistry config) {
           // Outbound prefix
           config.enableSimpleBroker("/topic");
           // Inbound prefix for @MessageMapping
           config.setApplicationDestinationPrefixes("/app");
       }
   }
   ```

2. **Controller Logic**:
   ```java
   @Controller
   public class AuctionController {

       private final SimpMessagingTemplate messagingTemplate;
       private final BiddingService biddingService;

       public AuctionController(SimpMessagingTemplate template, BiddingService service) {
           this.messagingTemplate = template;
           this.biddingService = service;
       }

       @MessageMapping("/bid") // Client sends to /app/bid
       public void placeBid(BidMessage bid) {
           boolean success = biddingService.processBid(bid);
           
           if (success) {
               BidUpdate update = new BidUpdate(bid.getBidderName(), bid.getAmount());
               // Dynamically broadcast to the specific item's topic
               messagingTemplate.convertAndSend("/topic/item/" + bid.getItemId(), update);
           }
       }
   }
   ```