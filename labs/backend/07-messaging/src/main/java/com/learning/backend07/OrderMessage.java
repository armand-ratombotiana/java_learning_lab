package com.learning.backend07;

import java.io.Serializable;
import java.time.Instant;

/**
 * Message payload sent via the messaging system.
 * Must be Serializable for JMS. For Kafka, a custom serializer is used.
 */
public class OrderMessage implements Serializable {

    private String orderId;
    private String product;
    private int quantity;
    private double totalPrice;
    private Instant timestamp;

    public OrderMessage() {}

    public OrderMessage(String orderId, String product, int quantity, double totalPrice) {
        this.orderId = orderId;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.timestamp = Instant.now();
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "OrderMessage{orderId='" + orderId + "', product='" + product +
               "', quantity=" + quantity + ", totalPrice=" + totalPrice +
               ", timestamp=" + timestamp + "}";
    }
}
