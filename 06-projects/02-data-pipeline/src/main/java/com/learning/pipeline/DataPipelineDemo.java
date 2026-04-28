package com.learning.pipeline;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * Real-World Data Pipeline Processing System
 * 
 * Use Case:
 * - Process user activity logs from e-commerce platform
 * - Filter, transform, aggregate data
 * - Generate real-time analytics
 * - Identify trends and patterns
 * 
 * Technologies Used:
 * - Module 04: Streams API (map, filter, reduce, collect, groupBy)
 * - Module 03: Collections (List, Map, Set)
 * - Module 05: Concurrency (ConcurrentHashMap for thread-safe aggregation)
 * - Module 02: OOP (interfaces, composition)
 * 
 * Companies Using This Pattern:
 * - Netflix (activity streaming)
 * - Spotify (listening analytics)
 * - Google Analytics (user tracking)
 * - Twitter (tweet analysis)
 */
public class DataPipelineDemo {
    
    /**
     * User activity event
     */
    public static class UserActivity {
        private final String userId;
        private final String action;  // VIEW, ADD_TO_CART, PURCHASE
        private final String productId;
        private final double amount;
        private final LocalDateTime timestamp;
        private final String deviceType;  // MOBILE, DESKTOP, TABLET
        
        public UserActivity(String userId, String action, String productId, 
                          double amount, LocalDateTime timestamp, String deviceType) {
            this.userId = userId;
            this.action = action;
            this.productId = productId;
            this.amount = amount;
            this.timestamp = timestamp;
            this.deviceType = deviceType;
        }
        
        public String getUserId() { return userId; }
        public String getAction() { return action; }
        public String getProductId() { return productId; }
        public double getAmount() { return amount; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getDeviceType() { return deviceType; }
        
        @Override
        public String toString() {
            return String.format("%s - %s: %s (%.2f) [%s]", 
                timestamp, userId, action, amount, deviceType);
        }
    }
    
    /**
     * Analytics metrics
     */
    public static class AnalyticsMetrics {
        public final int totalEvents;
        public final double totalRevenue;
        public final int uniqueUsers;
        public final Map<String, Long> actionCounts;
        public final Map<String, Double> deviceRevenue;
        public final double averageOrderValue;
        
        public AnalyticsMetrics(int totalEvents, double totalRevenue, int uniqueUsers,
                              Map<String, Long> actionCounts, Map<String, Double> deviceRevenue,
                              double averageOrderValue) {
            this.totalEvents = totalEvents;
            this.totalRevenue = totalRevenue;
            this.uniqueUsers = uniqueUsers;
            this.actionCounts = actionCounts;
            this.deviceRevenue = deviceRevenue;
            this.averageOrderValue = averageOrderValue;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("╔════════════════════════════════════════╗\n");
            sb.append("║        ANALYTICS METRICS REPORT       ║\n");
            sb.append("╚════════════════════════════════════════╝\n");
            sb.append(String.format("Total Events: %d\n", totalEvents));
            sb.append(String.format("Total Revenue: $%.2f\n", totalRevenue));
            sb.append(String.format("Unique Users: %d\n", uniqueUsers));
            sb.append(String.format("Average Order Value: $%.2f\n", averageOrderValue));
            sb.append("\nAction Breakdown:\n");
            actionCounts.forEach((action, count) ->
                sb.append(String.format("  %s: %d\n", action, count))
            );
            sb.append("\nRevenue by Device:\n");
            deviceRevenue.forEach((device, revenue) ->
                sb.append(String.format("  %s: $%.2f\n", device, revenue))
            );
            return sb.toString();
        }
    }
    
    /**
     * Pipeline processor with streaming data
     */
    public static class DataPipeline {
        private final List<UserActivity> activities;
        
        public DataPipeline(List<UserActivity> activities) {
            this.activities = activities;
        }
        
        /**
         * Example 1: Filter purchases and calculate total revenue
         * Streams: filter, map, reduce
         */
        public double calculateTotalRevenue() {
            return activities.stream()
                .filter(a -> "PURCHASE".equals(a.getAction()))
                .mapToDouble(UserActivity::getAmount)
                .sum();
        }
        
        /**
         * Example 2: Find top users by spending
         * Streams: filter, collect, groupingBy, sorting
         */
        public List<String> findTopSpenders(int topN) {
            return activities.stream()
                .filter(a -> "PURCHASE".equals(a.getAction()))
                .collect(Collectors.groupingBy(
                    UserActivity::getUserId,
                    Collectors.summingDouble(UserActivity::getAmount)
                ))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        }
        
        /**
         * Example 3: Analyze conversion funnel
         * Streams: groupingBy, mapping, toMap
         */
        public Map<String, Integer> getConversionFunnel() {
            return activities.stream()
                .collect(Collectors.groupingBy(
                    UserActivity::getAction,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        List::size
                    )
                ));
        }
        
        /**
         * Example 4: Revenue by device type
         * Streams: filter, groupingBy, summingDouble
         */
        public Map<String, Double> revenueByDeviceType() {
            return activities.stream()
                .filter(a -> "PURCHASE".equals(a.getAction()))
                .collect(Collectors.groupingBy(
                    UserActivity::getDeviceType,
                    Collectors.summingDouble(UserActivity::getAmount)
                ));
        }
        
