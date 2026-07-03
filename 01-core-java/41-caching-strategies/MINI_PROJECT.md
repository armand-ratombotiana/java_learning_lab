# Module 41: Caching Strategies - Mini Project

**Project Name**: Resilient Distributed Cache Implementation  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Implement a Spring Boot application utilizing Spring Data Redis to demonstrate various caching strategies (Cache-Aside, Write-Through), eviction policies, and solutions to the Thundering Herd (Cache Stampede) problem.

## 📝 Requirements

### Core Features

1. **Redis Setup**:
   - Ensure you have a running Redis instance (via Docker `docker run -p 6379:6379 -d redis` or Testcontainers in tests).
   - Add `spring-boot-starter-data-redis` and `spring-boot-starter-cache`.
   - Add `@EnableCaching` to your main class.

2. **The Slow Service**:
   - Create a `ProductService` with a method `public Product getProductDetails(Long id)`.
   - Simulate a highly expensive database/network call by adding a `Thread.sleep(3000)`.

3. **Cache-Aside Implementation**:
   - Apply the `@Cacheable(value = "products", key = "#id")` annotation to `getProductDetails`.
   - Create an endpoint `GET /products/{id}`. Test that the first request takes 3 seconds, but subsequent requests return instantly.

4. **Cache Eviction & Updating**:
   - Implement `public Product updateProduct(Product product)`.
   - Use `@CachePut` to update the cache simultaneously (Write-Through behavior), or use `@CacheEvict` to clear the cache so the next read repopulates it.

5. **Solving the Thundering Herd (Sync Attribute)**:
   - Imagine the cache TTL expires, and 500 users request the same product simultaneously. All 500 threads will hit the `Thread.sleep(3000)` database call, crashing the system.
   - To fix this, update the annotation: `@Cacheable(value = "products", key = "#id", sync = true)`.
   - The `sync = true` attribute acts as a local Mutex lock. It ensures that if the cache is empty, only *one* thread is allowed to enter the slow method to fetch the data. The other 499 threads will wait for the first thread to populate the cache, and then they will all read from the newly populated cache.

---

## 💡 Solution Blueprint

1. **Service Configuration**:
   ```java
   @Service
   public class ProductService {

       // sync = true prevents Cache Stampedes by locking the key during calculation
       @Cacheable(value = "products", key = "#id", sync = true)
       public Product getProductDetails(Long id) {
           simulateSlowNetwork();
           return new Product(id, "Premium Laptop", 1200.00);
       }

       // @CachePut updates the cache without removing it
       @CachePut(value = "products", key = "#product.id")
       public Product updateProduct(Product product) {
           // Save to DB...
           return product;
       }
       
       // @CacheEvict clears the cache
       @CacheEvict(value = "products", key = "#id")
       public void deleteProduct(Long id) {
           // Delete from DB...
       }

       private void simulateSlowNetwork() {
           try {
               Thread.sleep(3000);
           } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
           }
       }
   }
   ```