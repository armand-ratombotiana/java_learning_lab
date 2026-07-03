# Code Deep Dive: Sealed Classes

## JSON Parser with Sealed Types

```java
import java.util.*;
import java.util.stream.*;

// JSON value as an algebraic data type
sealed interface Json 
    permits JsonNull, JsonBool, JsonNumber, JsonString, JsonArray, JsonObject {}

record JsonNull() implements Json {}
record JsonBool(boolean value) implements Json {}
record JsonNumber(double value) implements Json {}
record JsonString(String value) implements Json {}
record JsonArray(List<Json> elements) implements Json {
    public JsonArray {
        elements = List.copyOf(elements);  // Defensive copy
    }
}
record JsonObject(Map<String, Json> properties) implements Json {
    public JsonObject {
        properties = Map.copyOf(properties);
    }
}

// Complete JSON serializer with exhaustive pattern matching
class JsonSerializer {
    private static final String INDENT = "  ";
    
    public String serialize(Json json) {
        return serialize(json, 0);
    }
    
    private String serialize(Json json, int depth) {
        String indent = INDENT.repeat(depth);
        String childIndent = INDENT.repeat(depth + 1);
        
        return switch (json) {
            case JsonNull __ -> "null";
            case JsonBool(var b) -> String.valueOf(b);
            case JsonNumber(var n) -> {
                if (n == Math.floor(n) && !Double.isInfinite(n)) {
                    yield String.valueOf((long) n);
                }
                yield String.valueOf(n);
            }
            case JsonString(var s) -> "\"" + escapeString(s) + "\"";
            case JsonArray(var arr) -> {
                if (arr.isEmpty()) yield "[]";
                yield "[\n" + arr.stream()
                    .map(e -> childIndent + serialize(e, depth + 1))
                    .collect(Collectors.joining(",\n"))
                    + "\n" + indent + "]";
            }
            case JsonObject(var props) -> {
                if (props.isEmpty()) yield "{}";
                yield "{\n" + props.entrySet().stream()
                    .map(e -> childIndent + "\"" + escapeString(e.getKey()) + "\": " 
                             + serialize(e.getValue(), depth + 1))
                    .collect(Collectors.joining(",\n"))
                    + "\n" + indent + "}";
            }
        };
    }
    
    private String escapeString(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
```

## Expression Evaluator

```java
sealed interface Expr permits Constant, Var, Add, Multiply, Negate {}

record Constant(double value) implements Expr {}
record Var(String name) implements Expr {}
record Add(Expr left, Expr right) implements Expr {}
record Multiply(Expr left, Expr right) implements Expr {}
record Negate(Expr expr) implements Expr {}

class ExpressionEvaluator {
    
    public double evaluate(Expr expr, Map<String, Double> vars) {
        return switch (expr) {
            case Constant(var v) -> v;
            case Var(var name) -> {
                Double val = vars.get(name);
                if (val == null) throw new UndefinedVariableException(name);
                yield val;
            }
            case Add(var l, var r) -> evaluate(l, vars) + evaluate(r, vars);
            case Multiply(var l, var r) -> evaluate(l, vars) * evaluate(r, vars);
            case Negate(var e) -> -evaluate(e, vars);
        };
    }
    
    // Simplify expressions using algebraic rules
    public Expr simplify(Expr expr) {
        return switch (expr) {
            case Add(Expr l, Constant(double v)) when v == 0 -> simplify(l);
            case Add(Constant(double v), Expr r) when v == 0 -> simplify(r);
            case Multiply(Expr l, Constant(double v)) when v == 0 -> new Constant(0);
            case Multiply(Expr l, Constant(double v)) when v == 1 -> simplify(l);
            case Multiply(Constant(double v), Expr r) when v == 0 -> new Constant(0);
            case Multiply(Constant(double v), Expr r) when v == 1 -> simplify(r);
            case Negate(Negate(Expr e)) -> simplify(e);
            default -> expr;
        };
    }
    
    public String toPrettyString(Expr expr) {
        return switch (expr) {
            case Constant(var v) -> String.valueOf(v);
            case Var(var n) -> n;
            case Add(var l, var r) -> toPrettyString(l) + " + " + toPrettyString(r);
            case Multiply(var l, var r) -> "(" + toPrettyString(l) + ") * (" + toPrettyString(r) + ")";
            case Negate(var e) -> "-(" + toPrettyString(e) + ")";
        };
    }
}
```