        /**
         * Example 5: Identify high-value products
         * Streams: filter, groupingBy, filtering
         */
        public Map<String, Double> getTopProducts(int minPurchases) {
            return activities.stream()
                .filter(a -> "PURCHASE".equals(a.getAction()))
                .collect(Collectors.groupingBy(
                    UserActivity::getProductId,
                    Collectors.summingDouble(UserActivity::getAmount)
                ))
                .entrySet()
                .stream()
                .filter(e -> activities.stream()
                    .filter(a -> a.getProductId().equals(e.getKey()) && 
                               "PURCHASE".equals(a.getAction()))
                    .count() >= minPurchases)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue
                ));
        }
        
        /**
         * Example 6: User engagement stat - views to purchase ratio
         * Streams: groupingBy, custom collectors
         */
        public double getViewToPurchaseRatio() {
            long purchases = activities.stream()
                .filter(a -> "PURCHASE".equals(a.getAction()))
                .count();
            
            long views = activities.stream()
                .filter(a -> "VIEW".equals(a.getAction()))
                .count();
            
            return views == 0 ? 0 : (double) purchases / views;
        }
        
        /**
         * Example 7: Parallel processing for large datasets
         * Streams: parallel, filter, map, collect
         */
        public double calculateLargeDatasetRevenue(int iterations) {
            // Simulate processing large dataset by repeating calculations
            return IntStream.range(0, iterations)
                .parallel()  // Use all available CPU cores
                .mapToDouble(i -> calculateTotalRevenue())
                .sum() / iterations;
        }
        
        /**
         * Example 8: Generate comprehensive analytics report
         * Streams: multiple operations chained together
         */
        public AnalyticsMetrics generateReport() {
            // Count totals
            int totalEvents = (int) activities.stream().count();
            
            // Calculate revenue
            double totalRevenue = calculateTotalRevenue();
            
            // Count unique users
            int uniqueUsers = (int) activities.stream()
                .map(UserActivity::getUserId)
                .distinct()
                .count();
            
            // Action breakdown
            Map<String, Long> actionCounts = activities.stream()
                .collect(Collectors.groupingBy(
                    UserActivity::getAction,
                    Collectors.counting()
                ));
            
            // Revenue by device
            Map<String, Double> deviceRevenue = revenueByDeviceType();
            
            // Average order value
            double averageOrderValue = activities.stream()
                .filter(a -> "PURCHASE".equals(a.getAction()))
                .mapToDouble(UserActivity::getAmount)
                .average()
                .orElse(0.0);
            
            return new AnalyticsMetrics(
                totalEvents,
                totalRevenue,
                uniqueUsers,
                actionCounts,
                deviceRevenue,
                averageOrderValue
            );
        }
    }
    
    // ==================== DEMONSTRATION ====================
    
    public static void main(String[] args) {
        System.out.println("╔" + "═".repeat(68) + "╗");
        System.out.println("║" + " ".repeat(12) + "DATA PIPELINE & STREAMS API PROCESSING DEMONSTRATION" + " ".repeat(5) + "║");
        System.out.println("╚" + "═".repeat(68) + "╝\n");
        
        // Generate sample data
        List<UserActivity> activities = generateSampleData();
        
        System.out.println("Sample Activities:");
        activities.stream().limit(5).forEach(a -> System.out.println("  " + a));
        System.out.println("  ... and " + (activities.size() - 5) + " more\n");
        
        DataPipeline pipeline = new DataPipeline(activities);
        
        // Run analyses
        System.out.println("1. TOTAL REVENUE");
        System.out.println("   Revenue: $" + String.format("%.2f", pipeline.calculateTotalRevenue()));
        System.out.println();
        
        System.out.println("2. TOP SPENDERS");
        pipeline.findTopSpenders(5).forEach(user ->
            System.out.println("   - " + user)
        );
        System.out.println();
        
        System.out.println("3. CONVERSION FUNNEL");
        pipeline.getConversionFunnel().forEach((action, count) ->
            System.out.println("   " + action + ": " + count + " events")
        );
        System.out.println();
        
        System.out.println("4. REVENUE BY DEVICE");
        pipeline.revenueByDeviceType().forEach((device, revenue) ->
            System.out.println("   " + device + ": $" + String.format("%.2f", revenue))
        );
        System.out.println();
        
        System.out.println("5. VIEW TO PURCHASE RATIO");
        System.out.println("   Ratio: " + String.format("%.2f%%", pipeline.getViewToPurchaseRatio() * 100));
        System.out.println();
        
        System.out.println("6. COMPREHENSIVE REPORT");
        System.out.println(pipeline.generateReport());
    }
    
    // ==================== DATA GENERATION ====================
    
    private static List<UserActivity> generateSampleData() {
        List<UserActivity> activities = new ArrayList<>();
        String[] users = {"user1", "user2", "user3", "user4", "user5"};
        String[] actions = {"VIEW", "ADD_TO_CART", "PURCHASE"};
        String[] products = {"laptop", "mouse", "keyboard", "monitor", "headphones"};
        String[] devices = {"MOBILE", "DESKTOP", "TABLET"};
        
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        
        // Generate 100 activities
        for (int i = 0; i < 100; i++) {
            String user = users[random.nextInt(users.length)];
            String action = actions[random.nextInt(actions.length)];
            String product = products[random.nextInt(products.length)];
            double amount = "PURCHASE".equals(action) ? 
                            50 + random.nextDouble() * 500 : 0;
            String device = devices[random.nextInt(devices.length)];
            
            activities.add(new UserActivity(
                user,
                action,
                product,
                amount,
                now.minusMinutes(random.nextInt(1440)),
                device
            ));
        }
        
        return activities;
    }
}
