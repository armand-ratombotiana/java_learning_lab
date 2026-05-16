# Refactoring with Design Patterns

## Common Refactorings

### To Singleton
```java
// Before: Global state scattered
class AppConfig {
    static Map<String, String> settings;
    static { settings = load(); }
}

// After: Controlled singleton
class AppConfig {
    private static volatile AppConfig instance;
    private final Map<String, String> settings;
    
    private AppConfig() { 
        this.settings = load(); 
    }
    
    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized(AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }
}
```

### To Factory
```java
// Before: Object creation scattered
class PaymentService {
    public void process(String type) {
        if ("credit".equals(type)) {
            CreditProcessor p = new CreditProcessor();
            p.process();
        } else if ("debit".equals(type)) {
            DebitProcessor p = new DebitProcessor();
            p.process();
        }
    }
}

// After: Factory pattern
interface PaymentProcessor {
    void process();
}

class PaymentFactory {
    public static PaymentProcessor create(String type) {
        return switch(type) {
            case "credit" -> new CreditProcessor();
            case "debit" -> new DebitProcessor();
            default -> throw new IllegalArgumentException();
        };
    }
}
```

### To Strategy
```java
// Before: Conditional logic
class Sorter {
    public void sort(List<String> data, String algorithm) {
        if ("quick".equals(algorithm)) {
            // quick sort implementation
        } else if ("merge".equals(algorithm)) {
            // merge sort implementation
        }
    }
}

// After: Strategy pattern
interface SortStrategy {
    void sort(List<String> data);
}

class Sorter {
    private SortStrategy strategy;
    public void setStrategy(SortStrategy s) { this.strategy = s; }
    public void sort(List<String> data) { strategy.sort(data); }
}
```

## When to Refactor

- Code has scattered creation logic → Factory
- Complex object construction → Builder
- Need single access point → Singleton
- Multiple algorithms that swap → Strategy