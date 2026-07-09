package com.oracleebs.scm;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class OrderLifecycleManager {
    public enum OrderStatus { ENTERED, BOOKED, SHIPPED, CLOSED, CANCELLED }
    public enum LineType { STANDARD, DROPSHIP, BACK_TO_BACK, CONFIGURED }

    public static class OrderLine {
        private final int lineNumber;
        private final String itemCode;
        private final int quantity;
        private final LineType lineType;
        private final double unitPrice;
        private OrderStatus status;

        public OrderLine(int lineNum, String item, int qty, LineType type, double price) {
            this.lineNumber = lineNum;
            this.itemCode = item;
            this.quantity = qty;
            this.lineType = type;
            this.unitPrice = price;
            this.status = OrderStatus.ENTERED;
        }

        public int getLineNumber() { return lineNumber; }
        public String getItemCode() { return itemCode; }
        public int getQuantity() { return quantity; }
        public LineType getLineType() { return lineType; }
        public double getUnitPrice() { return unitPrice; }
        public OrderStatus getStatus() { return status; }
        public void setStatus(OrderStatus s) { status = s; }
    }

    public static class Order {
        private final long orderId;
        private final String customerId;
        private final LocalDate orderDate;
        private final String shipToLocation;
        private OrderStatus headerStatus;
        private final List<OrderLine> lines;

        public Order(long id, String customer, LocalDate date, String shipTo) {
            this.orderId = id;
            this.customerId = customer;
            this.orderDate = date;
            this.shipToLocation = shipTo;
            this.headerStatus = OrderStatus.ENTERED;
            this.lines = new ArrayList<>();
        }

        public long getOrderId() { return orderId; }
        public String getCustomerId() { return customerId; }
        public LocalDate getOrderDate() { return orderDate; }
        public String getShipToLocation() { return shipToLocation; }
        public OrderStatus getHeaderStatus() { return headerStatus; }
        public void setHeaderStatus(OrderStatus s) { headerStatus = s; }
        public void addLine(OrderLine line) { lines.add(line); }
        public List<OrderLine> getLines() { return Collections.unmodifiableList(lines); }
    }

    private final Map<Long, Order> orders;
    private final AtomicLong orderIdSeq;

    public OrderLifecycleManager() {
        this.orders = new ConcurrentHashMap<>();
        this.orderIdSeq = new AtomicLong(10000);
    }

    public Order createOrder(String customer, LocalDate date, String shipTo) {
        long id = orderIdSeq.incrementAndGet();
        Order order = new Order(id, customer, date, shipTo);
        orders.put(id, order);
        return order;
    }

    public boolean bookOrder(long orderId) {
        Order order = orders.get(orderId);
        if (order == null) return false;
        if (order.getHeaderStatus() != OrderStatus.ENTERED) return false;
        if (order.getLines().isEmpty()) return false;
        order.setHeaderStatus(OrderStatus.BOOKED);
        order.getLines().forEach(l -> l.setStatus(OrderStatus.BOOKED));
        return true;
    }

    public boolean shipOrder(long orderId) {
        Order order = orders.get(orderId);
        if (order == null || order.getHeaderStatus() != OrderStatus.BOOKED) return false;
        order.setHeaderStatus(OrderStatus.SHIPPED);
        order.getLines().forEach(l -> l.setStatus(OrderStatus.SHIPPED));
        return true;
    }

    public boolean cancelOrder(long orderId) {
        Order order = orders.get(orderId);
        if (order == null) return false;
        if (order.getHeaderStatus() == OrderStatus.SHIPPED) return false;
        order.setHeaderStatus(OrderStatus.CANCELLED);
        order.getLines().forEach(l -> l.setStatus(OrderStatus.CANCELLED));
        return true;
    }

    public Optional<Order> getOrder(long orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orders.values().stream().filter(o -> o.getHeaderStatus() == status).toList();
    }
}
