package com.architecture.deep.lab08;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ServerlessLab {
    public static void main(String[] args) {
        var stateStore = new InMemoryStateStore();
        var pipeline = new ServerlessPipeline(stateStore);

        pipeline.addFunction("ValidateOrder", new ValidateOrderFn());
        pipeline.addFunction("ProcessPayment", new ProcessPaymentFn());
        pipeline.addFunction("SendConfirmation", new SendConfirmationFn());

        var event = new OrderEvent("order-1", "user-42", List.of("item-a", "item-b"), 2999);

        var coldSimulator = new ColdStartSimulator();
        System.out.println("Cold start? " + coldSimulator.isColdStart("ValidateOrder"));

        pipeline.execute(event);

        System.out.println("Final state: " + stateStore.get(event.orderId()));
    }
}

record OrderEvent(String orderId, String userId, List<String> items, long totalCents) {}

record FunctionEvent(String source, String type, Map<String, Object> payload) {}

interface StateStore {
    void put(String key, Object value);
    Object get(String key);
}

class InMemoryStateStore implements StateStore {
    private final Map<String, Object> store = new ConcurrentHashMap<>();
    public void put(String key, Object value) { store.put(key, value); }
    public Object get(String key) { return store.get(key); }
}

interface ServerlessFunction {
    String name();
    FunctionEvent apply(FunctionEvent event, StateStore state);
}

class ValidateOrderFn implements ServerlessFunction {
    public String name() { return "ValidateOrder"; }
    public FunctionEvent apply(FunctionEvent event, StateStore state) {
        var payload = new HashMap<>(event.payload());
        payload.put("validated", true);
        payload.put("validationTime", Instant.now().toString());
        return new FunctionEvent("ValidateOrder", "OrderValidated", payload);
    }
}

class ProcessPaymentFn implements ServerlessFunction {
    public String name() { return "ProcessPayment"; }
    public FunctionEvent apply(FunctionEvent event, StateStore state) {
        var payload = new HashMap<>(event.payload());
        payload.put("paymentStatus", "SUCCESS");
        payload.put("paymentId", UUID.randomUUID().toString().substring(0, 8));
        return new FunctionEvent("ProcessPayment", "PaymentProcessed", payload);
    }
}

class SendConfirmationFn implements ServerlessFunction {
    public String name() { return "SendConfirmation"; }
    public FunctionEvent apply(FunctionEvent event, StateStore state) {
        var payload = new HashMap<>(event.payload());
        payload.put("confirmationSent", true);
        var orderId = payload.getOrDefault("orderId", "unknown");
        state.put(orderId.toString(), "ORDER_CONFIRMED");
        return new FunctionEvent("SendConfirmation", "Confirmed", payload);
    }
}

class ServerlessPipeline {
    private final Map<String, ServerlessFunction> functions = new LinkedHashMap<>();
    private final StateStore stateStore;

    ServerlessPipeline(StateStore stateStore) { this.stateStore = stateStore; }

    void addFunction(String name, ServerlessFunction fn) { functions.put(name, fn); }

    void execute(OrderEvent orderEvent) {
        var payload = new HashMap<String, Object>();
        payload.put("orderId", orderEvent.orderId());
        payload.put("userId", orderEvent.userId());
        payload.put("items", orderEvent.items());
        payload.put("totalCents", orderEvent.totalCents());

        var currentEvent = new FunctionEvent("Pipeline", "OrderPlaced", payload);
        for (var fn : functions.values()) {
            System.out.println("Invoking " + fn.name() + "...");
            currentEvent = fn.apply(currentEvent, stateStore);
        }
        System.out.println("Pipeline complete. Last event: " + currentEvent.source() + "/" + currentEvent.type());
    }
}

class ColdStartSimulator {
    private final Set<String> initialized = ConcurrentHashMap.newKeySet();

    boolean isColdStart(String functionName) {
        return initialized.add(functionName);
    }
}
