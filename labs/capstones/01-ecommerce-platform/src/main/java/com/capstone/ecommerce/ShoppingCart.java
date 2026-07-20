package com.capstone.ecommerce;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ShoppingCart {
    private final String cartId;
    private final String userId;
    private final Map<String, LineItem> items = new ConcurrentHashMap<>();
    private final AtomicReference<BigDecimal> total = new AtomicReference<>(BigDecimal.ZERO);
    private boolean persistent;

    public record LineItem(String productId, String productName, int quantity, BigDecimal unitPrice) {
        public LineItem {
            if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        }
        public BigDecimal subtotal() { return unitPrice.multiply(BigDecimal.valueOf(quantity)); }
    }

    public ShoppingCart(String cartId, String userId) {
        this(cartId, userId, false);
    }

    public ShoppingCart(String cartId, String userId, boolean persistent) {
        if (cartId == null || cartId.isBlank()) throw new IllegalArgumentException("Cart ID required");
        if (userId == null || userId.isBlank()) throw new IllegalArgumentException("User ID required");
        this.cartId = cartId;
        this.userId = userId;
        this.persistent = persistent;
    }

    public void addItem(String productId, String productName, int quantity, BigDecimal unitPrice) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        items.merge(productId, new LineItem(productId, productName, quantity, unitPrice),
            (old, _new) -> new LineItem(productId, productName, old.quantity() + quantity, unitPrice));
        recalculate();
    }

    public void updateQuantity(String productId, int newQuantity) {
        if (newQuantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        if (newQuantity == 0) {
            items.remove(productId);
        } else {
            items.computeIfPresent(productId, (k, v) -> new LineItem(productId, v.productName(), newQuantity, v.unitPrice()));
        }
        recalculate();
    }

    public void removeItem(String productId) {
        items.remove(productId);
        recalculate();
    }

    public void clear() {
        items.clear();
        total.set(BigDecimal.ZERO);
    }

    public List<LineItem> getItems() { return List.copyOf(items.values()); }

    public BigDecimal getTotal() { return total.get(); }

    public int getItemCount() { return items.values().stream().mapToInt(LineItem::quantity).sum(); }

    public String getCartId() { return cartId; }

    public String getUserId() { return userId; }

    public boolean isEmpty() { return items.isEmpty(); }

    public boolean isPersistent() { return persistent; }

    public void setPersistent(boolean p) { this.persistent = p; }

    private void recalculate() {
        total.set(items.values().stream()
            .map(LineItem::subtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShoppingCart that)) return false;
        return cartId.equals(that.cartId);
    }

    @Override
    public int hashCode() { return cartId.hashCode(); }
}
