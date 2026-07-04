# Mental Models for DDD

## The Map of Territories
Each bounded context is a country on a map with its own borders, language, and rules. Context maps show how countries relate (trade agreements = partnerships, customs = anticorruption layer).

## The Chess Board Model
Entities are like chess pieces - they have identity and change state. Value objects are like board positions - they describe where things are but replaceable. The aggregate is the entire game state.

## The Plant Cell Model
Aggregate root is like the cell nucleus - all changes must go through it. Entities are organelles with identity. Value objects are molecules - interchangeable.

## The Language Dictionary
Ubiquitous language is like creating a shared dictionary between two cultures. Every term must be defined precisely and used consistently.

## The Factory Pattern in Real World
Just like a car factory assembles complex products, a domain factory encapsulates the complexity of creating aggregates with all their invariants.

```java
// Domain Factory encapsulates creation complexity
@Component
public class OrderFactory {
    public Order createOrder(Customer customer, Cart cart, ShippingAddress address) {
        // Validate business rules
        // Create value objects
        // Return valid aggregate
        return Order.builder()
            .id(new OrderId(UUID.randomUUID()))
            .customerId(customer.getId())
            .items(cart.toOrderItems())
            .shippingAddress(address)
            .status(OrderStatus.PENDING)
            .build();
    }
}
```
