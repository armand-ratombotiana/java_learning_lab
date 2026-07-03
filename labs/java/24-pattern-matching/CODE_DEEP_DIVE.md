# Code Deep Dive: Pattern Matching

## Complete Expression Evaluator with Pattern Matching

```java
import java.util.*;

sealed interface Expr permits Constant, Variable, Negate, Add, Subtract, Multiply, Divide, Power {}

record Constant(double value) implements Expr {}
record Variable(String name) implements Expr {}
record Negate(Expr expr) implements Expr {}
record Add(Expr left, Expr right) implements Expr {}
record Subtract(Expr left, Expr right) implements Expr {}
record Multiply(Expr left, Expr right) implements Expr {}
record Divide(Expr left, Expr right) implements Expr {}
record Power(Expr base, Expr exponent) implements Expr {}

class ExpressionProcessor {
    
    public double evaluate(Expr expr, Map<String, Double> vars) {
        return switch (expr) {
            case Constant(var v) -> v;
            case Variable(var name) -> {
                Double val = vars.get(name);
                if (val == null) throw new IllegalArgumentException("Undefined variable: " + name);
                yield val;
            }
            case Negate(var e) -> -evaluate(e, vars);
            case Add(var l, var r) -> evaluate(l, vars) + evaluate(r, vars);
            case Subtract(var l, var r) -> evaluate(l, vars) - evaluate(r, vars);
            case Multiply(var l, var r) -> evaluate(l, vars) * evaluate(r, vars);
            case Divide(var l, var r) -> {
                double right = evaluate(r, vars);
                if (right == 0) throw new ArithmeticException("Division by zero");
                yield evaluate(l, vars) / right;
            }
            case Power(var b, var e) -> Math.pow(evaluate(b, vars), evaluate(e, vars));
        };
    }
    
    public Expr simplify(Expr expr) {
        return switch (expr) {
            // Identity: x + 0 = x, 0 + x = x
            case Add(Expr l, Constant(double v)) when v == 0 -> simplify(l);
            case Add(Constant(double v), Expr r) when v == 0 -> simplify(r);
            
            // Identity: x - 0 = x
            case Subtract(Expr l, Constant(double v)) when v == 0 -> simplify(l);
            
            // Zero: x * 0 = 0, 0 * x = 0
            case Multiply(Expr l, Constant(double v)) when v == 0 -> new Constant(0);
            case Multiply(Constant(double v), Expr r) when v == 0 -> new Constant(0);
            
            // Identity: x * 1 = x, 1 * x = x
            case Multiply(Expr l, Constant(double v)) when v == 1 -> simplify(l);
            case Multiply(Constant(double v), Expr r) when v == 1 -> simplify(r);
            
            // Zero exponent
            case Power(Expr b, Constant(double v)) when v == 0 -> new Constant(1);
            
            // Identity: x^1 = x
            case Power(Expr b, Constant(double v)) when v == 1 -> simplify(b);
            
            // Double negation: -(-x) = x
            case Negate(Negate(Expr e)) -> simplify(e);
            
            // Constant folding
            case Add(Constant(double a), Constant(double b)) -> new Constant(a + b);
            case Multiply(Constant(double a), Constant(double b)) -> new Constant(a * b);
            case Subtract(Constant(double a), Constant(double b)) -> new Constant(a - b);
            case Divide(Constant(double a), Constant(double b)) when b != 0 -> new Constant(a / b);
            case Negate(Constant(double v)) -> new Constant(-v);
            
            // No simplification rule applies
            default -> expr;
        };
    }
    
    public String toPrettyString(Expr expr) {
        return switch (expr) {
            case Constant(var v) -> {
                if (v == Math.floor(v)) yield String.valueOf((long) v);
                yield String.valueOf(v);
            }
            case Variable(var n) -> n;
            case Negate(var e) -> "(-" + toPrettyString(e) + ")";
            case Add(var l, var r) -> toPrettyString(l) + " + " + toPrettyString(r);
            case Subtract(var l, var r) -> toPrettyString(l) + " - " + toPrettyString(r);
            case Multiply(var l, var r) -> "(" + toPrettyString(l) + ") * (" + toPrettyString(r) + ")";
            case Divide(var l, var r) -> "(" + toPrettyString(l) + ") / (" + toPrettyString(r) + ")";
            case Power(var b, var e) -> toPrettyString(b) + "^" + toPrettyString(e);
        };
    }
}
```

