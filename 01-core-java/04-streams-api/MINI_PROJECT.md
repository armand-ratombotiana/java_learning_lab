# Module 04: Streams API - Mini Project

**Project Name**: E-Commerce Data Analyzer  
**Difficulty Level**: Intermediate  
**Estimated Time**: 2-3 hours

---

## 🎯 Objective
Use the Java 8+ Streams API to perform complex data processing, filtering, aggregation, and transformation on a collection of objects in a declarative manner.

## 📝 Requirements

### 1. Data Models
Create the following classes/records:
- `Product(String id, String category, double price, double rating)`
- `Order(String orderId, String customerName, List<Product> products, LocalDate orderDate, String status)` (Status: "DELIVERED", "PENDING", "CANCELLED")

### 2. Stream Operations to Implement
Write a `DataAnalyzer` service class with methods that take a `List<Order>` and return the required data using *only* the Streams API (no standard `for` or `while` loops allowed):

1. **Get Delivered Orders**: Return a list of all orders with the status "DELIVERED".
2. **Calculate Total Revenue**: Return the total sum (double) of all products across all *delivered* orders.
3. **Get Top Rated Products**: Return a list of the 5 products with the highest ratings.
4. **Group Products by Category**: Return a `Map<String, List<Product>>` mapping product categories to the products in those categories.
5. **Get Most Expensive Order**: Return the `Order` with the highest total price using `Optional<Order>`.
6. **Get Unique Customers**: Return a `Set<String>` of unique customer names who have placed at least one order.

---

## 💡 Solution Blueprint

- Use `.stream()` or `.flatMap()` to dive into nested collections (e.g., getting all products from all orders).
- **Total Revenue**: `.filter(status check) .flatMap(get products) .mapToDouble(Product::price) .sum()`.
- **Top Rated**: `.sorted(Comparator.comparingDouble(Product::rating).reversed()) .limit(5)`.
- **Grouping**: Use `.collect(Collectors.groupingBy(Product::category))`.
- **Max Order**: Map each order to its total price inside an `.max(Comparator.comparingDouble(...))` call.