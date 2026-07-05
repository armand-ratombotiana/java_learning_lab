package com.arch.cqrs;

public class CqrsDemo {
    public static void main(String[] args) throws Exception {
        EventStore eventStore = new EventStore();

        CommandHandler<CreateOrderCommand, String> createOrderHandler = cmd -> {
            String orderId = "ORD-" + System.currentTimeMillis();
            eventStore.append(orderId, new EventStore.Event("OrderCreated",
                "{\"customerId\":\"" + cmd.getCustomerId() + "\"}"));
            return orderId;
        };

        QueryHandler<GetOrderQuery, String> getOrderHandler = query -> {
            var events = eventStore.getEvents(query.getOrderId());
            StringBuilder sb = new StringBuilder("Order " + query.getOrderId() + " events:\n");
            for (EventStore.Event e : events) {
                sb.append("  - ").append(e.getType()).append(": ").append(e.getData()).append("\n");
            }
            return sb.toString();
        };

        String orderId = createOrderHandler.handle(new CreateOrderCommand("CUST-001"));
        System.out.println("Created: " + orderId);

        eventStore.append(orderId, new EventStore.Event("OrderShipped",
            "{\"trackingId\":\"TRK-123\"}"));

        String result = getOrderHandler.handle(new GetOrderQuery(orderId));
        System.out.println(result);
    }
}