## JSON Processing with Pattern Matching

```java
sealed interface Json permits JsonNull, JsonBool, JsonNumber, JsonString, JsonArray, JsonObject {}

record JsonNull() implements Json {}
record JsonBool(boolean value) implements Json {}
record JsonNumber(double value) implements Json {}
record JsonString(String value) implements Json {}
record JsonArray(List<Json> elements) implements Json {
    public JsonArray { elements = List.copyOf(elements); }
}
record JsonObject(Map<String, Json> properties) implements Json {
    public JsonObject { properties = Map.copyOf(properties); }
}

class JsonQuery {
    
    // Deep search for values matching a predicate
    public List<Json> find(Json json, java.util.function.Predicate<Json> pred) {
        return switch (json) {
            case JsonArray(var elements) -> {
                var results = new ArrayList<Json>();
                if (pred.test(json)) results.add(json);
                elements.forEach(e -> results.addAll(find(e, pred)));
                yield results;
            }
            case JsonObject(var props) -> {
                var results = new ArrayList<Json>();
                if (pred.test(json)) results.add(json);
                props.values().forEach(v -> results.addAll(find(v, pred)));
                yield results;
            }
            case JsonString(var s) when s.equals("secret") -> List.of();
            case Json __ when pred.test(json) -> List.of(json);
            default -> List.of();
        };
    }
    
    // Merge two JSON objects
    public Json merge(Json a, Json b) {
        return switch (a) {
            case JsonObject(var propsA) -> switch (b) {
                case JsonObject(var propsB) -> {
                    var merged = new LinkedHashMap<>(propsA);
                    propsB.forEach((key, val) -> {
                        if (merged.containsKey(key)) {
                            merged.put(key, merge(merged.get(key), val));
                        } else {
                            merged.put(key, val);
                        }
                    });
                    yield new JsonObject(Map.copyOf(merged));
                }
                default -> b;
            };
            case JsonNull __ when b instanceof JsonNull -> a;
            case JsonNull __ -> b;
            default -> b;
        };
    }
}
```

## HTTP Request Router with Pattern Matching

```java
sealed interface HttpRequest permits GetRequest, PostRequest, PutRequest, DeleteRequest {}

record GetRequest(String path, Map<String, String> params) implements HttpRequest {}
record PostRequest(String path, String body, String contentType) implements HttpRequest {}
record PutRequest(String path, String body, String contentType) implements HttpRequest {}
record DeleteRequest(String path) implements HttpRequest {}

class Router {
    
    public Response handle(HttpRequest request) {
        return switch (request) {
            case GetRequest(var path, var params) when path.equals("/api/users") 
                -> handleListUsers(params);
            case GetRequest(var path, var params) when path.matches("/api/users/\\d+")
                -> handleGetUser(extractId(path));
            case PostRequest(var path, var body, var ct) when path.equals("/api/users")
                -> handleCreateUser(body, ct);
            case PutRequest(var path, var body, var ct) when path.matches("/api/users/\\d+")
                -> handleUpdateUser(extractId(path), body, ct);
            case DeleteRequest(var path) when path.matches("/api/users/\\d+")
                -> handleDeleteUser(extractId(path));
            default -> new Response(404, "Not Found");
        };
    }
    
    private int extractId(String path) {
        var parts = path.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }
    
    private Response handleListUsers(Map<String, String> params) { return new Response(200, "[]"); }
    private Response handleGetUser(int id) { return new Response(200, "{id:" + id + "}"); }
    private Response handleCreateUser(String body, String ct) { return new Response(201, "Created"); }
    private Response handleUpdateUser(int id, String body, String ct) { return new Response(200, "Updated"); }
    private Response handleDeleteUser(int id) { return new Response(200, "Deleted"); }
}

record Response(int statusCode, String body) {}
```
