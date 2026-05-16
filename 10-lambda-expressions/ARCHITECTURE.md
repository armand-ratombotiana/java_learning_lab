# Architecture with Lambda Expressions

## Functional Interface Design

### Creating Custom Functional Interfaces

```java
@FunctionalInterface
public interface Transformer<T, R> {
    R transform(T input);
    
    // Default method for chaining
    default Transformer<T, R> andThen(Transformer<R, R> after) {
        return input -> after.transform(this.transform(input));
    }
}
```

### Strategy Pattern with Lambdas

Replace strategy pattern classes with lambdas:
```java
// Before: Strategy interface + implementations
interface PaymentStrategy { void pay(double amount); }
class CreditCardPayment implements PaymentStrategy { ... }

// After: Use Function/Consumer
Function<Double, PaymentResult> creditCardPayment = ...

// Usage
PaymentService service = new PaymentService();
service.process(amount, paymentMethod -> creditCardPayment.apply(amount));
```

## Pipeline Architecture

Build processing pipelines using functional composition:

```java
public class PipelineBuilder<T> {
    private List<Function<T, T>> steps = new ArrayList<>();
    
    public PipelineBuilder<T> addStep(Function<T, T> step) {
        steps.add(step);
        return this;
    }
    
    public Function<T, T> build() {
        return steps.stream()
            .reduce(Function.identity(), Function::andThen);
    }
}
```

## Method Chaining with Fluent API

```java
public class UserQuery {
    private List<User> users;
    
    public UserQuery filter(Predicate<User> predicate) {
        users = users.stream().filter(predicate).collect(...);
        return this;
    }
    
    public UserQuery sort(Comparator<User> comparator) {
        users = users.stream().sorted(comparator).collect(...);
        return this;
    }
    
    public List<User> execute() {
        return new ArrayList<>(users);
    }
}
```

## Architecture Benefits

- **Declarative**: Specify what, not how
- **Composable**: Build complex operations from simple parts
- **Testable**: Each lambda is a unit that can be tested in isolation
- **Flexible**: Easy to add/modify behavior without changing structure