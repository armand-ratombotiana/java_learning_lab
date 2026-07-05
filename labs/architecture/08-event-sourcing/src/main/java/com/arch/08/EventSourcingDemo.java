package com.arch.eventsourcing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventSourcingDemo {
    private static final Map<String, List<Event>> eventStore = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        String orderId = "ORD-001";
        String customerId = "CUST-001";

        eventStore.computeIfAbsent(orderId, k -> new ArrayList<>())
            .add(new Event("OrderCreated", orderId, "{\"customerId\":\"" + customerId + "\"}"));
        eventStore.get(orderId)
            .add(new Event("ItemAdded", orderId, "{\"item\":\"Laptop\",\"qty\":1}"));

        OrderAggregate order = new OrderAggregate();
        EventReplayer replayer = new EventReplayer();
        replayer.replay(order, eventStore.get(orderId));

        System.out.println("Order ID: " + order.getId());
        System.out.println("Customer: " + order.getCustomerId());
        System.out.println("Items: " + order.getItemCount());
    }

    static class OrderAggregate extends EventStoreAggregate {
        private String customerId;
        private int itemCount = 0;

        protected void apply(Event event) {
            switch (event.getType()) {
                case "OrderCreated":
                    this.id = event.getAggregateId();
                    this.customerId = event.getData().replaceAll(".*customerId\":\"([^\"]+).*", "$1");
                    break;
                case "ItemAdded":
                    itemCount++;
                    break;
            }
        }

        public String getCustomerId() { return customerId; }
        public int getItemCount() { return itemCount; }
    }
}
