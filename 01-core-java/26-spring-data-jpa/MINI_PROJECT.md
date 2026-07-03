# Module 26: Spring Data JPA - Mini Project

**Project Name**: E-Commerce Data Layer  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Design a robust Data Access Layer using Spring Data JPA, demonstrating entity relationships, derived queries, custom JPQL queries, pagination, and addressing the N+1 problem.

## 📝 Requirements

### Core Features

1. **Entity Modeling & Relationships**:
   - Create a `Customer` entity (`id`, `name`, `email`).
   - Create an `Order` entity (`id`, `orderDate`, `status`).
   - Establish a **Bidirectional One-to-Many** relationship: One `Customer` can have many `Order`s.
   - Set the `fetch` type to `LAZY` for the many-to-one side.
   - Use `CascadeType.ALL` and `orphanRemoval = true` on the `Customer` side.

2. **Basic Repositories & Derived Queries**:
   - Create `CustomerRepository` extending `JpaRepository`.
   - Write a derived query method to find customers by email: `Optional<Customer> findByEmail(String email)`.
   - Write a derived query method to find orders by status: `List<Order> findByStatus(String status)`.

3. **Custom JPQL & Native Queries**:
   - In `OrderRepository`, write a custom JPQL query using `@Query` to find all orders placed after a certain date.
   - Write a native SQL query to find the total count of orders for a specific customer ID.

4. **Solving the N+1 Problem**:
   - In `CustomerRepository`, write a method `List<Customer> findAllWithOrders()`.
   - Use a `JOIN FETCH` JPQL query (or an `@EntityGraph`) to load all Customers and their associated Orders in a single SQL query, preventing the N+1 problem.

5. **Pagination and Sorting**:
   - Add a method in `CustomerRepository` to return a `Page<Customer>` based on a `Pageable` parameter.

6. **Auditing (Bonus)**:
   - Add `@CreatedDate` and `@LastModifiedDate` to the `Order` entity.
   - Enable auditing in a configuration class using `@EnableJpaAuditing`.

---

## 💡 Solution Blueprint

1. **Entities**:
   ```java
   @Entity
   public class Customer {
       @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
       private String email;

       @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
       private List<Order> orders = new ArrayList<>();
       
       // Helper method to sync bidirectional state
       public void addOrder(Order order) {
           orders.add(order);
           order.setCustomer(this);
       }
   }

   @Entity
   @Table(name = "orders") // "Order" is a reserved keyword in some SQL dialects
   public class Order {
       @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;

       @ManyToOne(fetch = FetchType.LAZY)
       @JoinColumn(name = "customer_id")
       private Customer customer;
   }
   ```

2. **Repositories**:
   ```java
   public interface CustomerRepository extends JpaRepository<Customer, Long> {
       Optional<Customer> findByEmail(String email);
       
       // Solves N+1
       @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.orders")
       List<Customer> findAllWithOrders();
   }
   
   public interface OrderRepository extends JpaRepository<Order, Long> {
       // Custom JPQL
       @Query("SELECT o FROM Order o WHERE o.orderDate > :date")
       List<Order> findRecentOrders(@Param("date") LocalDate date);
       
       // Native SQL
       @Query(value = "SELECT count(*) FROM orders WHERE customer_id = ?1", nativeQuery = true)
       long countOrdersForCustomer(Long customerId);
   }
   ```