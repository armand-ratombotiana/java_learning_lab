# Architectural Patterns with Sealed Classes

## Algebraic Data Type Architecture

Sealed classes enable **Algebraic Data Type (ADT)** architecture, where domain models are built from combinations of sum types (sealed classes) and product types (records):

```java
// ADT architecture for a document processing system
sealed interface Document 
    permits TextDocument, SpreadsheetDocument, PresentationDocument, PDFDocument {}

record TextDocument(String content, String encoding) implements Document {}
record SpreadsheetDocument(Map<String, CellValue> cells, int rows, int cols) implements Document {}
record PresentationDocument(List<Slide> slides, String template) implements Document {}
record PDFDocument(byte[] data, String version, boolean encrypted) implements Document {}
```

ADT architecture provides:
- **Exhaustiveness**: Operations over documents must handle all types
- **Type safety**: Each document type carries precisely typed data
- **Extensibility**: Adding a new document type updates all operations at compile time
- **Documentation**: The sealed interface documents the complete set of document types

## Command Pattern Architecture

```java
// Command pattern with sealed types
sealed interface Command 
    permits CreateUserCommand, UpdateUserCommand, DeleteUserCommand, 
            DeactivateUserCommand, ExportUsersCommand {}

record CreateUserCommand(String name, String email, String role) implements Command {}
record UpdateUserCommand(long userId, Map<String, Object> changes) implements Command {}
record DeleteUserCommand(long userId, String reason) implements Command {}
record DeactivateUserCommand(long userId) implements Command {}
record ExportUsersCommand(String format, String filter) implements Command {}

class CommandHandler {
    public void handle(Command cmd) {
        switch (cmd) {
            case CreateUserCommand(var name, var email, var role) -> userService.create(name, email, role);
            case UpdateUserCommand(var id, var changes) -> userService.update(id, changes);
            case DeleteUserCommand(var id, var reason) -> userService.delete(id, reason);
            case DeactivateUserCommand(var id) -> userService.deactivate(id);
            case ExportUsersCommand(var format, var filter) -> exportService.export(format, filter);
        }
    }
}
```

## Interpreter Architecture

```java
// Expression tree as an ADT
sealed interface Expr 
    permits Literal, Variable, UnaryOp, BinaryOp {}

record Literal(double value) implements Expr {}
record Variable(String name) implements Expr {}
record UnaryOp(Operator op, Expr operand) implements Expr {}
record BinaryOp(Operator op, Expr left, Expr right) implements Expr {}

enum Operator { PLUS, MINUS, MULTIPLY, DIVIDE, NEGATE, POWER }

class Interpreter {
    public double evaluate(Expr expr, Map<String, Double> env) {
        return switch (expr) {
            case Literal(var v) -> v;
            case Variable(var name) -> env.getOrDefault(name, Double.NaN);
            case UnaryOp(var op, var operand) -> switch (op) {
                case NEGATE -> -evaluate(operand, env);
                default -> throw new UnsupportedOperationException();
            };
            case BinaryOp(var op, var left, var right) -> {
                double l = evaluate(left, env);
                double r = evaluate(right, env);
                yield switch (op) {
                    case PLUS -> l + r;
                    case MINUS -> l - r;
                    case MULTIPLY -> l * r;
                    case DIVIDE -> l / r;
                    case POWER -> Math.pow(l, r);
                };
            }
        };
    }
}
```

## State Machine Architecture

```java
sealed interface OrderState 
    permits PendingState, ConfirmedState, ShippedState, DeliveredState, CancelledState {}

record PendingState(LocalDateTime createdAt) implements OrderState {}
record ConfirmedState(LocalDateTime confirmedAt, String confirmedBy) implements OrderState {}
record ShippedState(LocalDateTime shippedAt, String trackingNumber) implements OrderState {}
record DeliveredState(LocalDateTime deliveredAt, String recipient) implements OrderState {}
record CancelledState(LocalDateTime cancelledAt, String reason) implements OrderState {}

class Order {
    private final String id;
    private OrderState state;
    
    public void transition(OrderEvent event) {
        this.state = switch (state) {
            case PendingState ts when event instanceof ConfirmEvent e 
                -> new ConfirmedState(e.timestamp(), e.userId());
            case PendingState ts when event instanceof CancelEvent e 
                -> new CancelledState(e.timestamp(), e.reason());
            case ConfirmedState ts when event instanceof ShipEvent e 
                -> new ShippedState(e.timestamp(), e.trackingNumber());
            case ShippedState ts when event instanceof DeliverEvent e 
                -> new DeliveredState(e.timestamp(), e.recipient());
            default -> throw new IllegalStateException("Cannot transition from " + state);
        };
    }
}
```

## Visitor Pattern Replacement

The classic **Visitor pattern** was designed to simulate exhaustiveness in object-oriented languages. Sealed classes eliminate this need:

```java
// No Visitor pattern needed!
sealed interface JsonValue permits JsonNull, JsonBool, JsonNumber, JsonString, JsonArray, JsonObject {}

class JsonTransformer {
    // Exhaustive transformation without visitor boilerplate
    JsonValue transform(JsonValue value, Function<String, String> mapStrings) {
        return switch (value) {
            case JsonString(var s) -> new JsonString(mapStrings.apply(s));
            case JsonArray(var arr) -> new JsonArray(
                arr.stream().map(v -> transform(v, mapStrings)).toList());
            case JsonObject(var props) -> new JsonObject(
                props.entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> transform(e.getValue(), mapStrings))));
            case JsonNull n -> n;
            case JsonBool b -> b;
            case JsonNumber n -> n;
        };
    }
}
```

## Microservices Contract Architecture

```java
// Shared library between services
sealed interface ServiceEvent 
    permits OrderCreated, PaymentReceived, InventoryUpdated, ShipmentDispatched {}

record OrderCreated(
    String orderId, String customerId, List<OrderLine> items, Money total
) implements ServiceEvent {}

record PaymentReceived(
    String transactionId, String orderId, Money amount, PaymentStatus status
) implements ServiceEvent {}

record InventoryUpdated(
    String productId, int quantityChange, int newStockLevel
) implements ServiceEvent {}

record ShipmentDispatched(
    String shipmentId, String orderId, String carrier, String trackingNumber
) implements ServiceEvent {}
```

The sealed interface defines the complete contract between services. Every service that processes these events must handle all types.
