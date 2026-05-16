# Architecture with Design Patterns

## Pattern-Driven Architecture

### Layered Architecture with Patterns
```
┌────────────────────────────────────────────┐
│              Presentation                  │
│     ┌──────────┐  ┌──────────┐            │
│     │  Facade   │  │  Proxy   │            │
│     └──────────┘  └──────────┘            │
├────────────────────────────────────────────┤
│              Service                       │
│     ┌──────────┐  ┌──────────┐            │
│     │  Factory │  │ Strategy │            │
│     └──────────┘  └──────────┘            │
├────────────────────────────────────────────┤
│              Data/Repository               │
│     ┌──────────┐  ┌──────────┐            │
│     │  Builder │  │ Singleton│            │
│     └──────────┘  └──────────┘            │
└────────────────────────────────────────────┘
```

## Pattern Combinations

### Factory + Strategy
```java
// Factory creates strategies
interface PaymentStrategy { void pay(BigDecimal amount); }

class PaymentFactory {
    public static PaymentStrategy getStrategy(String type) {
        return switch(type) {
            case "credit" -> new CreditCardStrategy();
            case "paypal" -> new PayPalStrategy();
            default -> throw new IllegalArgumentException();
        };
    }
}
```

### Observer + Mediator
```java
// Central mediator coordinates observers
class EventMediator {
    private Map<String, List<Consumer>> handlers = new HashMap<>();
    
    public void subscribe(String event, Consumer handler) {
        handlers.computeIfAbsent(event, k -> new ArrayList<>()).add(handler);
    }
    
    public void publish(String event, Object data) {
        handlers.getOrDefault(event, List.of()).forEach(h -> h.accept(data));
    }
}
```

## Architecture Principles

1. **Pattern Selection**: Choose based on problem, not because it's cool
2. **Combination**: Patterns work together
3. **Simplicity**: Prefer simpler solutions when possible