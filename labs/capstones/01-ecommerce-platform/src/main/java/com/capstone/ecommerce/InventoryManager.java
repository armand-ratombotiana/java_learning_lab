package com.capstone.ecommerce;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class InventoryManager {
    private final Map<String, StockItem> stock = new ConcurrentHashMap<>();
    private final Map<String, List<Reservation>> reservations = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public record StockItem(String productId, int totalQuantity, int reservedQuantity, int lowThreshold, boolean active) {
        public StockItem {
            if (productId == null || productId.isBlank()) throw new IllegalArgumentException("Product ID required");
            if (totalQuantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
            if (reservedQuantity < 0) throw new IllegalArgumentException("Reserved cannot be negative");
        }
        public int availableQuantity() { return totalQuantity - reservedQuantity; }
        public boolean isLowStock() { return availableQuantity() <= lowThreshold; }
        public boolean isOutOfStock() { return availableQuantity() <= 0; }
    }

    public record Reservation(String reservationId, String productId, int quantity, Instant createdAt, boolean released) {}

    public StockItem addStock(String productId, int quantity, int lowThreshold) {
        StockItem item = new StockItem(productId, quantity, 0, lowThreshold, true);
        stock.put(productId, item);
        return item;
    }

    public StockItem updateStock(String productId, int newTotal) {
        return stock.computeIfPresent(productId, (k, v) ->
            new StockItem(productId, newTotal, v.reservedQuantity(), v.lowThreshold(), v.active()));
    }

    public Reservation reserve(String productId, int quantity) {
        lock.lock();
        try {
            StockItem item = stock.get(productId);
            if (item == null) throw new NoSuchElementException("Product not in inventory: " + productId);
            if (item.availableQuantity() < quantity) {
                throw new IllegalStateException("Insufficient stock. Available: " + item.availableQuantity());
            }
            String resId = "RES-" + UUID.randomUUID().toString().substring(0, 8);
            Reservation res = new Reservation(resId, productId, quantity, Instant.now(), false);
            StockItem updated = new StockItem(productId, item.totalQuantity(),
                item.reservedQuantity() + quantity, item.lowThreshold(), item.active());
            stock.put(productId, updated);
            reservations.computeIfAbsent(productId, k -> new ArrayList<>()).add(res);
            return res;
        } finally {
            lock.unlock();
        }
    }

    public void releaseReservation(String reservationId) {
        lock.lock();
        try {
            for (Map.Entry<String, List<Reservation>> entry : reservations.entrySet()) {
                for (ListIterator<Reservation> it = entry.getValue().listIterator(); it.hasNext(); ) {
                    Reservation r = it.next();
                    if (r.reservationId().equals(reservationId) && !r.released()) {
                        it.set(new Reservation(reservationId, r.productId(), r.quantity(), r.createdAt(), true));
                        StockItem item = stock.get(r.productId());
                        if (item != null) {
                            StockItem updated = new StockItem(r.productId(), item.totalQuantity(),
                                item.reservedQuantity() - r.quantity(), item.lowThreshold(), item.active());
                            stock.put(r.productId(), updated);
                        }
                        return;
                    }
                }
            }
            throw new NoSuchElementException("Reservation not found: " + reservationId);
        } finally {
            lock.unlock();
        }
    }

    public Optional<StockItem> getStock(String productId) {
        return Optional.ofNullable(stock.get(productId));
    }

    public List<StockItem> getLowStockItems() {
        return stock.values().stream().filter(StockItem::isLowStock).toList();
    }

    public List<StockItem> getOutOfStockItems() {
        return stock.values().stream().filter(StockItem::isOutOfStock).toList();
    }

    public List<Reservation> getReservations(String productId) {
        return List.copyOf(reservations.getOrDefault(productId, List.of()));
    }

    public int size() { return stock.size(); }

    public void reset() { stock.clear(); reservations.clear(); }
}