## Event System with Sealed Types

```java
import java.time.Instant;

// Domain events for an order system
sealed interface OrderEvent 
    permits OrderCreated, OrderPaid, OrderShipped, OrderDelivered, OrderCancelled {}

record OrderCreated(
    String orderId, 
    String customerId, 
    List<OrderItem> items, 
    Instant timestamp
) implements OrderEvent {}

record OrderPaid(
    String orderId, 
    double amount, 
    String paymentMethod,
    Instant timestamp
) implements OrderEvent {}

record OrderShipped(
    String orderId, 
    String trackingNumber,
    Instant timestamp
) implements OrderEvent {}

record OrderDelivered(
    String orderId,
    String recipientName,
    Instant timestamp
) implements OrderEvent {}

record OrderCancelled(
    String orderId,
    String reason,
    Instant timestamp
) implements OrderEvent {}

// Event processor with exhaustive handling
class OrderEventProcessor {
    
    public void process(OrderEvent event) {
        switch (event) {
            case OrderCreated(var id, var cId, var items, var ts) -> {
                System.out.println("Order " + id + " created for customer " + cId);
                sendNotification(cId, "Order " + id + " created with " + items.size() + " items");
            }
            case OrderPaid(var id, var amt, var method, var ts) -> {
                System.out.println("Payment of $" + amt + " received for order " + id);
                updateInventory(id);  // Reserve items
            }
            case OrderShipped(var id, var track, var ts) -> {
                System.out.println("Order " + id + " shipped: " + track);
                sendTrackingEmail(id, track);
            }
            case OrderDelivered(var id, var name, var ts) -> {
                System.out.println("Order " + id + " delivered to " + name);
                requestReview(id);
            }
            case OrderCancelled(var id, var reason, var ts) -> {
                System.out.println("Order " + id + " cancelled: " + reason);
                processRefund(id);
                releaseInventory(id);
            }
        }
    }
    
    // Stub methods
    private void sendNotification(String customerId, String message) {}
    private void updateInventory(String orderId) {}
    private void sendTrackingEmail(String orderId, String tracking) {}
    private void requestReview(String orderId) {}
    private void processRefund(String orderId) {}
    private void releaseInventory(String orderId) {}
}
```

## Configuration System

```java
// Configuration values that can be different types
sealed interface ConfigValue 
    permits ConfigString, ConfigNumber, ConfigBoolean, ConfigList, ConfigObject {}

record ConfigString(String value) implements ConfigValue {}
record ConfigNumber(double value) implements ConfigValue {}
record ConfigBoolean(boolean value) implements ConfigValue {}
record ConfigList(List<ConfigValue> values) implements ConfigValue {
    public ConfigList { values = List.copyOf(values); }
}
record ConfigObject(Map<String, ConfigValue> entries) implements ConfigValue {
    public ConfigObject { entries = Map.copyOf(entries); }
}

class ConfigParser {
    
    public String getString(ConfigValue val, String defaultValue) {
        return switch (val) {
            case ConfigString(var s) -> s;
            case ConfigNumber(var n) -> String.valueOf(n);
            case ConfigBoolean(var b) -> String.valueOf(b);
            default -> defaultValue;
        };
    }
    
    public List<String> getStringList(ConfigValue val) {
        return switch (val) {
            case ConfigString(var s) -> List.of(s);
            case ConfigList(var list) -> list.stream()
                .map(v -> switch (v) {
                    case ConfigString(var s) -> s;
                    default -> v.toString();
                })
                .toList();
            default -> List.of();
        };
    }
}
```
