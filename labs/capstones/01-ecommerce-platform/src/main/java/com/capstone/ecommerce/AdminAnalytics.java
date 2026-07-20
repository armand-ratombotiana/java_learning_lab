package com.capstone.ecommerce;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class AdminAnalytics {
    private final AtomicLong totalOrders = new AtomicLong(0);
    private final AtomicLong totalRevenue = new AtomicLong(0);
    private final AtomicLong totalUsers = new AtomicLong(0);
    private final Map<String, Long> salesByCategory = new ConcurrentHashMap<>();
    private final Map<String, Long> salesByDay = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> productSales = new ConcurrentHashMap<>();
    private final List<Double> revenueHistory = new ArrayList<>();

    public record SalesSummary(long totalOrders, BigDecimal totalRevenue, long totalUsers,
                                Map<String, Long> salesByCategory, Map<String, Long> salesByDay,
                                List<String> topProducts, double averageOrderValue) {}

    public void recordOrder(String orderId, String userId, BigDecimal amount, String category, Instant timestamp) {
        totalOrders.incrementAndGet();
        totalRevenue.addAndGet(amount.longValue());
        salesByCategory.merge(category, 1L, Long::sum);
        String dayKey = timestamp.toString().substring(0, 10);
        salesByDay.merge(dayKey, 1L, Long::sum);
        revenueHistory.add(amount.doubleValue());
    }

    public void recordUserRegistration() {
        totalUsers.incrementAndGet();
    }

    public void recordProductSale(String productId) {
        productSales.computeIfAbsent(productId, k -> new AtomicLong(0)).incrementAndGet();
    }

    public SalesSummary getSummary() {
        long orders = totalOrders.get();
        BigDecimal revenue = BigDecimal.valueOf(totalRevenue.get());
        long users = totalUsers.get();
        double avgOrderValue = orders > 0 ? revenue.doubleValue() / orders : 0.0;

        List<String> topProducts = productSales.entrySet().stream()
            .sorted((a, b) -> Long.compare(b.getValue().get(), a.getValue().get()))
            .limit(10)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        return new SalesSummary(orders, revenue, users,
            Map.copyOf(salesByCategory), Map.copyOf(salesByDay), topProducts, avgOrderValue);
    }

    public long getOrderCount() { return totalOrders.get(); }

    public long getRevenue() { return totalRevenue.get(); }

    public long getUserCount() { return totalUsers.get(); }

    public Map<String, Long> getSalesByCategory() { return Map.copyOf(salesByCategory); }

    public Map<String, Long> getSalesByDay() { return Map.copyOf(salesByDay); }

    public List<Double> getRevenueHistory() { return List.copyOf(revenueHistory); }

    public double getConversionRate(long visits) {
        return visits > 0 ? (double) totalOrders.get() / visits * 100.0 : 0.0;
    }

    public double getAverageOrderValue() {
        long orders = totalOrders.get();
        return orders > 0 ? (double) totalRevenue.get() / orders : 0.0;
    }

    public void reset() {
        totalOrders.set(0);
        totalRevenue.set(0);
        totalUsers.set(0);
        salesByCategory.clear();
        salesByDay.clear();
        productSales.clear();
        revenueHistory.clear();
    }
}
